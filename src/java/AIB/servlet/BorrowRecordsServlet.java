/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package AIB.servlet;

import AIB.DL.BorrowRecord;
import AIB.db.ITP4511_DB;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author andyt
 */
@WebServlet(name = "BorrowRecordsServlet", urlPatterns = {"/BorrowRecordsServlet","/BorrowRecordServlet", "/Shop/BorrowRecords"})
public class BorrowRecordsServlet extends HttpServlet {

    private BorrowRecord borrowedRecords;

    @Override
    public void init() throws ServletException {
        ITP4511_DB db = new ITP4511_DB(
                getServletContext().getInitParameter("dbUrl"),
                getServletContext().getInitParameter("dbUser"),
                getServletContext().getInitParameter("dbPassword"));
        borrowedRecords = new BorrowRecord(db);
        
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
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet BorrowRecordsServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BorrowRecordsServlet at " + request.getContextPath() + "</h1>");
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
        HttpSession session = request.getSession();
        Long shopId = (Long) session.getAttribute("shopId");
        if (shopId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String filter = request.getParameter("filter");
        String sort = request.getParameter("sort");
        try {
            request.setAttribute("records", borrowedRecords.getBorrowRecords(shopId,filter, sort));
            request.getRequestDispatcher("/Shop/borrowRecords.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("Database error", e);
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
        HttpSession session = request.getSession();
        Long shopId = (Long) session.getAttribute("shopId");
        String action = request.getParameter("action");
        long recordId = Long.parseLong(request.getParameter("recordId"));

        try {
            switch (action) {
                case "accept":
                    borrowedRecords.updateRecordState(recordId, "A", shopId);
                    break;
                case "reject":
                    borrowedRecords.updateRecordState(recordId, "R", shopId);
                    break;
                case "complete":
                    borrowedRecords.updateRecordState(recordId, "F", shopId);
                    break;
            }
            response.sendRedirect("BorrowRecordServlet");
        } catch (Exception e) {
            response.sendRedirect("BorrowRecordServlet?error=1");
        }
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
