package com.ezcorporate.Others;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ezcorporate.Authentications.CompnayInfo;
import com.ezcorporate.Authentications.LoginActivity;
import com.ezcorporate.Authentications.MainActivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.R;
public class SplashScreen extends AppCompatActivity {
    LinearLayout l;
    ImageView iv;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        init();
        fullScreen();
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        iv.startAnimation(anim);

        Thread splashTread = new Thread() {
            @Override
            public void run() {

                try {
                    sleep(3000);
                    Log.i("statusLogin",""+SharedPrefManager.getInstance(getApplicationContext()).isUrlExist());
                    if(SharedPrefManager.getInstance(getApplicationContext()).isUrlExist()){
                        if(SharedPrefManager.getInstance(getApplicationContext()).isLogIn()) {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            SplashScreen.this.finish();
                            overridePendingTransition(R.anim.in_right,R.anim.out_left);
                        }else {
                            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                            SplashScreen.this.finish();
                            overridePendingTransition(R.anim.in_right,R.anim.out_left);
                        }
                    }else {
                        startActivity(new Intent(SplashScreen.this, CompnayInfo.class));
                        SplashScreen.this.finish();
                        overridePendingTransition(R.anim.in_right,R.anim.out_left);
                    }

                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }
            }
        };
        splashTread.start();
    }

    private void init() {
        l = findViewById(R.id.layout);
        iv = findViewById(R.id.logo);

    }
    public void fullScreen() {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
        } else {
        }
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }
}
