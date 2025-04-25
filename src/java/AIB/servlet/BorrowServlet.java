/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package AIB.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import AIB.DL.Borrow;
import AIB.Bean.ShopBean;
import AIB.db.ITP4511_DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author andyt
 */
@WebServlet(name = "BorrowServlet", urlPatterns = { "/BorrowServlet", "/Shop/Borrow" })
public class BorrowServlet extends HttpServlet {

    private Borrow borrow;

    @Override
    public void init() throws ServletException {
        super.init();
        ITP4511_DB db = new ITP4511_DB(
                getServletContext().getInitParameter("dbUrl"),
                getServletContext().getInitParameter("dbUser"),
                getServletContext().getInitParameter("dbPassword"));
        borrow = new Borrow(db);
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
            out.println("<title>Servlet BorrowServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BorrowServlet at " + request.getContextPath() + "</h1>");
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
            Long currentShopId = (Long) session.getAttribute("shopId");
            Long cityId = (Long) session.getAttribute("cityId");

            if (currentShopId == null || cityId == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            List<ShopBean> stocks;

            try {
                Long targetShopId = Long.parseLong(request.getParameter("targetShopId"));
                stocks = borrow.getCityShopsStock(currentShopId, cityId, targetShopId);
            } catch (NumberFormatException e) {
                stocks = borrow.getCityShopsStock(currentShopId, cityId);
            }

            request.setAttribute("stocks", stocks);
            request.getRequestDispatcher("/Shop/borrow.jsp").forward(request, response);
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
        HttpSession session = request.getSession();
        Long destShopId = Long.parseLong(request.getParameter("sourceShopId"));
        Long sourceShopId = (Long) session.getAttribute("shopId");

        Map<Long, Integer> items = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            if (param.startsWith("fruit_")) {
                Long fruitId = Long.parseLong(param.substring(6));
                int qty = Integer.parseInt(request.getParameter(param));
                if (qty > 0) {
                    items.put(fruitId, qty);
                }
                
            }
        }

        try {
            if (!items.isEmpty()) {
                long recordId = borrow.createBorrowRequest(sourceShopId, destShopId, items);
                response.sendRedirect("BorrowServlet?success=" + recordId);
            } else {
                response.sendRedirect("BorrowServlet?error=1");
            }
        } catch (SQLException e) {
            response.sendRedirect("BorrowServlet?error=2");
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
