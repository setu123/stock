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
        

        //System.out.println("date: " + today.getDate() + ", sma25: " + sma25 + ", yesterdaySma10: " + yesterdaySma10 + ", dayBeforeYesterdaySma10: " + dayBeforeYesterdaySma10 + ", smaTrend: " + smaTrend + ", halfway: " + halfway + ", low: " + today.getLow() + ", gap: " + todayGap);

        if (
                (todayGap >= 1.2 && oneFourthWay > sma25 && today.getOpenPrice() <= (sma25 * 1.015))
                && divergence <= maxDivergence
                && todayValue >= minValue
                && todayTrade >= minTrade
                && vChange >= 0.8
//                && vChange >= minVChange && ((marketWasDown && vChange <= 4) || (vChange <= 2 && weeklyVChange<2.8) || (vChange>3 && publicShare<10000000 && lastTwoMonthVariation <= 10))
//                && volumePerTradeChange >= 1.3 && vChange >=5
//                && Math.min(todayGap, yesterdayGap) > -3
                && diffWithPreviousLow10 <= 12 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                && upperTail < 4
//                && topTail <= 1.1
                && acceptableItemSMA && acceptableDSEXSMA
//                && dsexMaxRsiInLast2Days <= maxAllowedDsexRsi
//                && sma25Diff >= -0.6
                && todayClosePrice > sma10
                && emaPass
//                && !isShaky
//                && lastMonthVariation <=5
//                && lastWeekMaxVolume < today.getAdjustedVolume()
                && changeWithMaxPriceDay >= -1
//                && yesterdayGap>0
//                && smaTrend
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
