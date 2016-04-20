package com.onescream.library;

import org.json.JSONArray;
import org.json.JSONObject;

public class SuggestAddressesMgr {
	
	private static SuggestAddressesMgr singletoneInstance = new SuggestAddressesMgr();
	
	public static SuggestAddressesMgr sharedInstance() {
		return singletoneInstance;
	}

	/**
	 * *** Address Suggestion *****
	 */
	public JSONArray m_arrAddressSuggestion = new JSONArray();

	public void clearSuggestionList() {
		m_arrAddressSuggestion = new JSONArray();
	}

	public int getSuggestionListSize() {
		int count = 0;
		try {
			count = m_arrAddressSuggestion.length();
		} catch (Exception exception) {
		}
		return count;
	}

	public String getAddressSuggestionByIndex(int index) {
		String szAddress = "";
		if (m_arrAddressSuggestion.length() <= index)
			return szAddress;

		try {
			JSONObject item = m_arrAddressSuggestion.getJSONObject(index);
			szAddress = item.getString("description");
		} catch (Exception exception) {
		}
		return szAddress;
	}

	public String getAddressReferenceByIndex(int index) {
		String szReference = "";
		if (m_arrAddressSuggestion.length() <= index)
			return szReference;

		try {
			JSONObject item = m_arrAddressSuggestion.getJSONObject(index);
			szReference = item.getString("reference");
		} catch (Exception exception) {
		}
		return szReference;
	}

}
