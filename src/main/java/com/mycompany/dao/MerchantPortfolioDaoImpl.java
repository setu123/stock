
package com.mycompany.dao;

import com.mycompany.model.Item;
import com.mycompany.model.MerchantPortfolio;
import com.mycompany.service.Utils;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @date Feb 23, 2017
 * @author setu
 */
public class MerchantPortfolioDaoImpl  extends BasicDaoImpl{
public int importMerchantPortfolios(List<MerchantPortfolio> merchantPortfolios) throws SQLException, ClassNotFoundException {

        String sql = "INSERT INTO merchant_portfolio (REMOTE_ID, AMOUNT, LAST_ACTIVITY) VALUES (?, ?, ?)";
        int rowEffected = 0;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (MerchantPortfolio portfolio : merchantPortfolios) {
            preparedStatement.setInt(1, portfolio.getRemoteId());
            preparedStatement.setDouble(2, portfolio.getAmount());
            preparedStatement.setDate(3, Utils.getSqlDate(portfolio.getLastActivity()));
            preparedStatement.addBatch();
        }
        int[] batchResult = preparedStatement.executeBatch();

        for (int i : batchResult) {
            rowEffected += i;
        }

        return rowEffected;
    }
}
