<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>User List</title>
        <style>
            .content-bg {
                min-height: calc(100vh);
            }
        </style>
    </head>
    <body>

        <jsp:include page="/component/modal.jsp" />
        <component:navbar/>

        <!-- header -->
        <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>
        <!-- /header -->

        <!-- content -->
        <div class="d-flex position-relative content-bg justify-content-center">
            <div class="container">
                <div class="container mt-5">
                    <h1>User List</h1>
                    <div class="mb-3">
                        <a href="addUserServlet" class="btn btn-success">Add New User</a>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Login Name</th>
                                <th>Name</th>
                                <th>Role</th>
                                <th>ID</th>
                                <th>Actions</th> 
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${userList}" var="user">
                                <tr>
                                    <td>${user.loginName}</td>
                                    <td>${user.name}</td>
                                    <td>${user.typeDescription}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${user.type.toString() == 'B' && user.shopId != 0}">
                                                Shop: ${user.shopId}    
                                            </c:when>
                                            <c:when test="${user.type.toString() == 'W' && user.warehouseId != 0}">
                                                Warehouse: ${user.warehouseId}
                                            </c:when>
                                            <c:otherwise>
                                                -
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
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
            </div> 
        </div>
        <!-- /content -->

        <!-- GoToTop -->
        <div id="page-top" style="">
            <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" /></a>
        </div>
        <!-- /GoToTop -->

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>      
    </body>
</html>