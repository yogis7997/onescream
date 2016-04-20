package com.onescream.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;

import java.util.ArrayList;

import com.onescream.MainApplication;
import com.onescream.R;
import com.onescream.engine.UniversalScreamEngine;
import com.onescream.intros.FirstScreenActivity;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Activity class for Settings Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class SettingsActivity extends Activity implements View.OnClickListener {

    private final String TAG = "SettingsActivity";

    private Context mContext;

    private ListView m_lvMenus;
    private SettingsMenuAdapter m_adapterMenus;

    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActivityTask.INSTANCE.add(this);

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
        findViewById(R.id.iv_back).setOnClickListener(this);

        m_lvMenus = (ListView) findViewById(R.id.lv_menus);
        m_adapterMenus = new SettingsMenuAdapter(mContext, R.layout.list_item_settings_menu, new ArrayList<MenuItemInfo>());
        m_lvMenus.setAdapter(m_adapterMenus);

        refreshMenus();
    }

    public void refreshMenus() {
        int[] support_menus = {
                R.string.settings_submenu_faq,
                R.string.settings_submenu_contactus,
                R.string.settings_submenu_your_details,
                R.string.settings_submenu_logout};

        int[] aboutus_menus = {
                R.string.settings_submenu_tour,
                R.string.settings_submenu_founders,
                R.string.settings_submenu_toc,
                R.string.settings_submenu_privacy,
                R.string.settings_submenu_howitworks};

        MenuItemInfo menuItem = null;

        // SUPPORT Menus
        m_adapterMenus.add(new MenuItemInfo(true, getString(R.string.settings_header_support)));
        for (int i = 0; i < support_menus.length; i++) {
            m_adapterMenus.add(new MenuItemInfo(false, getString(support_menus[i])));
        }

        // ABOUT US Menus
        m_adapterMenus.add(new MenuItemInfo(true, getString(R.string.settings_header_aboutus)));
        for (int i = 0; i < aboutus_menus.length; i++) {
            m_adapterMenus.add(new MenuItemInfo(false, getString(aboutus_menus[i])));
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
        switch (p_nPosition) {
            case 1:
                // FAQ
                goToFaqsActivity();
                break;
            case 2:
                // Contact us
                goToContactUsActivity();
                break;
            case 3:
                // Your details
                goToYourDetailsActivity();
                break;
            case 4:
                // Log out
                logout();
                break;
            case 6:
                // One Scream tour
                goToFirstScreenActivity(false, true);
                break;
            case 7:
                // Founders
                goToFoundersActivity();
                break;
            case 8:
                // Terms and Conditions
                goToTermsActivity();
                break;
            case 9:
                // Privacy Policy
                goToPrivacyActivity();
                break;
            case 10:
                // How it works
                goToHowitworksActivity();
                break;
        }
    }

    public void goBack() {
        finish();
        overridePendingTransition(R.anim.hold, R.anim.right_out);
    }

    /**
     * Faqs
     */
    public void goToFaqsActivity() {
        Intent intent = new Intent(SettingsActivity.this, FaqsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * Contact us
     */
    public void goToContactUsActivity() {
        Intent intent = new Intent(SettingsActivity.this, ContactUsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * Your details
     */
    public void goToYourDetailsActivity() {
        Intent intent = new Intent(SettingsActivity.this, YourDetailsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * Log out
     */
    private void logout() {
        PRJFUNC.showAlertDialog(SettingsActivity.this,
                "Do you really log out?", "Yes", "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser user = ParseUser.getCurrentUser();
                        ParsePush.unsubscribeInBackground(PRJFUNC
                                .convertEmailToChannelStr(user.getEmail()));

                        ParseUser.logOutInBackground(new LogOutCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    PRJFUNC.showAlertDialog(SettingsActivity.this, e.getMessage());
                                    return;
                                }

                                turnOffDetecting();

                                goToFirstScreenActivity(true, false);
                            }
                        });
                    }
                }, null);
    }


    /**
     * Founders
     */
    public void goToFoundersActivity() {
        Intent intent = new Intent(SettingsActivity.this, FoundersActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * Terms and Conditions
     */
    public void goToTermsActivity() {
        Intent intent = new Intent(SettingsActivity.this, TermsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * Privacy Policy
     */
    public void goToPrivacyActivity() {
        Intent intent = new Intent(SettingsActivity.this, PrivacyPolicyActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * How it works
     */
    public void goToHowitworksActivity() {
        Intent intent = new Intent(SettingsActivity.this, HowitworksActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * To the first screen activity
     */
    public void goToFirstScreenActivity(boolean p_bOnlyFirstScreen, boolean p_bInTour) {
        Intent intent = new Intent(SettingsActivity.this, FirstScreenActivity.class);
        intent.putExtra(FirstScreenActivity.PARAM_ONLY_FIRST_SCREEN, p_bOnlyFirstScreen);
        intent.putExtra(FirstScreenActivity.PARAM_IN_TOUR, p_bInTour);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
        if (p_bOnlyFirstScreen) {
            ActivityTask.INSTANCE.clear();
        }
    }

    private void turnOffDetecting() {

        GlobalValues.sharedInstance().m_bDetecting = false;

        if (GlobalValues.sharedInstance()._soundEngine != null) {
            UniversalScreamEngine soundEngine = GlobalValues.sharedInstance()._soundEngine;
            if (soundEngine != null) {
                GlobalValues.sharedInstance()._soundEngine = null;
                soundEngine.Terminate();
            }
        }

        SharedPreferencesMgr pPhoneDb = ((MainApplication) getApplication())
                .getSharedPreferencesMgrPoint();
        pPhoneDb.saveDetectionMode(GlobalValues.sharedInstance().m_bDetecting);

        if (GlobalValues.sharedInstance()._myService != null
                && GlobalValues.sharedInstance()._myService.m_handler != null) {

            GlobalValues.sharedInstance()._myService.m_handler
                    .sendEmptyMessage(GlobalValues.COMMAND_DISABLE_DETECT);
        }
    }

    // ////////////////////////////////////////////////////////////////////
    // // Adapter
    // ////////////////////////////////////////////////////////////////////
    public class MenuItemInfo {
        public MenuItemInfo(boolean p_bHeader, String p_strContent) {
            isHeader = p_bHeader;
            strContent = p_strContent;
        }

        public boolean isHeader;
        public String strContent;
    }

    public class SettingsMenuAdapter extends
            ArrayAdapter<MenuItemInfo> {
        private final String TAG = "SettingsMenuAdapter";
        private int ITEM_LAYOUT = -1;
        private Context mContext = null;

        public SettingsMenuAdapter(Context context, int p_res,
                                   ArrayList<MenuItemInfo> p_items) {
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
                final MenuItemInfo menuItem = getItem(position);

                if (menuItem.isHeader) {
                    holder.m_frmSubHeader.setVisibility(View.VISIBLE);
                    holder.m_frmSubMenu.setVisibility(View.GONE);
                    holder.m_tvSubHeader.setText(menuItem.strContent);
                } else {
                    holder.m_frmSubHeader.setVisibility(View.GONE);
                    holder.m_frmSubMenu.setVisibility(View.VISIBLE);
                    holder.m_tvSubMenu.setText(menuItem.strContent);
                    holder.m_frmSubMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onMenuItemPressed(nPosition);
                        }
                    });
                }
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

            public View m_frmSubHeader;
            public TextView m_tvSubHeader;
            public View m_frmSubMenu;
            public TextView m_tvSubMenu;

            public ViewHolder(View V) {
                m_frmSubHeader = V.findViewById(R.id.frm_sub_header);
                m_tvSubHeader = (TextView) V.findViewById(R.id.tv_sub_header);
                m_frmSubMenu = V.findViewById(R.id.frm_sub_menu);
                m_tvSubMenu = (TextView) V.findViewById(R.id.tv_sub_menu);
            }
        }
    }
}
