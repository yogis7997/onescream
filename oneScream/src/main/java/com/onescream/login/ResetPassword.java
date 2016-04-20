package com.onescream.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onescream.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

import java.util.ArrayList;

public class ResetPassword extends Activity implements View.OnClickListener {
    private Context mContext;
    Typeface RobotoBold;
    private EditText m_etEmail;
    private String m_strEmail;
    LinearLayout Reset_error;
    TextView Title_tv,Message_tv,btn_tv;
    Typeface facethin,facebold,faceRegular,EstiloRegular,sanfaceMedium,sanfacebold;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ActivityTask.INSTANCE.add(this);

        mContext = (Context) this;
        updateLCD();
    }
    private void updateLCD() {
        initTypeFace();
         RobotoBold = Typeface.createFromAsset( getAssets(),
                "fonts/Roboto-Bold.ttf");
        TextView tv_btn_submit= (TextView)findViewById(R.id.tv_btn_login);
        Reset_error=(LinearLayout)findViewById(R.id.reseterror);
        tv_btn_submit.setTypeface(RobotoBold);
        findViewById(R.id.frm_btn_Reset).setOnClickListener(this);
        findViewById(R.id.frm_btn_thanks).setOnClickListener(this);
        findViewById(R.id.tv_btn_signup).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        m_etEmail = (EditText) findViewById(R.id.et_email);
        Title_tv= (TextView)findViewById(R.id.tv_head_resetpassword);
        Title_tv.setTypeface(sanfacebold);
        Message_tv= (TextView)findViewById(R.id.msg_tv);
        Message_tv.setTypeface(sanfaceMedium);
        btn_tv= (TextView)findViewById(R.id.tv_btn_login);
        btn_tv.setTypeface(facebold);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frm_btn_Reset:
                m_strEmail = m_etEmail.getText().toString();
                if (m_strEmail.isEmpty() ) {
                    PRJFUNC.showAlertDialog(ResetPassword.this,
                            "Invalid Email");
                    return;
                }
                hideKeyboard();
                Reset();
                break;
            case R.id.frm_btn_thanks:
                Intent intent = new Intent(ResetPassword.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
                break;

            case R.id.tv_btn_signup:
                Intent in = new Intent(ResetPassword.this, SignupActivity.class);
                startActivity(in);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void initTypeFace()
    {
        facethin = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Thin.ttf");
        facebold = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Bold.otf");

        faceRegular = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Regular.ttf");
        EstiloRegular = Typeface.createFromAsset(this.getAssets(),
                "fonts/EstiloRegular.otf");
        sanfaceMedium = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Medium.otf");
        sanfacebold = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Bold.otf");


    }

    private void Reset() {

        PRJFUNC.showProgress(mContext, "Reseting...");




        ParseUser.requestPasswordResetInBackground(m_strEmail,
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {

                            Reset_error.setVisibility(View.GONE);
                            // An email was successfully sent with reset instructions.
                            Title_tv.setText("Success!");
                            Message_tv.setText("Your password reset email has been sent.");
                            ImageView line=(ImageView)findViewById(R.id.line);
                            line.setVisibility(View.GONE);
//                            Message_tv.setText("");
                            findViewById(R.id.frm_btn_Reset).setVisibility(View.GONE);
                            findViewById(R.id.frm_btn_thanks).setVisibility(View.VISIBLE);
                            m_etEmail.setVisibility(View.GONE);
                            PRJFUNC.closeProgress(ResetPassword.this);

                        } else {
//                            PRJFUNC.showAlertDialog(ResetPassword.this,
//                                    "There is no such user");
                            // Something went wrong. Look at the ParseException to see what's up.
                            Reset_error.setVisibility(View.VISIBLE);
                            findViewById(R.id.frm_btn_thanks).setVisibility(View.GONE);
                            findViewById(R.id.frm_btn_Reset).setVisibility(View.VISIBLE);
                            PRJFUNC.closeProgress(ResetPassword.this);
                        }
                    }
                });







    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
