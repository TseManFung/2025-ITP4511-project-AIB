<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Management Dashboard</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <div class="container mt-5">
            <h1>Hi This is Management Dashboard</h1>

            <div class="alert alert-info mt-3">
                <p>Logged in as: <strong>${userName}</strong></p>
                <p>User type: <strong>${userType}</strong></p>
            </div>

            <c:if test="${userType == 'S'}">
                <a href="${pageContext.request.contextPath}/userListServlet" class="btn btn-primary">View User List</a>
            </c:if>

            <form action="../loginServlet" method="POST">
                <input type="hidden" name="action" value="logout">
                <button type="submit" class="btn btn-danger">Logout</button>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>