package groupfiesta.fiestafinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Stacy on 4/7/2016.
 */
public class SplashScreen extends Activity {
    private static int TIME_OUT = 3000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, Login.class);
                startActivity(i);

                finish();
            }
        }, TIME_OUT);
    }
}
