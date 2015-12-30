/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date Dec 14, 2015
 * @author setu
 */

public class Portfolio implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private int remoteId;
    private Map<String, PortfolioItem> portfolioItems;

    public Portfolio() {
    }

    public Portfolio(Integer id) {
        this.id = id;
    }

    public Portfolio(Integer id, String name, int remoteId) {
        this.id = id;
        this.name = name;
        this.remoteId = remoteId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(int remoteId) {
        this.remoteId = remoteId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Portfolio)) {
            return false;
        }
        Portfolio other = (Portfolio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.model.Portfolio[ id=" + id + " ]";
    }

    /**
     * @return the portfolioItems
     */
    public Map<String, PortfolioItem> getPortfolioItems() {
        if(portfolioItems == null)
            portfolioItems = new HashMap<>();
        return portfolioItems;
    }

    /**
     * @param portfolioItems the portfolioItems to set
     */
    public void setPortfolioItems(Map<String, PortfolioItem> portfolioItems) {
        this.portfolioItems = portfolioItems;
    }

}
