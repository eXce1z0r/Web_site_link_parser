package Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import SiteParser.SiteLoader;

public class App 
{
	public static void main(String[] args)
	{		
		String siteString = SiteLoader.load("http://www.scrabblo.com/containing-words/dawqw");
		
		SiteLoader.writeToFile("last_site_code.txt", siteString);
		
		for(String link:SiteLoader.getAllLinks(siteString))
		{
			System.out.println(link);
		}
		
		System.out.println("-----------------------");
		
		
		
		System.out.println("Res: "+SiteLoader.countSitePR("http://www.scrabblo.com/containing-words/dawqw", new HashMap<String, Double>(), null));
	}
}
