package com.adplan.smsapplication.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;

import com.adplan.smsapplication.R;

public class SplashActivity extends Activity {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash, options);
        image = (ImageView) findViewById(R.id.activity_splash_image);
        image.setImageBitmap(bitmap);

        CountDownTimer timer = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
