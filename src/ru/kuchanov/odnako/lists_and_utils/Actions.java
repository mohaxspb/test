/*
 09.11.2014
Actions.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityArticle;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityComments;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.fragments.FragmentArtsRecyclerList;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerAllAuthors;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

public class Actions
{
	private static String LOG = Actions.class.getSimpleName() + "/";

	//	public static void showAllAuthorsArticles(ArtInfo p, ActionBarActivity act)
	/**
	 * We can have such situations:
	 * <ul>
	 * <li>on ActivityMain
	 * <ul>
	 * <li>twoPane</li>
	 * </ul>
	 * </ul>
	 * 
	 * @param authorBlogUrl
	 *            to show list or position in list and pager
	 * @param act
	 *            for switching action depend on activity
	 */
	public static void showAllAuthorsArticles(String authorBlogUrlFUCK, final ActionBarActivity act)
	{
		final String authorBlogUrl = Author.getURLwithoutSlashAtTheEnd(authorBlogUrlFUCK);
		if (!authorBlogUrl.equals("empty") && !authorBlogUrl.equals(""))
		{
			Log.d(LOG, "show all AuthorsArticles!");

			//check if it's large screen
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
			boolean twoPane = pref.getBoolean("twoPane", false);
			if (twoPane)
			{
				if (act.getClass().getSimpleName().equals("ActivityMain"))
				{
					final ActivityMain mainActivity = (ActivityMain) act;
					//check if we show allAuthors frag
					if (mainActivity.getCurentCategoryPosition() == 3)
					{
						//if so send message to switch items
						String[] allMenuCategories = CatData.getAllCategoriesMenuLinks(act);
						Intent intentToAllAutFrag = new Intent(
						allMenuCategories[mainActivity.getCurentCategoryPosition()] + "art_position");
						LocalBroadcastManager.getInstance(act).sendBroadcast(intentToAllAutFrag);

						//switch to frag in right pager
						ViewPager rightPager = (ViewPager) mainActivity.findViewById(R.id.article_comments_container);
						PagerAuthorsListsAdapter adapter = (PagerAuthorsListsAdapter) rightPager.getAdapter();
						int position = 0;
						for (int i = 0; i < adapter.getAllAuthorsList().size(); i++)
						{
							if (authorBlogUrl.equals(adapter.getAllAuthorsList().get(i).getBlog_url()))//.blogLink))
							{
								position = i;
								break;
							}
						}
						rightPager.setCurrentItem(position, true);
					}
					else
					{
						//else we must show authors list.
						//we can switch here if we have him in autList or not
						//if so we can show arts list in allAuthor frag or not
						ViewPager leftPager = (ViewPager) act.findViewById(R.id.arts_list_container);
						PagerAuthorsListsAdapter pagerAllAut = new PagerAuthorsListsAdapter(
						act.getSupportFragmentManager(), act);
						boolean weFindIt = false;
						int position = 0;
						for (int i = 0; i < pagerAllAut.getAllAuthorsList().size(); i++)
						{
//							if (authorBlogUrl.equals(pagerAllAut.getAllAuthorsList().get(i).blogLink))
							if (authorBlogUrl.equals(pagerAllAut.getAllAuthorsList().get(i).getBlog_url()))
							{
								weFindIt = true;
								position = i;
								break;
							}
						}
						if (weFindIt)
						{
							leftPager.setAdapter(pagerAllAut);
							leftPager.setOnPageChangeListener(new PagerListenerAllAuthors(mainActivity, pagerAllAut.getAllAuthorsList()));
							leftPager.setCurrentItem(position);
							mainActivity.setPagerType(ActivityMain.PAGER_TYPE_AUTHORS);
						}
						else
						{
							leftPager.setAdapter(new PagerOneArtsListAdapter(act.getSupportFragmentManager(), act,
							authorBlogUrl));
							leftPager.setOnPageChangeListener(null);
							ViewPager rightPager = (ViewPager) mainActivity
							.findViewById(R.id.article_comments_container);
							rightPager.setAdapter(new PagerArticlesAdapter(act.getSupportFragmentManager(),
							authorBlogUrl, mainActivity));
							rightPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
							{
								@Override
								public void onPageSelected(int position)
								{
									//move topImg and toolBar while scrolling left list
									Toolbar toolbarRight = (Toolbar) act.findViewById(R.id.toolbar_right);
									toolbarRight.setTitle("");
									System.out.println("onPageSelected in articlePager; position: " + position);
									mainActivity.getAllCatListsSelectedArtPosition().put(authorBlogUrl, position);

									Intent intentToListFrag = new Intent(authorBlogUrl + "art_position");
									Bundle b = new Bundle();
									b.putInt("position", position);
									intentToListFrag.putExtras(b);

									LocalBroadcastManager.getInstance(act).sendBroadcast(intentToListFrag);
								}
							});
							if (mainActivity.getAllCatListsSelectedArtPosition().get(authorBlogUrl) != null)
							{
								rightPager.setCurrentItem(mainActivity.getAllCatListsSelectedArtPosition().get(
								authorBlogUrl));
							}
							mainActivity.setPagerType(ActivityMain.PAGER_TYPE_SINGLE);
							mainActivity.setCurrentCategory(authorBlogUrl);
						}
					}
				}
			}
			else
			{
				//if not twoPane
				//TODO
			}
		}
		else
		{
			System.out.println("p.authorBlogUrl.equals('empty' || ''): WTF?!");
		}
	}

	public static void markAsRead(String url, Context ctx)
	{
		Toast.makeText(ctx, "readed!", Toast.LENGTH_SHORT).show();
	}

	public static void shareUrl(String url, Context ctx)
	{
		Toast.makeText(ctx, "share!", Toast.LENGTH_SHORT).show();
	}

	public static void showComments(ArrayList<ArtInfo> allArtsInfo, int position, ActionBarActivity act)
	{
		Toast.makeText(act, "comments!", Toast.LENGTH_SHORT).show();

		//light clicked card if we can find frag from @param act 
		if (act.getClass().getSimpleName().equals("ActivityMain"))
		{
			FragmentArtsRecyclerList artsListFrag = (FragmentArtsRecyclerList) act.getSupportFragmentManager()
			.findFragmentById(R.id.arts_list_container);
			artsListFrag.setActivatedPosition(position);
		}

		//check if it's large screen
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);

		if (twoPane)
		{

			ViewPager pager = (ViewPager) act.findViewById(R.id.article_comments_container);
			if (pager.getAdapter().getClass().getSimpleName().equals(CommentsViewPagerAdapter.class.getSimpleName()))
			{
				pager.setCurrentItem(position, true);
			}
			//so it's comments adapter and need to switch to artAdapter
			else
			{
				PagerAdapter pagerAdapter = new CommentsViewPagerAdapter(act.getSupportFragmentManager(),
				allArtsInfo, CommentInfo.getDefaultAllArtsCommentsInfo(30, 10), act);
				pager.setAdapter(pagerAdapter);
				pager.setCurrentItem(position, true);
			}

		}
		else
		{
			Intent intent = new Intent(act, ActivityComments.class);
			Bundle b = new Bundle();
			b.putInt("position", position);
			ArtInfo.writeAllArtsInfoToBundle(b, allArtsInfo, allArtsInfo.get(position));
			b.putIntArray("groupChildPosition", ((ActivityBase) act).getGroupChildPosition());
			intent.putExtras(b);
			act.startActivity(intent);
		}
	}

	public static void showArticle(ArrayList<ArtInfo> allArtsInfo, int positionOfArticle, final ActionBarActivity act)
	{
		Log.d(LOG, "showArticle!");

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(act);
		boolean twoPane = pref.getBoolean("twoPane", false);
		//check if it's large screen
//		if (act.getClass().getSimpleName().equals(ActivityMain.class.getSimpleName()))
		if(act instanceof ActivityMain)
		{
			final ActivityMain mainActivity = (ActivityMain) act;
			if (twoPane)
			{
				final ViewPager rightPager = (ViewPager) act.findViewById(R.id.article_comments_container);
				//check if we are showing allAuthors (curCatPosition=3) or allCategories (curCatPosition=13) 
				if (rightPager.getAdapter() instanceof PagerAuthorsListsAdapter)
				{
					
					//if so we must change adapters to all ViewPagers
					final ViewPager leftPager = (ViewPager) act.findViewById(R.id.arts_list_container);
					PagerAuthorsListsAdapter pagerAllAut = new PagerAuthorsListsAdapter(
					act.getSupportFragmentManager(), act);
					boolean weFindIt = false;
					int positionInLeftPager = 0;
					String authorBlogUrl = allArtsInfo.get(positionOfArticle).authorBlogUrl;

					for (int i = 0; i < pagerAllAut.getAllAuthorsList().size(); i++)
					{
						if (authorBlogUrl.equals(pagerAllAut.getAllAuthorsList().get(i).getBlog_url()))//.blogLink))
						{
							weFindIt = true;
							positionInLeftPager = i;
							break;
						}
					}
					if (weFindIt)
					{
						mainActivity.setPagerType(ActivityMain.PAGER_TYPE_AUTHORS);
						
						//setCurentCategoryPosition for activity
						mainActivity.setCurentCategoryPosition(positionInLeftPager);
						
//						final String authorBlogUrlFromAdapter = pagerAllAut.getAllAuthorsList()
//						.get(positionInLeftPager).getBlog_url();
						
						leftPager.setAdapter(pagerAllAut);
						leftPager.setOnPageChangeListener(new PagerListenerAllAuthors(mainActivity, pagerAllAut.getAllAuthorsList()));
						leftPager.setCurrentItem(positionInLeftPager);
					}
					else
					{
						leftPager.setAdapter(new PagerOneArtsListAdapter(act.getSupportFragmentManager(), act,
						authorBlogUrl));
						//setCurentCategoryPosition for activity
						mainActivity.setCurentCategoryPosition(0);
						mainActivity.setPagerType(ActivityMain.PAGER_TYPE_SINGLE);
					}
				}
				//TODO CHECK IT so it's comments adapter and need to switch to artAdapter
				else
				{
					rightPager.setCurrentItem(positionOfArticle, true);
				}
			}
			else
			{
				Intent intent = new Intent(act, ActivityArticle.class);
				Bundle b = new Bundle();
				b.putInt("position", positionOfArticle);
				ArtInfo.writeAllArtsInfoToBundle(b, allArtsInfo, allArtsInfo.get(positionOfArticle));
				b.putIntArray("groupChildPosition", ((ActivityBase) act).getGroupChildPosition());
				intent.putExtras(b);

				act.startActivity(intent);
			}
		}
		else if (act.getClass().getSimpleName().equals(ActivityArticle.class.getSimpleName()))
		{
			//TODO
		}
		else
		{
			//TODO
		}
	}
}
