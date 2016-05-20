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
import static com.mycompany.service.calculator.SignalCalculator.averageValuePerTrade;
import static com.mycompany.service.calculator.SignalCalculator.today;
import static com.mycompany.service.calculator.SignalCalculator.vChange;
import static com.mycompany.service.calculator.SignalCalculator.weeklyVChange;
import java.util.List;

/**
 * @date Dec 17, 2015
 * @author setu
 */
public class Sma10 extends BuySignalCalculator {

    public Sma10(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        //intializeVariables(itemSubList, calculated);

        //System.out.println("date: " + today.getDate() + ", sma25: " + sma25 + ", yesterdaySma10: " + yesterdaySma10 + ", dayBeforeYesterdaySma10: " + dayBeforeYesterdaySma10 + ", smaTrend: " + smaTrend + ", halfway: " + halfway + ", low: " + today.getLow() + ", gap: " + todayGap);
        float change = (today.getOpenPrice()-yesterday.getAdjustedClosePrice())/yesterday.getAdjustedClosePrice();
        float todayValuePerTrade = today.getValue()/today.getTrade();
        float ratio = todayValuePerTrade/averageValuePerTrade;

        if (
                (todayGap >= 1 && halfway > sma10 && today.getDayLow() <= (sma10 * 1.01))
                && todayClosePrice < sma25
                && sma25 > sma10
                && divergence <= 0
                && todayValue >= minValue
                && todayTrade >= minTrade
                && vChange >= 1 && weeklyVChange>=1 
//                && ((marketWasDown && vChange <= 4) || (vChange <= 4 && weeklyVChange<4.8) || (vChange>3 && publicShare<10000000 && lastTwoMonthVariation <= 10))
//                && today.getAdjustedVolume()/lastWeekMaxVolume > 1.5
//                && sma25>sma10
//                && ratio>0.7 && ratio<1.4
//                && volumePerTradeChange < 1.8
//                && sma25>dayBeforeYesterdaySma25
//                && Math.min(todayGap, yesterdayGap) > -3
                && diffWithPreviousLow10 <= 10 //&& Math.max(todayGap, yesterdayGap) >= 0.5
//                && upperTail < 4
//                && acceptableItemSMA && acceptableDSEXSMA
//                && dsexMaxRsiInLast2Days <= maxAllowedDsexRsi
//                && lastMonthMaximum < today.getAdjustedClosePrice()
//                && smaTrend
//                //&& (lastMonthVariation <= 7 ? lastMonthMaximum < today.getAdjustedClosePrice() : true)
//                && (lastMonthVariation <= 7 ? diffWithLastMonthHigh > -1 : true)
//                && !((today.getAdjustedClosePrice() - today.getYesterdayClosePrice()) <= 0.1)
//                && !((today.getDayHigh() - today.getAdjustedClosePrice()) >= (today.getAdjustedClosePrice() - today.getOpenPrice()))
                ) {
            setCause(this.getClass().getName());
            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }
        return false;
    }

}
