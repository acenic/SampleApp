package com.example.android.mediarecorder;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.IOException;

public class PreviewActivity extends AppCompatActivity {
    Data d;
    String path;
    VideoView videoView;
    final Context context = this;

    private MediaRecorder myAudioRecorder;
    private String outputFile;
    int Current_MEDIA_VOLUME = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);


        d = new Data();
        if (d != null)
        {
          final Button playAudio, stopAudio, recordAudio;
            playAudio = (Button) findViewById(R.id.play);
            stopAudio = (Button) findViewById(R.id.stop);
            recordAudio = (Button) findViewById(R.id.record);
            stopAudio.setEnabled(false);
            playAudio.setEnabled(false);
            outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/CameraSample/recording.3gp";
            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            myAudioRecorder.setOutputFile(outputFile);

            recordAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            stopAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            playAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(outputFile);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        // make something
                    }
                }
            });

            /****---------------------    video ---------------------*****/

           // path = d.getUri_preview();
           /* Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();*/
            String path = Environment.getExternalStorageDirectory().toString()+"/Pictures/CameraSample/";
            Log.d("Files", "Path: " + path);
            File directory = new File(path);
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                Log.d("Files", "FileName:" + files[i].getName());
            }
           final String source = path + files[files.length-1].getName();
            TextView tv = (TextView)findViewById(R.id.pathTEXT);
            tv.setText(source);
             videoView = (VideoView)findViewById(R.id.VideoView);
            videoView.setVideoPath(source);
          // videoView.start();

            final  Button  playVideo = (Button)findViewById(R.id.playVideo);
           final Button  stopVideo = (Button)findViewById(R.id.stopVideo);
            playVideo.setEnabled(true);
            stopVideo.setEnabled(false);
            playVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    utisajAudioSkroz();
                    //kod za snimanje audio
                    try {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();

                    } catch (IllegalStateException ise) {
                        // make something ...
                    } catch (IOException ioe) {
                        // make something
                    }
                    recordAudio.setEnabled(false);
                    stopAudio.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

                    //video kod
                    playVideo.setEnabled(false);
                    stopVideo.setEnabled(true);
                    videoView = null;
                    videoView = (VideoView)findViewById(R.id.VideoView);
                    videoView.setVideoPath(source);
                    videoView.start();
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            stopVideo.setEnabled(false);
                            playVideo.setEnabled(true);
                            Toast.makeText(PreviewActivity.this, "Zavrsen video", Toast.LENGTH_SHORT).show();
                            //kod za audio
                            myAudioRecorder.stop();
                            myAudioRecorder.release();
                            myAudioRecorder = null;
                            recordAudio.setEnabled(true);
                            stopAudio.setEnabled(false);
                            playAudio.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_SHORT).show();
                            Compress_uj_Video();
                            pojacajVolume();
                        }
                    });

                }
            });


            stopVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //kod za video
                    if(null!=videoView){
                        videoView.stopPlayback();
                        //kod za audio
                        myAudioRecorder.stop();
                        myAudioRecorder.release();
                        myAudioRecorder = null;
                        recordAudio.setEnabled(true);
                        stopAudio.setEnabled(false);
                        playAudio.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Audio Recorder successfully", Toast.LENGTH_LONG).show();
                        Compress_uj_Video();
                        pojacajVolume();
                    }

                }
            });

        }

    }
    public void ObrisiAudio(View view) {
        //
        String outputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/result.mp4";
        //String outputPathOBRISANI_AUDIO = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/resultOBRISAN_AUDIO.mp4";


        String inputPathOriginal = Environment.getExternalStorageDirectory().toString()+"/Pictures/CameraSample/";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }
        /*  File f = new File(outputPath);
      if (f.exists())
        {       f.delete();    }*/

        String s = "-i "+inputPathOriginal+" -c:v copy -an -movflags faststart -pix_fmt yuv420p "+inputPathOriginal+"1";

        String[] cmd= s.split(" ");
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Toast.makeText(context, "starting....", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(String message) {
                    Toast.makeText(context, "progressing....", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(context, "Failure: "+message, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String message) {
                    Toast.makeText(context, "succes" + message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    Toast.makeText(context, "finish", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            Toast.makeText(context, ""+e, Toast.LENGTH_LONG).show();
        }
    }
    public void Compress_uj_Video() {
       final String outputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/result.mp4";
        File f = new File(outputPath);
        if (f.exists())
        {       f.delete();    }
        String musicPathMP3 =  Environment.getExternalStorageDirectory().toString()+"/Pictures/CameraSample/recording.3gp";
        String path = Environment.getExternalStorageDirectory().toString()+"/Pictures/CameraSample/";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
        final String inputPathOriginal = path + files[files.length-1].getName();

        FFmpeg ffmpeg = FFmpeg.getInstance(context);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }



        //   String s = "-i "+inputPathOriginal+" -i "+musicPathMP3+" -c:v copy -c:a copy " + outputPath;
        //String s = "-i "+inputPathOriginal+" -i "+musicPathMP3+" -c:v copy -map 0:v:0 -map 1:a:0 " + outputPath;
       // String s = "-i "+inputPathOriginal+" -map 0 -map -0:a -c copy "+outputPath;


        String[] cmd ={"-i", inputPathOriginal, "-i",musicPathMP3 ,"-c:v","copy",
                "-c:a",  "aac",  "-strict", "experimental",   "-map",      "0:v:0",
                "-map",   "1:a:0",outputPath};
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Toast.makeText(context, "starting....", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(String message) {
                    Toast.makeText(context, "progressing....", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(context, "Failure: "+message, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(String message) {
                    Toast.makeText(context, "succes" + message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    Toast.makeText(context, "finish", Toast.LENGTH_SHORT).show();
                    TextView tv = (TextView)findViewById(R.id.outputPathID);
                    tv.setText("Video je smesten u DCIM:" + outputPath);
                }
            });

        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            Toast.makeText(context, ""+e, Toast.LENGTH_LONG).show();
        }
    }
    public void utisajAudioSkroz()
    {
        //Utisava se media volume dok se reprodukuje video , a kasnije se poziva funkcije pojacajVolume() kada se zavrsi reprodukovanje/snimanje videa
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        Current_MEDIA_VOLUME = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }
    public void pojacajVolume()
    {
        //Utisava se media volume dok se reprodukuje video , a kasnije se poziva funkcije pojacajVolume() kada se zavrsi reprodukovanje/snimanje videa
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Current_MEDIA_VOLUME, 0);
    }
}
