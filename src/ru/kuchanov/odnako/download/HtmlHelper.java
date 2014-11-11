package ru.kuchanov.odnako.download;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlCleanerException;
import org.htmlcleaner.TagNode;

import android.text.Html;

import ru.kuchanov.odnako.lists_and_utils.ArtInfo;

public class HtmlHelper
{
	TagNode rootNode;
	public String htmlString;

	public static int NUM_OF_ARTS_ON_CUR_PAGE;

	public HtmlHelper(URL htmlPage) throws IOException
	{
		HtmlCleaner cleaner = new HtmlCleaner();
		try
		{
			System.out.println("HtmlHelper constructor URL: " + htmlPage.toString());

			rootNode = cleaner.clean(htmlPage);
			htmlString = cleaner.getInnerHtml(rootNode);
		} catch (HtmlCleanerException e)
		{
			//System.out.println(e.getMessage());
			System.out
			.println("Error in HtmlHelper while try to clean HTML. May be FileNot found or NOconnection exception");
		} catch (FileNotFoundException e)
		{
			System.out.println("FileNotFoundException at HtmlHelper");
			System.out.println(e.getMessage());
			System.out.println("FileNotFoundException at HtmlHelper");
		}
	}

	ArrayList<ArrayList<String>> getAllCategoriesAsList()
	{
		ArrayList<ArrayList<String>> allCategoriesAsList=new ArrayList<ArrayList<String>>();
		TagNode[] liElements;
		TagNode allCategoriesUl = null;
		String CSSClassname = "l-full-col outlined-hard-flipped";

		allCategoriesUl = rootNode.findElementByAttValue("class", CSSClassname, true, false);

		liElements = allCategoriesUl.getChildTags();
		
		for(int i=0; i<liElements.length; i++)
		{
			ArrayList<String> category=new ArrayList<String>();
			category.add(liElements[i].findElementByName("a", true).getAttributeByName("title"));
			category.add(liElements[i].findElementByName("a", true).getAttributeByName("href"));
			
			allCategoriesAsList.add(category);
		}

		return allCategoriesAsList;
	}
	
	ArrayList<ArrayList<String>> getAllAuthorsAsList()
	{
		ArrayList<ArrayList<String>> allAuthorsAsList=new ArrayList<ArrayList<String>>();
		TagNode[] liElements;
		TagNode allAuthorsUl = null;
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block block-top l-tripple-cols";

		allAuthorsUl = rootNode.findElementByAttValue("class", CSSClassname, true, false);

		liElements = allAuthorsUl.getChildTags();
		
		for(int i=0; i<liElements.length; i++)
		{
			ArrayList<String> authorInfo=new ArrayList<String>();
			
			authorInfo.add(liElements[i].findElementByName("a", true).getAttributeByName("title"));
			authorInfo.add(liElements[i].findElementByName("a", true).getAttributeByName("href"));
			if(liElements[i].findElementByName("img", true)!=null)
			{
				authorInfo.add(liElements[i].findElementByName("img", true).getAttributeByName("src"));
			}
			else
			{
				authorInfo.add("empty");
			}
			if(liElements[i].findElementByName("p", true).getText().toString()!="")
			{
				authorInfo.add(liElements[i].findElementByName("p", true).getText().toString());
			}
			else
			{
				authorInfo.add("empty");
			}
			allAuthorsAsList.add(authorInfo);
		}

		return allAuthorsAsList;
	}
	
	public ArrayList<ArtInfo> getAllArtsInfoFromPage()
	{
		ArrayList<ArtInfo> allArtsInfo=new ArrayList<ArtInfo>();
		
		TagNode[] liElements=null;
		TagNode[] allArtsUl = null;
//		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block block-top l-tripple-cols";
		String CSSClassname = "news-wrap clearfix clearfix l-3col packery block l-tripple-cols";

		allArtsUl = rootNode.getElementsByAttValue("class", CSSClassname, true, false);
		
//		System.out.println(allArtsUl[0].getText());

		if(allArtsUl.length>1)
		{
			liElements = allArtsUl[1].getChildTags();
		}
		else
		{
			liElements = allArtsUl[0].getChildTags();
		}
		
		for(int i=0; i<liElements.length; i++)
		{
			String[] info=new String[5];
			
			TagNode element = liElements[i];
			TagNode element1 = element.findElementByAttValue("class", "m-news-info", true, true);
			TagNode element2 = element1.findElementByAttValue("class", "m-news-text", true, true);
			TagNode element3 = element2.findElementByName("a", true);

			TagNode[] imgEl = element.getElementsByName("img", true);

			TagNode author = element.findElementByAttValue("class", "m-news-author-wrap", true, false);
			TagNode[] author1 = author.getElementsByName("a", true);

			info[0] = element3.getAttributeByName("href").toString();
			info[1] = Html.fromHtml(element3.getAttributeByName("title").toString()).toString();
			//System.out.println(output[i][1]);

			if (imgEl.length == 0)
			{
				info[2] = "empty";
			}
			else
			{
				String imgSrc=imgEl[0].getAttributeByName("src").toString();
				if(imgSrc.startsWith("/i/"))
				{
					imgSrc="http://odnako.org"+imgSrc;
				}
				info[2] = imgSrc;
			}
			if (author1.length == 0)
			{
				info[3] = "empty";
				info[4] = "empty";
			}
			else
			{
				info[3] = author1[0].getAttributeByName("href");
				info[4] = Html.fromHtml(author1[0].getAttributeByName("title")).toString();
			}
			
			allArtsInfo.add(new ArtInfo(info));
		}

		return allArtsInfo;
	}

}