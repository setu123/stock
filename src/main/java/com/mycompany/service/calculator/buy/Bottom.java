/*
 * summery: 344.4604, totalBuy: 76, totalSell: 76, profitRate: 4.5323734, profit:loss= 68 : 8 gainPercent: 89.5, averageTenure: 65.8421
 */
package com.mycompany.service.calculator.buy;

import com.mycompany.model.Item;
import com.mycompany.model.Portfolio;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ScannerService;
import com.mycompany.service.calculator.SignalCalculator;
import static com.mycompany.service.calculator.SignalCalculator.averageValuePerTrade;
import static com.mycompany.service.calculator.SignalCalculator.maxPriceDay;
import static com.mycompany.service.calculator.SignalCalculator.sma25;
import static com.mycompany.service.calculator.SignalCalculator.today;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @date Dec 17, 2015
 * @author setu
 */
public class Bottom extends BuySignalCalculator {

    public Bottom(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        //intializeVariables(itemSubList, calculated);
        float change = (today.getOpenPrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice();
        float todayValuePerTrade = today.getValue() / today.getTrade();
        float ratio = todayValuePerTrade / averageValuePerTrade;
        float oneThirdWay = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 1.5f + today.getOpenPrice();
        float oneFourthWay = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 1.1f + today.getOpenPrice();
        float yesterdayMax = Math.max(yesterday.getOpenPrice(), yesterday.getAdjustedClosePrice());
        float gap = today.getOpenPrice() - yesterdayMax;
        gap = (gap / yesterdayMax) * 100;

        float twoWeeksAgoSma25 = calculateBackdatedSMA(itemSubList, 25, ScannerService.TRADING_DAYS_IN_A_WEEK * 2);
        float OneAndHalfMonthAgoSma25 = calculateBackdatedSMA(itemSubList, 25, ScannerService.TRADING_DAYS_IN_A_MONTH + ScannerService.TRADING_DAYS_IN_A_WEEK);
        float twoMonthAgoSma25 = calculateBackdatedSMA(itemSubList, 25, ScannerService.TRADING_DAYS_IN_A_MONTH * 2);
        
        float sma25DiffFor_0_2 = ((sma25 - twoWeeksAgoSma25) / twoWeeksAgoSma25) * 100;
        float sma25DiffFor_2_4 = ((twoWeeksAgoSma25 - oneMonthAgoSma25) / oneMonthAgoSma25) * 100;
        float sma25DiffFor_4_6 = ((oneMonthAgoSma25 - OneAndHalfMonthAgoSma25) / OneAndHalfMonthAgoSma25) * 100;
        float sma25DiffFor_6_8 = ((OneAndHalfMonthAgoSma25 - twoMonthAgoSma25) / twoMonthAgoSma25) * 100;
        float sma25DiffFor_0_8 = ((sma25 - twoMonthAgoSma25) / twoMonthAgoSma25) * 100;
        float sma25DiffFor_0_4 = ((sma25 - oneMonthAgoSma25) / oneMonthAgoSma25) * 100;
        float sma25DiffFor_4_8 = ((oneMonthAgoSma25 - twoMonthAgoSma25) / twoMonthAgoSma25) * 100;

        float oneWeekAgoSma10 = fourDayBeforeYesterday.getSmaList().get(10);
        float oneWeekAgoSma10Diff = Math.abs(((SignalCalculator.sma10 - oneWeekAgoSma10) / oneWeekAgoSma10) * 100);

        float monthAgoSma10 = monthBefore.getSmaList().get(10);
        float monthAgoSma10Diff = Math.abs(((SignalCalculator.sma10 - monthAgoSma10) / monthAgoSma10) * 100);

        boolean gapLogic = gap > 0.7 ? todayGap >= 4 : true;
        float plainGap = todayClosePrice - today.getOpenPrice();
        float oneWeekAgoEma9 = fourDayBeforeYesterday.getEmaList().get(9);
        float todayEma9 = today.getEmaList().get(9);
        float macdPercentage = (macd / todayClosePrice) * 100;
        boolean emaPass = todayEma9 <= 0 ? true : todayEma9 >= oneWeekAgoEma9;
        float straightGap = Math.max(today.getOpenPrice(), today.getAdjustedClosePrice()) - Math.min(today.getOpenPrice(), today.getAdjustedClosePrice());
        float tails = ((today.getDayHigh() - today.getDayLow()) - straightGap);
        boolean isShaky = tails > straightGap;
        float topTail = ((today.getDayHigh() - Math.max(today.getOpenPrice(), today.getAdjustedClosePrice())) / Math.max(today.getOpenPrice(), today.getAdjustedClosePrice())) * 100;
        float bottomTail = ((Math.min(today.getOpenPrice(), today.getAdjustedClosePrice()) - today.getDayLow()) / Math.min(today.getOpenPrice(), today.getAdjustedClosePrice())) * 100;
        float maxPriceDayMax = Math.max(maxPriceDay.getOpenPrice(), maxPriceDay.getAdjustedClosePrice());
        float changeWithMaxPriceDay = ((todayClosePrice - maxPriceDayMax) / maxPriceDayMax) * 100;
        float todaysmaDiff = ((sma25 - sma10) / sma10) * 100;
        float dsexSma25 = dsex.getSmaList().get(25);
        float tolarity = 5;
//        float dsexDown = ((dsex.getDayHigh()-dsex.getAdjustedClosePrice())/dsex.getDayHigh())*100;
//        float dsexMax = Math.max(dsex.getOpenPrice(), dsex.getAdjustedClosePrice());
        float todayDsexTopTail = ((dsex.getDayHigh() - Math.max(dsex.getOpenPrice(), dsex.getAdjustedClosePrice())) / Math.max(dsex.getOpenPrice(), dsex.getAdjustedClosePrice())) * 100;
        float yesterdayDsexTopTail = ((dsexYesterday.getDayHigh() - Math.max(dsexYesterday.getOpenPrice(), dsexYesterday.getAdjustedClosePrice())) / Math.max(dsexYesterday.getOpenPrice(), dsexYesterday.getAdjustedClosePrice())) * 100;
        float dayBeforeDsexTopTail = ((dsexDayBefore.getDayHigh() - Math.max(dsexDayBefore.getOpenPrice(), dsexDayBefore.getAdjustedClosePrice())) / Math.max(dsexDayBefore.getOpenPrice(), dsexDayBefore.getAdjustedClosePrice())) * 100;

        float changeWithBottom = 100;
        //Item bottom = getBottom(itemSubList);
        Item bottom = getLowest(itemSubList);
        //long intervalFromBottom = getDateDiff(bottom.getDate(), today.getDate(), TimeUnit.DAYS);
        boolean similarPriceBefore = similarPriceBefore(itemSubList);
        
        int publicSecurity = (int) ((today.getTotalSecurity() * today.getSharePercentage().getPublics())/100);
        float publicShareAmount = publicSecurity * today.getAdjustedClosePrice();
        
//        int publicSecurity = (int) ((today.getTotalSecurity() * today.getSharePercentage().getPublics())/100);
//        float publicShareAmount = publicSecurity * today.getAdjustedClosePrice();
        
        if (bottom != null) {
            changeWithBottom = ((today.getAdjustedClosePrice() - bottom.getAdjustedClosePrice()) / bottom.getAdjustedClosePrice()) * 100;
//            System.out.println("Date: " + today.getDate() + ", bottomDay: " + bottom.getDate() + ", changeWithBottom: " + changeWithBottom + ", similarPriceBefore: " + similarPriceBefore + ", isBullTrap: " + isBullTrap);
//            System.out.println("today: " + today.getDate());
        }

        if ( //changeWithBottom>=-2 && changeWithBottom<=2
                changeWithBottom < bottomTolerationPercent 
                && todayGap > 0.70 
                && yesterdayGap > 0
//                && vChange > 2
//                && intervalFromBottom<90
                && similarPriceBefore
                && divergence < maxDivergence
                && rsi >= (35 + todaychange)
                && ((today.getPaidUpCapital()<600 || publicShareAmount<600000000) || today.getSharePercentage().getGovernment()>10)
//                && today.getPaidUpCapital() < 300
//                && publicShareAmount < 300000000
//                && diffWithPreviousLow10 <= 10
                ) {
            setCause(this.getClass().getName());

            boolean maskPassed = isMaskPassed(today, portfolio);
            if (maskPassed) {
//                System.out.print(", Bottom day: " + bottom.getDate() + ", today: " + today.getDate() + ", interval: " + intervalFromBottom);
            }
            return maskPassed;
        }
        return false;
    }
    
    private boolean similarPriceBefore(List<Item> itemSubList){
        int size = itemSubList.size();
        for(int i=size-ScannerService.TRADING_DAYS_IN_A_WEEK*3; i>size-ScannerService.TRADING_DAYS_IN_A_MONTH*2&&i>=0; i--){
            Item anItem = itemSubList.get(i);
            float priceDiff = ((anItem.getAdjustedClosePrice() - today.getAdjustedClosePrice())/today.getAdjustedClosePrice())*100;
            if(priceDiff>=-bottomTolerationPercent && priceDiff<=bottomTolerationPercent){
//                System.out.println("today: " + today.getDate() + ", anItem: " + anItem.getDate() + ", priceDiff: " + priceDiff);
                return true;
            }
        }
        return false;
    }

    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private Item getLowest(List<Item> items) {
        Item minimum = new Item();
        minimum.setAdjustedClosePrice(1000000);
//        System.out.println("finidng minimum on " + today.getDate() + ", itemSize: " + items.size() + ", top item: " + items.get(items.size()-1).getDate());
        Calendar oneYearBack = Calendar.getInstance();
        oneYearBack.add(Calendar.YEAR, -1);
        for (int i = items.size() - 1; i >= 0 && oneYearBack.getTime().before(items.get(i).getDate()); i--) {
            if (items.get(i).getAdjustedClosePrice() < minimum.getAdjustedClosePrice()) {
//                System.out.println("minimum is: " + items.get(i).getAdjustedClosePrice() + ", date: " + items.get(i).getDate());
                minimum = items.get(i);
            }
        }
        return minimum;
    }

    private Item getBottom(List<Item> items) {
        if (items.size() <= ScannerService.TRADING_DAYS_IN_A_MONTH * 4) {
            return null;
        }

        for (int i = items.size() - ScannerService.TRADING_DAYS_IN_A_MONTH * 2; i > ScannerService.TRADING_DAYS_IN_A_MONTH * 2; i--) {
//            System.out.println("---------------------------");
            if (isBottom(items, i)) {
//                System.out.println("Date: " + items.get(i).getDate() + ", code: " + today.getCode() + ", testedFrom: " + items.get(items.size()-ScannerService.TRADING_DAYS_IN_A_MONTH*2).getDate());
                return items.get(i);
            }
        }

        return null;
    }

    private boolean isBottom(List<Item> items, int index) {
        boolean isLowestInNext = isLowestInNext(items, index, ScannerService.TRADING_DAYS_IN_A_MONTH * 2);
//        System.out.println("Date: " + items.get(index).getDate() + ", code: " + today.getCode() + ", isLowestInNext: " + isLowestInNext);
        if (!isLowestInNext) {
            return false;
        }

        boolean hasAdvancedIn = hasAdvancedIn(items, index, 20, ScannerService.TRADING_DAYS_IN_A_MONTH * 2);
//        System.out.println("Date: " + items.get(index).getDate() + ", code: " + today.getCode() + ", hasAdvancedIn: " + hasAdvancedIn);
        if (!hasAdvancedIn) {
            return false;
        }

        boolean isLowestIn = isLowestInLast(items, index, ScannerService.TRADING_DAYS_IN_A_MONTH * 2);
//        System.out.println("Date: " + items.get(index).getDate() + ", code: " + today.getCode() + ", isLowestIn: " + isLowestIn);
        return isLowestIn;
    }

    private boolean isLowestInNext(List<Item> items, int index, int days) {
        for (int i = index + 1; i < index + days; i++) {
            if (items.get(i).getAdjustedClosePrice() < items.get(index).getAdjustedClosePrice()) {
                return false;
            }
        }

        return true;
    }

    private boolean hasAdvancedIn(List<Item> items, int index, int advancePercentage, int days) {
        Item baseItem = items.get(index);
        for (int i = index + 1; i < index + days; i++) {
            Item currentItem = items.get(i);
            float gain = ((currentItem.getAdjustedClosePrice() - baseItem.getAdjustedClosePrice()) / baseItem.getAdjustedClosePrice()) * 100;

            if (gain >= 20) {
                return true;
            }
        }

        return false;
    }

    private boolean isLowestInLast(List<Item> items, int index, int days) {
        for (int i = index - 1; i > index - days; i--) {
            if (items.get(i).getAdjustedClosePrice() < items.get(index).getAdjustedClosePrice()) {
                return false;
            }
        }

        return true;
    }

}
