/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @date Dec 14, 2015
 * @author setu
 */

public class PortfolioDetails extends BasicInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int quantity;
    private float buyPrice;
    private String comments;
    private Date date;
    private int portfolio;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.code);
        hash = 71 * hash + Objects.hashCode(this.date);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PortfolioDetails other = (PortfolioDetails) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        return true;
    }

    public PortfolioDetails() {
    }

    public PortfolioDetails(String code, int quantity, float buyPrice, Date date) {
        super.code = code;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(float buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PortfolioDetails[ code=" + code + ", date=" + date + ", quantity=" + quantity + ", buyPrice=" + buyPrice + " ]";
    }

    /**
     * @return the portfolio
     */
    public int getPortfolio() {
        return portfolio;
    }

    /**
     * @param portfolio the portfolio to set
     */
    public void setPortfolio(int portfolio) {
        this.portfolio = portfolio;
    }

}
