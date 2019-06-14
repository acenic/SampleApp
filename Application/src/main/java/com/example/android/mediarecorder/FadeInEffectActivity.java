package com.example.android.mediarecorder;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class FadeInEffectActivity extends AppCompatActivity {
final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fade_in_effect);
    }
    public void slowMotionCreate(View view) {
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







        String[] cmd = {"-y", "-i", inputPathOriginal, "-acodec", "copy", "-vf", "fade=t=in:st=0:d=5,fade=t=out:st=" + String.valueOf(10 - 5) + ":d=5", outputPath};
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
