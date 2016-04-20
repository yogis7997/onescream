package com.onescream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.onescream.intros.FirstScreenActivity;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Activity class for Splash Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class _SplashActivity extends Activity implements View.OnClickListener {

    private final String TAG = "_DefaultActivity";

    private Context mContext;

    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActivityTask.INSTANCE.add(this);

        mContext = (Context) this;
        
        updateLCD();

        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
                    goToIntroActivity(phoneDb.isLoggedInOnce());
                }
            }, 2000);
        } else {
            user.fetchInBackground();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    goToHomeActivity();
                }
            }, 2000);
        }

    }

    @Override
    protected void onDestroy() {
        releaseValues();

        ActivityTask.INSTANCE.remove(this);

        super.onDestroy();
    }

    private void releaseValues() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    // //////////////////////////////////////////////////
    private void updateLCD() {

    }


    /**
     * OnClick Event method
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    /**
     * Go to home activity
     */
    public void goToHomeActivity() {
        Intent intent = new Intent(_SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToIntroActivity(boolean p_bLoggedOnce) {
        Intent intent = new Intent(_SplashActivity.this, FirstScreenActivity.class);
        intent.putExtra(FirstScreenActivity.PARAM_ONLY_FIRST_SCREEN, p_bLoggedOnce);
        startActivity(intent);
        finish();
    }

}
