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
public class Macd extends BuySignalCalculator {

    public Macd(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        //intializeVariables(itemSubList, calculated);
        //System.out.println("Consecutive-Date: " + today.getDate() + ", code: " + item.getCode() + ", price: " + today.getAdjustedClosePrice() + ", tchange: " + tChange + ", volumeChange: " + vChange + ", diffWithPreviousLow10: " + diffWithPreviousLow10 + ", todayChange: " + todaychange + ", todayGap: " + todayGap + ", acceptableItemSMA: " + acceptableItemSMA + ", accedsex: " + acceptableDSEXSMA + ", yesterdayRsi: " + yesterdayRsi + ", dayBeforeRsi: " + dayBeforeRsi);

        if ((todaychange >= 1 && todayGap >= 1.2) && (yesterdaychange >= 1 || yesterdayGap >= 1)
                && todayDiv > yesterdayDiv
                && yesterdayDiv > dayBeforeYesterdayDiv
                && todaySignalLine <= 0
                && macd <= todaySignalLine
                && diffWithPreviousLow10 <= 10
                && todayValue >= minValue
                && todayTrade >= minTrade
                && vChange > 1 && ((marketWasDown && vChange <= 4) || vChange <= 2)
                && upperTail < 4
                && acceptableItemSMA && acceptableDSEXSMA
                && dsexMaxRsiInLast2Days <= maxAllowedDsexRsi
                && !((today.getAdjustedClosePrice() - today.getYesterdayClosePrice()) <= 0.1)
                && !((today.getDayHigh() - today.getAdjustedClosePrice()) >= (today.getAdjustedClosePrice() - today.getOpenPrice()))
                && dsex.getValue() >= 3000
                && oneMonthBackSma25Change > -2
                && !(dsex.getYesterdayRSI()<=30 && dsexYesterday.getClosePrice()<dsexYesterday.getOpenPrice())
                ) {
            //System.out.println("Macd0000000-Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange);
            setCause("Macd");
            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }

        return false;
    }

}
