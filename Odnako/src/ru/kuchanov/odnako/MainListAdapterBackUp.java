package ru.kuchanov.odnako;

import java.io.File;
import java.util.ArrayList;

import ru.kuchanov.odnako.utils.FormatURLToFileName;
import ru.kuchanov.odnako.utils.ReadUnreadRegister;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class MainListAdapterBackUp extends BaseAdapter
{
	ActionBarActivity act;
	LayoutInflater lInflater;
	ArrayList<MainInfo> objects;
	SharedPreferences pref;

	public MainListAdapterBackUp(ActionBarActivity act, ArrayList<MainInfo> mainInfo)
	{
		System.out.println("MainListAdapter()");
		this.act = act;
		objects = mainInfo;
		lInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	// ���-�� ���������
	@Override
	public int getCount()
	{
		return objects.size();
	}

	// ������� �� �������
	@Override
	public Object getItem(int position)
	{
		return objects.get(position);
	}

	// id �� �������
	@Override
	public long getItemId(int position)
	{
		return position;
	}

	// ����� ������
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		System.out.println("getView()");
		final MainInfo p = getArticle(position);
		int type = getItemViewType(position);
		View view = null;
		switch (type)
		{
			case 0:
				ArticleHolder holderMain;
				if (convertView == null)
				{
					ViewGroup viewGroup;
					//����� ������ ��������� �� xml � ������ �������� �� ���������
					SharedPreferences pref;
					pref = PreferenceManager.getDefaultSharedPreferences(act);
					if (pref.getString("theme", "dark").equals("dark"))
					{
						viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.article_child_view_dark, null);
						holderMain = new ArticleHolder((TextView) viewGroup.findViewById(R.id.title), 
						(TextView) viewGroup.findViewById(R.id.author_name),
						(ImageView) viewGroup.findViewById(R.id.art_img), 
						(LinearLayout) viewGroup.findViewById(R.id.text_lin_lay_art), 
						(ImageView) viewGroup.findViewById(R.id.save_img), 
						(ImageView) viewGroup.findViewById(R.id.read_img));
						viewGroup.setTag(holderMain);
						view = viewGroup;
					}
					else
					{
						viewGroup = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.article_child_view_ligth, null);
						holderMain = new ArticleHolder((TextView) viewGroup.findViewById(R.id.title_ligth), 
						(TextView) viewGroup.findViewById(R.id.author_name_ligth),
						(ImageView) viewGroup.findViewById(R.id.art_img), 
						(LinearLayout) viewGroup.findViewById(R.id.text_lin_lay_art), 
						(ImageView) viewGroup.findViewById(R.id.save_img), 
						(ImageView) viewGroup.findViewById(R.id.read_img));
						viewGroup.setTag(holderMain);
						view = viewGroup;
					}
				}
				else
				{
					view = convertView;
					holderMain = (ArticleHolder) convertView.getTag();
				}
				
				//allAutors removing save read imgs
				System.out.println("allAutors removing save read imgs");
				System.out.println("allAutors removing save read imgs");
					holderMain.read.setImageDrawable(null);
					holderMain.save.setImageDrawable(null);
				////end allAutors removing save read imgs

				//Title of article
				Spanned spannedContentTitle = Html.fromHtml(p.title);
				holderMain.title.setText(spannedContentTitle);
				holderMain.linLay.setOnClickListener(new OnClickListener()
				{
					public void onClick(View arg0)
					{
						if (p.authorBlogLink.equals("allAuthors"))
						{
							act.getSupportActionBar().setTitle(p.title);
							MainActivityNew.CATEGORY_TO_LOAD = p.linkToArt;
							PullToRefreshListView pullToRefListV = (PullToRefreshListView) act.findViewById(R.id.pull_to_refresh_listview_main);
							pullToRefListV.setMode(Mode.PULL_FROM_START);
							pullToRefListV.setRefreshing();
						}
						else
						{
							Intent intent = new Intent(act, ArticleActivity.class);
							//String[] artInfo = new String[2];
							String[] artInfo = new String[3];
							artInfo[0] = p.linkToArt;
							artInfo[1] = p.title;
							/* h */artInfo[2] = p.authorName;
							MainActivityNew.CUR_ART_INFO = artInfo;
							System.out.println(artInfo[0] + artInfo[1]);
							intent.putExtra(MainActivityNew.EXTRA_MESSAGE, artInfo);
							act.startActivity(intent);
						}
					}
				});

				pref = PreferenceManager.getDefaultSharedPreferences(act);

				String scaleFactorString = pref.getString("scale", "1");
				float scaleFactor = Float.valueOf(scaleFactorString);
				holderMain.title.setTextSize(21 * scaleFactor);

				//name of author
				if (!p.authorName.equals("default"))
				{
					Spanned spannedContent = Html.fromHtml("<b>" + p.authorName + "</b>");
					holderMain.author.setText(spannedContent);
					LayoutParams layParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0, 1);
					holderMain.author.setLayoutParams(layParams);
					holderMain.author.setPadding(10, 0, 0, 0);
					holderMain.author.setGravity(Gravity.AXIS_X_SHIFT);
				}
				else if (p.authorName.equals("default"))
				{
					holderMain.author.setText(null);
					LayoutParams layParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
					holderMain.author.setLayoutParams(layParams);
				}
				holderMain.author.setTextSize(21 * scaleFactor);
				//end

				// ART_IMG
				DownloadImageTask downFlag = (DownloadImageTask) new DownloadImageTask((ImageButton) null, (ImageView) holderMain.img, (ActionBarActivity) act);
				downFlag.execute(p.img);
				holderMain.img.setPadding(1, 1, 1, 1);
				final float scale = act.getResources().getDisplayMetrics().density;
				int pixels = (int) (75 * scaleFactor * scale + 0.5f);
				holderMain.img.setScaleType(ScaleType.FIT_XY);
				LayoutParams params = new LayoutParams(pixels, pixels);
				holderMain.img.setLayoutParams(params);
				//onClickListener
				holderMain.img.setOnClickListener(new OnClickListener()
				{
					public void onClick(View arg0)
					{
						if (!p.authorBlogLink.equals("default"))
						{
							act.getSupportActionBar().setTitle(p.authorName);
							MainActivityNew.CATEGORY_TO_LOAD = p.authorBlogLink;
							PullToRefreshListView pullToRefListV = (PullToRefreshListView) act.findViewById(R.id.pull_to_refresh_listview_main);//
							pullToRefListV.setRefreshing();
						}
						else
						{
							System.out.println("p.authorBlogLink.equals('default')");
						}
					}
				});
				//end of ART_IMG

				//SaveImg
				String appDir;
				appDir = pref.getString("filesDir", "");

				String formatedCategory=FormatURLToFileName.format(MainActivityNew.CATEGORY_TO_LOAD);
				String formatedLink=FormatURLToFileName.format(p.linkToArt);

				File currentArticleFile = new File(appDir + "/" + formatedCategory + "/" + formatedLink);
				//System.out.println("Try load from file: " + currentArticleFile.getAbsolutePath());
				int pixelsForIcons = (int) (35 * scaleFactor * scale + 0.5f);
				LayoutParams paramsForIcons = new LayoutParams(pixelsForIcons, pixelsForIcons);
				
				holderMain.save.setPadding(1, 1, 1, 1);
				holderMain.save.setScaleType(ScaleType.FIT_XY);
				holderMain.save.setLayoutParams(paramsForIcons);
				
				if (currentArticleFile.exists())
				{
					SharedPreferences pref;
					pref = PreferenceManager.getDefaultSharedPreferences(act);
					if (pref.getString("theme", "dark").equals("dark"))
					{
						holderMain.save.setImageResource(R.drawable.ic_action_save_dark);
					}
					else
					{
						holderMain.save.setImageResource(R.drawable.ic_action_save_light);
					}
				}
				else
				{
					holderMain.save.setImageResource(0);
				}
				////end SaveImg
				
				//read Img
				ReadUnreadRegister read=new ReadUnreadRegister(act);
				SharedPreferences pref;
				pref = PreferenceManager.getDefaultSharedPreferences(act);
				
				holderMain.read.setPadding(1, 1, 1, 1);
				holderMain.read.setScaleType(ScaleType.FIT_XY);
				holderMain.read.setLayoutParams(paramsForIcons);
				
				if (read.check(p.linkToArt))
				{
					
					if (pref.getString("theme", "dark").equals("dark"))
					{
						holderMain.read.setImageResource(R.drawable.ic_action_read_dark);
					}
					else
					{
						holderMain.read.setImageResource(R.drawable.ic_action_read_light);
					}
				}
				else
				{
					if (pref.getString("theme", "dark").equals("dark"))
					{
						holderMain.read.setImageResource(R.drawable.ic_action_unread_dark);
					}
					else
					{
						holderMain.read.setImageResource(R.drawable.ic_action_unread_light);
					}
					
				}
				////end read Img
				//allAutors removing save read imgs
//				System.out.println("allAutors removing save read imgs");
//				if (p.authorBlogLink.equals("allAuthors"))
//				{
//					System.out.println("allAutors removing save read imgs");
//					holderMain.read.setImageDrawable(null);
//					holderMain.save.setImageDrawable(null);
//				}
				////end allAutors removing save read imgs
				return view;
			default: 
				System.out.println("default case in MainListAdapter in getView method");
				return view;
		}
	}

	@Override
	public int getItemViewType(int position)
	{
		return 0;
	}

	@Override
	public int getViewTypeCount()
	{
		return 1;
	}

	// ����� �� �������
	MainInfo getArticle(int position)
	{
		return ((MainInfo) getItem(position));
	}

	static class ArticleHolder
	{
		TextView title;
		TextView author;
		ImageView img;
		LinearLayout linLay;
		ImageView save;
		ImageView read;

		ArticleHolder(TextView title, TextView author, ImageView img, LinearLayout linLay, ImageView save, ImageView read)
		{
			this.title = title;
			this.author = author;
			this.img = img;
			this.linLay = linLay;
			this.save = save;
			this.read=read;
		}
	}
}