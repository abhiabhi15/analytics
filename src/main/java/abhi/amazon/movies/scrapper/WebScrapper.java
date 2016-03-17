package abhi.amazon.movies.scrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Author : abhishek
 * Created on 3/13/16.
 */
public class WebScrapper {

    public void scrap(String url){

        Document doc = null;
        try{
            doc = Jsoup.connect(url).data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(5000)
                    .post();

//            Elements elements = doc.getElementsByAttributeValueStarting("href", "https://www.amazon.com/s/ref=atv_dp_imdb_hover_genre");
//            for(Element element : elements){
//                System.out.println(element.data());
//            }

            String title = doc.title();
            System.out.println(title);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        WebScrapper scrapper = new WebScrapper();
        scrapper.scrap("http://amazon.com/gp/product/B001N8Q6V0/");
    }
}
