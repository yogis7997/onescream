package com.onescream.Utils;

import android.content.Context;
import android.util.Log;

import com.onescream.R;
import com.parse.ParseUser;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;

/**
 * Created by seraphicinfosolutions on 4/21/16.
 */
public class Utility {

    private static final String TAG ="Utility" ;
    public Context context;

    public Utility(Context context) {
        this.context = context;
    }

    public static void RegisterScreen(Context context, String string) {

        try{
            final ParseUser user = ParseUser.getCurrentUser();
            Analytics.with(context).identify("email:" + user.getEmail(), new Traits().putName(user.getUsername()).putEmail(user.getEmail()), null);

            Analytics.with(context).screen("Activity", string, new Properties().putValue(string, string));
//            Analytics.with(context).track(string, new Properties().putValue(string, string));
        }catch (Exception e){
            Log.e(TAG," error "+e.getMessage() );
        }


    }
}
