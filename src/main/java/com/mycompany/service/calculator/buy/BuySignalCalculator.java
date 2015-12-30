/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.service.calculator.buy;

import com.mycompany.model.Item;
import com.mycompany.model.Portfolio;
import com.mycompany.model.PortfolioItem;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.ScannerService;
import com.mycompany.service.calculator.DecisionMaker;
import com.mycompany.service.calculator.SignalCalculator;
import java.util.List;

/**
 *
 * @author setu
 */
public abstract class BuySignalCalculator extends SignalCalculator implements DecisionMaker{
    
    

    public BuySignalCalculator(ScannerService scanner, CustomHashMap oneYearData, Portfolio portfolio) {
        super(scanner, oneYearData, portfolio);
    }
    
    public abstract boolean isBuyCandidate(List<Item> itemSubList, Item calculated);
    
    protected boolean isMaskPassed(Item item, Portfolio portfolio){
        //System.out.println("date: " + item.getDate() + " cause: " + getCause() + ", s: " + item.getSignal());
        Item buyItem = getBuyItem(item, portfolio);
        if(item.getSignal().equals(Item.SignalType.BUY)){
            
            if(buyItem!=null && buyItem.getCode().equals(item.getCode()))
                return false; 
            
            
            //buyItem = item;
            //++totalBuy;
            //System.out.println("");
            //System.out.print(item.getCode() + " on " + item.getDate() + ", price: " + item.getAdjustedClosePrice() + ", cause: " + getCause() + "(t:"+df.format(item.getTradeChange())+", v:"+df.format(item.getVolumeChange())+")" + " ---- ");
        }
        
        //System.out.println("lastTradingDay: " + lastTradingDay + ", item.getDate(): " + item.getDate());
        if(item.getDate().equals(lastTradingDay.getTime()) || item.getDate().after(lastTradingDay.getTime()))
            return false;
        
        return true;
    }
    
    private Item getBuyItem(Item item, Portfolio portfolio){
        PortfolioItem portfolioItem = portfolio.getPortfolioItems().get(item.getCode());
        if(portfolioItem == null)
            return null;
        
        Item buyItem = new Item(item.getCode());
        buyItem.setDate(portfolioItem.getDate());
        buyItem.setAdjustedClosePrice(portfolioItem.getAverageBuyPrice());
        return buyItem;
    }
}
