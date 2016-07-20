/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.service.calculator.sell;

import com.mycompany.model.Item;
import com.mycompany.model.Portfolio;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ScannerService;
import static com.mycompany.service.calculator.SignalCalculator.gain;
import static com.mycompany.service.calculator.SignalCalculator.lastTradingDay;
import static com.mycompany.service.calculator.SignalCalculator.today;
import java.util.List;

/**
 * @date Dec 17, 2015
 * @author setu
 */
public class ClusteredSellSignalCalculator {

    public static class sell1 extends SellSignalCalculator {

        public sell1(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //initializeVariables(itemSubList, calItem);
            //@1. Should have two consecutive red (among them at least one day open-close should be more than 1%) and close price less than day before yesterday open price
            //System.out.println("sell1: " + itemSubList.get(itemSubList.size()-1).getDate() + ", belowBothSMA: " + belowBothSMA + ", belowDSEXBothSMA: " + belowDSEXBothSMA);
            if (twoConsecutiveRed && today.getAdjustedClosePrice() < dayBeforeYesterday.getOpenPrice() && isValidCandidate(calculatedItem, itemSubList)) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }

            return false;
        }
    }

    public static class sell2 extends SellSignalCalculator {

        public sell2(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        //@2. Close price less than buy day open price
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            if (buyDayItem != null && today.getAdjustedClosePrice() < buyDayItem.getOpenPrice()) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell3 extends SellSignalCalculator {

        public sell3(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        //@3. Open price - close price is more than 6% 
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            if (todayGap < -6) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell4 extends SellSignalCalculator {

        public sell4(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            //@4. close price dropped to more than 6% of previous close price
            if (todaychange < -6) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell5 extends SellSignalCalculator {

        public sell5(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            //@5. Today is down and yesterday was hammer<-2
            if (todayGap < 0 && todaychange < 0 && getDBHammer(yesterday) < -2) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell55 extends SellSignalCalculator {

        public sell55(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            //@5.5. Today is down and today is hammer<-3
            //System.out.println("date: " + today.getDate() + ", todayGap: " + todayGap + ", getDBHammer(today): " + getDBHammer(today));
            if (todayGap < 1 && (todaychange < -1 || Math.max(rsi, yesterdayRsi) >= 80) && ((getDBHammer(today) < -3) || upperTail > 2.9)) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell56 extends SellSignalCalculator {

        public sell56(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            //@5.6. Today uppertail is > 4
            float sellingTail = sellingUpperTail;
            if (rsi < 69) {
                sellingTail = sellingUpperTail + 69 - rsi;
            }
            if (upperTail > sellingTail && todayGap <= 1 && (todaychange < -1 || Math.max(rsi, yesterdayRsi) >= 80) && Math.max(rsi, yesterdayRsi) >= 69) {
                //System.out.println("sell56-date: " + today.getDate() + ", upperTail: " + upperTail);
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell6 extends SellSignalCalculator {

        public sell6(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            //@6. Today is down and is less than 3% and RSI > 70
            if (todayGap < -3 && calculatedItem.getRSI() > 70) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell7 extends SellSignalCalculator {

        public sell7(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            //@7. Today is down and pressure <-10
            if (todayGap < 0 && calculatedItem.getPressure() < -10) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell8 extends SellSignalCalculator {

        public sell8(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            // @8. Today is down and todays higher greater than yesterdsys higher and todays lower less than yesterdays lower
            //System.out.println("code: " + today.getCode() + ", date: " + today.getDate() + ", todayGap: " + todayGap + ", todaysHigher: " + todaysHigher + ", yesHigher: " + yesterdayHigher + ", todaysLower: " + todaysLower + ", yesLower: " + yesterdayLower);
            if (todayGap < 0 && todaysHigher > yesterdayHigher && todaysLower < yesterdayLower) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell9 extends SellSignalCalculator {

        public sell9(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            // @9. Today close price less than yesterday minimum and less than day before yesterday minimum
            if (todayGap < 0 && today.getAdjustedClosePrice() < yesterdayLower && today.getAdjustedClosePrice() < dayBeforeYesterdayLower) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell10 extends SellSignalCalculator {

        public sell10(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            // @10. any of last 3 days RSI is 70+ and today is not green nor up
            //System.out.println(today.getDate() + ", yesterdayrsi: " + calculatedItem.getYesterdayRSI());
            boolean rsi80 = rsi >= 80 || yesterdayRsi >= 80 || dayBeforeRsi >= 80;
//            System.out.println(", sell10date: " + today.getDate() + ", rsi70: " + rsi70 + ", todaygap: " + todayGap + ", yesterdayRsi: " + yesterdayRsi + ", dayBeforeRsi: " + dayBeforeRsi + ", daybefore: " + dayBeforeYesterday.getDate());
            if (rsi80 && (todayGap <= 0 || todaychange <= 0)) {
                setCause(this.getClass().getName());
                boolean mask = isMaskPassed(today, portfolio);
                System.out.println(", sell10date: " + today.getDate() + ", rsi80: " + rsi80 + ", todaygap: " + todayGap + ", mask: " + mask);
                return mask;
            }
            return false;
        }
    }

    public static class sell11 extends SellSignalCalculator {

        public sell11(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            // @11. If price falls bellow both sma10 and sma25 either for item or for dsex
            //System.out.println("sell11date: " + today.getDate() + ", belowBothSMA: " + belowBothSMA + ", belowDSEXBothSMA: " + belowDSEXBothSMA);
            if (todaychange < 0 && todayGap < 0 && (belowBothSMA && belowDSEXBothSMA)) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class sell12 extends SellSignalCalculator {

        public sell12(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            // @12. DSEX index is below both SMA
            //System.out.println("sell11date: " + today.getDate() + ", belowBothSMA: " + belowBothSMA + ", belowDSEXBothSMA: " + belowDSEXBothSMA);
            if (belowDSEXBothSMA) {
                setCause(this.getClass().getName());
                return isMaskPassed(today, portfolio);
            }
            return false;
        }
    }

    public static class BellowEitherItemOrDsexBothSMA extends SellSignalCalculator {

        public BellowEitherItemOrDsexBothSMA(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            // @12. DSEX index is below both SMA
            //System.out.println("sell11date: " + today.getDate() + ", belowBothSMA: " + belowBothSMA + ", belowDSEXBothSMA: " + belowDSEXBothSMA);
            if (belowBothSMA || belowDSEXBothSMA) {
                setCause(this.getClass().getName());
                return true;
            }
            return false;
        }
    }

    public static class sell13 extends SellSignalCalculator {

        public sell13(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            // @12. RSI 80+ and today gap is negetive and price is falling
            System.out.println("sell13date: " + today.getDate() + ", rsi: " + rsi + ", todayGap: " + todayGap + ", todayChange: " + todaychange);
            if (rsi >= 80 && todayGap < 0 && todaychange < 0) {
                setCause(this.getClass().getName());
                boolean mask = isMaskPassed(today, portfolio);
                //System.out.println("sell13date: " + today.getDate() + ", rsi: " + rsi + ", mask: " + mask);
                return mask;
            }
            return false;
        }
    }

    public static class sell14 extends SellSignalCalculator {

        public sell14(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);

            float yesterdayVChange = yesterday.getVolumeChange();
            float todayVChange = today.getVolumeChange();
            float volumeDrop = todayVChange / yesterdayVChange;
            long tenure = today.getDate().getTime() - buyItem.getDate().getTime();
            tenure = tenure / 86400000;

            if (gain > 0 && gain <= 5 && tenure > DECISION_MAKING_TENURE && vChange < 3 && Math.max(rsi, yesterdayRsi) >= 70) {
//                System.out.println("Going to check sell4.date: " + today.getDate());
                setCause(this.getClass().getName());
                boolean mask = isMaskPassed(today, portfolio);
//                System.out.println(", sell14date: " + today.getDate() + ", mask: " + mask + ", gain: " + gain);
                return mask;
            }
            return false;
        }
    }

    public static class ProfitTake extends SellSignalCalculator {

        public ProfitTake(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);

            float yesterdayVChange = yesterday.getVolumeChange();
            float todayVChange = today.getVolumeChange();
            float volumeDrop = todayVChange / yesterdayVChange;
            long tenure = today.getDate().getTime() - buyItem.getDate().getTime();
            tenure = tenure / 86400000;
            float topTail = ((today.getDayHigh() - Math.max(today.getOpenPrice(), today.getAdjustedClosePrice())) / Math.max(today.getOpenPrice(), today.getAdjustedClosePrice())) * 100;
            float bottomTail = ((Math.min(today.getOpenPrice(), today.getAdjustedClosePrice()) - today.getDayLow()) / Math.min(today.getOpenPrice(), today.getAdjustedClosePrice())) * 100;
            float diffWithSma10 = ((todayClosePrice-sma10)/sma10)*100;

//            if (gain >= 8 && gain <= 13
//                    && (todayGap < 0 || topTail >= 2)) {
//                setCause(this.getClass().getName());
//                boolean mask = isMaskPassed2(today, portfolio);
////                System.out.println(", sell14date: " + today.getDate() + ", mask: " + mask + ", gain: " + gain);
//                return mask;
//            }
            
            if (
                    gain >= 10
                    && (todayGap < 0 || topTail >= 2)
                    && diffWithSma10<2
                    ) {
                setCause(this.getClass().getName());
                boolean mask = isMaskPassed2(today, portfolio);
//                System.out.println(", sell14date: " + today.getDate() + ", mask: " + mask + ", gain: " + gain);
                return mask;
            }
            
            return false;
        }
    }
    
    public static class EndOfRise extends SellSignalCalculator {

        public EndOfRise(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);

            float yesterdayVChange = yesterday.getVolumeChange();
            float todayVChange = today.getVolumeChange();
            float volumeDrop = todayVChange / yesterdayVChange;
            long tenure = today.getDate().getTime() - buyItem.getDate().getTime();
            tenure = tenure / 86400000;
            float topTail = ((today.getDayHigh() - Math.max(today.getOpenPrice(), today.getAdjustedClosePrice())) / Math.max(today.getOpenPrice(), today.getAdjustedClosePrice())) * 100;
            float bottomTail = ((Math.min(today.getOpenPrice(), today.getAdjustedClosePrice()) - today.getDayLow()) / Math.min(today.getOpenPrice(), today.getAdjustedClosePrice())) * 100;
//            System.out.println("gain: " + gain + ", topTail: " + topTail);
            float maxRsi = Math.max(yesterdayRsi, dayBeforeRsi);
//            if (gain >= 8 && gain <= 13
//                    && (todayGap < 0 || topTail >= 2)) {
//                setCause(this.getClass().getName());
//                boolean mask = isMaskPassed2(today, portfolio);
////                System.out.println(", sell14date: " + today.getDate() + ", mask: " + mask + ", gain: " + gain);
//                return mask;
//            }
            
            if (gain >= 5 && maxRsi>=70 && todayClosePrice<sma10) {
                setCause(this.getClass().getName());
                //boolean mask = isMaskPassed2(today, portfolio);
//                System.out.println(", sell14date: " + today.getDate() + ", mask: " + mask + ", gain: " + gain);
                return true;
            }
            
            return false;
        }
    }

    public static class StopLoss extends SellSignalCalculator {

        public StopLoss(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);

            float yesterdayVChange = yesterday.getVolumeChange();
            float todayVChange = today.getVolumeChange();
            float volumeDrop = todayVChange / yesterdayVChange;
            long tenure = today.getDate().getTime() - buyItem.getDate().getTime();
            tenure = tenure / 86400000;



            float diffWithMinimum = ((bottom.getAdjustedClosePrice() - today.getAdjustedClosePrice()) / bottom.getAdjustedClosePrice()) * 100;
//            System.out.println("diffWithMinimum: " + diffWithMinimum + ", minimum.getAdjustedClosePrice(): " + bottom.getAdjustedClosePrice() + ", today.getAdjustedClosePrice(): " + today.getAdjustedClosePrice());

//            if (gain<=-4 && gain>=-6) {
            if (diffWithMinimum > 2) {
//                System.out.println("Going to check sell4.date: " + today.getDate());
                setCause(this.getClass().getName());
                boolean mask = isMaskPassed2(today, portfolio);
//                System.out.println(", sell14date: " + today.getDate() + ", mask: " + mask + ", gain: " + gain);
//                return mask;
                return true;
            }
            return false;
        }
    }

    public static class RSIDrop extends SellSignalCalculator {

        public RSIDrop(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            float maxRsi = Math.max(yesterdayRsi, dayBeforeRsi);
            if ((maxRsi >= 80 && rsi < maxRsi) || (maxRsi >= 70 && rsi < 70)) {
                setCause(this.getClass().getName());
                boolean mask = isMaskPassed(today, portfolio);
//                System.out.println(", sell14date: " + today.getDate() + ", mask: " + mask + ", gain: " + gain);
                return mask;
            }
            return false;
        }
    }

    public static class EOM extends SellSignalCalculator {

        public EOM(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
            super(scanner, oneYearData, portfolio);
        }

        @Override
        public boolean isSellCandidate(List<Item> itemSubList, Item calItem) {
            //super.initializeVariables(itemSubList, calItem);
            setCause(this.getClass().getName());
            return lastTradingDay != null && today.getDate().after(lastTradingDay.getTime());
        }
    }
}
