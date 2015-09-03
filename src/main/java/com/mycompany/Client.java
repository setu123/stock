package com.mycompany;

import bsh.util.Util;
import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.model.ChangesInVolumePerTrade;
import com.mycompany.model.Item;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ImportService;
import com.mycompany.service.ScannerService;
import com.mycompany.service.SyncService;
import com.mycompany.service.Utils;
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
        checkByScript();

        //checkByScript2();
        //getBSPressure();
        //hammer();
    }

    private static void checkByScript() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getData(365);
        ScannerService scanerService = new ScannerService();
        String script = "PREMIERCEM";
        //List<Item> codes = Utils.getCodes();
        for (String code : oneYearData.keySet()) {
//            if (!code.equals(script)) {
//                continue;
//            }

            List<Item> items = oneYearData.getItems(code);
            scanerService.calculateVolumePerTradeChange(items, ScannerService.TRADING_DAYS_IN_2_MONTH);
            Collections.sort(items);

            float previousVolumeChange = 0;
            float previousTradeChange = 0;
            float previousYesterdayVolumePerTradeChange = 0;

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
                //priceGap is tuned for higher price stocks
                todayGap = todayGap + today.getAdjustedClosePrice()/1000;
                float todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
                float todayTrade = today.getTrade();
                float todayValue = today.getValue();

//                List<Item> itemSubList = new ArrayList();
//                long totalTrade = 0;
//                long totalVolume = 0;
//                float yesterdayVolumePerTradeChange = 0;
//                for (int j = 0; j <= i; j++) {
//                    if (j == i) {
//                        float averageVolumePerTrade = (float) (totalVolume / totalTrade);
//                        float yesterdayVolumePerTrade = yesterday.getAdjustedVolume() / yesterday.getTrade();
//                        yesterdayVolumePerTradeChange = yesterdayVolumePerTrade / averageVolumePerTrade;
//                    }
//                    totalTrade += items.get(j).getTrade();
//                    totalVolume += items.get(j).getAdjustedVolume();
//                    itemSubList.add(items.get(j));
//                }
                
                
                List<Item> itemSubList = new ArrayList();
                int counter = 0;
                for(int j=i; j>=0; j--){
                    itemSubList.add(items.get(j));
                    if(counter >= ScannerService.TRADING_DAYS_IN_2_MONTH)
                        break;
                    ++counter;
                }
                Collections.sort(itemSubList);
                
//                float averageVolumePerTrade = (totalVolume / totalTrade);
//                float todayVolumePerTrade = today.getAdjustedVolume() / today.getTrade();
//                float volumePerTradeChange = todayVolumePerTrade / averageVolumePerTrade;
                
                //if(today.getCode().equals("WATACHEM"))
                //    System.out.println("i: " + i + ", todayHead: " + today.getDate() + ", subitemHead: " + itemSubList.get(itemSubList.size()-1).getDate());
                
//                ChangesInVolumePerTrade changes = ChangesInVolumePerTrade.getChangesInVolumePerTrade(itemSubList, ScannerService.TRADING_DAYS_IN_2_MONTH);
//                float volumePerTradeChange = changes.getTodayChange();
//                float yesterdayVolumePerTradeChange = changes.getYesterdayChange();
                
                float volumePerTradeChange = today.getVolumePerTradeChange();
                float yesterdayVolumePerTradeChange = yesterday.getVolumePerTradeChange();
                Item maximumVolumePerTradeChange = scanerService.getMaximumVolumePerTradeChange(items, i-1, ScannerService.TRADING_DAYS_IN_2_MONTH/2);

                float volumeChange = scanerService.calculateVolumeChange(itemSubList, ScannerService.TRADING_DAYS_IN_2_MONTH);
                float tradeChange = scanerService.calculateTradeChange(itemSubList, ScannerService.TRADING_DAYS_IN_2_MONTH);
                float rsi = scanerService.calculateRSI(itemSubList);
                boolean isLastTwoDaysGreen = yesterdayGap > 0 && dayBeforeYesterdayGap > 0 && yesterdaychange > 0.5 && dayBeforeYesterdayChange > 0.5;
                float tradeChangeWithYesterday = ((float) today.getTrade() / (float) yesterday.getTrade());
                float volumeChangeWithYesterday = ((float) today.getVolume() / (float) yesterday.getVolume());
                boolean isSuddenHike = volumeChangeWithYesterday >= 2 && volumeChange >= 2;
                float diffWithPreviousLow10 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 10);
                float diffWithPreviousLow3 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 3);
                float diffWithPreviousHighVolume = scanerService.getVolumeDiffWithPreviousHigh(itemSubList, ScannerService.TRADING_DAYS_IN_2_MONTH/2);
                float hammer = getHammer(today);
                scanerService.calculateDivergence(itemSubList);
                int divergence = itemSubList.get(itemSubList.size()-1).getDivergence();
                boolean consecutive3DaysGreen = scanerService.isConsecutive3DaysGreen(itemSubList);
                float lastFiewDaysVariation = scanerService.getLastFiewDaysVariation(itemSubList, ScannerService.TRADING_DAYS_IN_A_WEEK);
                int upDayCount7 = scanerService.getUpDayCount(items, i, 7);
                int upDayCount4 = scanerService.getUpDayCount(items, i, 4);

//                if ((todaychange > 0.5 && todayGap >= 0)
//                        && !(yesterdayVolumePerTradeChange > 1 && previousYesterdayVolumePerTradeChange > 1)
//                        && ((volumePerTradeChange < 1.2
//                        && (vtcRatioToday > 0.95 && (vtcRatioToday - vtcRatioYesterday) > -0.3)
//                        && (volumeChange > 1.5 && tradeChange > 2)
//                        && (volumeChangeWithYesterday > 1)))
//                        && diffWithPreviousLow < 12) {
//                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday + ", diffWithPreviousLow: " + diffWithPreviousLow);
//                }
                
                if ((todaychange>0.5 && todayGap >= 0)
//                        && !(yesterdayVolumePerTradeChange>1 && previousYesterdayVolumePerTradeChange>1)
                        && (volumePerTradeChange > 1.2 && volumePerTradeChange < 2)
                        //&& (vtcRatioToday>0.95 && (vtcRatioToday-vtcRatioYesterday)>-0.3 )
                        && ( tradeChange>1.2 && tradeChange<3 )
                        && (volumeChangeWithYesterday > 1)
                        
                        && (diffWithPreviousLow10<12 && diffWithPreviousLow3<8)
                        && todayTrade > 70
                        && todayValue > 1.5
                        && (hammer <= 2 && todayGap >= hammer )
                        //&& divergence < 20
                        && rsi < 75
                        && todayGap <6
                        && today.getAdjustedClosePrice() >8
                        && diffWithPreviousHighVolume > 1
                        && !consecutive3DaysGreen
                        //&& volumeChange/lastFiewDaysVariation >0.6
                        //&& upDayCount7 < 5
                        //&& upDayCount4 < 3
//                        && !(maximumVolumePerTradeChange.getVolumePerTradeChange()>1.1 && maximumVolumePerTradeChange.getTradeChange()>1.1)
                   ) {
                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday + ", diffWithPreviousLow: " + diffWithPreviousLow10 + ", rsi: " + rsi + ", hammer: " + hammer + ", divergence: " + divergence + ", diffWithPreviousHighVolume: " + diffWithPreviousHighVolume + ", lastFiewDaysVariation: " + lastFiewDaysVariation + ", maximumVolumePerTradeChange: " + maximumVolumePerTradeChange.getVolumePerTradeChange()) ;
                    //System.out.println("volumePerTradeChange: " + volumePerTradeChange + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", changeToday: " + today.getVolumePerTradeChange() + ", changeYesterday: " + yesterday.getVolumePerTradeChange());
                }

                //previousVolumeChange = volumeChange;
                //previousTradeChange = tradeChange;
                //previousYesterdayVolumePerTradeChange = yesterdayVolumePerTradeChange;
            }
        }
    }
    
    private static float getHammer(Item item){
        float difference = Math.abs(item.getOpenPrice() - item.getAdjustedClosePrice());
        float largest = Math.max(item.getOpenPrice(), item.getClosePrice());
        //float smallest  = (item.getOpenPrice() + item.getAdjustedClosePrice() - difference)/2;
        //float hammer = (((smallest-item.getLow())-(item.getHigh()-largest))/item.getAdjustedClosePrice())*100;
        float hammer = ((item.getHigh()-largest)/largest)*100;
        return hammer;
    }
}
