package com.mycompany.service;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
    ListeningExecutorService executorService;
    private static int NEXT_PORTFOLIO_ID = 93722;
    public static boolean isCrawlingActive;
    
    public void identifyMerchatPortfolios(){
        executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(NUMBER_OF_THREAD));
        
        //Make a task
        Callable<Crawler> loginCrawler = createLoginTask();
        ListenableFuture listenableFuture = executorService.submit(loginCrawler);
        Futures.addCallback(listenableFuture, new FutureCallback<Crawler>() {
            @Override
            public void onSuccess(Crawler v) {
                createAndSubmitTask(v);
                System.out.println("Shutting down executor service");
                executorService.shutdown();
            }

            @Override
            public void onFailure(Throwable thrwbl) {
                System.out.println("Login crawler failed");
            }
        });
    }
    
    public void shutdown(){
        executorService.shutdown();
    }
    
    private void createAndSubmitTask(Crawler loginCrawler){
        int initialNumberOfTasks = 100;
        int counter = 0;
        
        for (int portfolioId = getNextPortfolioId(); counter<initialNumberOfTasks; counter++) {
            Callable<Crawler> task = createTask(loginCrawler, portfolioId);
            
            ListenableFuture listenableFuture = executorService.submit(task);
            
            Futures.addCallback(listenableFuture, new FutureCallbackImpl(loginCrawler));
        }
        
        System.out.println("100 task has been submitted");
    }
    
    private class FutureCallbackImpl implements FutureCallback<Crawler>{
        Crawler loginCrawler;

        public FutureCallbackImpl(Crawler loginCrawler) {
            this.loginCrawler = loginCrawler;
        }

        @Override
        public void onSuccess(Crawler v) {
            System.out.println("PortfolioId: " + v.getParams().get("PORTFOLIO_ID") + ", isQualified: " + v.getParams().get("IS_QUALIFIED") + ", totalPurchase: " + v.getParams().get("TOTAL_PURCHASE_VALUE"));
            
            if(isCrawlingActive && NEXT_PORTFOLIO_ID>9300){
                //Create and submit another task
                Callable<Crawler> task = createTask(loginCrawler, getNextPortfolioId());
                System.out.println("Adding task to executor service");
                ListenableFuture listenableFuture = executorService.submit(task);
                Futures.addCallback(listenableFuture, new FutureCallbackImpl(loginCrawler));
            }
        }

        @Override
        public void onFailure(Throwable thrwbl) {
            System.out.println("Merchant portfolio sync failed");
        }
        
    }
    
    private synchronized static int getNextPortfolioId(){
        NEXT_PORTFOLIO_ID--;
        return NEXT_PORTFOLIO_ID;
    }
    
    private Callable<Crawler> createTask(Crawler loginCrawler, int portfolioId){
        Crawler crawler = null;
        try {
            String path = Utils.getConfigFilesPath();
            Map params = new HashMap(loginCrawler.getParams());
            params.put("PORTFOLIO_ID", portfolioId);
            ScraperConfiguration config = Crawler.getScraperConfig(null, path, Crawler.CrawlType.MERCHANT_PORTFOLIO_IDENTIFIER);
            crawler = new Crawler(config, null, Crawler.CrawlType.MERCHANT_PORTFOLIO_IDENTIFIER,params);
        } catch (FileNotFoundException ex) {
            System.out.println("Fatal error file not found");
        }
        
        return crawler;
    }
    
    private Callable<Crawler> createLoginTask(){
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
