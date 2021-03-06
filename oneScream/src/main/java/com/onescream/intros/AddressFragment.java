package com.onescream.intros;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onescream.R;
import com.onescream.Utils.Utility;
import com.onescream.settings.AddAdressActivity;
import com.onescream.settings.WiFiItemsActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class AddressFragment extends Activity implements View.OnClickListener {
    Typeface facethin,facebold,faceRegular,EstiloRegular,faceMedium;
    Typeface sanfacebold,sanfaceRegular, sanfacesemibold
            ,sanfaceMedium, proximasemi;

    boolean m_bFromSignup=false;
    public static final String pref_name="onescreamshared";
    TextView   home_address3,
            work_address1,
            work_address2,
            work_address3,
            frequent_address1,
            frequent_address2,
            frequent_address3 , home_address1,

     home_address2;

    TextView work_addres,home_addres,
            frequent_addres;
    LinearLayout layout_home,
            layout_work,
            layout_frequent;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_one);
        utility = new Utility(this);
        utility.RegisterScreen(this,getResources().getString(R.string.frequesnted_address));
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            boolean value = extras.getBoolean(WiFiItemsActivity.PARAM_IS_FROM_SIGNUP,false);
        }
        sharedpreferences = getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        updateLCD();
        loadFrequentAddresses();
    }
    private void updateLCD() {
        initTypeFace();
        findViewById(R.id.fm_btn_home).setOnClickListener(this);
        findViewById(R.id.fm_btn_work).setOnClickListener(this);
        findViewById(R.id.fm_btn_frequent).setOnClickListener(this);

        TextView title_txt=(TextView)findViewById(R.id.title);
        TextView text1=(TextView)findViewById(R.id.txt1);
        title_txt.setTypeface(sanfaceRegular);
        text1.setTypeface(sanfacesemibold);

        home_address1=(TextView)findViewById(R.id.a_adresa);
        home_address2=(TextView)findViewById(R.id.a_adresb);
        home_address3=(TextView)findViewById(R.id.a_adresc);
        work_address1=(TextView)findViewById(R.id.b_adresa);
        work_address2=(TextView)findViewById(R.id.b_adresb);
        work_address3=(TextView)findViewById(R.id.b_adresc);
        frequent_address1=(TextView)findViewById(R.id.c_adresa);
        frequent_address2=(TextView)findViewById(R.id.c_adresb);
        frequent_address3=(TextView)findViewById(R.id.c_adresc);
        home_address1.setTypeface(sanfaceRegular);
        home_address2.setTypeface(sanfaceRegular);
        home_address3.setTypeface(sanfaceRegular);
        work_address1.setTypeface(sanfaceRegular);
        work_address2.setTypeface(sanfaceRegular);
        work_address3.setTypeface(sanfaceRegular);
        frequent_address1.setTypeface(sanfaceRegular);
        frequent_address2.setTypeface(sanfaceRegular);
        frequent_address3.setTypeface(sanfaceRegular);


        layout_home=(LinearLayout)findViewById(R.id.layout_txt);
        layout_work=(LinearLayout)findViewById(R.id.layout_txt1);
        layout_frequent=(LinearLayout)findViewById(R.id.layout_txt2);
        layout_home.setVisibility(View.GONE);
        layout_work.setVisibility(View.GONE);
        layout_frequent.setVisibility(View.GONE);

        Button home_edit=(Button)findViewById(R.id.a_edit);
        Button home_delete=(Button)findViewById(R.id.a_dlt);
        Button work_edit=(Button)findViewById(R.id.b_edit);
        Button work_delete=(Button)findViewById(R.id.b_dlt);
        Button frequent_edit=(Button)findViewById(R.id.c_edit);
        Button frequent_delete=(Button)findViewById(R.id.c_dlt);
        home_edit.setTypeface(sanfacebold);
        home_delete.setTypeface(sanfacebold);
        work_edit.setTypeface(sanfacebold);
        work_delete.setTypeface(sanfacebold);
        frequent_edit.setTypeface(sanfacebold);
        frequent_delete.setTypeface(sanfacebold);

        home_edit.setOnClickListener(this);
        work_edit.setOnClickListener(this);
        frequent_edit.setOnClickListener(this);
        work_delete.setOnClickListener(this);
        home_delete.setOnClickListener(this);
        frequent_delete.setOnClickListener(this);


        home_addres=(TextView)findViewById(R.id.add_home);
        work_addres=(TextView)findViewById(R.id.add_work);
        frequent_addres=(TextView)findViewById(R.id.add_frequent);
        home_addres.setTypeface(sanfacesemibold);
        work_addres.setTypeface(sanfacesemibold);
        frequent_addres.setTypeface(sanfacesemibold);


        TextView adresA=(TextView)findViewById(R.id.a_adre);
        TextView adresb=(TextView)findViewById(R.id.b_adre);
        TextView adresc=(TextView)findViewById(R.id.c_adre);
        adresA.setTypeface(sanfaceMedium);
        adresb.setTypeface(sanfaceMedium);
        adresc.setTypeface(sanfaceMedium);

        TextView tv_save=(TextView)findViewById(R.id.tv_btn_save);
        tv_save.setTypeface(facebold);
        findViewById(R.id.frm_btn_save).setOnClickListener(this);

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
        proximasemi= Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/Proxima Nova Semibold.otf");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.frm_btn_save:
                gotoIntro();
                break;
            case R.id.a_edit:
                gotoAddress("Home Address","Save Home Address","1",1);
                break;
            case R.id.b_edit:
                gotoAddress("Work Address","Save Work Address","2",1);
                break;
            case R.id.c_edit:
                gotoAddress("Other Address","Save Other Address","3",1);
                break;
            case R.id.a_dlt:
                editor.putString("home","");
                editor.commit();
                layout_home.setVisibility(View.GONE);
                home_addres.setVisibility(View.VISIBLE);
                deletedata(1);
                break;
            case R.id.b_dlt:
                editor.putString("work","");
                editor.commit();
                layout_work.setVisibility(View.GONE);
                work_addres.setVisibility(View.VISIBLE);
                deletedata(2);
                break;
            case R.id.c_dlt:
                editor.putString("freq","");
                editor.commit();
                layout_frequent.setVisibility(View.GONE);
                frequent_addres.setVisibility(View.VISIBLE);
                deletedata(3);
                break;
            case R.id.fm_btn_home:
                gotoAddress("Home Address","Save Home Address", "1",0);
                break;
            case R.id.fm_btn_work:
                gotoAddress("Work Address","Save Work Address", "2",0);
                break;
            case R.id.fm_btn_frequent:
                gotoAddress("Other Address","Save Other Address", "3",0);
                break;
            default:
                break;
        }
    }


    private void gotoAddress(String home, String s, String s1, int i) {
        Intent intent = new Intent(AddressFragment.this, AddAdressActivity.class);
        intent.putExtra(FirstScreenActivity.PARAM_ONLY_FIRST_SCREEN,true );
        intent.putExtra("btn_txt",s);
        intent.putExtra("title_txt",home);
        intent.putExtra("type",s1);
        intent.putExtra("edit",i);

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }



    private void gotoIntro() {
        Intent intent = new Intent(AddressFragment.this, TourLastActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    private void loadFrequentAddresses()
    {
        SharedPreferences sharedpreferences = getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String home_str= sharedpreferences.getString("home", "");
        String work_str= sharedpreferences.getString("work", "");
        String frqk_str= sharedpreferences.getString("freq", "");
//        Set<String> setHome = sharedpreferences.getStringSet("home", null);
//        Set<String> setWork = sharedpreferences.getStringSet("work", null);
//        Set<String> setFrequent = sharedpreferences.getStringSet("frequent", null);

        List<String> list_frequent = null,list_home = null,list_work = null;
        String[] homelists = home_str.split(",,,");
        String[] worklists = work_str.split(",,,");
        String[]frqlists = frqk_str.split(",,,");
        list_home=new ArrayList<String>();
        list_work = new ArrayList<String>();
        list_frequent = new ArrayList<String>();
        for(int i=0;i<homelists.length;i++){
            list_home.add(homelists[i]);

        }
        for(int i=0;i<worklists.length;i++){
            list_work.add(worklists[i]);

        }
        for(int i=0;i<frqlists.length;i++){
            list_frequent.add(frqlists[i]);

        }

        setLcdValue(list_home,list_work,list_frequent);


    }

    private void setLcdValue(List<String> list_home, List<String> list_work, List<String> list_frequent) {
        if(list_home !=null){
            if(list_home.size()>1){
                setHomeValues(list_home);
                layout_home.setVisibility(View.VISIBLE);
                home_addres.setVisibility(View.GONE);
               findViewById(R.id.frm_btn_save).setVisibility(View.VISIBLE);
            }
            else {
                layout_home.setVisibility(View.GONE);
                home_addres.setVisibility(View.VISIBLE);
            }
        }
        if(list_work !=null){
            if(list_work.size()>1){
                setWorkValues(list_work);
                layout_work.setVisibility(View.VISIBLE);
                work_addres.setVisibility(View.GONE);
            }
            else {
                layout_work.setVisibility(View.GONE);
                work_addres.setVisibility(View.VISIBLE);
            }
        }
        if(list_frequent !=null) {
            if (list_frequent.size() >1) {
                setFrqValues(list_frequent);
                layout_frequent.setVisibility(View.VISIBLE);
                frequent_addres.setVisibility(View.GONE);
            } else {
                layout_frequent.setVisibility(View.GONE);
                frequent_addres.setVisibility(View.VISIBLE);

            }
        }
    }

    private void setFrqValues(List<String> list_frequent) {
        int size=list_frequent.size();
        String txt1="";
        String txt2="";
        String txt3="";
        String txt4="";
        String txt5="";
        String txt6="";
        switch (size){
            case 1:
                txt1=list_frequent.get(0);
                break;
            case 2:
                txt1=list_frequent.get(0);
                txt2=list_frequent.get(1);
                break;
            case 3:
                txt1=list_frequent.get(0);
                txt2=list_frequent.get(1);
                txt3=list_frequent.get(2);
                break;
            case 4:
                txt1=list_frequent.get(0);
                txt2=list_frequent.get(1);
                txt3=list_frequent.get(2);
                txt4=list_frequent.get(3);
                break;
            case 5:
                txt1=list_frequent.get(0);
                txt2=list_frequent.get(1);
                txt3=list_frequent.get(2);
                txt4=list_frequent.get(3);
                txt5=list_frequent.get(4);
                break;
            case 6:
                txt1=list_frequent.get(0);
                txt2=list_frequent.get(1);
                txt3=list_frequent.get(2);
                txt4=list_frequent.get(3);
                txt5=list_frequent.get(4);
                txt6=list_frequent.get(5);
                break;
            default:
                break;
        }
        if(txt1.isEmpty()){
            frequent_address1.setVisibility(View.GONE);
        }
        else{
            frequent_address1.setText(txt1);
        }
        if(txt3.isEmpty() && txt2.isEmpty()){
            frequent_address2.setVisibility(View.GONE);
        }
        else{
            frequent_address2.setText(txt2+","+txt3);
        }
        if(txt5.isEmpty() && txt6.isEmpty() && txt4.isEmpty() ){
            frequent_address3.setVisibility(View.GONE);
        }
        else{
            frequent_address3.setText(txt4+", "+txt5+", "+txt6);
        }
    }

    private void setWorkValues(List<String> list_work) {
        int size=list_work.size();
        String txt1="";
        String txt2="";
        String txt3="";
        String txt4="";
        String txt5="";
        String txt6="";
        switch (size){
            case 1:
                txt1=list_work.get(0);
                break;
            case 2:
                txt1=list_work.get(0);
                txt2=list_work.get(1);
                break;
            case 3:
                txt1=list_work.get(0);
                txt2=list_work.get(1);
                txt3=list_work.get(2);
                break;
            case 4:
                txt1=list_work.get(0);
                txt2=list_work.get(1);
                txt3=list_work.get(2);
                txt4=list_work.get(3);
                break;
            case 5:
                txt1=list_work.get(0);
                txt2=list_work.get(1);
                txt3=list_work.get(2);
                txt4=list_work.get(3);
                txt5=list_work.get(4);
                break;
            case 6:
                txt1=list_work.get(0);
                txt2=list_work.get(1);
                txt3=list_work.get(2);
                txt4=list_work.get(3);
                txt5=list_work.get(4);
                txt6=list_work.get(5);
                break;
            default:
                break;
        }
        if(txt2.isEmpty()){
            work_address1.setVisibility(View.GONE);
        }
        else{
            work_address1.setText(txt2);
        }
        if(txt3.isEmpty() ){
            work_address2.setVisibility(View.GONE);
        }
        else{
            work_address2.setText(txt3);
        }
        if(txt4.isEmpty() && txt5.isEmpty() && txt6.isEmpty()){
            work_address3.setVisibility(View.GONE);
        }
        else{
            work_address3.setText(txt4+", "+txt5+", "+txt6);
        }
    }

    private void setHomeValues(List<String> list_home) {
        int size=list_home.size();
        String txt1="";
        String txt2="";
        String txt3="";
        String txt4="";
        String txt5="";
        String txt6="";
        switch (size){
            case 1:
                txt1=list_home.get(0);
                break;
            case 2:
                txt1=list_home.get(0);
                txt2=list_home.get(1);
                break;
            case 3:
                txt1=list_home.get(0);
                txt2=list_home.get(1);
                txt3=list_home.get(2);
                break;
            case 4:
                txt1=list_home.get(0);
                txt2=list_home.get(1);
                txt3=list_home.get(2);
                txt4=list_home.get(3);
                break;
            case 5:
                txt1=list_home.get(0);
                txt2=list_home.get(1);
                txt3=list_home.get(2);
                txt4=list_home.get(3);
                txt5=list_home.get(4);
                break;
            case 6:
                txt1=list_home.get(0);
                txt2=list_home.get(1);
                txt3=list_home.get(2);
                txt4=list_home.get(3);
                txt5=list_home.get(4);
                txt6=list_home.get(5);
                break;
            default:
                break;
        }

        if(txt1.isEmpty()){
            home_address1.setVisibility(View.GONE);
        }
        else{
            home_address1.setText(txt1);
        }
        if(txt2.isEmpty()&& txt3.isEmpty()){
            home_address2.setVisibility(View.GONE);
        }
        else{
            home_address2.setText(txt2+","+txt3);
        }
        if(txt4.isEmpty() && txt5.isEmpty() && txt6.isEmpty()){
            home_address3.setVisibility(View.GONE);
        }
        else{
            home_address3.setText(txt4+", "+txt5+", "+txt6);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        loadFrequentAddresses();
    }
    private void deletedata(int i) {
        SharedPreferences sharedpreferences = getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String Id_object= sharedpreferences.getString("object"+i, "");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserAddress");
        query.whereEqualTo("objectId", Id_object);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> invites, ParseException e) {
                if (e == null) {
                    // iterate over all messages and delete them
                    for(ParseObject invite : invites)
                    {
                        invite.deleteInBackground();
                    }
                } else {
                    //Handle condition here
                }
            }
        });
    }

}
