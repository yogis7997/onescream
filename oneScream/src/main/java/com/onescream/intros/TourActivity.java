package com.onescream.intros;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.onescream.HomeActivity;
import com.onescream.R;
import com.onescream.Utils.Utility;
import com.onescream.login.LoginActivity;
import com.onescream.login.SignupActivity;
import com.parse.ParseUser;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Activity class for First Screen appeared when the user downloaded (including
 * swipe-able pages)
 * <p/>
 * Created by Anwar Almojarkesh
 */
public class TourActivity extends FragmentActivity implements
        View.OnClickListener {

    FirstScreensAdapter mAdapter;
    ViewPager mPager;
    View frmHeader;
    public static final String PARAM_ONLY_FIRST_SCREEN = "only_first";
    public static final String PARAM_IN_TOUR = "in_tour";
    private ImageView[] m_pageIndicators;
    public int m_nCurPage = -1;
    public int back_no = 0;
    public boolean m_bOnlyFirstScreen = false;
    public boolean m_bInTour = true;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstscreen);
        Utility.RegisterScreen(this, getResources().getString(R.string.tour));

        Bundle extras = getIntent().getExtras();
        boolean value = false;
        if (extras != null) {
            value = extras.getBoolean(FirstScreenActivity.PARAM_ONLY_FIRST_SCREEN, false);
        }
        if (value) {
            mAdapter = new FirstScreensAdapter(getSupportFragmentManager(),
                    true, m_bInTour, this);
        } else {
            mAdapter = new FirstScreensAdapter(getSupportFragmentManager(),
                    m_bOnlyFirstScreen, m_bInTour, this);
        }
        updateLCD();


    }

    private void updateLCD() {
        frmHeader = findViewById(R.id.frm_header);
        frmHeader.setVisibility(true ? View.VISIBLE : View.GONE);

        back = (ImageView) findViewById(R.id.iv_back);
        back.setOnClickListener(this);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                selectPageIndicator(position);
                if (mPager.getCurrentItem() == 3) {
                    back_no = 1;
                    back.setImageResource(R.drawable.menusmal);
                } else {
                    back_no = 0;
                    back.setImageResource(R.drawable.btn_back);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
        mPager.setAdapter(mAdapter);

        if (m_bOnlyFirstScreen) {
            findViewById(R.id.frm_indicator).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.frm_indicator).setVisibility(View.INVISIBLE);
        }

        int[] idPages = {R.id.iv_page1,
                R.id.iv_page2, R.id.iv_page3,
                R.id.iv_page4, R.id.iv_page4, R.id.iv_page5};

        m_pageIndicators = new ImageView[idPages.length];
        for (int i = 0; i < idPages.length; i++) {
            m_pageIndicators[i] = (ImageView) findViewById(idPages[i]);
        }

        selectPageIndicator(0);

    }

    private void selectPageIndicator(int p_nPage) {
        if (!m_bInTour && p_nPage != 0) {
            frmHeader.setVisibility(View.VISIBLE);
        } else if (m_bInTour) {
            frmHeader.setVisibility(View.VISIBLE);
        } else {
            frmHeader.setVisibility(View.GONE);
        }
        if (m_nCurPage >= 0)
            m_pageIndicators[m_nCurPage].setSelected(false);

        m_nCurPage = p_nPage;

        m_pageIndicators[m_nCurPage].setSelected(true);
    }


    public void goBack() {
        Intent intent = new Intent(TourActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                int pageIndex = mPager.getCurrentItem();

                if (pageIndex != 0) {
                    if (back_no == 1) {
                        goBack();
                    } else {
                        mPager.setCurrentItem(pageIndex - 1);
                        selectPageIndicator(pageIndex - 1);
                    }


                } else {
                    goBack();
                }
                break;
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
    public void onBackPressed() {
        Intent intent = new Intent(TourActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}