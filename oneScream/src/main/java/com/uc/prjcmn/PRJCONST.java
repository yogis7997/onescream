package com.uc.prjcmn;

/**
 * The class for Constants used in this application
 *
 * Created by Anwar Almojarkesh
 *
 */

public interface PRJCONST {
	public static final boolean isTesting = true;
	
	public static final int FREE_USE_DAYS = 14;
	
	public static final String DEFAULT_PASSWORD = "onescream";
	
	
	// -- Screen size --
	final static int SCREEN_WIDTH = 720;
	final static int SCREEN_HEIGHT = 1280;
	final static int SCREEN_DPI = 320;

	public static int MAX_WIFI_ITEM_INFO = 6;

	final static String FONT_OpenSans = "OpenSans-Light.ttf";
	final static String FONT_OpenSansBold = "OpenSans-Bold.ttf";

	// Universal engine type
	public static final int UNIVERSAL_ONE_SCREAM = 0;
	public static final int UNIVERSAL_ENGINE_CNT = 1;
	
	// WIFI Checking day's period
	public static final int WIFI_CHECKING_PERIOD = 1;

	// User Type
	public static final String USER_TYPE_TRIAL = "Trial";
	public static final String USER_TYPE_MONTH = "Paid_Month";
	public static final String USER_TYPE_YEAR = "Paid_Year";
}
