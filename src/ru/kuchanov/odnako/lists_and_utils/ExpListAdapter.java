package ru.kuchanov.odnako.lists_and_utils;

import java.util.ArrayList;

import ru.kuchanov.odnako.R;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpListAdapter extends BaseExpandableListAdapter
{

	private ArrayList<ArrayList<String>> mGroups;
	ActionBarActivity act;
	String[] cat;
	SharedPreferences pref;
	private Drawable drawableArrrowDown;
	Drawable drawableArrrowLeft;
	private Drawable drawableArrrowRight;
	private Drawable drawableDownload;
	private Drawable drawableSettings;
	private Drawable drawableAsList;
	private Drawable drawableSubject;
	private Drawable drawableCategoriesMore;
	private Drawable drawableAuthor;
	private Drawable drawableAuthorsMore;
	private Drawable drawableArrowUp;

	public ExpListAdapter(ActionBarActivity act, ArrayList<ArrayList<String>> groups)
	{
		this.act = act;
		pref = PreferenceManager.getDefaultSharedPreferences(this.act);
		mGroups = groups;
		cat = this.act.getResources().getStringArray(R.array.menu_items);

		this.setThemeDependedDrawables();
	}

	private void setThemeDependedDrawables()
	{
		//set arrowDownIcon by theme
		int[] attrs = new int[] { R.attr.arrowDownIcon };
		TypedArray ta = this.act.obtainStyledAttributes(attrs);
		drawableArrrowDown = ta.getDrawable(0);
		ta.recycle();
		//set arrowDownIcon by theme
		attrs = new int[] { R.attr.arrowUpIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableArrowUp = ta.getDrawable(0);
		ta.recycle();
		//set arrowLeftIcon by theme
		attrs = new int[] { R.attr.arrowLeftIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableArrrowLeft = ta.getDrawable(0);
		ta.recycle();
		//set arrowRightIcon by theme
		attrs = new int[] { R.attr.arrowRightIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableArrrowRight = ta.getDrawable(0);
		ta.recycle();
		//set downloadIcon by theme
		attrs = new int[] { R.attr.downloadIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableDownload = ta.getDrawable(0);
		ta.recycle();
		//set settingsIcon by theme
		attrs = new int[] { R.attr.settingsIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableSettings = ta.getDrawable(0);
		ta.recycle();
		//set asListIcon by theme
		attrs = new int[] { R.attr.asListIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableAsList = ta.getDrawable(0);
		ta.recycle();
		//set subjectIcon by theme
		attrs = new int[] { R.attr.subjectIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableSubject = ta.getDrawable(0);
		ta.recycle();
		//set categoriesMoreIcon by theme
		attrs = new int[] { R.attr.categoriesMoreIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableCategoriesMore = ta.getDrawable(0);
		ta.recycle();
		//set personIcon by theme
		attrs = new int[] { R.attr.authorIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableAuthor = ta.getDrawable(0);
		ta.recycle();
		//set authorsMoreIcon by theme
		attrs = new int[] { R.attr.authorsMoreIcon };
		ta = this.act.obtainStyledAttributes(attrs);
		drawableAuthorsMore = ta.getDrawable(0);
		ta.recycle();
	}

	@Override
	public int getGroupCount()
	{
		return mGroups.size();
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return mGroups.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return mGroups.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		return mGroups.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		View view = null;
		MyHolder holderMain;
		if (convertView == null)
		{
			ViewGroup vg;
			vg = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.drawer_group, parent, false);
			holderMain = new MyHolder((ImageView) vg.findViewById(R.id.img_left),
			(TextView) vg.findViewById(R.id.text), (ImageView) vg.findViewById(R.id.img_right));
			vg.setTag(holderMain);
			view = vg;
		}
		else
		{
			view = convertView;
			holderMain = (MyHolder) convertView.getTag();
		}
		///light checked item
		//		if (((ExpandableListView)parent).getch.getCheckedItemPosition()==groupPosition)
		//		if (((ExpandableListView)parent).getCheckedItemPositions().==groupPosition)
		//		{
		//			view.setBackgroundColor(act.getResources().getColor(R.color.blue));
		//		}
		//		else
		//		{
		//			view.setBackgroundColor(Color.TRANSPARENT);
		//		}
		SparseBooleanArray a = ((ExpandableListView) parent).getCheckedItemPositions();

		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < a.size(); i++)
		{

			if (a.valueAt(i))
			{
				int idx = a.keyAt(i);
				//	 
				//	            if (sb.length() > 0)
				//	                sb.append(", ");
				//	 
				//	 
				//	            String s = (String)this.getListView().getAdapter().getItem(idx);
				//	            sb.append(s);
//				if()
			}
		}
		///////

		holderMain.text.setText(this.cat[groupPosition]);

		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		holderMain.text.setTextSize(21 * scaleFactor);

		switch (groupPosition)
		{
		//authors
			case 0:
				// Create an array of the attributes we want to resolve
				// using values from a theme
				int[] attrsAutors = new int[] { R.attr.authorsIcon };
				// Obtain the styled attributes. 'themedContext' is a context with a
				// theme, typically the current Activity (i.e. 'this')
				TypedArray taAutors = this.act.obtainStyledAttributes(attrsAutors);
				// To get the value of the 'listItemBackground' attribute that was
				// set in the theme used in 'themedContext'. The parameter is the index
				// of the attribute in the 'attrs' array. The returned Drawable
				// is what you are after
				Drawable drawableAutors = taAutors.getDrawable(0 /* index */);
				// Finally, free the resources used by TypedArray
				taAutors.recycle();
				holderMain.left.setImageDrawable(drawableAutors);

				//Right img
				if (isExpanded)
				{
					holderMain.right.setImageDrawable(drawableArrowUp);
				}
				else
				{
					holderMain.right.setImageDrawable(drawableArrrowDown);
				}

			break;
			//categories
			case 1:
				//Left img
				holderMain.left.setImageDrawable(drawableSubject);

				//Right img
				if (isExpanded)
				{
					holderMain.right.setImageDrawable(drawableArrowUp);
				}
				else
				{
					holderMain.right.setImageDrawable(drawableArrrowDown);
				}
			break;
			//downloads
			case 2:
				//Left img
				holderMain.left.setImageDrawable(drawableDownload);

				//Right img
				holderMain.right.setImageDrawable(null);
			break;
			//settings
			case 3:
				//Left img
				holderMain.left.setImageDrawable(drawableSettings);

				//Right img
				holderMain.right.setImageDrawable(null);
			break;
		}
		return view;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
	ViewGroup parent)
	{
		View view = null;
		MyHolder holderMain;
		if (convertView == null)
		{
			ViewGroup vg;
			vg = (ViewGroup) LayoutInflater.from(act).inflate(R.layout.drawer_group, parent, false);
			holderMain = new MyHolder((ImageView) vg.findViewById(R.id.img_left),
			(TextView) vg.findViewById(R.id.text), (ImageView) vg.findViewById(R.id.img_right));
			vg.setTag(holderMain);
			view = vg;
		}
		else
		{
			view = convertView;
			holderMain = (MyHolder) convertView.getTag();
		}
		//text and it's size
		holderMain.text.setText(mGroups.get(groupPosition).get(childPosition));
		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		holderMain.text.setTextSize(21 * scaleFactor);

		//left and right imgs
		holderMain.right.setImageDrawable(drawableArrrowRight);
		switch (groupPosition)
		{
			case (0):
				if (isLastChild)
				{
					holderMain.left.setImageDrawable(drawableAuthorsMore);
				}
				else
				{
					holderMain.left.setImageDrawable(drawableAuthor);
				}

			break;
			case (1):
				if (isLastChild)
				{
					holderMain.left.setImageDrawable(drawableCategoriesMore);
				}
				else
				{
					holderMain.left.setImageDrawable(drawableAsList);
				}
			break;

		}

		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}

	static class MyHolder
	{

		ImageView left;
		TextView text;
		ImageView right;

		MyHolder(ImageView left, TextView text, ImageView right)
		{
			this.left = left;
			this.text = text;
			this.right = right;
		}
	}
}
