/*
 * summery: 344.4604, totalBuy: 76, totalSell: 76, profitRate: 4.5323734, profit:loss= 68 : 8 gainPercent: 89.5, averageTenure: 65.8421
 */
package com.mycompany.service.calculator.buy;

import com.mycompany.model.Item;
import com.mycompany.model.Portfolio;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ScannerService;
import static com.mycompany.service.calculator.SignalCalculator.sma25;
import static com.mycompany.service.calculator.SignalCalculator.today;
import static com.mycompany.service.calculator.SignalCalculator.vChange;
import java.util.List;

/**
 * @date Dec 17, 2015
 * @author setu
 */
public class AroundSma25 extends BuySignalCalculator {

    public AroundSma25(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        float oneFourthWay = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 1.1f + today.getOpenPrice();
 
        int publicSecurity = (int) ((today.getTotalSecurity() * today.getSharePercentage().getPublics()) / 100);
        float publicShareAmount = publicSecurity * today.getAdjustedClosePrice();

        int aroundSMA25Count = getAroundSMA25Count(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);

        if ((todayGap >= 1 && oneFourthWay > sma25 && today.getOpenPrice() <= (sma25 * 1.015))
                && divergence <= maxDivergence
                && diffWithPreviousLow10 <= 10 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                && upperTail < 4
                //                && vChange > 2
                //                && priceDiffWithMinimum <=10
                && todayClosePrice > lastMonthMaximum
                //                && today.getEmaList().get(9) < 0
                //                && yesterdayGap > 0
                //                && changeWithBottom <= 10
                //                && vChange > 1
                && vChange <= 5
                && todaychange <= 5
                && oneMonthBackSma25Change > 0
                && today.getTrade() >= 50
                //                && overSMA25Count >= ScannerService.TRADING_DAYS_IN_A_WEEK*3
                && aroundSMA25Count >= ScannerService.TRADING_DAYS_IN_A_WEEK * 3
                //                && today.getAdjustedClosePrice() < 500
                //                && (today.getReserve()/today.getPaidUpCapital()) < 1
                //                && today.getSharePercentage().getGovernment() >= 50
                //                && today.getSharePercentage().getPublics()<30
                //                && averagePriceOnLastFewDays > 0
                && (publicShareAmount < 900000000
                || today.getPaidUpCapital() < 900) //                && today.getPE() > 40
                ) {
            setCause(this.getClass().getName());
            boolean maskPassed = isMaskPassed(today, portfolio);
            return maskPassed;
        }
        return false;
    }

    private int getAroundSMA25Count(List<Item> itemSubList, int days) {
        int size = itemSubList.size();
        int count = 0;
        for (int i = size - 2; i > size - days && i >= 0; i--) {
            Item anItem = itemSubList.get(i);
            if(anItem.getSmaList().get(25)==null)
                continue;
            if (anItem.getSmaList().get(25) * 0.97 < anItem.getAdjustedClosePrice() && anItem.getSmaList().get(25) * 1.03 > anItem.getAdjustedClosePrice()) {
                ++count;
            }
        }
        return count;
    }

}
