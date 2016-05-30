package com.mycompany.service;

import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.dao.PortfolioDaoImpl;
import com.mycompany.model.ChangesInVolumePerTrade;
import com.mycompany.model.DividentHistory;
import com.mycompany.model.Item;
import com.mycompany.model.Portfolio;
import com.mycompany.model.PortfolioItem;
import com.mycompany.service.calculator.SignalCalculator;
import com.mycompany.service.calculator.buy.Average;
import com.mycompany.service.calculator.buy.BuySignalCalculator;
import com.mycompany.service.calculator.buy.Consecutive1;
import com.mycompany.service.calculator.buy.Consecutive15;
import com.mycompany.service.calculator.buy.Consecutive2;
import com.mycompany.service.calculator.buy.Consecutive3;
import com.mycompany.service.calculator.buy.ExtendedSMA25;
import com.mycompany.service.calculator.buy.GreenAfterRsi30;
import com.mycompany.service.calculator.buy.LargeCandle;
import com.mycompany.service.calculator.buy.Macd;
import com.mycompany.service.calculator.buy.MultipleSma25Intersect;
import com.mycompany.service.calculator.buy.Sma25;
import com.mycompany.service.calculator.buy.Sma25Trend;
import com.mycompany.service.calculator.buy.SmaIntersect;
import com.mycompany.service.calculator.buy.SuddenHike;
import com.mycompany.service.calculator.buy.Tail;
import com.mycompany.service.calculator.buy.Test1;
import com.mycompany.service.calculator.buy.ThreeGreen;
import com.mycompany.service.calculator.sell.ClusteredSellSignalCalculator;
import com.mycompany.service.calculator.sell.SellSignalCalculator;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @date Apr 24, 2015
 * @author Setu
 */
public class ScannerService {

    private final ItemDaoImpl dao;
    private final String HAMMER = "HAMMER";
    private final String VOLUME_CHANGE = "VOLUME_CHANGE";
    private final String CANDLESTICK_LENGTH_CHANGE = "CANDLESTICK_LENGTH_CHANGE";
    private final String CONSECUTIVE_GREEN = "CONSECUTIVE_GREEN";
    private final String TRADE_CHANGE = "TRADE_CHANGE";
    private final String RSI = "RSI";
    private final String DIVERGENCE = "DIVERGENCE";
    private final String SIGNAL = "SIGNAL";
    private final String VTC_SIGNAL = "VTC_SIGNAL";
    private final String DIVIDENT_YIELD = "DIVIDENT_YIELD";
    public static final int RSI_PERIOD = 14;
    public static final int TRADING_DAYS_IN_A_YEAR = 250;
    //public static final int TRADING_DAYS_IN_2_MONTH = 44;
    public static final int TRADING_DAYS_IN_A_MONTH = 22;
    public static final int TRADING_DAYS_IN_A_WEEK = 5;
    public static final int DAYS_IN_A_YEAR = 365;
    private Portfolio portfolio;
    private PortfolioDaoImpl portfolioDao;
    private DecimalFormat df = new DecimalFormat("#.#");
    //private static float MACD_MAX = 0;

    public ScannerService() {
        dao = new ItemDaoImpl();
        portfolioDao = new PortfolioDaoImpl();
        try{
        portfolioDao.open();
        portfolio = portfolioDao.getPortfolio(SyncService.PORTFOLIO_ID);
        portfolioDao.close();
        }catch(Exception ex){
            System.out.println("Exception caught: " + ex.getMessage());
        }
    }

    public List<Item> getItemsWithscannedProperties() throws SQLException, ClassNotFoundException {
        Calendar start = Calendar.getInstance();
        dao.open();

        Utils.updateDates(dao);
        
        List<Item> items = getPressure();
        //CustomHashMap oneYearData = dao.getOneYearData();
        //Get 2 month data
        CustomHashMap dataArchive = dao.getData(365);
        //System.out.println("up to one year data time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        mergeItems(items, getHammer(), HAMMER);
        //System.out.println("up to hammer time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        mergeItems(items, getCandlestickLengthChange(dataArchive), CANDLESTICK_LENGTH_CHANGE);
        //System.out.println("up to candle stick change time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        mergeItems(items, getConsecutiveGreen(), CONSECUTIVE_GREEN);
        //System.out.println("up to consecutive green time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        mergeItems(items, getTradeChange(dataArchive), TRADE_CHANGE);
        //System.out.println("up to trade change time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        mergeItems(items, getRSI(dataArchive), RSI);
        //System.out.println("up to rsi time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        mergeItems(items, getDivergence(dataArchive), DIVERGENCE);
        //System.out.println("up to divergence time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        mergeItems(items, getVolumeChange(dataArchive), VOLUME_CHANGE);
        //System.out.println("up to volume change time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        //mergeItems(items, getDivergenceAndRSIBasedSignal(items, dataArchive), SIGNAL);
        //System.out.println("up to getDivergenceAndRSIBasedSignal time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        mergeItems(items, getPurifiedSignal(dataArchive), SIGNAL);
        //System.out.println("up to getPurifiedSignal time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        //mergeItems(items, getVolumePerTradeChangeBasedSignal(items, dataArchive), VTC_SIGNAL);
        mergeItems(items, getDividentYield(dataArchive), DIVIDENT_YIELD);

        dao.close();
        interceptDSEXItem(items);
        return items;
    }
    
    private List<Item> getDividentYield(CustomHashMap oneYearData){
        Date latestDividentDate;
        DividentHistory latestCashHistory;
        
        List<Item> distinctItems = new ArrayList<>();
        for (String code : oneYearData.keySet()) {
            if (code.equals("DSEX")) {
                continue;
            }
            List<Item> items = oneYearData.getItems(code);
            Collections.sort(items);
            Item item = items.get(items.size() - 1);

            List<DividentHistory> histories = Utils.getDividentHistory(item.getCode());
            latestDividentDate = new Date(Long.MIN_VALUE);
            latestCashHistory = null;
            
            //Find lastestDividentDate
            for(DividentHistory history: histories){
                if(!history.getDate().before(latestDividentDate) && history.getDate().before(item.getDate()))
                    latestDividentDate = history.getDate();
            }
            
            for(DividentHistory history: histories){
                if(history.getDate().equals(latestDividentDate) && history.getType().equals(DividentHistory.DividentType.CASH))
                    latestCashHistory = history;
            }
            
            if(latestCashHistory != null){
                float cashDivident = latestCashHistory.getPercent();
                float dividentYield = (cashDivident*10)/item.getAdjustedClosePrice();
                String dividentYieldString = df.format(dividentYield);
                
                try{
                    dividentYield = Float.parseFloat(dividentYieldString);
                    item.setDividentYield(dividentYield);
                }catch(NumberFormatException nfe){
                    System.out.println("NumberFormatException: " + nfe.getMessage() + ", dividentYield: " + dividentYield + ", code: " + item.getCode());
                }
            }
            
            distinctItems.add(item);
        }
        
        return distinctItems;
    }

    private void interceptDSEXItem(List<Item> items) {
        Item dsex = getItemByCode(items, "DSEX");
        dsex.setVolumeChange(0);
        dsex.setcLengthChange(0);
    }

    private List<Item> getPressure() throws SQLException, ClassNotFoundException {
        List<Item> items = dao.getBSPressure();
        return items;
    }

    private List<Item> getHammer() throws SQLException, ClassNotFoundException {
        List<Item> items = dao.getHammer();
        return items;
    }

    @Deprecated
    private List<Item> getVolumeChange() throws SQLException, ClassNotFoundException {
        List<Item> items = dao.getVolumeChange();
        return items;
    }

    @Deprecated
    private List<Item> getTradeChange() throws SQLException, ClassNotFoundException {
        List<Item> items = dao.getTradeChange();
        return items;
    }

    private List<Item> getCandlestickLengthChange() throws SQLException, ClassNotFoundException {
        List<Item> items = dao.getCandleLengthChange();
        return items;
    }

    private List<Item> getCandlestickLengthChange(CustomHashMap oneYearData) throws SQLException, ClassNotFoundException {
        List<Item> distinctItems = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.#");
        for (String code : oneYearData.keySet()) {
            if (code.equals("DSEX")) {
                continue;
            }
            List<Item> items = oneYearData.getItems(code);
            Collections.sort(items);
            Item item = new Item();
            item.setCode(code);
            Item today = items.get(items.size() - 1);
            float candleLength = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
            String candleLengthString = df.format(candleLength);

            try {
                if (Float.isNaN(candleLength)) {
                    candleLengthString = "0";
                } else {
                    candleLength = Float.parseFloat(candleLengthString);
                }
            } catch (java.lang.NumberFormatException nfe) {
                System.out.println("NumberFormatException:: candleLength: " + candleLength + ", candleLengthString: " + candleLengthString + ", code: " + code);
            }

            item.setcLengthChange(candleLength);
            distinctItems.add(item);
        }

        return distinctItems;
    }

    private List<Item> getConsecutiveGreen() throws SQLException, ClassNotFoundException {
        List<Item> items = dao.getConsecutiveGreen();
        return items;
    }

    private List<Item> getSignal(List<Item> calculatedItems, CustomHashMap oneYearData) throws SQLException, ClassNotFoundException {
        List<Item> distinctItems = new ArrayList<>();
        for (String code : oneYearData.keySet()) {
            List<Item> items = oneYearData.getItems(code);
            Collections.sort(items);
            Item item = new Item();
            item.setCode(code);
            Item calculatedItem = getItemByCode(calculatedItems, code);
            Item.SignalType signal = getSignal(calculatedItem, items);
            item.setSignal(signal);
            distinctItems.add(item);

        }

        return distinctItems;
    }

    private Item getDSEXIndex(CustomHashMap oneYearData, Date date) {
        List<Item> items = oneYearData.getItems("DSEX");
        if (date == null) {
            Item today = items.get(items.size() - 1);
            date = today.getDate();
        }

        List<Item> itemSubList = new ArrayList();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getDate().after(date)) {
                break;
            }
            itemSubList.add(item);
        }
        Collections.sort(itemSubList);

        float sma10 = calculateSMA(itemSubList, 10);
        float sma25 = calculateSMA(itemSubList, 25);
        float rsi = calculateRSI(itemSubList);
        Item dsex = itemSubList.get(itemSubList.size() - 1);
        dsex.getSmaList().put(10, sma10);
        dsex.getSmaList().put(25, sma25);
        dsex.setRSI(rsi);
        itemSubList.remove(itemSubList.size() - 1);
        rsi = calculateRSI(itemSubList);
        dsex.setYesterdayRSI(rsi);
        itemSubList.remove(itemSubList.size() - 1);
        rsi = calculateRSI(itemSubList);
        dsex.setDayBeforeYesterdayRSI(rsi);
        return dsex;
    }

    @Deprecated
    private List<Item> getDivergenceAndRSIBasedSignal(List<Item> calculatedItems, CustomHashMap oneYearData) throws SQLException, ClassNotFoundException {
        List<Item> distinctItems = new ArrayList<>();

        Item dsex = getDSEXIndex(oneYearData, null);

        for (String code : oneYearData.keySet()) {
            List<Item> items = oneYearData.getItems(code);
            Collections.sort(items);
            Item item = new Item();
            item.setCode(code);
            item.setSignal(Item.SignalType.HOLD);
            Item calculatedItem = getItemByCode(calculatedItems, code);

            if (calculatedItem == null) {
                continue;
            }

            if (items.size() < TRADING_DAYS_IN_A_MONTH + 3) {
                calculatedItem.setSignal(Item.SignalType.HOLD);
                continue;
            }

            Item today = items.get(items.size() - 1);
            Item yesterday = items.get(items.size() - 2);
            Item dayBeforeYesterday = items.get(items.size() - 3);
            Item twoDayBeforeYesterday = items.get(items.size() - 4);
            float divergence = calculatedItem.getDivergence();
            float rsi = calculatedItem.getRSI();
            float vChange = calculatedItem.getVolumeChange();
            float tChange = calculatedItem.getTradeChange();
            float todayTrade = today.getTrade();
            float todayValue = today.getValue();
            float volumePerTradeChange = today.getVolumePerTradeChange();

            float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
            float todaychange = ((today.getAdjustedClosePrice() - today.getYesterdayClosePrice()) / today.getYesterdayClosePrice()) * 100;
            float yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
            float yesterdaychange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
            float dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;

            float last3DaysMax = Math.max(twoDayBeforeYesterday.getAdjustedClosePrice(), twoDayBeforeYesterday.getOpenPrice());
            last3DaysMax = Math.max(Math.max(dayBeforeYesterday.getAdjustedClosePrice(), dayBeforeYesterday.getOpenPrice()), last3DaysMax);
            last3DaysMax = Math.max(Math.max(yesterday.getAdjustedClosePrice(), yesterday.getOpenPrice()), last3DaysMax);

            float diffWithPreviousLow10 = getPriceDiffWithPreviousLow(items, 10);
            float todayDiv = (today.getEmaList().get(12) - today.getEmaList().get(26)) - today.getEmaList().get(9);
            float yesterdayDiv = (yesterday.getEmaList().get(12) - yesterday.getEmaList().get(26)) - yesterday.getEmaList().get(9);
            float dayBeforeYesterdayDiv = (dayBeforeYesterday.getEmaList().get(12) - dayBeforeYesterday.getEmaList().get(26)) - dayBeforeYesterday.getEmaList().get(9);
            float macd = today.getEmaList().get(12) - today.getEmaList().get(26);

            float todaySignalLine = 0;
            Object todaySignalLineObject = today.getEmaList().get(9);
            if (todaySignalLineObject != null) {
                todaySignalLine = (float) todaySignalLineObject;
            }

            float difference = Math.abs(yesterday.getOpenPrice() - yesterday.getAdjustedClosePrice());
            float smallest = (yesterday.getOpenPrice() + yesterday.getAdjustedClosePrice() - difference) / 2;
            float tail = ((smallest - yesterday.getDayLow()) / smallest) * 100;
            float upperTail = getUpperTail(today);

            float sma10 = calculateSMA(items, 10);
            float sma25 = calculateSMA(items, 25);

            float halfway = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 2 + today.getOpenPrice();

            items.remove(items.size() - 1);
            float yesterdayRsi = calculateRSI(items);
            items.remove(items.size() - 1);
            float dayBeforeRsi = calculateRSI(items);

            boolean smapass = !(today.getAdjustedClosePrice() < sma10 && today.getAdjustedClosePrice() < sma25);
            boolean final1 = Math.min(yesterdayRsi, dayBeforeRsi) <= 27 || smapass;

            boolean dsexpass = !(dsex.getClosePrice() < dsex.getSmaList().get(10) && dsex.getClosePrice() < dsex.getSmaList().get(25));
            boolean final2 = Math.min(dsex.getYesterdayRSI(), dsex.getDayBeforeYesterdayRSI()) <= 27 || dsexpass;

            distinctItems.add(item);

            if (!(final1 && final2)) {
                continue;
            }

            if ((today.getAdjustedClosePrice() - today.getYesterdayClosePrice()) <= 0.1) {
                continue;
            }

            if ((today.getDayHigh() - today.getAdjustedClosePrice()) >= (today.getAdjustedClosePrice() - today.getOpenPrice())) {
                continue;
            }

            if (((todaychange >= 1 && todayGap >= 0.5) && (yesterdaychange >= 1 || yesterdayGap >= 0.5) && (todaychange + yesterdaychange) > 1)
                    && divergence <= 5
                    && rsi <= 50
                    && todayValue >= 1
                    && todayTrade >= 50
                    && vChange >= 0.3
                    && volumePerTradeChange < 1.8
                    && Math.min(todayGap, yesterdayGap) > -3
                    && diffWithPreviousLow10 <= 10 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                    && upperTail < 4) {
                item.setSignal(Item.SignalType.BUY);
                System.out.println("Consecutive-Date: " + today.getDate() + ", code: " + code + ", tchange: " + tChange + ", volumeChange: " + vChange);
            } else if ((todaychange >= 1 && todayGap >= 0.5) && (yesterdaychange >= 1 || yesterdayGap >= 0.5)
                    && todayDiv > yesterdayDiv
                    && yesterdayDiv > dayBeforeYesterdayDiv
                    && todaySignalLine <= 0
                    && macd <= todaySignalLine
                    && diffWithPreviousLow10 <= 10
                    && todayValue >= 1
                    && todayTrade >= 50
                    && vChange >= 0.3
                    && upperTail < 4) {
                item.setSignal(Item.SignalType.BUY);
                System.out.println("Macd0000000-Date: " + today.getDate() + ", code: " + code + ", tchange: " + tChange + ", volumeChange: " + vChange);
            } else if ((todayGap >= 1 && todaychange >= 0.5)
                    && tail >= 3 && (yesterdayGap > (-tail / 2))
                    && calculatedItem.getRSI() <= 40
                    && calculatedItem.getDivergence() <= 5
                    && todayValue >= 1
                    && todayTrade >= 50
                    && vChange >= 0.3
                    && upperTail < 4) {
                item.setSignal(Item.SignalType.BUY);
                System.out.println("Tail0000000-Date: " + today.getDate() + ", code: " + code + ", tchange: " + tChange + ", volumeChange: " + vChange);
            } else if (((todaychange >= 1 && todayGap >= 0.5) && (yesterdayGap > 0 && dayBeforeYesterdayGap > 0) && (todaychange + yesterdaychange) > 1)
                    && divergence <= 5
                    && rsi <= 45
                    && todayValue >= 1
                    && todayTrade >= 50
                    && vChange >= 0.3
                    && volumePerTradeChange < 1.8
                    && Math.min(todayGap, yesterdayGap) > -3
                    && diffWithPreviousLow10 <= 10 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                    && upperTail < 4) {
                item.setSignal(Item.SignalType.BUY);
                System.out.println("Three000-Date: " + today.getDate() + ", code: " + code + ", tchange: " + tChange + ", volumeChange: " + vChange);
            } else if ((todayGap >= 1 && today.getAdjustedClosePrice() > last3DaysMax)
                    && divergence <= 5
                    && rsi <= 35
                    && todayValue >= 1
                    && todayTrade >= 50
                    && vChange >= 0.3
                    && volumePerTradeChange < 1.8
                    && Math.min(todayGap, yesterdayGap) > -3
                    && diffWithPreviousLow10 <= 10 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                    && upperTail < 4) {
                item.setSignal(Item.SignalType.BUY);
                System.out.println("Sudengren-Date: " + today.getDate() + ", code: " + code + ", tchange: " + tChange + ", volumeChange: " + vChange);
            } else if ((todayGap >= 1 && halfway > sma25 && today.getDayLow() <= sma25)
                    && divergence <= 5
                    && todayValue >= 1
                    && todayTrade >= 50
                    && vChange >= 0.3
                    && volumePerTradeChange < 1.8
                    && Math.min(todayGap, yesterdayGap) > -3
                    && diffWithPreviousLow10 <= 10 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                    && upperTail < 4) {
                item.setSignal(Item.SignalType.BUY);
                System.out.println("sma250000Date: " + today.getDate() + ", code: " + code + ", tchange: " + tChange + ", volumeChange: " + vChange);
            } else {
                //item.setSignal(Item.SignalType.HOLD);
            }

        }

        return distinctItems;
    }

    private List<Item> getPurifiedSignal(CustomHashMap oneYearData) throws SQLException {
        List<BuySignalCalculator> buyCalculators = new ArrayList<>();
        buyCalculators.add(new Consecutive1(this, oneYearData, portfolio));
        buyCalculators.add(new Consecutive15(this, oneYearData, portfolio));
        buyCalculators.add(new ExtendedSMA25(this, oneYearData, portfolio));
        buyCalculators.add(new Tail(this, oneYearData, portfolio));
        buyCalculators.add(new ThreeGreen(this, oneYearData, portfolio));
        buyCalculators.add(new Sma25Trend(this, oneYearData, portfolio));
        buyCalculators.add(new LargeCandle(this, oneYearData, portfolio));
        buyCalculators.add(new MultipleSma25Intersect(this, oneYearData, portfolio));
        buyCalculators.add(new SmaIntersect(this, oneYearData, portfolio));
        buyCalculators.add(new Test1(this, oneYearData, portfolio));
        buyCalculators.add(new Average(this, oneYearData, portfolio));

        List<SellSignalCalculator> sellCalculators = new ArrayList<>();
        ClusteredSellSignalCalculator clusterSell = new ClusteredSellSignalCalculator();
        sellCalculators.add(new ClusteredSellSignalCalculator.sell1(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell2(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell3(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell4(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell5(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell55(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell56(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell6(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell7(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell8(this, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell9(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell10(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell11(this, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.sell14(this, oneYearData, portfolio));
        //sellCalculators.add(new ClusteredSellSignalCalculator.EOM(this, oneYearData, portfolio));

        List<Item> distinctItems = new ArrayList<>();

        outerloop:
        for (String code : oneYearData.keySet()) {
            List<Item> items = oneYearData.getItems(code);
            Collections.sort(items);
            Item item = new Item();
            item.setCode(code);
            item.setSignal(Item.SignalType.NA);
            distinctItems.add(item);
            PortfolioItem pItem = portfolio.getPortfolioItems().get(code);
            if (pItem != null) {
                item.setSignal(Item.SignalType.HOLD);
            }

            SignalCalculator aCalculator = buyCalculators.get(0);
            List<Item> copyOfSubList = new ArrayList<>(items);
            aCalculator.intializeVariables(copyOfSubList, null);
            item.setPotentiality(SignalCalculator.potentiality);

            for (BuySignalCalculator calculator : buyCalculators) {
                if (calculator.isBuyCandidate(items, null)) {
                    //System.out.println("date: " + SignalCalculator.today.getDate() + ", code: " + code + ", gain: " + SignalCalculator.gain + ", buycause: " + calculator.getCause() + ", pItem: " + pItem);
                    if (pItem == null) {
                        item.setSignal(Item.SignalType.BUY);
                    } else if (SignalCalculator.gain >= 0) {
                        item.setSignal(Item.SignalType.HOLD);
                    } else {
                        item.setSignal(Item.SignalType.AVG);
                    }

                    //if(!today.getDate().after(SignalCalculator.lastTradingDay.getTime()))
                    continue outerloop;
                }
            }

            //No buy item, so not need to check sell
            if (pItem == null) {
                continue;
            }

            for (SellSignalCalculator calculator : sellCalculators) {
                if (calculator.isSellCandidate(items, null)) {
                    item.setSignal(Item.SignalType.SELL);
                    String cause = calculator.getClass().getName();
                    cause = cause.substring(cause.indexOf("$") + 1);
                    continue outerloop;
                }
            }
        }

        return distinctItems;
    }

    private float getUpperTail(Item item) {
        float largest = Math.max(item.getOpenPrice(), item.getAdjustedClosePrice());
        float upperTail = ((item.getDayHigh() - largest) / largest) * 100;
        return upperTail;
    }

    private List<Item> getVolumePerTradeChangeBasedSignal(List<Item> calculatedItems, CustomHashMap oneYearData) throws SQLException, ClassNotFoundException {
        List<Item> distinctItems = new ArrayList<>();
        for (String code : oneYearData.keySet()) {
            List<Item> items = oneYearData.getItems(code);
            Collections.sort(items);
            Item item = new Item();
            item.setCode(code);
            Item calculatedItem = getItemByCode(calculatedItems, code);
            Item.SignalType signal = getVolumePerTradeChangeBasedSignal(calculatedItem, items);
            item.setVtcSignal(signal);
            distinctItems.add(item);

        }

        return distinctItems;
    }

    /**
     * @Buy rule
     * @1. Today price change should be greater than equal 1%
     * @2. Should have consecutive green or volume/trade change greater than 2
     * than year average and greater than 1.5 than last 7 days
     * @3. Close price - open price should be greater than 1% of open price
     * @4. Today close price greater than day_before_yesterday_open
     * @5. Both vchange and tchange should be greater than 0.8
     * @6. Divergence should be less than equal 30
     * @7. RSI should be less than equal 70
     * @8. Trade should be at least 50
     * @9. Value should be at least 2 million
     * @10. Pressure not less than -5
     * @11. One day gap greater than 7% or 2 day gap greater than 8% or 3 day
     * gap greater than 8%
     * @12. Hammer should be greater than -2
     *
     * @Sale rule
     * @1. Should have two consecutive red (among them at least one day
     * open-close should be more than 1%) and close price less than day before
     * yesterday open price
     * @2. Close price less than buy day open price
     * @3. Open price - close price is more than 6%
     * @4. close price dropped to more than 6% of previous close price
     * @5. Today is down and yesterday was hammer<-2
     * @6. Today is down and RSI > 70
     * @7. Today is down and pressure <-10
     * @8. Today is down and todays higher greater than yesterdsys higher and
     * todays lower less than yesterdays lower
     * @9. Today close price less than yesterday minimum and less than day
     * before yesterday minimum
     * @10. Tree consecutive red
     * @param items
     * @return
     */
    private Item.SignalType getSignal(Item calculatedItem, List<Item> items) {
        if (items.size() < 3) {
            return null;
        }

        if (isBuySignal(calculatedItem, items)) {
            return Item.SignalType.BUY;
        }

        if (isSellSignal(calculatedItem, items)) {
            return Item.SignalType.SELL;
        }

        return Item.SignalType.HOLD;
    }

    private Item.SignalType getVolumePerTradeChangeBasedSignal(Item calculatedItem, List<Item> items) {
        if (items.size() < 4 || calculatedItem == null) {
            return null;
        }

        int size = items.size();
        Item today = items.get(size - 1);
        Item yesterday = items.get(size - 2);
        Item dayBeforeYesterday = items.get(size - 3);

        float tcYesterday = (float) yesterday.getTrade() / (float) dayBeforeYesterday.getTrade();
        float vcYesterday = (float) yesterday.getAdjustedVolume() / (float) dayBeforeYesterday.getAdjustedVolume();
        float vtcRatioYesterday = vcYesterday / tcYesterday;

        float tcToday = (float) today.getTrade() / (float) yesterday.getTrade();
        float vcToday = (float) today.getAdjustedVolume() / (float) yesterday.getAdjustedVolume();
        float vtcRatioToday = vcToday / tcToday;
        float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
        float todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;

        float volumeChange = calculatedItem.getVolumeChange();
        float tradeChange = calculatedItem.getTradeChange();
        int divergence = calculatedItem.getDivergence();
        int trade = calculatedItem.getTrade();
        float value = calculatedItem.getValue();

        float tradeChangeWithYesterday = ((float) today.getTrade() / (float) yesterday.getTrade());
        float volumeChangeWithYesterday = ((float) today.getAdjustedVolume() / (float) yesterday.getAdjustedVolume());
        float diffWithPreviousLow = getPriceDiffWithPreviousLow(items, 10);

        ChangesInVolumePerTrade changes = ChangesInVolumePerTrade.getChangesInVolumePerTrade(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
        float previousYesterdayVolumePerTradeChange = changes.getDayBeforeYesterdayChange();
        float yesterdayVolumePerTradeChange = changes.getYesterdayChange();
        float todayVolumePerTradeChange = changes.getTodayChange();

        if ((todaychange > 0.5 && todayGap >= 0)
                && !(yesterdayVolumePerTradeChange > 1 && previousYesterdayVolumePerTradeChange > 1)
                && ((todayVolumePerTradeChange > 1.5
                && (vtcRatioToday > 0.95 && (vtcRatioToday - vtcRatioYesterday) > -0.3)
                && (volumeChange > 2 && tradeChange >= 1.6)
                && (volumeChangeWithYesterday > 1.5)))
                && diffWithPreviousLow < 12
                && divergence < 20
                && trade >= 50) {
            System.out.println("Date: " + today.getDate() + ", code: " + today.getCode() + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + todayVolumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday + ", diffWithPreviousLow: " + diffWithPreviousLow);
            return Item.SignalType.BUY;
        }

        return Item.SignalType.HOLD;
    }

    private boolean isSellSignal(Item calculatedItem, List<Item> items) {
        if (calculatedItem == null) {
            return false;
        }

        Item today = items.get(items.size() - 1);
        Item yesterday = items.get(items.size() - 2);
        Item dayBeforeYesterday = items.get(items.size() - 3);

        float todayPriceChange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
        //float todayPriceChangeWithRespectToOpen = ((today.getAdjustedClosePrice()-yesterday.getOpenPrice())/yesterday.getOpenPrice())*100;
        //float todayPriceChange = todayPriceChangeWithRespectToClose<todayPriceChangeWithRespectToOpen ? todayPriceChangeWithRespectToClose: todayPriceChangeWithRespectToOpen;
        float todayPriceGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
        float yesterdayPriceChange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
        float yesterdayPriceGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
        float dayBeforeYesterdayPriceGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;

        boolean twoConsecutiveRed = false;
        if ((todayPriceChange <= -0.5 && yesterdayPriceChange <= -1.0) || (todayPriceChange <= -1.0 && yesterdayPriceChange <= -0.5)) {
            twoConsecutiveRed = true;
        }

        //@1. Should have two consecutive red (among them at least one day open-close should be more than 1%) and close price less than day before yesterday open price
        if (twoConsecutiveRed && today.getAdjustedClosePrice() < dayBeforeYesterday.getOpenPrice() && isSellCandidate(calculatedItem, items)) {
            return true;
        }

        boolean threeConsecutiveRed = false;
        if (todayPriceGap <= 0 && yesterdayPriceGap <= 0 && dayBeforeYesterdayPriceGap <= 0) {
            threeConsecutiveRed = true;
        }

        if (threeConsecutiveRed && isSellCandidate(calculatedItem, items)) {
            return true;
        }

        //@2. Close price less than buy day open price
        //Should be implemented later
        //@3. Open price - close price is more than 6% 
        if (todayPriceGap < -6) {
            return true;
        }

        //@4. close price dropped to more than 6% of previous close price
        if (todayPriceChange < -6) {
            return true;
        }

        //@5. Today is down and yesterday was hammer<-2
        if (todayPriceGap < 0 && calculatedItem.getHammer() < -2) {
            return true;
        }

        //@6. Today is down and is less than 3% and RSI > 70
        if (todayPriceGap < -3 && calculatedItem.getRSI() > 70) {
            return true;
        }

        //@7. Today is down and pressure <-10
        if (todayPriceGap < 0 && calculatedItem.getPressure() < -10) {
            return true;
        }

        float todayPriceDiff = Math.abs(today.getOpenPrice() - today.getAdjustedClosePrice());
        float yesterdayPriceDiff = Math.abs(yesterday.getOpenPrice() - yesterday.getAdjustedClosePrice());
        float dayBeforeYesterdayPriceDiff = Math.abs(dayBeforeYesterday.getOpenPrice() - dayBeforeYesterday.getAdjustedClosePrice());
        float todaysHigher = (today.getOpenPrice() + today.getAdjustedClosePrice() + todayPriceDiff) / 2;
        float todaysLower = (today.getOpenPrice() + today.getAdjustedClosePrice() - todayPriceDiff) / 2;
        float yesterdayHigher = (yesterday.getOpenPrice() + yesterday.getAdjustedClosePrice() + Math.abs(yesterdayPriceDiff)) / 2;
        float yesterdayLower = (yesterday.getOpenPrice() + yesterday.getAdjustedClosePrice() - Math.abs(yesterdayPriceDiff)) / 2;
        float dayBeforeYesterdayLower = (dayBeforeYesterday.getOpenPrice() + dayBeforeYesterday.getAdjustedClosePrice() - Math.abs(dayBeforeYesterdayPriceDiff)) / 2;

        // @8. Today is down and todays higher greater than yesterdsys higher and todays lower less than yesterdays lower
        if (todayPriceGap < 0 && todaysHigher > yesterdayHigher && todaysLower < yesterdayLower) {
            return true;
        }

        // @9. Today close price less than yesterday minimum and less than day before yesterday minimum
        if (today.getAdjustedClosePrice() < yesterdayLower && today.getAdjustedClosePrice() < dayBeforeYesterdayLower) {
            return true;
        }

        return false;
    }

    /**
     * @1. Sum of today and yesterday candle length should not be greater than
     * 4%
     * @2. Price should not go more than 5% less of higher (yesterday_higher,
     * day_before_yesterday_higher)
     * @3. VChange and tchange should be at least 0.8
     */
    private boolean isSellCandidate(Item calculatedItem, List<Item> items) {
        Item today = items.get(items.size() - 1);
        Item yesterday = items.get(items.size() - 2);
        Item dayBeforeYesterday = items.get(items.size() - 3);

        float yesterdayCandleLength = (Math.abs(yesterday.getOpenPrice() - yesterday.getAdjustedClosePrice()) / yesterday.getOpenPrice()) * 100;
        float todayCandleLength = (Math.abs(today.getOpenPrice() - today.getAdjustedClosePrice()) / today.getOpenPrice()) * 100;
        float yesterdayHigher = Math.max(yesterday.getOpenPrice(), yesterday.getAdjustedClosePrice());
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

    private boolean isBuySignal(Item calculatedItem, List<Item> items) {
        if (calculatedItem == null) {
            return false;
        }

        Item today = items.get(items.size() - 1);
        Item yesterday = items.get(items.size() - 2);
        Item dayBeforeYesterday = items.get(items.size() - 3);

        float twoMonthVolumeChange = calculatedItem.getVolumeChange();
        float oneWeekVolumeChange = calculateVolumeChange(items, TRADING_DAYS_IN_A_WEEK);
        float twoMonthTradeChange = calculatedItem.getTradeChange();
        float oneWeekTradeChange = calculateTradeChange(items, TRADING_DAYS_IN_A_WEEK);
        float todayPriceChangeWithRespectToClose = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
        float todayPriceChangeWithRespectToOpen = ((today.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
        float todayPriceChange = todayPriceChangeWithRespectToClose < todayPriceChangeWithRespectToOpen ? todayPriceChangeWithRespectToClose : todayPriceChangeWithRespectToOpen;
        float todayPriceGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
        float yesterdayPriceChange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
        float yesterdayPriceGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / today.getOpenPrice()) * 100;

        // @1. Today price change should be greater than equal 1% 
        if (!(todayPriceChange >= 1)) {
            return false;
        }

        //@Tail.
        if (isTailFoundYesterday(calculatedItem, items)) {
            return true;
        }

        boolean consecutiveGreen = false;
        if ((yesterdayPriceChange >= .5 || yesterdayPriceGap >= 1.0) && todayPriceGap >= 1.5) {
            consecutiveGreen = true;
        }

        // @11. One day gap greater than 7% or 2 day gap greater than 8% or 3 day gap greater than 8%
        float yesterdayMinimum = yesterday.getAdjustedClosePrice() < yesterday.getOpenPrice() ? yesterday.getAdjustedClosePrice() : yesterday.getOpenPrice();
        float dayBeforeYesterdayMinimum = dayBeforeYesterday.getAdjustedClosePrice() < dayBeforeYesterday.getOpenPrice() ? dayBeforeYesterday.getAdjustedClosePrice() : dayBeforeYesterday.getOpenPrice();
        float gapWithDayBeforeYesterdayMinimum = ((today.getAdjustedClosePrice() - dayBeforeYesterdayMinimum) / dayBeforeYesterdayMinimum) * 100;
        float gapWithYesterdayMinimum = ((today.getAdjustedClosePrice() - yesterdayMinimum) / yesterdayMinimum) * 100;

        // @2. Should have consecutive green or volume/trade change greater than 2 than year average and greater than 1.5 than last 7 days
        if (!(consecutiveGreen || ((twoMonthVolumeChange >= 2 && oneWeekVolumeChange >= 1.5) || (twoMonthTradeChange >= 2 && oneWeekTradeChange >= 1.5)) || gapWithDayBeforeYesterdayMinimum > 7 || gapWithYesterdayMinimum > 7 || todayPriceGap > 6)) {
            return false;
        }

        // @3. Today price gap should be greater than 1% of open price 
        if (!(todayPriceGap >= 1)) {
            return false;
        }

        // @4. Today close price greater than day_before_yesterday minimum
        if (!(today.getAdjustedClosePrice() > getMinimum(dayBeforeYesterday))) {
            return false;
        }

        // @5. Both vchange and tchange should be greater than 0.8
        if (!(twoMonthVolumeChange >= 0.8 && twoMonthTradeChange >= 0.8)) {
            return false;
        }

        // @6. Divergence should be less than equal 30 
        if (!(calculatedItem.getDivergence() <= 30)) {
            return false;
        }

        // @7. RSI should be less than equal 70
        if (!(calculatedItem.getRSI() <= 65)) {
            return false;
        }

        // @8. Trade should be at least 50
        if (!(today.getTrade() > 100)) {
            return false;
        }

        // @9. Value should be at least 2 million
        if (!(today.getValue() > 3)) {
            return false;
        }

        // @10. Pressure not less than -300
        if (calculatedItem.getPressure() != 0) {
            if (!(calculatedItem.getPressure() > -10)) {
                return false;
            }
        }

        //@12. Hammer should be greater than -2
        if (!(calculatedItem.getHammer() > -2)) {
            return false;
        }

        return true;
    }

    public boolean isTailFoundYesterday(Item calculatedItem, List<Item> items) {
        Item today = items.get(items.size() - 1);
        Item yesterday = items.get(items.size() - 2);
        Item dayBeforeYesterday = items.get(items.size() - 3);

        float difference = Math.abs(yesterday.getOpenPrice() - yesterday.getAdjustedClosePrice());
        //float largest = (yesterday.getOpenPrice() + yesterday.getAdjustedClosePrice() + difference) / 2;
        float smallest = (yesterday.getOpenPrice() + yesterday.getAdjustedClosePrice() - difference) / 2;
        float tail = ((smallest - yesterday.getDayLow()) / smallest) * 100;
        float todayPriceChange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
        float todayPriceGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;

        float yesterdayPriceChange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
        float yesterdayPriceGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;

        // @1. RSI should be less than 40
        if (!(calculatedItem.getRSI() < 40)) {
            return false;
        }

        // @2. Yesterday hammer should be greater than 1.5% 
        if (!(tail > 2)) {
            return false;
        }

        //@3. Divergence should be less 10
        if (!(calculatedItem.getDivergence() < 5)) {
            return false;
        }

        //@4. Today price gap should be greater than equal 1%
        if (!((todayPriceGap >= 1 && todayPriceChange >= 0.5))) {
            return false;
        }

        return true;
    }

    private float getMinimum(Item item) {
        return item.getAdjustedClosePrice() < item.getOpenPrice() ? item.getAdjustedClosePrice() : item.getOpenPrice();
    }

    public Item getItemByCode(List<Item> items, String code) {
        for (Item item : items) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    private void mergeItems(List<Item> mergeTo, List<Item> mergeWith, String property) {
        for (Item with : mergeWith) {
            for (Item to : mergeTo) {
                if (to.getCode().equals(with.getCode())) {
                    switch (property) {
                        case HAMMER:
                            to.setHammer(with.getHammer());
                            break;
                        case VOLUME_CHANGE:
                            to.setVolumeChange(with.getVolumeChange());
                            break;
                        case CANDLESTICK_LENGTH_CHANGE:
                            to.setcLengthChange(with.getcLengthChange());
                            break;
                        case CONSECUTIVE_GREEN:
                            to.setConsecutiveGreen(true);
                            break;
                        case TRADE_CHANGE:
                            to.setTradeChange(with.getTradeChange());
                            break;
                        case RSI:
                            to.setRSI(with.getRSI());
                            break;
                        case DIVERGENCE:
                            to.setDivergence(with.getDivergence());
                            break;
                        case SIGNAL:
                            to.setSignal(with.getSignal());
                            to.setPotentiality(with.isPotentiality());
                            break;
                        case VTC_SIGNAL:
                            to.setVtcSignal(with.getVtcSignal());
                            break;
                        case DIVIDENT_YIELD:
                            to.setDividentYield(with.getDividentYield());
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private Map<String, List<Item>> getOneYearItemizedData(List<Item> items) {
        CustomHashMap cMap = new CustomHashMap();
        for (Item item : items) {
            cMap.putItem(item);
        }
        return cMap;
    }

    private List<Item> getRSI(CustomHashMap cMap) throws SQLException, ClassNotFoundException {
        List<Item> distinctItems = new ArrayList<>();
        List<Item> items;
        for (String code : cMap.keySet()) {
            items = cMap.getItems(code);
            float rsi = calculateRSI(items);
            Item item = new Item();
            item.setCode(code);
            item.setRSI(Math.round(rsi));
            distinctItems.add(item);
        }

        return distinctItems;
    }

    /**
     * A-1 = A0 + A0( ((P-1/S) - P0 - D) / P0) where A0 is today's adjusted
     * price. A-1 is yesterday's adjusted price. P0 is today's actual price. P-1
     * is yesterday's actual price. S is the split ratio, if today is a split
     * ex-date. For example, a 3-to-2 split means S is 1.5. S is 1 if today is
     * not a split ex-date. D is the actual dividend, if today is a dividend
     * ex-date. D is 0 when not a dividend ex-date.
     *
     * @param items
     * @return
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    private List<Item> getDivergence(CustomHashMap cMap) throws SQLException, ClassNotFoundException {
        List<Item> distinctItems = new ArrayList<>();
        List<Item> items;
        for (String code : cMap.keySet()) {
            items = cMap.getItems(code);
            Collections.sort(items);

//            float EMA_26 = calculateEMA(items, 26, items.size());
//            float EMA_12 = calculateEMA(items, 12, items.size());
//            float EMA_9 = calculateSignalLineEMAWithDivergence(items, 9, items.size()).signalLineEMA;
            calculateDivergence(items);
            distinctItems.add(items.get(items.size() - 1));
            //System.out.println("code: " + code + ", 26_ema: " + EMA_26 + ", 12_ema: " + EMA_12 + ", MACD: " + (EMA_12-EMA_26) + ", EMA9: " + EMA_9 + ", divergence: " + items.get(items.size()-1).getDivergence());
        }

        return distinctItems;
    }

    public void calculateDivergence(List<Item> items) {
        float EMA_26 = calculateEMA(items, 26, items.size());
        float EMA_12 = calculateEMA(items, 12, items.size());
        float EMA_9 = calculateSignalLineEMAWithDivergence(items, 9, items.size()).signalLineEMA;
    }

    private SignalLineEMAWithMACDMax calculateSignalLineEMAWithDivergence(List<Item> items, int N, int index) {
        float k = 2 / ((float) N + 1);
        //System.out.println("code: " + items.get(index-1).getCode() + ", index: " + index + ", size: " + items.size() );
        if (index <= (N + 26) || items.size() < index) {
            int limit = (N + 26);
            if (items.size() < limit) {
                limit = items.size();
            }

            float SMA = 0;
            //System.out.println("index: " + index + ", limit: " + limit + ", N: " + N);
            for (int i = 26; i < limit; i++) {
                float ema12 = items.get(i).getEmaList().get(12);
                float ema26 = items.get(i).getEmaList().get(26);
                SMA += ema12 - ema26;
            }

            SMA = SMA / limit;
            //System.out.println("sma: " + SMA);
            return new SignalLineEMAWithMACDMax(SMA, 0);
        }

        //float prefix = items.get(index-1).getClosePrice() * k;
        //float postfix = (calculateEMA(items, N, index-1)*(1-k));
        //Formula #1
        //float EMA = prefix + postfix;
        //Formula #2
        SignalLineEMAWithMACDMax slemm = calculateSignalLineEMAWithDivergence(items, N, index - 1);
        float previousEMA = slemm.signalLineEMA;
        float ema12 = items.get(index - 1).getEmaList().get(12);
        float ema26 = items.get(index - 1).getEmaList().get(26);
        float MACD = ema12 - ema26;
        float MACD_abs = Math.abs(MACD);
        slemm.MACDMax = MACD_abs > slemm.MACDMax ? MACD_abs : slemm.MACDMax;
//        if(items.get(index-1).getCode().equals("ABBANK"))
//            System.out.println("index: " + index + ", emaDiff: " + emaDiff);
        float signalLineEMA = (MACD - previousEMA) * k + previousEMA;
        slemm.signalLineEMA = signalLineEMA;
        items.get(index - 1).getEmaList().put(N, signalLineEMA);

        //Calculate this only on last day
        if (index == items.size()) {
            float divergence = (MACD - signalLineEMA);
            //System.out.println("Code: " + items.get(items.size()-1).getCode() + ", ema12: " + ema12 + ", ema26: " + ema26 + "MACD: " + MACD + ", signalLineEMA: " + signalLineEMA + ", divergence: " + divergence);
//            if(items.get(index - 1).getCode().equals("ATLASBANG"))
//                System.out.println("macd: " + MACD + ", signalLineEMA: " + signalLineEMA + ", divergence: " + (MACD - signalLineEMA));
            float adjustedClosePrice = items.get(index - 1).getAdjustedClosePrice();
//            if(items.get(index-1).getCode().equals("GOLDENSON")){
//                System.out.println("divergence: " + divergence + ", signalLineEMA: " + signalLineEMA + ", adjustedClosePrice: " + adjustedClosePrice);
//            }
            if (signalLineEMA > 0) {
                divergence = divergence + signalLineEMA;
            } else if (signalLineEMA < 0) {
                divergence = divergence + signalLineEMA / 2;
            }
//            if(items.get(index-1).getCode().equals("GOLDENSON")){
//                System.out.println("updated divergence: " + divergence);
//            }
            divergence = (divergence / adjustedClosePrice) * 1000;
            items.get(index - 1).setDivergence(Math.round(divergence));
//            if (items.get(index - 1).getCode().equals("ENVOYTEX")) {
//                System.out.println("date: " + items.get(index - 1).getDate() + ", code: " + items.get(index - 1).getCode() + ", MACD: " + MACD + ", signalLineEMA: " + signalLineEMA + ", MACD_MAX: " + slemm.MACDMax + ", divergence: " + divergence);
//            }
        }

        return slemm;
    }

//    private List<Item> getUpDayItems(List<Item> items) {
//        List<Item> updays = new ArrayList<>();
//        for (Item item : items) {
//            float diff = item.getClosePrice() - item.getYesterdayClosePrice();
//            if (diff > 0) {
//                updays.add(item);
//            }
//        }
//        return updays;
//    }
//    private List<Item> getDownDayItems(List<Item> items) {
//        List<Item> downdays = new ArrayList<>();
//        for (Item item : items) {
//            float diff = item.getClosePrice() - item.getYesterdayClosePrice();
//            if (diff < 0) {
//                downdays.add(item);
//            }
//        }
//        return downdays;
//    }
    public float calculateSMA(List<Item> items, int N) {
        int limit = N;
        if (items.size() < limit) {
            limit = items.size();
        }

        float SMA = 0;
        //System.out.println("index: " + index + ", limit: " + limit + ", N: " + N);
        for (int i = items.size() - 1; i >= (items.size() - limit); i--) {
            SMA += items.get(i).getAdjustedClosePrice();
        }
        SMA = SMA / limit;

        Item today = items.get(items.size() - 1);
        //System.out.println(today.getDate() + ", SMA: " + SMA + ", close: " + today.getAdjustedClosePrice());
        return SMA;
    }

    private float calculateEMA(List<Item> items, int N, int index) {
        float k = 2 / ((float) N + 1);
        //System.out.println("code: " + items.get(index-1).getCode() + ", index: " + index + ", size: " + items.size() );
        if (index <= N || items.size() < index) {
            int limit = N;
            if (items.size() < limit) {
                limit = items.size();
            }

            float SMA = 0;
            //System.out.println("index: " + index + ", limit: " + limit + ", N: " + N);
            for (int i = 0; i < limit; i++) {
                SMA += items.get(i).getAdjustedClosePrice();
            }
            SMA = SMA / limit;
            //System.out.println("sma: " + SMA);
            return SMA;
        }

        //float prefix = items.get(index-1).getClosePrice() * k;
        //float postfix = (calculateEMA(items, N, index-1)*(1-k));
        //Formula #1
        //float EMA = prefix + postfix;
        //Formula #2
        float previousEMA = calculateEMA(items, N, index - 1);
        float EMA = (items.get(index - 1).getAdjustedClosePrice() - previousEMA) * k + previousEMA;
        items.get(index - 1).getEmaList().put(N, EMA);

        //System.out.println("EMA for index " + index + ": " + EMA + ", closepricewas: " + items.get(index-1).getClosePrice() + ", N: " + N + ", k: " + k);
        return EMA;
    }

    public float calculateRSI(List<Item> items) {
        Collections.sort(items);

        float rs = getRS(items);
        float rsi = 100 - (100 / (1 + rs));
        if (rsi == 0) {
            //System.out.println("rs: " + rs + ", code: " + items.get(items.size()-1).getCode());
        }

        return rsi;
    }

    private float getRS(List<Item> items) {
        float averageGain = getAverageGain(items, items.size());
        float averageLoss = getAverageLoss(items, items.size());

        return averageGain / averageLoss;
    }

    private float getAverageGain(List<Item> items, int index) {
        float averageGain;

        if (index <= (RSI_PERIOD + 1)) {
            float totalGain = 0;

            int limit = RSI_PERIOD;
            if (items.size() <= limit) {
                limit = items.size() - 1;
            }

            for (int i = 1; i <= limit; i++) {
                Item item = items.get(i);
                totalGain += ((item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice()) + Math.abs(item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice())) / 2;
            }

            averageGain = totalGain / (float)RSI_PERIOD;
            return averageGain;
        }

        float previousAverageGain = getAverageGain(items, index - 1);
        Item item = items.get(index - 1);
        float todayGain = ((item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice()) + Math.abs(item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice())) / 2;
        averageGain = (previousAverageGain * 13 + todayGain) / (float)RSI_PERIOD;
        return averageGain;
    }

    private float getAverageLoss(List<Item> items, int index) {
        float averageLoss;

        if (index <= (RSI_PERIOD + 1)) {
            float totalLoss = 0;

            int limit = RSI_PERIOD;
            if (items.size() <= limit) {
                limit = items.size() - 1;
            }

            for (int i = 1; i <= limit; i++) {
                Item item = items.get(i);
                totalLoss += (Math.abs(item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice()) - (item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice())) / 2;
            }

            averageLoss = totalLoss / (float)RSI_PERIOD;
            return averageLoss;
        }

        float previousAverageLoss = getAverageLoss(items, index - 1);
        Item item = items.get(index - 1);
        float todayLoss = (Math.abs(item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice()) - (item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice())) / 2;
        averageLoss = (previousAverageLoss * 13 + todayLoss) / (float)RSI_PERIOD;

        return averageLoss;
    }

    private List<Item> getVolumeChange(CustomHashMap cMap) {
        List<Item> distinctItems = new ArrayList<>();
        List<Item> items;
        for (String code : cMap.keySet()) {
            items = cMap.getItems(code);
            Collections.sort(items);
            Item item = new Item();
            item.setCode(code);
            item.setVolumeChange(calculateVolumeChange(items, TRADING_DAYS_IN_A_MONTH));
            distinctItems.add(item);
        }

        return distinctItems;
    }

    private List<Item> getTradeChange(CustomHashMap cMap) {
        List<Item> distinctItems = new ArrayList<>();
        List<Item> items;
        for (String code : cMap.keySet()) {
            items = cMap.getItems(code);
            Collections.sort(items);
            Item item = new Item();
            item.setCode(code);
            item.setTradeChange(calculateTradeChange(items, TRADING_DAYS_IN_A_MONTH));
            distinctItems.add(item);
        }

        return distinctItems;
    }

//    private float calculateVolumeChange(List<Item> items) {
//        if (items.size() == 1) {
//            return 1;
//        }
//
//        long totalVolume = 0;
//        for (int i = 0; i < items.size() - 1; i++) {
//            totalVolume += items.get(i).getAdjustedVolume();
//        }
//        float avgVolume = Math.round(totalVolume / (items.size() - 1));
//        //System.out.println("code: " + items.get(0).getCode() + ", totalVolume: " + totalVolume + ", avgVolume: " + avgVolume);
//        float ratio = (items.get(items.size() - 1).getAdjustedVolume()) / avgVolume;
//        DecimalFormat df = new DecimalFormat("#.#");
//        String ratioString = df.format(ratio);
//        return Float.parseFloat(ratioString);
//    }
    public float calculateVolumeChange(List<Item> items, int days) {
        if (items.size() == 1) {
            return 1;
        }

        if (days <= 0) {
            days = TRADING_DAYS_IN_A_MONTH;     //Trading days in a year
        }
        long totalVolume = 0;
        int count = 0;
        Item firstDay = null;
        for (int i = items.size() - 2; (i >= 0 && (items.size() - i) < (days + 2)); i--) {
            totalVolume += items.get(i).getAdjustedVolume();
            ++count;
            firstDay = items.get(i);
        }
        float avgVolume = (float)totalVolume / (float)count;
        //System.out.println("code: " + items.get(0).getCode() + ", totalVolume: " + totalVolume + ", avgVolume: " + avgVolume);
        float ratio = ((float)items.get(items.size() - 1).getAdjustedVolume()) / avgVolume;

        Item today = items.get(items.size() - 1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(today.getDate());

        //if(items.get(items.size()-1).getCode().equals("WATACHEM") )
        //    System.out.println("Code: " + today.getCode() + ", date: " + today.getDate() + ", totalVolume: " + totalVolume + ", count: " + count + "avg: " + avgVolume + ", current: " + items.get(items.size() - 1).getAdjustedVolume() + ", ratio: " + ratio);
//        DecimalFormat df = new DecimalFormat("#.#");
//        String ratioString = df.format(ratio);
//        return Float.parseFloat(ratioString);
        //System.out.println("lastday: " + items.get(items.size()-1).getDate() + ", v-change: " + ratio + ", days: " + days + ", firstDay: " + firstDay.getDate() + ", todayvolume: " + items.get(items.size() - 1).getAdjustedVolume() + ", avg: " + avgVolume);
        return ratio;
    }

    public void calculateVolumePerTradeChange(List<Item> items, int days) {
        for (int i = days; i < items.size(); i++) {

            long totalVolume = 0;
            int totalTrade = 0;
            Item start = items.get(i - 1);
            Item end = new Item();
            for (int j = i - 1; (i - j) <= days; j--) {
                items.get(j);
                totalVolume += items.get(j).getAdjustedVolume();
                totalTrade += items.get(j).getTrade();
                end = items.get(j);
            }

            Item today = items.get(i);
            float averageVolumePerTrade = ((float) totalVolume / (float) totalTrade);
            float todayVolumePerTrade = (float) today.getAdjustedVolume() / (float) today.getTrade();
            float todayVolumePerTradeChange = todayVolumePerTrade / averageVolumePerTrade;
            today.setVolumePerTradeChange(todayVolumePerTradeChange);
            //System.out.println("today: " + today.getDate() + ", start: " + start.getDate() + ", end: " + end.getDate());
        }
    }

    public Item getMaximumVolumePerTradeChange(List<Item> items, int head, int days) {
        if (items.size() <= days || head <= days) {
            return new Item();
        }

        Item maximum = new Item();
        for (int i = head; (head - i) < days; i--) {
            if (items.get(i).getVolumePerTradeChange() > maximum.getVolumePerTradeChange()) {
                maximum = items.get(i);
            }
        }
        return maximum;
    }

    public float getPriceDiffWithPreviousLow(List<Item> items, int days) {
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

    public float getVolumeDiffWithPreviousHigh(List<Item> items, int days) {
        int size = items.size();
        int counter = 0;
        int maximum = 0;

        for (int i = size - 2; i >= 0; i--) {
            int volume = items.get(i).getAdjustedVolume();
            if (volume > maximum) {
                maximum = volume;
                //System.out.println("maximum: " + maximum + ", date: " + items.get(i).getDate());
            }
            ++counter;
            if (counter >= days) {
                break;
            }
        }

        int lastDayVolume = items.get(size - 1).getAdjustedVolume();
        float diff = (float) lastDayVolume / (float) maximum;
        //System.out.println("lastDayVolume: " + lastDayVolume + ", maximum: " + maximum);
        return diff;
    }

    public float getLastFiewDaysVariation(List<Item> items, int days) {
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

    public float getLastFiewDaysMaximum(List<Item> items, int days) {
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

    public float getLastFiewDaysMinimum(List<Item> items, int days) {
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

    public boolean isConsecutive3DaysGreen(List<Item> items) {

        int size = items.size();
        if (size < 4) {
            return false;
        }

        Item today = items.get(size - 1);
        Item yesterday = items.get(size - 2);
        Item dayBeforeYesterday = items.get(size - 3);
        Item towDayBeforeYesterday = items.get(size - 4);

        float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
        float todayChange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
        float yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
        float yesterdayChange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
        float dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;
        float dayBeforeYesterdayChange = ((dayBeforeYesterday.getAdjustedClosePrice() - towDayBeforeYesterday.getAdjustedClosePrice()) / towDayBeforeYesterday.getAdjustedClosePrice()) * 100;

        //if(today.getCode().equals("PRIMELIFE") )
        //    System.out.println("todayGap: " + todayGap + ", todayChange: " + todayChange + ", yesterdayGap: " + yesterdayGap + ", yesterdayChange: " + yesterdayChange + ", dayBeforeYesterdayGap: " + dayBeforeYesterdayGap + ", dayBeforeYesterdayChange: " + dayBeforeYesterdayChange + ", date: " + today.getDate() + ", dayofmonth: " + cal.get(Calendar.DAY_OF_MONTH));
        if ((todayGap >= 0.5 && todayChange >= 0.5) && (yesterdayGap >= 0.5 && yesterdayChange >= 0.5) && (dayBeforeYesterdayGap >= 0.5 && dayBeforeYesterdayChange > 0.5)) {
            return true;
        }

        return false;
    }

    public int getUpDayCount(List<Item> items, int head, int totalDay) {

        int size = items.size();
        if (size < (totalDay + 1) || head < (totalDay + 1)) {
            return 0;
        }

        int updaysCount = 0;
        for (int i = head; (head - i) < totalDay; i--) {
            Item today = items.get(i);
            Item yesterday = items.get(i - 1);
            if (today.getAdjustedClosePrice() > yesterday.getAdjustedClosePrice()) {
                ++updaysCount;
            }
        }

        return updaysCount;
    }

    public float calculateTradeChange(List<Item> items, int head, int days) {
        List<Item> itemSubList = new ArrayList();
        for (int i = head; (head - i) <= days + 1 && i >= 0; i--) {
            itemSubList.add(items.get(i));
        }

        Collections.sort(itemSubList);

        return calculateTradeChange(itemSubList, days);
    }

    public float calculateTradeChange(List<Item> items, int days) {
        if (items.size() == 1) {
            return 1;
        }

        if (days <= 0) {
            days = TRADING_DAYS_IN_A_YEAR;     //Trading days in a year
        }
        long totalTrade = 0;
        int count = 0;
        for (int i = items.size() - 2; (i >= 0 && (items.size() - i) < (days + 2)); i--) {
            totalTrade += items.get(i).getTrade();
            ++count;
        }
        float avgTrade = Math.round(totalTrade / count);
        //System.out.println("code: " + items.get(0).getCode() + ", totalVolume: " + totalVolume + ", avgVolume: " + avgVolume);
        float ratio = (items.get(items.size() - 1).getTrade()) / avgTrade;
        //DecimalFormat df = new DecimalFormat("#.#");
        //String ratioString = df.format(ratio);
        //System.out.println("code: " + items.get(items.size()-1).getCode() + ", totalTrade: " + totalTrade + ", avgTrade: " + avgTrade + ", ratio: " + ratio + ", ratioString: " + ratioString);
        return ratio;
    }

    private class SignalLineEMAWithMACDMax {

        public SignalLineEMAWithMACDMax(float signalLineEMA, float MACDMax) {
            this.signalLineEMA = signalLineEMA;
            this.MACDMax = MACDMax;
        }
        float signalLineEMA;
        float MACDMax;
    }
}
