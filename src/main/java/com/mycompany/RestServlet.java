/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.mycompany.model.Item;
import com.mycompany.model.ItemMixin;
import com.mycompany.service.ScannerService;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Setu
 */
@WebServlet(name = "RestServlet", urlPatterns = {"/getBSPressure"})
public class RestServlet extends HttpServlet {
    
    private ObjectMapper objectMapper;
    private static final int NUMBER_OF_ITEM_TO_RETURN = 100;
    private ScannerService scannerService;

    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper = new ObjectMapper();
        objectMapper.getSerializationConfig().addMixInAnnotations(Item.class, ItemMixin.class);
        scannerService = new ScannerService();
    }
    
    

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Calendar start = Calendar.getInstance();
        
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            
            List<Item> items = scannerService.getItemsWithscannedProperties();
            
            try (PrintWriter out = response.getWriter()) {
                objectMapper.writeValue(out, items);
            }
        }   catch (SQLException | ClassNotFoundException | IllegalStateException ex) {
            Logger.getLogger(RestServlet.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        
        Calendar end = Calendar.getInstance();
        long elapsedTime = (end.getTimeInMillis() - start.getTimeInMillis())/1000;
        System.out.println("Data given in " + elapsedTime + " seconds. time: " + new Date());
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
//    private abstract class ItemMixin{
//        @JsonIgnore abstract int getTrade();
//    }

}
