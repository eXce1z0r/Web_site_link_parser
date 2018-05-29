package Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import SiteParser.SiteLoader;

public class App 
{
	public static void main(String[] args)
	{		
		String siteString = SiteLoader.load("http://www.scrabblo.com/containing-words/dawqw"/*"http://www.scrabblo.com/containing-words/dawqw"*//*"https://artjoker.ua/ru/"*/);
		
		SiteLoader.writeToFile("last_site_code.txt", siteString);
		//System.out.println(siteString);
		
		for(String link:SiteLoader.getAllLinks(siteString))
		{
			System.out.println(link);
		}
		
		System.out.println("-----------------------");
		
		/*
		java.util.HashMap<String, Object> a = SiteLoader.countSitePR("http://www.russian-poetry.ru/Poet.php?PoetId=36");
		
 		//HashMap<String, Object> a = new HashMap<String, Object>();
		
		//ArrayList<String> b = new ArrayList<String>();
		
		//for(String siteLink: allSiteLinks)
		//{
		//	b.add("1");
		//	b.add("2");
		//	a.put(siteLink, b);
		//}
		
		for(Entry b: a.entrySet())
		{
			System.out.println(b.getKey()+": \n");
			
			java.util.ArrayList<String> bKeyData= (java.util.ArrayList<String>)b.getValue();
			
			for(String bData: bKeyData)
			{
				System.out.println("\t"+bData);
			}
		}
		*/
		
		System.out.println("Res: "+SiteLoader.countSitePR("http://www.scrabblo.com/containing-words/dawqw"/*"http://www.scrabblo.com/containing-words/dawqw"*//*"https://artjoker.ua/ru/"*/, new HashMap<String, Double>(), null));
		//http://www.scrabblo.com/containing-words/dawqw
		//http://www.russian-poetry.ru/Poet.php?PoetId=36
	}
}
