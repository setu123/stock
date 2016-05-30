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
import static com.mycompany.service.calculator.SignalCalculator.maxPriceDay;
import static com.mycompany.service.calculator.SignalCalculator.sma25;
import static com.mycompany.service.calculator.SignalCalculator.today;
import java.util.List;

/**
 * @date Dec 17, 2015
 * @author setu
 */
public class Average extends BuySignalCalculator {

    public Average(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
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
        float oneWeekAgoSma10 = fourDayBeforeYesterday.getSmaList().get(10);
        float sma10Diff = ((SignalCalculator.sma10-oneWeekAgoSma10)/oneWeekAgoSma10)*100;
        boolean gapLogic = gap>0.7? todayGap>=4:true;
        float plainGap = todayClosePrice-today.getOpenPrice();
        float oneWeekAgoEma9 = fourDayBeforeYesterday.getEmaList().get(9);
        float todayEma9 = today.getEmaList().get(9);
        float macdPercentage = (macd/todayClosePrice)*100;
        boolean emaPass = todayEma9<=0?true:todayEma9>=oneWeekAgoEma9;
        float straightGap = Math.max(today.getOpenPrice(), today.getAdjustedClosePrice()) - Math.min(today.getOpenPrice(), today.getAdjustedClosePrice());
        float tails = ((today.getDayHigh() - today.getDayLow()) - straightGap);
        boolean isShaky = tails>straightGap;
        float topTail = ((today.getDayHigh()-Math.max(today.getOpenPrice(), today.getAdjustedClosePrice()))/Math.max(today.getOpenPrice(), today.getAdjustedClosePrice()))*100;
        float bottomTail = ((Math.min(today.getOpenPrice(), today.getAdjustedClosePrice())-today.getDayLow())/Math.min(today.getOpenPrice(), today.getAdjustedClosePrice()))*100;
        float maxPriceDayMax = Math.max(maxPriceDay.getOpenPrice(), maxPriceDay.getAdjustedClosePrice());
        float changeWithMaxPriceDay = ((todayClosePrice-maxPriceDayMax)/maxPriceDayMax)*100;
        float todaysmaDiff = ((sma25-sma10)/sma10)*100;
        float dsexSma25 = dsex.getSmaList().get(25);
        
        

        //System.out.println("date: " + today.getDate() + ", sma25: " + sma25 + ", yesterdaySma10: " + yesterdaySma10 + ", dayBeforeYesterdaySma10: " + dayBeforeYesterdaySma10 + ", smaTrend: " + smaTrend + ", halfway: " + halfway + ", low: " + today.getLow() + ", gap: " + todayGap);
//        if(buyItem!=null && buyItem.getCode().equals("PADMAOIL"))
//            System.out.println("date: " + today.getDate() + ", gain: " + gain);

        if (buyItem!=null && gain<-AVERAGE_ON_LOSS_PERCENT) {
            
            //float newAveragePrice = (float) (buyItem.getAverageBuyPrice()/2 + buyItem.getAverageBuyPrice()*0.01);
//            System.out.println("halfing buy price");
            //buyItem.setAverageBuyPrice(newAveragePrice);
            setCause(this.getClass().getName());
            boolean maskPassed = isMaskPassed(today, portfolio);
//            if(buyItem.getCode().equals("PADMAOIL"))
//                System.out.println("average_code: " + buyItem.getCode() + today.getDate() + ", maskPassed: " + maskPassed + ", isBullTrap: " + isBullTrap);
            return maskPassed;
        }
        return false;
    }

}
