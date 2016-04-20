package com.uc.prjcmn;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * The class to manage all opened activities
 *
 * Created by Anwar Almojarkesh
 *
 */

public class ActivityTask {

	public static ActivityTask INSTANCE = new ActivityTask();
	private List<Activity> mList;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ActivityTask() {
		mList = new ArrayList();
	}

	public void add(Activity act) {
		mList.add(act);
	}

	public void remove(Activity act) {
		mList.remove(act);
	}

	public void clear() {
		for (Activity act : mList) {
			act.finish();
		}
	}
}
