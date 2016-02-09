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
 * @date Jan 12, 2016
 * @author setu
 */
public class LargeCandle extends BuySignalCalculator {

    public LargeCandle(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        if (
                (todayGap >= 7 )
                && todaychange <= 2
                && divergence <= maxDivergence
//                && todayClosePrice > sma25
                && notBelowBothSMA
                && diffWithPreviousLow10 <= 7
                ) {
            setCause(this.getClass().getName());
            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }
        return false;
    }

}
