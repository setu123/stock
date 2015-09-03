package com.mycompany.model;

import static com.mycompany.service.ScannerService.TRADING_DAYS_IN_2_MONTH;
import java.util.List;

/**
 * @date Aug 20, 2015
 * @author Setu
 */
public class ChangesInVolumePerTrade {
    private float todayChange;
    private float yesterdayChange;
    private float dayBeforeYesterdayChange;

    private ChangesInVolumePerTrade() {
    }

    public float getTodayChange() {
        return todayChange;
    }

    private void setTodayChange(float todayChange) {
        this.todayChange = todayChange;
    }

    public float getYesterdayChange() {
        return yesterdayChange;
    }

    private void setYesterdayChange(float yesterdayChange) {
        this.yesterdayChange = yesterdayChange;
    }

    public float getDayBeforeYesterdayChange() {
        return dayBeforeYesterdayChange;
    }

    private void setDayBeforeYesterdayChange(float dayBeforeYesterdayChange) {
        this.dayBeforeYesterdayChange = dayBeforeYesterdayChange;
    }
    
    public static ChangesInVolumePerTrade getChangesInVolumePerTrade(List<Item> items, int days){
        ChangesInVolumePerTrade changes = new ChangesInVolumePerTrade();
        int size = items.size();
        
        if (size == 1) {
            return changes;
        }
        
        Item today = items.get(size-1);
        Item yesterday = items.get(size-2);
        Item dayBeforeYesterday = items.get(size-3);

        if (days <= 0) {
            days = TRADING_DAYS_IN_2_MONTH;     //Trading days in a year
        }
        
        long totalVolume = 0;
        int totalTrade = 0;
        
        Item start = items.get(size - 2);
        Item end = new Item();
        for (int i = size - 2; (i >= 0 && (size - i) < (days + 2)); i--) {
            totalVolume += items.get(i).getAdjustedVolume();
            totalTrade  += items.get(i).getTrade();
            end = items.get(i);
        }
        
        float averageVolumePerTradeForToday =  ((float)totalVolume / (float)totalTrade);
        float todayVolumePerTrade = (float)today.getAdjustedVolume() / (float)today.getTrade();
        float todayVolumePerTradeChange = todayVolumePerTrade / averageVolumePerTradeForToday;
        changes.setTodayChange(todayVolumePerTradeChange);
        
        float averageVolumePerTradeForYesterday =  ((float)(totalVolume-yesterday.getAdjustedVolume()) / (float)(totalTrade-yesterday.getTrade()));
        float yesterdayVolumePerTrade = (float)yesterday.getAdjustedVolume() / (float)yesterday.getTrade();
        float yesterdayVolumePerTradeChange = yesterdayVolumePerTrade / averageVolumePerTradeForYesterday;
        changes.setYesterdayChange(yesterdayVolumePerTradeChange);
        
        float averageVolumePerTradeForDeyBeforeYesterday =  ((totalVolume-yesterday.getAdjustedVolume()-dayBeforeYesterday.getAdjustedVolume()) / (totalTrade-yesterday.getTrade()-dayBeforeYesterday.getTrade()));
        float dayBeforeYesterdayVolumePerTrade = yesterday.getAdjustedVolume() / yesterday.getTrade();
        float dayBeforeYesterdayVolumePerTradeChange = dayBeforeYesterdayVolumePerTrade / averageVolumePerTradeForDeyBeforeYesterday;
        changes.setDayBeforeYesterdayChange(dayBeforeYesterdayVolumePerTradeChange);
        
        //System.out.println("change- today: " + today.getDate() + ", start: " + start.getDate() + ", end: " + end.getDate());
        
        return changes;
    }
    
    public static ChangesInVolumePerTrade getChangesInVolumePerTrade(List<Item> items, int head, int days){
        ChangesInVolumePerTrade changes = new ChangesInVolumePerTrade();
        int size = items.size();
        
        if (size == 1) {
            return changes;
        }
        
        Item today = items.get(head);
        Item yesterday = items.get(head-1);
        Item dayBeforeYesterday = items.get(head-2);

        if (days <= 0) {
            days = TRADING_DAYS_IN_2_MONTH;     //Trading days in a year
        }
        
        long totalVolume = 0;
        int totalTrade = 0;
        
        for (int i = head - 1; (i >= 0 && (head - i) < (days + 2)); i--) {
            totalVolume += items.get(i).getAdjustedVolume();
            totalTrade  += items.get(i).getTrade();
        }
        
        float averageVolumePerTradeForToday =  (totalVolume / totalTrade);
        float todayVolumePerTrade = today.getAdjustedVolume() / today.getTrade();
        float todayVolumePerTradeChange = todayVolumePerTrade / averageVolumePerTradeForToday;
        changes.setTodayChange(todayVolumePerTradeChange);
        
        float averageVolumePerTradeForYesterday =  ((totalVolume-yesterday.getAdjustedVolume()) / (totalTrade-yesterday.getTrade()));
        float yesterdayVolumePerTrade = yesterday.getAdjustedVolume() / yesterday.getTrade();
        float yesterdayVolumePerTradeChange = yesterdayVolumePerTrade / averageVolumePerTradeForYesterday;
        changes.setYesterdayChange(yesterdayVolumePerTradeChange);
        
        float averageVolumePerTradeForDeyBeforeYesterday =  ((totalVolume-yesterday.getAdjustedVolume()-dayBeforeYesterday.getAdjustedVolume()) / (totalTrade-yesterday.getTrade()-dayBeforeYesterday.getTrade()));
        float dayBeforeYesterdayVolumePerTrade = yesterday.getAdjustedVolume() / yesterday.getTrade();
        float dayBeforeYesterdayVolumePerTradeChange = dayBeforeYesterdayVolumePerTrade / averageVolumePerTradeForDeyBeforeYesterday;
        changes.setDayBeforeYesterdayChange(dayBeforeYesterdayVolumePerTradeChange);
        
        return changes;
    }
}
