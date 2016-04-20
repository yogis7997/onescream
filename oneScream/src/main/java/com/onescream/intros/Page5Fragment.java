package com.onescream.intros;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onescream.HomeActivity;
import com.onescream.R;

/**
 * Fragment class for Page5 of First Screen
 *
 * Created by Anwar Almojarkesh
 */
public final class Page5Fragment extends Fragment implements OnClickListener {
	Typeface facethin,facebold,faceRegular,EstiloRegular,faceMedium;

	private TextView m_tvContents;
	ImageView Indicator6,Indicator5;
	boolean getValue=false;
	Typeface sanfacebold,sanfaceRegular, sanfaceMedium,sanfacesemibold,proximasemi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.fragment_page5, container,
				false);
		Bundle bundle=getArguments();
		if(bundle!=null) {
			getValue = getArguments().getBoolean("activity",false);
		}
		updateLCD(v);

		return v;
	}

	private void updateLCD(View v) {
		initTypeFace();
		TextView title = (TextView ) v.findViewById(R.id.title);
		TextView introtxt = (TextView ) v.findViewById(R.id.intro);
		TextView text1 = (TextView ) v.findViewById(R.id.text1);
		TextView text2 = (TextView ) v.findViewById(R.id.text2);


		//		 TextView labelTitle = (TextView) v.findViewById(R.id.labelll);
		//		 labelTitle.setTypeface(EstiloRegular);
		title.setTypeface(sanfaceRegular);
		introtxt.setTypeface(sanfacesemibold);
		text1.setTypeface(proximasemi);
		text2.setTypeface(proximasemi);
		// /> Free Trial
		v.findViewById(R.id.frm_btn_trial).setOnClickListener(this);

		// /> SubScribe
		v.findViewById(R.id.frm_btn_subscribe).setOnClickListener(this);
		Indicator6= (ImageView) v.findViewById(R.id.iv_page6);
		Indicator5= (ImageView ) v.findViewById(R.id.iv_page5);
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
//			goToHomeActivity(true);
			break;
		default:
			break;
		}
	}
	public void goToHomeActivity(boolean p_bNeedSubscribe) {
		Intent intent = new Intent(getActivity(), HomeActivity.class);
		intent.putExtra(HomeActivity.PARAM_NEED_SUBSCRIBE, p_bNeedSubscribe);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
		getActivity().finish();
	}
	private void initTypeFace()
	{
		  facethin = Typeface.createFromAsset(getActivity().getAssets(),
	            "fonts/Roboto-Thin.ttf");
		  facebold = Typeface.createFromAsset(getActivity().getAssets(),
	            "fonts/Roboto-Bold.ttf");
		  faceRegular = Typeface.createFromAsset(getActivity().getAssets(),
	            "fonts/Roboto-Regular.ttf");
		  EstiloRegular = Typeface.createFromAsset(getActivity().getAssets(),
		            "fonts/EstiloRegular.otf");
		faceMedium = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Roboto-Medium.ttf");

		sanfacebold = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/SanFranciscoDisplay-Bold.otf");
		sanfaceRegular = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/SanFranciscoDisplay-Regular.otf");
		sanfaceMedium = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/SanFranciscoDisplay-Medium.otf");
		sanfacesemibold = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/SanFranciscoDisplay-Semibold.otf");
		proximasemi= Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Proxima Nova Semibold.otf");

	}

}
