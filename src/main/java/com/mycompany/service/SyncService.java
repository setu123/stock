package com.mycompany.service;

import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.model.Item;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
        
        try {
            List<Item> items = Utils.getCodes();
            selectTopItems(items);
            fetchBSVolume(items);
            items = getValidItems(items);
            System.out.println("Fetching completed, going to update database");

            if (items.isEmpty()) {
                System.out.println("Skipping update");
                return;
            }

            ItemDaoImpl dao = new ItemDaoImpl();
            dao.open();
            dao.setItems(items);
            dao.close();
        } catch (MalformedURLException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SyncService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Calendar end = Calendar.getInstance();
        long elapsedTime = (end.getTimeInMillis() - start.getTimeInMillis())/1000;
        System.out.println("Sync completed in " + elapsedTime/60 + " minutes " + elapsedTime%60 + " seconds");
    }

    public void syncYearStatistics() throws IOException {
        try {
            List<Item> items = Utils.getCodes();
            fetchYearStatistics(items);

            ItemDaoImpl itemDao = new ItemDaoImpl();
            itemDao.setYearStatistics(items);
        } catch (MalformedURLException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SyncService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fetchYearStatistics(List<Item> items) throws MalformedURLException, IOException, InterruptedException {
        List<Crawler> crawlers = new ArrayList<>();
        ScraperConfiguration config = Crawler.getScraperConfig(context, Crawler.CrawlType.ITEM_YEAR_STATISTICS);
        for (Item item : items) {
            Crawler crawler = new Crawler(config, item, Crawler.CrawlType.ITEM_YEAR_STATISTICS, null);
            crawler.start();
            crawlers.add(crawler);
        }

        for (Crawler crawler : crawlers) {
            crawler.join();
        }
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
    
    public float getPriceDiffWithPreviousLow(List<Item> items, int days){
        int size = items.size();
        int counter = 0;
        for(int i=size-2; i>=0; i--)
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
            ScraperConfiguration config = Crawler.getScraperConfig(context, Crawler.CrawlType.CODE_NAMES);
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
        ScraperConfiguration config = Crawler.getScraperConfig(context, Crawler.CrawlType.ITEM_PRICE);
        for (Item item : items) {
            Crawler crawler = new Crawler(config, item, Crawler.CrawlType.ITEM_PRICE, null);
            crawler.start();
            crawlers.add(crawler);
        }

        for (Crawler crawler : crawlers) {
            crawler.join();
        }
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            JobDataMap data = jec.getJobDetail().getJobDataMap();
            this.context = (ServletContext) data.get("context");
            System.out.println("This is job from SyncService");
            sync();

        } catch (Exception ex) {
            Logger.getLogger(SyncService.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Exception: " + ex);
        }
    }

}
