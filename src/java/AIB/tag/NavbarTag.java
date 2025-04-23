/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AIB.tag;

/**
 *
 * @author andyt
 */
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import static javax.swing.UIManager.put;

public class NavbarTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        PageContext pageContext = (PageContext) getJspContext();
        HttpSession session = pageContext.getSession();
        String contextPath = pageContext.getServletContext().getContextPath();

        // 定义不同角色的导航菜单
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
                put("Bakery Dashboard", "/Shop/bakeryDashboard.jsp");
                put("Create Reservation", "/reserveServlet");
                put("Borrow Management", "/borrowServlet");
                put("Stock Update", "/updateStockServlet");
            }
        };

        Map<String, String> warehouseNav = new LinkedHashMap<String, String>() {
            {
                put("Warehouse Dashboard", "/Warehouse/warehouseDashboard.jsp");
                put("Inventory Management", "/inventoryServlet");
                put("Shipment Tracking", "/shipmentServlet");
                put("Replenishment", "/replenishmentServlet");
            }
        };

        try {
            out.println("<header id='headerofnav'>");
            // Logo（保持不变）
            out.println("    <a href='" + getDashboardUrl(session, contextPath) + "' class='logo' id='navbar-logo'>");
            out.println("        <img src='" + contextPath + "/images/common/lego.png' style='width: 100%; height: 8vh;' alt='Logo'>");
            out.println("    </a>");

            // 菜单图标
            out.println("    <i class='fas fa-bars menu-icon'></i>");

            // 导航菜单
            out.println("    <ul id='navbar-ul'>");

            // 根据角色显示对应菜单项
            String userType = (session != null) ? (String) session.getAttribute("userType") : null;
            Map<String, String> currentNav = getNavMap(userType, managerNav, shopNav, warehouseNav);

            for (Map.Entry<String, String> entry : currentNav.entrySet()) {
                out.println("        <li class='navbar-item'>");
                out.println("            <a href='" + contextPath + entry.getValue() + "'>" + entry.getKey() + "</a>");
                out.println("        </li>");
            }

            // 登出按钮（保持不变）
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
                return contextPath + "/Warehouse/warehouseDashboard.jsp";
            default:
                return contextPath;
        }
    }
}
