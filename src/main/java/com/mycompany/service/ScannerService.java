package com.mycompany.service;

import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.model.ChangesInVolumePerTrade;
import com.mycompany.model.Item;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    public static final int RSI_PERIOD = 14;
    public static final int TRADING_DAYS_IN_A_YEAR = 250;
    public static final int TRADING_DAYS_IN_2_MONTH = 44;
    public static final int TRADING_DAYS_IN_A_WEEK = 5;
    public static final int DAYS_IN_A_YEAR = 365;
    //private static float MACD_MAX = 0;

    public ScannerService() {
        dao = new ItemDaoImpl();
    }

    public List<Item> getItemsWithscannedProperties() throws SQLException, ClassNotFoundException {
        Calendar start = Calendar.getInstance();
        dao.open();
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
        mergeItems(items, getSignal(items, dataArchive), SIGNAL);
        //System.out.println("up to volume change time elapsed " + (Calendar.getInstance().getTimeInMillis()-start.getTimeInMillis())/1000 + " seconds");
        mergeItems(items, getVolumePerTradeChangeBasedSignal(items, dataArchive), VTC_SIGNAL);

        dao.close();
        return items;
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
            List<Item> items = oneYearData.getItems(code);
            Collections.sort(items);
            Item item = new Item();
            item.setCode(code);
            Item today = items.get(items.size() - 1);
            float candleLength = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
            String candleLengthString = df.format(candleLength);
            candleLength = Float.parseFloat(candleLengthString);
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
        
        ChangesInVolumePerTrade changes = ChangesInVolumePerTrade.getChangesInVolumePerTrade(items, ScannerService.TRADING_DAYS_IN_2_MONTH);
        float previousYesterdayVolumePerTradeChange = changes.getDayBeforeYesterdayChange();
        float yesterdayVolumePerTradeChange = changes.getYesterdayChange();
        float todayVolumePerTradeChange = changes.getTodayChange();       
        
        if ((todaychange > 0.5 && todayGap >= 0)
                && !(yesterdayVolumePerTradeChange > 1 && previousYesterdayVolumePerTradeChange > 1)
                && ((todayVolumePerTradeChange > 1.5
                && (vtcRatioToday > 0.95 && (vtcRatioToday - vtcRatioYesterday) > -0.3)
                && (volumeChange > 2 && tradeChange >= 1.6)
                && (volumeChangeWithYesterday > 1.5))
                )
                && diffWithPreviousLow < 10
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
        float largest = (yesterday.getOpenPrice() + yesterday.getAdjustedClosePrice() + difference) / 2;
        float smallest = (yesterday.getOpenPrice() + yesterday.getAdjustedClosePrice() - difference) / 2;
        float tail = ((smallest - yesterday.getLow()) / smallest) * 100;
        float todayPriceChange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
        float todayPriceGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;

        // @1. RSI should be less than 40
        if (!(calculatedItem.getRSI() < 40)) {
            return false;
        }

        // @2. Yesterday hammer should be greater than 1.5% 
        if (!(tail > 1.5)) {
            return false;
        }

        //@3. Divergence should be less 10
        if (!(calculatedItem.getDivergence() < 10)) {
            return false;
        }

        //@4. Today price gap should be greater than equal 1%
        if (!(todayPriceGap > 1)) {
            return false;
        }

        return true;
    }

    private float getMinimum(Item item) {
        return item.getAdjustedClosePrice() < item.getOpenPrice() ? item.getAdjustedClosePrice() : item.getOpenPrice();
    }

    private Item getItemByCode(List<Item> items, String code) {
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
                            break;
                        case VTC_SIGNAL:
                            to.setVtcSignal(with.getVtcSignal());
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

            float EMA_26 = calculateEMA(items, 26, items.size());
            float EMA_12 = calculateEMA(items, 12, items.size());
            float EMA_9 = calculateSignalLineEMAWithDivergence(items, 9, items.size()).signalLineEMA;
            distinctItems.add(items.get(items.size() - 1));
            //System.out.println("code: " + code + ", 26_ema: " + EMA_26 + ", 12_ema: " + EMA_12 + ", MACD: " + (EMA_12-EMA_26) + ", EMA9: " + EMA_9 + ", divergence: " + items.get(items.size()-1).getDivergence());
        }

        return distinctItems;
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

    private List<Item> getUpDayItems(List<Item> items) {
        List<Item> updays = new ArrayList<>();
        for (Item item : items) {
            float diff = item.getClosePrice() - item.getYesterdayClosePrice();
            if (diff > 0) {
                updays.add(item);
            }
        }
        return updays;
    }

    private List<Item> getDownDayItems(List<Item> items) {
        List<Item> downdays = new ArrayList<>();
        for (Item item : items) {
            float diff = item.getClosePrice() - item.getYesterdayClosePrice();
            if (diff < 0) {
                downdays.add(item);
            }
        }
        return downdays;
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

            averageGain = totalGain / RSI_PERIOD;
            return averageGain;
        }

        float previousAverageGain = getAverageGain(items, index - 1);
        Item item = items.get(index - 1);
        float todayGain = ((item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice()) + Math.abs(item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice())) / 2;
        averageGain = (previousAverageGain * 13 + todayGain) / RSI_PERIOD;
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

            averageLoss = totalLoss / RSI_PERIOD;
            return averageLoss;
        }

        float previousAverageLoss = getAverageLoss(items, index - 1);
        Item item = items.get(index - 1);
        float todayLoss = (Math.abs(item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice()) - (item.getAdjustedClosePrice() - item.getAdjustedYesterdayClosePrice())) / 2;
        averageLoss = (previousAverageLoss * 13 + todayLoss) / RSI_PERIOD;

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
            item.setVolumeChange(calculateVolumeChange(items, TRADING_DAYS_IN_2_MONTH));
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
            item.setTradeChange(calculateTradeChange(items, TRADING_DAYS_IN_2_MONTH));
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
            days = TRADING_DAYS_IN_2_MONTH;     //Trading days in a year
        }
        long totalVolume = 0;
        int count = 0;
        for (int i = items.size() - 2; (i >= 0 && (items.size() - i) < (days + 2)); i--) {
            totalVolume += items.get(i).getAdjustedVolume();
            ++count;
        }
        float avgVolume = Math.round(totalVolume / count);
        //System.out.println("code: " + items.get(0).getCode() + ", totalVolume: " + totalVolume + ", avgVolume: " + avgVolume);
        float ratio = (items.get(items.size() - 1).getAdjustedVolume()) / avgVolume;
        //System.out.println("totalVolume: " + totalVolume + ", count: " + count + "avg: " + avgVolume + ", current: " + items.get(items.size() - 1).getAdjustedVolume());
//        DecimalFormat df = new DecimalFormat("#.#");
//        String ratioString = df.format(ratio);
//        return Float.parseFloat(ratioString);
        return ratio;
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
