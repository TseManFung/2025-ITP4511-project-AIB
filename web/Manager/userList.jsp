<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.UserBean" %>
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

                    <%
                        String error = (String) request.getAttribute("error");
                        if (error != null && !error.isEmpty()) {
                    %>
                        <div class="alert alert-danger"><%= error %></div>
                    <%
                        }
                    %>

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
                            <%
                                List<UserBean> userList = (List<UserBean>) request.getAttribute("userList");
                                if (userList != null) {
                                    for (UserBean user : userList) {
                                        pageContext.setAttribute("user", user);
                            %>
                                <tr>
                                    <td><%= user.getLoginName() != null ? user.getLoginName() : "" %></td>
                                    <td><%= user.getName() != null ? user.getName() : "" %></td>
                                    <td><%= user.getTypeDescription() != null ? user.getTypeDescription() : "Unknown" %></td>
                                    <td>
                                        <%
                                            Character userType = user.getType();
                                            String typeStr = userType != null ? userType.toString() : "";
                                            if ("B".equals(typeStr) && user.getShopId() != null && user.getShopId() != 0) {
                                                out.print("Shop: " + user.getShopId());
                                            } else if ("W".equals(typeStr) && user.getWarehouseId() != null && user.getWarehouseId() != 0) {
                                                out.print("Warehouse: " + user.getWarehouseId());
                                            } else {
                                                out.print("-");
                                            }
                                        %>
                                    </td>
                                    <td>
                                        <%
                                            String sessionUserType = (String) session.getAttribute("userType");
                                            if ("S".equals(sessionUserType)) {
                                        %>
                                            <a href="editUserServlet?loginName=<%= user.getLoginName() != null ? user.getLoginName() : "" %>" class="btn btn-warning btn-sm">Edit</a>
                                            <form action="deleteUserServlet" method="POST" style="display:inline;">
                                                <input type="hidden" name="loginName" value="<%= user.getLoginName() != null ? user.getLoginName() : "" %>">
                                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                            </form>
                                        <%
                                            }
                                        %>
                                    </td>
                                </tr>
                            <%
                                    }
                                }
                            %>
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

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>      
    </body>
</html>