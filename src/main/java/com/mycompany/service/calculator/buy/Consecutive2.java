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
public class Consecutive2 extends BuySignalCalculator {

    public Consecutive2(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        //intializeVariables(itemSubList, calculated);

        //System.out.println("Consecutive-Date: " + today.getDate() + ", code: " + today.getCode() + ", price: " + today.getAdjustedClosePrice() + ", tchange: " + tChange + ", volumeChange: " + vChange + ", diffWithPreviousLow10: " + diffWithPreviousLow10 + ", todayChange: " + todaychange + ", todayGap: " + todayGap + ", acceptableItemSMA: " + acceptableItemSMA + ", accedsex: " + acceptableDSEXSMA + ", yesterdayRsi: " + yesterdayRsi + ", dayBeforeRsi: " + dayBeforeRsi);
        //High volume price dropp
        if (((todaychange <= -1 && todayGap <= -1))
                && divergence <= maxDivergence
                && rsi <= 22
                && todayValue >= minValue
                && todayTrade >= minTrade
                && vChange >= 5
                && dsexMaxRsiInLast2Days < maxAllowedDsexRsi //&& volumePerTradeChange < 1.8
                && dsex.getValue() >= 3000
                //&& Math.min(todayGap, yesterdayGap) > -3
                //&& (diffWithPreviousLow10 <= 10 || (marketWasDown && diffWithPreviousLow10<=15))
                //&& hammer < 4
                //&& ((final1 && final2))
                //&& !((today.getAdjustedClosePrice() - today.getYesterdayClosePrice()) <= 0.1)
                //&& !((today.getHigh() - today.getAdjustedClosePrice()) >= (today.getAdjustedClosePrice() - today.getOpenPrice()))
                ) {
            //System.out.println("Consecutive-Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange);
            setCause("Consecutive2:");

            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }

        return false;
    }

}
