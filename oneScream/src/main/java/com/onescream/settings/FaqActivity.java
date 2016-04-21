package com.onescream.settings;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.onescream.R;

import com.onescream.Utils.Utility;
import com.uc.prjcmn.ActivityTask;

/**
 * Activity class for detailed Faq Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class FaqActivity extends Activity implements View.OnClickListener {

    private final String TAG = "FaqActivity";

    public static final String PARAM_TITLE = "title";
    public static final String PARAM_CONTENT = "content";

    private Context mContext;

    private View m_frmHeader;

    private TextView m_tvTitle;
    private TextView m_tvContents;

    private String m_strTitle;
    private String m_strContent;
 
	Typeface facethin,facebold,faceRegular,faceMedium;

    private ListView m_lvFaqs;
    private FaqsListAdapter m_adapterFaqs;

    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_faq);
        ActivityTask.INSTANCE.add(this);

        mContext = (Context) this;
        initTypeFace();
        m_strTitle = getIntent().getStringExtra(PARAM_TITLE);
        m_strContent = getIntent().getStringExtra(PARAM_CONTENT);

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
        Typeface  sanfacebold = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Bold.otf");
        m_tvTitle = (TextView) findViewById(R.id.tv_sub_title);
        m_tvTitle.setTypeface(sanfacebold);
       // m_tvContents = (TextView) findViewById(R.id.tv_contents);
        final ScrollView scroll= (ScrollView) findViewById(R.id.scroll);
        findViewById(R.id.iv_back).setOnClickListener(this);

        m_lvFaqs = (ListView) findViewById(R.id.lv_faqs);
        m_adapterFaqs = new FaqsListAdapter(mContext, R.layout.list_item_faq, new ArrayList<FaqItemInfo>());
        m_lvFaqs.setAdapter(m_adapterFaqs);
        scroll.smoothScrollTo(0, 0);
        refreshContents();
        refreshMenus();

    }

    private void refreshContents() {
        m_tvTitle.setText(m_strTitle);
     //   m_tvContents.setText(m_strContent);
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
            default:
                break;
        }
    }
    public void refreshMenus() {
    	String formatedstring="Please click on this link for simple instructions on how to change or update your payment method  Google’s support and pay section " +"<b>" +"https://support.google.com/googleplay/topic/3364264?hl=en-GB"+ "</b> " ;
    	        
    	String[][] strFaqs = stringFaqs(Integer.parseInt(m_strContent));
        // Faqs
        for (int i = 0; i < strFaqs.length; i++) {
            m_adapterFaqs.add(new FaqItemInfo(strFaqs[i][0], strFaqs[i][1]));
        }
    }
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
    public void onMenuItemPressed(int p_nPosition) {
        FaqItemInfo faq = m_adapterFaqs.getItem(p_nPosition);
        
        faq.m_bContentVisible = !faq.m_bContentVisible;
        
        m_adapterFaqs.notifyDataSetChanged();
        // goToFaqActivity(faq);
    }
    
   private  String[][] stringFaqs (int index)
   {
   	String formatedstring="Please click on this link for simple instructions on how to change or update your payment method  Google’s support and pay section " +"<b>" +"https://support.google.com/googleplay/topic/3364264?hl=en-GB"+ "</b> " ;

	   String[][] strFaqs0 =  {
   		{
   			"How can I change my account information?",
   			"To change your account information, go to the Account page and click on Your Details to amend."
   		},
   		{
   			"How can I manage my subscription?",
   			"You can change or cancel your subscription in the ACcount section of the app. Go to Your Details then click the Manage Subscription button.",
   		},
   		
   		{
   			"Can I cancel my yearly subscription before the end of the year?",
   			"You can click on Account, then Your Details to find the Manage Subscriptions button. You can cancel your monthly or yearly renewal within 24 hours of the next renewal date.",
   		},
   		//////////////////// end of 0
   	  		
   };
	   String[][] strFaqs1 = {
				{
		   			"How much does One Scream cost?",
		   			"One Scream costs 1.49 a month, or save almost 20% by signing up for a 14.49 yearly subscription.",
		   		},
		   		{
		   			"I am having trouble paying!",
		   			Html.fromHtml(formatedstring).toString()
		   		},
		   		{
		   			"Are there any hidden extra fees, for example when I have created an alert?",
		   			"There are no hidden fees, what you see is what you get. "
		   		},
		   		///////////////// end of 1
	   };
	   String[][] strFaqs2 = {
				{
		   			"How do I close or exit the One Scream app?",
		   			"To close or exit the One Scream app, click on the menu bar in the top left of the screen. Click Log Out."
		   		},
		   		{
		   			"My app is not working.",
		   			"You may need to change or update your payment method in Apple to reactivate the app. Go to the Account section of the app. In Your Details, click the Manage Subscription button. If you have been blocked for misusing the app, you will not be able to activate One Scream again."
		   		},
		   		////////////////////// end of 2
	   };
	   String[][] strFaqs3 = {
				{
		   			"Is One Scream listening to what I say?",
		   			"No! One scream does not hear spoken words, only sounds. We care about your privacy. We only ever listen for or record screams to become smarter, and our app learns with every user. "
		   		},
		   		{
		   			"Who can see my location?",
		   			"No one will check your location, including us. It will only be sent to Police if a scream is detected.  "
		   		},
		   		{
		   			"What do you do with my information?",
		   			"We only use the details you have provided in the event of an emergency, to provide Police with your identity and location."
		   		},
		   		/////////////////////// end of 3
	   };
	   
	   String[][] strFaqs4 = {
			   {
		   			"What is a panic scream?",
		   			"A panic scream is a woman’s natural response to danger. A scream is a universally recognised primal sound that humans make when in distress to call for help. All panic screams have very similar qualities."

		               
		    		},
		   		{
		   			"In my nightmares I cannot scream. What if I cannot scream in real life?",
		   			"Many people who cannot scream in nightmares or have never screamed in their lives will often still scream if they are in danger and need help. It is an innate response for females to scream to call for help. If you do not scream, the app will not be able to help you."
		   		},
		   		{
		   			"What happens if I scream at my phone when I am not in danger?",
		   			"Do not try to scream a panic scream for the app unless you are in distress. If you misuse the app Police can ask us to block you as a user; false alarms distract Police from doing their job, protecting those in real danger. "
		   		},
		   		{
		   			"Do I need to scream for the app?",
		   			"You should not scream unless you are in real distress. "
		   		},
		   		///////////////////// end of 4	   
	   };
	   String[][] strFaqs5 = {
			   {
		   			"I am worried my alarm will activate when it shouldn’t!",
		   			"We have carefully measured the qualities that define a true panic scream. Just as your own ear hears real distress, our app can also distinguish a true panic scream from other screams and sounds. However if the alarm does mistakenly activate, you have 12 seconds to cancel the alarm. A push notification with the option to cancel the alarm will appear on your phone."
		   		},
		   		{
		   			"What should I expect if the alarm is activated?",
		   			"The phone will vibrate, you will hear a loud siren and the lights of your phone will flash. You have 12 seconds to cancel the alarm before the police are notified."
		              +"How can I stop the alert if I am not able to cancel within the 12 seconds?" 
		              +"The alert has been sent to Police, it is crucial to stay on the line and confirm to Police this has been a false alarm before an emergency vehicle has been dispatched."
		   		},
		   		
		   		{"How can I stop the alert if I am not able to cancel within the 12 seconds?",
		   			"if an alert has been sent to Police, it is crucial to stay on the line and confirm your false alarm before an emergency vehicle has been dispatched."
		   		},
		   		{
		   			"What are Frequented Addresses?",
		   			"Frequented addresses are places you go often. Home, work or a relative’s home are examples of frequented addresses. We use the WiFi within your frequented addresses to confirm where you are as GPS is not always entirely accurate. Also if you are living in a flat, GPS would likely locate your building number, but knowing your flat number means Police know where in the building to find you. It is mandatory to connect to your home address and important to connect to the other WiFi in your frequented addresses."
		   		},
		   		{
		   			"How do I know One Scream is listening?",
		   			"If you are an Android user, you will see the app icon of One Scream at the top of the screen when the app is listening."
		   		},
		   		{
		   			"In which countries is One Scream available? ",
		   			"Currently One Scream is only available in the UK. We hope to be in the USA and other countries soon! "
		   		},
		   		{
		   			"When should I use One Scream?",
		   			"While it is up to your discretion, we hope you will leave One Scream running in the background at all times. There will be instances when you would always remember to turn One Scream on such as when walking home late or when on a run alone. We also want to be listening in the unlikely event of a sudden or unexpected attack. If One Scream is not listening, it can not help. "
		   		},
		///////////////////// end of 5   
	   };
	   
	   String[][] strFaqs6 = {
			   {
		   			"Do I need a phone plan? ",
		   			"You do not need a phone plan for One Scream to operate. One Scream will not be able to contact Police on your behalf if you do not have your phone topped up. "
		   		},
		   		

		   		{
		   			"Can I use One Scream on my tablet or computer?",
		   			"One Scream is only designed for mobile phones phones."
		   		},
		   		{
		   			"What happens if I have no signal/connection?",
		   			"One Scream is working within the phone, if you have no signal the app will detect your scream but will not have a way to communicate to Police."
		   		},
		   		{
		   			"Is One Scream running down my battery? ",
		   			"One Scream operates incredibly efficiently but if it runs in the background all the time, it will have an impact on battery life." 

		 
		 		},
		   		{"I want to switch One Scream off in places like the cinema or the theatre.",
		 			"Please feel free to use One Scream at your discretion. If you turn off your app at the cinema or theatre, remember to turn it back on after the show. We have designed the app to run in the background all the time because attacks are often sudden and unexpected."
		 			  
		   			
		   		},
		///////////////////// end of 6	   
	   };

	   switch (index) {
	case 0:
		return strFaqs0;
		 
case 1:
		
	return strFaqs1;
case 2:
	
	return strFaqs2;
case 3:
	
	return strFaqs3;
case 4:
	
	return strFaqs4;
case 5:
	
	return strFaqs5;
case 6:
	return strFaqs6;
	
	default:
		break;
	}
	   return null;
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
}
