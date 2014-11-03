/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package crawler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


/**
 *
 * @author james
 */
public class Crawler {
    public static ArrayList getWebPage(String url, String file) throws IOException, CircularRedirectException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse resp = client.execute(httpget);
        ArrayList<String> lines = new ArrayList<>();
        try {
            HttpEntity entity = resp.getEntity();
            if(entity != null) {
                InputStream is = entity.getContent();
                BufferedReader br = new BufferedReader(
                new InputStreamReader(is));
                String line;
                
                PrintWriter writer = new PrintWriter(file, "UTF-8");
                while((line = br.readLine()) != null) {
                    writer.println(line);
                    lines.add(line);
                }
                writer.close();
            }
        } finally{
            resp.close();
            
        }
        return lines;
    }
    
    public static ArrayList extractLinks(ArrayList<String> lines) throws CircularRedirectException {
        ArrayList<String> links = new ArrayList<>();
        for (String line: lines) {
            int position = line.indexOf("<a href=\"http://");
            if(position != -1) {
                String line1 = line.substring(position + 9);
                int pos2 = line1.indexOf("\"");
                if(pos2 != -1) {
                    String oneLink = line1.substring(0, pos2);
                    int pos3 = oneLink.indexOf("#");
                    if(pos3 != -1) {
                        oneLink = oneLink.substring(0, pos3);
                    }
                    if(!oneLink.contains("http://m.") && (!oneLink.contains("blog.hu/policy")) &&
                            (!oneLink.contains("blog.hu/user/")) && (!oneLink.contains(" "))) {
                    links.add(oneLink);
                    }
                    
                    
                }
            }
            
    }
        return links;
    }
    
    public static void crawl(ArrayList<String> pages) throws IOException, CircularRedirectException {
        int index = 3369;
        int i = 0;
        while(i < pages.size()) {
            String page = pages.get(i);
            System.out.println("Download");
            System.out.println(page);
            ArrayList<String> page_cont = new ArrayList<>();
            try {
            page_cont = getWebPage(page, "page" + index + ".txt");
            i++;
            index++;
            } catch(CircularRedirectException e) {
                page_cont = getWebPage(pages.get(i + 1), "page" + index + ".txt");
                i+= 2;
                index+=2;
            } 
            ArrayList<String> new_links = extractLinks(page_cont);
            
            System.out.println("Add New Links");
            ArrayList<String> goodLinks = new ArrayList<>();
            for(String link: new_links) {
                if(link.contains(".blog.hu") && (!link.contains("policy"))) {
                    goodLinks.add(link);
                }
            }
            for(String goodLink: goodLinks) {
                if (!pages.contains(goodLink)) {
                    pages.add(goodLink);
                    System.out.println(goodLink);
                }
            }
            try{
            Thread.sleep(3000);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        ArrayList<String> pages = new ArrayList<>();
        pages.add("http://retemu.blog.hu/2014/11/02/gepismerteto-_a_keleti_blokk-_lengyelorszag_i?utm_medium=doboz&utm_campaign=bloghu_cimlap&utm_source=tech");
        try {
            //extractLinks(
            //getWebPage("http://juditgubacsi.blog.hu/2011/12/21/kell_240", "test3.txt");//);
            crawl(pages);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
    
    

