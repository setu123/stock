package com.mycompany.service;

import com.mycompany.model.Item;
import com.mycompany.model.ItemNews;
import com.mycompany.model.Portfolio;
import com.mycompany.model.PortfolioDetails;
import com.mycompany.model.SharePercentage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import org.webharvest.runtime.variables.ListVariable;
import org.xml.sax.SAXException;

/**
 * @date Apr 18, 2015
 * @author Setu
 */
public class Crawler extends Thread {

    public Map getParams() {
        if (params == null) {
            params = new HashMap();
        }
        return params;
    }

    public Item getItem() {
        return item;
    }

    public enum CrawlType {

        ITEM_PRICE, ITEM_YEAR_STATISTICS, DATA_ARCHIVE, DSEX_DATA_ARCHIVE, DSEX_DATA_SYNC, CODE_NAMES, NEWS, PORTFOLIO_SYNC
    }

    private static ScraperConfiguration PRESSURE_CONFIG;
    private static ScraperConfiguration YEAR_STATISTIC_CONFIG;
    private static ScraperConfiguration DATA_ARCHIVE_CONFIG;
    private static ScraperConfiguration DSEX_DATA_ARCHIVE_CONFIG;
    private static ScraperConfiguration DSEX_DATA_SYNC_CONFIG;
    private static ScraperConfiguration CODE_NAMES_CONFIG;
    private static ScraperConfiguration NEWS_CONFIG;
    private static ScraperConfiguration PORTFOLIO_CONFIG;
    private ScraperConfiguration scraperConfig = null;

    static final Logger logger = Logger.getLogger(Crawler.class.getName());

    private final String PRICE_URL = "http://dsebd.org/bshis_new1_old.php?w=";
    private final String YEAR_STATISTICS_URL = "http://dsebd.org/displayCompany.php?name=";
    private final String DATA_ARCHIVE_URL = "http://www.dsebd.org/day_end_archive.php";
    private final String DSEX_DATA_ARCHIVE_URL = "http://dsebd.org/market_summary.php";
    private final String CODE_NAMES_URL = "http://www.dsebd.org/company%20listing.php";
    private final String NEWS_URL = "http://dsebd.org/old_news1.php";
    private final String PORTFOLIO_URL_PREFIX = "http://www.stockbangladesh.com/portfolios/performance/";
    //private final ServletContext context;
    private final Item item;
    private final CrawlType crawlType;
    private final static String pressureConfigFile = "volume.xml";
    private final static String yearStatisticsFile = "yearStatistics.xml";
    private final static String dataArchiveFile = "data_archive.xml";
    private final static String dsexDataArchiveFile = "dsex_data_archive.xml";
    private final static String dsexDataSyncFile = "dsex_index_sync.xml";
    private final static String codeNamesFile = "codes.xml";
    private final static String newsFile = "news.xml";
    private final static String portfolioFile = "portfolio.xml";
    private final static String DATA_ARCHIVE_DATE_PATTERN = "yyyy-MM-dd";
    private final static String DSEX_DATA_ARCHIVE_DATE_PATTERN = "MMM dd, yyyy";
    private final static String PORTFOLIO_DATE_PATTERN = "dd/MM/yyyy";
    private final static String DSEX_DATA_SYNC_DATE_PATTERN = "MMM dd, yyyy";
    private final String SKIP_CODE_PATTERN = "(T\\d+Y\\d+|.*dse.*|DEB.*)";
    private final long HTTP_TIMEOUT_1_MINUTE = 60000;
    private Map params;

    public Crawler(ScraperConfiguration scraperConfig, Item item, CrawlType crawlType, Map params) {
        this.scraperConfig = scraperConfig;
        this.item = item;
        this.crawlType = crawlType;
        this.params = params;
    }

    public static ScraperConfiguration getScraperConfig(ServletContext context, String configPath, CrawlType crawlType) throws FileNotFoundException {
        
        if(context!=null){
            configPath = context.getRealPath("/") + "/WEB-INF/classes/";
        }
        
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
            case DSEX_DATA_ARCHIVE:
                if (DSEX_DATA_ARCHIVE_CONFIG == null) {
                    DSEX_DATA_ARCHIVE_CONFIG = new ScraperConfiguration(configPath + dsexDataArchiveFile);
                }
                return DSEX_DATA_ARCHIVE_CONFIG;
            case DSEX_DATA_SYNC:
                if (DSEX_DATA_SYNC_CONFIG == null) {
                    DSEX_DATA_SYNC_CONFIG = new ScraperConfiguration(configPath + dsexDataSyncFile);
                }
                return DSEX_DATA_SYNC_CONFIG;
            case CODE_NAMES:
                if (CODE_NAMES_CONFIG == null) {
                    CODE_NAMES_CONFIG = new ScraperConfiguration(configPath + codeNamesFile);
                }
                return CODE_NAMES_CONFIG;
            case NEWS:
                if (NEWS_CONFIG == null) {
                    NEWS_CONFIG = new ScraperConfiguration(configPath + newsFile);
                }
                return NEWS_CONFIG;
            case PORTFOLIO_SYNC:
                if (PORTFOLIO_CONFIG == null) {
                    PORTFOLIO_CONFIG = new ScraperConfiguration(configPath + portfolioFile);
                }
                return PORTFOLIO_CONFIG;
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
            } else if (crawlType.equals(CrawlType.DSEX_DATA_ARCHIVE)) {
                crawlDsexDataArchive();
            } else if (crawlType.equals(CrawlType.DSEX_DATA_SYNC)) {
                crawlDsexDataSync();
            } else if (crawlType.equals(CrawlType.CODE_NAMES)) {
                crawlCodeNames();
            } else if (crawlType.equals(CrawlType.NEWS)) {
                crawlNews();
            } else if (crawlType.equals(CrawlType.PORTFOLIO_SYNC)) {
                crawlPortfolio();
            }
        } catch (Exception ex) {
            //System.out.println("Error caught: " + ex.getMessage() + ", skipping " + getItem());
            //ex.printStackTrace();
            //this.interrupt();
        }
    }
    
    private void crawlPortfolio() {
        Scraper scraper = new Scraper(scraperConfig, "d:/expekt");
        //int remoteId = (int) params.get("REMOTE_ID");
        //String url = PORTFOLIO_URL_PREFIX+remoteId;
        //scraper.addVariableToContext("url", url);
        scraper.setDebug(true);
        synchronized (scraper) {
            scraper.execute();
        }

        ListVariable variables = (ListVariable) scraper.getContext().get("portfolioDetails");
        List<PortfolioDetails> portfolioDetails = parsePortfolioDetails(variables.toString());
        getParams().put("PORTFOLIO_DETAILS", portfolioDetails);
    }
    
    private List<PortfolioDetails> parsePortfolioDetails(String domStr){
        Document doc;
        List<PortfolioDetails> portfolioDetails = new ArrayList<>();
        //Portfolio portfolio = (Portfolio) this.getParams().get("PORTFOLIO");
        //System.out.println("passed portfolio: " + portfolio);

        try {
            InputStream is = new ByteArrayInputStream(domStr.getBytes());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);
            doc.normalizeDocument();

            NodeList nodeList = doc.getElementsByTagName("data");
            DateFormat dateFormat = new SimpleDateFormat(PORTFOLIO_DATE_PATTERN);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String dateStr = attributes.getNamedItem("buyDate").getNodeValue();
                String code = attributes.getNamedItem("code").getNodeValue();
                String buyPriceStr = attributes.getNamedItem("buyPrice").getNodeValue();
                String sharesStr = attributes.getNamedItem("shares").getNodeValue();

                PortfolioDetails portfolioDetail = new PortfolioDetails();
                portfolioDetail.setCode(code);
                portfolioDetail.setDate(dateFormat.parse(dateStr));
                float buyPrice = Float.parseFloat(buyPriceStr);
                int shares = Integer.parseInt(sharesStr);
                portfolioDetail.setQuantity(shares);
                portfolioDetail.setBuyPrice(buyPrice);

                portfolioDetails.add(portfolioDetail);
            }
        } catch (SAXException | IOException | ParserConfigurationException | ParseException ex) {
            System.out.println("Exception caught in parsing xml: " + ex.getMessage() );
            ex.printStackTrace();
            return new ArrayList<>();
        }

        return portfolioDetails;
    }

    private void crawlNews() throws ParserConfigurationException, ParseException, XPathExpressionException, SAXException, IOException {
        Scraper scraper = new Scraper(scraperConfig, "d:/expekt");
        String url = NEWS_URL;
        scraper.addVariableToContext("url", url);
        scraper.setDebug(true);

        synchronized (scraper) {
            scraper.execute();
        }

        ListVariable variables = (ListVariable) scraper.getContext().get("newses");

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        XPath xPath = XPathFactory.newInstance().newXPath();
        DateFormat dateFormat = new SimpleDateFormat(DATA_ARCHIVE_DATE_PATTERN);
        
        List<ItemNews> newses = new ArrayList<>();
        for (Iterator it = variables.toList().iterator(); it.hasNext();) {
            String newsNode =  it.next().toString();
            ItemNews news = parseNews(newsNode, dBuilder, xPath, dateFormat);
            if(news != null)
                newses.add(news);
        }
        
        getParams().put("newses", newses);
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
        for (Object code_name : variables.toList()) {
            codes.add(code_name.toString());
        }
        Collections.sort(codes);

        List<Item> items = new ArrayList<>();
        for (String code : codes) {
            if (!code.matches(SKIP_CODE_PATTERN)) {
                items.add(new Item(code));
                //System.out.println("code: " + code);
            }
        }

        System.out.println("code size: " + items.size());
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
    
    private void crawlDsexDataArchive() {
        Scraper scraper = new Scraper(scraperConfig, "d:/expekt");
        String url = DSEX_DATA_ARCHIVE_URL;
        scraper.addVariableToContext("url", url);
        scraper.addVariableToContext("startDate", getParams().get("startDate"));
        scraper.addVariableToContext("endDate", getParams().get("endDate"));
        scraper.setDebug(true);
        synchronized (scraper) {
            scraper.execute();
        }

        ListVariable variables = (ListVariable) scraper.getContext().get("items");
        List<Item> items = parseDsexXML(variables.toString());
        getParams().put("items", items);
    }
    
    private void crawlDsexDataSync() throws ParseException {
        Scraper scraper = new Scraper(scraperConfig, "d:/expekt");
        scraper.setDebug(true);
        synchronized (scraper) {
            scraper.execute();
        }

        String dateStr = scraper.getContext().get("date").toString();
        dateStr = dateStr.substring("Last update on".length());
        dateStr = dateStr.substring(0, dateStr.indexOf("at")-1).trim();
        DateFormat dateFormat = new SimpleDateFormat(DSEX_DATA_SYNC_DATE_PATTERN);
        Date date = dateFormat.parse(dateStr);
        String str = scraper.getContext().get("index").toString();
        float index = Float.parseFloat(str);
        str = scraper.getContext().get("change").toString();
        float change = Float.parseFloat(str);
        str = scraper.getContext().get("trade").toString();
        int trade = Integer.parseInt(str);
        str = scraper.getContext().get("value").toString();
        float value = Float.parseFloat(str);
        
        item.setDate(date);
        item.setClosePrice(index);
        item.setLastPrice(index);
        item.setAdjustedClosePrice(index);
        item.setYesterdayClosePrice(index-change);
        item.setAdjustedYesterdayClosePrice(index-change);
        item.setTrade(trade);
        item.setValue(value);
    }

    private ItemNews parseNews(String domStr, DocumentBuilder dBuilder, XPath xPath, DateFormat dateFormat) throws ParseException, XPathExpressionException, SAXException, IOException {
        Document doc;
        ItemNews itemNews = null;

        InputStream is = new ByteArrayInputStream(domStr.getBytes());
        doc = dBuilder.parse(is);
        doc.normalizeDocument();
        
        NodeList codeNode = (NodeList) xPath.evaluate("/tbody/tr[1]/td[2]/text()", doc.getDocumentElement(), XPathConstants.NODESET);
        String code = codeNode.item(0).getNodeValue();

        NodeList newsNode = (NodeList) xPath.evaluate("/tbody/tr[2]/td[2]/text()", doc.getDocumentElement(), XPathConstants.NODESET);
        String news = newsNode.item(0).getNodeValue();

        NodeList dateNode = (NodeList) xPath.evaluate("/tbody/tr[3]/td[2]/text()", doc.getDocumentElement(), XPathConstants.NODESET);
        String dateString = dateNode.item(0).getNodeValue();
        Date date = dateFormat.parse(dateString);

        if (Utils.getCodes(Utils.getCodes()).contains(code)) {
            itemNews = new ItemNews();
            itemNews.setCode(code);
            itemNews.setDate(date);
            itemNews.setNews(news);
        }

        return itemNews;
    }
    
    private List<Item> parseDsexXML(String domStr) {
        //System.out.println("domStr: " + domStr);
        Document doc;
        List<Item> items = new ArrayList<>();

        try {
            InputStream is = new ByteArrayInputStream(domStr.getBytes());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);
            doc.normalizeDocument();

            NodeList nodeList = doc.getElementsByTagName("data");
            DateFormat dateFormat = new SimpleDateFormat(DSEX_DATA_ARCHIVE_DATE_PATTERN);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String datePrefix = "Market Summary of ";
                String dateStr = attributes.getNamedItem("date").getNodeValue().substring(datePrefix.length());
                String closeStr = attributes.getNamedItem("close").getNodeValue().replace(",", "");
                String tradeStr = attributes.getNamedItem("trade").getNodeValue().replace(",", "");
                String changeStr = attributes.getNamedItem("change").getNodeValue().replace(",", "");
                String valueStr = attributes.getNamedItem("value").getNodeValue().replace(",", "");

                Item anItem = new Item();
                anItem.setCode("DSEX");
                anItem.setDate(dateFormat.parse(dateStr));
                float closePrice = Float.parseFloat(closeStr);
                float change = Float.parseFloat(changeStr);
                anItem.setClosePrice(closePrice);
                anItem.setAdjustedClosePrice(closePrice);
                anItem.setLastPrice(closePrice);
                anItem.setYesterdayClosePrice(closePrice-change);
                anItem.setTrade(Integer.parseInt(tradeStr));
                anItem.setValue(Float.parseFloat(valueStr));
                    items.add(anItem);
            }
        } catch (SAXException | IOException | ParserConfigurationException | ParseException ex) {
            System.out.println("Exception caught in parsing xml: " + ex.getMessage() + ", code: " + getItem().getCode());
            ex.printStackTrace();
            return new ArrayList<>();
        }

        return items;
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
            //System.out.println(getItem().getCode() + " size: " + nodeList.getLength());
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

                if (anItem.getLastPrice() != 0) {
                    items.add(anItem);
                }
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
        scraper.getHttpClientManager().getHttpClient().getParams().setConnectionManagerTimeout(HTTP_TIMEOUT_1_MINUTE*3);
        //System.out.println("Going to fetch " + item.getCode());
        synchronized (scraper) {
            scraper.execute();
        }
       // System.out.println("Fetch completed " + item.getCode());

        ListVariable variable = (ListVariable) scraper.getContext().get("range");
        String str = variable.toString();
        String[] lowHigh = str.split("-");
        float low = 0;
        float high = 0;

        if (str.length() >= 2) {
            low = Float.parseFloat(lowHigh[0].trim());
            high = Float.parseFloat(lowHigh[1].trim());
        }

        variable = (ListVariable) scraper.getContext().get("sector");
        String sector = variable.toString().trim();

        variable = (ListVariable) scraper.getContext().get("faceValue");
        str = variable.toString().trim();
        int faceValue = (int) Float.parseFloat(str);

        variable = (ListVariable) scraper.getContext().get("totalSecurity");
        str = variable.toString().trim().replace(",", "");
        int totalSecurity = Integer.parseInt(str);

        variable = (ListVariable) scraper.getContext().get("authorizedCapital");
        str = variable.toString().trim().replace(",", "");
        float authorizedCapital = Float.parseFloat(str);

        variable = (ListVariable) scraper.getContext().get("paidUpCapital");
        str = variable.toString().trim().replace(",", "");
        float paidUpCapital = Float.parseFloat(str);

        variable = (ListVariable) scraper.getContext().get("yearEnd");
        String yearEnd = variable.toString().trim();

        variable = (ListVariable) scraper.getContext().get("reserve");
        str = variable.toString().trim().replace(",", "");
        float reserve = Float.parseFloat(str);

        variable = (ListVariable) scraper.getContext().get("PE");
        char amp = (char) 160;
        str = variable.toString().trim().replace("" + amp, "");
        float PE = 0;
        if (str.isEmpty() || str.equals("n/a")) {
            PE = 0;
        } else {
            PE = Float.parseFloat(str);
        }

        variable = (ListVariable) scraper.getContext().get("category");
        String category = variable.toString().trim();

        variable = (ListVariable) scraper.getContext().get("director");
        str = variable.toString().trim().split(" ")[1];
        float director = Float.parseFloat(str);

        variable = (ListVariable) scraper.getContext().get("government");
        str = variable.toString().trim().split("Govt.")[1];
        float government = Float.parseFloat(str);

        variable = (ListVariable) scraper.getContext().get("institute");
        str = variable.toString().trim().split(" ")[1];
        float institute = Float.parseFloat(str);

        variable = (ListVariable) scraper.getContext().get("foreign");
        str = variable.toString().trim().split(" ")[1];
        float foreign = Float.parseFloat(str);

        variable = (ListVariable) scraper.getContext().get("public");
        str = variable.toString().trim().split(" ")[1];
        float publics = Float.parseFloat(str);

        item.setLow(low);
        item.setHigh(high);
        item.setSector(sector);
        item.setFaceValue(faceValue);
        item.setTotalSecurity(totalSecurity);
        item.setAuthorizedCapital(authorizedCapital);
        item.setPaidUpCapital(paidUpCapital);
        item.setYearEnd(yearEnd);
        item.setReserve(reserve);
        item.setPE(PE);
        item.setCategory(category);
        SharePercentage percentage = new SharePercentage(director, government, institute, foreign, publics);
        item.setSharePercentage(percentage);

        //System.out.println("Item: " + item);
    }

    private void crawlPrice() throws Exception {
        //ScraperConfiguration config = new ScraperConfiguration(context.getRealPath("/") + "/WEB-INF/classes/" + "volume.xml");
        Scraper scraper = new Scraper(scraperConfig, "/home/setu/expekt");
        String url = PRICE_URL + getItem().getCode();
        scraper.addVariableToContext("url", url);
        scraper.setDebug(true);
        scraper.getHttpClientManager().getHttpClient().getParams().setConnectionManagerTimeout(HTTP_TIMEOUT_1_MINUTE);
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
        //System.out.println(item.getCode() + " crawled");
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
        float pressure = ((float) greatest / (float) smallest);
        pressure = (sellVolume > buyVolume) ? -pressure : pressure;
        DecimalFormat df = new DecimalFormat("#.#");
        String pressureString = df.format(pressure);
        float returnValue = Float.parseFloat(pressureString);
        return returnValue;
    }

}
