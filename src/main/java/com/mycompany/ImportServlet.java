/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.mycompany.service.ImportService;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author Setu
 */
@WebServlet(name = "ImportServlet", urlPatterns = {"/import/*"})
@MultipartConfig()
public class ImportServlet extends HttpServlet {

    private ImportService importService;

    @Override
    public void init() throws ServletException {
        super.init(); //To change body of generated methods, choose Tools | Templates.
        importService = new ImportService(this.getServletContext());
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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            try {
                final Part filePart = request.getPart("file");
                InputStream filecontent = filePart.getInputStream();
                String code = request.getParameter("code");
                importService.importSingleCode(code, filecontent);
            } catch (IOException | ServletException | ParseException | SQLException | ClassNotFoundException ex) {
                System.err.println("Error caught: " + ex.getMessage());
            }

            response.sendRedirect(request.getContextPath());
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
        if (uri.endsWith("/archive")) {
            String code = request.getParameter("code");
            String dayStr = request.getParameter("day");
            int day = 0;
            if(dayStr != null){
                try{
                    day = Integer.parseInt(dayStr);
                }catch(NumberFormatException NFE){}
            }
            if(code == null)
                importService.importArchive(day);
            else
                importService.importArchive(code, day);

            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet SyncServlet</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Data archive imported for " + request.getParameter("code") + "</h1>");
                out.println("</body>");
                out.println("</html>");
            }
        }else if(uri.endsWith("/alphabetic")){
            char alphabet = request.getParameter("alphabet").charAt(0);
            String dayStr = request.getParameter("day");
            int day = 7;
            if(dayStr != null){
                try{
                    day = Integer.parseInt(dayStr);
                }catch(NumberFormatException NFE){}
            }
            importService.importAlphabeticArchive(alphabet, day);
            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet SyncServlet</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Data archive imported for All" + "</h1>");
                out.println("</body>");
                out.println("</html>");
            }
        }else if(uri.endsWith("/archive/asNeeded")){
            String message = importService.importArchiveAsNeeded();
            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet SyncServlet</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>" + message + "</h1>");
                out.println("</body>");
                out.println("</html>");
            }
        }
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
