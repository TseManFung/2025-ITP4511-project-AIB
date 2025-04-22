<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Acer International Bakery</title>
    </head>
    <body>
        <%
            // Check if user role exists in session
            String userRole = (String) session.getAttribute("role");
            
            if (userRole != null) {
                switch(userRole) {
                    case "B": // Bakery shop staff
                        response.sendRedirect("bakeryStaffDashboard.jsp");
                        break;
                    case "W": // Warehouse Staff
                        response.sendRedirect("warehouseStaffDashboard.jsp");
                        break;
                    case "S": // Senior Management
                        response.sendRedirect("managementDashboard.jsp");
                        break;
                    default: // Invalid role
                        response.sendRedirect("login.jsp");
                        break;
                }
            } else {
                // No role in session - go to login
                response.sendRedirect("login.jsp");
            }
        %>
    </body>
</html>