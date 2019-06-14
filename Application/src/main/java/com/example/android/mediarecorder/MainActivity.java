/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.mediarecorder;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.common.media.CameraHelper;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *  This activity uses the camera/camcorder as the A/V source for the {@link android.media.MediaRecorder} API.
 *  A {@link android.view.TextureView} is used as the camera preview which limits the code to API 14+. This
 *  can be easily replaced with a {@link android.view.SurfaceView} to run on older devices.
 */
public class MainActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    final Context context = this;
    ProgressDialog pDialog;
    private static final int MEDIA_RECORDER_REQUEST = 0;

    private Camera mCamera;
    private TextureView mPreview;
    private MediaRecorder mMediaRecorder;
    private File mOutputFile;

    private boolean isRecording = false;
    private static final String TAG = "Recorder";
    private static final  int CAMERA_MAX_DURATION = 10*1000;
    private Button captureButton;

    private final String[] requiredPermissions = {
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

        mPreview = (TextureView) findViewById(R.id.surface_view);
        captureButton = (Button) findViewById(R.id.button_capture);


    }

    /**
     * The capture button controls all user interaction. When recording, the button click
     * stops recording, releases {@link android.media.MediaRecorder} and {@link android.hardware.Camera}. When not recording,
     * it prepares the {@link android.media.MediaRecorder} and starts recording.
     *
     * @param view the view generating the event.
     */
    public void onCaptureClick(View view) {

        if (areCameraPermissionGranted()){
            startCapture();
        } else {
            requestCameraPermissions();
        }
    }

    private void startCapture(){

        if (isRecording) {
            // BEGIN_INCLUDE(stop_release_media_recorder)

            // stop recording and release camera
            try {
                mMediaRecorder.stop();  // stop the recording
                play_button();
            } catch (RuntimeException e) {
                // RuntimeException is thrown when stop() is called immediately after start().
                // In this case the output file is not properly constructed ans should be deleted.
                Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
                //noinspection ResultOfMethodCallIgnored
                mOutputFile.delete();
            }
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            setCaptureButtonText("Capture");
            isRecording = false;
            releaseCamera();
            // END_INCLUDE(stop_release_media_recorder)

        } else {

            // BEGIN_INCLUDE(prepare_start_media_recorder)

            new MediaPrepareTask().execute(null, null, null);

            // END_INCLUDE(prepare_start_media_recorder)

        }
    }

    private void setCaptureButtonText(String title) {
        captureButton.setText(title);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if we are using MediaRecorder, release it first
        releaseMediaRecorder();
        // release the camera immediately on pause event
        releaseCamera();
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            // clear recorder configuration
            mMediaRecorder.reset();
            // release the recorder object
            mMediaRecorder.release();
            mMediaRecorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            mCamera.lock();
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            // release the camera for other applications
            mCamera.release();
            mCamera = null;
        }
    }

    private boolean prepareVideoRecorder(){

        // BEGIN_INCLUDE (configure_preview)
        mCamera = CameraHelper.getDefaultCameraInstance();

        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                mSupportedPreviewSizes, mPreview.getWidth(), mPreview.getHeight());

        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        mCamera.setParameters(parameters);
        try {
                // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
                // with {@link SurfaceView}
                mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
        } catch (IOException e) {
            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
            return false;
        }
        // END_INCLUDE (configure_preview)


        // BEGIN_INCLUDE (configure_media_recorder)
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT );
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setMaxDuration(CAMERA_MAX_DURATION);
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(profile);
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                releaseMediaRecorder(); // release the MediaRecorder object
                mCamera.lock();         // take camera access back from MediaRecorder

                // inform the user that recording has stopped
                setCaptureButtonText("Capture");
                isRecording = false;
                releaseCamera();
            }
        });
        // Step 4: Set output file
        mOutputFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
        if (mOutputFile == null) {
            return false;
        }
        mMediaRecorder.setOutputFile(mOutputFile.getPath());

        // END_INCLUDE (configure_media_recorder)

        // Step 5: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private boolean areCameraPermissionGranted() {

        for (String permission : requiredPermissions){
            if (!(ActivityCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                MEDIA_RECORDER_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (MEDIA_RECORDER_REQUEST != requestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        boolean areAllPermissionsGranted = true;
        for (int result : grantResults){
            if (result != PackageManager.PERMISSION_GRANTED){
                areAllPermissionsGranted = false;
                break;
            }
        }

        if (areAllPermissionsGranted){
            startCapture();
        } else {
            // User denied one or more of the permissions, without these we cannot record
            // Show a toast to inform the user.
            Toast.makeText(getApplicationContext(),
                    getString(R.string.need_camera_permissions),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void onMergeClick(View view) {
        if (dalSuNapravljena_minimum_dva_videa())
        {
            new MergeTwoVideos().execute("");
        }
        else{
            Toast.makeText(context, "Mora da snimis 2 ili vise klipa da bi ih spojio!", Toast.LENGTH_LONG).show();
        }

    }

    public void onPlayVideoClick(View view) {
        play_button();
    }

    /**
     * Asynchronous task for preparing the {@link android.media.MediaRecorder} since it's a long blocking
     * operation.
     */
    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                MainActivity.this.finish();
            }
            // inform the user that recording has started
            setCaptureButtonText("Stop");

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            case R.id.listView_Videos:
              startActivity(new Intent(getApplicationContext(),VideoListActivity.class));
              overridePendingTransition(0,0);
                return true;
            case R.id.convert:
                startActivity(new Intent(getApplicationContext(),ConvertImageToVideo.class));
                overridePendingTransition(0,0);
                return true;
            case R.id.slowmotion:
                startActivity(new Intent(getApplicationContext(),SlowMotionEffect.class));
                overridePendingTransition(0,0);
                return true;
            case R.id.fadeinout:
                startActivity(new Intent(getApplicationContext(),FadeInEffectActivity.class));
                overridePendingTransition(0,0);
                return true;
            case R.id.zoominout:
                startActivity(new Intent(getApplicationContext(),ZoomInZoomOut.class));
                overridePendingTransition(0,0);
                return true;
            case R.id.change:
                startActivity(new Intent(getApplicationContext(),AddAudioInVideo.class));
                overridePendingTransition(0,0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

 public Boolean dalSuNapravljena_minimum_dva_videa()
 {
     String path = Environment.getExternalStorageDirectory().toString()+"/Pictures/CameraSample/";
     Log.d("Files", "Path: " + path);
     File directory = new File(path);
     File[] files = directory.listFiles();

   if (files.length>=2)
       return true;
   else
       return false;

 }
 public void play_button()
 {
     String path = Environment.getExternalStorageDirectory().toString()+"/Pictures/CameraSample/";
     Log.d("Files", "Path: " + path);
     File directory = new File(path);
     File[] files = directory.listFiles();
     for (int i = 0; i < files.length; i++)
     {
         Log.d("Files", "FileName:" + files[i].getName());
     }
     String source = path + files[files.length-1].getName();
    Data D = new Data();
    D.setUri_preview(source);
    startActivity(new Intent(context,PreviewActivity.class));
 }
 public void spojiDvaVidea()
 {

         String path = Environment.getExternalStorageDirectory().toString()+"/Pictures/CameraSample/";
         Log.d("Files", "Path: " + path);
         File directory = new File(path);
         File[] files = directory.listFiles();
         for (int i = 0; i < files.length; i++)
         {

             Log.d("Files", "FileName:" + files[i].getName());
         }
         String sourceFilePath1 = path + files[0].getName();
         String sourceFilePath2 = path + files[1].getName();
//        destFilePath = mp3File.getAbsolutePath();
     final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/MergedVideo";
     File dir = new File(dirPath);
     if (!dir.exists())
         dir.mkdirs();
         String pathEXPORT = Environment.getExternalStorageDirectory().getPath()+"/Pictures/MergedVideo" + "/"
                 + "SPOJENI_VIDEO_" + System.currentTimeMillis() + ".mp4";

         FFmpeg ffmpeg = FFmpeg.getInstance(MainActivity.this);


         try {
             ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                 @Override
                 public void onFailure() {
                     Log.e("gc", "onFailure command");
                 }
             });
         } catch (FFmpegNotSupportedException e) {
             Log.e("gc", "onSuccess command");
         }


         try {

//            String cmd[] = new String[]{"-y", "-i", sourceFilePath,
//                    "-vn", "-ar", "44100", "-ac", "2", "-b:a", "256k", "-f", "mp3", path};

        /* String cmd[] = new String[]{
                 "-i", sourceFilePath1, "-i", sourceFilePath2, "-i", sourceFilePath2 , "-preset", "ultrafast",
                 "-filter_complex", "[0:v] [0:a] [1:v] [1:a] [2:v] [2:a] concat=n=3:v=1:a=1 [v] [a]","-map","[v]","-map","[a]",path};*/
             String cmd[] =new String[]{"-y", "-i", sourceFilePath1, "-i", sourceFilePath2, "-strict", "experimental", "-filter_complex",
                     "[0:v]scale=1920x1080,setsar=1:1[v0];[1:v]scale=1920x1080,setsar=1:1[v1];[v0][0:a][v1][1:a] concat=n=2:v=1:a=1",
                     "-ab", "48000", "-ac", "2", "-ar", "22050", "-s", "1920x1080", "-vcodec", "libx264","-crf","27","-q","4","-preset", "ultrafast", pathEXPORT + "output.mp4"};
     /* ako treba da se skalira:
         String cmd[] = new String[]{"-y", "-i", sourceFilePath1, "-i", sourceFilePath2, "-strict", "experimental", "-filter_complex",
                 "[0:v]scale=iw*min(1920/iw\\,1080/ih):ih*min(1920/iw\\,1080/ih), pad=1920:1080:(1920-iw*min(1920/iw\\,1080/ih))/2:(1080-ih*min(1920/iw\\,1080/ih))/2,setsar=1:1[v0];[1:v] scale=iw*min(1920/iw\\,1080/ih):ih*min(1920/iw\\,1080/ih), pad=1920:1080:(1920-iw*min(1920/iw\\,1080/ih))/2:(1080-ih*min(1920/iw\\,1080/ih))/2,setsar=1:1[v1];[v0][0:a][v1][1:a] concat=n=2:v=1:a=1",
                 "-ab", "48000", "-ac", "2", "-ar", "22050", "-s", "1920x1080", "-vcodec", "libx264", "-crf", "27", "-q", "4", "-preset", "ultrafast", path + "output.mp4"};

      */
             ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                 @Override
                 public void onStart() {
                     Log.e("gc", "Command Started");
                 }

                 @Override
                 public void onProgress(String message) {
                     Log.e("gc", "onProgress" + message);
                 }

                 @Override
                 public void onFailure(String message) {
                     Log.e("gc", "onFailure command" + message);
                 }

                 @Override
                 public void onSuccess(String message) {
                     Log.e("gc", "onSuccess command" + message);
                 }

                 @Override
                 public void onFinish() {
                     Log.e("gc", "onFinish command");
                 }
             });

         } catch (FFmpegCommandAlreadyRunningException e) {
             // Handle if FFmpeg is already running
             e.printStackTrace();
         }
     }




    private class MergeTwoVideos extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
           /* try{

            }catch(Exception ioe){
                Toast.makeText(context, "GRESKA", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }*/

            return "executed";

        }

        @Override
        protected void onPostExecute(String result) {

            pDialog.dismiss();
            Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            spojiDvaVidea();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Spajanje prva dva kreirana videa...Sacekaj brt");
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
