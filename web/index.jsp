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
            String userRole = (String) session.getAttribute("userType");
            
            if (userRole != null) {
                response.sendRedirect("loginServlet?action=home");
            } else {
                response.sendRedirect("login.jsp");
            }
        %>
    </body>
</html>