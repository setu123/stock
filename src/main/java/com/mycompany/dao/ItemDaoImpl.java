package com.mycompany.dao;

import com.mycompany.model.DividentHistory;
import com.mycompany.model.Item;
import com.mycompany.model.BasicInfo;
import com.mycompany.service.CustomHashMap;
import com.mycompany.service.Utils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @date Apr 18, 2015
 * @author Setu
 */
public class ItemDaoImpl {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/stock";
    static final String USER = "root";
    static final String PASS = "root";
    static final int FACE_VALUE = 10;
    //private final String DATE_FORMAT = "dd/MM/yyyy";
    //private final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    //private static Connection conn;
    private Connection connection;

    public void open() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connection opened");
        }
    }

    public ItemDaoImpl() {
//        try {
//            createConnection();
//        } catch (SQLException | ClassNotFoundException ex) {
//            Logger.getLogger(ItemDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ItemDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setItems(List<Item> items) throws SQLException, ClassNotFoundException {
        //Insert codes
        //System.out.println("check 1");
        insertCodes(getCodes(items));
        //System.out.println("check 2");

        if (items.get(0).getPressure() == 0) {
            setItemsWithoutBSPressure(items);
            //System.out.println("check 4");
        } else {
            //System.out.println("check 5");
            setItemsWithBSPressure(items);
            //System.out.println("check 6");
        }

        System.out.println("Finished updating " + items.size() + " items");
    }

    public List<Item> getItems(String code) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM bs_pressure WHERE CODE = ?";
        List<Item> items = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, code);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setCode(rs.getString("code"));
                item.setDate(rs.getDate("date"));
                items.add(item);
            }
        }

        return items;
    }

    public int importItems(List<Item> items) throws SQLException, ClassNotFoundException {
        int deleted = removeExistingItems(items);
        System.out.println("deleted: " + deleted);

        String sql = "INSERT INTO bs_pressure (CODE, DATE, OPEN_PRICE, CLOSE_PRICE, DAY_HIGH, DAY_LOW, VOLUME, LAST_PRICE, TRADE, YESTERDAY_CLOSE_PRICE, VALUE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int rowEffected = 0;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (Item item : items) {
            preparedStatement.setString(1, item.getCode());
            preparedStatement.setDate(2, getSqlDate(item.getDate()));
            preparedStatement.setFloat(3, item.getOpenPrice());
            preparedStatement.setFloat(4, item.getClosePrice());
            preparedStatement.setFloat(5, item.getDayHigh());
            preparedStatement.setFloat(6, item.getDayLow());
            preparedStatement.setInt(7, item.getVolume());
            preparedStatement.setFloat(8, item.getLastPrice());
            preparedStatement.setFloat(9, item.getTrade());
            preparedStatement.setFloat(10, item.getYesterdayClosePrice());
            preparedStatement.setFloat(11, item.getValue());
            preparedStatement.addBatch();
        }
        int[] batchResult = preparedStatement.executeBatch();

        for (int i : batchResult) {
            rowEffected += i;
        }

        return rowEffected;
    }

    private int removeExistingItems(List<Item> items) throws SQLException, ClassNotFoundException {
        if (items == null) {
            return 0;
        }

        String sql = "DELETE FROM bs_pressure WHERE CODE = ? AND DATE = ? ";
        int rowEffected = 0;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (Item item : items) {
            preparedStatement.setString(1, item.getCode());
            preparedStatement.setDate(2, getSqlDate(item.getDate()));
            preparedStatement.addBatch();
        }
        int[] batchResult = preparedStatement.executeBatch();

        for (int i : batchResult) {
            rowEffected += i;
        }

        return rowEffected;
    }

    private java.sql.Date getSqlDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime());
    }

    private void setItemsWithBSPressure(List<Item> items) throws ClassNotFoundException, SQLException {
        String sql = "UPDATE bs_pressure SET PRESSURE=?, OPEN_PRICE=?, LAST_PRICE=?, TRADE=?, CLOSE_PRICE=?, VOLUME=?, VALUE=?, DAY_HIGH=?, DAY_LOW=?, YESTERDAY_CLOSE_PRICE=?, LAST_UPDATED=NOW() WHERE CODE=? AND DATE=?";
        PreparedStatement preparedStatement;
        Date today = new Date(new java.util.Date().getTime());

        try {
            preparedStatement = connection.prepareStatement(sql);
            for (Item item : items) {
                preparedStatement.setFloat(1, item.getPressure());
                preparedStatement.setFloat(2, item.getOpenPrice());
                preparedStatement.setFloat(3, item.getLastPrice());
                preparedStatement.setInt(4, item.getTrade());
                preparedStatement.setFloat(5, item.getClosePrice());
                preparedStatement.setInt(6, item.getVolume());
                preparedStatement.setFloat(7, item.getValue());
                preparedStatement.setFloat(8, item.getDayHigh());
                preparedStatement.setFloat(9, item.getDayLow());
                preparedStatement.setFloat(10, item.getYesterdayClosePrice());
                preparedStatement.setString(11, item.getCode());
                preparedStatement.setDate(12, today);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } finally {
//            if (preparedStatement != null) {
//                preparedStatement.close();
//            }
        }
    }

    private void setItemsWithoutBSPressure(List<Item> items) throws ClassNotFoundException, SQLException {
        String sql = "UPDATE bs_pressure SET OPEN_PRICE=?, LAST_PRICE=?, TRADE=?, CLOSE_PRICE=?, VOLUME=?, VALUE=?, DAY_HIGH=?, DAY_LOW=?, YESTERDAY_CLOSE_PRICE=?, LAST_UPDATED=NOW() WHERE CODE=? AND DATE=?";
        PreparedStatement preparedStatement;
        Date today = new Date(new java.util.Date().getTime());

        try {
            preparedStatement = connection.prepareStatement(sql);
            for (Item item : items) {
                preparedStatement.setFloat(1, item.getOpenPrice());
                preparedStatement.setFloat(2, item.getLastPrice());
                preparedStatement.setInt(3, item.getTrade());
                preparedStatement.setFloat(4, item.getClosePrice());
                preparedStatement.setInt(5, item.getVolume());
                preparedStatement.setFloat(6, item.getValue());
                preparedStatement.setFloat(7, item.getDayHigh());
                preparedStatement.setFloat(8, item.getDayLow());
                preparedStatement.setFloat(9, item.getYesterdayClosePrice());
                preparedStatement.setString(10, item.getCode());
                preparedStatement.setDate(11, today);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } finally {
//            if (preparedStatement != null) {
//                preparedStatement.close();
//            }
        }
    }

    public List<Item> getHammer() throws SQLException, ClassNotFoundException {
        String sql = "SELECT CODE, HAMMERVALUE(DAY_HIGH, OPEN_PRICE, closeprice(LAST_PRICE,CLOSE_PRICE), DAY_LOW) AS HAMMER FROM bs_pressure WHERE DATE = (SELECT MAX(DATE) FROM bs_pressure) ";
        List<Item> items = new ArrayList<>();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            DecimalFormat df = new DecimalFormat("#.#");
            while (rs.next()) {
                String code = rs.getString("code");
                String hammerStr = rs.getString("hammer");
                float hammer = 0;
                if (hammerStr != null) {
                    hammer = Float.parseFloat(hammerStr);
                    hammerStr = df.format(hammer);
                    hammer = Float.parseFloat(hammerStr);
                }

                Item item = new Item();
                item.setCode(code);
                item.setHammer(hammer);
                items.add(item);
            }
        }

        return items;
    }

    @Deprecated
    public List<Item> getVolumeChange() throws SQLException, ClassNotFoundException {
        String sql = "SELECT CODE, VOLUME/(SELECT AVG(VOLUME) FROM bs_pressure WHERE CODE=BS.CODE AND DATE<=?) AS VCHANGE FROM bs_pressure BS WHERE DATE = ?";
        List<Item> items = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, getSqlDate(Utils.yesterday));
            stmt.setDate(2, getSqlDate(Utils.today));
            ResultSet rs = stmt.executeQuery();
            DecimalFormat df = new DecimalFormat("#.#");
            while (rs.next()) {
                String code = rs.getString("code");
                String vchangeStr = rs.getString("vchange");
                float vchange = 0;
                if (vchangeStr != null) {
                    vchange = Float.parseFloat(vchangeStr);
                    vchangeStr = df.format(vchange);
                    vchange = Float.parseFloat(vchangeStr);
                }

                Item item = new Item();
                item.setCode(code);
                item.setVolumeChange(vchange);
                items.add(item);
            }
        }

        return items;
    }

    public List<Item> getTradeChange() throws SQLException, ClassNotFoundException {
        String sql = "SELECT CODE, TRADE/(SELECT AVG(TRADE) FROM bs_pressure WHERE CODE=BS.CODE AND DATE<=? AND TRADE>0) AS TCHANGE FROM bs_pressure BS WHERE DATE = ?";
        List<Item> items = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, getSqlDate(Utils.yesterday));
            stmt.setDate(2, getSqlDate(Utils.today));
            ResultSet rs = stmt.executeQuery();
            DecimalFormat df = new DecimalFormat("#.#");
            while (rs.next()) {
                String code = rs.getString("code");
                String tchangeStr = rs.getString("tchange");
                float tchange = 0;
                if (tchangeStr != null) {
                    tchange = Float.parseFloat(tchangeStr);
                    tchangeStr = df.format(tchange);
                    tchange = Float.parseFloat(tchangeStr);
                }

                Item item = new Item();
                item.setCode(code);
                item.setTradeChange(tchange);
                items.add(item);
            }
        }

        return items;
    }

    public List<Item> getCandleLengthChange() throws SQLException, ClassNotFoundException {
        String sql = "SELECT CODE, (CLOSEPRICE(LAST_PRICE,CLOSE_PRICE)-OPEN_PRICE)/(SELECT ABS(AVG(OPEN_PRICE-CLOSEPRICE(LAST_PRICE,CLOSE_PRICE))) FROM bs_pressure WHERE OPEN_PRICE<CLOSEPRICE(LAST_PRICE,CLOSE_PRICE) AND CODE=BS.CODE AND DATE<=?) AS CCHANGE FROM bs_pressure BS WHERE DATE = ?";
        List<Item> items = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, getSqlDate(Utils.yesterday));
            stmt.setDate(2, getSqlDate(Utils.today));
            ResultSet rs = stmt.executeQuery();
            DecimalFormat df = new DecimalFormat("#.#");
            while (rs.next()) {
                String code = rs.getString("code");
                String cchangeStr = rs.getString("cchange");
                float cchange = 0;
                if (cchangeStr != null) {
                    cchange = Float.parseFloat(cchangeStr);
                    cchangeStr = df.format(cchange);
                    cchange = Float.parseFloat(cchangeStr);
                }

                Item item = new Item();
                item.setCode(code);
                item.setcLengthChange(cchange);
                items.add(item);
            }
        }

        return items;
    }

    public List<Item> getConsecutiveGreen() throws SQLException, ClassNotFoundException {

//        String sql = "SELECT DISTINCT(BS1.CODE) FROM (SELECT * FROM bs_pressure WHERE DATE=?) AS BS1, (SELECT * FROM bs_pressure WHERE DATE=?) AS BS2\n"
//                + "WHERE CLOSEPRICE(BS1.LAST_PRICE,BS1.CLOSE_PRICE)>BS1.OPEN_PRICE\n"
//                + "AND BS1.CODE = BS2.CODE\n"
//                + "AND CLOSEPRICE(BS1.LAST_PRICE,BS1.CLOSE_PRICE)>BS1.YESTERDAY_CLOSE_PRICE\n"
//                + "AND BS2.CLOSE_PRICE>BS2.OPEN_PRICE\n"
//                + "AND BS2.CLOSE_PRICE>BS2.YESTERDAY_CLOSE_PRICE\n"
//                + "ORDER BY BS1.CODE";
        String sql = "SELECT DISTINCT(BS1.CODE) FROM (SELECT * FROM bs_pressure WHERE DATE=?) AS BS1, (SELECT * FROM bs_pressure WHERE DATE=?) AS BS2\n"
                + "WHERE BS1.CODE = BS2.CODE\n"
                + "AND (CLOSEPRICE(BS1.LAST_PRICE,BS1.CLOSE_PRICE)-BS1.YESTERDAY_CLOSE_PRICE)/BS1.YESTERDAY_CLOSE_PRICE >= 0.01\n"
                + "AND (BS2.CLOSE_PRICE-BS2.YESTERDAY_CLOSE_PRICE)/BS2.YESTERDAY_CLOSE_PRICE >= 0.005\n"
                + "ORDER BY BS1.CODE";

        List<Item> items = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, getSqlDate(Utils.today));
            stmt.setDate(2, getSqlDate(Utils.yesterday));
            ResultSet rs = stmt.executeQuery();
            //System.out.print("CGreen: " + ", today: " + getSqlDate(Utils.today) + ", yesterday: " + getSqlDate(Utils.yesterday));
            while (rs.next()) {
                String code = rs.getString("code");
                //System.out.print(code + " " );
                Item item = new Item();
                item.setCode(code);
                items.add(item);
            }
            //System.out.println("");
        }

        return items;
    }

    public Date getToday() throws SQLException {
        String sql = "SELECT MAX(DATE) AS DATE FROM bs_pressure";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getDate("DATE");
        }
    }

    public Date getYesterday() throws SQLException {
        String sql = "SELECT MAX(DATE) AS DATE FROM bs_pressure WHERE DATE < (SELECT MAX(DATE) FROM bs_pressure)";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            return rs.getDate("DATE");
        }
    }

    public void setYearStatistics(List<Item> items) throws ClassNotFoundException, SQLException {
        //Insert codes
        insertCodesToYearStatistics(getCodes(items));

        String sql = "UPDATE year_statistics SET LOW=?, HIGH=?, DATE=?, SECTOR=?, FACEVALUE=?, TOTALSECURITY=?, AUTHORIZEDCAPITAL=?, PAIDUPCAPITAL=?, YEAREND=?, RESERVE=?, PE=?, CATEGORY=?, DIRECTOR=?, GOVERNMENT=?, INSTITUTE=?, FOREIN=?, PUBLIC=? WHERE CODE=?";
        PreparedStatement preparedStatement;
        Date today = new Date(new java.util.Date().getTime());

        try {
            preparedStatement = connection.prepareStatement(sql);
            for (BasicInfo item : items) {
                preparedStatement.setFloat(1, item.getLow());
                preparedStatement.setFloat(2, item.getHigh());
                preparedStatement.setDate(3, today);
                preparedStatement.setString(4, item.getSector());
                preparedStatement.setInt(5, item.getFaceValue());
                preparedStatement.setInt(6, item.getTotalSecurity());
                preparedStatement.setFloat(7, item.getAuthorizedCapital());
                preparedStatement.setFloat(8, item.getPaidUpCapital());
                preparedStatement.setString(9, item.getYearEnd());
                preparedStatement.setFloat(10, item.getReserve());
                preparedStatement.setFloat(11, item.getPE());
                preparedStatement.setString(12, item.getCategory());
                preparedStatement.setFloat(13, item.getSharePercentage().getDirector());
                preparedStatement.setFloat(14, item.getSharePercentage().getGovernment());
                preparedStatement.setFloat(15, item.getSharePercentage().getInstitute());
                preparedStatement.setFloat(16, item.getSharePercentage().getForeign());
                preparedStatement.setFloat(17, item.getSharePercentage().getPublics());
                preparedStatement.setString(18, item.getCode());
                //System.out.println("code: " + item.getCode() + "governament: " + item.getSharePercentage().getGovernment() + ", insti: " + item.getSharePercentage().getInstitute() + ", foreign: " + item.getSharePercentage().getForeign());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } finally {
//            if (preparedStatement != null) {
//                preparedStatement.close();
//            }
        }
    }

    public List<Item> getBSPressure() throws SQLException, ClassNotFoundException {
        //String sql = "SELECT * FROM bs_pressure WHERE DATE = (SELECT MAX(DATE) FROM bs_pressure) ORDER BY PRESSURE DESC";
        String sql = "SELECT * FROM bs_pressure WHERE DATE = (SELECT MAX(DATE) FROM bs_pressure) ORDER BY PRESSURE DESC";
        List<Item> items;
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            items = new ArrayList<>();
            while (rs.next()) {
                Item item = new Item();
                item.setCode(rs.getString("code"));
                item.setDate(rs.getDate("date"));
                item.setPressure(rs.getFloat("pressure"));
                items.add(item);
            }
        }
        return items;
    }

    @Deprecated
    public CustomHashMap getOne_YearData() throws SQLException, ClassNotFoundException {
        //String sql = "SELECT CODE, CLOSEPRICE(LAST_PRICE, CLOSE_PRICE) AS CLOSE_PRICE, DATE FROM bs_pressure WHERE DATE IN (SELECT * FROM (SELECT DISTINCT(DATE) FROM bs_pressure ORDER BY DATE ) AS T) ORDER BY DATE;";
        String sql = "SELECT DATE, CODE, OPEN_PRICE, CLOSEPRICE(LAST_PRICE, CLOSE_PRICE) AS CLOSE_PRICE, YESTERDAY_CLOSE_PRICE, DAY_LOW, DAY_HIGH, DATE, VOLUME, TRADE, VALUE FROM bs_pressure WHERE DATE >= ? ORDER BY DATE";
        List<Item> items;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Calendar oneYearBack = Calendar.getInstance();
            oneYearBack.add(Calendar.YEAR, -1);

            preparedStatement.setDate(1, getSqlDate(oneYearBack.getTime()));
            ResultSet rs = preparedStatement.executeQuery();
            items = new ArrayList<>();

            while (rs.next()) {
                Item item = new Item();
                item.setDate(rs.getDate("date"));
                item.setCode(rs.getString("code"));
                item.setOpenPrice(rs.getFloat("open_price"));
                item.setClosePrice(rs.getFloat("close_price"));
                item.setYesterdayClosePrice(rs.getFloat("yesterday_close_price"));
                item.setLow(rs.getFloat("day_low"));
                item.setHigh(rs.getFloat("day_high"));
                item.setDate(rs.getDate("date"));
                item.setVolume(rs.getInt("volume"));
                item.setValue(rs.getFloat("value"));
                item.setTrade(rs.getInt("trade"));
                calculateAdjustedClosePrice(item);
                calculateAdjustedVolume(item);
                items.add(item);
            }
        }

        CustomHashMap cMap = getItemizedData(items);
        for (String code : cMap.keySet()) {
            items = cMap.getItems(code);
            items.get(0).setAdjustedYesterdayClosePrice(items.get(0).getYesterdayClosePrice());
            for (int i = 1; i < items.size(); i++) {
                calculateAdjustedYesterdayClosePrice(items.get(i), items.get(i - 1).getDate());
            }
        }

        return cMap;
    }

    public CustomHashMap getData(int days) throws SQLException, ClassNotFoundException {
        //String sql = "SELECT CODE, CLOSEPRICE(LAST_PRICE, CLOSE_PRICE) AS CLOSE_PRICE, DATE FROM bs_pressure WHERE DATE IN (SELECT * FROM (SELECT DISTINCT(DATE) FROM bs_pressure ORDER BY DATE ) AS T) ORDER BY DATE;";
        String sql = "SELECT DATE, CODE, OPEN_PRICE, CLOSEPRICE(LAST_PRICE, CLOSE_PRICE) AS CLOSE_PRICE, YESTERDAY_CLOSE_PRICE, DAY_LOW, DAY_HIGH, DATE, VOLUME, TRADE, VALUE FROM bs_pressure WHERE DATE >= ? ORDER BY DATE";
        List<Item> items;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            Calendar oneYearBack = Calendar.getInstance();
            oneYearBack.add(Calendar.DAY_OF_YEAR, -days);

            preparedStatement.setDate(1, getSqlDate(oneYearBack.getTime()));
            ResultSet rs = preparedStatement.executeQuery();
            items = new ArrayList<>();

            while (rs.next()) {
                Item item = new Item();
                item.setDate(rs.getDate("date"));
                item.setCode(rs.getString("code"));
                item.setOpenPrice(rs.getFloat("open_price"));
                item.setClosePrice(rs.getFloat("close_price"));
                item.setYesterdayClosePrice(rs.getFloat("yesterday_close_price"));
                item.setLow(rs.getFloat("day_low"));
                item.setHigh(rs.getFloat("day_high"));
                item.setDate(rs.getDate("date"));
                item.setVolume(rs.getInt("volume"));
                item.setValue(rs.getFloat("value"));
                item.setTrade(rs.getInt("trade"));
                calculateAdjustedClosePrice(item);
                calculateAdjustedVolume(item);
                items.add(item);
            }
        }

        CustomHashMap cMap = getItemizedData(items);
        for (String code : cMap.keySet()) {
            items = cMap.getItems(code);
            items.get(0).setAdjustedYesterdayClosePrice(items.get(0).getYesterdayClosePrice());
            for (int i = 1; i < items.size(); i++) {
                calculateAdjustedYesterdayClosePrice(items.get(i), items.get(i - 1).getDate());
            }
        }

        return cMap;
    }
    
    private CustomHashMap getItemizedData(List<Item> items) {
        CustomHashMap cMap = new CustomHashMap();
        for (Item item : items) {
            cMap.putItem(item);
        }
        return cMap;
    }

    private void calculateAdjustedYesterdayClosePrice(Item item, java.util.Date yesterday) {
        List<DividentHistory> history = Utils.getDividentHistory(item.getCode());

        float adjustedPrice = item.getYesterdayClosePrice();
        float factor;
        java.util.Date today = new java.util.Date();
        for (DividentHistory divident : history) {
            if (yesterday.before(divident.getDate()) && divident.getDate().before(today)) {
                switch (divident.getType()) {
                    case CASH:
                        adjustedPrice = adjustedPrice - (FACE_VALUE * divident.getPercent()) / 100;
                        break;
                    case STOCK:
                        factor = 1 / (1 + (divident.getPercent() / 100));
                        adjustedPrice = adjustedPrice * factor;
                        break;
                    case RIGHT:
                        int baseQuantity = Math.round(100 / divident.getPercent());
                        adjustedPrice = ((adjustedPrice * baseQuantity) + item.getIssuePrice()) / (baseQuantity + 1);
                        break;
                    case SPLIT:
                        factor = 1 / (1 + (divident.getPercent() / 100));
                        adjustedPrice = adjustedPrice * factor;
                        break;
                }
            }
        }
        //if(item.getCode().equals("NPOLYMAR"))
        //System.out.println("Code: " + item.getCode() + ", date: " + item.getDate() + ", closePrice: " + item.getClosePrice() + ", adjustedPrice: " + adjustedPrice);
        item.setAdjustedYesterdayClosePrice(adjustedPrice);
    }

    private void calculateAdjustedClosePrice(Item item) {
        List<DividentHistory> history = Utils.getDividentHistory(item.getCode());

        float adjustedClosePrice = item.getClosePrice();
        float adjustedOpenPrice = item.getOpenPrice();
        float adjustedHigh = item.getHigh();
        float adjustedLow = item.getLow();
        float factor = 0;
        java.util.Date today = new java.util.Date();
        for (DividentHistory divident : history) {
            if (item.getDate().before(divident.getDate()) && divident.getDate().before(today)) {
                switch (divident.getType()) {
                    case CASH:
                        adjustedClosePrice = adjustedClosePrice - (FACE_VALUE * divident.getPercent()) / 100;
                        adjustedOpenPrice = adjustedOpenPrice - (FACE_VALUE * divident.getPercent()) / 100;
                        adjustedHigh = adjustedHigh - (FACE_VALUE * divident.getPercent()) / 100;
                        adjustedLow = adjustedLow - (FACE_VALUE * divident.getPercent()) / 100;
                        break;
                    case STOCK:
                        factor = 1 / (1 + (divident.getPercent() / 100));
                        adjustedClosePrice = adjustedClosePrice * factor;
                        adjustedOpenPrice = adjustedOpenPrice * factor;
                        adjustedHigh = adjustedHigh * factor;
                        adjustedLow = adjustedLow * factor;
                        break;
                    case RIGHT:
                        int baseQuantity = Math.round(100 / divident.getPercent());
                        adjustedClosePrice = ((adjustedClosePrice * baseQuantity) + item.getIssuePrice()) / (baseQuantity + 1);
                        adjustedHigh = ((adjustedHigh * baseQuantity) + item.getIssuePrice()) / (baseQuantity + 1);
                        adjustedLow = ((adjustedLow * baseQuantity) + item.getIssuePrice()) / (baseQuantity + 1);
                        break;
                    case SPLIT:
                        factor = 1 / (1 + (divident.getPercent() / 100));
                        adjustedClosePrice = adjustedClosePrice * factor;
                        adjustedOpenPrice = adjustedOpenPrice * factor;
                        adjustedHigh = adjustedHigh * factor;
                        adjustedLow = adjustedLow * factor;
                        break;
                }
            }
        }

        item.setAdjustedClosePrice(adjustedClosePrice);
        item.setOpenPrice(adjustedOpenPrice);
        item.setHigh(adjustedHigh);
        item.setLow(adjustedLow);
    }

    private void calculateAdjustedVolume(Item item) {
        List<DividentHistory> history = Utils.getDividentHistory(item.getCode());

        float adjustedPrice = item.getAdjustedClosePrice();
        if (adjustedPrice == 0) {
            adjustedPrice = item.getClosePrice();
        }
        int adjustedVolume = item.getVolume();
        float factor;
        java.util.Date today = new java.util.Date();

        for (DividentHistory divident : history) {
            if (item.getDate().before(divident.getDate()) && divident.getDate().before(today)) {
                switch (divident.getType()) {
                    case STOCK:
                        factor = 1 + (divident.getPercent() / 100);
                        adjustedVolume = Math.round(adjustedVolume * factor);
                        break;
                    case RIGHT:
                        int baseQuantity = Math.round(100 / divident.getPercent());
                        factor = ((baseQuantity + 1) / ((adjustedPrice * baseQuantity) + item.getIssuePrice())) * adjustedPrice;
                        adjustedVolume = Math.round(adjustedVolume * factor);
                        break;
                    case SPLIT:
                        factor = 1 + (divident.getPercent() / 100);
                        adjustedVolume = Math.round(adjustedVolume * factor);
                        break;
                }
            }
        }
        //if(item.getCode().equals("NPOLYMAR"))
        //System.out.println("Code: " + item.getCode() + ", date: " + item.getDate() + ", closePrice: " + item.getClosePrice() + ", adjustedPrice: " + adjustedPrice);
        item.setAdjustedVolume(adjustedVolume);
    }

    private List<String> getCodes(List items) {
        List<String> codes = new ArrayList<>();
        for (Object item : items) {
            codes.add(((BasicInfo) item).getCode());
        }

        return codes;
    }

    private void insertCodes(List<String> codes) throws SQLException, ClassNotFoundException {
        PreparedStatement pStatement;

        try {
            //Check which codes to insert
            String sql = "SELECT CODE FROM bs_pressure WHERE DATE = ?";
            pStatement = connection.prepareStatement(sql);
            Date today = new Date(new java.util.Date().getTime());
            pStatement.setDate(1, today);
            ResultSet rs = pStatement.executeQuery();

            while (rs.next()) {
                codes.remove(rs.getString("CODE"));
            }

            if (codes.isEmpty()) {
                return; //Nothing to insert
            }
            //Insert
            sql = "INSERT INTO bs_pressure (CODE, DATE) VALUES (?, ?)";
            pStatement = connection.prepareStatement(sql);
            for (String code : codes) {
                pStatement.setString(1, code);
                pStatement.setDate(2, today);
                pStatement.addBatch();
            }
            pStatement.executeBatch();
        } finally {
//            if (pStatement != null) {
//                pStatement.close();
//            }
        }
    }

    private void insertCodesToYearStatistics(List<String> codes) throws SQLException, ClassNotFoundException {
        PreparedStatement pStatement;

        try {
            //Check which codes to insert
            String sql = "SELECT CODE FROM year_statistics";
            pStatement = connection.prepareStatement(sql);
            ResultSet rs = pStatement.executeQuery();

            while (rs.next()) {
                codes.remove(rs.getString("CODE"));
            }

            if (codes.isEmpty()) {
                return; //Nothing to insert
            }
            //Insert
            sql = "INSERT INTO year_statistics (CODE, DATE) VALUES (?, ?)";
            pStatement = connection.prepareStatement(sql);
            Date today = new Date(new java.util.Date().getTime());
            for (String code : codes) {
                pStatement.setString(1, code);
                pStatement.setDate(2, today);
                pStatement.addBatch();
            }
            pStatement.executeBatch();
        } finally {
//            if (pStatement != null) {
//                pStatement.close();
//            }
        }
    }

    public List<DividentHistory> getDividentHistory() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM divident_history";
        List<DividentHistory> dividents = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                DividentHistory divident = new DividentHistory();
                divident.setCode(rs.getString("code"));
                divident.setDate(rs.getDate("date"));
                divident.setPercent(rs.getFloat("percent"));
                String type = rs.getString("type");
                divident.setType(getDividentType(type));
                dividents.add(divident);
            }
        }

        return dividents;
    }

    private DividentHistory.DividentType getDividentType(String type) {
        DividentHistory.DividentType dividentType = null;
        switch (type) {
            case "CASH":
                dividentType = DividentHistory.DividentType.CASH;
                break;
            case "STOCK":
                dividentType = DividentHistory.DividentType.STOCK;
                break;
            case "RIGHT":
                dividentType = DividentHistory.DividentType.RIGHT;
                break;
            case "SPLIT":
                dividentType = DividentHistory.DividentType.SPLIT;
                break;
        }
        return dividentType;
    }
}
