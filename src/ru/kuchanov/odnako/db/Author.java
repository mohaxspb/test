/*
 09.12.2014
Author.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "author")
public class Author
{

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String blog_url;

	@DatabaseField(dataType = DataType.STRING, canBeNull = false)
	private String name;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String description;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String who;

	@DatabaseField(dataType = DataType.STRING, canBeNull = true)
	private String avatar;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false)
	private Date refreshed;

	@DatabaseField(dataType = DataType.DATE, canBeNull = false)
	private Date lastArticleDate;

	public Author()
	{

	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getBlog_url()
	{
		return blog_url;
	}

	public void setBlog_url(String blog_url)
	{
		this.blog_url = blog_url;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getWho()
	{
		return who;
	}

	public void setWho(String who)
	{
		this.who = who;
	}

	public String getAvatar()
	{
		return avatar;
	}

	public void setAvatar(String avatar)
	{
		this.avatar = avatar;
	}

	public Date getRefreshed()
	{
		return refreshed;
	}

	public void setRefreshed(Date refreshed)
	{
		this.refreshed = refreshed;
	}

	public Date getLastArticleDate()
	{
		return lastArticleDate;
	}

	public void setLastArticleDate(Date lastArticleDate)
	{
		this.lastArticleDate = lastArticleDate;
	}

}