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
 * Activity class for Terms and Conditions Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class TermsActivity extends Activity implements View.OnClickListener {
	 
     
    private final String TAG = "TermsActivity";

    public static String PARAM_IN_SIGNING = "is_in_signing";

    private Context mContext;

   // private View m_frmHeader;

    private TextView m_tvSubTitle;
    private TextView m_tvContents;

    private View m_frmBack;

    private boolean m_bInSigning;

    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toc);
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

     //   m_frmHeader = findViewById(R.id.frm_header);

        findViewById(R.id.iv_back).setOnClickListener(this);
        Typeface sanfacebold = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Bold.otf");
        TextView titl = (TextView) findViewById(R.id.titl);
        titl.setTypeface(sanfacebold);
        m_tvSubTitle = (TextView) findViewById(R.id.tv_sub_title);
//        m_tvContents = (TextView) findViewById(R.id.tv_contents);
//        if(termStr!=null) {
//            m_tvContents.setText(termStr);
//        }
      //  m_frmBack = findViewById(R.id.frm_btn_back);
       // m_frmBack.setOnClickListener(this);

        if (m_bInSigning) {
          //  m_frmHeader.setVisibility(View.GONE);
           // m_frmBack.setVisibility(View.VISIBLE);
        } else {
           // m_frmHeader.setVisibility(View.VISIBLE);
           // m_frmBack.setVisibility(View.GONE);
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
    
	 String termStr = "1. INTRODUCTION: Welcome to our One Scream mobile application. This Application is published by or on behalf of One Scream Ltd.\n\n"

	 + "By downloading or otherwise accessing the App you agree to be bound by the following terms and conditions. If you have any queries about the App or these Terms, you can contact us by any of the means set out in paragraph 10 of these Terms. If you do not agree with these Terms, you should stop using the App immediately.\n\n"
 
 
   + "B2. GENERAL RULES RELATING TO CONDUCT: The App is made available for your own, personal use. The App must not be used for any commercial purpose whatsoever or for any illegal or unauthorised purpose. When you use the App you must comply with all applicable UK laws and with any applicable international laws, including the local laws in your country of residence (together referred to as “Applicable Laws”).\n"
 
 
   + "You agree that when using the App you will comply with all Applicable Laws and these Terms. In particular, but without limitation, you agree not to:\n\n"
 
 
   + " (a) Use the App in any unlawful manner or in a manner which promotes or encourages illegal activity including (without limitation) copyright infringement or\n"
 
 
   + " (b) Attempt to gain unauthorized access to the App or any networks, servers or computer systems connected to the App or\n"
 
 
   + " (c) Modify, adapt, translate or reverse engineer any part of the App or re-format or frame any portion of the pages comprising the App, save to the extent expressly permitted by these Terms or by law.\n\n"
 
 
   + "3. CONTENT: The copyright in all material contained on, in, or available through the App including all information, data, text, music, sound, photographs, graphics and video messages, the selection and arrangement thereof, and all source code, software compilations and other material is owned by or licensed to One Scream Ltd.or its group companies. All rights are reserved. You can view, print or download extracts of the Material for your own personal use but you cannot otherwise copy, edit, vary, reproduce, publish, display, distribute, store, transmit, commercially exploit, disseminate in any form whatsoever or use the Material without One Scream Ltd.express permission.\n"
 
 
   + "The trademarks, service marks, and logos contained on or in the App are owned by One Scream Ltd. or its group companies or third party partners of One Scream Ltd. You cannot use, copy, edit, vary, reproduce, publish, display, distribute, store, transmit, commercially exploit or disseminate the Trade Marks without the prior written consent of One Scream Ltd. or the relevant group company or the relevant third party partner of One Scream Ltd..\n\n"
 
 + "4. LINK TO THIRD PARTIES: The App may contain links to websites operated by third parties. One Scream Ltd. may monetise some of these links through the use of third party affiliate programmes. Notwithstanding such affiliate programmes, One Scream Ltd. does not have any influence or control over any such Third Party Websites and, unless otherwise stated, is not responsible for and does not endorse any Third Party Websites or their availability or contents.\n\n"
 
 
   + "5. ONE SCREAM LTD. PRIVACY POLICY: We take your privacy very seriously. One Scream Ltd.  will only use your personal information in accordance with the terms of our policies. By using the App you acknowledge and agree that you have read and accept the terms.\n\n"
 
 
   + "6. DISCLAIMER / LIABILITY: USE OF THE APP IS AT YOUR OWN RISK.\n One Scream Ltd. will not be liable, in contract, tort (including, without limitation, negligence), under statute or otherwise, as a result of or in connection with the App, for any: (i) economic loss (including, without limitation, loss of revenues, profits, contracts, business or anticipated savings) or (ii) loss of goodwill or reputation or (iii) special or indirect or consequential loss. One Scream Ltd. Will not be liable for any technology fails as the technology can and will fail. It is the responsibility of the user to always be within quick reach of other means within the area.\n\n"
 
 
   + "7. SERVICE SUSPENSION: One Scream Ltd. reserves the right to suspend or cease providing any services relating to the apps published by it, with or without notice, and shall have no liability or responsibility to you in any manner whatsoever if it chooses to do so.\n\n"
 
 
   + "8. ADVERTISERS IN THE APP: We accept no responsibility for adverts contained within the App. If you agree to purchase goods and/or services from any third party who advertises in the App, you do so at your own risk. The advertiser, not One Scream Ltd. is responsible for such goods and/or services and if you have any queries or complaints in relation to them, your only recourse is against the advertiser.\n\n"
 
+ "9. COMPETITIONS: If you take part in any competition which is run in or through the App, you agree to be bound by the rules of that competition and any other rules specified by One Scream Ltd. from time to time and by the decisions of One Scream Ltd. which are final in all matters relating to the Competition. One Scream Ltd. reserves the right to disqualify any entrant and/or winner in its absolute discretion without notice in accordance with the Competition Rules.\n\n"
 
 
   + "10. GENERAL: These Terms (as amended from time to time) constitute the entire agreement between you and One Scream Ltd.  concerning your use of the App.\n\n"
 
 
   + "One Scream Ltd.  reserves the right to update these Terms from time to time. If it does so, the updated version will be effective immediately, and the current Terms are available through a link in the App to this page.  You are responsible for regularly reviewing these Terms so that you are aware of any changes to them and you will be bound by the new policy upon your continued use of the App.  No other variation to these Terms shall be effective unless in writing and signed by an authorized representative on behalf of One Scream Ltd.’\n\n"
 
 
   + "These Terms shall be governed by and construed in accordance with English law and you agree to submit to the exclusive jurisdiction of the English Courts.\n\n"
 
 
   + "If any provision(s) of these Terms is held by a court of competent jurisdiction to be invalid or unenforceable, then such provision(s) shall be construed, as nearly as possible, to reflect the intentions of the parties (as reflected in the provision(s)) and all other provisions shall remain in full force and effect.\n\n"
 
 
   + "One Scream Ltd. failure to exercise or enforce any right or provision of these Terms shall not constitute a waiver of such right or provision unless acknowledged and agreed to by One Scream Ltd. in writing.\n\n"
 
 
   + "Unless otherwise expressly stated, nothing in the Terms shall create any rights or any other benefits whether pursuant to the Contracts (Rights of Third Parties) Act 1999 or otherwise in favor of any person other than you, One Scream Ltd. and its group of companies.\n\n";
}
