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
public class SuddenHike extends BuySignalCalculator {

    public SuddenHike(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        //intializeVariables(itemSubList, calculated);
        if ((todayGap >= 3 && yesterdaychange < -0.5 && dayBeforeYesterdayChange < -0.5 && twoDayBeforeYesterdayGap < -0.5 && today.getAdjustedClosePrice() > twoDayBeforeYesterday.getOpenPrice())
                && divergence <= maxDivergence
                && todayValue >= minValue
                && todayTrade >= minTrade
                && vChange >= minVChange && ((marketWasDown && vChange <= 4) || vChange <= 2)
                && volumePerTradeChange < 1.8
                && Math.min(todayGap, yesterdayGap) > -3
                && diffWithPreviousLow10 <= 10 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                && upperTail < 4
                && acceptableItemSMA && acceptableDSEXSMA
                && dsexMaxRsiInLast2Days <= maxAllowedDsexRsi
                && !((today.getDayHigh() - today.getAdjustedClosePrice()) >= (today.getAdjustedClosePrice() - today.getOpenPrice()))) {
            setCause("Suddenhike");
            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }
        return false;
    }

}
