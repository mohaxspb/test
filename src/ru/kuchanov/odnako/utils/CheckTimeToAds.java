/*
 24.04.2015
CheckTimeToAds.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class CheckTimeToAds
{
	final static String LOG = CheckTimeToAds.class.getSimpleName() + "/";
	private final static String PREF_IN_APP_PERIOD = "inAppPeriod";
	private final static String PREF_MAX_IN_APP_PERIOD = "maxInAppPeriod";
	public final static String PREF_NEED_TO_SHOW_ADS = "needToShowAds";

	Context ctx;
	SharedPreferences pref;//f=PreferenceManager.getDefaultSharedPreferences(ctx);
	long timeOnResume;

	//long maxInAppPeriod;

	public CheckTimeToAds(Context ctx)
	{
		this.ctx = ctx;
		pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		//this.maxInAppPeriod = this.pref.getLong(PREF_MAX_IN_APP_PERIOD, (90L * 60L * 1000L));
	}

	public static long getMaxInAppPeriod(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return pref.getLong(PREF_MAX_IN_APP_PERIOD, (90L * 60L * 1000L));
	}

	public static void setMaxInAppPeriod(Context ctx, long period)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		pref.edit().putLong(PREF_MAX_IN_APP_PERIOD, period).commit();
	}

	public static boolean isTimeToShowAds(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return pref.getBoolean(PREF_NEED_TO_SHOW_ADS, false);
	}

	public static void adsShown(Context ctx)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		pref.edit().putBoolean(PREF_NEED_TO_SHOW_ADS, false).commit();
	}

	public void onResume()
	{
		timeOnResume = System.currentTimeMillis();
	}

	public void onPause()
	{
		long timeOnPause = System.currentTimeMillis();
		long inAppPeriod = timeOnPause - this.timeOnResume;

		long alreadyStoredInAppPeriod = this.pref.getLong(PREF_IN_APP_PERIOD, 0L);

		//check if new inAppPeriod < max
		if (inAppPeriod + alreadyStoredInAppPeriod < getMaxInAppPeriod(this.ctx))
		{
			inAppPeriod += alreadyStoredInAppPeriod;
			this.pref.edit().putLong(PREF_IN_APP_PERIOD, inAppPeriod).commit();
			Log.e(LOG, "onPause, less then max");

			Log.e(LOG, "onPause, inAppPeriod: " + inAppPeriod);
			Log.e(LOG, "onPause, getMaxInAppPeriod(this.ctx): " + getMaxInAppPeriod(this.ctx));
		}
		else
		{
			inAppPeriod = inAppPeriod + alreadyStoredInAppPeriod - getMaxInAppPeriod(this.ctx);
			this.pref.edit().putBoolean(PREF_NEED_TO_SHOW_ADS, true).commit();
			this.pref.edit().putLong(PREF_IN_APP_PERIOD, inAppPeriod).commit();
			Log.e(LOG, "onPause, MORE then max");
		}
	}
}