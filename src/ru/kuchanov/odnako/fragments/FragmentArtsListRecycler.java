/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityBase;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.activities.ActivityPreference;
import ru.kuchanov.odnako.animations.RecyclerViewOnScrollListener;
import ru.kuchanov.odnako.animations.SpacesItemDecoration;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.db.Author;
import ru.kuchanov.odnako.db.Category;
import ru.kuchanov.odnako.db.DataBaseHelper;
import ru.kuchanov.odnako.db.Msg;
import ru.kuchanov.odnako.db.ServiceDB;
import ru.kuchanov.odnako.db.ServiceRSS;
import ru.kuchanov.odnako.download.ParsePageForAllArtsInfo;
import ru.kuchanov.odnako.lists_and_utils.CatData;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterAllAuthors;
import ru.kuchanov.odnako.lists_and_utils.PagerAdapterAllCategories;
import ru.kuchanov.odnako.lists_and_utils.PagerListenerArticle;
import ru.kuchanov.odnako.lists_and_utils.RecyclerAdapterArtsListFragment;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Fragment for artsList. We use it as main Fragment for menu categories instead
 * of allAuthors and -Categories
 */
public class FragmentArtsListRecycler extends Fragment
{
	private final static String LOG = FragmentArtsListRecycler.class.getSimpleName() + "/";

	ImageLoader imgLoader;

	private int pageToLoad = 1;

	private SwipeRefreshLayout swipeRef;

	/**
	 * art's list top image
	 */
	private ImageView topImg;
	private float topImgCoord;

	private int toolbarId = R.id.toolbar;
	private boolean isInLeftPager = true;

	private RecyclerView recycler;
	private RecyclerAdapterArtsListFragment recyclerAdapter;

	private ActivityMain act;
	private SharedPreferences pref;

	private String categoryToLoad;
	private ArrayList<Article> allArtsInfo;
	private int position = 0;

	private final static String KEY_IS_LOADING = "isLoading";
	private boolean isLoading = false;
	private final static String KEY_IS_LOADING_FROM_TOP = "isLoadingFromTop";
	private boolean isLoadingFromTop = true;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		//Log.d(LOG_TAG+categoryToLoad, "onCreate");
		super.onCreate(savedInstanceState);

		this.act = (ActivityMain) this.getActivity();
		this.imgLoader = MyUIL.get(act);
		this.pref = PreferenceManager.getDefaultSharedPreferences(act);

		Bundle fromArgs = this.getArguments();
		if (fromArgs != null)
		{
			this.categoryToLoad = fromArgs.getString("categoryToLoad");
			this.position = fromArgs.getInt("position");
		}

		//restore topImg and toolbar prop's
		if (savedInstanceState != null)
		{
			this.topImgCoord = savedInstanceState.getFloat("topImgYCoord");
			this.pageToLoad = savedInstanceState.getInt("pageToLoad");
			this.categoryToLoad = savedInstanceState.getString("categoryToLoad");
			this.position = savedInstanceState.getInt("position");
			this.isLoading = savedInstanceState.getBoolean(KEY_IS_LOADING);
			this.isLoadingFromTop = savedInstanceState.getBoolean(KEY_IS_LOADING_FROM_TOP);
		}

		LocalBroadcastManager.getInstance(this.act).registerReceiver(artsDataReceiver,
		new IntentFilter(this.getCategoryToLoad()));

		//reciver for scrolling and highligting selected position
		LocalBroadcastManager.getInstance(this.act).registerReceiver(artSelectedReceiver,
		new IntentFilter(this.getCategoryToLoad() + "art_position"));

		//receiver for updating savedState (if artsText is loaded)
		LocalBroadcastManager.getInstance(this.act).registerReceiver(receiverArticleLoaded,
		new IntentFilter(Const.Action.ARTICLE_CHANGED));
	}

	private void setLoading(boolean isLoading)
	{
		this.isLoading = isLoading;
		if (isLoading)
		{
			if (this.isLoadingFromTop)
			{
				int[] actionBarSizeAttr = new int[] { android.R.attr.actionBarSize };
				TypedValue typedValue = new TypedValue();
				TypedArray a = act.obtainStyledAttributes(typedValue.data, actionBarSizeAttr);
				int actionBarSize = a.getDimensionPixelSize(0, 100);
				a.recycle();
				//this.swipeRef.setProgressViewOffset(false, 0, actionBarSize);
				swipeRef.setProgressViewEndTarget(false, actionBarSize);
			}
			else
			{
				int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
				TypedValue typedValue = new TypedValue();
				TypedArray a = act.obtainStyledAttributes(typedValue.data, textSizeAttr);
				int actionBarSize = a.getDimensionPixelSize(0, 100);
				a.recycle();
				DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
				int height = displayMetrics.heightPixels;
				swipeRef.setProgressViewOffset(false, 0, height - actionBarSize * 2);
				swipeRef.setProgressViewEndTarget(false, height - actionBarSize * 2);
			}
			swipeRef.setRefreshing(true);
		}
		else
		{
			int[] textSizeAttr = new int[] { android.R.attr.actionBarSize };
			int indexOfAttrTextSize = 0;
			TypedValue typedValue = new TypedValue();
			TypedArray a = act.obtainStyledAttributes(typedValue.data, textSizeAttr);
			int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, 100);
			a.recycle();
			//this.swipeRef.setProgressViewOffset(false, 0, actionBarSize);
			swipeRef.setProgressViewEndTarget(false, actionBarSize);
			swipeRef.setRefreshing(false);
		}
	}

	private BroadcastReceiver artSelectedReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			//Log.i(LOG + categoryToLoad, "artSelectedReceiver onReceive called");
			position = intent.getIntExtra("position", 0);

			if (!isAdded())
			{
				return;
			}
			if (recyclerAdapter != null)
			{
				setActivatedPosition(position);
				recyclerAdapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * receives intent with Articles data and updates list, toolbar and toast in
	 * some cases, based on message from DB. Also, if this is main list
	 * (odnako.org/blogs) and we load from top it starts loading data from rss
	 */
	private BroadcastReceiver artsDataReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.i(LOG + categoryToLoad, "artsDataReceiver onReceive called");

			if (!isAdded())
			{
				Log.e(LOG + categoryToLoad, "fragment not added! RETURN!");
				return;
			}
			//check if this fragment is currently displayed 
			boolean isDisplayed = false;
			int pagerType = act.getPagerType();
			int currentCategoryPosition = act.getCurentCategoryPosition();
			ViewPager pagerLeft = (ViewPager) act.findViewById(R.id.pager_left);
			ViewPager pagerRight = (ViewPager) act.findViewById(R.id.pager_right);
			switch (pagerType)
			{
				case ActivityMain.PAGER_TYPE_MENU:
					String[] allMenuLinks = CatData.getMenuLinks(act);

					if (categoryToLoad.equals(allMenuLinks[currentCategoryPosition]) && isInLeftPager)
					{
						//so it's currently displayed fragment
						isDisplayed = true;
					}
					else if (!isInLeftPager)
					{
						if (currentCategoryPosition == 3)
						{

							PagerAdapterAllAuthors allAuthorsAdapter = (PagerAdapterAllAuthors) pagerRight
							.getAdapter();
							List<String> allAuthorsUrls = allAuthorsAdapter.getAllAuthorsURLsList();
							int selectedArtPosOfAllAuthorsFrag = act.getAllCatListsSelectedArtPosition().get(
							allMenuLinks[3]);
							if (categoryToLoad.equals(allAuthorsUrls.get(selectedArtPosOfAllAuthorsFrag)))
							{
								//so it's currently displayed fragment
								isDisplayed = true;
							}
							else
							{
								isDisplayed = false;
							}
						}
						else if (currentCategoryPosition == 13)
						{
							PagerAdapterAllCategories allCategoriesAdapter = (PagerAdapterAllCategories) pagerRight
							.getAdapter();
							List<String> alllCategoriesUrls = allCategoriesAdapter.getAllCategoriesURLsList();
							int selectedArtPosOfAlllCategoriesFrag = act.getAllCatListsSelectedArtPosition().get(
							allMenuLinks[3]);
							if (categoryToLoad.equals(alllCategoriesUrls.get(selectedArtPosOfAlllCategoriesFrag)))
							{
								//so it's currently displayed fragment
								isDisplayed = true;
							}
							else
							{
								isDisplayed = false;
							}
						}
					}
					else
					{
						isDisplayed = false;
					}
				break;
				case ActivityMain.PAGER_TYPE_AUTHORS:
					PagerAdapterAllAuthors allAuthorsAdapter = (PagerAdapterAllAuthors) pagerLeft.getAdapter();
					List<String> allAuthorsUrls = allAuthorsAdapter.getAllAuthorsURLsList();
					if (categoryToLoad.equals(allAuthorsUrls.get(currentCategoryPosition)) && isInLeftPager)
					{
						//so it's currently displayed fragment
						isDisplayed = true;
					}
					else
					{
						isDisplayed = false;
					}
				break;
				case ActivityMain.PAGER_TYPE_CATEGORIES:
					PagerAdapterAllCategories allCategoriesAdapter = (PagerAdapterAllCategories) pagerLeft.getAdapter();
					List<String> allCategoriesUrls = allCategoriesAdapter.getAllCategoriesURLsList();
					if (categoryToLoad.equals(allCategoriesUrls.get(currentCategoryPosition)) && isInLeftPager)
					{
						//so it's currently displayed fragment
						isDisplayed = true;
					}
					else
					{
						isDisplayed = false;
					}
				break;
				case ActivityMain.PAGER_TYPE_SINGLE:
					//it's the only one fragment, so isDisplayed is always true
					isDisplayed = true;
					//try setting title to toolbar
					Toolbar toolbar = (Toolbar) act.findViewById(toolbarId);

					//find Category or Author name in DB
					DataBaseHelper h = new DataBaseHelper(act);
					if (Category.isCategory(h, categoryToLoad) == null)
					{
						toolbar.setTitle(categoryToLoad);
					}
					else
					{
						String toolbarTitle;
						if (Category.isCategory(h, categoryToLoad))
						{
							toolbarTitle = Category.getNameByUrl(h, categoryToLoad);
							toolbar.setTitle(toolbarTitle);
						}
						else
						{
							toolbarTitle = Author.getNameByUrl(h, categoryToLoad);
							toolbar.setTitle(toolbarTitle);
						}
						Log.d(LOG, "toolbarTitle: " + toolbarTitle);
					}
					h.close();
				break;
			}

			//get result message
			String[] msg = intent.getStringArrayExtra(Msg.MSG);
			int page = intent.getIntExtra("pageToLoad", 1);
			boolean showPreview = pref.getBoolean(ActivityPreference.PREF_KEY_PREVIEW_SHOW, false) == true;
			switch (msg[0])
			{
				case Msg.DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL:
				case Msg.DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG:
				case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
					Log.d(LOG + categoryToLoad, "Msg.DB_ANSWER_INFO_SENDED_TO_FRAG");
					updateAdapter(intent, page);
					setLoading(false);
				break;
				case (Msg.NO_NEW):
					Log.d(LOG + categoryToLoad, "Новых статей не обнаружено!");
					if (isDisplayed)
					{
						Toast.makeText(act, "Новых статей не обнаружено!", Toast.LENGTH_SHORT).show();
					}
					updateAdapter(intent, page);
					setLoading(false);
					//TODO remove RSS to ServiceDB
					if (getCategoryToLoad().contains("odnako.org/blogs") && showPreview)
					{
						Intent intentRSS = new Intent(act, ServiceRSS.class);
						intentRSS.putExtra("categoryToLoad", getCategoryToLoad());
						act.startService(intentRSS);
					}
				break;
				case (Msg.NEW_QUONT):
					Log.d(LOG + categoryToLoad, "Обнаружено " + msg[1] + " новых статей");
					//setPosition to zero to avoid bugs
					position = 0;
					((ActivityMain) act).getAllCatListsSelectedArtPosition().put(categoryToLoad, position);
					if (isDisplayed)
					{
						Toast.makeText(act, "Обнаружено " + msg[1] + " новых статей", Toast.LENGTH_SHORT).show();
					}
					updateAdapter(intent, page);
					setLoading(false);
					//TODO remove RSS to ServiceDB
					if (getCategoryToLoad().contains("odnako.org/blogs") && showPreview)
					{
						Intent intentRSS = new Intent(act, ServiceRSS.class);
						intentRSS.putExtra("categoryToLoad", getCategoryToLoad());
						act.startService(intentRSS);
					}
				break;
				case (Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES):
					Log.d(LOG + categoryToLoad, "Обнаружено " + msg[1] + " новых статей");
					position = 0;
					((ActivityMain) act).getAllCatListsSelectedArtPosition().put(categoryToLoad, position);
					if (isDisplayed)
					{
						Toast.makeText(act, "Обнаружено более 30 новых статей", Toast.LENGTH_SHORT).show();
					}
					updateAdapter(intent, page);
					setLoading(false);
				break;
				case (Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT):
					Log.d(LOG + categoryToLoad, "Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT");
					updateAdapter(intent, page);
					setLoading(false);
				break;
				case (Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION):
					//we catch publishing lag from bottom, so we'll toast unsinked status
					//and start download from top (pageToLoad=1)
					Log.d(LOG + "/" + categoryToLoad, "Синхронизирую базу данных. Загружаю новые статьи");
					if (isDisplayed)
					{
						Toast.makeText(act, "Синхронизирую базу данных. Загружаю новые статьи", Toast.LENGTH_SHORT)
						.show();
					}
					pageToLoad = 1;
					position = 0;
					((ActivityMain) act).getAllCatListsSelectedArtPosition().put(categoryToLoad, position);
					allArtsInfo.clear();
					ArrayList<Article> def = new ArrayList<Article>();
					Article a = new Article();
					a.setTitle("Статьи загружаются, подождите пожалуйста");
					def.add(a);
					allArtsInfo = def;
					((ActivityMain) act).getAllCatArtsInfo().put(categoryToLoad, allArtsInfo);
					recycler.getAdapter().notifyDataSetChanged();
					getAllArtsInfo(true);
				break;
				case (Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY):
					Log.e(LOG + categoryToLoad, "Ни одной статьи не обнаружено!");
					if (isDisplayed)
					{
						Toast.makeText(act, "Ни одной статьи не обнаружено!", Toast.LENGTH_SHORT).show();
					}
					position = 0;
					((ActivityMain) act).getAllCatListsSelectedArtPosition().put(categoryToLoad, position);
					updateAdapter(intent, page);
					setLoading(false);
				break;
				case (Msg.ERROR):
					Log.e(LOG + categoryToLoad, msg[1]);
					if (isDisplayed)
					{
						Toast.makeText(act, msg[1], Toast.LENGTH_SHORT).show();
					}
					//check if there was error while loading from bottom, if so, decrement pageToLoad
					if (page != 1)
					{
						pageToLoad--;
					}
					setLoading(false);
				break;
				default:
					Log.e(LOG + categoryToLoad, "непредвиденный ответ базы данных");
					if (isDisplayed)
					{
						Toast.makeText(act, "непредвиденный ответ базы данных", Toast.LENGTH_SHORT).show();
					}
					setLoading(false);
				break;
			}

			boolean refreshRightToolbarAndPager = isInLeftPager && (pref.getBoolean("twoPane", false) == true)
			&& isDisplayed;
			if (refreshRightToolbarAndPager)
			{
				updateRightPagerAndToolbar(msg);
			}
		}//onReceive
	};//artsDataReceiver

	private void updateAdapter(Intent intent, int page)
	{
		if (this.act.getServiceDB().getAllCatArtsInfo().get(this.categoryToLoad) != null)
		{
			if (page == 1)
			{
				allArtsInfo = this.act.getServiceDB().getAllCatArtsInfo().get(this.categoryToLoad);
				this.recyclerAdapter = new RecyclerAdapterArtsListFragment(act, allArtsInfo, this);
				this.recycler.setAdapter(recyclerAdapter);
			}
			else
			{
				recyclerAdapter.notifyDataSetChanged();
			}
			((ActivityBase) act).getAllCatArtsInfo().put(categoryToLoad, allArtsInfo);
		}
		else
		{
			System.out.println("ArrayList<Article> someResult=NULL!!!");
		}
	}

	private void updateRightPagerAndToolbar(String[] msg)
	{
		ActivityMain mainActivity = (ActivityMain) this.act;
		ViewPager pagerRight = (ViewPager) mainActivity.findViewById(R.id.pager_right);
		
		if(pagerRight==null)
		{
			mainActivity.recreate();
		}
		
		Toolbar rightToolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar_right);
		int selectedArt;
		int allArtsSize;

		ViewPager.OnPageChangeListener listener = new PagerListenerArticle(mainActivity, categoryToLoad);

		switch (msg[0])
		{
			case Msg.DB_ANSWER_INFO_SENDED_TO_FRAG:
				pagerRight.getAdapter().notifyDataSetChanged();

				selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(this.categoryToLoad) + 1;
				allArtsSize = this.allArtsInfo.size();
				rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);

				//				pagerRight.setOnPageChangeListener(listener);
				pagerRight.addOnPageChangeListener(listener);
				listener.onPageSelected(this.position);
			case (Msg.NO_NEW):
				pagerRight.getAdapter().notifyDataSetChanged();

				selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(this.categoryToLoad) + 1;
				allArtsSize = this.allArtsInfo.size();
				rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);

				//				pagerRight.setOnPageChangeListener(listener);
				pagerRight.addOnPageChangeListener(listener);
				listener.onPageSelected(this.position);
			break;
			case (Msg.NEW_QUONT):
				pagerRight.getAdapter().notifyDataSetChanged();
				//			pagerRight.setOnPageChangeListener(listener);
				pagerRight.addOnPageChangeListener(listener);
				listener.onPageSelected(0);
			break;
			case (Msg.DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES):
				pagerRight.getAdapter().notifyDataSetChanged();
				//			pagerRight.setOnPageChangeListener(listener);
				pagerRight.addOnPageChangeListener(listener);
				listener.onPageSelected(0);
			break;
			case (Msg.DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT):
				pagerRight.getAdapter().notifyDataSetChanged();

				selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(this.categoryToLoad) + 1;
				allArtsSize = this.allArtsInfo.size();
				rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);

				//				pagerRight.setOnPageChangeListener(listener);
				pagerRight.addOnPageChangeListener(listener);
				listener.onPageSelected(this.position);
			break;
			case (Msg.DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION):
				pagerRight.getAdapter().notifyDataSetChanged();

				selectedArt = mainActivity.getAllCatListsSelectedArtPosition().get(this.categoryToLoad) + 1;
				allArtsSize = this.allArtsInfo.size();
				rightToolbar.setTitle("Статья " + selectedArt + "/" + allArtsSize);
			break;
			case (Msg.DB_ANSWER_NO_ARTS_IN_CATEGORY):
				pagerRight.getAdapter().notifyDataSetChanged();
			break;
			case (Msg.ERROR):
			//nothing to do;
			break;
			default:
			//nothing to do;
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		//Log.d(LOG_TAG, "onCreateView");
		View v = inflater.inflate(R.layout.fragment_arts_list, container, false);

		//find cur frag's toolbar
		if (container.getId() == R.id.pager_right)
		{
			this.toolbarId = R.id.toolbar_right;
			//unregister from articles selected events to not highlight items
			if (artSelectedReceiver != null)
			{
				LocalBroadcastManager.getInstance(act).unregisterReceiver(artSelectedReceiver);
				artSelectedReceiver = null;
			}
			this.setInLeftPager(false);
		}
		else if (container.getId() == R.id.pager_left)
		{
			this.toolbarId = R.id.toolbar;
			this.setInLeftPager(true);
		}

		//set top image of category
		this.topImg = (ImageView) v.findViewById(R.id.top_img);
		ArrayList<Category> allCats = (ArrayList<Category>) act.getAllCategoriesList();
		boolean findIt = false;
		if (allCats != null)
		{
			//find imgUrl
			for (Category c : allCats)
			{
				if (Author.getURLwithoutSlashAtTheEnd(this.getCategoryToLoad()).equals(
				Author.getURLwithoutSlashAtTheEnd(c.getUrl())))
				{
					String imgUrl = c.getImgUrl();
					if (imgUrl.startsWith("/i/"))
					{
						imgUrl = Const.DOMAIN_MAIN + imgUrl;
					}
					imgLoader.displayImage(imgUrl, topImg, MyUIL.getCategoryImageOptions(act));
					findIt = true;
					break;
				}
			}
			if (findIt == false)
			{
				//setDefault
				int resId = R.drawable.odnako;
				topImg.setImageResource(resId);
			}
		}
		else
		{
			Log.d(LOG, categoryToLoad + ": " + "allCats=NULL WTF?!");
			//setDefault
			int resId = R.drawable.odnako;
			topImg.setImageResource(resId);
		}

		this.topImg.setY(topImgCoord);

		this.swipeRef = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
		//workaround to fix issue with not showing refreshing indicator before swipeRef.onMesure() was called
		//as I understand before onResume of Activity

		TypedValue typed_value = new TypedValue();
		getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
		this.swipeRef.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));

		this.swipeRef.setProgressViewEndTarget(false, getResources().getDimensionPixelSize(typed_value.resourceId));

		this.swipeRef.setColorSchemeResources(R.color.material_red_300,
		R.color.material_red_500,
		R.color.material_red_500,
		R.color.material_red_500);
		////set on swipe listener
		this.swipeRef.setOnRefreshListener(new OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				pageToLoad = 1;
				getAllArtsInfo(true);
			}
		});

		this.recycler = (RecyclerView) v.findViewById(R.id.arts_list_view);
		this.recycler.setItemAnimator(new DefaultItemAnimator());
		this.recycler.addItemDecoration(new SpacesItemDecoration(25));
		this.recycler.setLayoutManager(new LinearLayoutManager(act));

		//animate loading state if we load something
		this.setLoading(this.isLoading);

		if (this.act.getServiceDB() != null)
		{
			this.allArtsInfo = this.act.getServiceDB().getAllCatArtsInfo().get(this.categoryToLoad);
		}
		else
		{
			this.allArtsInfo = this.act.getAllCatArtsInfo().get(this.categoryToLoad);
		}

		if (this.allArtsInfo == null)
		{
			if (this.isLoading == false)
			{
				this.getAllArtsInfo(false);
			}
			//XXX
			//			this.recyclerAdapter = new RecyclerAdapterArtsListFragment(act, allArtsInfo, this);
			//			this.recycler.setAdapter(recyclerAdapter);
		}
		else
		{
			this.recyclerAdapter = new RecyclerAdapterArtsListFragment(act, allArtsInfo, this);
			this.recycler.setAdapter(recyclerAdapter);
		}

		this.recycler.addOnScrollListener(new RecyclerViewOnScrollListener(act, this.categoryToLoad, this.topImg,
		this.toolbarId)
		{
			public void onLoadMore()
			{
				if (isLoading == false)
				{
					pageToLoad++;
					getAllArtsInfo(true);
					Log.e(LOG + categoryToLoad, "Start loading page " + pageToLoad + " from bottom!");
				}
			}
		});

		FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
		fab.attachToRecyclerView(recycler);
		fab.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Log.i(LOG, "FAB clicked!");
				ArrayList<Category> allCategories = new ArrayList<Category>();
				ArrayList<Author> allAuthors = new ArrayList<Author>();
				boolean isCategory = true;
				switch (act.getPagerType())
				{
					case ActivityMain.PAGER_TYPE_AUTHORS:
						isCategory = false;
						allAuthors = ((PagerAdapterAllAuthors) act.artsListPager.getAdapter()).getAllAuthorsList();
					break;
					case ActivityMain.PAGER_TYPE_CATEGORIES:
						isCategory = true;
						allCategories = ((PagerAdapterAllCategories) act.artsListPager.getAdapter())
						.getAllCategoriesList();
					break;
					case ActivityMain.PAGER_TYPE_MENU:
					case ActivityMain.PAGER_TYPE_SINGLE:
						isCategory = true;
					break;
				}
				int positionInList = act.getCurentCategoryPosition();
				FragmentDialogDownloads frag = FragmentDialogDownloads.newInstance(allCategories, allAuthors,
				isCategory, positionInList);
				frag.show(act.getSupportFragmentManager(), FragmentDialogDownloads.class.getSimpleName());
			}
		});

		return v;
	}

	private void getAllArtsInfo(boolean startDownload)
	{
		//Log.i(categoryToLoad, "getAllArtsInfo called");
		if (this.pageToLoad == 1)
		{
			this.isLoadingFromTop = true;
		}
		else
		{
			this.isLoadingFromTop = false;
		}
		setLoading(true);

		Intent intent = new Intent(this.act, ServiceDB.class);
		String[] menuLinks = CatData.getMenuLinks(act);
		String action = this.getCategoryToLoad().equals(menuLinks[menuLinks.length - 1]) ? Const.Action.GET_DOWNLOADED
		: Const.Action.DATA_REQUEST;
		//		intent.setAction(Const.Action.DATA_REQUEST);
		intent.setAction(action);
		intent.putExtra("categoryToLoad", this.getCategoryToLoad());
		intent.putExtra("pageToLoad", this.pageToLoad);
		intent.putExtra("timeStamp", System.currentTimeMillis());
		intent.putExtra("startDownload", startDownload);
		this.act.startService(intent);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		//check if we connected to service
		final Handler handler = new Handler();

		final Runnable r = new Runnable()
		{
			public void run()
			{
				if (act.getServiceDB() != null)
				{
					List<ParsePageForAllArtsInfo> currentTasks = act.getServiceDB().currentTasks;
					//check if we have NOT running task with this frags category
					boolean noCategoryInService = true;
					for (ParsePageForAllArtsInfo parse : currentTasks)
					{
						if (parse.getCategoryToLoad().equals(categoryToLoad))
						{
							noCategoryInService = false;
							break;
						}
					}
					if (noCategoryInService)
					{
						//Log.e(LOG, "noCategoryInService = true");
						isLoading = false;
						swipeRef.setRefreshing(false);
					}
					else
					{
						//Log.e(LOG, "noCategoryInService = false");
					}
				}
				//handler.postDelayed(this, 3000);
			}
		};
		handler.postDelayed(r, 3000);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//Log.d(LOG_TAG + categoryToLoad, "onSaveInstanceState called");
		super.onSaveInstanceState(outState);

		outState.putFloat("topImgYCoord", this.topImg.getY());
		outState.putString("categoryToLoad", categoryToLoad);
		outState.putInt("pageToLoad", this.pageToLoad);
		outState.putInt("position", this.position);
		outState.putBoolean(KEY_IS_LOADING, isLoading);
		outState.putBoolean(KEY_IS_LOADING_FROM_TOP, isLoadingFromTop);
	}

	public void setActivatedPosition(int position)
	{
		//System.out.println("setActivatedPosition(int position: " + position);
		this.position = position;

		this.recycler.scrollToPosition(RecyclerAdapterArtsListFragment.getPositionInRecyclerView(position));
	}

	public int getMyActivatedPosition()
	{
		return this.position;
	}

	public String getCategoryToLoad()
	{
		return categoryToLoad;
	}

	public void setCategoryToLoad(String categoryToLoad)
	{
		this.categoryToLoad = categoryToLoad;
	}

	@Override
	public void onDestroy()
	{
		// If the DownloadStateReceiver still exists, unregister it and set it to null
		if (artSelectedReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(artSelectedReceiver);
			artSelectedReceiver = null;
		}
		if (artsDataReceiver != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(artsDataReceiver);
			artsDataReceiver = null;
		}
		if (receiverArticleLoaded != null)
		{
			LocalBroadcastManager.getInstance(act).unregisterReceiver(receiverArticleLoaded);
			receiverArticleLoaded = null;
		}
		// Must always call the super method at the end.
		super.onDestroy();
	}

	public boolean isInLeftPager()
	{
		return isInLeftPager;
	}

	public void setInLeftPager(boolean isInLeftPager)
	{
		this.isInLeftPager = isInLeftPager;
	}

	protected BroadcastReceiver receiverArticleLoaded = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			//			Log.i(LOG+categoryToLoad, "receiverArticleLoaded onReceive");
			//			Log.i(LOG+categoryToLoad, intent.getStringExtra(Const.Action.ARTICLE_CHANGED));
			if (allArtsInfo == null)
			{
				Log.e(LOG + categoryToLoad, "allArtsInfo==null in onReceive");
				return;
			}
			if (intent.getParcelableExtra(Article.KEY_CURENT_ART) == null)
			{
				Log.e(LOG + categoryToLoad, "intent.getParcelableExtra(Article.KEY_CURENT_ART)==null in onReceive");
				return;
			}

			Article a = intent.getParcelableExtra(Article.KEY_CURENT_ART);

			//Log.e(LOG, a.getUrl());
			Set<String> keySet = act.getAllCatArtsInfo().keySet();
			boolean notFound = true;
			switch (intent.getStringExtra(Const.Action.ARTICLE_CHANGED))
			{
				case Const.Action.ARTICLE_READ:
					//loop through all arts in activity and update them and adapters
					try
					{
						if (allArtsInfo != null)
						{
							for (int i = 0; i < allArtsInfo.size() && notFound; i++)
							{
								Article artInList = allArtsInfo.get(i);
								if (artInList != null && artInList.getUrl().equals(a.getUrl()))
								{
									allArtsInfo.get(i).setReaden(a.isReaden());
									recyclerAdapter.updateArticle(allArtsInfo.get(i), i);
									notFound = false;
								}
							}
						}
					} catch (Exception e)
					{
						Log.i(LOG + categoryToLoad, "Catched error in ArticleChanged receiver of artsList");
					}
				break;
				case Const.Action.ARTICLE_LOADED:
					//loop through all arts in activity and update them and adapters
					try
					{
						for (String key : keySet)
						{
							ArrayList<Article> artsList = act.getAllCatArtsInfo().get(key);
							if (artsList != null && artsList.size() != 0)
							{
								notFound = true;
								for (int i = 0; i < artsList.size() && notFound; i++)
								{
									Article artInList = artsList.get(i);
									if (artInList.getUrl().equals(a.getUrl()))
									{
										if (!a.getArtText().equals(Const.EMPTY_STRING))
										{
											artsList.get(i).setArtText(a.getArtText());
										}
										//pubDate
										if (artsList.get(i).getPubDate().getTime() < a.getPubDate().getTime())
										{
											artsList.get(i).setPubDate(a.getPubDate());
										}
										//set preview
										artsList.get(i).setPreview(a.getPreview());
										notFound = false;
									}
								}
							}
						}
						notFound = true;
						if (allArtsInfo != null)
						{
							for (int i = 0; i < allArtsInfo.size() && notFound; i++)
							{
								Article artInList = allArtsInfo.get(i);
								if (artInList.getUrl().equals(a.getUrl()))
								{
									if (!a.getArtText().equals(Const.EMPTY_STRING))
									{
										allArtsInfo.get(i).setArtText(a.getArtText());
									}
									//pubDate
									if (allArtsInfo.get(i).getPubDate().getTime() < a.getPubDate().getTime())
									{
										allArtsInfo.get(i).setPubDate(a.getPubDate());
									}
									//set preview
									allArtsInfo.get(i).setPreview(a.getPreview());
									//							recyclerAdapter.notifyDataSetChanged();
									recyclerAdapter.updateArticle(allArtsInfo.get(i), i);
									notFound = false;
								}
							}
						}
					} catch (Exception e)
					{
						Log.i(LOG + categoryToLoad, "Catched error in ArticleChanged receiver of artsList");
					}

				break;
				case Const.Action.ARTICLE_DELETED:
					try
					{
						//Log.e(LOG+categoryToLoad, Const.Action.ARTICLE_DELETED);
						//loop through all arts in activity and update them and adapters
						for (String key : keySet)
						{
							ArrayList<Article> artsList = act.getAllCatArtsInfo().get(key);
							if (artsList != null)
							{
								notFound = true;
								for (int i = 0; i < artsList.size() && notFound; i++)
								{
									Article artInList = artsList.get(i);
									if (artInList.getUrl().equals(a.getUrl()))
									{
										artsList.get(i).setArtText(Const.EMPTY_STRING);
										artsList.get(i).setRefreshed(new Date(0));
										notFound = false;
									}
								}
							}
						}
						notFound = true;
						if (allArtsInfo != null)
						{
							for (int i = 0; i < allArtsInfo.size() && notFound; i++)
							{
								Article artInList = allArtsInfo.get(i);
								if (artInList.getUrl().equals(a.getUrl()))
								{
									//Log.e(LOG, a.getUrl());
									allArtsInfo.get(i).setArtText(Const.EMPTY_STRING);
									allArtsInfo.get(i).setRefreshed(new Date(0));
									//							recyclerAdapter.notifyDataSetChanged();
									recyclerAdapter.updateArticle(allArtsInfo.get(i), i);
									notFound = false;
								}
							}
						}
					} catch (Exception e)
					{
						Log.i(LOG + categoryToLoad, "Catched error in ArticleChanged receiver of artsList");
					}
				break;
			}
		}
	};
}