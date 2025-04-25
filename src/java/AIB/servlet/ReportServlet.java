package AIB.servlet;

import AIB.Bean.ConsumptionBean;
import AIB.Bean.ReserveReportBean;
import AIB.DL.ReportDL;
import AIB.db.ITP4511_DB;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ReportServlet", urlPatterns = {"/reports"})
public class ReportServlet extends HttpServlet {
    private ReportDL reportDL;
    
    @Override
    public void init() throws ServletException {
        super.init();
        ITP4511_DB db = new ITP4511_DB(
            getServletContext().getInitParameter("dbUrl"),
            getServletContext().getInitParameter("dbUser"),
            getServletContext().getInitParameter("dbPassword")
        );
        reportDL = new ReportDL(db);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Handle direct access to selection page
        if(request.getParameter("report") == null) {
            request.getRequestDispatcher("/reports/selectReport.jsp").forward(request, response);
            return;
        }

        String reportType = request.getParameter("report");
        String locationType = request.getParameter("type");
        
        if (locationType == null || locationType.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Location type is required");
            return;
        }
        
        Long locationId;
        try {
            locationId = Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid location ID");
            return;
        }
        
        try {
            if ("consumption".equals(reportType)) {
                List<ConsumptionBean> report = reportDL.getConsumptionReport(locationType, locationId);
                request.setAttribute("consumptionReport", report);
                request.getRequestDispatcher("/reports/consumptionReport.jsp").forward(request, response);
            } else {
                List<ReserveReportBean> report = reportDL.getReserveReport(locationType, locationId);
                request.setAttribute("reserveReport", report);
                request.getRequestDispatcher("/reports/reserveReport.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}