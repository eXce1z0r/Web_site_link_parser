package SiteParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class SiteLoader 
{	
	public static double countSitePR(String urlAddress, HashMap<String, Double> alreadyCalculatedSites, String siteToStay)
	{
		double res = 0;	
		
		ArrayList<String> allSiteLinks = getAllLinks(load(urlAddress));
		HashMap<String, Integer> dataForPRCalc = new HashMap<String, Integer>();
		
		StringBuffer linkForSearching = new StringBuffer(urlAddress);
		
		int startRemoveIndex = linkForSearching.indexOf("http://");
		if(startRemoveIndex != -1)
		{
			linkForSearching.delete(startRemoveIndex, 7);
		}
		
		startRemoveIndex = linkForSearching.indexOf("https://");
		if(startRemoveIndex != -1)
		{
			linkForSearching.delete(startRemoveIndex, 8);
		}
		
		startRemoveIndex = linkForSearching.indexOf("//");
		if(startRemoveIndex != -1)
		{
			linkForSearching.delete(0, 2);
		}
		
		startRemoveIndex = linkForSearching.indexOf("www.");
		if(startRemoveIndex != -1)
		{
			linkForSearching.delete(0, 4);
		}
		
		startRemoveIndex = linkForSearching.indexOf("/");
		if(startRemoveIndex != -1)
		{
			linkForSearching.delete(startRemoveIndex, linkForSearching.length());
		}
		
		for(String siteLink: allSiteLinks)
		{
			StringBuffer linkToFix = new StringBuffer(siteLink);
			
			startRemoveIndex = linkToFix.indexOf("http://");
			if(startRemoveIndex != -1)
			{
				linkToFix.delete(startRemoveIndex, startRemoveIndex+7);
			}
			
			startRemoveIndex = linkToFix.indexOf("https://");
			if(startRemoveIndex != -1)
			{
				linkToFix.delete(startRemoveIndex, startRemoveIndex+7);
			}
			
			startRemoveIndex = linkToFix.indexOf("//");

			if(startRemoveIndex != -1)
			{
				linkToFix.delete(startRemoveIndex, startRemoveIndex+2);
			}
			
			int wwwIndex = linkToFix.indexOf("www.");
			if(wwwIndex == 0)
			{
				linkToFix.delete(0, 4);
			}
			
			startRemoveIndex = linkToFix.indexOf("/");

			if(startRemoveIndex == 0)
			{
				linkToFix.delete(startRemoveIndex, 1);
			}
			
			String correctLink = "http://www."+linkToFix;			
			
			if(correctLink.indexOf(linkForSearching.toString())!= -1)// checking for links of this domain
			{
				dataForPRCalc.put(correctLink, amountReferencesOfCurrentSite(linkForSearching.toString(), correctLink));
			}
		}
		
		
		if(!alreadyCalculatedSites.containsKey(linkForSearching.toString()))
		{				
			alreadyCalculatedSites.put(linkForSearching.toString(), 0.0);
			
			System.out.println("Link for search: "+linkForSearching);
			
			for(Entry site: dataForPRCalc.entrySet())
			{
				
				if(Double.parseDouble(site.getValue().toString()) != 0)
				{
					
					double placeForRecursionVal = countSitePR(site.getKey().toString(), alreadyCalculatedSites, siteToStay);
					
					System.out.println("\t"+site.getKey().toString()+": "+placeForRecursionVal);
					res += placeForRecursionVal/Double.parseDouble(site.getValue().toString());
				}
				else
				{
					System.out.println("\t"+site.getKey().toString()+": 0");
				}
				
			}
			
			if(dataForPRCalc.size() == 0)
			{
				res = 0;
			}
			else
			{
				res = (1-0.85)/dataForPRCalc.size()+0.85*(res);
			}
			
			alreadyCalculatedSites.put(linkForSearching.toString(), res);
		}
		else
		{
			res = alreadyCalculatedSites.get(linkForSearching.toString());
		}
				
		return res;
		
	}
	
	private static int amountReferencesOfCurrentSite(String currentSite, String siteForSeeking)
	{
		int res = 0;
		
		ArrayList<String> links = getAllLinks(load(siteForSeeking));
		
		for(String link: links)
		{
			if(link.indexOf(currentSite)!=-1)
			{
				res++;
			}
		}
		
		return res;
	}
	
	public static String load(String urlAddress)
	{
		URL url = null;
		URLConnection con = null;// used to work with Timeouts(Read/Connect)
		
		BufferedReader reader = null;
		
		StringBuffer resStringCreator = new StringBuffer();
		
		String res = "";
		
		try 
		{
			url = new URL(urlAddress);
		} 
		catch (MalformedURLException e) // wrong url address exception
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try 
		{
			con = url.openConnection();
			
			con.setConnectTimeout(1000);
			

			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} 
		catch (IOException e) // input/output exception
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		
		if(reader != null)
		{
			try 
			{
				String line = null;
				while((line = reader.readLine())!=null)
				{
					resStringCreator.append(line).append("\n");
				}
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
			res = resStringCreator.toString();
			
			try 
			{
				reader.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return res;
	}
	
	public static void writeToFile(String fileName, String data)
	{
		OutputStreamWriter writer = null;
		try 
		{
			writer = new OutputStreamWriter(new FileOutputStream(new File("res/"+fileName)));
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try 
		{
			writer.write(data);
			writer.flush();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try 
		{
			writer.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> getAllLinks(String siteDataCode)
	{
		ArrayList<String> links = new ArrayList<String>();
		
		String[] ignoreRefsWithSymbAtBegining = {"-"};
		
		String[] referenceMarks = {"http://", "https://", "www.", "//"/*, "href"*/};
		
		String[] forbiddenSymbs = {"@"};
		
		String line = null;
		
		int currLineBeginIndex = 0;
		int currLineEndIndex = 0;
		
		int lineCounter = 0;
		
		if(siteDataCode.indexOf("\n", currLineBeginIndex)!= -1)
		{
			while((line = siteDataCode.substring(currLineBeginIndex, (currLineEndIndex=siteDataCode.indexOf("\n", currLineBeginIndex))))!=null)
			{
				//Parsing BEGIN
				
				lineCounter++;
				
				for(String referenceMark: referenceMarks)
				{
					String link = null;
					
					int currLinkBeginIndex = 0;
					int currLinkEndIndex = 0;
					
					int currRefMarkLength = referenceMark.length();
					
					boolean currLineLinkSeekerFlag = true;
					
					while(currLineLinkSeekerFlag)
					{
						boolean addToLinkListFlag = true;
						
						currLinkBeginIndex = line.indexOf(referenceMark, currLinkBeginIndex);
						
						if(currLinkBeginIndex != -1)
						{						
							int refEndMarkV1 = line.indexOf("\'", currLinkBeginIndex);
							int refEndMarkV2 = line.indexOf("\"", currLinkBeginIndex);
							
							if(refEndMarkV1<refEndMarkV2)
							{
								if(refEndMarkV1!= -1)
								{
									currLinkEndIndex= refEndMarkV1;
								}
								else
								{
									currLinkEndIndex= refEndMarkV2;
								}
							}
							else if(refEndMarkV1>refEndMarkV2)
							{
								if(refEndMarkV2 != -1)
								{
									currLinkEndIndex= refEndMarkV2;
								}
								else
								{
									currLinkEndIndex= refEndMarkV1;
								}
							}
							else // there means that refEndMarkV1 = -1 and refEndMarkV2 = -1
							{
								currLinkEndIndex = line.indexOf(" ", currLinkBeginIndex);
								
								if(currLinkEndIndex == -1)
								{
									currLinkEndIndex = line.length()-1;
								}
							}
							
							try
							{
								link = line.substring(currLinkBeginIndex, currLinkEndIndex);
							}
							catch(StringIndexOutOfBoundsException e)
							{
								System.out.println(line);
								System.out.println("currLinkBeginIndex= "+ currLinkBeginIndex+ " -> "+ line.indexOf(currLinkBeginIndex));
								
								System.out.println("currLinkEndIndex= "+ currLinkEndIndex);
								System.exit(0);
							}
							
							//Check on ignore symbols BEGIN
							if(link.length()>3)
							{
								String forStrCheck = null;
								if(currRefMarkLength+1>link.length())
								{
									forStrCheck = link.substring(currRefMarkLength, link.length());
								}
								else
								{
									forStrCheck = link.substring(currRefMarkLength, currRefMarkLength+1);
								}
									
								for(String ignoreSymb :ignoreRefsWithSymbAtBegining)		
								{
									if(forStrCheck.equals(ignoreSymb))
									{
										addToLinkListFlag = false;
									}
								}
								
								//Check on ignore symbols END
								
								for(String forbiddenSymb:forbiddenSymbs)
								{
									if(link.indexOf(forbiddenSymb)!= -1)
									{
										addToLinkListFlag = false;
									}
								}
								
								if(addToLinkListFlag)
								{
									links.add(link);	
								}
								
								
								currLinkBeginIndex = currLinkEndIndex;
								
								if(currLinkBeginIndex+1<line.length())
								{
									currLinkBeginIndex++;
								}
							}
							else
							{
								currLineLinkSeekerFlag = false;
							}
						}
						else
						{
							currLineLinkSeekerFlag = false;
						}
					}
				}
						
				
				//Parsing ENDS
				currLineBeginIndex = currLineEndIndex;
				
				if(currLineBeginIndex+1<siteDataCode.length())
				{
					currLineBeginIndex++;
				}
				else
				{
					break;
				}
			}
		}
		
		return links;
	}
}
