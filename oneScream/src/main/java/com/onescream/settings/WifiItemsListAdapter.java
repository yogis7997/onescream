package com.onescream.settings;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.onescream.R;
import com.onescream.info.WiFiItemInfo;
import com.uc.prjcmn.PRJFUNC;

/**
 * Adapter class for showing registered WiFi items
 *
 * Created by Anwar Almojarkesh
 *
 */

public class WifiItemsListAdapter extends ArrayAdapter<WiFiItemInfo> {
	private final String TAG = "WifiItemsListAdapter";
	Context m_context;
	private int ITEM_LAYOUT = -1;

	private boolean m_bDeleteButtonVisible = true;

	public interface Callback {
		public void onItemClick(int p_nPosition);

		public void onItemDelete(int p_nPosition);
	}

	private Callback m_callback = null;

	public void setCallback(Callback p_callback) {
		m_callback = p_callback;
	}

	public void setDeleteButtonVisible(boolean p_bDeleteButtonVisible) {
		m_bDeleteButtonVisible = p_bDeleteButtonVisible;
	}

	public WifiItemsListAdapter(Context context, int p_res,
			ArrayList<WiFiItemInfo> p_items) {
		super(context, p_res, p_items);

		m_context = context;
		ITEM_LAYOUT = p_res;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = convertView;
		final ViewHolder holder;
		final int _position = position;

		// set view
		if (item == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			item = vi.inflate(ITEM_LAYOUT, null);
			holder = new ViewHolder(item);
			item.setTag(holder);
		} else {
			holder = (ViewHolder) item.getTag();
		}
		item.setId(position);

		// -----------------
		try {
			WiFiItemInfo value = getItem(position);
			if (value != null) {

				if (PRJFUNC.isNullOrEmpty(value.m_szWiFiID)) {
					holder.m_tvTitle.setText(value.m_szTitle);
				} else {
					holder.m_tvTitle.setText(value.m_szTitle + " (Connected)");
				}
				holder.m_tvAddress.setText(value.m_szAddress);
			}

			holder.m_ivSelect.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (m_callback != null) {
						m_callback.onItemClick(_position);
					}
				}
			});

			if (m_bDeleteButtonVisible) {
				holder.m_ivDelete.setVisibility(View.VISIBLE);
				holder.m_ivDelete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (m_callback != null) {
							m_callback.onItemDelete(_position);
						}
					}
				});
			} else {
				holder.m_ivDelete.setVisibility(View.GONE);
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

		public ImageView m_ivDelete;

		public TextView m_tvTitle;
		public TextView m_tvAddress;

		public ImageView m_ivSelect;

		public ViewHolder(View V) {
			m_ivDelete = (ImageView) V.findViewById(R.id.iv_delete);

			m_tvTitle = (TextView) V.findViewById(R.id.tv_title);
			m_tvAddress = (TextView) V.findViewById(R.id.tv_address);

			m_ivSelect = (ImageView) V.findViewById(R.id.iv_select);
		}
	}
}