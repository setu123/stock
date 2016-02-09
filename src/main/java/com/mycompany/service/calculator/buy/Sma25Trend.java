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
 * @date Jan 5, 2016
 * @author setu
 */
public class Sma25Trend extends BuySignalCalculator {

    public Sma25Trend(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        int size = itemSubList.size();
        
        if(size<= ScannerService.TRADING_DAYS_IN_A_WEEK*5 || itemSubList.get(size-ScannerService.TRADING_DAYS_IN_A_WEEK*5).getSmaList().isEmpty())
            return false;
        
        Item oneWeekBack = itemSubList.get(size-ScannerService.TRADING_DAYS_IN_A_WEEK);
        Item twoWeekBack = itemSubList.get(size-ScannerService.TRADING_DAYS_IN_A_WEEK*2);
        Item threeWeekBack = itemSubList.get(size-ScannerService.TRADING_DAYS_IN_A_WEEK*3);
        Item fourWeekBack = itemSubList.get(size-ScannerService.TRADING_DAYS_IN_A_WEEK*4);
        Item fiveWeekBack = itemSubList.get(size-ScannerService.TRADING_DAYS_IN_A_WEEK*5);
        Item sixWeekBack = itemSubList.get(size-ScannerService.TRADING_DAYS_IN_A_WEEK*6);
        
        float oneWeekChange = ((today.getSmaList().get(25) - oneWeekBack.getSmaList().get(25))/oneWeekBack.getSmaList().get(25))*100;
        float twoWeekChange = ((oneWeekBack.getSmaList().get(25) - twoWeekBack.getSmaList().get(25))/twoWeekBack.getSmaList().get(25))*100;
        float threeWeekChange = ((twoWeekBack.getSmaList().get(25) - threeWeekBack.getSmaList().get(25))/threeWeekBack.getSmaList().get(25))*100;
        float fourWeekChange = ((threeWeekBack.getSmaList().get(25) - fourWeekBack.getSmaList().get(25))/fourWeekBack.getSmaList().get(25))*100;
        //System.out.println("fourWeekBack.getSmaList(): " + fourWeekBack.getSmaList() + ", fiveWeekBack.getSmaList(): " + fiveWeekBack.getSmaList());
        float fiveWeekChange = ((fourWeekBack.getSmaList().get(25) - fiveWeekBack.getSmaList().get(25))/fiveWeekBack.getSmaList().get(25))*100;
        //float sixWeekChange = ((fiveWeekBack.getSmaList().get(25) - sixWeekBack.getSmaList().get(25))/sixWeekBack.getSmaList().get(25))*100;
        //Item oneAndHalfMonthBack = itemSubList.get(size-(ScannerService.TRADING_DAYS_IN_A_MONTH+ScannerService.TRADING_DAYS_IN_A_WEEK*2));
        float changeLimit = 3;
        float smaDiff = Math.abs(sma10 - sma25);
        float smaAvg = (sma10+sma25)/2;
        smaDiff = (smaDiff/smaAvg)*100;

        if (
//                today.getSmaList().get(25) > oneWeekBack.getSmaList().get(25)
//                && oneWeekBack.getSmaList().get(25) > twoWeekBack.getSmaList().get(25)
//                && twoWeekBack.getSmaList().get(25) > threeWeekBack.getSmaList().get(25)
//                && threeWeekBack.getSmaList().get(25) > fourWeekBack.getSmaList().get(25)
//                && fourWeekBack.getSmaList().get(25) > fiveWeekBack.getSmaList().get(25)
//                && fiveWeekBack.getSmaList().get(25) > sixWeekBack.getSmaList().get(25)
                
                 oneWeekChange > 0 && oneWeekChange < changeLimit
                && twoWeekChange > 0 && twoWeekChange < changeLimit
                && threeWeekChange > 0 && threeWeekChange < changeLimit
                && fourWeekChange > 0 && fourWeekChange < changeLimit
                && fiveWeekChange > 0 && fiveWeekChange < changeLimit
                
//                && sixWeekChange > 0 && sixWeekChange < changeLimit
                
                && divergence <= 10
                && twoWeekBack.getDivergence() < divergence
//                && today.getEmaList().get(9)<=2
//                && rsi <= maxRsi
                && diffWithPreviousHigh10 > -5
                && halfway > sma25 && today.getDayLow() <= (sma25 * 1.01)
                && upperTail < 4
//                && !((today.getDayHigh() - today.getAdjustedClosePrice()) > (today.getAdjustedClosePrice() - today.getOpenPrice()))
//                && upperTail < 4
                //&& today.getSmaList().get(25) > twoWeekBack.getSmaList().get(25)
                ) {
            setCause(this.getClass().getName());
            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }

        return false;
    }

}
