/*
 19.10.2014
ArticlesListFragment.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.activities.ActivityMain;
import ru.kuchanov.odnako.lists_and_utils.ArtInfo;
import ru.kuchanov.odnako.lists_and_utils.ArtsListAdapter;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class ArticleFragment extends Fragment
{
	private ActionBarActivity act;
	private SharedPreferences pref;
	private boolean twoPane;

	private TextView artTextView;

	private ScrollView scroll;

	private TextView artTitleTV;
	private TextView artAuthorTV;
	private TextView artAuthorDescription;

	private ImageView artAuthorIV;
	private ImageView artAuthorDescriptionIV;
	private ImageView artAuthorArticlesIV;

	LinearLayout bottomPanel;
	CardView shareCard;
	CardView commentsBottomBtn;
	CardView allTegsCard;
	CardView alsoByThemeCard;
	CardView alsoToReadCard;

	ArtInfo curArtInfo;
	int position;/*position in all art arr; need to show next/previous arts*/
	ArrayList<ArtInfo> allArtsInfo;

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);
		System.out.println("ArticleFragment onCreate");

		this.act = (ActionBarActivity) this.getActivity();

//		this.curArtInfo = ActivityMain.getCUR_ART_INFO();
		this.curArtInfo=new ArtInfo(this.getArguments().getStringArray("curArtInfo"));
		this.position=this.getArguments().getInt("position");
		//restore AllArtsInfo
		this.allArtsInfo=new ArrayList<ArtInfo>();
		Set<String> keySet=this.getArguments().keySet();
		ArrayList<String> keySetSortedArrList = new ArrayList<String>(keySet);
		Collections.sort(keySetSortedArrList);
		for(int i=0; i<keySetSortedArrList.size(); i++)
		{
			if(keySetSortedArrList.get(i).startsWith("allArtsInfo_"))
			{
				if(i<10)
				{
					this.allArtsInfo.add(new ArtInfo(this.getArguments().getStringArray("allArtsInfo_0"+String.valueOf(i))));
				}
				else
				{
					this.allArtsInfo.add(new ArtInfo(this.getArguments().getStringArray("allArtsInfo_"+String.valueOf(i))));
				}
				
			}
			else
			{
				break;
			}
		}
		
		pref = PreferenceManager.getDefaultSharedPreferences(act);
		this.twoPane=pref.getBoolean("twoPane", false);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		System.out.println("ArticleFragment onCreateView");
		View v = inflater.inflate(R.layout.fragment_art, container, false);

		//find all views
		this.artTextView = (TextView) v.findViewById(R.id.art_text);
		this.artTextView.setText(R.string.version_history);

		this.scroll = (ScrollView) v.findViewById(R.id.art_scroll);

		this.artTitleTV = (TextView) v.findViewById(R.id.art_title);
		this.artAuthorTV = (TextView) v.findViewById(R.id.art_author);
		this.artAuthorDescription = (TextView) v.findViewById(R.id.art_author_description);

		this.artAuthorIV = (ImageView) v.findViewById(R.id.art_author_img);
		this.artAuthorArticlesIV = (ImageView) v.findViewById(R.id.art_author_all_arts_btn);
		this.artAuthorDescriptionIV = (ImageView) v.findViewById(R.id.art_author_description_btn);

		this.bottomPanel = (LinearLayout) v.findViewById(R.id.art_bottom_panel);
		//inflate bottom panels
		DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels;
		if (width < 800)
		{
			this.shareCard = (CardView) inflater.inflate(R.layout.share_panel, bottomPanel, false);
			this.bottomPanel.addView(this.shareCard);
		}
		else
		{
			this.shareCard = (CardView) inflater.inflate(R.layout.share_panel_landscape, bottomPanel, false);
			this.bottomPanel.addView(this.shareCard);
		}

		this.commentsBottomBtn = (CardView) inflater.inflate(R.layout.comments_bottom_btn_layout, bottomPanel, false);
		//set onClickListener
		this.commentsBottomBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(twoPane)
				{
					ArticlesListFragment atsListFrag=(ArticlesListFragment) act.getSupportFragmentManager().findFragmentById(R.id.articles_list);
					
					ArtsListAdapter.showComments(curArtInfo, atsListFrag.getMyActivatedPosition(), act);
				}
				else
				{
					ArtsListAdapter.showComments(curArtInfo, -1, act);
				}
			}
		});
		this.bottomPanel.addView(this.commentsBottomBtn);

		this.allTegsCard = (CardView) inflater.inflate(R.layout.all_tegs_layout, bottomPanel, false);
		this.alsoByThemeCard = (CardView) inflater.inflate(R.layout.also_by_theme, bottomPanel, false);
		this.alsoToReadCard = (CardView) inflater.inflate(R.layout.also_to_read, bottomPanel, false);

		//test
//		ArtInfo artInfoTEST = new ArtInfo("http://www.odnako.org/blogs/cifrovoy-front-latviyskiy-blickrig-i-nash-otvet/", "Заголовок статьи", "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg",
//		"http://yuriykuchanov.odnako.org/", "Разработчик");
//		artInfoTEST.updateArtInfoFromRSS(act.getResources().getString(R.string.preview), "1 сентября 1939");
//		artInfoTEST.updateArtInfoFromARTICLE(0, 0, act.getResources().getString(R.string.version_history), "Описание автора", "Интернет", "Интернет !!!! Андроид !!!! ещё тег",
//		"10 !!!! 10 !!!! 10 !!!! 10 !!!! 10 !!!! 10", "url !!!! title !!!! date !!!! url !!!! title !!!! date !!!! url !!!! title !!!! date", "url !!!! title !!!! date !!!! url !!!! title !!!! date");
//		this.curArtInfo = artInfoTEST;

		this.setUpAllTegsLayout(v);
		this.setUpAlsoByTheme();
		this.setUpAlsoToRead();
		//end of find all views


		//setting size of Images and text
		this.setSizeAndTheme();
		//End of setting size of Images and text

		//scroll to previous position
		if (savedInstanceState != null && savedInstanceState.keySet().contains("ARTICLE_SCROLL_POSITION"))
		{
			final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
			if (position != null)
			{
				if (position != null)
				{
					scroll.post(new Runnable()
					{
						public void run()
						{
							scroll.scrollTo(position[0], position[1]);
						}
					});
				}
			}
		}

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		System.out.println("ArticleFragment onViewCreated");
		super.onViewCreated(view, savedInstanceState);

	}

	private void setUpAllTegsLayout(View v)
	{
		String[] allTegs = this.curArtInfo.getAllTegsArr();
		//allTegs = new String[] { "ddddddddddddddddddddhhhhhhhhhhhhhhfdhfjgfjfgdddddddddddddddd", "jhdjsdhjsdh", "jhddddddddddddddddjsdhjsdh", "jhdjsdhjsdh", "jhdjsdhjsdh", "jhdjsdhjsdh", "jhdjsdhjsdh", "jhdjsdhjsdh", "jhdjsdhjsdh" };
		if (allTegs == null)
		{
			return;
		}
		this.bottomPanel.addView(this.allTegsCard);
		LinearLayout allTegsLin = (LinearLayout) allTegsCard.findViewById(R.id.all_tegs_lin);
		LinearLayout firstLin = (LinearLayout) allTegsCard.findViewById(R.id.first_tegs_lin);
		LayoutInflater inflater = act.getLayoutInflater();
		int curLinId = 0;
		LinearLayout curLinLay = firstLin;

		//max width
		int width;
		DisplayMetrics displayMetrics = act.getResources().getDisplayMetrics();
		if(this.twoPane)
		{
			width = displayMetrics.widthPixels / 4 * 3;
		}
		else
		{
			width=displayMetrics.widthPixels;
		}
		
		int vPad=v.getPaddingLeft()+v.getPaddingRight();
		int bPad=this.bottomPanel.getPaddingLeft()+this.bottomPanel.getPaddingRight();
		int cPad=this.allTegsCard.getPaddingLeft()+this.allTegsCard.getPaddingRight();
		int minusPaddings=vPad+bPad+cPad;
		System.out.println("width: " + width);
		System.out.println("minusPaddings: "+minusPaddings);
		width-=minusPaddings;
//		System.out.println("minusPaddings: "+minusPaddings);
		int minHeight = 0;
//		System.out.println("width: " + width);
		//
		for (int i = 0; i < allTegs.length; i++)
		{
			
			CardView c = (CardView) inflater.inflate(R.layout.teg_card, curLinLay, false);
			TextView tag = (TextView) c.findViewById(R.id.teg_tv);
			tag.setText(allTegs[i]);
			curLinLay.addView(c);
			
			
			//calculate total linLay width
			int curLinChildrenWidth = 0;
			
			for (int u = 0; u < curLinLay.getChildCount(); u++)
			{
				curLinLay.getChildAt(u).measure(0, 0);
				curLinChildrenWidth += curLinLay.getChildAt(u).getMeasuredWidth();
			}
			//plus 10*2 (2xpaddings of each tag
			curLinChildrenWidth+=curLinLay.getChildCount()*10;
			if(i==0)
			{
				curLinLay.getChildAt(1).measure(0, 0);
				minHeight=curLinLay.getChildAt(1).getMeasuredHeight();
			}
			//curLinLay.getChildAt(curLinLay.getChildCount()-1).measure(0, 0);
			int height=curLinLay.getChildAt(curLinLay.getChildCount()-1).getMeasuredHeight();
			//check if it's too much
			//must check not device, but View width
			//so if it's planshet we must take only 3/4 of device width
			System.out.println("curLinChildrenWidth: " + curLinChildrenWidth+"/ width: " + width);
			System.out.println("height: " + height+"/ minHeight: " + minHeight);
			
			if (curLinChildrenWidth >= width || height> minHeight)
			{
				curLinId++;
				LinearLayout nextLin = new LinearLayout(act);
				nextLin.setOrientation(LinearLayout.HORIZONTAL);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				nextLin.setLayoutParams(params);
				nextLin.setId(curLinId);

				//remove previous and add it to next
				curLinLay.removeView(c);
				curLinLay = nextLin;
				curLinLay.addView(c);

				allTegsLin.addView(curLinLay);
			}
		}
	}

	private void setUpAlsoByTheme()
	{
		ArtInfo.AlsoToRead alsoToRead = this.curArtInfo.getAlsoByTheme();
		//for test
//		String[] s1 = new String[] { "title", "title" };
//		String[] s2 = new String[] { "title", "title" };
//		String[] s3 = new String[] { "title", "title" };
//		alsoToRead = this.curArtInfo.new AlsoToRead(s1, s2, s3);
		if (alsoToRead == null)
		{
			return;
		}

		this.bottomPanel.addView(this.alsoByThemeCard);
		LinearLayout mainLin = (LinearLayout) this.alsoByThemeCard.findViewById(R.id.also_main);
		LayoutInflater inflater = act.getLayoutInflater();
		for (int i = 0; i < alsoToRead.titles.length; i++)
		{
			CardView c = (CardView) inflater.inflate(R.layout.also_to_read_art_lay, mainLin, false);
			TextView title = (TextView) c.findViewById(R.id.title);
			title.setText(alsoToRead.titles[i]);
			TextView date = (TextView) c.findViewById(R.id.date);
			date.setText(alsoToRead.dates[i]);
			mainLin.addView(c);
		}
	}

	private void setUpAlsoToRead()
	{
		ArtInfo.AlsoToRead alsoToRead = this.curArtInfo.getAlsoToReadMore();
		//for test
//		String[] s1 = new String[] { "title", "title", "title" };
//		String[] s2 = new String[] { "url", "url", "url" };
//		String[] s3 = new String[] { "date", "date", "date" };
//		alsoToRead = this.curArtInfo.new AlsoToRead(s1, s2, s3);
		if (alsoToRead == null)
		{
			return;
		}

		this.bottomPanel.addView(this.alsoToReadCard);
		LinearLayout mainLin = (LinearLayout) this.alsoToReadCard.findViewById(R.id.also_main);
		LayoutInflater inflater = act.getLayoutInflater();
		for (int i = 0; i < alsoToRead.titles.length; i++)
		{
			CardView c = (CardView) inflater.inflate(R.layout.also_to_read_art_lay, mainLin, false);
			TextView title = (TextView) c.findViewById(R.id.title);
			title.setText(alsoToRead.titles[i]);
			TextView date = (TextView) c.findViewById(R.id.date);
			date.setText(alsoToRead.dates[i]);
			mainLin.addView(c);
		}
	}

	private void setSizeAndTheme()
	{
		
		String scaleFactorString = pref.getString("scale_art", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);

		this.artTextView.setTextSize(21 * scaleFactor);

		this.artTitleTV.setTextSize(25 * scaleFactor);
		this.artAuthorTV.setTextSize(21 * scaleFactor);
		this.artAuthorDescription.setTextSize(21 * scaleFactor);

		//images
		final float scale = getResources().getDisplayMetrics().density;
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);
		LayoutParams params = new LayoutParams(pixels, pixels);

		this.artAuthorIV.setPadding(5, 5, 5, 5);
		this.artAuthorIV.setScaleType(ScaleType.FIT_XY);
		this.artAuthorIV.setLayoutParams(params);

		this.artAuthorArticlesIV.setPadding(5, 5, 5, 5);
		this.artAuthorArticlesIV.setScaleType(ScaleType.FIT_XY);
		this.artAuthorArticlesIV.setLayoutParams(params);

		this.artAuthorDescriptionIV.setPadding(5, 5, 5, 5);
		this.artAuthorDescriptionIV.setScaleType(ScaleType.FIT_XY);
		this.artAuthorDescriptionIV.setLayoutParams(params);

	}

	@Override
	public void onAttach(Activity activity)
	{
		System.out.println("ArticleFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach()
	{
		System.out.println("ArticleFragment onDetach");
		super.onDetach();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		System.out.println("ArticleFragment onSaveInstanceState");
		super.onSaveInstanceState(outState);

		//save scrollView position
		outState.putIntArray("ARTICLE_SCROLL_POSITION", new int[] { scroll.getScrollX(), scroll.getScrollY() });
	}

}
