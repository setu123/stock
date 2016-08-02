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
import java.util.Map;
import java.util.stream.Collectors;

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
    static public Item today;
    static protected Item dsex;
    static protected Item dsexYesterday;
    static protected Item dsexDayBefore;
    static protected Item yesterday;
    static protected Item dayBeforeYesterday;
    static protected Item twoDayBeforeYesterday;
    static protected Item threeDayBeforeYesterday;
    static protected Item fourDayBeforeYesterday;
    static protected Item monthBefore;
    static protected float divergence;
    static protected float rsi;
    static public float vChange;
    static public float weeklyVChange;
    static public float tChange;
    static protected float todayTrade;
    static protected float todayValue;
    static public float volumePerTradeChange;
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
    static protected float diffWithPreviousHigh10;
    static protected float diffWithLastMonthHigh;
    static protected float todayDiv;
    static protected float yesterdayDiv;
    static protected float dayBeforeYesterdayDiv;
    static protected float macd;
    static protected float todaySignalLine;

    static protected float tail;
    static protected float upperTail;

    static protected float sma10;
    static public float sma25;
    static protected float oneMonthBackSma25Change;
    static protected float halfway;
    static protected float lastGreenMinimum;
    static protected float todayDseIndexChange;

    static protected float yesterdayRsi;
    static protected float dayBeforeRsi;
    static protected float twoDayBeforeRsi;
    static protected float threeDayBeforeRsi;
    static protected float fourDayBeforeRsi;
    static protected float fiveDayBeforeRsi;

    static protected float yesterdaySma10;
    static protected float yesterdaySma25;
    static protected float dayBeforeYesterdaySma10;
    static protected float dayBeforeYesterdaySma25;
    static public float oneWeekAgoSma25;
    static public float oneMonthAgoSma25;
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
    static protected float dsexMinRsiInLast2Days;
    static protected int maxDivergence;
    static protected int maxRsi;
    static protected float maxAllowedDsexRsi;
    static protected float lastMonthVariation;
    static protected float lastMonthSmaVariation;
    static protected float lastTwoMonthVariation;
    static protected float lastMonthMaximum;
    static protected float lastTwoWeekMaximum;

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
    static public float gain;

    static protected float minVChange;
    static protected float minValue;
    static protected int minTrade;
    static protected float todayClosePrice;
    static public boolean debugEnabled = false;
    static protected float sellingUpperTail = 2.5f;
    static protected float indexFluctuation;
    static protected boolean isBullTrap;
    static protected boolean isMarketDown = false;
    static protected float maxGainAfterBuy = 0;
    static public boolean potentiality = false;
    static public Item maxPriceDay;
    static public float averageValuePerTrade;
    static protected int lastWeekMaxVolume;
    static protected int lastMonthMaxVolume;
    static protected float sma25Diff;
    static protected int sma25IntersectInLastFewDays;
    static protected int greenCountInLastFewDays;
    static final public int AVERAGE_ON_LOSS_PERCENT= 15;
    static final public int DECISION_MAKING_TENURE= 30;
    static public Item bottom;
    static final public int bottomTolerationPercent = 3;

    public SignalCalculator(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        this.scanner = scanner;
        this.oneYearData = oneYearData;
        this.portfolio = portfolio;

//        lastTradingDay = Calendar.getInstance();
//        lastTradingDay.set(Calendar.YEAR, 2016);
//        lastTradingDay.set(Calendar.MONTH, 0);
//        lastTradingDay.set(Calendar.DAY_OF_MONTH, 4);
    }

    public void intializeVariables(List<Item> items, Item calculated) {
        if (items.size() <= ScannerService.TRADING_DAYS_IN_A_MONTH * 2) {
            return;
        }

        //this.items = items;
        if (calculated != null) {
            this.calculatedItem = calculated;
        } else {
            this.calculatedItem = getCalculatedItem(items);
        }

        today = items.get(items.size() - 1);
        today.setSignal(Item.SignalType.HOLD);
        today.setVolumeChanges(calculatedItem.getVolumeChanges());
        dsex = getDSEXIndex(oneYearData, today.getDate());
        yesterday = items.get(items.size() - 2);
        dsexYesterday = getDSEXIndex(oneYearData, yesterday.getDate());
        dayBeforeYesterday = items.get(items.size() - 3);
        dsexDayBefore = getDSEXIndex(oneYearData, dayBeforeYesterday.getDate());
        twoDayBeforeYesterday = items.get(items.size() - 4);
        threeDayBeforeYesterday = items.get(items.size() - 5);
        fourDayBeforeYesterday = items.get(items.size() - 6);
        
        monthBefore = today;
        if(items.size()>ScannerService.TRADING_DAYS_IN_A_MONTH)
            monthBefore = items.get(items.size()-ScannerService.TRADING_DAYS_IN_A_MONTH);
        
        divergence = calculatedItem.getDivergence();
        rsi = calculatedItem.getRSI();
        //vChange = calculatedItem.getVolumeChange();
        vChange = calculatedItem.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH);
        weeklyVChange = calculatedItem.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_WEEK);
        tChange = calculatedItem.getTradeChange();
        todayTrade = today.getTrade();
        todayValue = today.getValue();
        volumePerTradeChange = today.getVolumePerTradeChange();
        vtcRatio = ((float) today.getVolume() / (float) today.getTrade()) / ((float) yesterday.getVolume() / (float) yesterday.getTrade());
        todayClosePrice = today.getAdjustedClosePrice();
        todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
        //priceGap is tuned for higher price stocks
        if(todayGap>0)
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
        diffWithPreviousHigh10 = getPriceDiffWithPreviousHigh(items, 10);
        diffWithLastMonthHigh = getPriceDiffWithPreviousHigh(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
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
        tail = ((smallest - yesterday.getDayLow()) / smallest) * 100;
        upperTail = getUpperTail(today);

        sma10 = calculateSMA(items, 10);
        sma25 = calculateSMA(items, 25);
        today.getSmaList().put(10, sma10);
        today.getSmaList().put(25, sma25);

        halfway = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 2 + today.getOpenPrice();
        lastGreenMinimum = getLastGreenMinimum(items);
        todayDseIndexChange = ((dsex.getAdjustedClosePrice() - dsex.getYesterdayClosePrice()) / dsex.getYesterdayClosePrice()) * 100;

        if (items.size() > ScannerService.TRADING_DAYS_IN_A_MONTH) {
            Item oneMonthBack = items.get(items.size() - ScannerService.TRADING_DAYS_IN_A_MONTH);
            if (!oneMonthBack.getSmaList().isEmpty()) {
                float oneMonthBackSma25 = oneMonthBack.getSmaList().get(25);
                oneMonthBackSma25Change = ((sma25 - oneMonthBackSma25) / oneMonthBackSma25) * 100;
            }
        }

        boolean maxVolumeChangeInLast3days = getMaxVolumeChangeInLast3days(items, ScannerService.TRADING_DAYS_IN_A_MONTH * 2);
        buyItem = portfolio.getPortfolioItems().get(today.getCode());
        maxGainAfterBuy = getMaxGainAfterBuy(items);
        maxPriceDay = getMaxPriceDayOnLastXDay(items, ScannerService.TRADING_DAYS_IN_A_WEEK);
        averageValuePerTrade = getAverageValuePerTrade(items);
        lastWeekMaxVolume = getLastFewDaysMaxVolume(items, ScannerService.TRADING_DAYS_IN_A_WEEK);
        lastMonthMaxVolume = getLastFewDaysMaxVolume(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
        sma25IntersectInLastFewDays = getSma25IntersectInLastFewDays(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
        greenCountInLastFewDays = getGreenCountInLastFewDays(items, ScannerService.TRADING_DAYS_IN_A_WEEK*2);
        oneMonthAgoSma25 = calculateBackdatedSMA(items, 25, ScannerService.TRADING_DAYS_IN_A_MONTH);
        
        bottom = getLowest(items);

        //items are removed. so dont use items bellow this line
//        items.remove(items.size() - 1);
        yesterdayRsi = scanner.calculateRSI(items, items.size()-2);
        yesterdaySma10 = calculateSMA(items, 10, items.size()-1);
        yesterdaySma25 = calculateSMA(items, 25, items.size()-1);
        lastMonthVariation = getLastFiewDaysVariation(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
        lastMonthSmaVariation = getLastFiewDaysSmaVariation(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
        //lastMonthSmaVariation is tuned for higher price stocks
        //lastMonthSmaVariation = lastMonthSmaVariation + todayClosePrice/1000;
        lastTwoMonthVariation = getLastFiewDaysVariation(items, ScannerService.TRADING_DAYS_IN_A_MONTH * 2);
        lastMonthMaximum = getLastFiewDaysMaximumClosing(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
        lastTwoWeekMaximum = getLastFiewDaysMaximumClosing(items, ScannerService.TRADING_DAYS_IN_A_WEEK * 2);
//        items.remove(items.size() - 1);
        dayBeforeRsi = scanner.calculateRSI(items, items.size()-2);
        dayBeforeYesterdaySma10 = calculateSMA(items, 10, items.size()-2);
        dayBeforeYesterdaySma25 = calculateSMA(items, 25, items.size()-2);

//        items.remove(items.size() - 1);
//        items.remove(items.size() - 1);
//        items.remove(items.size() - 1);
        oneWeekAgoSma25 = calculateSMA(items, 25, items.size()-3);

        smaTrend = true;
        if (sma10 < yesterdaySma10 && yesterdaySma10 < dayBeforeYesterdaySma10) {
            smaTrend = false;
        }

        belowSMAFraction = -2;
        notBelowBothSMA = !(today.getAdjustedClosePrice() < sma10 && today.getAdjustedClosePrice() < sma25);
        belowSMA25 = ((todayClosePrice - sma25) / sma25) * 100 < belowSMAFraction / 4;
        float minSMA = Math.min(sma10, sma25);
        float minSmaDiffWithClose = ((todayClosePrice - minSMA) / minSMA) * 100;
        belowBothSMA = minSmaDiffWithClose < belowSMAFraction;
        acceptableItemSMA = Math.min(yesterdayRsi, dayBeforeRsi) <= 38 || notBelowBothSMA;

        notBelowDSEXBothSMA = !(dsex.getClosePrice() < dsex.getSmaList().get(10) && dsex.getClosePrice() < dsex.getSmaList().get(25));
        float minDSEXSMA = Math.min(dsex.getSmaList().get(10), dsex.getSmaList().get(25));
        float minDsexSmaDiffWithClose = ((dsex.getAdjustedClosePrice() - minDSEXSMA) / minDSEXSMA) * 100;
        belowDSEXBothSMA = minDsexSmaDiffWithClose < belowSMAFraction / 4;
        acceptableDSEXSMA = (Math.min(dsex.getYesterdayRSI(), dsex.getDayBeforeYesterdayRSI()) <= 30 && todayDseIndexChange > 0.9) || notBelowDSEXBothSMA;

        marketWasDown = Math.min(dsex.getYesterdayRSI(), dsex.getDayBeforeYesterdayRSI()) <= 30;
        dsexMaxRsiInLast2Days = Math.max(Math.max(dsex.getYesterdayRSI(), dsex.getDayBeforeYesterdayRSI()), dsex.getRSI());
        dsexMinRsiInLast2Days = Math.min(Math.min(dsex.getYesterdayRSI(), dsex.getDayBeforeYesterdayRSI()), dsex.getRSI());
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

        buyDayItem = getBuyDayItem(items.get(items.size() - 1).getCode());
        rsi70 = (rsi >= 70 || yesterdayRsi >= 70 || dayBeforeRsi >= 70);

        publicShare = today.getTotalSecurity() * (today.getSharePercentage().getPublics() / 100);

        if (buyItem != null) {
            gain = ((today.getAdjustedClosePrice() - buyItem.getAverageBuyPrice()) / buyItem.getAverageBuyPrice()) * 100;
            gain = gain - 0.5f;   //Sell commision
        }

        minVChange = 0.3f;
        minValue = 0.9f;
        minTrade = 50;
        if (dsex.getRSI() <= 30 || dsex.getYesterdayRSI() <= 30) {
            minValue = 0;
            minTrade = 0;
        }

        /**
         * index fluctuation is calculated as x+(x/(2x+y)) where x in upper tail
         * and y is index gap
         */
        float dsexHigher = Math.max(dsex.getOpenPrice(), dsex.getClosePrice());
        float dsexUpperTail = ((dsex.getDayHigh() - dsexHigher) / dsexHigher) * 100;
        float dsexGap = ((Math.abs(dsex.getOpenPrice() - dsex.getClosePrice())) / dsex.getClosePrice()) * 100;
        indexFluctuation = dsexUpperTail + (dsexUpperTail / (2 * dsexUpperTail + dsexGap));

        isBullTrap = isBullTrap();
        isMarketDown = isMarketDown();
        setCause(null);
        potentiality = isPotential();
        //boolean maxvolum = maxVolumeChangeInLastWeek > today.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH*1);
        sma25Diff = ((SignalCalculator.sma25 - SignalCalculator.oneWeekAgoSma25) / SignalCalculator.oneWeekAgoSma25) * 100;
        
//        items.remove(items.size() - 1);
        threeDayBeforeRsi = scanner.calculateRSI(items, items.size()-1);
//        items.remove(items.size()-1);
        fourDayBeforeRsi = scanner.calculateRSI(items, items.size()-2);
//        items.remove(items.size()-1);
        fiveDayBeforeRsi = scanner.calculateRSI(items, items.size()-3);

        if (debugEnabled) {
//            if(potentiality)
            System.out.print("\ncode: " + today.getCode() + ", date: " + today.getDate() + ", close: " + today.getAdjustedClosePrice() + ", tchange: " + df.format(today.getTradeChange()) + ", vchange: " + df.format(today.getVolumeChange()) + ", weekVchange: " + today.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_WEEK) + ", vtc: " + volumePerTradeChange + ", publicShare: " + publicShare + ", div: " + divergence + ", trade: " + today.getTrade() + ", value: " + today.getValue() + ", minValue: " + minValue + ", minTrade: " + minTrade + ", minSmaDiffWithClose: " + minSmaDiffWithClose + ", minDsexSmaDiffWithClose: " + minDsexSmaDiffWithClose + ", gain: " + gain + ", uppertail: " + upperTail + ", vtcRatio: " + vtcRatio + ", rsi: " + rsi + ", div: " + divergence + ", lastMonthSmaVariation: " + lastMonthSmaVariation + ", lastMonthMaximum: " + lastMonthMaximum + ", smaTrend: " + smaTrend + ", sma10: " + sma10 + ", sma25: " + sma25 + ", ex: " + (lastMonthSmaVariation <= 3.7 && lastMonthMaximum <= todayClosePrice) + ", indexFluctuation: " + indexFluctuation + ", oneMonthBackSma25Change: " + oneMonthBackSma25Change + ", dsexyesopen: " + dsexYesterday.getOpenPrice() + ", dsexyesclose: " + dsexYesterday.getClosePrice() + ", isBullTrap: " + isBullTrap + ", maxVolumeChangeInLast3days: " + maxVolumeChangeInLast3days + ", diffWithLastMonthHigh: " + diffWithLastMonthHigh + ", isMarketDown: " + isMarketDown + ", maxGain: " + maxGainAfterBuy + ", poten: " + potentiality + ", avgvaluePerTrade: " + averageValuePerTrade + ", sma25Diff: " + sma25Diff + ", todayGap: " + todayGap + ", diffWithPreviousLow10:" + diffWithPreviousLow10 + ", todayChange: " + todaychange);
        }
    }
    
    private Item getLowest(List<Item> itemss){
        Item minimum = new Item();
        minimum.setAdjustedClosePrice(1000000);
        for(int i=itemss.size()-1; i>=0; i--){
            if(itemss.get(i).getAdjustedClosePrice() < minimum.getAdjustedClosePrice())
                minimum = itemss.get(i);
        }
        return minimum;
    }
    
    private int getGreenCountInLastFewDays(List<Item> items, int days) {
        int greenCount = 0;
        for (int i = items.size() - 2; i > items.size() - days - 1; i--) {
            Item itm = items.get(i);

            float gap = ((itm.getAdjustedClosePrice() - itm.getOpenPrice()) / itm.getOpenPrice()) * 100;
            if(gap >= 0.5)
                ++greenCount;
            
        }

        return greenCount;
    }

    private int getSma25IntersectInLastFewDays(List<Item> items, int days) {
        int intersects = 0;
        for (int i = items.size() - 2; i > items.size() - days - 1; i--) {
            Item itm = items.get(i);

            if (itm!=null && itm.getSmaList().size() == 2) {
                float sm25 = itm.getSmaList().get(25);
                if (sm25 >= itm.getDayLow() && sm25 <= itm.getDayHigh() && itm.getAdjustedClosePrice()>itm.getOpenPrice()) {
                    ++intersects;
                }
            }
        }

        return intersects;
    }

    private int getLastFewDaysMaxVolume(List<Item> items, int days) {
        int maxVolume = 0;
        for (int i = items.size() - 2; i > items.size() - days - 1; i--) {
            Item itm = items.get(i);
            if (itm.getAdjustedVolume() > maxVolume) {
                maxVolume = itm.getAdjustedVolume();
            }
        }
        return maxVolume;
    }

    private boolean isPotential() {
        float saturation = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 1.5f + today.getOpenPrice();
        if (todayGap >= 1.2 && saturation > sma25 && today.getDayLow() <= (sma25 * 1.01)
                && !((today.getDayHigh() - today.getAdjustedClosePrice()) > (today.getAdjustedClosePrice() - today.getOpenPrice()))
                && vChange >= 0.5
                //                && vChange <= 10
                && upperTail < 4
                && divergence <= maxDivergence
                && rsi <= 65) {
            return true;
        }
        return false;
    }

    private float getAverageValuePerTrade(List<Item> items) {

        float valuePerTrade = 0;
        float average = 0;
        for (int i = items.size() - 2; i >= 0; i--) {
            Item anItem = items.get(i);
            valuePerTrade += anItem.getValue() / anItem.getTrade();
        }

        return valuePerTrade / ((float) (items.size() - 1));
    }

    private Item getMaxPriceDayOnLastXDay(List<Item> items, int day) {

        Item maxPriceD = null;
        float maxPrice = 0;
        for (int i = items.size() - 2; i >= items.size() - 1 - day; i--) {
            Item anItem = items.get(i);
            if (Math.max(anItem.getOpenPrice(), anItem.getAdjustedClosePrice()) > maxPrice) {
                maxPrice = Math.max(anItem.getOpenPrice(), anItem.getAdjustedClosePrice());
                maxPriceD = anItem;
            }
        }

        return maxPriceD;
    }

    private float getMaxGainAfterBuy(List<Item> items) {
        float maxGain = 0;
        float gain = 0;
        if (buyItem == null) {
            return 0;
        }

        for (int i = items.size() - 1; i >= 0; i--) {
            Item anItem = items.get(i);
            if (anItem.getDate().before(buyItem.getDate())) {
                break;
            }

            gain = ((anItem.getAdjustedClosePrice() - buyItem.getAverageBuyPrice()) / buyItem.getAverageBuyPrice()) * 100;
            if (gain > maxGain) {
                maxGain = gain;
            }
        }

        return maxGain;
    }

    protected static boolean isMarketDown() {
        float todayDsexMinSma = Math.min(dsex.getSmaList().get(10), dsex.getSmaList().get(25));
        float yesterdayDsexMinSma = Math.min(dsexYesterday.getSmaList().get(10), dsexYesterday.getSmaList().get(25));
        boolean temp1 = ((dsex.getClosePrice() - todayDsexMinSma) / todayDsexMinSma) * 100 < -1;
        boolean temp2 = ((dsexYesterday.getClosePrice() - yesterdayDsexMinSma) / yesterdayDsexMinSma) * 100 < -1;
        boolean temp3 = dsex.getClosePrice() < dsexYesterday.getClosePrice() && dsexYesterday.getClosePrice() < dsexDayBefore.getClosePrice();
//        System.out.print("\ncode: " + today.getCode() + ", date: " + today.getDate() + ", temp1: " + temp1 + ", temp2: " + temp2 + ", temp3: " + temp3  + ", isMarketDown: " + isMarketDown + ", dsexYesterday.getClosePrice(): " + dsexYesterday.getClosePrice() + ", yesterdayDsexMinSma: " + yesterdayDsexMinSma);
        return temp1 && temp2 && temp3;
    }

    protected static boolean getMaxVolumeChangeInLast3days(List<Item> items, int days) {
        int size = items.size();
        float twoMonthMax = 0;
        for (int i = size - 5; i > size - days && i >= 0; i--) {
            if (items.get(i).getVolumeChanges().isEmpty()) {
                continue;
            }
            float change = items.get(i).getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH * 2);
            if (change > twoMonthMax) {
                twoMonthMax = change;
            }

        }

        if (twoDayBeforeYesterday.getVolumeChanges().isEmpty()) {
            return false;
        }
        float threeDaysMax = 0;

        Item maxDay = twoDayBeforeYesterday;
        if (dayBeforeYesterday.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH * 2) > maxDay.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH * 2)) {
            maxDay = dayBeforeYesterday;
        }
        if (yesterday.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH * 2) > maxDay.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH * 2)) {
            maxDay = yesterday;
        }

        //threeDaysMax = Math.max(yesterday.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH * 2), dayBeforeYesterday.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH * 2));
        //threeDaysMax = Math.max(threeDaysMax, twoDayBeforeYesterday.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH * 2));
        //System.out.println("\ntoday: " + today.getDate() + ", threeDaysMax: " + threeDaysMax + ", twoMonthMax: " + twoMonthMax + ", vChange: " + vChange + ", divergence: " + divergence + ", diffWithPreviousLow10: " + diffWithPreviousLow10);
        return (maxDay.getAdjustedClosePrice() - maxDay.getOpenPrice()) > 0 && (maxDay.getVolumeChanges().get(ScannerService.TRADING_DAYS_IN_A_MONTH * 2) > twoMonthMax * 1.5f) && divergence <= maxDivergence && diffWithPreviousLow10 <= 10;
    }

    private static boolean isBullTrap() {
        float minDsexRsiInLast2Days = Math.min(dsexDayBefore.getRSI(), dsexYesterday.getRSI());
        minDsexRsiInLast2Days = Math.min(minDsexRsiInLast2Days, dsex.getRSI());
        float todayIndexGap = dsex.getClosePrice() - dsexYesterday.getClosePrice();
        float yesterdayIndexChange = dsexYesterday.getClosePrice() - dsexDayBefore.getClosePrice();
        //System.out.println("minDsexRsiInLast2Days: " + minDsexRsiInLast2Days + ", todayIndexGap: " + todayIndexGap + ", indexFluctuation: " + indexFluctuation);
        if (minDsexRsiInLast2Days <= 30 && todayIndexGap > 0 && indexFluctuation > 1) {
            return true;
        }

        if (minDsexRsiInLast2Days <= 30 && todayIndexGap > 0 && todayIndexGap <0.8 && yesterdayIndexChange < 0) {
            return true;
        }

        return false;
    }

    protected static float getDBHammer(Item item) {
        float difference = Math.abs(item.getOpenPrice() - item.getAdjustedClosePrice());
        float largest = (item.getOpenPrice() + item.getAdjustedClosePrice() + difference) / 2;
        float smallest = (item.getOpenPrice() + item.getAdjustedClosePrice() - difference) / 2;
        float dbHammer = (((smallest - item.getDayLow()) - (item.getDayHigh() - largest)) / item.getAdjustedClosePrice()) * 100;
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

    protected float getLastFiewDaysMaximumClosing(List<Item> items, int days) {
        int size = items.size();
        int counter = 0;
        float maximum = 0;

        for (int i = size - 2; i >= 0; i--) {
            float openPrice = items.get(i).getOpenPrice();
            float closePrice = items.get(i).getAdjustedClosePrice();
            float dayHigh = closePrice;
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

    protected float getLastFiewDaysSmaVariation(List<Item> items, int days) {
        int size = items.size();
        int counter = 0;
        float maximum = 0;
        float minimum = 100000;

        for (int i = size - 2; i >= 0; i--) {
            Map<Integer, Float> smaList = items.get(i).getSmaList();
            if (smaList != null && smaList.isEmpty()) {
                smaList.put(10, calculateSMA(items, 10));
                smaList.put(25, calculateSMA(items, 25));
            }

            float sma_10 = smaList.get(10);
            float sma_25 = smaList.get(25);
            float smaHigh = Math.max(sma_10, sma_25);
            float smaLow = Math.min(sma_10, sma_25);
//            float smaHigh = sma_10;
//            float smaLow = sma_10;

            if (smaHigh > maximum) {
                maximum = smaHigh;
            }

            if (smaLow < minimum) {
                minimum = smaLow;
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

    protected float getPriceDiffWithPreviousHigh(List<Item> items, int days) {
        int size = items.size();
        int counter = 0;
        float maximum = 0;

        for (int i = size - 2; i >= 0; i--) {
            float closePrice = items.get(i).getAdjustedClosePrice();
            if (closePrice > maximum) {
                maximum = closePrice;
            }
            ++counter;
            if (counter == days) {
                break;
            }
        }

        float lastDayClosePrice = items.get(size - 1).getAdjustedClosePrice();
        float diff = ((lastDayClosePrice - maximum) / maximum) * 100;
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
        float uTail = ((item.getDayHigh() - largest) / largest) * 100;
        float lowest = Math.min(item.getOpenPrice(), item.getAdjustedClosePrice());
        float bTail = ((lowest - item.getDayLow()) / lowest) * 100;
        uTail = uTail - bTail / 2;
        //float uTail = ((item.getHigh() - todayClosePrice) / todayClosePrice) * 100;
        //if(uTail>2)
        //    uTail = uTail + 2/(1+Math.abs(todayGap));
        if (todayGap < 0) {
            uTail += 0.75;
        }
        return uTail;
    }

    private Item getCalculatedItem(List<Item> itemsSublist) {
        //System.out.println("items: " + items);
        Item calculated = itemsSublist.get(itemsSublist.size() - 1);
        float twoMonthlyVolumeChange = scanner.calculateVolumeChange(itemsSublist, ScannerService.TRADING_DAYS_IN_A_MONTH * 2);
        float monthlyVolumeChange = scanner.calculateVolumeChange(itemsSublist, ScannerService.TRADING_DAYS_IN_A_MONTH);
        float weeklyVolumeChange = scanner.calculateVolumeChange(itemsSublist, ScannerService.TRADING_DAYS_IN_A_WEEK);
        float tradeChange = scanner.calculateTradeChange(itemsSublist, ScannerService.TRADING_DAYS_IN_A_MONTH);
        float tempRsi = scanner.calculateRSI(itemsSublist);
        scanner.calculateDivergence(itemsSublist);
        int diverge = itemsSublist.get(itemsSublist.size() - 1).getDivergence();
        calculated.setVolumeChange(monthlyVolumeChange);    //This would be removed in future
        calculated.getVolumeChanges().put(ScannerService.TRADING_DAYS_IN_A_MONTH * 2, twoMonthlyVolumeChange);
        calculated.getVolumeChanges().put(ScannerService.TRADING_DAYS_IN_A_MONTH, monthlyVolumeChange);
        calculated.getVolumeChanges().put(ScannerService.TRADING_DAYS_IN_A_WEEK, weeklyVolumeChange);
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
    
    protected float calculateSMA(List<Item> items, int N, int index) {
        List<Item> itemsCopy = new ArrayList<>(items);
        for(int i=itemsCopy.size()-1; i>index; i--)
            itemsCopy.remove(i);
        
        int limit = N;
        if (itemsCopy.size() < limit) {
            limit = itemsCopy.size();
        }

        float SMA = 0;
        for (int i = itemsCopy.size() - 1; i >= (itemsCopy.size() - limit); i--) {
            SMA += itemsCopy.get(i).getAdjustedClosePrice();
        }
        SMA = SMA / limit;

        return SMA;
    }
    
    protected float calculateBackdatedSMA(List<Item> originalItems, int N, int backDays) {
        
        List<Item> items = new ArrayList<>(originalItems);
        
        if(items.size() > backDays){
            for(int i=0; i<backDays; i++){
                items.remove(items.size()-1);
            }
        }
        
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

    protected void setCause(String cause) {
        this.cause = cause;
    }

}
