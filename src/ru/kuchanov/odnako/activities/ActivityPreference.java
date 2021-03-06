/*
 29.10.2014
ActivityPreference.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.activities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.FragmentPreference;
import ru.kuchanov.odnako.fragments.FragmentPreferenceAbout;
import ru.kuchanov.odnako.fragments.FragmentPreferenceDesign;
import ru.kuchanov.odnako.fragments.FragmentPreferenceSystem;
import ru.kuchanov.odnako.receivers.ReceiverTimer;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;

import com.yandex.metrica.YandexMetrica;

public class ActivityPreference extends PreferenceActivity implements
SharedPreferences.OnSharedPreferenceChangeListener
{
	private static final String LOG = ActivityPreference.class.getSimpleName();

	public static final String PREF_KEY_IS_PRO = "isPro";

	public static final String PREF_KEY_ADS_IS_ON = "adsOn";
	//Theme
	public static final String PREF_KEY_NIGHT_MODE = "night_mode";
	public static final String PREF_KEY_THEME = "theme";
	public static final String THEME_GREY = "grey";
	public static final String THEME_INDIGO = "indigo";
	public static final String THEME_RED = "red";
	public static final String THEME_TEAL = "teal";
	public static final String THEME_GREEN = "green";
	public static final String THEME_AMBER = "amber";

	public static final String PREF_KEY_TWO_PANE = "twoPane";
	
	public static final String PREF_KEY_TEXT_SIZE = "text_size";
	public static final String PREF_KEY_SCALE_UI = "scale";
	public static final String PREF_KEY_SCALE_ARTICLE = "scale_art";
	public static final String PREF_KEY_SCALE_COMMENTS = "scale_comments";
	
	public static final String PREF_KEY_IMAGE_SHOW = "image_show";
	public static final String PREF_KEY_IMAGE_POSITION = "images_position";
	public static final String PREF_VALUE_IMAGE_POSITION_UP = "up";
	public static final String PREF_VALUE_IMAGE_POSITION_LEFT = "left";
	public static final String PREF_VALUE_IMAGE_POSITION_RIGHT = "right";
	public static final String PREF_KEY_PREVIEW_SHOW = "preview_show";
	public static final String PREF_KEY_AUTHOR_IMAGE_SHOW = "author_image_show";
	public static final String PREF_KEY_ARTICLE_IS_READEN_BACKGROUND = "is_readen_background_show";
	
	//system
	public static final String PREF_KEY_MAX_ARTICLES_TO_STORE = "max_arts_to_store";
	public static final String PREF_KEY_DB_CLEAR = "clear_db";
	public static final String PREF_KEY_IMAGE_CACHE_INFO = "imageCacheSizeAndRoot";
	public static final String PREF_KEY_CACHE_CLEAR = "clearCache";
	
	//notification keys
	public static final String PREF_KEY_NOTIFICATION = "notification";
	public static final String PREF_KEY_NOTIF_VIBRATION = "vibration";
	public static final String PREF_KEY_NOTIF_SOUND = "sound";
	public static final String PREF_KEY_NOTIF_PERIOD = "notif_period";
	///
	public static final String PREF_KEY_FIRST_LAUNCH = "firstLaunch";

	private SharedPreferences pref;

	protected Method mLoadHeaders = null;
	protected Method mHasHeaders = null;

	int themeIconId;
	int vibrationIconId;
	int systemSettingsIconId;
	int aboutIconId;

	private List<Header> headersList;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		System.out.println("ActivityPreference onCreate");

		//get default settings to get all settings later
		PreferenceManager.setDefaultValues(this, R.xml.pref_design, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_notifications, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_system, true);
		PreferenceManager.setDefaultValues(this, R.xml.pref_about, true);
		this.pref = PreferenceManager.getDefaultSharedPreferences(this);
		//end of get default settings to get all settings later

		//set theme before super and set content to apply it
		boolean nightModeIsOn = this.pref.getBoolean(ActivityPreference.PREF_KEY_NIGHT_MODE, false) == true;
		int themeID = R.style.ThemeLightPreference;
		switch (this.pref.getString(ActivityPreference.PREF_KEY_THEME, ActivityPreference.THEME_GREY))
		{
			case ActivityPreference.THEME_GREY:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkPreference : R.style.ThemeLightPreference;
			break;
			case ActivityPreference.THEME_INDIGO:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkPreferenceIndigo : R.style.ThemeLightPreferenceIndigo;
			break;
			case ActivityPreference.THEME_RED:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkPreferenceRed : R.style.ThemeLightPreferenceRed;
			break;
			case ActivityPreference.THEME_TEAL:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkPreferenceTeal : R.style.ThemeLightPreferenceTeal;
			break;
			case ActivityPreference.THEME_GREEN:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkPreferenceGreen : R.style.ThemeLightPreferenceGreen;
			break;
			case ActivityPreference.THEME_AMBER:
				themeID = (nightModeIsOn) ? R.style.ThemeDarkPreferenceAmber : R.style.ThemeLightPreferenceAmber;
			break;
		}
		this.setTheme(themeID);

		//onBuildHeaders() will be called during super.onCreate()
		try
		{
			mLoadHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class);
			mHasHeaders = getClass().getMethod("hasHeaders");
		} catch (NoSuchMethodException e)
		{
		}

		//call super after setTheme to set it 0_0
		super.onCreate(savedInstanceState);

		///set title and icon to actionbar
		this.getActionBar().setTitle(R.string.settings);
		//set themeDependedIconsIDs
		int[] attrs = new int[] { R.attr.settingsIcon };
		TypedArray ta = this.obtainStyledAttributes(attrs);
		int settingsIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
		ta.recycle();
		this.getActionBar().setIcon(settingsIconId);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);

		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}

	public void onStop()
	{
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
		super.onStop();
	}

	@Override
	public void onPause()
	{
		YandexMetrica.onPauseActivity(this);

		super.onPause();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		YandexMetrica.onResumeActivity(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				//called when the up affordance/carat in actionbar is pressed
				onBackPressed();
				return true;
			default:
				return false;
		}
	}

	private void setHeadersIcons()
	{

		for (Header header : headersList)
		{
			switch (header.titleRes)
			{
				case (R.string.design):
					header.iconRes = themeIconId;
				break;
				case (R.string.notifications):
					header.iconRes = vibrationIconId;
				break;
				case (R.string.system_settings):
					header.iconRes = systemSettingsIconId;
				break;
				case (R.string.about):
					header.iconRes = aboutIconId;
				break;
			}
		}
	}

	private void getThemeDependedIconsIDs()
	{
		//set themeDependedIconsIDs
		int[] attrs = new int[] { R.attr.themeIcon };
		TypedArray ta = this.obtainStyledAttributes(attrs);
		themeIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
		ta.recycle();

		attrs = new int[] { R.attr.vibrationIcon };
		ta = this.obtainStyledAttributes(attrs);
		vibrationIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
		ta.recycle();

		attrs = new int[] { R.attr.systemSettingsIcon };
		ta = this.obtainStyledAttributes(attrs);
		systemSettingsIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
		ta.recycle();

		attrs = new int[] { R.attr.aboutIcon };
		ta = this.obtainStyledAttributes(attrs);
		aboutIconId = ta.getResourceId(0, R.drawable.ic_color_lens_grey600_48dp);
		ta.recycle();
	}

	@Override
	public void onBuildHeaders(List<Header> aTarget)
	{
		try
		{
			mLoadHeaders.invoke(this, new Object[] { R.xml.pref_headers, aTarget });

		} catch (IllegalArgumentException e)
		{
		} catch (IllegalAccessException e)
		{
		} catch (InvocationTargetException e)
		{
		} finally
		{
			this.headersList = aTarget;
			this.getThemeDependedIconsIDs();
			this.setHeadersIcons();
		}
	}

	@Override
	protected boolean isValidFragment(String fragmentName)
	{
		return FragmentPreference.class.getName().equals(fragmentName)
		|| FragmentPreferenceAbout.class.getName().equals(fragmentName)
		|| FragmentPreferenceDesign.class.getName().equals(fragmentName)
		|| FragmentPreferenceSystem.class.getName().equals(fragmentName);
	}

	/**
	 * As I remember it's for applying theme (backgroundColor) for dialogs
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
	{
		super.onPreferenceTreeClick(preferenceScreen, preference);
		if (preference != null)
			if (preference instanceof PreferenceScreen)
				if (((PreferenceScreen) preference).getDialog() != null)
					((PreferenceScreen) preference)
					.getDialog()
					.getWindow()
					.getDecorView()
					.setBackgroundDrawable(
					this.getWindow().getDecorView().getBackground().getConstantState().newDrawable());
		return false;
	}

	//to ALWAYS twoPane mode
	@Override
	public boolean onIsMultiPane()
	{
		if (this.getResources().getBoolean(R.bool.isTablet))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	//here we will:
	//change theme by restarting activity
	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key)
	{
		Log.i(LOG, "key: " + key);
		switch (key)
		{
			case PREF_KEY_TWO_PANE:
				Log.i(LOG, "twoPane: " + String.valueOf(pref.getBoolean(key, false) == true));
			break;
			case PREF_KEY_NIGHT_MODE:
				this.recreate();
			break;
			case PREF_KEY_THEME:
				boolean isPro = pref.getBoolean(ActivityPreference.PREF_KEY_IS_PRO, false) == true;
				if (isPro)
				{
					this.recreate();
				}
				else
				{
					String themeValue=pref.getString(key, "grey");
					if (themeValue.equals(THEME_GREY) || themeValue.equals(THEME_INDIGO))
					{
						this.recreate();
					}
				}
			break;
			case PREF_KEY_NOTIFICATION:
				//Запускаем\ отключаем сервис
				boolean notifOn = pref.getBoolean(key, false);

				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

				Intent intentToTimerReceiver = new Intent(this.getApplicationContext(), ReceiverTimer.class);
				intentToTimerReceiver.setAction("ru.kuchanov.odnako.RECEIVER_TIMER");

				PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0,
				intentToTimerReceiver,
				PendingIntent.FLAG_UPDATE_CURRENT);

				if (notifOn)
				{
					long checkPeriod = Long
					.valueOf(this.pref.getString(ActivityPreference.PREF_KEY_NOTIF_PERIOD, "60")) * 60L * 1000L;
					//test less interval in 1 min
					//checkPeriod = 60 * 1000;
					am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), checkPeriod, pendingIntent);
				}
				else
				{
					Log.e(LOG, "Canceling alarm");
					PendingIntent pendingIntentToDelete = PendingIntent.getBroadcast(this.getApplicationContext(), 0,
					intentToTimerReceiver,
					PendingIntent.FLAG_CANCEL_CURRENT);
					am.cancel(pendingIntentToDelete);
				}
			break;
		}
	}
}