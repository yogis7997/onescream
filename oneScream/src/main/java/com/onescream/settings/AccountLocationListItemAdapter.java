package com.onescream.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onescream.R;
import com.onescream.library.SuggestAddressesMgr;

public class AccountLocationListItemAdapter extends BaseAdapter {

	private final SuggestAddressesMgr mLocationManager;
	Context mContext;

	public AccountLocationListItemAdapter(Context c) {
		mLocationManager = SuggestAddressesMgr.sharedInstance();
		mContext = c;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mLocationManager.getSuggestionListSize();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		LayoutInflater m_layoutInflater = LayoutInflater.from(mContext);
		if (convertView == null) {
			convertView = m_layoutInflater.inflate(
					R.layout.list_item_search_location, parent, false);
			holder = new ViewHolder();
			holder.m_lblAddress = (TextView) convertView
					.findViewById(R.id.tv_text);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.m_lblAddress.setText(mLocationManager.getAddressSuggestionByIndex(position));

		return convertView;
	}

	static class ViewHolder {
		TextView m_lblAddress;
	}
}
