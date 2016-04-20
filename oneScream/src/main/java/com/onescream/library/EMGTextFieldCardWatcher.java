package com.onescream.library;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * TextWatcher Class for Card Number's EditText in Upgrade Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class EMGTextFieldCardWatcher implements TextWatcher {
	boolean m_isProcessed;
	boolean m_isBackspace;
	EditText m_txtSelf;

	public EMGTextFieldCardWatcher() {
		m_isProcessed = false;
		m_isBackspace = true;
	}

	public EMGTextFieldCardWatcher(EditText txt) {
		m_txtSelf = txt;
		m_isProcessed = false;
		m_isBackspace = true;
	}

	@Override
	public synchronized void afterTextChanged(Editable s) {
		if (!m_isProcessed) {
			m_isProcessed = true;

			// String digits = s.toString().replaceAll("\\D", "");
			String digits = m_txtSelf.getText().toString()
					.replaceAll("\\D", "");

			String szResult = "";

			szResult = getFormattedString(digits, m_isBackspace);

			s.replace(0, s.length(), szResult);
			m_isProcessed = false;
		}
	}

	private String getFormattedString(String digits, boolean p_isBackspace) {
		String szResult = "";
		szResult += digits.substring(0, Math.min(4, digits.length()));
		if (digits.length() <=4 && p_isBackspace) return szResult;
		if (digits.length() < 4 && !p_isBackspace) return szResult;

		szResult += " " + digits.substring(4, Math.min(8, digits.length()));
		if (digits.length() <=8 && p_isBackspace) return szResult;
		if (digits.length() < 8 && !p_isBackspace) return szResult;

		szResult += " " + digits.substring(8, Math.min(12, digits.length()));
		if (digits.length() <=12 && p_isBackspace) return szResult;
		if (digits.length() < 12 && !p_isBackspace) return szResult;

		szResult += " " + digits.substring(12, Math.min(16, digits.length()));
		return szResult;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
								  int after) {
		if (count > after) {
			m_isBackspace = true;
		} else {
			m_isBackspace = false;
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}
