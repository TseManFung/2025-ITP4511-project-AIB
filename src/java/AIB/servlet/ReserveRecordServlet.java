/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package AIB.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import AIB.Bean.ReserveRecordBean;
import AIB.db.ITP4511_DB;
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
@WebServlet(name = "ReserveRecordServlet", urlPatterns = { "/ReserveRecordServlet" })
public class ReserveRecordServlet extends HttpServlet {
    private ReserveRecordBean recordBean;

    @Override
    public void init() throws ServletException {
        super.init();
        ITP4511_DB db = new ITP4511_DB(
                getServletContext().getInitParameter("dbUrl"),
                getServletContext().getInitParameter("dbUser"),
                getServletContext().getInitParameter("dbPassword"));
        recordBean = new ReserveRecordBean(db);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ReserveRecordServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ReserveRecordServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            Long shopId = (Long) session.getAttribute("shopId");
            if (shopId == null) {
                response.sendRedirect("../login.jsp");
                return;
            }

            String filter = request.getParameter("filter");
            String sort = request.getParameter("sort");

            List<Map<String, Object>> records = recordBean.getReserveRecords(shopId, filter, sort);
            request.setAttribute("records", records);
            request.getRequestDispatcher("/Shop/reserveRecords.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long reserveId = Long.parseLong(request.getParameter("reserveId"));
            if (recordBean.completeReservation(reserveId)) {
                response.sendRedirect("reserveRecords?success=1");
            } else {
                response.sendRedirect("reserveRecords?error=1");
            }
        } catch (Exception e) {
            response.sendRedirect("reserveRecords?error=2");
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
