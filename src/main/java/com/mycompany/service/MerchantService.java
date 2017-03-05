package com.mycompany.service;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.dao.MerchantPortfolioDaoImpl;
import com.mycompany.model.MerchantPortfolio;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.webharvest.definition.ScraperConfiguration;

/**
 * @date Feb 23, 2017
 * @author setu
 */
public class MerchantService {

    private static final int NUMBER_OF_THREAD = 100;
    private static final int NUMBER_OF_PORTFOLIOS_IN_BATCH = 100;
    ListeningExecutorService executorService;
    private int PORTFOLIO_ID = 63722;
    private final Object lock = new Object();
    private final MerchantPortfolioDaoImpl merchantDao;
    private static MerchantService merchantService;

    private static List<MerchantPortfolio> merchantPortfolios;

    public static MerchantService getInstance() {
        if (merchantService == null) {
            merchantService = new MerchantService();
        }
        return merchantService;
    }

    private MerchantService() {
        merchantPortfolios = new ArrayList<>();
        merchantDao = new MerchantPortfolioDaoImpl();
    }

    public void startPortfolioIdSync() {
        executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(NUMBER_OF_THREAD));

        //Make a task
        Callable<Crawler> loginCrawler = createLoginTask();
        ListenableFuture listenableFuture = executorService.submit(loginCrawler);
        Futures.addCallback(listenableFuture, new FutureCallback<Crawler>() {
            @Override
            public void onSuccess(Crawler v) {
                createAndSubmitTask(v);
//                System.out.println("Shutting down executor service");
//                executorService.shutdown();
            }

            @Override
            public void onFailure(Throwable thrwbl) {
                System.out.println("Login crawler failed");
            }
        });
    }

    public String stopExecutorService() {
        String response;
        if (executorService == null || executorService.isTerminated()) {
            response = "Not running";
        } else {
            executorService.shutdown();
            response = "Stopped";
        }
        return response;
    }

    public String getStatus() {
        return (executorService == null || executorService.isTerminated()) ? "Not running" : "Running";
    }

    public int getLastPortfolioId() {
        return PORTFOLIO_ID;
    }

    public void setPortfolioId(int portfolioId) {
        synchronized (lock) {
            PORTFOLIO_ID = portfolioId;
        }
    }

    private void createAndSubmitTask(Crawler loginCrawler) {
        int initialNumberOfTasks = NUMBER_OF_THREAD;
        int counter = 0;

        for (; counter < initialNumberOfTasks; counter++) {
            int portfolioId = getNextPortfolioId();
            Callable<Crawler> task = createTask(loginCrawler, portfolioId);

            ListenableFuture listenableFuture = executorService.submit(task);

            Futures.addCallback(listenableFuture, new FutureCallbackImpl(loginCrawler));
        }

        System.out.println(NUMBER_OF_THREAD + " task has been submitted");
    }

    private class FutureCallbackImpl implements FutureCallback<Crawler> {

        Crawler loginCrawler;

        public FutureCallbackImpl(Crawler loginCrawler) {
            this.loginCrawler = loginCrawler;
        }

        @Override
        public void onSuccess(Crawler v) {
            processResult(v);
            
            if (!executorService.isShutdown()) {
                //Create and submit another task
                Callable<Crawler> task = createTask(loginCrawler, getNextPortfolioId());
//                System.out.println("Adding task to executor service");
                ListenableFuture listenableFuture = executorService.submit(task);
                Futures.addCallback(listenableFuture, new FutureCallbackImpl(loginCrawler));
            }
        }

        @Override
        public void onFailure(Throwable thrwbl) {
            System.out.println("Merchant portfolio sync failed");
        }

    }

    private void processResult(Crawler v) {
//        System.out.println("PortfolioId: " + v.getParams().get("PORTFOLIO_ID") + ", isQualified: " + v.getParams().get("IS_QUALIFIED") + ", totalPurchase: " + v.getParams().get("TOTAL_PURCHASE_VALUE"));
        boolean isQualified = (boolean) v.getParams().get("IS_QUALIFIED");
        
        if (isQualified) {
            int portfolioId = (int) v.getParams().get("PORTFOLIO_ID");
            double amount = (double) v.getParams().get("TOTAL_PURCHASE_VALUE");
            Date lastActivity = (Date) v.getParams().get("LAST_ACTIVITY");
            MerchantPortfolio portfolio = new MerchantPortfolio(portfolioId, (float) amount, lastActivity);
            System.out.println("Adding " + portfolio);
            addToMerchantList(portfolio, false);
        }
    }

    private synchronized void addToMerchantList(MerchantPortfolio merchantPortfolio, boolean flush) {
        if(merchantPortfolio != null)
            merchantPortfolios.add(merchantPortfolio);

        if ((merchantPortfolios.size()>=NUMBER_OF_PORTFOLIOS_IN_BATCH) || flush) {
            try {
                merchantDao.open();
                merchantDao.importMerchantPortfolios(merchantPortfolios);
                merchantDao.close();
                merchantPortfolios.clear();
            } catch (SQLException | ClassNotFoundException ex) {
                System.out.println("Exception caught: " + ex.getMessage());
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    private int getNextPortfolioId() {
        synchronized (lock) {
            PORTFOLIO_ID--;
        }
        return PORTFOLIO_ID;
    }

    private Callable<Crawler> createTask(Crawler loginCrawler, int portfolioId) {
        Crawler crawler = null;
        try {
            String path = Utils.getConfigFilesPath();
            Map params = new HashMap(loginCrawler.getParams());
            params.put("PORTFOLIO_ID", portfolioId);
            ScraperConfiguration config = Crawler.getScraperConfig(null, path, Crawler.CrawlType.MERCHANT_PORTFOLIO_IDENTIFIER);
            crawler = new Crawler(config, null, Crawler.CrawlType.MERCHANT_PORTFOLIO_IDENTIFIER, params);
        } catch (FileNotFoundException ex) {
            System.out.println("Fatal error file not found");
        }

        return crawler;
    }

    private Callable<Crawler> createLoginTask() {
        String path = Utils.getConfigFilesPath();
        ScraperConfiguration config = null;
        Callable<Crawler> loginCrawler = null;

        try {
            config = Crawler.getScraperConfig(null, path, Crawler.CrawlType.LOGIN);
            loginCrawler = new Crawler(config, null, Crawler.CrawlType.LOGIN, null);
        } catch (FileNotFoundException ex) {
            System.err.println("Config file for news not found");;
        }

        return loginCrawler;
    }
}
