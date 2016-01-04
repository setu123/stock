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
public class GreenAfterRsi30 extends BuySignalCalculator{

    public GreenAfterRsi30(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        //System.out.println("date: " + today.getDate() + ", todayGap: " + todayGap + ", Math.min(dsex.getYesterdayRSI(), dsex.getDayBeforeYesterdayRSI()): " + Math.min(dsex.getYesterdayRSI(), dsex.getDayBeforeYesterdayRSI()) + ", todayDseIndexChange: " + todayDseIndexChange);
        if (
                ((todayGap >= 1.2 || todaychange>=1) && dsex.getDayBeforeYesterdayRSI()<= 30 && dsexYesterday.getClosePrice()>dsexYesterday.getOpenPrice())
                        && divergence <= 10
                        && todayValue >= minValue
                        && todayTrade >= minTrade
//                        && vtcRatio>0.8 && vtcRatio<2
                        && rsi <=50
                        && vChange >= minVChange && ((marketWasDown && vChange <= 4) || vChange <= 2)
                        && volumePerTradeChange < 1.8
//                        && Math.min(todayGap, yesterdayGap) > -3
                        && dsex.getValue() >= 3000
                        //&& maxVChange > 0.5
                        && diffWithPreviousLow10 <= 15 //&& Math.max(todayGap, yesterdayGap) >= 0.5
//                        //&& upperTail < 4
                        && acceptableItemSMA && acceptableDSEXSMA
                        && dsexMaxRsiInLast2Days <= maxAllowedDsexRsi
                        //&& !((today.getHigh() - today.getAdjustedClosePrice()) >= (today.getAdjustedClosePrice() - today.getOpenPrice()))
                ) {
                    //System.out.println("sma250000Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", sma25: " + sma25);
                    setCause("gAfterRsi30:");
                    boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
                }
        return false;
    }

}
