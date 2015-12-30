/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.service.calculator;

import com.mycompany.model.Item;
import com.mycompany.model.Portfolio;
import com.mycompany.model.PortfolioItem;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ScannerService;
import static com.mycompany.service.calculator.DecisionMaker.df;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @date Dec 16, 2015
 * @author setu
 */
public class SignalCalculator {

    static protected ScannerService scanner;
    static protected CustomHashMap oneYearData;
    //static protected List<Item> items;

    static protected Item calculatedItem;
    static protected Item item;
    static protected Item today;
    static protected Item dsex;
    static protected Item yesterday;
    static protected Item dayBeforeYesterday;
    static protected Item twoDayBeforeYesterday;
    static protected float divergence;
    static protected float rsi;
    static protected float vChange;
    static protected float tChange;
    static protected float todayTrade;
    static protected float todayValue;
    static protected float volumePerTradeChange;
    static protected float vtcRatio;

    static protected float todayGap;
    static protected float todaychange;
    static protected float yesterdayGap;
    static protected float yesterdaychange;
    static protected float dayBeforeYesterdayChange;
    static protected float dayBeforeYesterdayGap;
    static protected float twoDayBeforeYesterdayGap;
    static protected float last3DaysMax;

    static protected float todaysHigher;
    static protected float todaysLower;
    static protected float yesterdayHigher;
    static protected float yesterdayLower;
    static protected float dayBeforeYesterdayLower;

    static protected float diffWithPreviousLow10;
    static protected float todayDiv;
    static protected float yesterdayDiv;
    static protected float dayBeforeYesterdayDiv;
    static protected float macd;
    static protected float todaySignalLine;

    static protected float tail;
    static protected float upperTail;

    static protected float sma10;
    static protected float sma25;
    static protected float halfway;
    static protected float lastGreenMinimum;
    static protected float todayDseIndexChange;

    static protected float yesterdayRsi;
    static protected float dayBeforeRsi;

    static protected float yesterdaySma10;
    static protected float dayBeforeYesterdaySma10;
    static protected boolean smaTrend = true;
    static protected boolean notBelowBothSMA;
    static protected boolean belowSMA25;
    static protected boolean belowBothSMA;
    static protected float belowSMAFraction;
    static protected boolean acceptableItemSMA;
    static protected boolean notBelowDSEXBothSMA;
    static protected boolean belowDSEXBothSMA;
    static protected boolean acceptableDSEXSMA;

    static protected boolean marketWasDown;
    static protected float dsexMaxRsiInLast2Days;
    static protected int maxDivergence;
    static protected int maxRsi;
    static protected float maxAllowedDsexRsi;
    static protected float lastMonthVariation;
    static protected float lastTwoMonthVariation;
    static protected float lastMonthMaximum;

    static protected Portfolio portfolio;
    static protected PortfolioItem buyItem;
    static public Calendar lastTradingDay;
    
    //These are used for sell
    static protected String cause;
    static protected boolean twoConsecutiveRed = false;
    static protected boolean threeConsecutiveRed = false;
    static protected Item buyDayItem;
    static protected boolean rsi70;
    
    static protected float publicShare;
    static protected float gain;
    
    static protected float minVChange;
    static protected float minValue;
    static protected int minTrade;
    static protected float todayClosePrice;

    public SignalCalculator(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        this.scanner = scanner;
        this.oneYearData = oneYearData;
        this.portfolio = portfolio;

        lastTradingDay = Calendar.getInstance();
        lastTradingDay.set(Calendar.YEAR, 2015);
        lastTradingDay.set(Calendar.MONTH, 11);
        lastTradingDay.set(Calendar.DAY_OF_MONTH, 9);
    }

    public void intializeVariables(List<Item> items, Item calculated) {
        //this.items = items;
        if (calculated != null) {
            this.calculatedItem = calculated;
        } else {
            this.calculatedItem = getCalculatedItem(items);
        }

        today = items.get(items.size() - 1);
        today.setSignal(Item.SignalType.HOLD);
        dsex = getDSEXIndex(oneYearData, today.getDate());
        yesterday = items.get(items.size() - 2);
        dayBeforeYesterday = items.get(items.size() - 3);
        twoDayBeforeYesterday = items.get(items.size() - 4);
        divergence = calculatedItem.getDivergence();
        rsi = calculatedItem.getRSI();
        vChange = calculatedItem.getVolumeChange();
        tChange = calculatedItem.getTradeChange();
        todayTrade = today.getTrade();
        todayValue = today.getValue();
        volumePerTradeChange = today.getVolumePerTradeChange();
        vtcRatio = ((float)today.getVolume()/(float)today.getTrade())/((float)yesterday.getVolume()/(float)yesterday.getTrade());
        todayClosePrice = today.getAdjustedClosePrice();
        todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
        //priceGap is tuned for higher price stocks
        todayGap = todayGap + today.getAdjustedClosePrice() / 1000;
        todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
        yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
        yesterdaychange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
        dayBeforeYesterdayChange = ((dayBeforeYesterday.getAdjustedClosePrice() - twoDayBeforeYesterday.getAdjustedClosePrice()) / twoDayBeforeYesterday.getAdjustedClosePrice()) * 100;
        dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;
        twoDayBeforeYesterdayGap = ((twoDayBeforeYesterday.getAdjustedClosePrice() - twoDayBeforeYesterday.getOpenPrice()) / twoDayBeforeYesterday.getOpenPrice()) * 100;
        float todayPriceDiff = Math.abs(today.getOpenPrice() - today.getAdjustedClosePrice());
        float yesterdayPriceDiff = Math.abs(yesterday.getOpenPrice() - yesterday.getAdjustedClosePrice());
        float dayBeforeYesterdayPriceDiff = Math.abs(dayBeforeYesterday.getOpenPrice() - dayBeforeYesterday.getAdjustedClosePrice());
        todaysHigher = (today.getOpenPrice() + today.getAdjustedClosePrice() + todayPriceDiff) / 2;
        todaysLower = (today.getOpenPrice() + today.getAdjustedClosePrice() - todayPriceDiff) / 2;
        yesterdayHigher = (yesterday.getOpenPrice() + yesterday.getAdjustedClosePrice() + Math.abs(yesterdayPriceDiff)) / 2;
        yesterdayLower = (yesterday.getOpenPrice() + yesterday.getAdjustedClosePrice() - Math.abs(yesterdayPriceDiff)) / 2;
        dayBeforeYesterdayLower = (dayBeforeYesterday.getOpenPrice() + dayBeforeYesterday.getAdjustedClosePrice() - Math.abs(dayBeforeYesterdayPriceDiff)) / 2;

        last3DaysMax = Math.max(twoDayBeforeYesterday.getAdjustedClosePrice(), twoDayBeforeYesterday.getOpenPrice());
        last3DaysMax = Math.max(Math.max(dayBeforeYesterday.getAdjustedClosePrice(), dayBeforeYesterday.getOpenPrice()), last3DaysMax);
        last3DaysMax = Math.max(Math.max(yesterday.getAdjustedClosePrice(), yesterday.getOpenPrice()), last3DaysMax);

        diffWithPreviousLow10 = getPriceDiffWithPreviousLow(items, 10);
        todayDiv = (today.getEmaList().get(12) - today.getEmaList().get(26)) - today.getEmaList().get(9);
        yesterdayDiv = (yesterday.getEmaList().get(12) - yesterday.getEmaList().get(26)) - yesterday.getEmaList().get(9);
        dayBeforeYesterdayDiv = (dayBeforeYesterday.getEmaList().get(12) - dayBeforeYesterday.getEmaList().get(26)) - dayBeforeYesterday.getEmaList().get(9);
        macd = today.getEmaList().get(12) - today.getEmaList().get(26);

        todaySignalLine = 0;
        Object todaySignalLineObject = today.getEmaList().get(9);
        if (todaySignalLineObject != null) {
            todaySignalLine = (float) todaySignalLineObject;
        }

        float difference = Math.abs(yesterday.getOpenPrice() - yesterday.getAdjustedClosePrice());
        float smallest = (yesterday.getOpenPrice() + yesterday.getAdjustedClosePrice() - difference) / 2;
        tail = ((smallest - yesterday.getLow()) / smallest) * 100;
        upperTail = getUpperTail(today);

        sma10 = calculateSMA(items, 10);
        sma25 = calculateSMA(items, 25);
        today.getSmaList().put(10, sma10);
        today.getSmaList().put(25, sma25);

        halfway = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 2 + today.getOpenPrice();
        lastGreenMinimum = getLastGreenMinimum(items);
        todayDseIndexChange = ((dsex.getAdjustedClosePrice() - dsex.getYesterdayClosePrice()) / dsex.getYesterdayClosePrice()) * 100;

        items.remove(items.size() - 1);
        yesterdayRsi = scanner.calculateRSI(items);
        yesterdaySma10 = calculateSMA(items, 10);
        lastMonthVariation = getLastFiewDaysVariation(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
        lastTwoMonthVariation = getLastFiewDaysVariation(items, ScannerService.TRADING_DAYS_IN_A_MONTH*2);
        lastMonthMaximum = getLastFiewDaysMaximum(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
        items.remove(items.size() - 1);
        dayBeforeRsi = scanner.calculateRSI(items);
        dayBeforeYesterdaySma10 = calculateSMA(items, 10);

        smaTrend = true;
        if (sma10 < yesterdaySma10 && yesterdaySma10 < dayBeforeYesterdaySma10) {
            smaTrend = false;
        }

        belowSMAFraction = -2;
        notBelowBothSMA = !(today.getAdjustedClosePrice() < sma10 && today.getAdjustedClosePrice() < sma25);
        belowSMA25 = ((todayClosePrice-sma25)/sma25)*100 < belowSMAFraction/4;
        float minSMA = Math.min(sma10, sma25);
        float minSmaDiffWithClose = ((todayClosePrice-minSMA)/minSMA)*100;
        belowBothSMA = minSmaDiffWithClose < belowSMAFraction;
        acceptableItemSMA = Math.min(yesterdayRsi, dayBeforeRsi) <= 38 || notBelowBothSMA;

        notBelowDSEXBothSMA = !(dsex.getClosePrice() < dsex.getSmaList().get(10) && dsex.getClosePrice() < dsex.getSmaList().get(25));
        float minDSEXSMA = Math.min(dsex.getSmaList().get(10), dsex.getSmaList().get(25));
        float minDsexSmaDiffWithClose = ((dsex.getAdjustedClosePrice()-minDSEXSMA)/minDSEXSMA)*100;
        belowDSEXBothSMA = minDsexSmaDiffWithClose < belowSMAFraction/4;
        acceptableDSEXSMA = (Math.min(dsex.getYesterdayRSI(), dsex.getDayBeforeYesterdayRSI()) <= 30 && todayDseIndexChange > 0.9) || notBelowDSEXBothSMA;

        marketWasDown = Math.min(dsex.getYesterdayRSI(), dsex.getDayBeforeYesterdayRSI()) <= 30;
        dsexMaxRsiInLast2Days = Math.max(Math.max(dsex.getYesterdayRSI(), dsex.getDayBeforeYesterdayRSI()), dsex.getRSI());
        maxDivergence = 10;
        maxRsi = 50;
        maxAllowedDsexRsi = 69;
        
        //These are used for sell
        twoConsecutiveRed = false;
        threeConsecutiveRed = false;
        buyDayItem = null;

        if (((todaychange <= -0.5 && yesterdaychange <= -1.0) || (todaychange <= -1.0 && yesterdaychange <= -0.5)) && todayGap < 0) {
            twoConsecutiveRed = true;
        }
        //System.out.println("on " + today.getDate() + ", todaychange: " + todaychange + ", todayGap: " + todayGap + ", twoConsecutiveRed: " + twoConsecutiveRed);

        if (todayGap <= 0 && yesterdayGap <= 0 && dayBeforeYesterdayGap <= 0) {
            threeConsecutiveRed = true;
        }
        
        buyDayItem = getBuyDayItem(items.get(items.size()-1).getCode());
        rsi70 = (rsi >= 70 || yesterdayRsi >= 70 || dayBeforeRsi >= 70);
        
        publicShare = today.getTotalSecurity()*(today.getSharePercentage().getPublics()/100);
        buyItem = portfolio.getPortfolioItems().get(today.getCode());
        
        if(buyItem !=null){
            gain = ((today.getAdjustedClosePrice() - buyItem.getAverageBuyPrice()) / buyItem.getAverageBuyPrice()) * 100;
            gain = gain - 0.5f;   //Sell commision
        }     
        
        minVChange = 0.3f;
        minValue = 1f;
        minTrade = 50;
        if(dsex.getRSI()<=30 || dsex.getYesterdayRSI()<=30){
            minValue = 0;
            minTrade = 0;
        }
            
        
//        System.out.print("\ncode: " + today.getCode() + ", date: " + today.getDate() + ", tchange: " + today.getTradeChange() + ", vchange: " + today.getVolumeChange() + ", vtc: " + volumePerTradeChange + ", publicShare: " + publicShare + ", div: " + divergence + ", trade: " + today.getTrade() + ", value: " + today.getValue() + ", minValue: " + minValue + ", minTrade: " + minTrade + ", minSmaDiffWithClose: " + minSmaDiffWithClose + ", minDsexSmaDiffWithClose: " + minDsexSmaDiffWithClose + ", gain: " + gain + ", uppertail: " + upperTail + ", vtcRatio: " + vtcRatio);
    }

    protected static float getDBHammer(Item item) {
        float difference = Math.abs(item.getOpenPrice() - item.getAdjustedClosePrice());
        float largest = (item.getOpenPrice() + item.getAdjustedClosePrice() + difference) / 2;
        float smallest = (item.getOpenPrice() + item.getAdjustedClosePrice() - difference) / 2;
        float dbHammer = (((smallest - item.getLow()) - (item.getHigh() - largest)) / item.getAdjustedClosePrice()) * 100;
        //if(item.getCode().equalsIgnoreCase("watachem"))
        //    System.out.println("yesterday " + item.getDate() + " hammer: " + dbHammer + ", open: " + item.getOpenPrice() + ", close: " + item.getClosePrice() + ", high: " + item.getHigh() + ", low: " + item.getLow() + ", largest: " + largest + ", smallest: " + smallest);
        return dbHammer;
    }
    
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

    protected float getLastFiewDaysMaximum(List<Item> items, int days) {
        int size = items.size();
        int counter = 0;
        float maximum = 0;

        for (int i = size - 2; i >= 0; i--) {
            float openPrice = items.get(i).getOpenPrice();
            float closePrice = items.get(i).getAdjustedClosePrice();
            float dayHigh = Math.max(openPrice, closePrice);
            //float dayLow = Math.min(openPrice, closePrice);

            if (dayHigh > maximum) {
                maximum = dayHigh;
            }

            ++counter;
            if (counter >= days) {
                break;
            }
        }

        return maximum;
    }

    protected float getLastFiewDaysMinimum(List<Item> items, int days) {
        int size = items.size();
        int counter = 0;
        float minimum = 100000;

        for (int i = size - 2; i >= 0; i--) {
            float openPrice = items.get(i).getOpenPrice();
            float closePrice = items.get(i).getAdjustedClosePrice();
            float dayLow = Math.min(openPrice, closePrice);

            if (dayLow < minimum) {
                minimum = dayLow;
            }

            ++counter;
            if (counter >= days) {
                break;
            }
        }

        return minimum;
    }

    protected float getLastFiewDaysVariation(List<Item> items, int days) {
        int size = items.size();
        int counter = 0;
        float maximum = 0;
        float minimum = 100000;

        for (int i = size - 2; i >= 0; i--) {
            float openPrice = items.get(i).getOpenPrice();
            float closePrice = items.get(i).getAdjustedClosePrice();
            float dayHigh = Math.max(openPrice, closePrice);
            float dayLow = Math.min(openPrice, closePrice);

            if (dayHigh > maximum) {
                maximum = dayHigh;
            }

            if (dayLow < minimum) {
                minimum = dayLow;
            }

            ++counter;
            if (counter >= days) {
                break;
            }
        }

        float diff = ((maximum - minimum) / ((maximum + minimum) / 2)) * 100;
//        Item today = items.get(size-1);
//        if(today.getCode().equals("DESCO"))
//            System.out.println("todayDate: " + today.getDate() + ", maximum: " + maximum + ", minimum: " + minimum + ", diff: " + diff);
        return diff;
    }

    protected float getPriceDiffWithPreviousLow(List<Item> items, int days) {
        int size = items.size();
        int counter = 0;
        float minimum = 10000;

        for (int i = size - 2; i >= 0; i--) {
            float closePrice = items.get(i).getAdjustedClosePrice();
            if (closePrice < minimum) {
                minimum = closePrice;
            }
            ++counter;
            if (counter == days) {
                break;
            }
        }

        float lastDayClosePrice = items.get(size - 1).getAdjustedClosePrice();
        float diff = ((lastDayClosePrice - minimum) / minimum) * 100;
        return diff;
    }

    private float getLastGreenMinimum(List<Item> items) {
        int head = items.size() - 1;
        for (int i = head - 1; i >= 0; i--) {
            Item item = items.get(i);
            float priceGap = ((item.getAdjustedClosePrice() - item.getOpenPrice()) / item.getOpenPrice()) * 100;
            if (priceGap >= 1) {
                return item.getOpenPrice();
            }

        }

        return items.get(head).getAdjustedClosePrice();
    }

    private Item getDSEXIndex(CustomHashMap oneYearData, Date date) {
        List<Item> dsexItems = oneYearData.getItems("DSEX");
        if (date == null) {
            Item todayDsex = dsexItems.get(dsexItems.size() - 1);
            date = todayDsex.getDate();
        }

        List<Item> itemSubList = new ArrayList();
        for (Item dsexItem : dsexItems) {
            if (dsexItem.getDate().after(date)) {
                break;
            }
            itemSubList.add(dsexItem);
        }
        Collections.sort(itemSubList);

        float dsexsma10 = calculateSMA(itemSubList, 10);
        float dsexsma25 = calculateSMA(itemSubList, 25);
        float dsexrsi = scanner.calculateRSI(itemSubList);
        Item dsexItem = itemSubList.get(itemSubList.size() - 1);
        dsexItem.getSmaList().put(10, dsexsma10);
        dsexItem.getSmaList().put(25, dsexsma25);
        dsexItem.setRSI(dsexrsi);
        itemSubList.remove(itemSubList.size() - 1);
        dsexrsi = scanner.calculateRSI(itemSubList);
        dsexItem.setYesterdayRSI(dsexrsi);
        itemSubList.remove(itemSubList.size() - 1);
        dsexrsi = scanner.calculateRSI(itemSubList);
        dsexItem.setDayBeforeYesterdayRSI(dsexrsi);
        return dsexItem;
    }

    private float getUpperTail(Item item) {
        float largest = Math.max(item.getOpenPrice(), item.getAdjustedClosePrice());
        float uTail = ((item.getHigh() - largest) / largest) * 100;
        return uTail;
    }

    private Item getCalculatedItem(List<Item> itemsSublist) {
        //System.out.println("items: " + items);
        Item calculated = itemsSublist.get(itemsSublist.size() - 1);
        float volumeChange = scanner.calculateVolumeChange(itemsSublist, ScannerService.TRADING_DAYS_IN_A_MONTH);
        float tradeChange = scanner.calculateTradeChange(itemsSublist, ScannerService.TRADING_DAYS_IN_A_MONTH);
        float tempRsi = scanner.calculateRSI(itemsSublist);
        scanner.calculateDivergence(itemsSublist);
        int diverge = itemsSublist.get(itemsSublist.size() - 1).getDivergence();
        calculated.setVolumeChange(volumeChange);
        calculated.setTradeChange(tradeChange);
        calculated.setRSI(tempRsi);
        calculated.setDivergence(diverge);

        return calculated;
    }

    protected float calculateSMA(List<Item> items, int N) {
        int limit = N;
        if (items.size() < limit) {
            limit = items.size();
        }

        float SMA = 0;
        for (int i = items.size() - 1; i >= (items.size() - limit); i--) {
            SMA += items.get(i).getAdjustedClosePrice();
        }
        SMA = SMA / limit;

        return SMA;
    }
    
    public static String getCause() {
        return cause;
    }
    
    protected void setCause(String cause){
        this.cause = cause;
    }

}
