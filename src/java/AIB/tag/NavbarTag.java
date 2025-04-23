package AIB.tag;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class NavbarTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        PageContext pageContext = (PageContext) getJspContext();
        HttpSession session = pageContext.getSession();
        String contextPath = pageContext.getServletContext().getContextPath();

        // 定義不同角色的導航菜單
        Map<String, String> managerNav = new LinkedHashMap<String, String>() {
            {
                put("Dashboard", "/Manager/managementDashboard.jsp");
                put("User Management", "/userListServlet");
                put("Fruit Management", "/fruitServlet");
                put("Warehouse Monitoring", "/warehouseMonitorServlet");
            }
        };

        Map<String, String> shopNav = new LinkedHashMap<String, String>() {
            {
                put("Reserve from Warehouse", "/reserveServlet");
                put("Reservation Record", "/ReserveRecordServlet");
                put("Borrow from Other Shop", "/BorrowServlet");
                put("Borrow Record", "/BorrowRecordServlet");
                put("Stock Update", "/StockUpdateServlet");
            }
        };

        Map<String, String> warehouseNav = getWarehouseNav(session);

        try {
            out.println("<header id='headerofnav'>");
            // Logo（保持不變）
            out.println("    <a href='" + getDashboardUrl(session, contextPath) + "' class='logo' id='navbar-logo'>");
            out.println("        <img src='" + contextPath + "/images/common/lego.png' style='width: 100%; height: 8vh;' alt='Logo'>");
            out.println("    </a>");

            // 菜單圖標
            out.println("    <i class='fas fa-bars menu-icon'></i>");

            // 導航菜單
            out.println("    <ul id='navbar-ul'>");

            // 根據角色顯示對應菜單項
            String userType = (session != null) ? (String) session.getAttribute("userType") : null;
            Map<String, String> currentNav = getNavMap(userType, managerNav, shopNav, warehouseNav);

            for (Map.Entry<String, String> entry : currentNav.entrySet()) {
                out.println("        <li class='navbar-item'>");
                out.println("            <a href='" + contextPath + entry.getValue() + "'>" + entry.getKey() + "</a>");
                out.println("        </li>");
            }

            // 登出按鈕（保持不變）
            out.println("        <li class='logout-item'>");
            out.println("            <a href='" + contextPath + "/loginServlet?action=logout'>Logout</a>");
            out.println("        </li>");

            out.println("    </ul>");
            out.println("</header>");
        } catch (IOException ex) {
            throw new JspException("Error in NavbarTag", ex);
        }
    }

    private Map<String, String> getNavMap(String userType,
            Map<String, String> managerNav,
            Map<String, String> shopNav,
            Map<String, String> warehouseNav) {
        if (userType == null) {
            return Collections.emptyMap();
        }

        switch (userType) {
            case "S": // Senior Management
            case "M": // Manager
                return managerNav;
            case "B": // Bakery Staff
                return shopNav;
            case "W": // Warehouse Staff
                return warehouseNav;
            default:
                return Collections.emptyMap();
        }
    }

    private Map<String, String> getWarehouseNav(HttpSession session) {
        String warehouseType = (session != null) ? (String) session.getAttribute("warehouseType") : null;
        Map<String, String> warehouseNav = new LinkedHashMap<>();

        if ("S".equals(warehouseType)) { // 源倉庫員工
            warehouseNav.put("庫存轉移", "/sourceWarehouseStockChangeServlet");
            warehouseNav.put("庫存查詢", "/inventoryQueryServlet");
        } else if ("C".equals(warehouseType)) { // 中央倉庫員工
            warehouseNav.put("庫存接收", "/centralWarehouseReceiveServlet");
            warehouseNav.put("預訂批准", "/acceptReserveListServlet");
            warehouseNav.put("庫存管理", "/inventoryManagementServlet");
        } else { // 其他倉庫員工（保持原有導航）
            warehouseNav.put("Warehouse Dashboard", "/Warehouse/warehouseDashboard.jsp");
            warehouseNav.put("Inventory Management", "/inventoryServlet");
            warehouseNav.put("Shipment Tracking", "/shipmentServlet");
            warehouseNav.put("Replenishment", "/replenishmentServlet");
        }

        return warehouseNav;
    }

    private String getDashboardUrl(HttpSession session, String contextPath) {
        if (session == null) {
            return contextPath;
        }

        String userType = (String) session.getAttribute("userType");
        if (userType == null) {
            return contextPath;
        }

        switch (userType) {
            case "S":
            case "M":
                return contextPath + "/Manager/managementDashboard.jsp";
            case "B":
                return contextPath + "/Shop/bakeryDashboard.jsp";
            case "W":
                return getWarehouseDashboardUrl(session, contextPath);
            default:
                return contextPath;
        }
    }

    private String getWarehouseDashboardUrl(HttpSession session, String contextPath) {
        String warehouseType = (session != null) ? (String) session.getAttribute("warehouseType") : null;

        if ("S".equals(warehouseType)) {
            return contextPath + "/Warehouse/sourceDashboard.jsp";
        } else if ("C".equals(warehouseType)) {
            return contextPath + "/Warehouse/centralDashboard.jsp";
        } else {
            return contextPath + "/Warehouse/warehouseDashboard.jsp";
        }
    }
}
