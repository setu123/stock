/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.model;

import java.util.Date;
import java.util.List;

/**
 * @date Dec 16, 2015
 * @author setu
 */
public class PortfolioItem extends BasicInfo{
    private int quantity;
    private float averageBuyPrice;
    private Date date;
    private List<PortfolioDetails> portfolioDetails;
    private String cause;

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the portfolioDetails
     */
    public List<PortfolioDetails> getPortfolioDetails() {
        return portfolioDetails;
    }

    /**
     * @param portfolioDetails the portfolioDetails to set
     */
    public void setPortfolioDetails(List<PortfolioDetails> portfolioDetails) {
        this.portfolioDetails = portfolioDetails;
    }

    /**
     * @return the averageBuyPrice
     */
    public float getAverageBuyPrice() {
        return averageBuyPrice;
    }

    /**
     * @param averageBuyPrice the averageBuyPrice to set
     */
    public void setAverageBuyPrice(float averageBuyPrice) {
        this.averageBuyPrice = averageBuyPrice;
    }

    @Override
    public String toString() {
        return "PortfolioItem{" + "code=" + code + ", quantity=" + quantity + ", averageBuyPrice=" + averageBuyPrice + ", date=" + date + ", portfolioDetails=" + portfolioDetails + '}';
    }

    /**
     * @return the cause
     */
    public String getCause() {
        return cause;
    }

    /**
     * @param cause the cause to set
     */
    public void setCause(String cause) {
        this.cause = cause;
    }
    
    
}
