package com.onescream.intros;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onescream.HomeActivity;
import com.onescream.R;
import com.onescream.Utils.Utility;
import com.uc.prjcmn.ActivityTask;

public class About extends Activity implements View.OnClickListener {
    Typeface facethin,facebold,faceRegular,EstiloRegular;
    Typeface sanfacebold,sanfaceRegular, sanfacesemibold
            ,sanfaceMedium, sanfacelight;
    private Utility utility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility = new Utility(this);
        utility.RegisterScreen(this, getResources().getString(R.string.about));
        setContentView(R.layout.activity_about);
        ActivityTask.INSTANCE.add(this);

        updateLCD();
    }
    private void updateLCD() {
        initTypeFace();
        TextView Title= (TextView)findViewById(R.id.title);
        Title.setTypeface(sanfacebold);
        TextView Text1= (TextView)findViewById(R.id.text1);
        Text1.setTypeface(sanfacelight);
        TextView txtfb= (TextView)findViewById(R.id.tv_btn_fb);
        txtfb.setTypeface(sanfaceMedium);
        TextView txt_rate= (TextView)findViewById(R.id.tv_btn_appstore);
        txt_rate.setTypeface(sanfaceMedium);
        TextView txt_invite= (TextView)findViewById(R.id.tv_btn_invite);
        txt_invite.setTypeface(sanfaceMedium);
        TextView txtone= (TextView)findViewById(R.id.tv_btn_signup);
        txtone.setTypeface(sanfaceMedium);

        findViewById(R.id.frm_btn_fb).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.frm_btn_appstore).setOnClickListener(this);
        findViewById(R.id.frm_btn_invite).setOnClickListener(this);
        findViewById(R.id.frm_btn_one).setOnClickListener(this);

    }

    private void initTypeFace()
    {
        facethin = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Thin.ttf");
        facebold = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Bold.ttf");
        faceRegular = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Regular.ttf");
        EstiloRegular = Typeface.createFromAsset(this.getAssets(),
                "fonts/EstiloRegular.otf");

        sanfacebold = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Bold.otf");
        sanfaceRegular = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Regular.otf");
        sanfaceMedium = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Medium.otf");
        sanfacesemibold = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Semibold.otf");
        sanfacelight= Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Light.otf");

    }

    @Override
    public void onClick(View v) {
    switch (v.getId()){
        case R.id.iv_back:
            Intent intent = new Intent(About.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
            finish();
            break;
        default:
            break;
    }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(About.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}
