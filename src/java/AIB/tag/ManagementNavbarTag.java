package AIB.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class ManagementNavbarTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        JspWriter out = getJspContext().getOut();
        PageContext pageContext = (PageContext) getJspContext();
        String contextPath = pageContext.getServletContext().getContextPath();

        try {
            out.println("<header id='headerofnav'>");
            // Logo
            out.println("    <a href='" + contextPath + "/Manager/managementDashboard.jsp' class='logo' id='navbar-logo'>");
            out.println("        <img src='" + contextPath + "/images/common/lego.png' style='width: 100%; height: 8vh;' alt='Logo'>");
            out.println("    </a>");
            // Menu icon
            out.println("    <i class='fas fa-bars menu-icon'></i>");
            // Navbar menu
            out.println("    <ul id='navbar-ul'>");
            out.println("        <li id='navbar-item'>");
            out.println("            <a href='" + contextPath + "/userListServlet'>User List</a>");
            out.println("        </li>");
             out.println("        <li id='navbar-item'>");
            out.println("            <a href='" + contextPath + "/fruitServlet'>fruit List</a>");
            out.println("        </li>");
            
            out.println("        <li id='logout-item'>");
            out.println("            <a href='" + contextPath + "/loginServlet?action=logout'>Logout</a>");
            out.println("        </li>");
            out.println("    </ul>");
            out.println("</header>");
        } catch (IOException ex) {
            throw new JspException("Error in NavbarTag", ex);
        }
    }
}
