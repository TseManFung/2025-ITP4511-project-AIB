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

import AIB.DL.StockUpdate;
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
@WebServlet(name = "StockUpdateServlet", urlPatterns = { "/StockUpdateServlet", "/Shop/StockUpdate" })
public class StockUpdateServlet extends HttpServlet {

    private StockUpdate stockBean;

    @Override
    public void init() throws ServletException {
        super.init();
        ITP4511_DB db = new ITP4511_DB(
                getServletContext().getInitParameter("dbUrl"),
                getServletContext().getInitParameter("dbUser"),
                getServletContext().getInitParameter("dbPassword"));
        stockBean = new StockUpdate(db);
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
            out.println("<title>Servlet StockUpdateServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StockUpdateServlet at " + request.getContextPath() + "</h1>");
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
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            Map<Long, Map<String, Object>> stock = stockBean.getShopStock(shopId);
            request.setAttribute("stock", stock);
            request.getRequestDispatcher("/Shop/updateStock.jsp").forward(request, response);
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
            HttpSession session = request.getSession();
            Long shopId = (Long) session.getAttribute("shopId");
            Map<Long, Integer> updates = new HashMap<>();

            Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) {
                String param = params.nextElement();
                if (param.startsWith("fruit_")) {
                    Long fruitId = Long.parseLong(param.substring(6));
                    int consumed = Integer.parseInt(request.getParameter(param));
                    if (consumed != 0) {
                        updates.put(fruitId, consumed);
                    }
                }
            }

            Map<Long, Map<String, Object>> result = stockBean.updateStock(shopId, updates);
            request.setAttribute("result", result);
            request.getRequestDispatcher("/Shop/updateResult.jsp").forward(request, response);

        } catch (SQLException e) {
            response.sendRedirect("StockUpdateServlet?error=1");
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
