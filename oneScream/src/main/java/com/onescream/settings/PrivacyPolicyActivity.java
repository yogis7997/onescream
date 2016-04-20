package com.onescream.settings;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.onescream.R;
import com.uc.prjcmn.ActivityTask;

/**
 * Activity class for Privacy Policy Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class PrivacyPolicyActivity extends Activity implements View.OnClickListener {

    private final String TAG = "PrivacyPolicyActivity";

    public static String PARAM_IN_SIGNING = "is_in_signing";

    private Context mContext;

    private View m_frmHeader;

    private TextView m_tvSubTitle;
    private TextView m_tvContents;

    private View m_frmBack;

    private boolean m_bInSigning;

    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        ActivityTask.INSTANCE.add(this);

        mContext = (Context) this;

        m_bInSigning = getIntent().getBooleanExtra(PARAM_IN_SIGNING, false);

        updateLCD();

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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    // //////////////////////////////////////////////////
    private void updateLCD() {

        m_frmHeader = findViewById(R.id.frm_header);

        findViewById(R.id.iv_back).setOnClickListener(this);
        Typeface sanfacebold = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Bold.otf");
        TextView titl = (TextView) findViewById(R.id.titl);
        titl.setTypeface(sanfacebold);
        m_tvSubTitle = (TextView) findViewById(R.id.tv_sub_title);
        m_tvContents = (TextView) findViewById(R.id.tv_contents);

        m_frmBack = findViewById(R.id.frm_btn_back);
        m_frmBack.setOnClickListener(this);

        if (m_bInSigning) {
            m_frmHeader.setVisibility(View.GONE);
            m_frmBack.setVisibility(View.VISIBLE);
        } else {
            m_frmHeader.setVisibility(View.VISIBLE);
            m_frmBack.setVisibility(View.GONE);
        }


        refreshContents();
    }

    private void refreshContents() {

    }


    // /////////////////////////////////////
    public void goBack() {
        finish();
        overridePendingTransition(R.anim.hold, R.anim.right_out);
    }


    /**
     * OnClick Event method
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                goBack();
                break;
            case R.id.frm_btn_back:
                goBack();
                break;
            default:
                break;
        }
    }
}
