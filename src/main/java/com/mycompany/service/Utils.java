package com.mycompany.service;

import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.model.BasicInfo;
import com.mycompany.model.DividentHistory;
import com.mycompany.model.Item;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.webharvest.definition.ScraperConfiguration;

/**
 * @date May 22, 2015
 * @author Setu
 */
public class Utils {

    private static List<DividentHistory> dividentHistory;
    private static ItemDaoImpl dao = new ItemDaoImpl();
    private static final Map<String, List<DividentHistory>> dividentMap = new HashMap<>();
    public static Date today;
    public static Date yesterday;
    private static List<Item> allItems;

    private static List<DividentHistory> getDividentHistory() {
        if (dividentHistory == null) {
            if (dao == null) {
                dao = new ItemDaoImpl();
            }
            try {
                dao.open();
                dividentHistory = dao.getDividentHistory();
                dao.close();
            } catch (SQLException | ClassNotFoundException ex) {
                dividentHistory = new ArrayList<>();
                ex.printStackTrace();
            }
        }

        return dividentHistory;
    }

    public static String getConfigFilesPath() {
        Utils utils = new Utils();
        String splitter = "com/";
        String path = utils.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.split(splitter)[0];
        return path;
    }

    public static List<Item> getCodes() {
        if (allItems == null) {
            String path = getConfigFilesPath();
            
            try {
                ScraperConfiguration config = Crawler.getScraperConfig(null, path, Crawler.CrawlType.CODE_NAMES);
                Crawler crawler = new Crawler(config, null, Crawler.CrawlType.CODE_NAMES, null);
                crawler.start();
                crawler.join();
                allItems = (List<Item>) crawler.getParams().get("items");
            } catch (FileNotFoundException | InterruptedException ex) {
                allItems = new ArrayList<>();
            }
        }
        return allItems;
    }
    
    public static List<String> getCodes(List items) {
        List<String> codes = new ArrayList<>();
        for (Object item : items) {
            codes.add(((BasicInfo) item).getCode());
        }

        return codes;
    }

    private static List<DividentHistory> getHistory(String code) {
        List<DividentHistory> historyList = new ArrayList<>();
        for (DividentHistory history : getDividentHistory()) {
            if (history.getCode().equals(code)) {
                historyList.add(history);
            }
        }

        Collections.sort(historyList);
        return historyList;
    }

    public static List<DividentHistory> getDividentHistory(String code) {
        if (dividentMap.get(code) == null) {
            dividentMap.put(code, getHistory(code));
        }
        return dividentMap.get(code);
    }

    public static void updateDates(ItemDaoImpl itemDao) throws SQLException {
        today = itemDao.getToday();
        yesterday = itemDao.getYesterday();
    }

    static {
        try {
            dao.open();
            updateDates(dao);
            dao.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
