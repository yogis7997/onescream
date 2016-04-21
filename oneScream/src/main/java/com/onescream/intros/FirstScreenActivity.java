package com.onescream.intros;

import android.content.Intent;
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
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Activity class for First Screen appeared when the user downloaded (including
 * swipe-able pages)
 * <p>
 * Created by Anwar Almojarkesh
 */
public class FirstScreenActivity extends FragmentActivity implements
        View.OnClickListener, Page1Fragment.FragmentListener {

    FirstScreensAdapter mAdapter;
    ViewPager mPager;
    View frmHeader;
    public static final String PARAM_ONLY_FIRST_SCREEN = "only_first";
    public static final String PARAM_IN_TOUR = "in_tour";

    private ImageView[] m_pageIndicators;

    public int m_nCurPage = -1;

    public boolean m_bOnlyFirstScreen = false;
    public boolean m_bInTour = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Utility.RegisterScreen(this, getResources().getString(R.string.signing_in));


        ActivityTask.INSTANCE.add(this);
        final ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            setContentView(R.layout.activity_firstscreen);


//		SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(this);
//		m_bOnlyFirstScreen=phoneDb.isLoggedInOnce();
//		m_bOnlyFirstScreen = getIntent().getBooleanExtra(
//				PARAM_ONLY_FIRST_SCREEN, false);
//		m_bInTour = getIntent().getBooleanExtra(PARAM_IN_TOUR, false);

            mAdapter = new FirstScreensAdapter(getSupportFragmentManager(),
                    m_bOnlyFirstScreen, m_bInTour, this);

            updateLCD();

        } else {
            Log.e("First ", " username== " + user.getUsername());
            Utility.RegisterScreen(this, getResources().getString(R.string.landing));
//            Analytics.with(FirstScreenActivity.this).identify("email:"+user.getEmail(), new Traits().putName("a user's name"), null);
//            Analytics.with(FirstScreenActivity.this).identify("",new Traits().putName(user.getUsername()).putEmail(user.getEmail()));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    user.fetchInBackground();
                    Intent intent = new Intent(FirstScreenActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 200);
        }


        Bundle bundle = getIntent().getExtras();

//Extract the data…
        if (bundle != null) {
            boolean venName = bundle.getBoolean("activity", false);
            if (venName) {
                mPager.setCurrentItem(1);
            }
        }
    }

    private void updateLCD() {
        frmHeader = findViewById(R.id.frm_header);
        frmHeader.setVisibility(true ? View.VISIBLE : View.GONE);

        findViewById(R.id.iv_back).setOnClickListener(this);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                selectPageIndicator(position);

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

    public void goToSignupActivity(boolean p_bInTour) {
        Intent intent = new Intent(FirstScreenActivity.this,
                SignupActivity.class);
        intent.putExtra(SignupActivity.PARAM_IN_TOUR, p_bInTour);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(FirstScreenActivity.this,
                LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    public void goBack() {
        finish();
        overridePendingTransition(R.anim.hold, R.anim.right_out);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                int pageIndex = mPager.getCurrentItem();

                if (pageIndex != 0) {
                    mPager.setCurrentItem(pageIndex - 1);
                    selectPageIndicator(pageIndex - 1);

                } else {
                    goBack();
                }
                break;
        }
    }

    @Override
    public void clickListner(String Value) {
        mPager.setCurrentItem(1);
    }

    @Override
    protected void onDestroy() {
        releaseValues();

        ActivityTask.INSTANCE.remove(this);

        super.onDestroy();
    }

    private void releaseValues() {

    }
}