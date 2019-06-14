package com.example.android.mediarecorder;

import android.content.Context;
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

public class AddAudioInVideo extends AppCompatActivity {
TextView LOC ;

    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio_in_video);
        LOC = (TextView)findViewById(R.id.lokacijaAudioFajla);
        String musicPathMP3 = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Snare.mp3";
        LOC.setText("LOKACIJA AUDIO FAJLA:"+"\n"+musicPathMP3);
    }

    public void Compress_uj_Video(View view) {
        String outputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/result.mp4";
        String musicPathMP3 = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Snare.mp3";
        String inputPathOriginal = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultOriginal.mp4";
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


        File f = new File(outputPath);
      /*  if (f.exists())
        {       f.delete();    }*/
        //   String s = "-i "+inputPathOriginal+" -i "+musicPathMP3+" -c:v copy -c:a copy " + outputPath;
        //String s = "-i "+inputPathOriginal+" -i "+musicPathMP3+" -c:v copy -map 0:v:0 -map 1:a:0 " + outputPath;
        String s = "-i "+inputPathOriginal+" -map 0 -map -0:a -c copy "+outputPath;
    /*    String[] cmd ={"-i",            inputPathOriginal,
                "-i",
               musicPathMP3
                ,
                "-c:v",
                "copy",
                "-c:a",
                "aac",
                "-strict",
                "experimental",
                "-map",
                "0:v:0",
                "-map",
                "1:a:0",outputPath};*/
        String[] cmd ={"-i", inputPathOriginal, "-i",musicPathMP3 ,"-c:v","copy",
                "-c:a",  "aac",  "-strict", "experimental",   "-map",      "0:v:0",
                "-map",   "1:a:0",outputPath};
      //  String[] cmd= s.split(" ");
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

    public void ObrisiAudio(View view) {
        //
        String outputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/result.mp4";
        String outputPathOBRISANI_AUDIO = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/resultOBRISAN_AUDIO.mp4";
        String musicPathMP3 = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Snare.mp3";
        String inputPathOriginal = Environment.getExternalStorageDirectory().getAbsolutePath() +"/resultOriginal.mp4";
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

        String s = "-i "+outputPath+" -c:v copy -an -movflags faststart -pix_fmt yuv420p "+outputPathOBRISANI_AUDIO;

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
