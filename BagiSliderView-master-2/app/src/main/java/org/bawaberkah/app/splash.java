package org.bawaberkah.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.felipecsl.gifimageview.library.GifImageView;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;

public class splash extends AppCompatActivity {

    //private GifImageView gifView;
    Animation frmBottom, fade, frmLeft, frmRight;
    ImageView logoBaber, labelBaber, tagBaber;
    TextView versi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        labelBaber = (ImageView)findViewById(R.id.logoBaber);
        logoBaber = (ImageView)findViewById(R.id.logo);
        tagBaber = (ImageView)findViewById(R.id.tagBaber);
        versi = (TextView)findViewById(R.id.lblversi);
        fade = AnimationUtils.loadAnimation(this,R.anim.fadeout);
        frmBottom = AnimationUtils.loadAnimation(this,R.anim.frmbottom);
        frmRight = AnimationUtils.loadAnimation(this,R.anim.fromright);
        frmLeft = AnimationUtils.loadAnimation(this,R.anim.fromleft);

        labelBaber.setAnimation(fade);
        versi.setAnimation(frmBottom);
        tagBaber.setAnimation(frmRight);
        logoBaber.setAnimation(frmLeft);

        //AppUpdateChecker appUpdateChecker=new AppUpdateChecker(this);  //pass the activity in constructure
        //appUpdateChecker.checkForUpdate(false); //mannual check false here

        /*gifView = (GifImageView)findViewById(R.id.gifImageView);

        try {
            InputStream inputStream = getAssets().open("splash_screen_bb.gif");
            byte[] bytes = IOUtils.toByteArray(inputStream);
            gifView.setBytes(bytes);
            gifView.startAnimation();
        }
        catch (IOException e){

        }*/

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                splash.this.startActivity(new Intent(splash.this.getApplicationContext(), MainActivity.class));
                splash.this.finish();
            }
        }, 4500L);
    }
}
