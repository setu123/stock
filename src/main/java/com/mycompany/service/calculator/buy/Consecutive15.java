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
public class Consecutive15 extends BuySignalCalculator {

    public Consecutive15(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        //intializeVariables(itemSubList, calculated);

        //System.out.println("Consecutive-Date: " + today.getDate() + ", code: " + item.getCode() + ", price: " + today.getAdjustedClosePrice() + ", tchange: " + tChange + ", volumeChange: " + vChange + ", diffWithPreviousLow10: " + diffWithPreviousLow10 + ", todayChange: " + todaychange + ", todayGap: " + todayGap + ", acceptableItemSMA: " + acceptableItemSMA + ", accedsex: " + acceptableDSEXSMA + ", yesterdayRsi: " + yesterdayRsi + ", dayBeforeRsi: " + dayBeforeRsi);
        if (
                (
                (todaychange >= 1 && todayGap >= 1.2) 
                && (yesterdaychange >= 1 || yesterdayGap >= 1) 
                && (todaychange + yesterdaychange) > 1
                )
                && divergence <= maxDivergence
                && (rsi <= maxRsi || (marketWasDown && rsi <= 60))
                && todayValue >= minValue
                && todayTrade >= minTrade
                && vChange >= minVChange && (vChange <= 2)
                //&& volumePerTradeChange < 1.8
                && Math.min(todayGap, yesterdayGap) > -3
                && (diffWithPreviousLow10 <= 10 || (marketWasDown && diffWithPreviousLow10 <= 16))
                && upperTail < 4
                && acceptableItemSMA && acceptableDSEXSMA
                && dsexMaxRsiInLast2Days < maxAllowedDsexRsi
                && !((today.getAdjustedClosePrice() - today.getYesterdayClosePrice()) <= 0.1)
                && !((today.getHigh() - today.getAdjustedClosePrice()) >= (today.getAdjustedClosePrice() - today.getOpenPrice()))
                ) {
            //System.out.println("Consecutive-Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange);
            setCause("Consecutive1.5: ");
                    //today.setSignal(Item.SignalType.BUY);
            //today.setTradeChange(tradeChange);
            //today.setVolumeChange(volumeChange);
            //doTrade(today, yesterday, cause, -1);
            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }
        return false;
    }

}
