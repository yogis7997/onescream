package com.uc.prjcmn;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.util.Log;

public class NetFuncs {
	private static final String TAG = "NetFuncs";

	public static int m_nResultCode;
	public static String m_strErrMsg;
	private static final int MAX_BUFF = 1024 * 100;

	public static JSONObject requestConnectionWithURL(String p_strURL,
			String[] p_fields, String[] p_values) {

		if (p_fields == null || p_values == null)
			return null;
		
		JSONObject j_source = null;

		URL connectUrl = null;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;

		try {
			// open connection
			int i, cnt;
			p_strURL = p_strURL + "?";
			cnt = p_fields.length;
			for (i = 0; i < cnt; i++) {
				if (p_fields[i].isEmpty())
					break;
				
				if (i > 0) {
					p_strURL += "&";
				}
				p_strURL += p_fields[i] + "=" + URLEncoder.encode(p_values[i], "UTF-8");
			}
			
			connectUrl = new URL(p_strURL);
			
			conn = (HttpURLConnection) connectUrl.openConnection();
			conn.setUseCaches(false);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);

			InputStream is = conn.getInputStream();
			boolean bParse = false;
			InputStreamReader isr = null;
			BufferedReader br = null;
			String resultData = "";

			try {
				isr = new InputStreamReader(is, "UTF-8");
				br = new BufferedReader(isr, MAX_BUFF);

				StringBuilder sb = new StringBuilder();
				while ((resultData = br.readLine()) != null) {
					sb.append(resultData);
				}
				resultData = sb.toString();
				sb = null;

				bParse = true;

			} catch (Exception e) {
				Log.i(TAG, "parse() : " + e.getMessage());
			} finally {
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
			}

			if (bParse) {
				int start_idx = resultData.indexOf("{");
				resultData = resultData.substring(start_idx,
						resultData.length());

				j_source = new JSONObject(resultData);
			}

		} catch (Exception e) {
			j_source = null;
			m_strErrMsg = e.getMessage();
		} finally {
			if (dos != null)
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (conn != null)
				conn.disconnect();
		}

		return j_source;
	}

	public static String getJSONString(JSONObject jsonObj, String strField) {
		String str = null;
		if (!jsonObj.isNull(strField)) {
			try {
				str = jsonObj.getString(strField);
				str = str.replace("<br/>", "\n");
			} catch (Exception e) {
			}
		}
		return str;
	}

}
