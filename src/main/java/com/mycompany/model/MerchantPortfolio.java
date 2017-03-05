/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.model;

import java.util.Date;

/**
 *
 * @author setu
 */
public class MerchantPortfolio extends Portfolio{
    private float amount;
    private Date lastActivity;

    public MerchantPortfolio(int remoteId, float amount, Date lastActivity) {
        this.remoteId = remoteId;
        this.amount = amount;
        this.lastActivity = lastActivity;
    }

    /**
     * @return the amount
     */
    public float getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * @return the lastActivity
     */
    public Date getLastActivity() {
        return lastActivity;
    }

    /**
     * @param lastActivity the lastActivity to set
     */
    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public String toString() {
        return "MerchantPortfolio{" + "portfolioId=" + remoteId + ", amount=" + amount + ", lastActivity=" + lastActivity + '}';
    }
}
