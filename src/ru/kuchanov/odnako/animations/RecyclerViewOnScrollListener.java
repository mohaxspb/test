/*
 08.11.2014
RecyclerViewOnScrollListener.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.animations;

import ru.kuchanov.odnako.activities.ActivityMain;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

public abstract class RecyclerViewOnScrollListener extends OnScrollListener
{
	static final String LOG = RecyclerViewOnScrollListener.class.getSimpleName();

	private AppCompatActivity act;

	private String categoryToLoad;

	private int initialDistance = -100000;
	private int curentDistance = -1;

	private LinearLayoutManager manager;
	private Toolbar toolbar;
	private ImageView topImg;

	private boolean loading = true; // True if we are still waiting for the last set of data to load.
	private int previousTotal = 0; // The total number of items in the dataset after the last load
	// The minimum amount of items to have below your current scroll position before loading more.
	private int visibleThreshold = 5;
	private int firstVisibleItem, visibleItemCount, totalItemCount;

	/**
	 * 
	 */
	public RecyclerViewOnScrollListener(AppCompatActivity act, String categoryToLoad, ImageView topImg, int toolbarId)
	{
		this.act = act;
		this.toolbar = (Toolbar) act.findViewById(toolbarId);
		this.topImg = topImg;
		this.categoryToLoad = categoryToLoad;
	}

	public void onScrollStateChanged(RecyclerView recyclerView, int newState)
	{
		manager = (LinearLayoutManager) recyclerView.getLayoutManager();

		switch (newState)
		{
			case (RecyclerView.SCROLL_STATE_DRAGGING):
				//mesuring initialDistance between actionBar and 1-st item
				if (initialDistance == -100000)
				{
					if (manager.findFirstVisibleItemPosition() == 0)
					{
						try
						{
							initialDistance = (int) (manager.findViewByPosition(1).getY() - toolbar.getHeight());
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			break;
			//scroll finished
			case (RecyclerView.SCROLL_STATE_IDLE):
				if (topImg.getY() > 0)
				{
					topImg.setY(0);
				}
				if (manager.findFirstVisibleItemPosition() == 0)
				{
					if (manager.findViewByPosition(0).getY() == 0)
					{
						topImg.setY(0);
					}
				}
				//save position to frag
				//save coord to ActivityMain
				ActivityMain actM = (ActivityMain) act;
				actM.updateAllCatToolbarTopImgYCoord(this.categoryToLoad,
				new int[] { (int) toolbar.getY(), (int) topImg.getY(), initialDistance, curentDistance });
			break;
			case (RecyclerView.SCROLL_STATE_SETTLING):
			break;
		}
	}

	public int getStatusBarHeight()
	{
		int result = 0;
		int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0)
		{
			result = act.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	@Override
	public void onScrolled(RecyclerView recyclerView, int x, int y)
	{
		manager = (LinearLayoutManager) recyclerView.getLayoutManager();

		visibleItemCount = manager.getChildCount();
		totalItemCount = manager.getItemCount();
		firstVisibleItem = manager.findFirstVisibleItemPosition();

		if (loading)
		{
			if (totalItemCount > previousTotal)
			{
				loading = false;
				previousTotal = totalItemCount;
			}
		}
		if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold))
		{
			// End has been reached
			//check if totaItemCount -1 (cause of header) a multiple of 30
			if ((totalItemCount - 1) % 30 == 0)
			{
				// TODO if so we can load more from bottom
				//CHECK here situation when total quont of arts on are multiple of 30
				//to prevent a lot of requests
				onLoadMore();
				loading = true;
			}
			else
			{
				//if so, we have reached onSiteVeryBottomOfArtsList
				//so we do not need to start download
			}
		}

		//////////////

		boolean scrollToUp = y > 0;
		//move picture
		if (scrollToUp)
		{
			if (topImg.getY() + topImg.getHeight() > 0)
			{
				topImg.setY(topImg.getY() - y / 2);
			}
			else
			{
				topImg.setY(-topImg.getHeight());
			}
		}
		else
		{
			if (manager.findFirstVisibleItemPosition() <= 1)
			{
				if (topImg.getY() < 0)
				{
					topImg.setY(topImg.getY() - y / 2);
				}
				else if (topImg.getY() > 0)
				{
					topImg.setY(0);
				}

			}
		}
		////End of move picture

		//as we have translusent status bar we must plus statusBar height to toolbar's Ycoord
		//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		//				{
		//					toolbar.setY(this.toolbar.getY() + getStatusBarHeight());
		//				}

		//move light actionBar
		if (scrollToUp)
		{
			//move actionBar UP
			//on the very top of list
			if (manager.findFirstVisibleItemPosition() == 0)
			{
				if (manager.findViewByPosition(1).getY() < toolbar.getHeight())
				{
					if (toolbar.getY() > -toolbar.getHeight())
					{
						toolbar.setY(toolbar.getY() - y);
					}
					else
					{
						toolbar.setY(-toolbar.getHeight());
					}
				}
			}
			//from any other position
			else
			{
				if (toolbar.getY() > -toolbar.getHeight())
				{
					toolbar.setY(toolbar.getY() - y);
				}
				else
				{
					toolbar.setY(-toolbar.getHeight());
				}
			}
			//UNlight actionBar UP
			//do it only while it's not moved
			//and we are on the top of our list
			//					System.out.println("firstVisPos ==0: "+String.valueOf(manager.findFirstVisibleItemPosition()==0));
//			Log.i(LOG, "scroll to up");
//			Log.e(LOG, "toolbars alpha: " + toolbar.getBackground().getAlpha());
//			int[] textSizeAttr = new int[] { ru.kuchanov.odnako.R.attr.colorPrimary };
//			int indexOfAttrTextSize = 0;
//			TypedValue typedValue = new TypedValue();
//			TypedArray a = act.obtainStyledAttributes(typedValue.data, textSizeAttr);
//			int colorPrimary = a.getColor(indexOfAttrTextSize, 0xFFFFFF);
//			a.recycle();
//			int alpha = Color.alpha(colorPrimary);
//			Log.d(LOG, "colorPrimary alpha: " + alpha);
			if (toolbar.getY() == 0)
			{
				if (manager.findFirstVisibleItemPosition() == 0)
				{
					curentDistance = (int) (manager.findViewByPosition(1).getY() - toolbar.getHeight());
					float percent = (float) this.curentDistance / (float) this.initialDistance;
					float gradient = 1f - percent;
					int newAlpha = (int) (255 * gradient);
					toolbar.getBackground().setAlpha(newAlpha);
				}
				else
				{
					toolbar.getBackground().setAlpha(255);
				}
			}
		}
		else
		{
			//if scroll to bottom
			//move actionBar
			if (toolbar.getY() < 0)
			{
				toolbar.setY(toolbar.getY() - y);
			}
			else
			{
				//as we have translusent status bar we must plus statusBar height to toolbar's Ycoord
				//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
				//				{
				////					toolbar.setY(this.toolbar.getY() + getStatusBarHeight());
				//					toolbar.setY(0 + getStatusBarHeight());
				//				}else
				//				{
				toolbar.setY(0);
				//				}

			}
			//light actionBar
//			Log.i(LOG, "scroll to bottom");
//			Log.e(LOG, "toolbars alpha: " + toolbar.getBackground().getAlpha());
//			int[] textSizeAttr = new int[] { ru.kuchanov.odnako.R.attr.colorPrimary };
//			int indexOfAttrTextSize = 0;
//			TypedValue typedValue = new TypedValue();
//			TypedArray a = act.obtainStyledAttributes(typedValue.data, textSizeAttr);
//			int colorPrimary = a.getColor(indexOfAttrTextSize, 0xFFFFFF);
//			a.recycle();
//			int alpha = Color.alpha(colorPrimary);
//			Log.d(LOG, "colorPrimary alpha: " + alpha);
			if (toolbar.getY() == 0)
			{
				if (manager.findFirstVisibleItemPosition() == 0)
				{
					curentDistance = (int) (manager.findViewByPosition(1).getY() - toolbar.getHeight());
					float percent = (float) this.curentDistance / (float) this.initialDistance;
					if (percent > 0)
					{
						float gradient = 1f - percent;
						int newAlpha = (int) (255 * gradient);
						toolbar.getBackground().setAlpha(newAlpha);
					}
				}
				else
				{
					toolbar.getBackground().setAlpha(255);
				}
			}
		}
	}

	public abstract void onLoadMore();
}