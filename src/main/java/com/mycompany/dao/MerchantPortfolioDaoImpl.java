package com.mycompany.dao;

import com.mycompany.model.MerchantPortfolio;
import com.mycompany.model.PortfolioDetails;
import com.mycompany.service.Utils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @date Feb 23, 2017
 * @author setu
 */
public class MerchantPortfolioDaoImpl extends BasicDaoImpl {

    public int importMerchantPortfolios(List<MerchantPortfolio> merchantPortfolios) throws SQLException, ClassNotFoundException {

        String sql = "INSERT INTO merchant_portfolio (REMOTE_ID, AMOUNT, LAST_ACTIVITY) VALUES (?, ?, ?)  ON DUPLICATE KEY UPDATE AMOUNT=?";
        int rowEffected = 0;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (MerchantPortfolio portfolio : merchantPortfolios) {
            preparedStatement.setInt(1, portfolio.getRemoteId());
            preparedStatement.setDouble(2, portfolio.getAmount());
            preparedStatement.setDate(3, Utils.getSqlDate(portfolio.getLastActivity()));
            preparedStatement.setDouble(4, portfolio.getAmount());
            preparedStatement.addBatch();
        }
        int[] batchResult = preparedStatement.executeBatch();

        for (int i : batchResult) {
            rowEffected += i;
        }

        return rowEffected;
    }

    public int getLastCrawledMerchantId() {
        int id = -1;
        String sql = "SELECT ID FROM last_crawled_merchant";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                id = rs.getInt("ID");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return id;
    }

    public List<MerchantPortfolio> getPortfolios() {
        List<MerchantPortfolio> portfolioList = new ArrayList<>();
        String sql = "SELECT * FROM merchant_portfolio ORDER BY REMOTE_ID";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("ID");
                int remoteId = rs.getInt("REMOTE_ID");
                float amount = rs.getFloat("AMOUNT");
                Date lastActivity = rs.getDate("LAST_ACTIVITY");
                MerchantPortfolio portfolio = new MerchantPortfolio(remoteId, amount, lastActivity);
                portfolio.setId(id);
                portfolioList.add(portfolio);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return portfolioList;
    }

    public void clearDetails() {
        try {
            String sql = "DELETE FROM merchant_portfolio_details";
            Statement statement = connection.prepareStatement(sql);
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            System.out.println("Error caught in clearing details");
            ex.printStackTrace();
        }
    }

    public void updatePortfolioDetails(List<PortfolioDetails> portfolioDetails) throws SQLException {
        if (portfolioDetails == null) {
            return;
        }

        PreparedStatement pStatement;

        Set<String> portfolioIdList = new HashSet<>();
        for (PortfolioDetails details : portfolioDetails) {
            portfolioIdList.add(details.getPortfolio() + "");
        }

        int debugPortfolioInternalId = -1;
        try {
            //Delete existing portfolio details
            String sql = "DELETE FROM merchant_portfolio_details WHERE PORTFOLIO IN (" + String.join(",", portfolioIdList) + ")";
            pStatement = connection.prepareStatement(sql);
            //System.out.println("pstatment: " + pStatement + ", por: " + portfolioDetails.get(0));
//            Array array = connection.createArrayOf("INT", portfolioIdList.toArray());
//            pStatement.setArray(1, array);
            pStatement.executeUpdate();

            //Insert portfolio details
            sql = "INSERT INTO merchant_portfolio_details (PORTFOLIO, CODE, QUANTITY, BUY_PRICE, COMMENTS, DATE) VALUES (?, ?, ?, ?, ?, ?)";
            pStatement = connection.prepareStatement(sql);
            for (PortfolioDetails portfolioDetail : portfolioDetails) {
                debugPortfolioInternalId = portfolioDetail.getPortfolio();
                pStatement.setInt(1, portfolioDetail.getPortfolio());
                pStatement.setString(2, portfolioDetail.getCode());
                pStatement.setInt(3, portfolioDetail.getQuantity());
                pStatement.setFloat(4, portfolioDetail.getBuyPrice());
                pStatement.setString(5, portfolioDetail.getComments());
                pStatement.setDate(6, new java.sql.Date(portfolioDetail.getDate().getTime()));
                pStatement.addBatch();
            }
            pStatement.executeBatch();

            //Update last_update time in portfolio
            sql = "UPDATE merchant_portfolio SET LAST_UPDATE = ? WHERE ID = ?";
            pStatement = connection.prepareStatement(sql);
            for (PortfolioDetails portfolioDetail : portfolioDetails) {
                pStatement.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
                pStatement.setInt(2, portfolioDetail.getPortfolio());
                pStatement.addBatch();
            }
            pStatement.executeBatch();
        } catch (SQLException ex) {
            System.out.println("Error caught for id: " + debugPortfolioInternalId);
            ex.printStackTrace();
            throw ex;
        } finally {
        }
    }

    public boolean setLastCrawledMerchantId(int id) {
        boolean status = false;
        try {
            String sql = "UPDATE last_crawled_merchant SET ID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            status = preparedStatement.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return status;
    }
}
