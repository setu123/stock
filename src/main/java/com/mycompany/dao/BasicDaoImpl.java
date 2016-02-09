/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @date Dec 15, 2015
 * @author setu
 */
public class BasicDaoImpl {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/stock";
    static final String USER = "root";
    static final String PASS = "root";
    static final int FACE_VALUE = 10;
    //private final String DATE_FORMAT = "dd/MM/yyyy";
    //private final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    //private static Connection conn;
    protected Connection connection;

    public void open() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            //System.out.println("Connection opened");
        }
    }
    
        public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                //System.out.println("Connection closed");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ItemDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
