<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>User List</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <div class="container mt-5">
            <h1>User List</h1>


            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Login Name</th>
                        <th>Name</th>
                        <th>Role</th>
                        <th>Warehouse ID</th>
                        <th>Shop ID</th>
                        <th>Actions</th> 
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${userList}" var="user">
                        <tr>
                            <td>${user.loginName}</td>
                            <td>${user.name}</td>
                            <td>${user.typeDescription}</td>
                            <td>${user.warehouseId}</td>
                            <td>${user.shopId}</td>
                            <td>
                                <c:if test="${sessionScope.userType == 'S'}">
                                    <a href="editUserServlet?loginName=${user.loginName}" class="btn btn-warning btn-sm">Edit</a>
                                    <form action="deleteUserServlet" method="POST" style="display:inline;">
                                        <input type="hidden" name="loginName" value="${user.loginName}">
                                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </body>
</html>