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
import java.util.List;

/**
 * @date Dec 17, 2015
 * @author setu
 */
public class Test1 extends BuySignalCalculator {

    public Test1(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }

    @Override
    public boolean isBuyCandidate(List<Item> itemSubList, Item calculated) {
        //intializeVariables(itemSubList, calculated);
                float change = (today.getOpenPrice()-yesterday.getAdjustedClosePrice())/yesterday.getAdjustedClosePrice();
        float todayValuePerTrade = today.getValue()/today.getTrade();
        float ratio = todayValuePerTrade/averageValuePerTrade;
        float oneThirdWay = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 1.5f + today.getOpenPrice();
        float oneFourthWay = (today.getAdjustedClosePrice() - today.getOpenPrice()) / 1.1f + today.getOpenPrice();
        float yesterdayMax = Math.max(yesterday.getOpenPrice(), yesterday.getAdjustedClosePrice());
        float gap = today.getOpenPrice()-yesterdayMax;
        gap = (gap/yesterdayMax)*100;
        
        float twoWeeksAgoSma25 = calculateBackdatedSMA(itemSubList, 25, ScannerService.TRADING_DAYS_IN_A_WEEK*2);
        float OneAndHalfMonthAgoSma25 = calculateBackdatedSMA(itemSubList, 25, ScannerService.TRADING_DAYS_IN_A_MONTH+ScannerService.TRADING_DAYS_IN_A_WEEK);
        float twoMonthAgoSma25 = calculateBackdatedSMA(itemSubList, 25, ScannerService.TRADING_DAYS_IN_A_MONTH*2);
        
        
        float sma25DiffFor_0_2 = ((sma25-twoWeeksAgoSma25)/twoWeeksAgoSma25)*100;
        float sma25DiffFor_2_4 = ((twoWeeksAgoSma25-oneMonthAgoSma25)/oneMonthAgoSma25)*100;
        float sma25DiffFor_4_6 = ((oneMonthAgoSma25-OneAndHalfMonthAgoSma25)/OneAndHalfMonthAgoSma25)*100;
        float sma25DiffFor_6_8 = ((OneAndHalfMonthAgoSma25-twoMonthAgoSma25)/twoMonthAgoSma25)*100;
        float sma25DiffFor_0_8 = ((sma25-twoMonthAgoSma25)/twoMonthAgoSma25)*100;
        float sma25DiffFor_0_4 = ((sma25-oneMonthAgoSma25)/oneMonthAgoSma25)*100;
        float sma25DiffFor_4_8 = ((oneMonthAgoSma25-twoMonthAgoSma25)/twoMonthAgoSma25)*100;
        
        
        float oneWeekAgoSma10 = fourDayBeforeYesterday.getSmaList().get(10);
        float oneWeekAgoSma10Diff = Math.abs(((SignalCalculator.sma10-oneWeekAgoSma10)/oneWeekAgoSma10)*100);
        
        float monthAgoSma10 = monthBefore.getSmaList().get(10);
        float monthAgoSma10Diff = Math.abs(((SignalCalculator.sma10-monthAgoSma10)/monthAgoSma10)*100);
        
        boolean gapLogic = gap>0.7? todayGap>=4:true;
        float plainGap = todayClosePrice-today.getOpenPrice();
        float oneWeekAgoEma9 = fourDayBeforeYesterday.getEmaList().get(9);
        float todayEma9 = today.getEmaList().get(9);
        float macdPercentage = (macd/todayClosePrice)*100;
        boolean emaPass = todayEma9<=0?true:todayEma9>=oneWeekAgoEma9;
        float straightGap = Math.max(today.getOpenPrice(), today.getAdjustedClosePrice()) - Math.min(today.getOpenPrice(), today.getAdjustedClosePrice());
        float tails = ((today.getDayHigh() - today.getDayLow()) - straightGap);
        boolean isShaky = tails>straightGap;
        float topTail = ((today.getDayHigh()-Math.max(today.getOpenPrice(), today.getAdjustedClosePrice()))/Math.max(today.getOpenPrice(), today.getAdjustedClosePrice()))*100;
        float bottomTail = ((Math.min(today.getOpenPrice(), today.getAdjustedClosePrice())-today.getDayLow())/Math.min(today.getOpenPrice(), today.getAdjustedClosePrice()))*100;
        float maxPriceDayMax = Math.max(maxPriceDay.getOpenPrice(), maxPriceDay.getAdjustedClosePrice());
        float changeWithMaxPriceDay = ((todayClosePrice-maxPriceDayMax)/maxPriceDayMax)*100;
        float todaysmaDiff = ((sma25-sma10)/sma10)*100;
        float dsexSma25 = dsex.getSmaList().get(25);
        float tolarity = 5;
//        float dsexDown = ((dsex.getDayHigh()-dsex.getAdjustedClosePrice())/dsex.getDayHigh())*100;
//        float dsexMax = Math.max(dsex.getOpenPrice(), dsex.getAdjustedClosePrice());
        float todayDsexTopTail = ((dsex.getDayHigh()- Math.max(dsex.getOpenPrice(), dsex.getAdjustedClosePrice()))/ Math.max(dsex.getOpenPrice(), dsex.getAdjustedClosePrice()))*100;
        float yesterdayDsexTopTail = ((dsexYesterday.getDayHigh()- Math.max(dsexYesterday.getOpenPrice(), dsexYesterday.getAdjustedClosePrice()))/ Math.max(dsexYesterday.getOpenPrice(), dsexYesterday.getAdjustedClosePrice()))*100;
        float dayBeforeDsexTopTail = ((dsexDayBefore.getDayHigh()- Math.max(dsexDayBefore.getOpenPrice(), dsexDayBefore.getAdjustedClosePrice()))/ Math.max(dsexDayBefore.getOpenPrice(), dsexDayBefore.getAdjustedClosePrice()))*100;
        
        float signalLine = today.getEmaList().get(9);
        
        
//        if(today.getCode().equals("SAPORTL") )
//        System.out.println("date: " + today.getDate() + ", diffWithPreviousLow10: " + diffWithPreviousLow10);

        if (

//                && (dsex.getAdjustedClosePrice()>dsex.getOpenPrice() && dsex.getAdjustedClosePrice()>dsexSma25 && dsex.getOpenPrice()<=(dsexSma25*1.01))
//                && yesterdayGap < 0
                divergence < maxDivergence
//                && todayValue >= minValue
//                && todayTrade >= minTrade
//                && yesterday.getVolumeChange() >=1.5
//                && weeklyVChange > 1.5
//                && vChange>2 
                && todayDsexTopTail<0.50 && yesterdayDsexTopTail<0.50 && dayBeforeDsexTopTail<0.50
                  && rsi <= 45
//                && signalLine > 0
//                && todaychange >9 && todaychange<=10
//                && today.getAdjustedVolume() >= yesterday.getAdjustedVolume()*2
//                && weeklyVChange >= 3
//                && vChange >= minVChange && ((marketWasDown && vChange <= 4) || (vChange <= 2 && weeklyVChange<2.8) || (vChange>3 && publicShare<10000000 && lastTwoMonthVariation <= 10))
//                && volumePerTradeChange >= 1.3 && vChange >=5
//                && Math.min(todayGap, yesterdayGap) > -3
                && diffWithPreviousLow10 <= 5
                && (topTail+bottomTail) >= 4
                && bottomTail > topTail
                && Math.abs(todayGap) < 0.5
//                && oneWeekAgoSma10Diff < 5
//                && monthAgoSma10Diff < 5
//                && !(topTail>=2 && vChange>=2)
//                && upperTail <= 1
//                && acceptableItemSMA 
//                && acceptableDSEXSMA
                && !today.getSector().equalsIgnoreCase("bank") 
                && !today.getSector().equalsIgnoreCase("mutual funds")
//                && sma25IntersectInLastFewDays >= 6
//                && gap >=2 && gap<=3
//                && todaychange >5 && todaychange<9
//                && dsexMaxRsiInLast2Days <= maxAllowedDsexRsi
//                && sma25DiffFor_0_2>=-tolarity && sma25DiffFor_0_2<=tolarity
//                && sma25DiffFor_2_4>=-tolarity && sma25DiffFor_2_4<=tolarity
////                && sma25DiffFor_4_6>=-tolarity && sma25DiffFor_4_6<=tolarity
////                && sma25DiffFor_6_8>=-tolarity && sma25DiffFor_6_8<=tolarity
////                && sma25DiffFor_0_8>=-tolarity*1 && sma25DiffFor_0_8<=tolarity*1
//                && sma25DiffFor_0_4>=-tolarity*1 && sma25DiffFor_0_4<=tolarity*1
//                && sma25DiffFor_4_8>=-tolarity*1 && sma25DiffFor_4_8<=tolarity*1
                
//                && volumePerTradeChange <= 1.3
////                
//                && todayClosePrice > sma10
//                && emaPass
//                && !isShaky
//                && sma25> sma10*1.01 && today.getOpenPrice()<sma10 && today.getAdjustedClosePrice()>sma25
//                && today.getAdjustedVolume() > lastMonthMaxVolume
//                  && todayClosePrice > lastMonthMaximum
//                && changeWithMaxPriceDay >= -1
//                && lastMonthSmaVariation < 3
//                && smaTrend
//                  && volumePerTradeChange <= 1.2 
                //&& (lastMonthVariation <= 7 ? lastMonthMaximum < today.getAdjustedClosePrice() : true)
//                && (lastMonthVariation <= 7 ? diffWithLastMonthHigh > -1 : true)
//                && !((today.getAdjustedClosePrice() - today.getYesterdayClosePrice()) <= 0.1)
//                && !((today.getDayHigh() - today.getAdjustedClosePrice()) > (today.getAdjustedClosePrice() - today.getOpenPrice()))
                ) {
            setCause(this.getClass().getName());
            
            if(today.getCode().equals("SAIFPOWER"))
                System.out.println("sma25: " + sma25 + ", twoWeeksAgoSma25: " + twoWeeksAgoSma25 + ", oneMonthAgoSma25: " + oneMonthAgoSma25 + ", OneAndHalfMonthAgoSma25: " + OneAndHalfMonthAgoSma25 + ", twoMonthAgoSma25: " + twoMonthAgoSma25);
            
            boolean maskPassed = isMaskPassed(today, portfolio);
            return true;
        }
        return false;
    }

}
