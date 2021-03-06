/*
 16.01.2015
ContentProviderOdnakoDB.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.sql.SQLException;

import ru.kuchanov.odnako.R;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.stmt.QueryBuilder;

public class ContentProviderOdnakoDB extends ContentProvider
{
	private static final String LOG = ContentProviderOdnakoDB.class.getSimpleName();

	private DataBaseHelper dataBaseHelper;

	Context ctx;

	/**
	 * Content authority for this provider.
	 */
	private static final String AUTHORITY = "ru.kuchanov.odnako.db.ContentProviderOdnakoDB";

	/**
	 * URI ID for route: /article
	 */
	public static final int ROUTE_ARTICLE = 1;

	/**
	 * URI ID for route: /article/{ID}
	 */
	public static final int ROUTE_ARTICLE_ID = 2;

	/**
	 * URI ID for route: /artcat
	 */
	public static final int ROUTE_ART_CAT = 3;

	/**
	 * URI ID for route: /artcat
	 */
	public static final int ROUTE_AUTHOR = 4;

	/**
	 * UriMatcher, used to decode incoming URIs.
	 */
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static
	{
		sUriMatcher.addURI(AUTHORITY, "article", ROUTE_ARTICLE);
		sUriMatcher.addURI(AUTHORITY, "article/*", ROUTE_ARTICLE_ID);
		sUriMatcher.addURI(AUTHORITY, "artcat", ROUTE_ART_CAT);
		sUriMatcher.addURI(AUTHORITY, "author", ROUTE_AUTHOR);
	}

	@Override
	public boolean onCreate()
	{
		Log.e(LOG, "onCreate called");
		this.ctx = this.getContext();
//		Toast.makeText(ctx, "Installed!", Toast.LENGTH_LONG).show();
		this.getHelper();
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		Log.d(LOG + uri.toString(), "Cursor query CALLED!");

		String msg;
		Cursor cursor;

		int uriMatch = sUriMatcher.match(uri);
		switch (uriMatch)
		{
			case ROUTE_ARTICLE:
				// Return all known entries.
				// Note: Notification URI must be manually set here for loaders to correctly
				// register ContentObservers.
				// build your query
				QueryBuilder<Article, Integer> qb = null;
				try
				{
					qb = this.getHelper().getDaoArticle().queryBuilder();
				} catch (SQLException e1)
				{
					e1.printStackTrace();
				}

				// when you are done, prepare your query and build an iterator
				CloseableIterator<Article> iterator = null;
				cursor = null;
				try
				{
					iterator = this.getHelper().getDaoArticle()
					.iterator(qb.where().ge(Article.FIELD_NAME_ID, 0).prepare());
					// get the raw results which can be cast under Android
					AndroidDatabaseResults results = (AndroidDatabaseResults) iterator.getRawResults();
					cursor = results.getRawCursor();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
				cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
				msg = String.valueOf(cursor.getCount());
				Log.d(LOG, "cursor.getCount(): " + msg);
				return cursor;
			case ROUTE_ART_CAT:
				// Return all known entries.
				// Note: Notification URI must be manually set here for loaders to correctly
				// register ContentObservers.
				// build your query
				QueryBuilder<ArtCatTable, Integer> artCatQB = null;
				try
				{
					artCatQB = this.getHelper().getDaoArtCatTable().queryBuilder();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}

				// when you are done, prepare your query and build an iterator
				CloseableIterator<ArtCatTable> i = null;
				cursor = null;
				try
				{
					i = this.getHelper().getDaoArtCatTable()
					.iterator(artCatQB.where().ge(ArtCatTable.ID_FIELD_NAME, 0).prepare());
					// get the raw results which can be cast under Android
					AndroidDatabaseResults results = (AndroidDatabaseResults) i.getRawResults();
					cursor = results.getRawCursor();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
				cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
				String m = String.valueOf(cursor.getCount());
				Log.d(LOG, "cursor.getCount(): " + m);
				return cursor;

			case ROUTE_AUTHOR:
				// Return all known entries.
				// Note: Notification URI must be manually set here for loaders to correctly
				// register ContentObservers.
				// build your query
				QueryBuilder<Author, Integer> qbAuthor = null;
				try
				{
					qbAuthor = this.getHelper().getDaoAuthor().queryBuilder();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}

				// when you are done, prepare your query and build an iterator
				CloseableIterator<Author> iteratorAuthor = null;
				cursor = null;
				try
				{
					iteratorAuthor = this.getHelper().getDaoAuthor()
					.iterator(qbAuthor.where().ge(Author.FIELD_ID, 0).prepare());
					// get the raw results which can be cast under Android
					AndroidDatabaseResults results = (AndroidDatabaseResults) iteratorAuthor.getRawResults();
					cursor = results.getRawCursor();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
				cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
				msg = String.valueOf(cursor.getCount());
				Log.d(LOG, "cursor.getCount(): " + msg);
				return cursor;
		}
		return null;
	}

	@Override
	public String getType(Uri uri)
	{
		//		final int match = sUriMatcher.match(uri);
		//		switch (match)
		//		{
		//			case ROUTE_ARTICLE:
		//				return CardUris.CONTENT_CARDS;
		//			case ROUTE_ARTICLE_ID:
		//				return CardUris.CONTENT_ITEM_CARD;
		//			default:
		//				throw new UnsupportedOperationException("Unknown uri: " + uri);
		//		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		return 0;
	}

	private DataBaseHelper getHelper()
	{
		if (dataBaseHelper == null)
		{
			int dbVer = this.ctx.getResources().getInteger(R.integer.db_version);
			dataBaseHelper = new DataBaseHelper(this.ctx, DataBaseHelper.DATABASE_NAME, null, dbVer);
		}
		return dataBaseHelper;
	}

}
