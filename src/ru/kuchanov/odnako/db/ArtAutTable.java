/*
 09.12.2014
ArtCatTable.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;


import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "art_aut_table")
public class ArtAutTable
{
	public final static String ARTICLE_ID_FIELD_NAME="article_id";
	public final static String AUTHOR_ID_FIELD_NAME="author_id"; 

	@DatabaseField(generatedId = true, allowGeneratedIdInsert=true)
	private int id;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, index = true, columnName = ARTICLE_ID_FIELD_NAME)
	private int article_id;

	@DatabaseField(dataType = DataType.INTEGER, canBeNull = false, index = true, columnName = AUTHOR_ID_FIELD_NAME)
	private int author_id;
	
	public ArtAutTable()
	{
		// TODO need empty constructor for ORMlite
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getArticle_id()
	{
		return article_id;
	}

	public void setArticle_id(int article_id)
	{
		this.article_id = article_id;
	}

	public int getCategory_id()
	{
		return author_id;
	}

	public void setCategory_id(int category_id)
	{
		this.author_id = category_id;
	}

}
