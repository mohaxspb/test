package ru.kuchanov.odnako.lists_and_utils;

import java.io.File;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.fragments.FragmentArtsListRecycler;
import ru.kuchanov.odnako.utils.DipToPx;
import ru.kuchanov.odnako.utils.ReadUnreadRegister;
import ru.kuchanov.odnako.utils.MyUIL;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ArtsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
	final static String TAG = ArtsListAdapter.class.getSimpleName();

	private static final int HEADER = 0;
	private static final int ARTICLE = 1;
	private static final int FOOTER = 2;

	ActionBarActivity act;
	LayoutInflater lInflater;

	RecyclerView artsListView;

	ImageLoader imageLoader;

	ArrayList<ArtInfo> artsInfo;
	ArrayList<ArtInfo> orig;
	SharedPreferences pref;

	boolean twoPane;
	boolean isInLeftPager;

	FragmentArtsListRecycler artsListFrag;

	public ArtsListAdapter(ActionBarActivity act, ArrayList<ArtInfo> artsInfo, RecyclerView artsListView,
	FragmentArtsListRecycler artsListFrag)
	{
		this.act = act;
		this.artsInfo = artsInfo;
		lInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.artsListView = artsListView;

		this.artsListFrag = artsListFrag;
		this.isInLeftPager = this.artsListFrag.isInLeftPager();

		pref = PreferenceManager.getDefaultSharedPreferences(act);
		twoPane = pref.getBoolean("twoPane", false);

		imageLoader = MyUIL.get(act);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public int getItemViewType(int position)
	{
		if (position == 0)
		{
			return HEADER;
		}
		else
		{
			return ARTICLE;
		}
	}

	@Override
	public int getItemCount()
	{
		//this for all cats and authors
		if (this.artsInfo == null)
		{
			return 1;
		}
		int header = 1;
		return this.artsInfo.size() + header;
	}

	public ArtInfo getArtInfoByPosition(int position)
	{
		return this.artsInfo.get(position - 1);
	}

	public static int getPositionInAllArtsInfo(int recyclerViewPosition)
	{
		return recyclerViewPosition - 1;
	}

	public static int getPositionInRecyclerView(int artsListPosition)
	{
		return artsListPosition + 1;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
	{
		final DisplayImageOptions options;
		if (this.pref.getString("theme", "dark").equals("dark"))
		{
			options = MyUIL.getDarkOptions();
		}
		else
		{
			options = MyUIL.getLightOptions();
		}

		switch (getItemViewType(position))
		{
			case (HEADER):

			break;
			case (FOOTER):

			break;
			case (ARTICLE):
				//catch all cat author frags and return;
				final ArtInfo p;
				try
				{
					p = this.getArtInfoByPosition(position);

					//				final ArtInfo p = this.getArtInfoByPosition(position);
					final int positionInAllArtsInfo = ArtsListAdapter.getPositionInAllArtsInfo(position);

					final ArticleHolder holderMain = (ArticleHolder) holder;

					//variables for scaling text and icons and images from settings
					String scaleFactorString = pref.getString("scale", "1");
					float scaleFactor = Float.valueOf(scaleFactorString);

					final float scale = act.getResources().getDisplayMetrics().density;
					int pixels = (int) (75 * scaleFactor * scale + 0.5f);
					////End of variables for scaling text and icons and images from settings

					//light checked item in listView
					ViewGroup vg = (ViewGroup) holderMain.card.getParent();
					if (this.twoPane && isInLeftPager)
					{

						if (artsListFrag.getMyActivatedPosition() == positionInAllArtsInfo)
						{
							vg.setBackgroundColor(act.getResources().getColor(R.color.blue));
						}
						else
						{
							vg.setBackgroundColor(Color.TRANSPARENT);
						}
					}
					else
					{
						vg.setBackgroundColor(Color.TRANSPARENT);
					}

					////////
					// ART_IMG
					if (!p.img_art.equals("empty") && !p.img_art.contains("/75_75/"))
					{
						LayoutParams params = (LayoutParams) holderMain.art_img.getLayoutParams();
						params.height = (int) DipToPx.convert(120, act);
						holderMain.art_img.setLayoutParams(params);
						String HDimgURL = p.img_art.replace("/120_72/", "/450_240/");

						imageLoader.displayImage(HDimgURL, holderMain.art_img, options,
						new ImageLoadingListener()
						{
							@Override
							public void onLoadingStarted(String imageUri, View view)
							{
								//							        ...
							}

							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
							{
								//							        ...
							}

							@Override
							public void onLoadingCancelled(String imageUri, View view)
							{
								//							        ...
							}

							@Override
							public void onLoadingFailed(String imageUri, View arg1, FailReason arg2)
							{
								String newURL = imageUri.replace("/450_240/", "/120_72/");
								imageLoader.displayImage(newURL, holderMain.art_img,
								options);
							}
						}, new ImageLoadingProgressListener()
						{
							@Override
							public void onProgressUpdate(String imageUri, View view, int current, int total)
							{
								//							        ...
							}
						});
					}
					else
					{
						LayoutParams params = (LayoutParams) holderMain.art_img.getLayoutParams();
						params.height = 0;
						holderMain.art_img.setLayoutParams(params);
					}
					//end of ART_IMG

					//Title of article
					Spanned spannedContentTitle = Html.fromHtml(p.title);
					holderMain.title.setText(spannedContentTitle);

					holderMain.top_lin_lay.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.showArticle(artsInfo, positionInAllArtsInfo, act);
						}
					});

					holderMain.title.setTextSize(21 * scaleFactor);

					//Date
					if (!p.pubDate.equals("empty"))
					{
						holderMain.date.setText(p.pubDate);
						holderMain.date.setTextSize(19 * scaleFactor);
					}
					else
					{
						holderMain.date.setText("date is empty; Must hide on relize");
						holderMain.date.setTextSize(19 * scaleFactor);
					}
					////End of Date

					//popUp menu in cardView
					holderMain.settings.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							PopupMenu popup = new PopupMenu(act, v);
							popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
							{

								public boolean onMenuItemClick(MenuItem item)
								{
									switch (item.getItemId())
									{
										case R.id.mark_as_read:
											Actions.markAsRead(p.url, act);
											return true;
										case R.id.share_link:
											Actions.shareUrl(p.url, act);
											return true;
										case R.id.show_comments:
											Actions.showComments(artsInfo, positionInAllArtsInfo, act);
											return true;
										default:
											return false;
									}
								}
							});
							MenuInflater inflater = popup.getMenuInflater();
							inflater.inflate(R.menu.art_card_menu_ligth, popup.getMenu());
							popup.show();
						}

					});
					////End of popUp menu in cardView

					//preview
					if (!p.preview.equals("empty"))
					{
						Spanned spannedContentPreview = Html.fromHtml(p.preview);
						holderMain.preview.setText(spannedContentPreview);
						holderMain.preview.setTextSize(21 * scaleFactor);
					}
					else
					{
						//holderMain.preview.setText("preview is empty; Must hide on relize");
						holderMain.preview.setTextSize(21 * scaleFactor);
					}

					//				Spanned spannedContentPreview = Html.fromHtml(p.preview);
					//				holderMain.preview.setText(spannedContentPreview);
					//
					//				holderMain.preview.setTextSize(21 * scaleFactor);
					////end of preview

					//name and image of author
					if (!p.img_art.equals("empty") && p.img_art.contains("/75_75/"))
					{
						LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
						params.height = pixels;
						params.width = pixels;
						holderMain.author_img.setLayoutParams(params);

						this.imageLoader.displayImage(p.img_art, holderMain.author_img,
						MyUIL.getTransparentBackgroundROUNDOptions(act));
					}
					else if (!p.img_author.equals("empty"))
					{
						LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
						params.height = pixels;
						params.width = pixels;
						holderMain.author_img.setLayoutParams(params);

						this.imageLoader.displayImage(p.img_author, holderMain.author_img,
						MyUIL.getTransparentBackgroundROUNDOptions(act));
					}
					else
					{
						LayoutParams params = (LayoutParams) holderMain.author_img.getLayoutParams();
						params.height = 0;
						params.width = 0;
						holderMain.author_img.setLayoutParams(params);
					}
					if (!p.authorName.equals("empty"))
					{
						LayoutParams params = (LayoutParams) holderMain.author_name.getLayoutParams();
						params.height = LayoutParams.WRAP_CONTENT;
						params.width = 0;
						Spanned spannedContent = Html.fromHtml("<b>" + p.authorName + "</b>");
						holderMain.author_name.setText(spannedContent);
						holderMain.author_name.setTextSize(21 * scaleFactor);
					}
					else
					{
						holderMain.author_name.setText(null);
						LayoutParams params = (LayoutParams) holderMain.author_name.getLayoutParams();
						params.height = 0;
						params.width = 0;
						holderMain.author_name.setLayoutParams(params);
					}

					holderMain.author_lin.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							Actions.showAllAuthorsArticles(p.authorBlogUrl, act);
						}
					});
					//end of name of author

					//SaveImg
					String appDir;
					appDir = pref.getString("filesDir", "");

					String formatedCategory;
					//				formatedCategory = MainActivityNew.CATEGORY_TO_LOAD.replace("-", "_");
					String TO_DELETE = "odnako.org/blogs";
					formatedCategory = TO_DELETE.replace("-", "_");
					formatedCategory = formatedCategory.replace("/", "_");
					formatedCategory = formatedCategory.replace(":", "_");
					formatedCategory = formatedCategory.replace(".", "_");

					String formatedLink;
					formatedLink = p.url.replace("-", "_");
					formatedLink = formatedLink.replace("/", "_");
					formatedLink = formatedLink.replace(":", "_");
					formatedLink = formatedLink.replace(".", "_");

					File currentArticleFile = new File(appDir + "/" + formatedCategory + "/"
					+ formatedLink);
					//System.out.println("Try load from file: " + currentArticleFile.getAbsolutePath());
					//					LayoutParams paramsForIcons = new LayoutParams((int) DipToPx.convert(25, act),
					//					(int) DipToPx.convert(25, act));
					//					paramsForIcons.setMargins(5, 5, 5, 5);

					holderMain.save.setScaleType(ScaleType.FIT_XY);
					//					holderMain.save.setLayoutParams(paramsForIcons);

					//TODO
					if (currentArticleFile.exists())
					{

					}
					if (p.url != null)
					{
						if (pref.getString("theme", "dark").equals("dark"))
						{
							holderMain.save.setImageResource(R.drawable.ic_save_white_48dp);
						}
						else
						{
							holderMain.save.setImageResource(R.drawable.ic_save_grey600_48dp);
						}

					}
					else
					{
						if (pref.getString("theme", "dark").equals("dark"))
						{
							holderMain.save.setImageResource(android.R.color.transparent);
						}
						else
						{
							holderMain.save.setImageResource(android.R.color.transparent);
						}
					}
					////end SaveImg

					//read Img
					//TODO
					ReadUnreadRegister read = new ReadUnreadRegister(act);
					//					holderMain.read.setLayoutParams(paramsForIcons);

					if (read.check(p.url))
					{
						if (pref.getString("theme", "dark").equals("dark"))
						{
							holderMain.read.setImageResource(R.drawable.ic_drafts_white_48dp);
						}
						else
						{
							holderMain.read.setImageResource(R.drawable.ic_drafts_grey600_48dp);
						}
					}
					else
					{
						if (pref.getString("theme", "dark").equals("dark"))
						{
							holderMain.read.setImageResource(R.drawable.ic_markunread_white_48dp);
						}
						else
						{
							holderMain.read.setImageResource(R.drawable.ic_markunread_grey600_48dp);
						}
					}
					////end read Img

					//share btn
					//					holderMain.share.setLayoutParams(paramsForIcons);
					holderMain.share.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.shareUrl(p.url, act);
						}
					});
					holderMain.num_of_shares.setText(String.valueOf(p.numOfSharings));
					holderMain.num_of_shares.setTextSize(21 * scaleFactor);
					holderMain.num_of_shares.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.shareUrl(p.url, act);
						}
					});
					////end of share btn

					//comments btn
					//					holderMain.comms.setLayoutParams(paramsForIcons);
					holderMain.comms.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.showComments(artsInfo, positionInAllArtsInfo, act);
						}
					});
					holderMain.num_of_comms.setText(String.valueOf(p.numOfComments));
					holderMain.num_of_comms.setTextSize(21 * scaleFactor);
					holderMain.num_of_comms.setOnClickListener(new OnClickListener()
					{
						public void onClick(View v)
						{
							Actions.showComments(artsInfo, positionInAllArtsInfo, act);
						}
					});
					////end of comments btn

					/////////animation
					// Here you apply the animation when the view is bound
					//					setAnimation(vg, position);
				} catch (Exception e)
				{
					return;
				}

			break;

		}

	}

	/**
	 * Here is the key method to apply the animation
	 */
	protected void setAnimation(View viewToAnimate, int position)
	{
		// If the bound view wasn't previously displayed on screen, it's animated
		//		if (position > lastPosition)
		//		{
		Animation animation = AnimationUtils.loadAnimation(this.act, android.R.anim.slide_in_left);
		//        	Animation animation = AnimationUtils.loadAnimation(this.act, android.R.anim.fade_in);
		viewToAnimate.startAnimation(animation);
		//			lastPosition = position;
		//		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position)
	{
		View itemLayoutView = null;
		RecyclerView.ViewHolder holder = null;
		switch (position)
		{
			case (HEADER):
				itemLayoutView = new LinearLayout(act);
				itemLayoutView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) DipToPx.convert(165,
				act)));

				holder = new HeaderHolder(itemLayoutView);
				return holder;

			case (ARTICLE):
				// create a new view
				itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card,
				parent,
				false);

				// create ViewHolder
				holder = new ArticleHolder(itemLayoutView);

				return holder;
			default:
				return holder;
		}
	}

	static class HeaderHolder extends RecyclerView.ViewHolder
	{

		HeaderHolder(View itemLayoutView)
		{
			super(itemLayoutView);
		}
	}

	static class ArticleHolder extends RecyclerView.ViewHolder
	{
		TextView title;
		TextView author_name;
		ImageView author_img;
		ViewGroup author_lin;
		ImageView art_img;
		ImageView save;
		ImageView read;
		ImageView comms;
		ImageView share;
		TextView num_of_comms;
		TextView num_of_shares;
		TextView date;
		TextView preview;
		ImageView settings;
		ViewGroup top_lin_lay;
		CardView card;

		ArticleHolder(View itemLayoutView)
		{
			super(itemLayoutView);
			//			top panel
			this.top_lin_lay = (ViewGroup) itemLayoutView.findViewById(R.id.art_card_top_lin);
			this.art_img = (ImageView) itemLayoutView.findViewById(R.id.art_card_img);
			this.title = (TextView) itemLayoutView.findViewById(R.id.art_card_title_tv);
			this.date = (TextView) itemLayoutView.findViewById(R.id.art_card_date_tv);
			this.settings = (ImageView) itemLayoutView.findViewById(R.id.art_card_settings);
			this.preview = (TextView) itemLayoutView.findViewById(R.id.art_card_preview_tv);
			//author
			this.author_name = (TextView) itemLayoutView.findViewById(R.id.author_name);
			this.author_img = (ImageView) itemLayoutView.findViewById(R.id.art_card_author_img);
			this.author_lin = (ViewGroup) itemLayoutView.findViewById(R.id.art_card_author_lin);
			//bottom panel
			this.save = (ImageView) itemLayoutView.findViewById(R.id.save_img);
			this.read = (ImageView) itemLayoutView.findViewById(R.id.read_img);
			this.comms = (ImageView) itemLayoutView.findViewById(R.id.comments_img);
			this.share = (ImageView) itemLayoutView.findViewById(R.id.share_img);
			this.num_of_comms = (TextView) itemLayoutView.findViewById(R.id.num_of_comms);
			this.num_of_shares = (TextView) itemLayoutView.findViewById(R.id.num_of_sharings);

			this.card = (CardView) itemLayoutView.findViewById(R.id.cardView);
		}
	}

	///////
	//	public ArrayList<ArtInfo> getAllArtsInfo()
	//	{
	//		return this.artsInfo;
	//	}
}