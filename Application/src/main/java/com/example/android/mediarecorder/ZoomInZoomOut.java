package com.example.android.mediarecorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

public class ZoomInZoomOut extends AppCompatActivity {
final Context context = this;
    String videoResultion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_in_zoom_out);
        String inputPathOriginal = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultOriginal.mp4";
        String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/result.mp4";
        String slikaPathPNG = Environment.getExternalStorageDirectory().getAbsolutePath() +"/text.png";
        MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
        mRetriever.setDataSource(inputPathOriginal);
        Bitmap frame = mRetriever.getFrameAtTime();

        int width = frame.getWidth();
        int height = frame.getHeight();
         videoResultion = width + "x" + height;
        TextView r = (TextView)findViewById(R.id.resoultion);
        r.setText(videoResultion);
        TextView ip = (TextView)findViewById(R.id.inputPath);
        ip.setText(inputPathOriginal);
    }
    public void AcrossFadeBetween2Videos(View view) {
        String inputPathOriginal = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultOriginal.mp4";
        String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/result.mp4";
        String slikaPathPNG = Environment.getExternalStorageDirectory().getAbsolutePath() +"/text.png";


        // File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
       //ffmpeg -loop 1 -i image_1.jpg -vf "zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=125" -c:v libx264 -t 5 -s "800x450"
        //-loop 1 -i image1.jpg -vf "zoompan=z='1.5-on/duration*0.5':d=125" -c:v libx264 -t 5 -s "800x450
       // String s = "-loop 1 -i "+slikaPathPNG+" -vf zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=125 -c:v libx264 -t 5 -s 800x450 " + outputPath;
       // String s = "-loop 1 -i "+inputPathOriginal+" -vf zoompan=z=1.5-on/duration*0.2:d=325 -t 30 " + outputPath;
        //String s = "-i "+inputPathOriginal+" -vf crop=640:256:0:400 -threads 5 -preset ultrafast -strict -2 " + outputPath; //RADI
      //  String s = "-i "+slikaPathPNG+" -vf zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=125 -c:v libx264 -t 5 -s 800x450 zoomin " + outputPath;
        /**  scale=960x720,trim=duration=5 **/

         /**/String s = "-i "+inputPathOriginal+" -i "+inputPathOriginal+" -f lavfi -i color=black -filter_complex [0:v]format=pix_fmts=yuva420p,fade=t=out:st=4:d=1:alpha=1,setpts=PTS-STARTPTS[va0];" +
                  "[1:v]format=pix_fmts=yuva420p,fade=t=in:st=0:d=1:alpha=1,setpts=PTS-STARTPTS+4/TB[va1];" +
                  "[2:v]scale="+videoResultion+",trim=duration=5[over];" +
                  "[over][va0]overlay[over1];" +
                  "[over1][va1]overlay=format=yuv420[outv] " +
                  "-vcodec libx264 -map [outv] " + outputPath;


/*String s = "-i "+inputPathOriginal+" -i "+inputPathOriginal+" -filter_complex [1]format=yuva444p,fade=t=in:st=0:d=2:alpha=1,setpts=PTS-STARTPTS+18/TB[va1];[0][va1]overlay[outv];[0][1]acrossfade=d=2[outa] -map [outv] -map [outa] -crf 10 " + outputPath;*/
        String[] cmd= s.split(" ");
        // String[] cmd = {"-i",inputPathOriginal,"-vf","scale=2*iw:-1", "crop=iw/2:ih/2", outputPath};
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
    public void VideoSaTekstom(View view) {
        // File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        String inputPathOriginal = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultOriginal.mp4";
        String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/result.mp4";
        String slikaPathPNG = Environment.getExternalStorageDirectory().getAbsolutePath() +"/text.png";
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
        //ffmpeg -i movie.mp4 -i logo.png -filter_complex overlay output.mp4
        String s = "-i "+inputPathOriginal+" -i "+slikaPathPNG+" -filter_complex overlay " + outputPath;
        String[] cmd= s.split(" ");
        // String[] cmd = {"-i",inputPathOriginal,"-vf","scale=2*iw:-1", "crop=iw/2:ih/2", outputPath};
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

    public void ZoomOut(View view) {
        // File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        String outputPathVIEDO = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultsFadeInFadeOut.mp4";
        String inputPathOriginal = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultOriginal.mp4";
        String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/result.mp4";
        String slikaPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/ce_vidimo.png";
        String slikaPathPNG = Environment.getExternalStorageDirectory().getAbsolutePath() +"/photo.jpg";
        String musicPathWAV = Environment.getExternalStorageDirectory().getAbsolutePath() +"/prazan_zvuk.wav";
        String musicPathMP3 = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Snare.mp3";
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
        /** dodatno objasnjenje sa stackoverlow-a:
         -s will set scale (video canvas area)
         crop=480:480 will crop from centre.. if you need from specific use: crop=480:480:pos-X:pos-Y
         setdar will set Display Aspect Ratio
         setsar will set Sample Aspect Ratio  **/
        File f = new File(outputPath);
        if (f.exists())
        {
            f.delete();
        }

        // String s = "-i "+inputPathOriginal+" -vf zoompan=z='if(lte(zoom,1.0),1.5,max(1.001,zoom-0.0015))':d=125 -c:v libx264 -t 5 -s 800x450 " + outputPath;// RADI !!!!!
        String s = "-i "+inputPathOriginal+" -vf zoompan=z='1.5-on/duration*0.5':d=125 -c:v libx264 -t 5 -s "+videoResultion+" " + outputPath;
        String[] cmd= s.split(" ");
        // String[] cmd = {"-i",inputPathOriginal,"-vf","scale=2*iw:-1", "crop=iw/2:ih/2", outputPath};
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

    public void Zoom_PICTURE(View view) {

    }

    public void ZoomCrop(View view) {
        // File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        String outputPathVIEDO = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultsFadeInFadeOut.mp4";
        String inputPathOriginal = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultOriginal.mp4"; //ulazna putanja za video
        String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/result.mp4";
        String slikaPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/ce_vidimo.png";
        String slikaPathPNG = Environment.getExternalStorageDirectory().getAbsolutePath() +"/photo.jpg";
        String musicPathWAV = Environment.getExternalStorageDirectory().getAbsolutePath() +"/prazan_zvuk.wav";
        String musicPathMP3 = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Snare.mp3";
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
        /** dodatno objasnjenje sa stackoverlow-a:
            -s will set scale (video canvas area)
            crop=480:480 will crop from centre.. if you need from specific use: crop=480:480:pos-X:pos-Y
            setdar will set Display Aspect Ratio
            setsar will set Sample Aspect Ratio  **/


        String s = "-i "+inputPathOriginal+" -s 480x480 -vf crop=480:480,setdar=1:1,setsar=1:1 " + outputPath;
        String[] cmd= s.split(" ");
        // String[] cmd = {"-i",inputPathOriginal,"-vf","scale=2*iw:-1", "crop=iw/2:ih/2", outputPath};
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

    public void ZoomIn(View view) {
        // File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        String outputPathVIEDO = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultsFadeInFadeOut.mp4";
        String inputPathOriginal = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultOriginal.mp4";
        String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/result.mp4";
        String slikaPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/ce_vidimo.png";
        String slikaPathPNG = Environment.getExternalStorageDirectory().getAbsolutePath() +"/photo.jpg";
        String musicPathWAV = Environment.getExternalStorageDirectory().getAbsolutePath() +"/prazan_zvuk.wav";
        String musicPathMP3 = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Snare.mp3";
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {

                }
            });
        } catch (FFmpegNotSupportedException e) {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }

        File f = new File(outputPath);
        if (f.exists())
        { f.delete(); }
        /**This has to be done manually. For two minutes, frame count is 3000 (@ the default 25 fps).
            -loop shouldn't be used when using zoompan with a single image.
            link sa Stackoverflow-a preuzet  ---> https://superuser.com/questions/1358310/ffmpeg-ffmpeg-image-zoom-in-according-to-duration  */
        String s = "-i "+inputPathOriginal+" -vf zoompan=z='1+on/1000*2':d=1000 -t 30 " + outputPath;
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
}
