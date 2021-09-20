/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package findyourfootballclient;

import com.mycompany.findyourfootballwebcrawler.FileNotFoundException_Exception;
import com.mycompany.findyourfootballwebcrawler.IOException_Exception;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jackg
 */
public class FindYourFootballClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException_Exception, FileNotFoundException_Exception {
        // Call each web service 

      //  getPremierFixtures();
      //  getChampionshipFixtures();
       // getFrenchLigueOneFixtures();
       // getLaLigaFixtures();
       // getChampionsLeagueFixtures();
       // getBundesligaFixtures();
       // getFACupFixtures();
       // getAmmenitites();
        getVenueFixtures();

    }

    // Connector to the web service 
    private static String getPremierFixtures() throws FileNotFoundException_Exception, IOException_Exception {
        com.mycompany.findyourfootballwebcrawler.WebCrawler_Service service = new com.mycompany.findyourfootballwebcrawler.WebCrawler_Service();
        com.mycompany.findyourfootballwebcrawler.WebCrawler port = service.getWebCrawlerPort();

        return port.getPremierFixtures();

    }

    // Connector to the web service 
    private static String getChampionshipFixtures() throws FileNotFoundException_Exception, IOException_Exception {
        com.mycompany.findyourfootballwebcrawler.WebCrawler_Service service = new com.mycompany.findyourfootballwebcrawler.WebCrawler_Service();
        com.mycompany.findyourfootballwebcrawler.WebCrawler port = service.getWebCrawlerPort();
        return port.getChampionshipFixtures();
    }

    // Connector to the web service 
    private static String getFrenchLigueOneFixtures() throws IOException_Exception {
        com.mycompany.findyourfootballwebcrawler.WebCrawler_Service service = new com.mycompany.findyourfootballwebcrawler.WebCrawler_Service();
        com.mycompany.findyourfootballwebcrawler.WebCrawler port = service.getWebCrawlerPort();
        return port.getFrenchLigueOneFixtures();
    }

    // Connector to the web service 
    private static String getLaLigaFixtures() throws IOException_Exception {
        com.mycompany.findyourfootballwebcrawler.WebCrawler_Service service = new com.mycompany.findyourfootballwebcrawler.WebCrawler_Service();
        com.mycompany.findyourfootballwebcrawler.WebCrawler port = service.getWebCrawlerPort();
        return port.getLaLigaFixtures();
    }

    // Connector to the web service 
    private static String getBundesligaFixtures() throws IOException_Exception {
        com.mycompany.findyourfootballwebcrawler.WebCrawler_Service service = new com.mycompany.findyourfootballwebcrawler.WebCrawler_Service();
        com.mycompany.findyourfootballwebcrawler.WebCrawler port = service.getWebCrawlerPort();
        return port.getBundesligaFixtures();
    }

    // Connector to the web service 
    private static String getFACupFixtures() throws IOException_Exception {
        com.mycompany.findyourfootballwebcrawler.WebCrawler_Service service = new com.mycompany.findyourfootballwebcrawler.WebCrawler_Service();
        com.mycompany.findyourfootballwebcrawler.WebCrawler port = service.getWebCrawlerPort();
        return port.getFACupFixtures();
    }

    // Connector to the web service 
    private static String getChampionsLeagueFixtures() throws IOException_Exception {
        com.mycompany.findyourfootballwebcrawler.WebCrawler_Service service = new com.mycompany.findyourfootballwebcrawler.WebCrawler_Service();
        com.mycompany.findyourfootballwebcrawler.WebCrawler port = service.getWebCrawlerPort();
        return port.getChampionsLeagueFixtures();
    }

    // Connector to the web service 
    private static String getAmmenitites() throws IOException_Exception, FileNotFoundException_Exception {
        com.mycompany.findyourfootballwebcrawler.WebCrawler_Service service = new com.mycompany.findyourfootballwebcrawler.WebCrawler_Service();
        com.mycompany.findyourfootballwebcrawler.WebCrawler port = service.getWebCrawlerPort();
        return port.getAmmenitites();
    }

    // Connector to the web service 
    private static String getVenueFixtures() {
        com.mycompany.findyourfootballwebcrawler.WebCrawler_Service service = new com.mycompany.findyourfootballwebcrawler.WebCrawler_Service();
        com.mycompany.findyourfootballwebcrawler.WebCrawler port = service.getWebCrawlerPort();
        return port.getVenueFixtures();
    }

}
