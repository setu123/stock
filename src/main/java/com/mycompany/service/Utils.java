package com.mycompany.service;

import com.mycompany.dao.ItemDaoImpl;
import com.mycompany.model.DividentHistory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @date May 22, 2015
 * @author Setu
 */
public class Utils {

    private static List<DividentHistory> dividentHistory;
    private static ItemDaoImpl dao = new ItemDaoImpl();
    private static final Map<String, List<DividentHistory>> dividentMap = new HashMap<>();
    public static Date today;
    public static Date yesterday;

    private static List<DividentHistory> getDividentHistory() {
        if (dividentHistory == null) {
            if (dao == null) {
                dao = new ItemDaoImpl();
            }
            try {
                dao.open();
                dividentHistory = dao.getDividentHistory();
                dao.close();
            } catch (SQLException | ClassNotFoundException ex) {
                dividentHistory = new ArrayList<>();
                ex.printStackTrace();
            }
        }
        
        return dividentHistory;
    }

    private static List<DividentHistory> getHistory(String code) {
        List<DividentHistory> historyList = new ArrayList<>();
        for (DividentHistory history : getDividentHistory()) {
            if (history.getCode().equals(code)) {
                historyList.add(history);
            }
        }

        Collections.sort(historyList);
        return historyList;
    }

    public static List<DividentHistory> getDividentHistory(String code) {
        if (dividentMap.get(code) == null) {
            dividentMap.put(code, getHistory(code));
        }
        return dividentMap.get(code);
    }

    static {
        try {
            dao.open();
            today = dao.getToday();
            yesterday = dao.getYesterday();
            dao.close();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    public static Date today() {
//        if (today == null) {
//            try {
//                dao.open();
//                today = dao.getToday();
//                dao.close();
//            } catch (SQLException | ClassNotFoundException ex) {
//                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return today;
//    }
//
//    public static Date yesterday() {
//        if (today == null) {
//            try {
//                dao.open();
//                today = dao.getYesterday();
//                dao.close();
//            } catch (SQLException | ClassNotFoundException ex) {
//                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return yesterday;
//    }
}
