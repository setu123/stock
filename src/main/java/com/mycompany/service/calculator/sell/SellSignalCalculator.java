/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.service.calculator.sell;

import com.mycompany.model.Item;
import com.mycompany.model.Portfolio;
import com.mycompany.model.PortfolioItem;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ScannerService;
import com.mycompany.service.calculator.DecisionMaker;
import com.mycompany.service.calculator.SignalCalculator;
import java.util.List;

/**
 * @date Dec 17, 2015
 * @author setu
 */
public abstract class SellSignalCalculator extends SignalCalculator implements DecisionMaker {

//    protected String cause;
//
//    protected boolean twoConsecutiveRed = false;
//    protected boolean threeConsecutiveRed = false;
//    protected Item buyDayItem;
//    protected boolean rsi70;

    public SellSignalCalculator(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

//    protected void initializeVariables(List<Item> itemSubList, Item calItem) {
//        super.intializeVariables(itemSubList, calItem);
//        
//        twoConsecutiveRed = false;
//        threeConsecutiveRed = false;
//        buyDayItem = null;
//
//        if (((todaychange <= -0.5 && yesterdaychange <= -1.0) || (todaychange <= -1.0 && yesterdaychange <= -0.5)) && todayGap < 0) {
//            twoConsecutiveRed = true;
//        }
//        //System.out.println("on " + today.getDate() + ", todaychange: " + todaychange + ", todayGap: " + todayGap + ", twoConsecutiveRed: " + twoConsecutiveRed);
//
//        if (todayGap <= 0 && yesterdayGap <= 0 && dayBeforeYesterdayGap <= 0) {
//            threeConsecutiveRed = true;
//        }
//        
//        buyDayItem = getBuyDayItem(itemSubList.get(itemSubList.size()-1).getCode());
//        rsi70 = rsi >= 70 || yesterdayRsi >= 70 || dayBeforeRsi >= 70;
//    }

    private Item getBuyDayItem(String code) {
        PortfolioItem portfolioItem = portfolio.getPortfolioItems().get(code);

        if (portfolioItem != null) {
            List<Item> buyItemData = oneYearData.getItems(portfolioItem.getCode());
            for (Item anItem : buyItemData) {
                if (today.getDate().equals(portfolioItem.getDate())) {
                    return anItem;
                }
            }
        }
        return null;
    }

    public abstract boolean isSellCandidate(List<Item> itemSubList, Item calItem);

    protected boolean isValidCandidate(Item calculatedItem, List<Item> items) {

        float yesterdayCandleLength = (Math.abs(yesterday.getOpenPrice() - yesterday.getAdjustedClosePrice()) / yesterday.getOpenPrice()) * 100;
        float todayCandleLength = (Math.abs(today.getOpenPrice() - today.getAdjustedClosePrice()) / today.getOpenPrice()) * 100;
        yesterdayHigher = Math.max(yesterday.getOpenPrice(), yesterday.getAdjustedClosePrice());
        float dayBeforeYesterdayHigher = Math.max(dayBeforeYesterday.getOpenPrice(), dayBeforeYesterday.getAdjustedClosePrice());
        float previousHigher = Math.max(yesterdayHigher, dayBeforeYesterdayHigher);
        float changeWithPreviousHigher = ((previousHigher - today.getAdjustedClosePrice()) / previousHigher) * 100;

        if ((yesterdayCandleLength + todayCandleLength) > 4) {
            return true;
        }

        if (changeWithPreviousHigher > 5) {
            return true;
        }

        if (calculatedItem.getVolumeChange() < 0.8 || calculatedItem.getTradeChange() < 0.8) {
            return true;
        }

        if (calculatedItem.getHammer() < 2) {
            return true;
        }

        return false;
    }

    protected boolean isMaskPassed(Item item, Portfolio portfolio) {
        //System.out.println("date: " + item.getDate() + ", code: " + item.getCode() + ", cause: " + getCause());

        
        
        if (!(buyItem != null && buyItem.getDate().before(yesterday.getDate())))
                return false;

//        if (buyItem != null && buyItem.getDate().before(yesterday.getDate())) {
        else{
            float gain = ((item.getAdjustedClosePrice() - buyItem.getAverageBuyPrice()) / buyItem.getAverageBuyPrice()) * 100;
            gain = gain - 0.5f;   //Sell commision

            float minSma = Math.min(item.getSmaList().get(10), item.getSmaList().get(25));
            float diffWithMinSma = ((item.getAdjustedClosePrice() - minSma) / minSma) * 100;
            float diffWithSma10 = ((item.getAdjustedClosePrice() - sma10) / sma10) * 100;
            float diffWithSma25 = ((item.getAdjustedClosePrice() - sma25) / sma25) * 100;
            boolean belowAcceptableSma10 = false;
            boolean belowAcceptableSma25 = false;
            boolean endOfMarket = cause.equalsIgnoreCase("EOM");

            if (diffWithSma10 < -3) {
                belowAcceptableSma10 = true;
            }

            if (diffWithSma25 < 0) {
                belowAcceptableSma25 = true;
            }

//            belowBothSMA = false;
//            if (diffWithMinSma < -2) {
//                //if(item.getAdjustedClosePrice()<item.getSmaList().get(10) && item.getAdjustedClosePrice()<item.getSmaList().get(25)){
//                belowBothSMA = true;
//                //System.out.println("Must sell " + item.getCode() + " on " + item.getDate());
//            }
            
            //System.out.println("code: " + today.getCode() + ", date: " + today.getDate() + ", cause: " + cause.substring(cause.indexOf("$")) + ", belowBothSMA: " + belowBothSMA + ", gain: " + gain + ", todayGap: " + todayGap + ", todaychange: " + todaychange + ", diffWithMinSma: " + diffWithMinSma + ", endOfMarket: " + endOfMarket + ", belowDSEXBothSMA: " + belowDSEXBothSMA);
            //System.out.println("exp1: " + (dsex.getAdjustedClosePrice()<dsex.getAdjustedVolume()));
            
            if(endOfMarket)
                return true;
            
//            if(todaychange < 0 && todayGap < 0 && gain>=10 && today.getAdjustedClosePrice()<sma10)
//                return true;
            
//            if(todaychange < 0 && todayGap < 0 && gain<-5 && belowBothSMA)
//                return true;
              
//            if(gain<-5 && belowBothSMA && todayGap<=0 && todaychange<0)
//                return true;
            
//            if(belowBothSMA && todayGap<=0 && todaychange<0 && belowDSEXBothSMA && dsex.getAdjustedClosePrice()<dsex.getYesterdayClosePrice())
//                return true;
            
//            if(gain<-5)
//                return true;
            
            if(gain>2 && gain <5 && (todayClosePrice<lastGreenMinimum))
                return true;
            
//            if(gain>=10 && gain<=11)
//                return true;
            
            if(gain>=20 && gain<=21)
                return true;
            
//            if(gain>=30 && gain<=31)
//                return true;
//            
//            if(gain>=40 && gain<=41)
//                return true;
            
            if(gain>40 && (belowAcceptableSma10))
                return true;
            
            if((gain>10 || rsi>=69) && upperTail>5)
                return true;
            
            if(cause.contains("sell56"))
                return true;
            
//            if(gain>45 && upperTail>4)
//                return true;
//            
//            if(gain>5 && today.getAdjustedClosePrice()<sma25)
//                return true;
//            
//            if(buyItem.getCause().contains("gAfterRsi30") && gain<0 &&  (belowDSEXBothSMA || belowBothSMA))
//                return true;
            
//            if(gain<-5)
//                return true;
            
//            if(gain>0 && belowSMA25 && todayGap<=0 && todaychange<0 && belowDSEXBothSMA && dsex.getAdjustedClosePrice()<dsex.getYesterdayClosePrice())
//                return true;
            
//            if(gain>7 && gain<15 && upperTail>2.9)
//                return true;
            
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(today.getDate());
//            cal.add(Calendar.DAY_OF_YEAR, -60);
//            if(buyItem.getDate().before(cal.getTime()) && gain>0)
//                return true;

            //boolean var1 = item.getAdjustedClosePrice() >= buyItem.getOpenPrice() && item.getAdjustedClosePrice() < sma25;
//            
            if(today.getAdjustedClosePrice()>sma10)
                return false;

            if(gain < -10)
                return false;
            
            if ((((gain > -5)) || (gain < 0)) && todayGap > -9) {
                return false;
            }

//                if(gain<0 && item.getAdjustedClosePrice()>=buyItem.getOpenPrice())
//                    return;
            if (gain < 0 && dsex.getRSI() <= 30) {
                return false;
            }

//                if(item.getAdjustedClosePrice()>= buyItem.getOpenPrice() && item.getAdjustedClosePrice()<sma25)
//                    return;
//                summery += gain;
//                System.out.print(item.getDate() + ", price: " + item.getAdjustedClosePrice() + ", cause: " + cause + ", gain: " + df.format(gain) + "%");
//                buyItem = null;
//
//                if (gain > 0) {
//                    ++profit;
//                } else {
//                    ++loss;
//                }
        }

        return true;
    }
}
