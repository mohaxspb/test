/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.fragments.ArticleFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;

public class ArticleViewPagerAdapter extends FragmentStatePagerAdapter
{
	ArrayList<ArtInfo> allArtsInfo;
	
	ActionBarActivity act;
	SharedPreferences pref;
	boolean twoPane;

	public ArticleViewPagerAdapter(FragmentManager fm, ArrayList<ArtInfo> allArtsInfo, ActionBarActivity act)
	{
		super(fm);
		this.allArtsInfo = allArtsInfo;
		this.act=act;
		
		this.pref=PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane=pref.getBoolean("twoPane", false);
		
	}

	@Override
	public Fragment getItem(int position)
	{
		ArticleFragment artFrag = new ArticleFragment();
		Bundle b = new Bundle();
		ArtInfo.writeAllArtsInfoToBundle(b, allArtsInfo, this.allArtsInfo.get(position));
		b.putInt("position", position);
		artFrag.setArguments(b);

		return artFrag;
	}

	@Override
	public int getCount()
	{
		return this.allArtsInfo.size();
	}

}
