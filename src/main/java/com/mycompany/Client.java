package com.mycompany;

import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.dao.PortfolioDaoImpl;
import com.mycompany.model.EPSList;
import com.mycompany.model.Item;
import com.mycompany.model.ItemNews;
import com.mycompany.model.Portfolio;
import com.mycompany.model.PortfolioItem;
import com.mycompany.service.Crawler;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ImportService;
import com.mycompany.service.ScannerService;
import com.mycompany.service.SyncService;
import com.mycompany.service.Utils;
import com.mycompany.service.calculator.SignalCalculator;
import static com.mycompany.service.calculator.SignalCalculator.lastTradingDay;
import com.mycompany.service.calculator.buy.BuySignalCalculator;
import com.mycompany.service.calculator.buy.*;
import com.mycompany.service.calculator.sell.ClusteredSellSignalCalculator;
import com.mycompany.service.calculator.sell.SellSignalCalculator;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.webharvest.definition.ScraperConfiguration;

/**
 * @date May 13, 2015
 * @author Setu
 */
public class Client {

    private final static String DATE_PATTERN = "dd-MM-yyyy";
    private final static String DSEX_CODE = "DSEX";
    static DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
    private static Map<Date, Item> dsexMap = new HashMap<>();

    public static void main(String[] args) {

        try {
            //checkByDate();
            //checkByScript();
            //checkByScript2();
            //tradeGTVolume();
            //hammer();
            //checkByScript3();
//            parseEPS();
//            getNews();
            //checkByScript4();
            //isTailFound();
            //checkByScript3_4_5();
            //importDsexArchive();
            //getPortfolio();
            calculateBuySell();
        } catch (Exception ex) {
            System.err.println("Error caught: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void calculateBuySell() throws SQLException, ClassNotFoundException, InterruptedException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getData(400);
        dao.close();

        ScannerService scanerService = new ScannerService();
        Portfolio portfolio = new Portfolio();
        List<BuySignalCalculator> buyCalculators = new ArrayList<>();
//        buyCalculators.add(new Consecutive1(scanerService, oneYearData, portfolio));        //21    Average:52
//        buyCalculators.add(new Consecutive15(scanerService, oneYearData, portfolio));       //17
//        buyCalculators.add(new Consecutive2(scanerService, oneYearData, portfolio));        //6
//        buyCalculators.add(new Consecutive3(scanerService, oneYearData, portfolio));        //15
//        buyCalculators.add(new PotentialGap(scanerService, oneYearData, portfolio));        //26  Average: 66
//        buyCalculators.add(new Sma25(scanerService, oneYearData, portfolio));           //21        Average: 30
//        buyCalculators.add(new ExtendedSMA25(scanerService, oneYearData, portfolio));     //10
//        buyCalculators.add(new GreenAfterRsi30(scanerService, oneYearData, portfolio));     //38    Average: 14
//        buyCalculators.add(new SuddenHike(scanerService, oneYearData, portfolio));        //27        Average: 60
//        buyCalculators.add(new Tail(scanerService, oneYearData, portfolio));            //18        Average: 0
//        buyCalculators.add(new ThreeGreen(scanerService, oneYearData, portfolio));      //22        Average: 16
//        buyCalculators.add(new Sma25Trend(scanerService, oneYearData, portfolio));      //19        Average: 17
//        buyCalculators.add(new LargeCandle(scanerService, oneYearData, portfolio));     //29          Average: 83
//        buyCalculators.add(new ConsecutiveGreenAfterRSI30(scanerService, oneYearData, portfolio));  //31    Average: 0
//        buyCalculators.add(new MultipleSma25Intersect(scanerService, oneYearData, portfolio));  //59    Average: 20
//        buyCalculators.add(new SmaIntersect(scanerService, oneYearData, portfolio));    //24            Average: 20
//        buyCalculators.add(new Bottom(scanerService, oneYearData, portfolio));        //24              Average: 17
        buyCalculators.add(new Average(scanerService, oneYearData, portfolio));
//        buyCalculators.add(new Macd(scanerService, oneYearData, portfolio));                //10
//        buyCalculators.add(new Sma10(scanerService, oneYearData, portfolio));           //16
//        buyCalculators.add(new AroundSma25(scanerService, oneYearData, portfolio));        //38         Average: 15
//        buyCalculators.add(new SteadySma10(scanerService, oneYearData, portfolio));        //27
        buyCalculators.add(new Test(scanerService, oneYearData, portfolio));   
          


        List<SellSignalCalculator> sellCalculators = new ArrayList<>();
        ClusteredSellSignalCalculator clusterSell = new ClusteredSellSignalCalculator();
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell1(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell2(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell3(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell4(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell5(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell55(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell56(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell6(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell7(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell8(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell9(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell10(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell11(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell12(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.sell14(scanerService, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.ProfitTake(scanerService, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.EndOfRise(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.WasWrongBuy(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.Sma25Counter(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.BellowSMA25(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.BellowEitherItemOrDsexBothSMA(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.StopLoss(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.RSIDrop(scanerService, oneYearData, portfolio));
//        sellCalculators.add(new ClusteredSellSignalCalculator.FixedProfit(scanerService, oneYearData, portfolio));
        sellCalculators.add(new ClusteredSellSignalCalculator.EOM(scanerService, oneYearData, portfolio));

        String script = "ICB";
        profit = 0;
        loss = 0;
        totalBuy = 0;
        summery = 0;
        
        Map<String, Integer> lossCounter = new HashMap<>();
        Map<String, Integer> profitCounter = new HashMap<>();
        
        lastTradingDay = Calendar.getInstance();
        lastTradingDay.set(Calendar.YEAR, 2017);
        lastTradingDay.set(Calendar.MONTH, 0);
        lastTradingDay.set(Calendar.DAY_OF_MONTH, 26);
        
        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, 2015);
        start.set(Calendar.MONTH, 4);
        start.set(Calendar.DAY_OF_MONTH, 1);
        
        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, 2015);
        end.set(Calendar.MONTH, 4);
        end.set(Calendar.DAY_OF_MONTH, 31);
        
//        Map<String, List<ItemNews>> newsMap = getNews();
        Map<String, List<ItemNews>> newsMap = new HashMap<>();

        for (String code : oneYearData.keySet()) {
            //Skip dsex
            if (code.equals(DSEX_CODE)) {
                continue;
            }
            
            
//            if (!code.equals(script)) {
//                SignalCalculator.debugEnabled = true;
//                continue;
//            }
            
            //Skip codes
//            List<String> skipCodes = new ArrayList<>();
//            skipCodes.add("PRIMELIFE");
//            if(skipCodes.contains(code))
//                continue;

            List<Item> items = oneYearData.getItems(code);
            scanerService.calculateVolumePerTradeChange(items, ScannerService.TRADING_DAYS_IN_A_MONTH);

            outerloop:
            for (int i = 50; i < items.size(); i++) {
                List<Item> itemSubList = new ArrayList();
                int counter = 0;
                for (int j = i; j >= 0; j--) {
                    itemSubList.add(items.get(j));
//                    if (counter >= ScannerService.TRADING_DAYS_IN_2_MONTH) {
//                        break;
//                    }
                    ++counter;
                }
                Collections.sort(itemSubList);
                Item today = itemSubList.get(itemSubList.size() - 1);
                PortfolioItem pItem = portfolio.getPortfolioItems().get(today.getCode());
                
                SignalCalculator aCalculator = buyCalculators.get(0);
                //System.out.println("lastItems: " + itemSubList.get(itemSubList.size()-1).getDate() + ", items size: " + items.size());
                //Item[] sublist = new Item[itemSubList.size()];
                //sublist = itemSubList.toArray(sublist);
                List<Item> copyOfSubList = new ArrayList<>(itemSubList);
                aCalculator.intializeVariables(copyOfSubList, null);  
                
                //System.out.println("firstdate: " + copyOfSubList.get(0).getDate() + ", lastdate: " + copyOfSubList.get(copyOfSubList.size()-1).getDate() + ", size: " + copyOfSubList.size() + ", pItem= " + pItem + ", lasttradeday: " + SignalCalculator.lastTradingDay.getTime());
                Item.SignalType signalType = Item.SignalType.NA;
                
                boolean isBadEps = isBadEps(today, newsMap);
//                if(today.getCode().equals("ISLAMIINS"))
//                    System.out.println("date: " + today.getDate() + "isBadEps: " + isBadEps);
                float sma25Diff = ((SignalCalculator.sma25-SignalCalculator.oneWeekAgoSma25)/SignalCalculator.oneWeekAgoSma25)*100;
                
                for (BuySignalCalculator calculator : buyCalculators) {
                    if (calculator.isBuyCandidate(copyOfSubList, null)) {
                        if(isBadEps)
                            continue;
                        
                        //Dont buy in May 2015
                        if(today.getDate().after(start.getTime()) && today.getDate().before(end.getTime()))
                            continue;
                        
                        float todayValuePerTrade = today.getValue()/today.getTrade();
                        float ratio = todayValuePerTrade/SignalCalculator.averageValuePerTrade;
                        String causeDetails = parseCause(calculator) + "(t:" + df.format(SignalCalculator.tChange) + ", v:" + df.format(SignalCalculator.vChange) + ", gain:" + SignalCalculator.gain + ", averageDiff: " + SignalCalculator.averagePriceOnLastFewDays + ")";
                        //System.out.println(", date: " + today.getDate() + ", buycause: " + SignalCalculator.getCause() + ", pItem: " + pItem);
                        if (pItem == null) {
                            pItem = createPortfolioItem(today);
                            portfolio.getPortfolioItems().put(today.getCode(), pItem);
                            ++totalBuy;
                            signalType = Item.SignalType.BUY;
                            System.out.println("");
                            System.out.print(signalType + " " + today.getCode() + " on " + today.getDate() + ", price: " + today.getAdjustedClosePrice() + ", cause: " + causeDetails);
                        }else if(SignalCalculator.gain>=0){
                            signalType = Item.SignalType.HOLD;
                            System.out.println("");
                            System.out.print(signalType + " " + today.getCode() + " on " + today.getDate() + ", price: " + today.getAdjustedClosePrice() + ", cause: " + causeDetails);
                        }else if(SignalCalculator.gain>-SignalCalculator.AVERAGE_ON_LOSS_PERCENT){
                            signalType = Item.SignalType.BUY;
                            System.out.println("");
                            System.out.print(signalType + " " + today.getCode() + " on " + today.getDate() + ", price: " + today.getAdjustedClosePrice() + ", cause: " + causeDetails);
//                        }else if(SignalCalculator.gain<=-SignalCalculator.AVERAGE_ON_LOSS_PERCENT){
//                            float earlierBuyPrice = pItem.getAverageBuyPrice();
//                            float todayBuyPrice = today.getAdjustedClosePrice()*1.005f;
//                            float avgPrice = (earlierBuyPrice + todayBuyPrice)/2f;
//                            pItem.setAverageBuyPrice(avgPrice);
//                            ++totalAvg;
//                            signalType = Item.SignalType.AVG;
//                            System.out.println("");
//                            System.out.print(signalType + " " + today.getCode() + " on " + today.getDate() + ", price: " + today.getAdjustedClosePrice() + ", avg: " + avgPrice + ", cause: " + causeDetails);
                        }else if(calculator.getClass().getName().contains("Average")){
                            float earlierBuyPrice = pItem.getAverageBuyPrice();
                            float todayBuyPrice = today.getAdjustedClosePrice()*1.005f;
                            float avgPrice = (earlierBuyPrice + todayBuyPrice)/2f;
                            pItem.setAverageBuyPrice(avgPrice);
                            ++totalAvg;
                            signalType = Item.SignalType.AVG;
                            System.out.println("");
                            System.out.print(signalType + " # " + today.getCode() + " on " + today.getDate() + ", price: " + today.getAdjustedClosePrice() + ", avg: " + avgPrice + ", cause: " + causeDetails);
                        }
                        
                        //if(!today.getDate().after(SignalCalculator.lastTradingDay.getTime()))
                        continue outerloop;
                    }
                }
                
                //System.out.println("Nobuyfound date: " + today.getDate() + ", pItem: " + pItem);
                //No buy item, so not need to check sell
                if (pItem == null) {
                    continue;
                }

                copyOfSubList = new ArrayList<>(items);
                for (SellSignalCalculator calculator : sellCalculators) {
                    if (calculator.isSellCandidate(copyOfSubList, null)) {
                        signalType = Item.SignalType.SELL;
                        
                        String cause = parseCause(calculator);
                        
                        float gain = calculateGain(pItem, today);
                        if(gain > maxGain)
                            maxGain = gain;
                        summery += gain;
                        ++totalSell;
                        
                        if (gain > 0) {
                            ++profit;
                            Integer profitCountObject = profitCounter.get(pItem.getCause());
                            if(profitCountObject == null)
                                profitCountObject = 0;
                            profitCounter.put(pItem.getCause(), profitCountObject+1);
                        } else {
                            ++loss;
                            Integer lossCountObject = lossCounter.get(pItem.getCause());
                            if(lossCountObject == null)
                                lossCountObject = 0;
                            lossCounter.put(pItem.getCause(), lossCountObject+1);
                        }
                        
                        long tenure = today.getDate().getTime() - pItem.getDate().getTime();
                        tenure = tenure/86400000;
                        totalTenure += tenure;
                        
                        System.out.print("----" + signalType + " " + today.getCode() + " on " + today.getDate() + ", price: " + today.getAdjustedClosePrice() + ", cause: " + cause + ", gain: " + df.format(gain) + "%" + ", tenure: " + tenure );
                        portfolio.getPortfolioItems().remove(today.getCode());
                        continue outerloop;
                    }
                }
                //System.out.println("nosellfound on " + today.getDate());
            }
        }

        System.out.println("");
        //float winPercentage = ((float)profit/(float)(profit+loss))*100;
        //System.out.println("Profit: " + profit + ", loss: " + loss + ", percentage: " + df.format(winPercentage) + "%");
        float gainPercent = ((float) profit / (float) (totalBuy)) * 100;
        float averageTenure = (float)totalTenure/(float)totalSell;
        float averagePercent = ((float)totalAvg/(float)totalBuy)*100;
        
        //Deduct max gain for safety
        summery -= maxGain;
        
        float profitRate = summery / totalBuy;
        float transactionInAYear = 365f/averageTenure;
        
//        float profitWeight = (profitRate*transactionInAYear)*(80f/100f);
        float reduceFactor = 15+averagePercent/2f;
        reduceFactor = 100-reduceFactor;
        
        float profitWeight = (profitRate*transactionInAYear)*(reduceFactor/100f);
        
        System.out.println("\nsummery: " + summery + ", totalBuy: " + totalBuy + ", totalAvg: " + totalAvg + ", totalSell: " + totalSell + ", profitRate: " + profitRate + ", profit:loss= " + profit + " : " + loss + " gainPercent: " + df.format(gainPercent) + ", averageTenure: " + averageTenure + ", averagePercent: " + averagePercent + ", reduceFactor: " + reduceFactor + "\nprofitWeight: " + profitWeight);
        System.out.println("losscounter: " + lossCounter);
        System.out.println("proftcounter: " + profitCounter);
    }
    
//    private static boolean byPassDate(Date date){
//        Calendar start = Calendar.getInstance();
//        start.set(Calendar.YEAR, 2015);
//        start.set(Calendar.MONTH, 4);
//        start.set(Calendar.DAY_OF_MONTH, 1);
//        
//        Calendar end = Calendar.getInstance();
//        end.set(Calendar.YEAR, 2015);
//        end.set(Calendar.MONTH, 4);
//        end.set(Calendar.DAY_OF_MONTH, 31);
//        
//        return date.after(start.getTime()) && date.before(end.getTime());
//    }
    
    private static boolean isBadEps(Item item, Map<String, List<ItemNews>> newsMap){
        List<ItemNews> newses = newsMap.get(item.getCode());

        if(newses == null)
            return false;
        
        Collections.sort(newses);
        Collections.reverse(newses);
        for(ItemNews news: newses){
            if(!news.getDate().after(item.getDate())){
                EPSList epsList = news.getEpsList();
//                if(item.getCode().equals("1STPRIMFMF")){
//                    System.out.println("date: " + item.getDate() + ", epsList: " + epsList + ", isgood: " + isGoodEps(epsList));
//                }
                return !isGoodEps(epsList);
            }
        }
        return false;
    }
    
    private static String parseCause(SignalCalculator calculator){
        String cause = SignalCalculator.getCause();
        if(cause != null){
            if(cause.contains("$"))
                cause = cause.substring(cause.indexOf("$")+1);
            if(cause.contains("."))
                cause = cause.substring(cause.lastIndexOf(".") + 1);
            return cause;
        }
            
        
//        if(calculator instanceof BuySignalCalculator){
//            //cause = calculator.getClass().getName();
//            return cause.substring(cause.lastIndexOf(".")+1);
//        }
//        
//        if(calculator instanceof SellSignalCalculator){
//            //cause = calculator.getClass().getName();
//            cause = cause.substring(cause.indexOf("$") + 1);
//            //System.out.println("parsed cause: " + cause);
//            return cause;
//        }
        
        return null;
    }

    private static float calculateGain(PortfolioItem pItem, Item today) {
        float gain = ((today.getAdjustedClosePrice() - pItem.getAverageBuyPrice()) / pItem.getAverageBuyPrice()) * 100;
        gain -= 0.5; //Comission;
        return gain;
    }

    private static PortfolioItem createPortfolioItem(Item item) {
        PortfolioItem pItem = new PortfolioItem();
        pItem.setCode(item.getCode());
        pItem.setDate(item.getDate());
        pItem.setAverageBuyPrice(item.getAdjustedClosePrice() * 1.005f);
        pItem.setCause(parseCause(null));
        return pItem;
    }

    private static Portfolio getPortfolio() throws SQLException, ClassNotFoundException {
        PortfolioDaoImpl dao = new PortfolioDaoImpl();
        dao.open();
        Portfolio portfolio = dao.getPortfolio(SyncService.PORTFOLIO_ID);
        dao.close();

        Map<String, PortfolioItem> portfolioItems = portfolio.getPortfolioItems();
//        for (PortfolioItem portfolioItem : portfolioItems.values()) {
//            System.out.println(portfolioItem);
//        }

        return portfolio;
    }

    private static void importDsexArchive() {
        ImportService importService = new ImportService();
        importService.importDSEXArchive(7);
    }

    private static Map<String, List<ItemNews>> getNews() throws InterruptedException {
        String path = Utils.getConfigFilesPath();
        ScraperConfiguration config = null;
        try {
            config = Crawler.getScraperConfig(null, path, Crawler.CrawlType.NEWS);
        } catch (FileNotFoundException ex) {
            System.err.println("Config file for news not found");;
        }
        Crawler crawler = new Crawler(config, null, Crawler.CrawlType.NEWS, null);
        crawler.start();
        crawler.join();

        List<ItemNews> newses = (List<ItemNews>) crawler.getParams().get("newses");
        Collections.reverse(newses);
        System.out.println("Size: " + newses.size());
        mergeNewses(newses);
        System.out.println("After merge size: " + newses.size());
        Map<String, List<ItemNews>> newsMap = new HashMap<>();
        Collections.sort(newses);
        for (ItemNews itemNews : newses) {
            EPSList epsList = null;
            try {
                epsList = parseEPS(itemNews.getNews());
//                if(itemNews.getCode().equals("1STPRIMFMF")){
//                    System.out.println("date: " + itemNews.getDate() + ", epsList: " + epsList);
//                }
                itemNews.setEpsList(epsList);
            } catch (NumberFormatException ex) {
                System.err.println("NumberFormatException: " + ex.getMessage());
                System.out.println("message: " + itemNews);
                throw ex;
            }
            if (epsList != null) {
                
                List<ItemNews> newsList;
                newsList = newsMap.get(itemNews.getCode());
                if(newsList == null)
                    newsList = new ArrayList<>();
                newsList.add(itemNews);
                newsMap.put(itemNews.getCode(), newsList);
                
                boolean goodEps = isGoodEps(epsList);
//                if (goodEps) {
//                    System.out.println("Date: " + itemNews.getDate() + ", code: " + itemNews.getCode() + ", " + epsList + ", goodEps: " + goodEps);
//                }
            }
        }
        
        return newsMap;
    }

    private static void mergeNewses(List<ItemNews> newses) {
        List<ItemNews> toBeRemove = new ArrayList<>();
        for (int i = 0; i < newses.size(); i++) {
            ItemNews itemNews1 = newses.get(i);
            for (int j = i + 1; j < newses.size(); j++) {
                ItemNews itemNews2 = newses.get(j);
                if (itemNews1.getDate().equals(itemNews2.getDate()) && itemNews1.getCode().equals(itemNews2.getCode())) {
                    String mergedNews = itemNews1.getNews() + "\n" + itemNews2.getNews();
                    itemNews1.setNews(mergedNews);
                    toBeRemove.add(itemNews2);
                }
            }
        }

        newses.removeAll(toBeRemove);
    }

    private static boolean isGoodEps(EPSList epsList) {
        float ratio1 = -1;
        float ratio2 = -1;
        float change = -1;

        if (epsList.getFirst() != 0 && epsList.getSecond() != 0) {
            ratio1 = getRatio(epsList.getFirst(), epsList.getSecond());
            change = ratio1;
        }

        if (epsList.getThird() != 0 && epsList.getFourth() != 0) {
            ratio2 = getRatio(epsList.getThird(), epsList.getFourth());
            change = Math.min(ratio1, ratio2);
        }

//        System.out.println("ratio1: " + ratio1 + ", ratio2: " + ratio2 + ", change: " + change);
        if (change >= 0) {
            return true;
        }

        return false;
    }

    private static float getRatio(float value1, float value2) {
        float diff = value1 - value2;
        float result = Math.abs(diff / value2);
        if (diff < 0) {
            result = -result;
        }
        return result;
    }

    private static void isTailFound() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getData(365);
        ScannerService scanerService = new ScannerService();
        String script = "BGIC";
        //List<Item> codes = Utils.getCodes();
        System.out.println("today: " + new java.sql.Date(Utils.today.getTime()));
        System.out.println("yesterday: " + new java.sql.Date(Utils.yesterday.getTime()));
        for (String code : oneYearData.keySet()) {
            if (code.equals(script)) {
                break;
            }

            List<Item> items = oneYearData.getItems(code);
            scanerService.calculateVolumePerTradeChange(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
            Collections.sort(items);

            float previousVolumeChange = 0;
            float previousTradeChange = 0;
            float previousYesterdayVolumePerTradeChange = 0;

            for (int i = 3; i < items.size(); i++) {
                Item today = items.get(i);
                Item yesterday = items.get(i - 1);
                Item dayBeforeYesterday = items.get(i - 2);
                Item towDayBeforeYesterday = items.get(i - 3);

                float tcYesterday = (float) yesterday.getTrade() / (float) dayBeforeYesterday.getTrade();
                float vcYesterday = (float) yesterday.getVolume() / (float) dayBeforeYesterday.getVolume();
                float vtcRatioYesterday = vcYesterday / tcYesterday;
                float yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
                float yesterdaychange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;
                float dayBeforeYesterdayChange = ((dayBeforeYesterday.getAdjustedClosePrice() - towDayBeforeYesterday.getAdjustedClosePrice()) / towDayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float yesterdayTrade = yesterday.getTrade();

                float tcToday = (float) today.getTrade() / (float) yesterday.getTrade();
                float vcToday = (float) today.getVolume() / (float) yesterday.getVolume();
                float vtcRatioToday = vcToday / tcToday;
                float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
                //priceGap is tuned for higher price stocks
                todayGap = todayGap + today.getAdjustedClosePrice() / 1000;
                float todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
                float todayTrade = today.getTrade();
                float todayValue = today.getValue();

                List<Item> itemSubList = new ArrayList();
                int counter = 0;
                for (int j = i; j >= 0; j--) {
                    itemSubList.add(items.get(j));
                    if (counter >= ScannerService.TRADING_DAYS_IN_A_MONTH) {
                        break;
                    }
                    ++counter;
                }
                Collections.sort(itemSubList);

                float volumePerTradeChange = today.getVolumePerTradeChange();
                float yesterdayVolumePerTradeChange = yesterday.getVolumePerTradeChange();                Item maximumVolumePerTradeChange = scanerService.getMaximumVolumePerTradeChange(items, i - 1, ScannerService.TRADING_DAYS_IN_A_MONTH);

                float volumeChange = scanerService.calculateVolumeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float tradeChange = scanerService.calculateTradeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float rsi = scanerService.calculateRSI(itemSubList);
                boolean isLastTwoDaysGreen = yesterdayGap > 0 && dayBeforeYesterdayGap > 0 && yesterdaychange > 0.5 && dayBeforeYesterdayChange > 0.5;
                float tradeChangeWithYesterday = ((float) today.getTrade() / (float) yesterday.getTrade());
                float volumeChangeWithYesterday = ((float) today.getVolume() / (float) yesterday.getVolume());
                boolean isSuddenHike = volumeChangeWithYesterday >= 2 && volumeChange >= 2;
                float diffWithPreviousLow10 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 10);
                float diffWithPreviousLow3 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 3);
                float diffWithPreviousHighVolume = scanerService.getVolumeDiffWithPreviousHigh(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float hammer = getHammer(today);
                scanerService.calculateDivergence(itemSubList);
                int divergence = itemSubList.get(itemSubList.size() - 1).getDivergence();
                boolean consecutive3DaysGreen = scanerService.isConsecutive3DaysGreen(itemSubList);
                float lastFiewDaysVariation = scanerService.getLastFiewDaysVariation(itemSubList, ScannerService.TRADING_DAYS_IN_A_WEEK);
                int upDayCount7 = scanerService.getUpDayCount(items, i, 7);
                int upDayCount4 = scanerService.getUpDayCount(items, i, 4);

                Item calculatedItem = new Item(today.getCode());
                calculatedItem.setDivergence(divergence);
                calculatedItem.setRSI(rsi);

                if (scanerService.isTailFoundYesterday(calculatedItem, itemSubList)) {
                    System.out.println("tDate: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday + ", diffWithPreviousLow: " + diffWithPreviousLow10 + ", rsi: " + rsi + ", hammer: " + hammer + ", divergence: " + divergence + ", diffWithPreviousHighVolume: " + diffWithPreviousHighVolume + ", lastFiewDaysVariation: " + lastFiewDaysVariation + ", maximumVolumePerTradeChange: " + maximumVolumePerTradeChange.getVolumePerTradeChange());
                }
            }
        }
    }

    private static EPSList parseEPS(String news) {
        //String news1 = "(Q2 Un-audited): EPS for April-June, 2015 was Tk. 0.78 as against Tk. 0.49 for April-June, 2014, EPS for Jan-June, 2015 was Tk. 1.61 as against Tk. 1.11 for Jan-June, 2014. NOCFPS was Tk. 2.65 for Jan-June, 2015 as against Tk. 1.34 for Jan-June, 2014. NAV per share was Tk. 23.87 as of June 30, 2015 and Tk. 27.09 as of June 30, 2014.";

        //System.out.println("news: " + news);
        int index = news.indexOf("EPS");
        if (index < 0) {
            index = news.toLowerCase().indexOf("earning per unit");
            if(index<0)
                return null;
        }

        EPSList epsList = new EPSList();

        index = news.indexOf("Tk.", index);
        if (index < 0) {
            return null;
        }

        String eps1 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
        epsList.setFirst(getFloatEPS(eps1));
        //System.out.println("eps1: " + eps1);

        index = news.indexOf("as against");
        if (index < 0) {
            return epsList;
        }
        index = news.indexOf("Tk.", index);
        int millionIndex = news.indexOf("million", index + 9);
        if (millionIndex < 0) {
            millionIndex = news.indexOf("m.", index + 9);
        }

        //System.out.println("index:: " + index + ", millionIndex: " + millionIndex + ", news: " + news);
        String eps2 = "0.0";

        if (millionIndex > 0 && millionIndex < index + 16) {
            index = news.indexOf("Tk.", millionIndex);
            eps2 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
        } else {
            eps2 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
        }
        epsList.setSecond(getFloatEPS(eps2));

        if (news.indexOf("considering proposed bonus") > 0) {
            index = news.indexOf("EPS", index);
            index = news.indexOf("Tk.", index);
            eps1 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();

            index = news.indexOf("and", index);
            index = news.indexOf("Tk.", index);
            millionIndex = news.indexOf("million", index + 9);
            if (millionIndex < 0) {
                millionIndex = news.indexOf("m.", index + 9);
            }

            //System.out.println("index: " + index + ", millionIndex: " + millionIndex + ", news: " + news);
            eps2 = "0.0";

            if (millionIndex > 0 && millionIndex < index + 14) {
                index = news.indexOf("Tk.", millionIndex);
                eps2 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            } else {
                eps2 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            }
            epsList.setSecond(getFloatEPS(eps2));
        }

        //System.out.println("eps1: " + eps1 + ", eps2: " + eps2);
        if (news.indexOf("Whereas", index) > 0) {
            index = news.indexOf("EPS", index);
            index = news.indexOf("Tk.", index);
            String eps3 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            epsList.setThird(getFloatEPS(eps3));

            index = news.indexOf("as against", index);
            index = news.indexOf("Tk.", index);
            millionIndex = news.indexOf("million", index + 9);
            if (millionIndex < 0) {
                millionIndex = news.indexOf("m.", index + 9);
            }

            //System.out.println(">> index: " + index + ", millionIndex: " + millionIndex);
            String eps4 = "0.0";

            if (millionIndex > 0 && millionIndex < index + 16) {
                index = news.indexOf("Tk.", millionIndex);
                eps4 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            } else {
                eps4 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            }
            epsList.setFourth(getFloatEPS(eps4));
            //System.out.println("eps3: " + eps3 + ", eps4: " + eps4);
        } else if (news.indexOf("EPS", index) > 0) {
            index = news.indexOf("EPS", index);
            index = news.indexOf("Tk.", index);
            if (index < 0) {
                return epsList;
            }
            String eps3 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            epsList.setThird(getFloatEPS(eps3));

            index = news.indexOf("as against", index);
            //System.out.println("here index: " + index);
            index = news.indexOf("Tk.", index);
            millionIndex = news.indexOf("million", index + 9);
            if (millionIndex < 0) {
                millionIndex = news.indexOf("m.", index + 9);
            }

            //System.out.println(">> index: " + index + ", millionIndex: " + millionIndex);
            String eps4 = "0.0";

            if (millionIndex > 0 && millionIndex < index + 14) {
                index = news.indexOf("Tk.", millionIndex);
                eps4 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            } else {
                eps4 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            }
            epsList.setFourth(getFloatEPS(eps4));
            //System.out.println("eps3: " + eps3 + ", eps4: " + eps4);
        }

        return epsList;
    }

    private static float getFloatEPS(String eps) {
        String epsString = eps;
        //System.out.println("eps:: " + eps);
        float actualEps;
        try {
            eps = eps.trim();
            eps = eps.split(",")[0].trim();
            if (eps.endsWith(".")) {
                eps = eps.substring(0, eps.length() - 1);
            }

            if (eps.startsWith("(") && eps.endsWith(")")) {
                eps = eps.substring(1);
                eps = eps.substring(0, eps.length() - 1);
                eps = eps.trim();
                actualEps = Float.parseFloat(eps);
                actualEps = -actualEps;
            } else {
                actualEps = Float.parseFloat(eps);
            }
        } catch (NumberFormatException ex) {
            System.out.println("eps: " + epsString);
            throw ex;
        }
        return actualEps;
    }

    private static void parseEPS() {
        String news1 = "(Q2 Un-audited): EPS for April-June, 2015 was Tk. 0.78 as against Tk. 0.49 for April-June, 2014, EPS for Jan-June, 2015 was Tk. 1.61 as against Tk. 1.11 for Jan-June, 2014. NOCFPS was Tk. 2.65 for Jan-June, 2015 as against Tk. 1.34 for Jan-June, 2014. NAV per share was Tk. 23.87 as of June 30, 2015 and Tk. 27.09 as of June 30, 2014.";
        String news2 = "(Q3): As per un-audited quarterly accounts for the 3rd quarter ended on 30th September 2014 (July'14 to Sep'14), the Company has reported net profit after tax of Tk. 81.49 million with EPS of Tk. 0.98 as against Tk. 90.00 million and Tk. 1.09 respectively for the same period of the previous year. Whereas net profit after tax was Tk. 218.12 million with EPS of Tk. 2.63 for the period of nine months (Jan'14 to Sep'14) ended on 30.09.2014 as against Tk. 271.97 million and Tk. 3.28 respectively for the same period of the previous year.";
        String news3 = "(Q1): As per un-audited quarterly accounts for the 1st quarter ended on 31st March 2014 (Jan'14 to March'14), the Company has reported net profit after tax of Tk. 76.91 million with basic EPS of Tk. 1.11 as against Tk. 90.54 million and Tk. 1.31 (restated) respectively for the same period of the previous year. However, considering proposed bonus share @ 20% for the year 2013, restated basic EPS will be Tk. 0.93 as on 31.03.2014 and Tk. 1.09 as on 31.03.2013.";
        String news4 = "(Q3-Unaudited): Net Profit/(Loss) after tax from Jan15 to March15 was Tk. (7.57) million with EPS of Tk. (1.33) as against Tk. 2.76 million and Tk. 0.48 respectively for the same period of the previous year. Whereas Net Profit after tax from July14 to March15 was Tk. 1.55 million with EPS of Tk. 0.27 as against Tk. 12.91 million and Tk. 2.26 respectively for the same period of the previous year.";
        String news5 = "(H/Y Un-audited): Net Profit after tax from July14 to Dec14 was Tk. 9.12 million with EPS Tk. 1.60 as against Tk. 10.15 million and Tk. 1.78 respectively for the same period of the previous year. Whereas net profit after tax from Oct14 to Dec14 was Tk. 3.95 million with EPS Tk. 0.69 as against Tk. 4.52 million and Tk. 0.79 respectively for the same period of the previous year.";
        String news6 = "(Q2 Un-audited): EPS for April-June, 2015 was Tk. 0.09 as against Tk. 0.25 for April-June, 2014, EPS for Jan-June, 2015 was Tk. 0.47 as against Tk. 0.69 for Jan-June, 2014. NOCFPS was Tk. 0.09 for Jan-June, 2015 as against Tk. 9.60 for Jan-June, 2014. NAV per share was Tk. 13.46 as of June 30, 2015 and Tk. 13.64 as of December 31, 2014.";
        String news7 = "(Q3): As per un-audited quarterly accounts for the 3rd quarter ended on 30th September 2014 (July'14 to Sep'14), the Company has reported net profit after tax of Tk. 28.66 million with EPS of Tk. 0.26 as against Tk. 74.55 million and Tk. 0.67 (restated) respectively for the same period of the previous year. Whereas net profit after tax was Tk. 105.15 million with EPS of Tk. 0.95 for the period of nine months (Jan'14 to Sep'14) ended on 30.09.2014 as against Tk. 155.64 million and Tk. 1.41 (restated) respectively for the same period of the previous year.";
        String news8 = "(Q3-Unaudited): Consolidated Net Profit after tax (excluding non-controlling interest) from Jan15-Mar15 was Tk. 33.51 m. with consolidated EPS of Tk. 0.41 as against Tk. 29.02 m. and Tk. 0.35 respectively for the same period of the previous year. Whereas consolidated Net Profit after tax from (excluding non-controlling interest) July14-Mar15 was Tk. 81.30 m. with consolidated EPS of Tk. 0.99 as against Tk. 94.95 m. and Tk. 1.16 respectively for the same period of the previous year.";
        String news9 = "(H/Y Un-audited): Consolidated net profit after tax (excluding non controlling interests) from July14 to Dec14 was Tk. 48.16 m. with consolidated EPS of Tk. 0.59 as against Tk. 66.06 m. and Tk. 0.81 respectively for the same period of the previous year. Whereas consolidated net profit after tax (excluding non controlling interests) from Oct14 to Dec14 was Tk. 13.88 m. with consolidated EPS of Tk. 0.17 as against Tk. 22.89 m. and Tk. 0.28 respectively for the same period of the previous year.";
        String news10 = "(Q3): As per un-audited quarterly accounts for the 3rd quarter ended on 31st March 2014 (Jan'14 to March'14), the Company has reported consolidated net profit after tax (excluding non-controlling interest) of Tk. 29.02 million with consolidated EPS of Tk. 0.35 as against Tk. 57.76 million and Tk. 0.71 (restated) respectively for the same period of the previous year. Whereas consolidated net profit after tax (excluding non-controlling interest) was Tk. 94.95 million with consolidated EPS of Tk. 1.16 for the period of nine months (July'13 to March'14) ended on 31.03.2014 as against Tk. 143.69 million and Tk. 1.75 (restated) respectively for the same period of the previous year.";
        String news11 = "(H/Y Un-audited): Profit after tax from Jan-15 to June-15 was Tk. 846.13 million with EPS of Tk. 14.97 as against Tk. 817.07 million and Tk. 14.46 respectively for the same period of the previous year. Whereas Profit after tax from April-15 to June-15 was Tk. 353.93 million with EPS of Tk. 6.26 as against Tk. 412.54 million and Tk. 7.30 respectively for the same period of the previous year.";
        String news12 = "(Q3): As per un-audited quarterly accounts for the 3rd quarter ended on 30th September 2014 (July'14 to Sep'14), the Company has reported profit after tax of Tk. 195.51 million with EPS of Tk. 3.46 as against Tk. 373.95 million and Tk. 6.62 respectively for the same period of the previous year. Whereas profit after tax was Tk. 1,012.58 million with EPS of Tk. 17.92 for the period of nine months (Jan'14 to Sep'14) ended on 30.09.2014 as against Tk. 1,236.73 million and Tk. 21.89 respectively for the same period of the previous year.";
        String news13 = "(Q2 Un-audited): EPS for April-June, 2015 was Tk. 1.31 as against Tk. 1.00 for April-June, 2014, EPS for Jan-June, 2015 was Tk. 2.41 as against Tk. 1.68 for Jan-June, 2014. NOCFPS was Tk. 2.24 for Jan-June, 2015 as against Tk. 3.85 for Jan-June, 2014. NAV per share was Tk. 26.89 as of June 30, 2015 and Tk. 29.40 as of June 30, 2014.";
        String news14 = "(Q1 Un-audited): Net Profit after tax from Jan15 to March15 was Tk. 24.50 million with basic EPS of Tk. 1.15 as against Tk. 16.77 million and Tk. 0.78 respectively for the same period of the previous year. However, considering proposed bonus share 5% for the year 2014, restated basic EPS will be Tk. 1.09 as on 31.03.2015 and Tk. 0.75 as on 31.03.2014.";
        String news15 = "(Q3 Un-audited): Consolidated EPS was Tk. (0.14) for Jan-Mar, 2015 as against Tk. 0.23 for July 2014-March, 2015; Consolidated NOCFPS was Tk. 0.22 for January-March, 2015. Consolidated NAV per share was Tk. 19.77 as of March 31, 2015 and Tk. 19.80 as of February 28, 2015.";
        String news16 = "(Q3-Unaudited): Net Profit after tax from Jan15 to March15 was Tk. 22.77 million with EPS of Tk. 0.41 as against Tk. 37.37 million and Tk. 0.98 respectively for the same period of the previous year. Whereas Net Profit after tax from July14 to March15 was Tk. 97.41 million with EPS of Tk. 1.74 as against Tk. 125.71 million and Tk. 3.44 respectively for the same period of the previous year.";
        String news17 = "(H/Y Un-audited): Consolidated Net Profit after tax (excluding non-controlling interests) from Jan-15 to June-15 was Tk. 3,395.24 m. with consolidated EPS of Tk. 2.11 as against Tk. 974.06 m. and Tk. 0.61 respectively for the same period of the previous year. Whereas consolidated Net Profit after tax (excluding non-controlling interests) from April-15 to June-15 was Tk. 2,994.46 m. with consolidated EPS of Tk. 1.86 as against Tk. 590.01 m. and Tk. 0.37 respectively for the same period of the previous year.";

        String news = news17;
        int index = news.indexOf("EPS");
        index = news.indexOf("Tk.", index);
        String eps1 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
        //System.out.println("eps1: " + eps1);

        index = news.indexOf("as against");
        index = news.indexOf("Tk.", index);
        int millionIndex = news.indexOf("million", index + 9);
        if (millionIndex < 0) {
            millionIndex = news.indexOf("m.", index + 9);
        }

        System.out.println("index: " + index + ", millionIndex: " + millionIndex);
        String eps2 = "0.0";

        if (millionIndex > 0 && millionIndex < index + 14) {
            index = news.indexOf("Tk.", millionIndex);
            eps2 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
        } else {
            eps2 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
        }

        if (news.indexOf("considering proposed bonus") > 0) {
            index = news.indexOf("EPS", index);
            index = news.indexOf("Tk.", index);
            eps1 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();

            index = news.indexOf("and", index);
            index = news.indexOf("Tk.", index);
            millionIndex = news.indexOf("million", index + 9);
            if (millionIndex < 0) {
                millionIndex = news.indexOf("m.", index + 9);
            }

            System.out.println("index: " + index + ", millionIndex: " + millionIndex);
            eps2 = "0.0";

            if (millionIndex > 0 && millionIndex < index + 14) {
                index = news.indexOf("Tk.", millionIndex);
                eps2 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            } else {
                eps2 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            }
        }

        System.out.println("eps1: " + eps1 + ", eps2: " + eps2);

        if (news.indexOf("Whereas", index) > 0) {
            index = news.indexOf("EPS", index);
            index = news.indexOf("Tk.", index);
            String eps3 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();

            index = news.indexOf("as against", index);
            index = news.indexOf("Tk.", index);
            millionIndex = news.indexOf("million", index + 9);
            if (millionIndex < 0) {
                millionIndex = news.indexOf("m.", index + 9);
            }

            System.out.println(">> index: " + index + ", millionIndex: " + millionIndex);
            String eps4 = "0.0";

            if (millionIndex > 0 && millionIndex < index + 14) {
                index = news.indexOf("Tk.", millionIndex);
                eps4 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            } else {
                eps4 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            }
            System.out.println("eps3: " + eps3 + ", eps4: " + eps4);
        } else if (news.indexOf("EPS", index) > 0) {
            index = news.indexOf("EPS", index);
            index = news.indexOf("Tk.", index);
            String eps3 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();

            index = news.indexOf("as against", index);
            //System.out.println("here index: " + index);
            index = news.indexOf("Tk.", index);
            millionIndex = news.indexOf("million", index + 9);
            if (millionIndex < 0) {
                millionIndex = news.indexOf("m.", index + 9);
            }

            System.out.println(">> index: " + index + ", millionIndex: " + millionIndex);
            String eps4 = "0.0";

            if (millionIndex > 0 && millionIndex < index + 14) {
                index = news.indexOf("Tk.", millionIndex);
                eps4 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            } else {
                eps4 = news.substring(index + 4, news.indexOf(" ", index + 5)).trim();
            }
            System.out.println("eps3: " + eps3 + ", eps4: " + eps4);
        }
    }

    private static Item buyItem;
    static DecimalFormat df = new DecimalFormat("#.#");
    static float summery = 0;
    static float maxGain = 0;
    static int counter = 0;
    static int profit = 0;
    static int loss = 0;
    static int totalBuy = 0;
    static int totalAvg = 0;
    static int totalSell = 0;
    float TotalGain = 0;
    static float lastMonthVariation = 0;
    static long totalTenure = 0;
    static Calendar upto = Calendar.getInstance();

    private static void doTrade(Item item, Item yesterDayItem, String cause, float lastGreenMinimum) {

        float todayGap = ((item.getAdjustedClosePrice() - item.getOpenPrice()) / item.getOpenPrice()) * 100;
        todayGap = todayGap + item.getAdjustedClosePrice() / 1000;

        if (item.getSignal().equals(Item.SignalType.BUY) && !item.getDate().after(upto.getTime())) {

            if (buyItem != null && buyItem.getCode().equals(item.getCode())) {
                return;
            }

            buyItem = item;
            buyItem.setAdjustedClosePrice(buyItem.getAdjustedClosePrice() * 1.005f);
            ++totalBuy;
            System.out.println("");
            System.out.print(item.getCode() + " on " + item.getDate() + ", price: " + item.getAdjustedClosePrice() + ", cause: " + cause + "(t:" + df.format(item.getTradeChange()) + ", v:" + df.format(item.getVolumeChange()) + ")" + " ---- ");
        }

        if (item.getSignal().equals(Item.SignalType.SELL)) {
            if (buyItem != null && (buyItem.getDate().before(yesterDayItem.getDate()) || cause.equals("EOM"))) {
                float gain = ((item.getAdjustedClosePrice() - buyItem.getAdjustedClosePrice()) / buyItem.getAdjustedClosePrice()) * 100 - 0.5f;

                boolean belowBothSma = false;
                float sma25 = item.getSmaList().get(25);
                float sma10 = item.getSmaList().get(10);
                float minSma = Math.min(item.getSmaList().get(10), item.getSmaList().get(25));
                float diffWithMinSma = ((item.getAdjustedClosePrice() - minSma) / minSma) * 100;
                float diffWithSma10 = ((item.getAdjustedClosePrice() - sma10) / sma10) * 100;
                float diffWithSma25 = ((item.getAdjustedClosePrice() - sma25) / sma25) * 100;
                boolean belowSma10 = false;
                boolean belowSma25 = false;

                if (diffWithSma10 < -3) {
                    belowSma10 = true;
                }

                if (diffWithSma25 < 0) {
                    belowSma25 = true;
                }

                if (diffWithMinSma < -5) {
                    //if(item.getAdjustedClosePrice()<item.getSmaList().get(10) && item.getAdjustedClosePrice()<item.getSmaList().get(25)){
                    belowBothSma = true;
                    //System.out.println("Must sell " + item.getCode() + " on " + item.getDate());
                }

                //Do no sell
                boolean endOfMarket = cause.equalsIgnoreCase("EOM");
                Item dsex = dsexMap.get(item.getDate());
                boolean final2 = dsex.getClosePrice() < dsex.getSmaList().get(10) && dsex.getClosePrice() < dsex.getSmaList().get(25);
                boolean marketIsDown = final2 && dsex.getAdjustedClosePrice() < dsex.getYesterdayClosePrice();
                boolean var1 = item.getAdjustedClosePrice() >= buyItem.getOpenPrice() && item.getAdjustedClosePrice() < sma25;
                //System.out.println("code: " + item.getCode() + ", date: " + item.getDate() + ", cause: " + cause + ", belowSma10: " + belowSma10 + ", gain: " + gain + ", todayGap: " + todayGap + ", endOfMarket: " + endOfMarket);
                if ((((gain > -5) && gain < 3) || gain < -10 || (!belowSma10 && gain > 5) || (gain < 0 && belowSma25)) && !endOfMarket && todayGap > -5) {
                    //System.out.println("on " + item.getDate() + " denied1");
                    return;
                }

//                if(gain<0 && item.getAdjustedClosePrice()>=buyItem.getOpenPrice())
//                    return;
                if (gain < 0 && dsex.getRSI() <= 30 && !endOfMarket) {
                    //System.out.println("on " + item.getDate() + " denied2");
                    return;
                }

//                if(item.getAdjustedClosePrice()>= buyItem.getOpenPrice() && item.getAdjustedClosePrice()<sma25)
//                    return;
                summery += gain;
                System.out.print(item.getDate() + ", price: " + item.getAdjustedClosePrice() + ", cause: " + cause + ", gain: " + df.format(gain) + "%");
                buyItem = null;

                if (gain > 0) {
                    ++profit;
                } else {
                    ++loss;
                }
            }
        }

    }

    private static float getLastGreenMinimum(List<Item> items) {
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

    private static boolean isSellSignal(ScannerService scanerService, Item calculatedItem, List<Item> items) {
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
        todayPriceGap = todayPriceGap + today.getAdjustedClosePrice() / 1000;
        float yesterdayPriceChange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
        float yesterdayPriceGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
        float dayBeforeYesterdayPriceGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;

        float sma10 = scanerService.calculateSMA(items, 10);
        float sma25 = scanerService.calculateSMA(items, 25);
        today.getSmaList().put(10, sma10);
        today.getSmaList().put(25, sma25);
        Item dsex = dsexMap.get(today.getDate());
        boolean final1 = today.getAdjustedClosePrice() < sma10 && today.getAdjustedClosePrice() < sma25;
        boolean final2 = dsex.getClosePrice() < dsex.getSmaList().get(10) && dsex.getClosePrice() < dsex.getSmaList().get(25);
        float lastGreenMinimum = getLastGreenMinimum(items);

        boolean twoConsecutiveRed = false;
        if (((todayPriceChange <= -0.5 && yesterdayPriceChange <= -1.0) || (todayPriceChange <= -1.0 && yesterdayPriceChange <= -0.5)) && todayPriceGap < 0) {
            twoConsecutiveRed = true;
        }

        //@1. Should have two consecutive red (among them at least one day open-close should be more than 1%) and close price less than day before yesterday open price
        //System.out.println(today.getDate() + ", twoConsecutiveRed: " + twoConsecutiveRed + ", issell: " + isSellCandidate(calculatedItem, items));
        if (twoConsecutiveRed && today.getAdjustedClosePrice() < dayBeforeYesterday.getOpenPrice() && isSellCandidate(calculatedItem, items)) {
            //System.out.println("Sell1 Date: " + today.getDate() + ", code: " + today.getCode());
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell1", lastGreenMinimum);
            return true;
        }

        boolean threeConsecutiveRed = false;
        if (todayPriceGap <= 0 && yesterdayPriceGap <= 0 && dayBeforeYesterdayPriceGap <= 0) {
            threeConsecutiveRed = true;
        }

//        if (threeConsecutiveRed && isSellCandidate(calculatedItem, items)) {
//            //System.out.println("Sell1.5 Date: " + today.getDate() + ", code: " + today.getCode());
//            today.setSignal(Item.SignalType.SELL);
//            doTrade(today, yesterday, "Sell1.5");
//            return true;
//        }
        //@2. Close price less than buy day open price
        //Should be implemented later
        //@3. Open price - close price is more than 6% 
        if (todayPriceGap < -6) {
            //System.out.println("Sell3 Date: " + today.getDate() + ", code: " + today.getCode());
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell3", lastGreenMinimum);
            return true;
        }

        //@4. close price dropped to more than 6% of previous close price
        if (todayPriceChange < -6) {
            //System.out.println("Sell4 Date: " + today.getDate() + ", code: " + today.getCode());
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell4", lastGreenMinimum);
            return true;
        }

        //@5. Today is down and yesterday was hammer<-2
        if (todayPriceGap < 0 && getDBHammer(yesterday) < -2) {
            //System.out.println("Sell5 Date: " + today.getDate() + ", code: " + today.getCode());
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell5", lastGreenMinimum);
            return true;
        }

        //@5.5. Today is down and today is hammer<-3
        if (todayPriceGap < 0 && getDBHammer(today) < -3) {
            //System.out.println("Sell5 Date: " + today.getDate() + ", code: " + today.getCode());
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell5.5", lastGreenMinimum);
            return true;
        }

        //@6. Today is down and is less than 3% and RSI > 70
        if (todayPriceGap < -3 && calculatedItem.getRSI() > 70) {
            //System.out.println("Sell6 Date: " + today.getDate() + ", code: " + today.getCode());
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell6", lastGreenMinimum);
            return true;
        }

        //@7. Today is down and pressure <-10
        if (todayPriceGap < 0 && calculatedItem.getPressure() < -10) {
            //System.out.println("Sell7 Date: " + today.getDate() + ", code: " + today.getCode());
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell7", lastGreenMinimum);
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
            //System.out.println("Sell8 Date: " + today.getDate() + ", code: " + today.getCode());
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell8", lastGreenMinimum);
            return true;
        }

        // @9. Today close price less than yesterday minimum and less than day before yesterday minimum
        if (todayPriceGap < 0 && today.getAdjustedClosePrice() < yesterdayLower && today.getAdjustedClosePrice() < dayBeforeYesterdayLower) {
            //System.out.println("Sell9 Date: " + today.getDate() + ", code: " + today.getCode());
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell9", lastGreenMinimum);
            return true;
        }

        // @10. any of last 3 days RSI is 70+ and today is not green nor up
        boolean rsi70 = calculatedItem.getRSI() >= 70 || calculatedItem.getYesterdayRSI() >= 70 || calculatedItem.getDayBeforeYesterdayRSI() >= 70;
        //System.out.println("code: " + today.getCode() + ", date: " + today.getDate() + ", rsi70: " + rsi70 + ", calculatedItem.getRSI(): " + calculatedItem.getRSI() + ", calculatedItem.getDayBeforeYesterdayRSI(): " + calculatedItem.getDayBeforeYesterdayRSI() + ", daybefore: " + dayBeforeYesterday.getDate());
        if (rsi70 && (todayPriceGap <= 0 || todayPriceChange <= 0)) {
            //System.out.println("Sell10 Date: " + today.getDate() + ", code: " + today.getCode());
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell10", lastGreenMinimum);
            return true;
        }

        //If price falls bellow both sma10 and sma25 either for item or for dsex
        if (todayPriceChange < 0 && todayPriceGap < 0 && (final1 && final2)) {
            //System.out.println("final1: " + final1 + ", final2: " + final2);
            today.setSignal(Item.SignalType.SELL);
            doTrade(today, yesterday, "Sell11", lastGreenMinimum);
            return true;
        }

        return false;
    }

    private static boolean isSellCandidate(Item calculatedItem, List<Item> items) {
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

    private static Item getDSEXIndex(CustomHashMap oneYearData, Date date, ScannerService scanerService) {
        List<Item> items = oneYearData.getItems("DSEX");
        List<Item> itemSubList = new ArrayList();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getDate().after(date)) {
                break;
            }
            itemSubList.add(item);
        }
        Collections.sort(itemSubList);

        float sma10 = scanerService.calculateSMA(itemSubList, 10);
        float sma25 = scanerService.calculateSMA(itemSubList, 25);
        float rsi = scanerService.calculateRSI(itemSubList);
        Item dsex = itemSubList.get(itemSubList.size() - 1);
        dsex.getSmaList().put(10, sma10);
        dsex.getSmaList().put(25, sma25);
        dsex.setRSI(rsi);
        itemSubList.remove(itemSubList.size() - 1);
        rsi = scanerService.calculateRSI(itemSubList);
        dsex.setYesterdayRSI(rsi);
        itemSubList.remove(itemSubList.size() - 1);
        rsi = scanerService.calculateRSI(itemSubList);
        dsex.setDayBeforeYesterdayRSI(rsi);
        return dsex;
    }

    private static void checkByScript4() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getData(365);
        ScannerService scanerService = new ScannerService();
        String script = "ICB";
        //List<Item> codes = Utils.getCodes();
        for (String code : oneYearData.keySet()) {
//            if (!code.equals(script)) {
//                continue;
//            }

            List<Item> items = oneYearData.getItems(code);
            scanerService.calculateVolumePerTradeChange(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
            Collections.sort(items);

            float previousVolumeChange = 0;
            float previousTradeChange = 0;
            float previousYesterdayVolumePerTradeChange = 0;

            for (int i = 50; i < items.size(); i++) {
                Item today = items.get(i);
                Item yesterday = items.get(i - 1);
                Item dayBeforeYesterday = items.get(i - 2);
                Item towDayBeforeYesterday = items.get(i - 3);
                Item oneWeekAgo = items.get(i - ScannerService.TRADING_DAYS_IN_A_WEEK);
                Item twoWeekAgo = items.get(i - ScannerService.TRADING_DAYS_IN_A_WEEK * 2);

                float tcYesterday = (float) yesterday.getTrade() / (float) dayBeforeYesterday.getTrade();
                float vcYesterday = (float) yesterday.getVolume() / (float) dayBeforeYesterday.getVolume();
                float vtcRatioYesterday = vcYesterday / tcYesterday;
                float yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
                float yesterdaychange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;
                float dayBeforeYesterdayChange = ((dayBeforeYesterday.getAdjustedClosePrice() - towDayBeforeYesterday.getAdjustedClosePrice()) / towDayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float yesterdayTrade = yesterday.getTrade();

                float tcToday = (float) today.getTrade() / (float) yesterday.getTrade();
                float vcToday = (float) today.getVolume() / (float) yesterday.getVolume();
                float vtcRatioToday = vcToday / tcToday;
                float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
                //priceGap is tuned for higher price stocks
                todayGap = todayGap + today.getAdjustedClosePrice() / 1000;
                float todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
                float todayTrade = today.getTrade();
                float todayValue = today.getValue();

                List<Item> itemSubList = new ArrayList();
                int counter = 0;
                for (int j = i; j >= 0; j--) {
                    itemSubList.add(items.get(j));
//                    if (counter >= ScannerService.TRADING_DAYS_IN_2_MONTH) {
//                        break;
//                    }
                    ++counter;
                }
                Collections.sort(itemSubList);

                float volumePerTradeChange = today.getVolumePerTradeChange();
                float yesterdayVolumePerTradeChange = yesterday.getVolumePerTradeChange();
                Item maximumVolumePerTradeChange = scanerService.getMaximumVolumePerTradeChange(items, i - 1, ScannerService.TRADING_DAYS_IN_A_MONTH);

                float volumeChange = scanerService.calculateVolumeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float tradeChange = scanerService.calculateTradeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float rsi = scanerService.calculateRSI(itemSubList);
                boolean isLastTwoDaysGreen = yesterdayGap > 0 && dayBeforeYesterdayGap > 0 && yesterdaychange > 0.5 && dayBeforeYesterdayChange > 0.5;
                float tradeChangeWithYesterday = ((float) today.getTrade() / (float) yesterday.getTrade());
                float volumeChangeWithYesterday = ((float) today.getVolume() / (float) yesterday.getVolume());
                boolean isSuddenHike = volumeChangeWithYesterday >= 2 && volumeChange >= 2;
                float diffWithPreviousLow10 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 10);
                float diffWithPreviousLow3 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 3);
                float diffWithPreviousHighVolume = scanerService.getVolumeDiffWithPreviousHigh(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float hammer = getHammer(today);
                scanerService.calculateDivergence(itemSubList);
                int divergence = itemSubList.get(itemSubList.size() - 1).getDivergence();

                float todaySignalLine = 0;
                Object todaySignalLineObject = itemSubList.get(itemSubList.size() - 1).getEmaList().get(9);
                if (todaySignalLineObject != null) {
                    todaySignalLine = (float) todaySignalLineObject;
                }

                float dayBeforeYesterdaySignalLine = 0;
                Object dayBeforeYesterdaySignalLineObject = dayBeforeYesterday.getEmaList().get(9);
                if (dayBeforeYesterdaySignalLineObject != null) {
                    dayBeforeYesterdaySignalLine = (float) dayBeforeYesterdaySignalLineObject;
                }

                float oneWeekAgoSignalLine = 0;
                Object oneWeekAgoSignalLineObject = itemSubList.get(itemSubList.size() - ScannerService.TRADING_DAYS_IN_A_WEEK).getEmaList().get(9);
                if (oneWeekAgoSignalLineObject != null) {
                    oneWeekAgoSignalLine = (float) oneWeekAgoSignalLineObject;
                }

                //float twoWeekAgoSignalLine = itemSubList.get(itemSubList.size() - ScannerService.TRADING_DAYS_IN_A_WEEK*2).getEmaList().get(9);
                boolean consecutive3DaysGreen = scanerService.isConsecutive3DaysGreen(itemSubList);
                float lastFiewDaysVariation = scanerService.getLastFiewDaysVariation(itemSubList, ScannerService.TRADING_DAYS_IN_A_WEEK);
                int upDayCount7 = scanerService.getUpDayCount(items, i, 7);
                int upDayCount4 = scanerService.getUpDayCount(items, i, 4);
                int subListSize = itemSubList.size();
                float todayDiv = (itemSubList.get(subListSize - 1).getEmaList().get(12) - itemSubList.get(subListSize - 1).getEmaList().get(26)) - itemSubList.get(subListSize - 1).getEmaList().get(9);
                float yesterdayDiv = (itemSubList.get(subListSize - 2).getEmaList().get(12) - itemSubList.get(subListSize - 2).getEmaList().get(26)) - itemSubList.get(subListSize - 2).getEmaList().get(9);
                float dayBeforeYesterdayDiv = (itemSubList.get(subListSize - 3).getEmaList().get(12) - itemSubList.get(subListSize - 3).getEmaList().get(26)) - itemSubList.get(subListSize - 3).getEmaList().get(9);
                Item test = itemSubList.get(itemSubList.size() - 1);
                float macd = test.getEmaList().get(12) - test.getEmaList().get(26);
                float signalLine = test.getEmaList().get(9);

                if ((todaychange >= 0.5 && todayGap >= 0.5) && (yesterdaychange >= 1 || yesterdayGap >= 0.5)
                        && todayDiv > yesterdayDiv
                        && yesterdayDiv > dayBeforeYesterdayDiv
                        && todaySignalLine <= 0
                        && macd <= todaySignalLine
                        && diffWithPreviousLow10 < 9
                        && todayValue >= 1
                        && todayTrade >= 50
                        && volumeChange >= 0.3) {
                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday + ", diffWithPreviousLow: " + diffWithPreviousLow10 + ", rsi: " + rsi + ", hammer: " + hammer + ", divergence: " + divergence + ", diffWithPreviousHighVolume: " + diffWithPreviousHighVolume + ", lastFiewDaysVariation: " + lastFiewDaysVariation + ", maximumVolumePerTradeChange: " + maximumVolumePerTradeChange.getVolumePerTradeChange());
                }
            }
        }
    }

    private static void checkByScript6() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getData(365);
        ScannerService scanerService = new ScannerService();
        String script = "CVOPRL";
        //List<Item> codes = Utils.getCodes();
        for (String code : oneYearData.keySet()) {
//            if (!code.equals(script)) {
//                continue;
//            }

            List<Item> items = oneYearData.getItems(code);
            scanerService.calculateVolumePerTradeChange(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
            Collections.sort(items);

            float previousVolumeChange = 0;
            float previousTradeChange = 0;
            float previousYesterdayVolumePerTradeChange = 0;

            for (int i = 50; i < items.size(); i++) {
                Item today = items.get(i);
                Item yesterday = items.get(i - 1);
                Item dayBeforeYesterday = items.get(i - 2);
                Item towDayBeforeYesterday = items.get(i - 3);
                Item oneWeekAgo = items.get(i - ScannerService.TRADING_DAYS_IN_A_WEEK);
                Item twoWeekAgo = items.get(i - ScannerService.TRADING_DAYS_IN_A_WEEK * 2);

                float tcYesterday = (float) yesterday.getTrade() / (float) dayBeforeYesterday.getTrade();
                float vcYesterday = (float) yesterday.getVolume() / (float) dayBeforeYesterday.getVolume();
                float vtcRatioYesterday = vcYesterday / tcYesterday;
                float yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
                float yesterdaychange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;
                float dayBeforeYesterdayChange = ((dayBeforeYesterday.getAdjustedClosePrice() - towDayBeforeYesterday.getAdjustedClosePrice()) / towDayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float yesterdayTrade = yesterday.getTrade();

                float tcToday = (float) today.getTrade() / (float) yesterday.getTrade();
                float vcToday = (float) today.getVolume() / (float) yesterday.getVolume();
                float vtcRatioToday = vcToday / tcToday;
                float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
                //priceGap is tuned for higher price stocks
                todayGap = todayGap + today.getAdjustedClosePrice() / 1000;
                float todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
                float todayTrade = today.getTrade();
                float todayValue = today.getValue();

                List<Item> itemSubList = new ArrayList();
                int counter = 0;
                for (int j = i; j >= 0; j--) {
                    itemSubList.add(items.get(j));
//                    if (counter >= ScannerService.TRADING_DAYS_IN_2_MONTH) {
//                        break;
//                    }
                    ++counter;
                }
                Collections.sort(itemSubList);

                float volumePerTradeChange = today.getVolumePerTradeChange();
                float yesterdayVolumePerTradeChange = yesterday.getVolumePerTradeChange();
                Item maximumVolumePerTradeChange = scanerService.getMaximumVolumePerTradeChange(items, i - 1, ScannerService.TRADING_DAYS_IN_A_MONTH);

                float volumeChange = scanerService.calculateVolumeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float tradeChange = scanerService.calculateTradeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float rsi = scanerService.calculateRSI(itemSubList);
                boolean isLastTwoDaysGreen = yesterdayGap > 0 && dayBeforeYesterdayGap > 0 && yesterdaychange > 0.5 && dayBeforeYesterdayChange > 0.5;
                float tradeChangeWithYesterday = ((float) today.getTrade() / (float) yesterday.getTrade());
                float volumeChangeWithYesterday = ((float) today.getVolume() / (float) yesterday.getVolume());
                boolean isSuddenHike = volumeChangeWithYesterday >= 2 && volumeChange >= 2;
                float diffWithPreviousLow10 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 10);
                float diffWithPreviousLow3 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 3);
                float diffWithPreviousHighVolume = scanerService.getVolumeDiffWithPreviousHigh(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float hammer = getHammer(today);

                scanerService.calculateDivergence(itemSubList);
                float sma25 = scanerService.calculateSMA(itemSubList, 25);
                //System.out.println("today: " + today.getDate() + ", sma25: " + sma25);

                int divergence = itemSubList.get(itemSubList.size() - 1).getDivergence();

                float todaySignalLine = 0;
                Object todaySignalLineObject = itemSubList.get(itemSubList.size() - 1).getEmaList().get(9);
                if (todaySignalLineObject != null) {
                    todaySignalLine = (float) todaySignalLineObject;
                }

                float dayBeforeYesterdaySignalLine = 0;
                Object dayBeforeYesterdaySignalLineObject = dayBeforeYesterday.getEmaList().get(9);
                if (dayBeforeYesterdaySignalLineObject != null) {
                    dayBeforeYesterdaySignalLine = (float) dayBeforeYesterdaySignalLineObject;
                }

                float oneWeekAgoSignalLine = 0;
                Object oneWeekAgoSignalLineObject = itemSubList.get(itemSubList.size() - ScannerService.TRADING_DAYS_IN_A_WEEK).getEmaList().get(9);
                if (oneWeekAgoSignalLineObject != null) {
                    oneWeekAgoSignalLine = (float) oneWeekAgoSignalLineObject;
                }

                //float twoWeekAgoSignalLine = itemSubList.get(itemSubList.size() - ScannerService.TRADING_DAYS_IN_A_WEEK*2).getEmaList().get(9);
                boolean consecutive3DaysGreen = scanerService.isConsecutive3DaysGreen(itemSubList);
                float lastFiewDaysVariation = scanerService.getLastFiewDaysVariation(itemSubList, ScannerService.TRADING_DAYS_IN_A_WEEK);
                int upDayCount7 = scanerService.getUpDayCount(items, i, 7);
                int upDayCount4 = scanerService.getUpDayCount(items, i, 4);
                int subListSize = itemSubList.size();
                float todayDiv = (itemSubList.get(subListSize - 1).getEmaList().get(12) - itemSubList.get(subListSize - 1).getEmaList().get(26)) - itemSubList.get(subListSize - 1).getEmaList().get(9);
                float yesterdayDiv = (itemSubList.get(subListSize - 2).getEmaList().get(12) - itemSubList.get(subListSize - 2).getEmaList().get(26)) - itemSubList.get(subListSize - 2).getEmaList().get(9);
                float dayBeforeYesterdayDiv = (itemSubList.get(subListSize - 3).getEmaList().get(12) - itemSubList.get(subListSize - 3).getEmaList().get(26)) - itemSubList.get(subListSize - 3).getEmaList().get(9);
                Item test = itemSubList.get(itemSubList.size() - 1);
                float macd = test.getEmaList().get(12) - test.getEmaList().get(26);
                float signalLine = test.getEmaList().get(9);

                if ((todaychange >= 0.5 && todayGap >= 0.5)
                        //&& todayDiv > yesterdayDiv
                        //&& yesterdayDiv > dayBeforeYesterdayDiv
                        //&& todaySignalLine <= 0
                        //&& macd <= todaySignalLine
                        //&& diffWithPreviousLow10 < 9
                        && todayValue >= 1
                        && todayTrade >= 50
                        && volumeChange >= 0.4
                        && today.getOpenPrice() <= sma25 && today.getAdjustedClosePrice() > sma25) {
                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", diffWithPreviousLow: " + diffWithPreviousLow10 + ", rsi: " + rsi + ", hammer: " + hammer + ", divergence: " + divergence + ", diffWithPreviousHighVolume: " + diffWithPreviousHighVolume + ", lastFiewDaysVariation: " + lastFiewDaysVariation + ", maximumVolumePerTradeChange: " + maximumVolumePerTradeChange.getVolumePerTradeChange());
                }
            }
        }
    }

    private static void checkByScript3() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getData(365);
        ScannerService scanerService = new ScannerService();
        String script = "PREMIERCEM";
        //List<Item> codes = Utils.getCodes();
        for (String code : oneYearData.keySet()) {
//            if (!code.equals(script)) {
//                continue;
//            }

            List<Item> items = oneYearData.getItems(code);
            scanerService.calculateVolumePerTradeChange(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
            Collections.sort(items);

            float previousVolumeChange = 0;
            float previousTradeChange = 0;
            float previousYesterdayVolumePerTradeChange = 0;

            for (int i = 3; i < items.size(); i++) {
                Item today = items.get(i);
                Item yesterday = items.get(i - 1);
                Item dayBeforeYesterday = items.get(i - 2);
                Item towDayBeforeYesterday = items.get(i - 3);

                float tcYesterday = (float) yesterday.getTrade() / (float) dayBeforeYesterday.getTrade();
                float vcYesterday = (float) yesterday.getVolume() / (float) dayBeforeYesterday.getVolume();
                float vtcRatioYesterday = vcYesterday / tcYesterday;
                float yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
                float yesterdaychange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;
                float dayBeforeYesterdayChange = ((dayBeforeYesterday.getAdjustedClosePrice() - towDayBeforeYesterday.getAdjustedClosePrice()) / towDayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float yesterdayTrade = yesterday.getTrade();

                float tcToday = (float) today.getTrade() / (float) yesterday.getTrade();
                float vcToday = (float) today.getVolume() / (float) yesterday.getVolume();
                float vtcRatioToday = vcToday / tcToday;
                float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
                //priceGap is tuned for higher price stocks
                todayGap = todayGap + today.getAdjustedClosePrice() / 1000;
                float todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
                float todayTrade = today.getTrade();
                float todayValue = today.getValue();

//                List<Item> itemSubList = new ArrayList();
//                long totalTrade = 0;
//                long totalVolume = 0;
//                float yesterdayVolumePerTradeChange = 0;
//                for (int j = 0; j <= i; j++) {
//                    if (j == i) {
//                        float averageVolumePerTrade = (float) (totalVolume / totalTrade);
//                        float yesterdayVolumePerTrade = yesterday.getAdjustedVolume() / yesterday.getTrade();
//                        yesterdayVolumePerTradeChange = yesterdayVolumePerTrade / averageVolumePerTrade;
//                    }
//                    totalTrade += items.get(j).getTrade();
//                    totalVolume += items.get(j).getAdjustedVolume();
//                    itemSubList.add(items.get(j));
//                }
                List<Item> itemSubList = new ArrayList();
                int counter = 0;
                for (int j = i; j >= 0; j--) {
                    itemSubList.add(items.get(j));
                    if (counter >= ScannerService.TRADING_DAYS_IN_A_MONTH) {
                        break;
                    }
                    ++counter;
                }
                Collections.sort(itemSubList);

//                float averageVolumePerTrade = (totalVolume / totalTrade);
//                float todayVolumePerTrade = today.getAdjustedVolume() / today.getTrade();
//                float volumePerTradeChange = todayVolumePerTrade / averageVolumePerTrade;
                //if(today.getCode().equals("WATACHEM"))
                //    System.out.println("i: " + i + ", todayHead: " + today.getDate() + ", subitemHead: " + itemSubList.get(itemSubList.size()-1).getDate());
//                ChangesInVolumePerTrade changes = ChangesInVolumePerTrade.getChangesInVolumePerTrade(itemSubList, ScannerService.TRADING_DAYS_IN_2_MONTH);
//                float volumePerTradeChange = changes.getTodayChange();
//                float yesterdayVolumePerTradeChange = changes.getYesterdayChange();
                float volumePerTradeChange = today.getVolumePerTradeChange();
                float yesterdayVolumePerTradeChange = yesterday.getVolumePerTradeChange();
                Item maximumVolumePerTradeChange = scanerService.getMaximumVolumePerTradeChange(items, i - 1, ScannerService.TRADING_DAYS_IN_A_MONTH);

                float volumeChange = scanerService.calculateVolumeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float tradeChange = scanerService.calculateTradeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float rsi = scanerService.calculateRSI(itemSubList);
                boolean isLastTwoDaysGreen = yesterdayGap > 0 && dayBeforeYesterdayGap > 0 && yesterdaychange > 0.5 && dayBeforeYesterdayChange > 0.5;
                float tradeChangeWithYesterday = ((float) today.getTrade() / (float) yesterday.getTrade());
                float volumeChangeWithYesterday = ((float) today.getVolume() / (float) yesterday.getVolume());
                boolean isSuddenHike = volumeChangeWithYesterday >= 2 && volumeChange >= 2;
                float diffWithPreviousLow10 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 10);
                float diffWithPreviousLow3 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 3);
                float diffWithPreviousHighVolume = scanerService.getVolumeDiffWithPreviousHigh(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float hammer = getHammer(today);
                scanerService.calculateDivergence(itemSubList);
                int divergence = itemSubList.get(itemSubList.size() - 1).getDivergence();
                boolean consecutive3DaysGreen = scanerService.isConsecutive3DaysGreen(itemSubList);
                float lastFiewDaysVariation = scanerService.getLastFiewDaysVariation(itemSubList, ScannerService.TRADING_DAYS_IN_A_WEEK);
                int upDayCount7 = scanerService.getUpDayCount(items, i, 7);
                int upDayCount4 = scanerService.getUpDayCount(items, i, 4);
                if (((todaychange >= 0.5 && todayGap >= 0.5) && (yesterdaychange >= 1 || yesterdayGap >= 0.5) && (todaychange + yesterdaychange) > 1)
                        && divergence <= -5
                        && rsi <= 45
                        && todayValue >= 1
                        && todayTrade >= 50
                        && volumePerTradeChange < 1.8
                        && Math.min(todayGap, yesterdayGap) > -3
                        && diffWithPreviousLow10 < 9 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                        ) {
                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday + ", diffWithPreviousLow: " + diffWithPreviousLow10 + ", rsi: " + rsi + ", hammer: " + hammer + ", divergence: " + divergence + ", diffWithPreviousHighVolume: " + diffWithPreviousHighVolume + ", lastFiewDaysVariation: " + lastFiewDaysVariation + ", maximumVolumePerTradeChange: " + maximumVolumePerTradeChange.getVolumePerTradeChange());
                }
            }
        }
    }

    private static void checkByScript() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getData(365);
        ScannerService scanerService = new ScannerService();
        String script = "MARICO";
        //List<Item> codes = Utils.getCodes();
        for (String code : oneYearData.keySet()) {
//            if (!code.equals(script)) {
//                continue;
//            }

            List<Item> items = oneYearData.getItems(code);
            scanerService.calculateVolumePerTradeChange(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
            Collections.sort(items);

            float previousVolumeChange = 0;
            float previousTradeChange = 0;
            float previousYesterdayVolumePerTradeChange = 0;

            for (int i = 50; i < items.size(); i++) {
                Item today = items.get(i);
                Item yesterday = items.get(i - 1);
                Item dayBeforeYesterday = items.get(i - 2);
                Item towDayBeforeYesterday = items.get(i - 3);

                float tcYesterday = (float) yesterday.getTrade() / (float) dayBeforeYesterday.getTrade();
                float vcYesterday = (float) yesterday.getVolume() / (float) dayBeforeYesterday.getVolume();
                float vtcRatioYesterday = vcYesterday / tcYesterday;
                float yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
                float yesterdaychange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;
                float dayBeforeYesterdayChange = ((dayBeforeYesterday.getAdjustedClosePrice() - towDayBeforeYesterday.getAdjustedClosePrice()) / towDayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float yesterdayTrade = yesterday.getTrade();

                float tcToday = (float) today.getTrade() / (float) yesterday.getTrade();
                float vcToday = (float) today.getVolume() / (float) yesterday.getVolume();
                float vtcRatioToday = vcToday / tcToday;
                float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
                //priceGap is tuned for higher price stocks
                float sign = todayGap / Math.abs(todayGap);
                todayGap = todayGap + (today.getAdjustedClosePrice() / 1000) * sign;
                float todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
                float todayTrade = today.getTrade();
                float todayValue = today.getValue();

//                List<Item> itemSubList = new ArrayList();
//                long totalTrade = 0;
//                long totalVolume = 0;
//                float yesterdayVolumePerTradeChange = 0;
//                for (int j = 0; j <= i; j++) {
//                    if (j == i) {
//                        float averageVolumePerTrade = (float) (totalVolume / totalTrade);
//                        float yesterdayVolumePerTrade = yesterday.getAdjustedVolume() / yesterday.getTrade();
//                        yesterdayVolumePerTradeChange = yesterdayVolumePerTrade / averageVolumePerTrade;
//                    }
//                    totalTrade += items.get(j).getTrade();
//                    totalVolume += items.get(j).getAdjustedVolume();
//                    itemSubList.add(items.get(j));
//                }
                List<Item> itemSubList = new ArrayList();
                int counter = 0;
                for (int j = i; j >= 0; j--) {
                    itemSubList.add(items.get(j));
                    if (counter >= ScannerService.TRADING_DAYS_IN_A_MONTH) {
                        break;
                    }
                    ++counter;
                }
                Collections.sort(itemSubList);

//                float averageVolumePerTrade = (totalVolume / totalTrade);
//                float todayVolumePerTrade = today.getAdjustedVolume() / today.getTrade();
//                float volumePerTradeChange = todayVolumePerTrade / averageVolumePerTrade;
                //if(today.getCode().equals("WATACHEM"))
                //    System.out.println("i: " + i + ", todayHead: " + today.getDate() + ", subitemHead: " + itemSubList.get(itemSubList.size()-1).getDate());
//                ChangesInVolumePerTrade changes = ChangesInVolumePerTrade.getChangesInVolumePerTrade(itemSubList, ScannerService.TRADING_DAYS_IN_2_MONTH);
//                float volumePerTradeChange = changes.getTodayChange();
//                float yesterdayVolumePerTradeChange = changes.getYesterdayChange();
                float volumePerTradeChange = today.getVolumePerTradeChange();
                float yesterdayVolumePerTradeChange = yesterday.getVolumePerTradeChange();
                Item maximumVolumePerTradeChange = scanerService.getMaximumVolumePerTradeChange(items, i - 1, ScannerService.TRADING_DAYS_IN_A_MONTH);

                float volumeChange = scanerService.calculateVolumeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float tradeChange = scanerService.calculateTradeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float rsi = scanerService.calculateRSI(itemSubList);
                boolean isLastTwoDaysGreen = yesterdayGap > 0 && dayBeforeYesterdayGap > 0 && yesterdaychange > 0.5 && dayBeforeYesterdayChange > 0.5;
                float tradeChangeWithYesterday = ((float) today.getTrade() / (float) yesterday.getTrade());
                float volumeChangeWithYesterday = ((float) today.getVolume() / (float) yesterday.getVolume());
                boolean isSuddenHike = volumeChangeWithYesterday >= 2 && volumeChange >= 2;
                float diffWithPreviousLow10 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 10);
                float diffWithPreviousLow3 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 3);
                float diffWithPreviousHighVolume = scanerService.getVolumeDiffWithPreviousHigh(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float hammer = getHammer(today);
                scanerService.calculateDivergence(itemSubList);
                int divergence = itemSubList.get(itemSubList.size() - 1).getDivergence();
                boolean consecutive3DaysGreen = scanerService.isConsecutive3DaysGreen(itemSubList);
                float lastFiewDaysVariation = scanerService.getLastFiewDaysVariation(itemSubList, ScannerService.TRADING_DAYS_IN_A_WEEK);
                int upDayCount7 = scanerService.getUpDayCount(items, i, 7);
                int upDayCount4 = scanerService.getUpDayCount(items, i, 4);
                int subListSize = itemSubList.size();
                float todayDiv = (itemSubList.get(subListSize - 1).getEmaList().get(12) - itemSubList.get(subListSize - 1).getEmaList().get(26)) - itemSubList.get(subListSize - 1).getEmaList().get(9);
                float divValue = (todayDiv / today.getAdjustedClosePrice()) * 1000;
//                if ((todaychange > 0.5 && todayGap >= 0)
//                        && !(yesterdayVolumePerTradeChange > 1 && previousYesterdayVolumePerTradeChange > 1)
//                        && ((volumePerTradeChange < 1.2
//                        && (vtcRatioToday > 0.95 && (vtcRatioToday - vtcRatioYesterday) > -0.3)
//                        && (volumeChange > 1.5 && tradeChange > 2)
//                        && (volumeChangeWithYesterday > 1)))
//                        && diffWithPreviousLow < 12) {
//                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday + ", diffWithPreviousLow: " + diffWithPreviousLow);
//                }
                if (((todaychange >= 0.5 && todayGap >= 0.5) && (yesterdaychange >= 1 || yesterdayGap >= 0.5) && (todaychange + yesterdaychange) > 1)
                        && divergence <= 15
                        && rsi <= 50
                        && yesterday.getAdjustedClosePrice() < today.getOpenPrice()
                        && todayValue >= 1
                        && todayTrade >= 50
                        && volumePerTradeChange < 1.8
                        && Math.min(todayGap, yesterdayGap) > -3
                        && diffWithPreviousLow10 < 9 //&& Math.max(todayGap, yesterdayGap) >= 0.5
                        ) {
                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday + ", diffWithPreviousLow: " + diffWithPreviousLow10 + ", rsi: " + rsi + ", hammer: " + hammer + ", divergence: " + divergence + ", diffWithPreviousHighVolume: " + diffWithPreviousHighVolume + ", lastFiewDaysVariation: " + lastFiewDaysVariation + ", maximumVolumePerTradeChange: " + maximumVolumePerTradeChange.getVolumePerTradeChange());
                }

                //previousVolumeChange = volumeChange;
                //previousTradeChange = tradeChange;
                //previousYesterdayVolumePerTradeChange = yesterdayVolumePerTradeChange;
            }
        }
    }

    private static void checkByScript2() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getData(365);
        ScannerService scanerService = new ScannerService();
        String script = "MHSML";
        //List<Item> codes = Utils.getCodes();
        for (String code : oneYearData.keySet()) {
            if (!code.equals(script)) {
                continue;
            }

            List<Item> items = oneYearData.getItems(code);
            scanerService.calculateVolumePerTradeChange(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
            Collections.sort(items);

            float previousVolumeChange = 0;
            float previousTradeChange = 0;
            float previousYesterdayVolumePerTradeChange = 0;

            for (int i = 3; i < items.size(); i++) {
                Item today = items.get(i);
                Item yesterday = items.get(i - 1);
                Item dayBeforeYesterday = items.get(i - 2);
                Item towDayBeforeYesterday = items.get(i - 3);

                float tcYesterday = (float) yesterday.getTrade() / (float) dayBeforeYesterday.getTrade();
                float vcYesterday = (float) yesterday.getVolume() / (float) dayBeforeYesterday.getVolume();
                float vtcRatioYesterday = vcYesterday / tcYesterday;
                float yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
                float yesterdaychange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;
                float dayBeforeYesterdayChange = ((dayBeforeYesterday.getAdjustedClosePrice() - towDayBeforeYesterday.getAdjustedClosePrice()) / towDayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float yesterdayTrade = yesterday.getTrade();

                float tcToday = (float) today.getTrade() / (float) yesterday.getTrade();
                float vcToday = (float) today.getVolume() / (float) yesterday.getVolume();
                float vtcRatioToday = vcToday / tcToday;
                float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
                //priceGap is tuned for higher price stocks
                todayGap = todayGap + today.getAdjustedClosePrice() / 1000;
                float todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
                float todayTrade = today.getTrade();
                float todayValue = today.getValue();

                List<Item> itemSubList = new ArrayList();
                int counter = 0;
                for (int j = i; j >= 0; j--) {
                    itemSubList.add(items.get(j));
                    if (counter >= ScannerService.TRADING_DAYS_IN_A_MONTH) {
                        break;
                    }
                    ++counter;
                }
                Collections.sort(itemSubList);

                float volumePerTradeChange = today.getVolumePerTradeChange();
                float yesterdayVolumePerTradeChange = yesterday.getVolumePerTradeChange();
                Item maximumVolumePerTradeChange = scanerService.getMaximumVolumePerTradeChange(items, i - 1, ScannerService.TRADING_DAYS_IN_A_MONTH);

                float volumeChange = scanerService.calculateVolumeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float tradeChange = scanerService.calculateTradeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float rsi = scanerService.calculateRSI(itemSubList);
                boolean isLastTwoDaysGreen = yesterdayGap > 0 && dayBeforeYesterdayGap > 0 && yesterdaychange > 0.5 && dayBeforeYesterdayChange > 0.5;
                float tradeChangeWithYesterday = ((float) today.getTrade() / (float) yesterday.getTrade());
                float volumeChangeWithYesterday = ((float) today.getVolume() / (float) yesterday.getVolume());
                boolean isSuddenHike = volumeChangeWithYesterday >= 2 && volumeChange >= 2;
                float diffWithPreviousLow10 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 10);
                float diffWithPreviousLow3 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 3);
                float diffWithPreviousHighVolume = scanerService.getVolumeDiffWithPreviousHigh(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float hammer = getHammer(today);
                scanerService.calculateDivergence(itemSubList);
                int divergence = itemSubList.get(itemSubList.size() - 1).getDivergence();
                boolean consecutive3DaysGreen = scanerService.isConsecutive3DaysGreen(itemSubList);
                float lastFiewDaysVariation = scanerService.getLastFiewDaysVariation(itemSubList, ScannerService.TRADING_DAYS_IN_A_WEEK);
                int upDayCount7 = scanerService.getUpDayCount(items, i, 7);
                int upDayCount4 = scanerService.getUpDayCount(items, i, 4);
                boolean happenedInLastFewDays = happenedInLastFewDays(items, i, ScannerService.TRADING_DAYS_IN_A_WEEK * 2, 1.2f, 1.2f);

//                if ((todaychange>0.5 && todayGap >= 0)
//                        && tradeChange>1.2 && volumePerTradeChange>1.2 & !happenedInLastFewDays
////                        && !(yesterdayVolumePerTradeChange>1 && previousYesterdayVolumePerTradeChange>1)
//                        //&& (volumePerTradeChange > 1.2 && volumePerTradeChange < 2)
//                        //&& (vtcRatioToday>0.95 && (vtcRatioToday-vtcRatioYesterday)>-0.3 )
//                        //&& ( tradeChange>1.2 && tradeChange<3 )
//                        //&& (volumeChangeWithYesterday > 1)
//                        
//                        && (diffWithPreviousLow10<12 && diffWithPreviousLow3<8)
//                        //&& todayTrade > 70
//                        //&& todayValue > 1.5
//                        && (hammer <= 2 && todayGap >= hammer )
//                        //&& divergence < 20
//                        //&& rsi < 75
//                        && todayGap <6
//                        && today.getAdjustedClosePrice() >8
//                        //&& diffWithPreviousHighVolume > 1
//                        //&& !consecutive3DaysGreen
//                        //&& volumeChange/lastFiewDaysVariation >0.6
//                        //&& upDayCount7 < 5
//                        //&& upDayCount4 < 3
////                        && !(maximumVolumePerTradeChange.getVolumePerTradeChange()>1.1 && maximumVolumePerTradeChange.getTradeChange()>1.1)
//                   ) {
                System.out.println("Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday + ", diffWithPreviousLow: " + diffWithPreviousLow10 + ", rsi: " + rsi + ", hammer: " + hammer + ", divergence: " + divergence + ", diffWithPreviousHighVolume: " + diffWithPreviousHighVolume + ", lastFiewDaysVariation: " + lastFiewDaysVariation + ", maximumVolumePerTradeChange: " + maximumVolumePerTradeChange.getVolumePerTradeChange());
//                }
            }
        }
    }

    private static void tradeGTVolume() throws SQLException, ClassNotFoundException, ParseException {
        ItemDaoImpl dao = new ItemDaoImpl();
        dao.open();
        CustomHashMap oneYearData = dao.getData(365);
        ScannerService scanerService = new ScannerService();
        String script = "PHARMAID";
        //List<Item> codes = Utils.getCodes();
        for (String code : oneYearData.keySet()) {
//            if (!code.equals(script)) {
//                continue;
//            }

            List<Item> items = oneYearData.getItems(code);
            scanerService.calculateVolumePerTradeChange(items, ScannerService.TRADING_DAYS_IN_A_MONTH);
            Collections.sort(items);

            float previousVolumeChange = 0;
            float previousTradeChange = 0;
            float previousYesterdayVolumePerTradeChange = 0;

            for (int i = 3; i < items.size(); i++) {
                Item today = items.get(i);
                Item yesterday = items.get(i - 1);
                Item dayBeforeYesterday = items.get(i - 2);
                Item towDayBeforeYesterday = items.get(i - 3);

                float tcYesterday = (float) yesterday.getTrade() / (float) dayBeforeYesterday.getTrade();
                float vcYesterday = (float) yesterday.getVolume() / (float) dayBeforeYesterday.getVolume();
                float vtcRatioYesterday = vcYesterday / tcYesterday;
                float yesterdayGap = ((yesterday.getAdjustedClosePrice() - yesterday.getOpenPrice()) / yesterday.getOpenPrice()) * 100;
                float yesterdaychange = ((yesterday.getAdjustedClosePrice() - dayBeforeYesterday.getAdjustedClosePrice()) / dayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float dayBeforeYesterdayGap = ((dayBeforeYesterday.getAdjustedClosePrice() - dayBeforeYesterday.getOpenPrice()) / dayBeforeYesterday.getOpenPrice()) * 100;
                float dayBeforeYesterdayChange = ((dayBeforeYesterday.getAdjustedClosePrice() - towDayBeforeYesterday.getAdjustedClosePrice()) / towDayBeforeYesterday.getAdjustedClosePrice()) * 100;
                float yesterdayTrade = yesterday.getTrade();

                float tcToday = (float) today.getTrade() / (float) yesterday.getTrade();
                float vcToday = (float) today.getVolume() / (float) yesterday.getVolume();
                float vtcRatioToday = vcToday / tcToday;
                float todayGap = ((today.getAdjustedClosePrice() - today.getOpenPrice()) / today.getOpenPrice()) * 100;
                //priceGap is tuned for higher price stocks
                todayGap = todayGap + today.getAdjustedClosePrice() / 1000;
                float todaychange = ((today.getAdjustedClosePrice() - yesterday.getAdjustedClosePrice()) / yesterday.getAdjustedClosePrice()) * 100;
                float todayTrade = today.getTrade();
                float todayValue = today.getValue();

                List<Item> itemSubList = new ArrayList();
                int counter = 0;
                for (int j = i; j >= 0; j--) {
                    itemSubList.add(items.get(j));
                    if (counter >= ScannerService.TRADING_DAYS_IN_A_MONTH) {
                        break;
                    }
                    ++counter;
                }
                Collections.sort(itemSubList);

                float volumePerTradeChange = today.getVolumePerTradeChange();
                float yesterdayVolumePerTradeChange = yesterday.getVolumePerTradeChange();
                Item maximumVolumePerTradeChange = scanerService.getMaximumVolumePerTradeChange(items, i - 1, ScannerService.TRADING_DAYS_IN_A_MONTH);

                float volumeChange = scanerService.calculateVolumeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float tradeChange = scanerService.calculateTradeChange(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float rsi = scanerService.calculateRSI(itemSubList);
                boolean isLastTwoDaysGreen = yesterdayGap > 0 && dayBeforeYesterdayGap > 0 && yesterdaychange > 0.5 && dayBeforeYesterdayChange > 0.5;
                float tradeChangeWithYesterday = ((float) today.getTrade() / (float) yesterday.getTrade());
                float volumeChangeWithYesterday = ((float) today.getVolume() / (float) yesterday.getVolume());
                boolean isSuddenHike = volumeChangeWithYesterday >= 2 && volumeChange >= 2;
                float diffWithPreviousLow10 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 10);
                float diffWithPreviousLow3 = scanerService.getPriceDiffWithPreviousLow(itemSubList, 3);
                float diffWithPreviousHighVolume = scanerService.getVolumeDiffWithPreviousHigh(itemSubList, ScannerService.TRADING_DAYS_IN_A_MONTH);
                float hammer = getHammer(today);
                scanerService.calculateDivergence(itemSubList);
                int divergence = itemSubList.get(itemSubList.size() - 1).getDivergence();
                boolean consecutive3DaysGreen = scanerService.isConsecutive3DaysGreen(itemSubList);
                float lastFiewDaysVariation = scanerService.getLastFiewDaysVariation(itemSubList, ScannerService.TRADING_DAYS_IN_A_WEEK);
                int upDayCount7 = scanerService.getUpDayCount(items, i, 7);
                int upDayCount4 = scanerService.getUpDayCount(items, i, 4);
                boolean happenedInLastFewDays = happenedInLastFewDays(items, i, ScannerService.TRADING_DAYS_IN_A_WEEK * 2, 1.2f, 1.2f);

                if ((todaychange > 0.5 && todayGap >= 0)
                        //                        && tradeChange<2 && volumePerTradeChange<0.85 && volumeChange>0.7
                        && volumePerTradeChange < 0.8 && volumeChange > 1
                        //                        && !(yesterdayVolumePerTradeChange>1 && previousYesterdayVolumePerTradeChange>1)
                        //&& (volumePerTradeChange > 1.2 && volumePerTradeChange < 2)
                        //&& (vtcRatioToday>0.95 && (vtcRatioToday-vtcRatioYesterday)>-0.3 )
                        //&& ( tradeChange>1.2 && tradeChange<3 )
                        //&& (volumeChangeWithYesterday > 1)

                        //                        && (diffWithPreviousLow10<12 && diffWithPreviousLow3<8)
                        //&& todayTrade > 70
                        //&& todayValue > 1.5
                        //                        && (hammer <= 2 && todayGap >= hammer )
                        && divergence < 20
                        //&& rsi < 75
                        && todayGap < 6
                        && today.getAdjustedClosePrice() > 8 //&& diffWithPreviousHighVolume > 1
                        //&& !consecutive3DaysGreen
                        //&& volumeChange/lastFiewDaysVariation >0.6
                        //&& upDayCount7 < 5
                        //&& upDayCount4 < 3
                        //                        && !(maximumVolumePerTradeChange.getVolumePerTradeChange()>1.1 && maximumVolumePerTradeChange.getTradeChange()>1.1)
                        ) {
                    System.out.println("Date: " + today.getDate() + ", code: " + code + ", tchange: " + tradeChange + ", volumeChange: " + volumeChange + ", vtcRatioYesterday: " + vtcRatioYesterday + ", vtcRatioToday: " + vtcRatioToday + ", yesterdayVolumePerTradeChange: " + yesterdayVolumePerTradeChange + ", volumePerTradeChange: " + volumePerTradeChange + ", tradeChangeWithYesterday: " + tradeChangeWithYesterday + ", volumeChangeWithYesterday: " + volumeChangeWithYesterday + ", diffWithPreviousLow: " + diffWithPreviousLow10 + ", rsi: " + rsi + ", hammer: " + hammer + ", divergence: " + divergence + ", diffWithPreviousHighVolume: " + diffWithPreviousHighVolume + ", lastFiewDaysVariation: " + lastFiewDaysVariation + ", maximumVolumePerTradeChange: " + maximumVolumePerTradeChange.getVolumePerTradeChange());
                }
            }
        }
    }

    private static boolean happenedInLastFewDays(List<Item> items, int head, int days, float tradeChange, float volumePerTradeChange) {
        if (head >= items.size() || days >= items.size() || head <= days) {
            return false;
        }

        ScannerService scanerService = new ScannerService();
//        if(items.get(items.size()-1).getCode().equals("BGIC"))
//            System.out.println("Calculating happened for " + items.get(head).getDate());

        for (int i = head - 1; (head - i) <= (days); i--) {
            Item item = items.get(i);
            float calculatedTradeChange = scanerService.calculateTradeChange(items, i, ScannerService.TRADING_DAYS_IN_A_MONTH);
//            if(item.getCode().equals("BGIC"))
//                System.out.println("Date: " + item.getDate() + ", calculatedTradeChange: " + calculatedTradeChange + ", vc: " + item.getVolumePerTradeChange());
            if (calculatedTradeChange >= tradeChange && item.getVolumePerTradeChange() >= volumePerTradeChange) {
                return true;
            }
        }

        return false;
    }

    private static float getHammer(Item item) {
        float largest = Math.max(item.getOpenPrice(), item.getAdjustedClosePrice());
        float hammer = ((item.getDayHigh() - largest) / largest) * 100;
        return hammer;
    }

    private static float getDBHammer(Item item) {
        float difference = Math.abs(item.getOpenPrice() - item.getAdjustedClosePrice());
        float largest = (item.getOpenPrice() + item.getAdjustedClosePrice() + difference) / 2;
        float smallest = (item.getOpenPrice() + item.getAdjustedClosePrice() - difference) / 2;
        float dbHammer = (((smallest - item.getDayLow()) - (item.getDayHigh() - largest)) / item.getAdjustedClosePrice()) * 100;
        //if(item.getCode().equalsIgnoreCase("watachem"))
        //    System.out.println("yesterday " + item.getDate() + " hammer: " + dbHammer + ", open: " + item.getOpenPrice() + ", close: " + item.getClosePrice() + ", high: " + item.getHigh() + ", low: " + item.getLow() + ", largest: " + largest + ", smallest: " + smallest);
        return dbHammer;
    }
}
