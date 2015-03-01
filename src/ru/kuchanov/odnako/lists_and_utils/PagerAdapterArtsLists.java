/*
 27.10.2014
ViewPagerAdapter.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import ru.kuchanov.odnako.fragments.FragmentAllAuthorsList;
import ru.kuchanov.odnako.fragments.FragmentArtsRecyclerList;
import ru.kuchanov.odnako.activities.ActivityMain;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;

/**
 * PagerAdapter for mainActivity, which shows main menu Categories
 */
public class PagerAdapterArtsLists extends FragmentStatePagerAdapter
{
	ActionBarActivity act;

	//////////
	//XXX
	public static interface FirstShot
	{
		public void onFirstShot();
	}

	private FirstShot firstShot;
	private boolean shot = false;

	//////////

	public PagerAdapterArtsLists(FragmentManager fm, ActionBarActivity act)
	{
		super(fm);
		this.act = act;
	}

	@Override
	public Fragment getItem(int position)
	{
		//XXX
		if (!shot && firstShot != null)
		{
			shot = true;
			firstShot.onFirstShot();
		}
		////////////
		
		if (position != 3 && position != 13)
		{
			FragmentArtsRecyclerList artsListFrag = new FragmentArtsRecyclerList();
			Bundle b = new Bundle();
			String categoryToLoad = CatData.getAllCategoriesMenuLinks(act)[position];
			b.putString("categoryToLoad", categoryToLoad);
			b.putInt("pageToLoad", 1);
			//setting position of selected art by asking Activities HashMap for it by category
			int selectedArtPosition = ((ActivityMain) act).getAllCatListsSelectedArtPosition().get(categoryToLoad);
			b.putInt("position", selectedArtPosition);
			artsListFrag.setArguments(b);
			return artsListFrag;
		}
		else if (position == 3)
		{
			FragmentAllAuthorsList frag = new FragmentAllAuthorsList();
			return frag;
		}
		else
		{
			return null;
		}
	}

	@Override
	public int getCount()
	{
		//		return this.categories.size();
		return CatData.getAllCategoriesMenuLinks(act).length;
	}
}