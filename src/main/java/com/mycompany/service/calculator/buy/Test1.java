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
import static com.mycompany.service.calculator.SignalCalculator.vChange;
import static com.mycompany.service.calculator.SignalCalculator.weeklyVChange;
import java.util.List;

/**
 * @date Dec 17, 2015
 * @author setu
 */
public class Test1 extends BuySignalCalculator {

    public Test1(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
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
        

        //System.out.println("date: " + today.getDate() + ", sma25: " + sma25 + ", yesterdaySma10: " + yesterdaySma10 + ", dayBeforeYesterdaySma10: " + dayBeforeYesterdaySma10 + ", smaTrend: " + smaTrend + ", halfway: " + halfway + ", low: " + today.getLow() + ", gap: " + todayGap);

        if (
                (todayGap >= 1 && oneFourthWay > sma25 && today.getOpenPrice() <= (sma25 * 1.01))
                && divergence <= maxDivergence
                && todayValue >= minValue
                && todayTrade >= minTrade
                && yesterday.getVolumeChange() >=1
                && vChange >= 2
                && todaychange <= 4
//                && today.getAdjustedVolume() >= yesterday.getAdjustedVolume()*2
//                && weeklyVChange >= 3
//                && vChange >= minVChange && ((marketWasDown && vChange <= 4) || (vChange <= 2 && weeklyVChange<2.8) || (vChange>3 && publicShare<10000000 && lastTwoMonthVariation <= 10))
//                && volumePerTradeChange >= 1.3 && vChange >=5
//                && Math.min(todayGap, yesterdayGap) > -3
                && diffWithPreviousLow10 <= 12 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                && bottomTail <= 1
//                && sma10Diff >= 2
//                && topTail <= 1.1
//                && upperTail <= 1
                && acceptableItemSMA && acceptableDSEXSMA
                && !today.getSector().equalsIgnoreCase("bank") 
                && !today.getSector().equalsIgnoreCase("mutual funds")
//                && sma25IntersectInLastFewDays >= 6
//                && gap >=2 && gap<=3
//                && todaychange >5 && todaychange<9
//                && dsexMaxRsiInLast2Days <= maxAllowedDsexRsi
//                && sma25Diff > 0
//                && volumePerTradeChange <= 1.3
////                
//                && todayClosePrice > sma10
//                && emaPass
//                && !isShaky
//                && sma25> sma10*1.01 && today.getOpenPrice()<sma10 && today.getAdjustedClosePrice()>sma25
//                && today.getAdjustedVolume() > lastMonthMaxVolume
//                  && todayClosePrice > lastMonthMaximum
//                && changeWithMaxPriceDay >= -1
//                && lastMonthSmaVariation < 3
//                && smaTrend
//                  && volumePerTradeChange <= 1.2 
                //&& (lastMonthVariation <= 7 ? lastMonthMaximum < today.getAdjustedClosePrice() : true)
//                && (lastMonthVariation <= 7 ? diffWithLastMonthHigh > -1 : true)
//                && !((today.getAdjustedClosePrice() - today.getYesterdayClosePrice()) <= 0.1)
//                && !((today.getDayHigh() - today.getAdjustedClosePrice()) > (today.getAdjustedClosePrice() - today.getOpenPrice()))
                ) {
            setCause(this.getClass().getName());
            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }
        return false;
    }

}
