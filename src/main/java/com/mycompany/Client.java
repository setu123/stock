package com.mycompany;

import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.model.Item;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ImportService;
import com.mycompany.service.ScannerService;
import com.mycompany.service.SyncService;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @date May 13, 2015
 * @author Setu
 */
public class Client {

    private final static String DATE_PATTERN = "dd-MM-yyyy";
    static DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, ParseException {

        //checkByDate();
        
        //checkByScript();
        
        checkByScript2();

    }
    
    private static void checkByScript2() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getOneYearData();
        ScannerService scanerService = new ScannerService();
        String script = "GEMINISEA";
        for (String code : oneYearData.keySet()) {
            if (!code.equals(script)) {
                continue;
            }

            List<Item> items = oneYearData.getItems(code);
            Collections.sort(items);
            
            
            

            for (int i = 3; i < items.size(); i++) {
                Item today = items.get(i);
                Item yesterday = items.get(i - 1);
                Item dayBeforeYesterday = items.get(i - 2);
                Item towDayBeforeYesterday = items.get(i - 3);
                
                float tcYesterday = (float) yesterday.getTrade() / (float) dayBeforeYesterday.getTrade();
                float vcYesterday = (float) yesterday.getVolume() / (float) dayBeforeYesterday.getVolume();
                float vtcRatioYesterday = vcYesterday / tcYesterday;
                float yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
                float yesterdaychange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;
                float dayBeforeYesterdayChange = ((dayBeforeYesterday.getAdjustedClosePrice() - towDayBeforeYesterday.getAdjustedClosePrice()) / towDayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float yesterdayTrade = yesterday.getTrade();
                
                float tcToday = (float) today.getTrade() / (float) yesterday.getTrade();
                float vcToday = (float) today.getVolume() / (float) yesterday.getVolume();
                float vtcRatioToday = vcToday / tcToday;
                float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
                float todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
                float todayTrade = today.getTrade();
                
                List itemSubList = new ArrayList();
                long totalTrade = 0;
                long totalVolume = 0;
                float yesterdayVolumePerTradeChange = 0;
                for(int j=0; j<=i; j++){
                    if(j==i){
                        float averageVolumePerTrade = (float)(totalVolume/totalTrade);
                        float yesterdayVolumePerTrade = yesterday.getAdjustedVolume()/yesterday.getTrade();
                        yesterdayVolumePerTradeChange = yesterdayVolumePerTrade/averageVolumePerTrade;
                    }
                    totalTrade += items.get(j).getTrade();
                    totalVolume += items.get(j).getAdjustedVolume();
                    itemSubList.add(items.get(j));
                }
                float averageVolumePerTrade = (float)(totalVolume/totalTrade);
                float todayVolumePerTrade = today.getAdjustedVolume()/today.getTrade();
                float volumePerTradeChange = todayVolumePerTrade/averageVolumePerTrade;
                
                float volumeChange = scanerService.calculateVolumeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_YEAR);
                float rsi = scanerService.calculateRSI(itemSubList);
                boolean isLastTwoDaysGreen = yesterdayGap>0 && dayBeforeYesterdayGap>0 && yesterdaychange>0.5 && dayBeforeYesterdayChange>0.5;
                float tradeChangeWithYesterday = (((float)today.getTrade()-(float)yesterday.getTrade())/(float)yesterday.getTrade())*100;
                float volumeChangeWithYesterday = today.getVolume()/yesterday.getVolume();
                boolean isSuddenHike = volumeChangeWithYesterday>=2 && volumeChange>=2;
                
                        
                        
                        
                //System.out.println("todayadjusted: " + today.getAdjustedClosePrice() + ", yesterdayadjusted: " + yesterday.getAdjustedClosePrice());
//                if (todaychange>1 && todayGap >= 0.5 && vtcRatioToday>1 && volumePerTradeChange<vtcRatioToday) {
//                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", todaychange: " + todaychange + ", todayGap: " + todayGap + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", volumePerTradeChange: " + volumePerTradeChange);
//                }
                
                
                //This is 2nd best choice
                if (todaychange>0.1 
                        && todayGap >= 0.5 
                        && vtcRatioToday>1.3 
                        && yesterdayVolumePerTradeChange<.80 
                        && volumePerTradeChange>.60 
                        && tradeChangeWithYesterday>10
                        && !isSuddenHike
                        && rsi<=65 
                        && !isLastTwoDaysGreen) {
                    System.out.println(today.getDate() + ", code: " + code + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday);
                } 
                
                if (todaychange>0.1 
                        && todayGap >= 0.5 
                        && vtcRatioToday>vtcRatioYesterday 
                        && vtcRatioToday>0.9
                        && volumePerTradeChange<vtcRatioToday 
                        && tradeChangeWithYesterday>5
                        //&& !isSuddenHike
                        && rsi<=65 
                        && !isLastTwoDaysGreen) {
                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday);
                }
                
                
                

                

            }
            
            
        }
    }

    private static void checkByScript() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getOneYearData();
        String script = "ABBANK";
        for (String code : oneYearData.keySet()) {
            if (!code.equals(script)) {
                continue;
            }

            List<Item> items = oneYearData.getItems(code);
            Collections.sort(items);

            for (int i = 2; i < items.size(); i++) {
                Item today = items.get(i);
                Item yesterday = items.get(i - 1);
                //Item dayBeforeYesterday = items.get(i - 2);
                float tc = (float) today.getTrade() / (float) yesterday.getTrade();
                float vc = (float) today.getVolume() / (float) yesterday.getVolume();
                float vtcRatio = vc / tc;
                float todayChange = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
                if (todayChange >= 1.5 && vtcRatio > 1.2) {
                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", vtcRatio: " + vtcRatio);
                }
            }
        }
    }

    private static void checkByDate() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getOneYearData();

        for (String code : oneYearData.keySet()) {
            List<Item> items = oneYearData.getItems(code);
            Collections.sort(items);
//            Item item = new Item();
//            item.setCode(code);

            String dateStr = "15-04-2015";
            Date date = dateFormat.parse(dateStr);
            for (int i = 1; i < items.size(); i++) {
                if (items.get(i).getDate().equals(date)) {
                    Item today = items.get(i);
                    Item yesterday = items.get(i - 1);
                    float tc = (float) today.getTrade() / (float) yesterday.getTrade();
                    float vc = (float) today.getVolume() / (float) yesterday.getVolume();
                    float vtcRatio = vc / tc;
                    float todayChange = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
                    if (todayChange >= 1.5 && vtcRatio > 1.1) {
                        System.out.println("Date: " + today.getDate() + ", code: " + code + ", vtcRatio: " + vtcRatio);
                    }
                    break;
                }
            }
        }

        dao.close();
    }

}
