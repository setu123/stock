package com.mycompany.service;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mycompany.dao.MerchantPortfolioDaoImpl;
import com.mycompany.model.MerchantPortfolio;
import com.mycompany.model.PortfolioDetails;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.webharvest.definition.ScraperConfiguration;

/**
 * @date Mar 10, 2017
 * @author setu
 */
public class MerchantDetailService {

    private static final int NUMBER_OF_THREAD = 10;
    private static final int NUMBER_OF_PORTFOLIO_DETAILS_IN_BATCH = 100;
    ListeningExecutorService executorService;
    private static int PORTFOLIO_ID;
    private final Object lock = new Object();
    private final MerchantPortfolioDaoImpl merchantDao;
    private static MerchantDetailService merchantService;

    private static List<PortfolioDetails> merchantPortfolioDetails;
    private Iterator<MerchantPortfolio> crawledMerchantPortfolioIdIterator;

    public static MerchantDetailService getInstance() {
        if (merchantService == null) {
            merchantService = new MerchantDetailService();
        }
        return merchantService;
    }

    private MerchantDetailService() {
        try {
            merchantPortfolioDetails = new ArrayList<>();
            merchantDao = new MerchantPortfolioDaoImpl();
            merchantDao.open();
            crawledMerchantPortfolioIdIterator = merchantDao.getPortfolios().iterator();
            merchantDao.close();
        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String reset() {
        try {
            merchantPortfolioDetails = new ArrayList<>();
            merchantDao.open();
            crawledMerchantPortfolioIdIterator = merchantDao.getPortfolios().iterator();
            merchantDao.close();
        } catch (SQLException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        return "Reset done";
    }

    public void startPortfolioDetailSync() {
        System.out.println("Detail sync service started at " + new Date());

        try {
            merchantDao.open();
            merchantDao.clearDetails();
            merchantDao.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

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
            System.out.println("shutting down " + new Date());
            try {
                executorService.awaitTermination(5, TimeUnit.MINUTES);
//                setLastCrawledMerchantId();
                addToMerchantDetailList(null, true);
                System.out.println("Terminated: " + new Date());
            } catch (InterruptedException ex) {
                System.out.println("Error in termination: " + ex.getMessage());
            }
            response = "Stopped";
        }
        return response;
    }

//    private void setLastCrawledMerchantId() {
//        try {
//            merchantDao.open();
//            merchantDao.setLastCrawledMerchantId(PORTFOLIO_ID);
//            merchantDao.close();
//        } catch (SQLException | ClassNotFoundException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
    public String getStatus() {
        return (executorService == null || executorService.isTerminated()) ? "Not running" : "Running";
    }

    public int getLastPortfolioId() {
        return PORTFOLIO_ID;
    }
//
//    public void setPortfolioId(int portfolioId) {
//        synchronized (lock) {
//            PORTFOLIO_ID = portfolioId;
//        }
//    }

    private void createAndSubmitTask(Crawler loginCrawler) {
        int initialNumberOfTasks = NUMBER_OF_THREAD;
        int counter = 0;

        for (; counter < initialNumberOfTasks; counter++) {
            MerchantPortfolio merchantPortfolio = getNextPortfolio();
            Callable<Crawler> task = createTask(loginCrawler, merchantPortfolio);

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
                MerchantPortfolio merchantPortfolio = getNextPortfolio();

                if (merchantPortfolio == null) {
                    stopExecutorService();
                    return;
                }

                Callable<Crawler> task = createTask(loginCrawler, merchantPortfolio);
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
        List<PortfolioDetails> portfolioDetails = (List<PortfolioDetails>) v.getParams().get("PORTFOLIO_DETAILS");
        addToMerchantDetailList(portfolioDetails, false);
    }

    private synchronized void addToMerchantDetailList(List<PortfolioDetails> portfolioDetails, boolean flush) {
        if (portfolioDetails != null && !portfolioDetails.isEmpty()) {
            merchantPortfolioDetails.addAll(portfolioDetails);
        }

        if ((merchantPortfolioDetails.size() >= NUMBER_OF_PORTFOLIO_DETAILS_IN_BATCH) || flush) {
            try {
                merchantDao.open();
                merchantDao.updatePortfolioDetails(portfolioDetails);
                merchantDao.close();
                merchantPortfolioDetails.clear();
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private synchronized MerchantPortfolio getNextPortfolio() {
        MerchantPortfolio merchantPortfolio = null;
        if (crawledMerchantPortfolioIdIterator.hasNext()) {
            merchantPortfolio = crawledMerchantPortfolioIdIterator.next();
            PORTFOLIO_ID = merchantPortfolio.getRemoteId();
        }
        return merchantPortfolio;
    }

    private Callable<Crawler> createTask(Crawler loginCrawler, MerchantPortfolio merchantPortfolio) {
        Crawler crawler = null;
        try {
            String path = Utils.getConfigFilesPath();
            Map params = new HashMap(loginCrawler.getParams());
            params.put("PORTFOLIO_ID", merchantPortfolio.getRemoteId());
            params.put("PORTFOLIO_INTERNAL_ID", merchantPortfolio.getId());
            ScraperConfiguration config = Crawler.getScraperConfig(null, path, Crawler.CrawlType.MERCHANT_PORTFOLIO_DETAILS);
            crawler = new Crawler(config, null, Crawler.CrawlType.MERCHANT_PORTFOLIO_DETAILS, params);
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
