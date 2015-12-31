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

        //System.out.println("date: " + today.getDate() + ", sma25: " + sma25 + ", yesterdaySma10: " + yesterdaySma10 + ", dayBeforeYesterdaySma10: " + dayBeforeYesterdaySma10 + ", smaTrend: " + smaTrend + ", halfway: " + halfway + ", low: " + today.getLow() + ", gap: " + todayGap);

        if (
                (todayGap >= 1.2 && halfway > sma25 && today.getLow() <= (sma25 * 1.01))
                && divergence <= maxDivergence
                && todayValue >= minValue
                && todayTrade >= minTrade
                && vChange >= minVChange && ((marketWasDown && vChange <= 4) || (vChange <= 2 && weeklyVChange<2.8) || (vChange>3 && publicShare<10000000 && lastTwoMonthVariation <= 10))
                && volumePerTradeChange < 1.8
                && Math.min(todayGap, yesterdayGap) > -3
                && diffWithPreviousLow10 <= 10 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                && upperTail < 4
                && acceptableItemSMA && acceptableDSEXSMA
                && dsexMaxRsiInLast2Days <= maxAllowedDsexRsi
                && smaTrend
                && (lastMonthVariation <= 7 ? lastMonthMaximum < today.getAdjustedClosePrice() : true)
                                        //&& lastGreenMinimum < today.getOpenPrice()
                && !((today.getAdjustedClosePrice() - today.getYesterdayClosePrice()) <= 0.1)
                && !((today.getHigh() - today.getAdjustedClosePrice()) > (today.getAdjustedClosePrice() - today.getOpenPrice()))
                ) {
            setCause("Sma25");
            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }
        return false;
    }

}
