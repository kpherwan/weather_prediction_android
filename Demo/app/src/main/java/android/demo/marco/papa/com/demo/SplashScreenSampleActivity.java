package android.demo.marco.papa.com.demo;

import android.app.Activity;
import android.os.Bundle;

import androidx.core.splashscreen.SplashScreen;

public class SplashScreenSampleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
    }
}
