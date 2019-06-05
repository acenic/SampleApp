package com.example.android.mediarecorder;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.File;

public class ConvertImageToVideo extends AppCompatActivity {
final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_image_to_picture);
    }
    public void NapraviTo()
    {
       // Uri path1 = Uri.parse("android.resource://"+BuildConfig.APPLICATION_ID+"/" + R.drawable.photo);
     //   String photoPath = path1.getPath();


        String outputPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/result.mp4";
       // String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/result.mp4";
        String slikaPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/ce_vidimo.png";
        String slikaPathPNG = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() +"/photo.jpg";
        String musicPathWAV = Environment.getExternalStorageDirectory().getAbsolutePath() +"/prazan_zvuk.wav";
        String musicPathMP3 = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Snare.mp3";
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

//ffmpeg -loop 1 -y -i image8.jpg -i sound11.amr -shortest -acodec copy -vcodec mjpeg result.avi
//String cmd[] = {"-y" ,"-i" ,slikaPath,"-i", musicPath,"-shortest","-acodec","copy","-vcodec","mjpeg",outputPath};
       /* String s = "-loop 1 -i +"+slikaPath+" -i "+musicPath+" -c:v libx264 -tune stillimage -c:a aac -b:a 192k -pix_fmt yuv420p -shortest "+outputPath;*/
       //ffmpeg -r 1 -loop 1 -i ep1.jpg -i ep1.wav -acodec copy -r 1 -shortest -vf scale=1280:720 ep1.flv
//String s = "-i "+slikaPath+" -i " + musicPath + " "+outputPath;
//String s = "ffmpeg -loop 1 -i "+slikaPath+" -c:v libx264 -t 15 -pix_fmt yuv420p -vf scale=320:240" + outputPath;
//String s = "-i "+slikaPath+" -pix_fmt yuv420p -t 20 "+ outputPath;
       // String s = " -y -i "+slikaPath+" -vf format=yuv420p -t 30 "+outputPath;
    //    String s = "ffmpeg -loop 1 -framerate 1 -i "+slikaPath+" -vf fps=25,format=yuv420p -t 30 " + outputPath;
     /* String s =  "-y -i "+musicPathMP3+" -f image2 -loop 1 -framerate 2 -i "+slikaPathPNG+" -shortest -c:a copy -c:v libx264 -crf 18 -r 30 -preset veryfast -movflags +faststart "+outputPath;*/
      /*  String cmd[]={
                "-y",
                "-r",
                "1/5",
                "-i",
               slikaPath, // only one image file path
                "-c:v",
                "libx264",
                "-vf",
                "fps=25",
                "-pix_fmt",
                "yuv420p",
                outputPath
        };*/
      File f = new File(outputPath);
      if (f.exists())
      {
          f.delete();
      }
        String s = "-loop 1 -i "+slikaPathPNG+" -f lavfi -i anullsrc -c:v libx264 -t 5 -pix_fmt yuv420p -crf 25 -s 1280x720 " + outputPath;
       String[] cmd= s.split(" ");
    /*    String cmd[] =new String[]{"-y", "-i", pa, "-i", sourceFilePath2, "-strict", "experimental", "-filter_complex",
                "[0:v]scale=1920x1080,setsar=1:1[v0];[1:v]scale=1920x1080,setsar=1:1[v1];[v0][0:a][v1][1:a] concat=n=2:v=1:a=1",
                "-ab", "48000", "-ac", "2", "-ar", "22050", "-s", "1920x1080", "-vcodec", "libx264","-crf","27","-q","4","-preset", "ultrafast", pathEXPORT + "output.mp4"};*/
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
      // dialogMessage(s);
    }
    public void dialogMessage(String tt)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(""+tt);

        AlertDialog  dialog = builder.create();



        dialog.show();
    }

    public void konvertuj(View view) {NapraviTo();
    }
}
