/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.mycompany.service.MerchantService;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author setu
 */

@WebServlet(name = "MerchantPortfolioServlet", urlPatterns = {"/merchant/*"})
public class MerchantPortfolioServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MerchantPortfolioServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MerchantPortfolioServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
        
        String uri = request.getRequestURI();
        MerchantService merchantService = MerchantService.getInstance();
        PrintWriter out = response.getWriter();
        
        if (uri.endsWith("/service/start")) {
            try{
                merchantService.startPortfolioIdSync();
                out.println("Service started");
            }catch(Exception ex){
                out.println("Could not start. cause: " + ex.getMessage());
            }
        }else if(uri.endsWith("/service/status")){
            String status = merchantService.getStatus();
            out.println(status);
        }else if(uri.endsWith("/service/stop")){
            String result = merchantService.stopExecutorService();
            out.println(result);
        }else if(uri.endsWith("/service/lastPortfolioId")){
            int lastPortfolioId = merchantService.getLastPortfolioId();
            out.println(lastPortfolioId);
        }else if(uri.endsWith("/service/setPortfolioId")){
            int portfolioId = Integer.parseInt(request.getParameter("id"));
            merchantService.setPortfolioId(portfolioId);
            out.println("Portfolio id set to " + merchantService.getLastPortfolioId());
        }
        
//        processRequest(request, response);
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

}
