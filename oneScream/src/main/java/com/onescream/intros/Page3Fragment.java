package com.onescream.intros;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onescream.R;
import com.onescream.Utils.Utility;

/**
 * Fragment class for Page3 of First Screen
 *
 * Created by Anwar Almojarkesh
 */
public final class Page3Fragment extends Fragment implements OnClickListener {
	Typeface facethin,facebold,faceRegular,EstiloRegular,faceMedium;
	Typeface sanfacebold,sanfaceRegular, sanfaceMedium,sanfacesemibold,proximasemi;
	ImageView Indicator6,Indicator5;
	boolean getValue=false;
	private Utility utility;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		utility = new Utility(getActivity());
		utility.RegisterScreen(getActivity(), getActivity().getResources().getString(R.string.about_how));


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.fragment_page3, container,
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
		title.setTypeface(sanfaceRegular);
		Indicator6= (ImageView ) v.findViewById(R.id.iv_page6);
		Indicator5= (ImageView ) v.findViewById(R.id.iv_page5);
		TextView text = (TextView ) v.findViewById(R.id.intro);
		TextView texthead = (TextView ) v.findViewById(R.id.text);
		text.setTypeface(sanfacesemibold);
		texthead.setTypeface(proximasemi);
		if(getValue){
			Indicator6.setVisibility(View.GONE);
			Indicator5.setVisibility(View.GONE);
		}
//		TextView labelTitle = (TextView) v.findViewById(R.id.labelll); 
//		 labelTitle.setTypeface(EstiloRegular);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
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
