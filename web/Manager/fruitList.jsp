<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.FruitBean" %>
<!DOCTYPE html>
<html>
<head>
    <title>Fruit List</title>
    <jsp:include page="/component/head.jsp" />
    <style>
        .content-bg {
            min-height: calc(100vh);
        }
    </style>
</head>
<body>
    <jsp:include page="/component/modal.jsp" />
    <component:navbar/>
    <!-- Header -->
    <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>

    <!-- Content -->
    <div class="d-flex position-relative content-bg justify-content-center">
        <div class="container">
            <div class="container mt-5">
                <h1>Fruit List</h1>
                <div class="mb-3">
                    <a href="fruitServlet?action=add" class="btn btn-success">Add New Fruit</a>
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
                            <th>ID</th>
                            <th>Source Country ID</th>
                            <th>Name</th>
                            <th>Unit</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<FruitBean> fruits = (List<FruitBean>) request.getAttribute("fruits");
                            if (fruits != null) {
                                for (FruitBean fruit : fruits) {
                                    pageContext.setAttribute("fruit", fruit);
                        %>
                            <tr>
                                <td><%= fruit.getId() %></td>
                                <td><%= fruit.getSourceCountryId() %></td>
                                <td><%= fruit.getName() %></td>
                                <td><%= fruit.getUnit() %></td>
                                <td>
                                    <a href="fruitServlet?action=edit&id=<%= fruit.getId() %>" class="btn btn-warning btn-sm">Edit</a>
                                    <a href="fruitServlet?action=delete&id=<%= fruit.getId() %>" class="btn btn-danger btn-sm">Delete</a>
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

    <!-- GoToTop -->
    <div id="page-top">
        <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" /></a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>