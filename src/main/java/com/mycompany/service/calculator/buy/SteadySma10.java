package com.mycompany.service.calculator.buy;

import com.mycompany.model.Item;
import com.mycompany.model.Portfolio;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ScannerService;
import static com.mycompany.service.calculator.SignalCalculator.today;
import java.util.Calendar;
import java.util.List;

/**
 * @date Dec 23, 2016
 * @author setu
 */
public class SteadySma10 extends BuySignalCalculator {

    public SteadySma10(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
//        if(!today.getCode().equals("DBH"))
//            return false;
        Item lowestSma10 = getLowestSma10(itemSubList);
        Item highestSma10 = getHighestSma10(itemSubList);
        float smaDiff = ((highestSma10.getSmaList().get(10) - lowestSma10.getSmaList().get(10)) / lowestSma10.getSmaList().get(10)) * 100;
//        System.out.println("code: " + today.getCode() + ", minday: " + lowestSma10.getDate() + ", sma10: " + lowestSma10.getSmaList().get(10) + ", maxday: " + highestSma10.getDate() + ", sma10: " + highestSma10.getSmaList().get(10) + ", smadiff: " + smaDiff);
        float changeWithBottom = 100;
        Item aBottom = getLowest(itemSubList);

        if (aBottom != null) {
            changeWithBottom = ((today.getAdjustedClosePrice() - aBottom.getAdjustedClosePrice()) / aBottom.getAdjustedClosePrice()) * 100;
        }

//        System.out.println("smaDiff: " + smaDiff + ", rsi: " + rsi + ", div: " + divergence + ", changeWithBottom: " + changeWithBottom + ", bottom: " + aBottom.getDate() + ", value: " + aBottom.getAdjustedClosePrice());
        
        if (smaDiff <= 7
                && rsi <= 60
                && divergence < maxDivergence
                && changeWithBottom <= 15
                && lowestSma10.getDate() != null
                && highestSma10.getDate() != null
                ) {
//            System.out.println("code: " + today.getCode() + ", minday: " + lowestSma10.getDate() + ", sma10: " + lowestSma10.getSmaList().get(10) + ", maxday: " + highestSma10.getDate() + ", sma10: " + highestSma10.getSmaList().get(10));
            setCause(this.getClass().getName());

            boolean maskPassed = isMaskPassed(today, portfolio);
            if (maskPassed) {
//                
            }
            return maskPassed;
        }
        return false;
    }

    private Item getLowestSma10(List<Item> items) {
//        System.out.println("StartFrom: " + items.get(items.size()-1).getDate());
        Item minimum = new Item();
        minimum.getSmaList().put(10, 100000f);

        for (int i = items.size() - 1; i >= (items.size() - ScannerService.TRADING_DAYS_IN_A_MONTH * 4) && i >= 0 && items.size() > ScannerService.TRADING_DAYS_IN_A_MONTH * 4; i--) {
            if (items.get(i).getSmaList().get(10) == null) {
                float sma_10 = calculateBackdatedSMA(items, 10, items.size() - i - 1);
                items.get(i).getSmaList().put(10, sma_10);
//                System.out.println("date: " + items.get(i).getDate() + ", sma_10: " + items.get(i).getSmaList().get(10));
            }

            if (items.get(i).getSmaList().get(10) < minimum.getSmaList().get(10)) {

                minimum = items.get(i);
            }
        }
        return minimum;
    }

    private Item getHighestSma10(List<Item> items) {
        Item maximum = new Item();
        maximum.getSmaList().put(10, 0f);

        for (int i = items.size() - 1; i >= (items.size() - ScannerService.TRADING_DAYS_IN_A_MONTH * 4) && i >= 0 && items.size() > ScannerService.TRADING_DAYS_IN_A_MONTH * 4; i--) {
            if (items.get(i).getSmaList().get(10) == null) {
                float sma_10 = calculateBackdatedSMA(items, 10, items.size() - i - 1);
                items.get(i).getSmaList().put(10, sma_10);
            }

            if (items.get(i).getSmaList().get(10) > maximum.getSmaList().get(10)) {

                maximum = items.get(i);
            }
        }
        return maximum;
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
}
