package com.onescream.intros;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onescream.HomeActivity;
import com.onescream.R;
public class TourLastActivity extends Activity implements View.OnClickListener {
    Typeface facethin,facebold,faceRegular,EstiloRegular,faceMedium;
    Typeface sanfacebold,sanfaceRegular, sanfaceMedium,sanfacesemibold,proximasemi;

    private TextView m_tvContents;
    ImageView Indicator6,Indicator5;
    boolean getValue=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_new);
        updateLCD();
    }






    private void updateLCD() {
        initTypeFace();
        TextView title = (TextView ) findViewById(R.id.title);
        TextView introtxt = (TextView )findViewById(R.id.intro);
        TextView text1 = (TextView ) findViewById(R.id.text1);
        TextView text2 = (TextView ) findViewById(R.id.text2);
        TextView btn_try = (TextView ) findViewById(R.id.tv_btn_trial);
        TextView btn_subs = (TextView ) findViewById(R.id.tv_btn_subscribe);
        btn_try.setTypeface(sanfacebold);
        btn_subs.setTypeface(sanfacebold);

        //		 TextView labelTitle = (TextView) v.findViewById(R.id.labelll);
        //		 labelTitle.setTypeface(EstiloRegular);
        title.setTypeface(sanfaceRegular);
        introtxt.setTypeface(sanfacesemibold);
        text1.setTypeface(proximasemi);
        text2.setTypeface(proximasemi);
        // /> Free Trial
        findViewById(R.id.frm_btn_trial).setOnClickListener(this);

        // /> SubScribe
        findViewById(R.id.frm_btn_subscribe).setOnClickListener(this);
        Indicator6= (ImageView) findViewById(R.id.iv_page6);
        Indicator5= (ImageView ) findViewById(R.id.iv_page5);
        if(getValue){
            Indicator6.setVisibility(View.GONE);
            Indicator5.setVisibility(View.GONE);
        }
        //findViewById(R.id.iv_page6).setSelected(true);
//		m_tvContents = (TextView) v.findViewById(R.id.tv_contents);
//
//		v.findViewById(R.id.frm_btn_continue).setOnClickListener(this);

        //	refreshContents();
    }

    private void refreshContents() {
        String strContents = getString(R.string.intro_text5_1);
        strContents += "\n\n" + getString(R.string.intro_text5_2);
        strContents += "\n\n" + getString(R.string.intro_text5_3);
        strContents += "\n\n" + getString(R.string.intro_text5_4);

        m_tvContents.setText(strContents);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frm_btn_trial:
                goToHomeActivity(false);
                break;
            case R.id.frm_btn_subscribe:
//                goToHomeActivity(true);
                break;
            default:
                break;
        }
    }
    public void goToHomeActivity(boolean p_bNeedSubscribe) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(HomeActivity.PARAM_NEED_SUBSCRIBE, p_bNeedSubscribe);
        startActivity(intent);
        this.overridePendingTransition(R.anim.right_in, R.anim.left_out);
        this.finish();
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
        faceMedium = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Medium.ttf");

        sanfacebold = Typeface.createFromAsset(this.getAssets(),
                "fonts/SanFranciscoDisplay-Bold.otf");
        sanfaceRegular = Typeface.createFromAsset(this.getAssets(),
                "fonts/SanFranciscoDisplay-Regular.otf");
        sanfaceMedium = Typeface.createFromAsset(this.getAssets(),
                "fonts/SanFranciscoDisplay-Medium.otf");
        sanfacesemibold = Typeface.createFromAsset(this.getAssets(),
                "fonts/SanFranciscoDisplay-Semibold.otf");
        proximasemi= Typeface.createFromAsset(this.getAssets(),
                "fonts/Proxima Nova Semibold.otf");

    }

}


