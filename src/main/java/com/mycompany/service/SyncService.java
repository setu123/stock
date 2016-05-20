package com.mycompany.service;

import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.dao.PortfolioDaoImpl;
import com.mycompany.model.Item;
import com.mycompany.model.Portfolio;
import com.mycompany.model.PortfolioDetails;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.webharvest.definition.ScraperConfiguration;

/**
 * @date Apr 16, 2015
 * @author Setu
 */
public class SyncService implements Job {

    private static final String WATCH_MATRIX_URL = "http://www.stockbangladesh.com/grids/watch";
    private static final int NUMBER_OF_ITEM_TO_CRAWL = 330; //Means all
    public static final int PORTFOLIO_ID = 1;

    ObjectMapper objectMapper;
    private ServletContext context = null;
    private static List<Item> allItems;
    
    public SyncService() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public SyncService(ServletContext context) {
        this.context = context;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

//    public SyncService() {
//        objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    }
    public void sync() throws IOException {
        Calendar start = Calendar.getInstance();
        ItemDaoImpl dao = null;
        
        try {
            List<Item> items = Utils.getCodes();
            System.out.println("codes size: " + items.size());
            selectTopItems(items);
            System.out.println("Check 1");
            fetchBSVolume(items);
            System.out.println("Check 2");
            items = getValidItems(items);

            if (items.isEmpty()) {
                System.out.println("Skipping update");
                return;
            }
            
            System.out.println("Check 3");
            
            //Get dse item
            //ImportService importService = new ImportService(dao);
            Item dsex = fetchDSEXIndex();
            
            dao = new ItemDaoImpl();
            dao.open();
            updateHighLow(dao, dsex);
            items.add(dsex);
            System.out.println("Fetching completed, going to update database..");

            dao.setItems(items);
            dao.close();
        } catch (MalformedURLException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SyncService.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exception caught in syncing");
            ex.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(SyncService.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exception2 caught in syncing");
            ex.printStackTrace();
        }
        
        Calendar end = Calendar.getInstance();
        long elapsedTime = (end.getTimeInMillis() - start.getTimeInMillis())/1000;
        System.out.println("Sync completed in " + elapsedTime/60 + " minutes " + elapsedTime%60 + " seconds");
    }

    private void updateHighLow(ItemDaoImpl dao, Item dsex) throws SQLException, ClassNotFoundException{
            Item dbItem = dao.getItem(dsex.getCode(), dsex.getDate());
            
            //System.out.println("dbitem: " + dbItem);
            //There is no data for today yet
            if(dbItem == null){
                dsex.setDayHigh(dsex.getOpenPrice());
                dsex.setDayLow(dsex.getOpenPrice());
                return;
            }
            
            if(dbItem.getDayHigh()!=0 && dbItem.getDayHigh()>dsex.getDayHigh())
                dsex.setDayHigh(dbItem.getDayHigh());
            
            if(dbItem.getDayLow()!=0 && dbItem.getDayLow()<dsex.getDayLow())
                dsex.setDayLow(dbItem.getDayLow());
            
            //System.out.println("2dsex low: " + dsex.getDayLow());
            //System.out.println("2dsex high: " + dsex.getDayHigh());
    }

    public void syncPortfolio() throws IOException {
        Calendar start = Calendar.getInstance();
        PortfolioDaoImpl dao = null;
        Portfolio portfolio = new Portfolio(PORTFOLIO_ID);
        
        try {            
            ScraperConfiguration config = Crawler.getScraperConfig(context, null, Crawler.CrawlType.PORTFOLIO_SYNC);
            Crawler crawler = new Crawler(config, null, Crawler.CrawlType.PORTFOLIO_SYNC, null);
            crawler.getParams().put("PORTFOLIO", portfolio);
            crawler.start();
            crawler.join();
            
            List<PortfolioDetails> portfolioDetails = (List<PortfolioDetails>) crawler.getParams().get("PORTFOLIO_DETAILS");
            //System.out.println("portfolio size: " + portfolioDetails.size() + ", portfolio: " + portfolioDetails.get(0).getPortfolio().getId());
            
            dao = new PortfolioDaoImpl();
            dao.open();
            dao.updatePortfolioDetails(portfolioDetails, PORTFOLIO_ID);
            dao.close();
        } catch (InterruptedException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(SyncService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Calendar end = Calendar.getInstance();
        long elapsedTime = (end.getTimeInMillis() - start.getTimeInMillis())/1000;
        System.out.println("Portfolio sync completed in " + elapsedTime/60 + " minutes " + elapsedTime%60 + " seconds");
    }
    
    public void syncYearStatistics() throws IOException {
        try {
            List<Item> items = Utils.getCodes();
            //System.out.println("total item size: " + items.size());
            
            int chunkSize = items.size()/10;
            if(chunkSize == 0)
                chunkSize = 1;
            
            List<Item> itemSublist = new ArrayList<>();
            
            for (int i = 0; i < items.size(); i++) {
                if(i%chunkSize == 0){
                    if(itemSublist.size()>0){
                        System.out.println("going to fetch items untill: " + i);
                        fetchYearStatistics(itemSublist);
                    }
                    
                    itemSublist = new ArrayList<>();
                }
                
                itemSublist.add(items.get(i));
            }
            
            fetchYearStatistics(itemSublist);

            ItemDaoImpl itemDao = new ItemDaoImpl();
            itemDao.open();
            //System.out.println("in syncyear percentage: " + items.get(0).getSharePercentage());
            itemDao.setYearStatistics(items);
            itemDao.close();
        } catch (MalformedURLException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SyncService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fetchYearStatistics(List<Item> items) throws MalformedURLException, IOException, InterruptedException {
        System.out.println("subitem size: " + items.size());
        
        List<Crawler> crawlers = new ArrayList<>();
        ScraperConfiguration config = Crawler.getScraperConfig(context, null, Crawler.CrawlType.ITEM_YEAR_STATISTICS);
        //int counter = 0;
        for (Item item : items) {
            Crawler crawler = new Crawler(config, item, Crawler.CrawlType.ITEM_YEAR_STATISTICS, null);
            crawler.start();
            crawlers.add(crawler);
            System.gc();
            //++counter;
            //if(counter==2)
            //    break;
        }

        for (Crawler crawler : crawlers) {
            crawler.join();
            }
            
        System.out.println("fetch completed: " + items.size());
    }
        
    private List<Item> getValidItems(List<Item> items) {
        List<Item> validItems = new ArrayList();
        for (Item item : items) {
            if (item.isValid()) {
                validItems.add(item);
            }
        }

        return validItems;
    }

//    private boolean isEligibleToUpdate(List<Item> items){
//        for(Item item: items)
//            if(item.getBuyVolume() == 0 && item.getSellVolume()==0){
//                System.out.println("Both buyvolume and sellvolume 0 from  " + item.getCode() + ", Item: " + item);
//                    return false;
//            }
//        
//        return true;
//    }
//    public List<Item> getWatchMatrix() throws MalformedURLException, IOException, ConnectException, InterruptedException{
//        URLConnection connection = new URL(WATCH_MATRIX_URL).openConnection();
//        InputStream response = connection.getInputStream();
//        
//        JsonNode jsonNode = objectMapper.readTree(response);
//        jsonNode = jsonNode.findValue("maingrid");
//        List<Item> items = objectMapper.readValue(jsonNode, new TypeReference<List<Item>>(){});
//        System.out.println("items: " + items.size());
//        
//        Collections.sort(items);
//        Collections.reverse(items);
//        
//        return items;
//    }
    
    @Deprecated
    public List<Item> getCodes() throws MalformedURLException, IOException, InterruptedException {
        if (allItems == null) {
            ScraperConfiguration config = Crawler.getScraperConfig(context, null, Crawler.CrawlType.CODE_NAMES);
            Crawler crawler = new Crawler(config, null, Crawler.CrawlType.CODE_NAMES, null);
            crawler.start();
            crawler.join();
            allItems = (List<Item>) crawler.getParams().get("items");
        }
        return allItems;
    }

    private void selectTopItems(List<Item> items) {
        while (items.size() > NUMBER_OF_ITEM_TO_CRAWL) {
            items.remove(NUMBER_OF_ITEM_TO_CRAWL);
        }
    }

    public void fetchBSVolume(List<Item> items) throws MalformedURLException, IOException, InterruptedException {
        List<Crawler> crawlers = new ArrayList<>();
        ScraperConfiguration config = Crawler.getScraperConfig(context, null, Crawler.CrawlType.ITEM_PRICE);
        for (Item item : items) {
            if(item.getCode().equals("DSEX"))
                continue;
            Crawler crawler = new Crawler(config, item, Crawler.CrawlType.ITEM_PRICE, null);
            crawler.start();
            crawlers.add(crawler);
        }

        int counter = 0;
        for (Crawler crawler : crawlers) {
            crawler.join();
            ++counter;
            //System.out.println((crawlers.size()-counter) + " to be finished yet");
        }
    }
    
    public Item fetchDSEXIndex() throws MalformedURLException, IOException, InterruptedException {
        //System.out.println("fetchDSEXIndex");
        ScraperConfiguration config = Crawler.getScraperConfig(context, null, Crawler.CrawlType.DSEX_DATA_SYNC);
        Item dsex = new Item("DSEX");
        Crawler crawler = new Crawler(config, dsex, Crawler.CrawlType.DSEX_DATA_SYNC, null);
        crawler.start();
        crawler.join();
        
        return dsex;
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            JobDataMap data = jec.getJobDetail().getJobDataMap();
            this.context = (ServletContext) data.get("context");
            System.out.println("This is job from SyncService at " + new Date());
            sync();

        } catch (Exception ex) {
            Logger.getLogger(SyncService.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exception: " + ex);
        }
    }

}
