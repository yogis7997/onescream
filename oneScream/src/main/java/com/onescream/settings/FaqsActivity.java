package com.onescream.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import com.onescream.HomeActivity;
import com.onescream.R;
import com.onescream.Utils.Utility;
import com.uc.prjcmn.ActivityTask;

/**
 * Activity class for Faqs list Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class FaqsActivity extends Activity implements View.OnClickListener {

    private final String TAG = "FaqsActivity";

    private Context mContext;

    private ListView m_lvFaqs;
    private FaqsListAdapter m_adapterFaqs;
	Typeface facethin,facebold,faceRegular,faceMedium;
	
	TextView title_faq;
    private Utility utility;

    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility = new Utility(this);
        utility.RegisterScreen(this, getResources().getString(R.string.FAQ));
        setContentView(R.layout.activity_faqs);
        ActivityTask.INSTANCE.add(this);

        initTypeFace();
        mContext = (Context) this;

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
    	
    	final ScrollView scroll= (ScrollView) findViewById(R.id.scroll);
        findViewById(R.id.iv_back).setOnClickListener(this);
        title_faq= (TextView) findViewById(R.id.title_faq);
        title_faq.setTypeface(facebold);
        m_lvFaqs = (ListView) findViewById(R.id.lv_faqs);
        m_adapterFaqs = new FaqsListAdapter(mContext, R.layout.list_item_faq, new ArrayList<FaqItemInfo>());
        m_lvFaqs.setAdapter(m_adapterFaqs);
        refreshMenus();
        scroll.smoothScrollTo(0, 0);
    }

    public void refreshMenus() {
    	        
    	String[][] strFaqs = {
        		{
        			"Your Details",
        			"0"
        		},
        		{
        			"Payment",
        			"1",
        		},
        		{
        			"Troubleshooting",
        			"2",
        		},
        		{
        			"Your Privacy",
        			"3"
        		},
        		{
        			"Screaming",
        			"4"
        		},
        		{
        			"About the App",
        			"5"
        		},
        		///////
        		{
        			"One Scream on Your Device",
        			"6"
        			},
        		
        };

        // Faqs
        for (int i = 0; i < strFaqs.length; i++) {
            m_adapterFaqs.add(new FaqItemInfo(strFaqs[i][0], strFaqs[i][1]));
        }
    }

    // /////////////////////////////////////


    /**
     * OnClick Event method
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                goBack();
                break;

            default:
                break;
        }
    }

    public void onMenuItemPressed(int p_nPosition) {
        
    
    	
    	
    	FaqItemInfo faq = m_adapterFaqs.getItem(p_nPosition);
    	
    	goToFaqActivity(faq);
       // faq.m_bContentVisible = !faq.m_bContentVisible;
        
        //m_adapterFaqs.notifyDataSetChanged();
       
    }

    public void goBack() {
        Intent intent = new Intent(FaqsActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }

    /**
     * Contact us
     */
    public void goToFaqActivity(FaqItemInfo faq) {
        Intent intent = new Intent(FaqsActivity.this, FaqActivity.class);
        intent.putExtra(FaqActivity.PARAM_TITLE, faq.strTitle);
        intent.putExtra(FaqActivity.PARAM_CONTENT, faq.strContent);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }


    // ////////////////////////////////////////////////////////////////////
    // // Adapter
    // ////////////////////////////////////////////////////////////////////
    public class FaqItemInfo {
        public FaqItemInfo(String p_strTitle, String p_strContent) {
            strTitle = p_strTitle;
            strContent = p_strContent;
            
            m_bContentVisible = false;
        }

        public String strTitle;
        public String strContent;
        public boolean m_bContentVisible;
    }

    public class FaqsListAdapter extends
            ArrayAdapter<FaqItemInfo> {
        private final String TAG = "FaqsListAdapter";
        private int ITEM_LAYOUT = -1;
        private Context mContext = null;

        public FaqsListAdapter(Context context, int p_res,
                               ArrayList<FaqItemInfo> p_items) {
            super(context, p_res, p_items);

            mContext = context;

            ITEM_LAYOUT = p_res;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = convertView;
            final ViewHolder holder;
            final int nPosition = position;

            // set view
            if (item == null) {
                LayoutInflater vi = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                item = vi.inflate(ITEM_LAYOUT, null);
                holder = new ViewHolder(item);
                item.setTag(holder);
            } else {
                holder = (ViewHolder) item.getTag();
            }
            item.setId(position);

            // -----------------
            try {
                final FaqItemInfo menuItem = getItem(position);

                holder.m_tvFaqTitle.setText(menuItem.strTitle);
                holder.m_tvFaqContent.setText(menuItem.strContent);
                if (menuItem.m_bContentVisible) {
                	holder.m_tvFaqContent.setVisibility(View.VISIBLE);
                } else {
                	holder.m_tvFaqContent.setVisibility(View.GONE);
                }
                holder.m_frmFaq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onMenuItemPressed(nPosition);
                    }
                });
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            return item;
        }

        // =====================================================================================================

        /**
         * UI elements of List item.
         */
        private class ViewHolder {

            public View m_frmFaq;
            public TextView m_tvFaqTitle;
            public TextView m_tvFaqContent;

            public ViewHolder(View V) {
                m_frmFaq = V.findViewById(R.id.frm_faq);
                m_tvFaqTitle = (TextView) V.findViewById(R.id.tv_faq_title);
                
                m_tvFaqContent = (TextView) V.findViewById(R.id.tv_faq_content);
                m_tvFaqContent.setTypeface(faceRegular);
                m_tvFaqTitle.setTypeface(faceMedium);
            }
        }
    }
    
	private void initTypeFace()
	{
		  facethin = Typeface.createFromAsset(getAssets(),
	            "fonts/Roboto-Thin.ttf");
		  facebold = Typeface.createFromAsset(getAssets(),
	            "fonts/Roboto-Bold.ttf");
		  faceRegular = Typeface.createFromAsset(getAssets(),
	            "fonts/Roboto-Regular.ttf");
		  faceMedium = Typeface.createFromAsset(getAssets(),
		            "fonts/Roboto-Medium.ttf");
	}
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FaqsActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();
    }
}
