package com.mycompany.service;

import com.mycompany.model.Item;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.webharvest.runtime.variables.ListVariable;
import org.webharvest.runtime.variables.NodeVariable;
import org.xml.sax.SAXException;
import sun.net.www.http.HttpClient;

/**
 * @date Apr 18, 2015
 * @author Setu
 */
public class Crawler extends Thread {

    public Map getParams() {
        if(params == null)
            params = new HashMap();
        return params;
    }

    public Item getItem() {
        return item;
    }

    public enum CrawlType {

        ITEM_PRICE, ITEM_YEAR_STATISTICS, DATA_ARCHIVE, CODE_NAMES
    }

    private static ScraperConfiguration PRESSURE_CONFIG;
    private static ScraperConfiguration YEAR_STATISTIC_CONFIG;
    private static ScraperConfiguration DATA_ARCHIVE_CONFIG;
    private static ScraperConfiguration CODE_NAMES_CONFIG;
    private ScraperConfiguration scraperConfig = null;

    static final Logger logger = Logger.getLogger(Crawler.class.getName());

    private final String PRICE_URL = "http://dsebd.org/bshis_new1_old.php?w=";
    private final String YEAR_STATISTICS_URL = "http://dsebd.org/displayCompany.php?name=";
    private final String DATA_ARCHIVE_URL = "http://www.dsebd.org/day_end_archive.php";
    private final String CODE_NAMES_URL = "http://www.dsebd.org/company%20listing.php";
    //private final ServletContext context;
    private final Item item;
    private final CrawlType crawlType;
    private final static String pressureConfigFile = "volume.xml";
    private final static String yearStatisticsFile = "yearStatistics.xml";
    private final static String dataArchiveFile = "data_archive.xml";
    private final static String codeNamesFile = "codes.xml";
    private final static String DATA_ARCHIVE_DATE_PATTERN = "yyyy-MM-dd";
    private final String SKIP_CODE_PATTERN = "(T\\d+Y\\d+|.*dse.*|DEB.*)";
    private final long HTTP_TIMEOUT = 60000;
    private Map params;

    public Crawler(ScraperConfiguration scraperConfig, Item item, CrawlType crawlType, Map params) {
        this.scraperConfig = scraperConfig;
        this.item = item;
        this.crawlType = crawlType;
        this.params = params;
    }

    public static ScraperConfiguration getScraperConfig(ServletContext context, CrawlType crawlType) throws FileNotFoundException {
        switch (crawlType) {
            case ITEM_PRICE:
                if (PRESSURE_CONFIG == null) {
                    PRESSURE_CONFIG = new ScraperConfiguration(context.getRealPath("/") + "/WEB-INF/classes/" + pressureConfigFile);
                }
                return PRESSURE_CONFIG;
            case ITEM_YEAR_STATISTICS:
                if (YEAR_STATISTIC_CONFIG == null) {
                    YEAR_STATISTIC_CONFIG = new ScraperConfiguration(context.getRealPath("/") + "/WEB-INF/classes/" + yearStatisticsFile);
                }
                return YEAR_STATISTIC_CONFIG;
            case DATA_ARCHIVE:
                if (DATA_ARCHIVE_CONFIG == null) {
                    DATA_ARCHIVE_CONFIG = new ScraperConfiguration(context.getRealPath("/") + "/WEB-INF/classes/" + dataArchiveFile);
                }
                return DATA_ARCHIVE_CONFIG;
            case CODE_NAMES:
                if (CODE_NAMES_CONFIG == null) {
                    CODE_NAMES_CONFIG = new ScraperConfiguration(context.getRealPath("/") + "/WEB-INF/classes/" + codeNamesFile);
                }
                return CODE_NAMES_CONFIG;
        }

        return null;
    }
    
    public static ScraperConfiguration getScraperConfig(String configPath, CrawlType crawlType) throws FileNotFoundException {
        switch (crawlType) {
            case ITEM_PRICE:
                if (PRESSURE_CONFIG == null) {
                    PRESSURE_CONFIG = new ScraperConfiguration(configPath + pressureConfigFile);
                }
                return PRESSURE_CONFIG;
            case ITEM_YEAR_STATISTICS:
                if (YEAR_STATISTIC_CONFIG == null) {
                    YEAR_STATISTIC_CONFIG = new ScraperConfiguration(configPath + yearStatisticsFile);
                }
                return YEAR_STATISTIC_CONFIG;
            case DATA_ARCHIVE:
                if (DATA_ARCHIVE_CONFIG == null) {
                    DATA_ARCHIVE_CONFIG = new ScraperConfiguration(configPath + dataArchiveFile);
                }
                return DATA_ARCHIVE_CONFIG;
            case CODE_NAMES:
                if (CODE_NAMES_CONFIG == null) {
                    CODE_NAMES_CONFIG = new ScraperConfiguration(configPath + codeNamesFile);
                }
                return CODE_NAMES_CONFIG;
        }

        return null;
    }

    @Override
    public void run() {
        try {
            if (crawlType.equals(CrawlType.ITEM_PRICE)) {
                crawlPrice();
            } else if (crawlType.equals(CrawlType.ITEM_YEAR_STATISTICS)) {
                crawlYearStatistics();
            } else if (crawlType.equals(CrawlType.DATA_ARCHIVE)) {
                crawlDataArchive();
            } else if (crawlType.equals(CrawlType.CODE_NAMES)) {
                crawlCodeNames();
            }
        } catch (Exception ex) {
            System.out.println("Error caught: " + ex.getMessage() + ", skipping " + getItem());
            //ex.printStackTrace();
            //this.interrupt();
        }
    }
    
    private void crawlCodeNames() {
        Scraper scraper = new Scraper(scraperConfig, "d:/expekt");
        String url = CODE_NAMES_URL;
        scraper.addVariableToContext("url", url);
        scraper.setDebug(true);
        synchronized (scraper) {
            scraper.execute();
        }

        ListVariable variables = (ListVariable) scraper.getContext().get("codes");
        List<String> codes = new ArrayList<>();
        for(Object code_name: variables.toList()){
            codes.add(code_name.toString());
        }
        Collections.sort(codes);
        
        List<Item> items = new ArrayList<>();
        for(String code: codes){
            if(!code.matches(SKIP_CODE_PATTERN)){
                items.add(new Item(code));
                //System.out.println("code: " + code);
            }
        }
        
        System.out.println("list size: " + items.size());
        getParams().put("items", items);
    }

    private void crawlDataArchive() {
        Scraper scraper = new Scraper(scraperConfig, "d:/expekt");
        String url = DATA_ARCHIVE_URL;
        scraper.addVariableToContext("url", url);
        scraper.addVariableToContext("startDate", getParams().get("startDate"));
        scraper.addVariableToContext("endDate", getParams().get("endDate"));
        scraper.addVariableToContext("code", getItem().getCode());
        scraper.setDebug(true);
        synchronized (scraper) {
            scraper.execute();
        }

        ListVariable variables = (ListVariable) scraper.getContext().get("items");
        List<Item> items = parseXML(variables.toString());
        getParams().put("items", items);
    }

    private List<Item> parseXML(String domStr) {
        Document doc;
        List<Item> items = new ArrayList<>();

        try {
            InputStream is = new ByteArrayInputStream(domStr.getBytes());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);
            doc.normalizeDocument();

            NodeList nodeList = doc.getElementsByTagName("data");
            DateFormat dateFormat = new SimpleDateFormat(DATA_ARCHIVE_DATE_PATTERN);
            System.out.println(getItem().getCode() + " size: " + nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String dateStr = attributes.getNamedItem("date").getNodeValue();
                String code = attributes.getNamedItem("code").getNodeValue();
                String lastPriceStr = attributes.getNamedItem("ltp").getNodeValue().replace(",", "");
                String highStr = attributes.getNamedItem("high").getNodeValue().replace(",", "");
                String lowStr = attributes.getNamedItem("low").getNodeValue().replace(",", "");
                String openStr = attributes.getNamedItem("open").getNodeValue().replace(",", "");
                String closeStr = attributes.getNamedItem("close").getNodeValue().replace(",", "");
                String yesterdayCloseStr = attributes.getNamedItem("ycp").getNodeValue().replace(",", "");
                String tradeStr = attributes.getNamedItem("trade").getNodeValue().replace(",", "");
                String valueStr = attributes.getNamedItem("value").getNodeValue().replace(",", "");
                String volumeStr = attributes.getNamedItem("volume").getNodeValue().replace(",", "");

                Item anItem = new Item();
                anItem.setCode(code);
                
                anItem.setDate(dateFormat.parse(dateStr));
                anItem.setLastPrice(Float.parseFloat(lastPriceStr));
                anItem.setDayHigh(Float.parseFloat(highStr));
                anItem.setDayLow(Float.parseFloat(lowStr));
                anItem.setOpenPrice(Float.parseFloat(openStr));
                anItem.setClosePrice(Float.parseFloat(closeStr));
                anItem.setYesterdayClosePrice(Float.parseFloat(yesterdayCloseStr));
                anItem.setTrade(Integer.parseInt(tradeStr));
                anItem.setValue(Float.parseFloat(valueStr));
                anItem.setVolume(Integer.parseInt(volumeStr));
                
                if(anItem.getLastPrice() != 0)
                    items.add(anItem);
            }
        } catch (SAXException | IOException | ParserConfigurationException | ParseException ex) {
            System.out.println("Exception caught in parsing xml: " + ex.getMessage() + ", code: " + getItem().getCode());
            ex.printStackTrace();
            return new ArrayList<>();
        }
        
        return items;
    }

    private void crawlYearStatistics() {
        //ScraperConfiguration config = new ScraperConfiguration(context.getRealPath("/") + "/WEB-INF/classes/" + "yearStatistics.xml");
        Scraper scraper = new Scraper(scraperConfig, "d:/expekt");
        String url = YEAR_STATISTICS_URL + getItem().getCode();
        scraper.addVariableToContext("url", url);
        scraper.setDebug(true);
        synchronized (scraper) {
            scraper.execute();
        }

        ListVariable variable = (ListVariable) scraper.getContext().get("range");
        String range = variable.toString();
        //System.out.println("range: " + range + ", item: " + item.getCode());
        String[] lowHigh = range.split("-");
        float low = Float.parseFloat(lowHigh[0].trim());
        float high = Float.parseFloat(lowHigh[1].trim());
        getItem().setLow(low);
        getItem().setHigh(high);
        //System.out.println("Item: " + item);
    }

    private void crawlPrice() throws Exception {
        //ScraperConfiguration config = new ScraperConfiguration(context.getRealPath("/") + "/WEB-INF/classes/" + "volume.xml");
        Scraper scraper = new Scraper(scraperConfig, "d:/expekt");
        String url = PRICE_URL + getItem().getCode();
        scraper.addVariableToContext("url", url);
        scraper.setDebug(true);
        scraper.getHttpClientManager().getHttpClient().getParams().setConnectionManagerTimeout(HTTP_TIMEOUT);
        synchronized (scraper) {
            scraper.execute();
        }

        ListVariable variable = (ListVariable) scraper.getContext().get("buyVolume");
        int buyVolumem = Double.valueOf(variable.toString()).intValue();
        variable = (ListVariable) scraper.getContext().get("sellVolume");
        int sellVolume = Double.valueOf(variable.toString()).intValue();
        float pressure = getBSPressure(buyVolumem, sellVolume);
        variable = (ListVariable) scraper.getContext().get("openPrice");
        float openPrice = Float.parseFloat(parseValue(variable.toString()));
        variable = (ListVariable) scraper.getContext().get("lastPrice");
        float lastPrice = Float.parseFloat(parseValue(variable.toString()));
        variable = (ListVariable) scraper.getContext().get("trade");
        int trade = Integer.valueOf(parseValue(variable.toString()));
        variable = (ListVariable) scraper.getContext().get("closePrice");
        float closePrice = Float.parseFloat(parseValue(variable.toString()));
        variable = (ListVariable) scraper.getContext().get("volume");
        int volume = Integer.valueOf(parseValue(variable.toString()));
        variable = (ListVariable) scraper.getContext().get("value");
        float value = Float.parseFloat(parseValue(variable.toString()));
        variable = (ListVariable) scraper.getContext().get("dayHigh");
        float dayHigh = Float.parseFloat(parseValue(variable.toString()));
        variable = (ListVariable) scraper.getContext().get("dayLow");
        float dayLow = Float.parseFloat(parseValue(variable.toString()));
        variable = (ListVariable) scraper.getContext().get("yesterdayClosePrice");
        float yesterdayClosePrice = Float.parseFloat(parseValue(variable.toString()));

        getItem().setPressure(pressure);
        getItem().setBuyVolume(buyVolumem);
        getItem().setSellVolume(sellVolume);
        getItem().setOpenPrice(openPrice);
        getItem().setLastPrice(lastPrice);
        getItem().setTrade(trade);
        getItem().setClosePrice(closePrice);
        getItem().setVolume(volume);
        getItem().setValue(value);
        getItem().setDayHigh(dayHigh);
        getItem().setDayLow(dayLow);
        getItem().setYesterdayClosePrice(yesterdayClosePrice);
    }

    private String parseValue(String text) throws ArrayIndexOutOfBoundsException {
        String[] tokens = text.split(":");
        String value = tokens[tokens.length - 1].trim();

        return value;
    }

    private float getBSPressure(int buyVolume, int sellVolume) {
        if (buyVolume == 0 && sellVolume == 0) {
            return 0;
        }

//        int difference = Math.abs(buyVolume - sellVolume);
//        if(item.getCode().equals("GBBPOWER"))
//            System.out.println("buyVolume: " + buyVolume + ", sellVolume: " + sellVolume + ", difference: ");
//        int smallest = ((buyVolume + sellVolume) - difference) / 2;
//        if (smallest == 0) {
//            smallest = 1; //get rid of devide by zero error
//        }
//        int pressure = Math.round((difference * 100) / smallest);
//        pressure = (sellVolume > buyVolume) ? -pressure : pressure;
//        return pressure;
        
        int difference = Math.abs(buyVolume - sellVolume);
        int smallest = ((buyVolume + sellVolume) - difference) / 2;
        int greatest = ((buyVolume + sellVolume) + difference) / 2;
        if (smallest == 0) {
            smallest = 1; //get rid of devide by zero error
        }
        float pressure = ((float)greatest/(float)smallest);
        pressure = (sellVolume > buyVolume) ? -pressure : pressure;
        DecimalFormat df = new DecimalFormat("#.#");
        String pressureString = df.format(pressure);
        float returnValue = Float.parseFloat(pressureString);
        return returnValue;
    }

}
