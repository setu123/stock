/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.dao;

import com.mycompany.model.Portfolio;
import com.mycompany.model.PortfolioDetails;
import com.mycompany.model.PortfolioItem;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date Dec 15, 2015
 * @author setu
 */
public class PortfolioDaoImpl extends BasicDaoImpl{
    
    public void updatePortfolioDetails(List<PortfolioDetails> portfolioDetails, int portfolioId) throws SQLException{
        PreparedStatement pStatement;

        try {
            //Delete existing portfolio details
            String sql = "DELETE FROM portfolio_details WHERE PORTFOLIO = ?";
            pStatement = connection.prepareStatement(sql);
            //System.out.println("pstatment: " + pStatement + ", por: " + portfolioDetails.get(0));
            pStatement.setInt(1, portfolioId);
            pStatement.executeUpdate();

            //Insert portfolio details
            sql = "INSERT INTO portfolio_details (PORTFOLIO, CODE, QUANTITY, BUY_PRICE, COMMENTS, DATE) VALUES (?, ?, ?, ?, ?, ?)";
            pStatement = connection.prepareStatement(sql);
            for (PortfolioDetails portfolioDetail : portfolioDetails) {
                pStatement.setInt(1, portfolioId);
                pStatement.setString(2, portfolioDetail.getCode());
                pStatement.setInt(3, portfolioDetail.getQuantity());
                pStatement.setFloat(4, portfolioDetail.getBuyPrice());
                pStatement.setString(5, portfolioDetail.getComments());
                pStatement.setDate(6, new Date(portfolioDetail.getDate().getTime()));
                pStatement.addBatch();
            }
            pStatement.executeBatch();
            
            //Update last_update time in portfolio
            sql = "UPDATE portfolio SET LAST_UPDATE = ? WHERE ID = ?";
            pStatement = connection.prepareStatement(sql);
            pStatement.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
            pStatement.setInt(2, portfolioId);
            pStatement.executeUpdate();
        } finally {
        }
    }
    
    public Portfolio getPortfolio(int id){
        PreparedStatement pStatement;
        Portfolio portfolio = new Portfolio();
        List<PortfolioDetails> portfolioDetails = new ArrayList<>();

        try {
            //Delete existing portfolio details
            String sql = "SELECT * FROM portfolio, portfolio_details where portfolio = id and id = ?";
            pStatement = connection.prepareStatement(sql);
            //System.out.println("pstatment: " + pStatement + ", por: " + portfolioDetails.get(0));
            pStatement.setInt(1, id);
            ResultSet rs = pStatement.executeQuery();            
            
            while(rs.next()){
                portfolio.setId(rs.getInt("id"));
                portfolio.setName(rs.getString("name"));
                portfolio.setRemoteId(rs.getInt("remote_id"));
                
                PortfolioDetails details = new PortfolioDetails();
                details.setBuyPrice(rs.getFloat("buy_price"));
                details.setCode(rs.getString("code"));
                details.setComments(rs.getString("comments"));
                details.setDate(rs.getDate("date"));
                details.setQuantity(rs.getInt("quantity"));
                portfolioDetails.add(details);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
        }
        
        Map<String,PortfolioItem> portfolioItems = getPortfolioItems(portfolioDetails);
        portfolio.setPortfolioItems(portfolioItems);
        return portfolio;
    }
    
    private Map<String,PortfolioItem> getPortfolioItems(List<PortfolioDetails> portfolioDetails){
        
        Map<String, List<PortfolioDetails>> items = new HashMap<>();
        for(PortfolioDetails details: portfolioDetails){
            String code = details.getCode();
            
            List<PortfolioDetails> itemDetails = items.get(code);
            if(itemDetails == null)
                itemDetails = new ArrayList<>();
            itemDetails.add(details);
            items.put(code, itemDetails);
        }
        
        Map<String, PortfolioItem> portfolioItems = new HashMap<>();
        
        for(Map.Entry<String, List<PortfolioDetails>> entry: items.entrySet()){
            String code = entry.getKey();
            List<PortfolioDetails> details = entry.getValue();
            
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, 2000);
            java.util.Date buyDate = date.getTime();
            float totalBuyPrice = 0;
            int totalQuantity = 0;
            
            for(PortfolioDetails detail: details){
                if(detail.getDate().after(buyDate))
                    buyDate = detail.getDate();
                
                totalQuantity += detail.getQuantity();
                totalBuyPrice += detail.getBuyPrice()*1.005*detail.getQuantity();
            }
            
            PortfolioItem portfolioItem = new PortfolioItem();
            portfolioItem.setCode(code);
            portfolioItem.setQuantity(totalQuantity);
            portfolioItem.setDate(buyDate);
            portfolioItem.setAverageBuyPrice(totalBuyPrice/(float)totalQuantity);
            portfolioItem.setPortfolioDetails(details);
            portfolioItems.put(code, portfolioItem);
        }
        
        return portfolioItems;
    }
}
