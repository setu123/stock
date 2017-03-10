package com.mycompany.dao;

import com.mycompany.model.Item;
import com.mycompany.model.MerchantPortfolio;
import com.mycompany.service.Utils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
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

    public boolean setLastCrawledMerchantId(int id){
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
