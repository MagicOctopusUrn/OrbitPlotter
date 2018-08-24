package net.cc.wiki;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OrbitPuller {
	public static void main(String[] args) throws MalformedURLException, IOException {
		new OrbitPuller("https://en.wikipedia.org/wiki/Earth");
		new OrbitPuller("https://en.wikipedia.org/wiki/Venus");
	}
	
	private final String wikipediaURL;
	
	public OrbitPuller(String wikipediaURL) throws MalformedURLException, IOException {
		this.wikipediaURL = wikipediaURL;
		Document doc = Jsoup.parse(new URL(wikipediaURL), 5000);
        Elements trs = doc.select("table.infobox > tbody > tr");
        if(trs != null && trs.size() > 0){
            for(Element tr : trs.first().siblingElements()){
            	tr.select("td > span.sortkey").remove();
                String name = tr.select("th > a[title]").attr("title");
                String value = tr.select("td").text();
            
                System.out.println(name + " - " + value.replaceAll("[\\d+]?", ""));
            }
        }
	}
}
