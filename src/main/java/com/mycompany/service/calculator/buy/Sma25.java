/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.service.calculator.buy;

import com.mycompany.model.Item;
import com.mycompany.model.Portfolio;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ScannerService;
import com.mycompany.service.calculator.SignalCalculator;
import static com.mycompany.service.calculator.SignalCalculator.averageValuePerTrade;
import static com.mycompany.service.calculator.SignalCalculator.today;
import java.util.List;

/**
 * @date Dec 17, 2015
 * @author setu
 */
public class Sma25 extends BuySignalCalculator {

    public Sma25(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        //intializeVariables(itemSubList, calculated);
                float change = (today.getOpenPrice()-yesterday.getAdjustedClosePrice())/yesterday.getAdjustedClosePrice();
        float todayValuePerTrade = today.getValue()/today.getTrade();
        float ratio = todayValuePerTrade/averageValuePerTrade;
        float oneThirdWay = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 1.5f + today.getOpenPrice();
        float oneFourthWay = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 1.1f + today.getOpenPrice();
        float yesterdayMax = Math.max(yesterday.getOpenPrice(), yesterday.getAdjustedClosePrice());
        float gap = today.getOpenPrice()-yesterdayMax;
        gap = (gap/yesterdayMax)*100;
        float sma25Diff = ((SignalCalculator.sma25-SignalCalculator.oneWeekAgoSma25)/SignalCalculator.oneWeekAgoSma25)*100;
        boolean gapLogic = gap>0.7? todayGap>=4:true;
        float plainGap = todayClosePrice-today.getOpenPrice();
        float oneWeekAgoEma9 = fourDayBeforeYesterday.getEmaList().get(9);
        float todayEma9 = today.getEmaList().get(9);
        boolean emaPass = todayEma9<=0?true:todayEma9>=oneWeekAgoEma9;
        float straightGap = Math.max(today.getOpenPrice(), today.getAdjustedClosePrice()) - Math.min(today.getOpenPrice(), today.getAdjustedClosePrice());
        float tails = ((today.getDayHigh() - today.getDayLow()) - straightGap);
        boolean isShaky = tails>straightGap;
        float topTail = ((today.getDayHigh()-Math.max(today.getOpenPrice(), today.getAdjustedClosePrice()))/Math.max(today.getOpenPrice(), today.getAdjustedClosePrice()))*100;
        float maxPriceDayMax = Math.max(maxPriceDay.getOpenPrice(), maxPriceDay.getAdjustedClosePrice());
        float changeWithMaxPriceDay = ((todayClosePrice-maxPriceDayMax)/maxPriceDayMax)*100;
        Item lowest = getLowest(itemSubList);
        float priceDiffWithMinimum = ((today.getAdjustedClosePrice() - lowest.getAdjustedClosePrice())/lowest.getAdjustedClosePrice())*100;
        
        Item bottom = getYearLowest(itemSubList);
        float changeWithBottom = 100;
        if (bottom != null) {
            changeWithBottom = ((today.getAdjustedClosePrice() - bottom.getAdjustedClosePrice()) / bottom.getAdjustedClosePrice()) * 100;
        }
        
        //System.out.println("date: " + today.getDate() + ", sma25: " + sma25 + ", yesterdaySma10: " + yesterdaySma10 + ", dayBeforeYesterdaySma10: " + dayBeforeYesterdaySma10 + ", smaTrend: " + smaTrend + ", halfway: " + halfway + ", low: " + today.getLow() + ", gap: " + todayGap);

        if (
                (todayGap >= 2 && oneFourthWay > sma25 && today.getOpenPrice() <= (sma25 * 1.015))
                && divergence <= maxDivergence
                && diffWithPreviousLow10 <= 12 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                && upperTail < 4
//                && vChange > 2
                && priceDiffWithMinimum <=10
//                && todayClosePrice > lastMonthMaximum
//                && today.getEmaList().get(9) < 0
                && yesterdayGap > 0
                && changeWithBottom <= 10
                && vChange <= 5
                && vChange >= 1.5
                && todaychange <= 5
                && today.getAdjustedClosePrice() < 500
//                && averagePriceOnLastFewDays > 0
                ) {
            setCause(this.getClass().getName());
            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }
        return false;
    }
    
    private Item getYearLowest(List<Item> items) {
        Item minimum = new Item();
        minimum.setAdjustedClosePrice(1000000);
//        System.out.println("finidng minimum on " + today.getDate() + ", itemSize: " + items.size() + ", top item: " + items.get(items.size()-1).getDate());
        for (int i = items.size() - ScannerService.TRADING_DAYS_IN_A_WEEK; i >= 0; i--) {
            if (items.get(i).getAdjustedClosePrice() < minimum.getAdjustedClosePrice()) {
//                System.out.println("minimum is: " + items.get(i).getAdjustedClosePrice() + ", date: " + items.get(i).getDate());
                minimum = items.get(i);
            }
        }
        return minimum;
    }
    
    private Item getLowest(List<Item> items) {
        Item minimum = new Item();
        minimum.setAdjustedClosePrice(1000000);
//        System.out.println("finidng minimum on " + today.getDate() + ", itemSize: " + items.size() + ", top item: " + items.get(items.size()-1).getDate());
        for (int i = items.size()-2; i >= items.size()-ScannerService.TRADING_DAYS_IN_A_MONTH*2 && i>=0; i--) {
            if (items.get(i).getAdjustedClosePrice() < minimum.getAdjustedClosePrice()) {
//                System.out.println("minimum is: " + items.get(i).getAdjustedClosePrice() + ", date: " + items.get(i).getDate());
                minimum = items.get(i);
            }
        }
        return minimum;
    }

}
