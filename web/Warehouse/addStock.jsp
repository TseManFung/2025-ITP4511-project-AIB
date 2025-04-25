<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.StockBean" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Add Stock - Warehouse</title>
        <style>
            .content-bg {
                min-height: calc(100vh);
            }
        </style>
    </head>
    <body>
        <jsp:include page="/component/modal.jsp" />
        <component:navbar/>

        <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>

        <div class="d-flex position-relative content-bg justify-content-center">
            <div class="container">
                <div class="container mt-5">
                    <h1>Add Stock</h1>

                    <% if ("true".equals(request.getParameter("success"))) { %>
                        <div class="alert alert-success">Stock updated successfully!</div>
                    <% } %>
                    <%
                        String error = request.getParameter("error");
                        if (error != null && !error.isEmpty()) {
                    %>
                        <div class="alert alert-danger">
                            <%
                                if ("invalid".equals(error)) {
                                    out.print("Invalid quantity entered. Please enter non-negative numbers.");
                                } else if ("database".equals(error)) {
                                    out.print("Database error occurred. Please try again later.");
                                } else {
                                    out.print("Unknown error occurred.");
                                }
                            %>
                        </div>
                    <% } %>
                    <% if (request.getAttribute("errorMessage") != null) { %>
                        <div class="alert alert-danger"><%= request.getAttribute("errorMessage") %></div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/AddStockServlet" method="POST">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Fruit ID</th>
                                    <th>Fruit Name</th>
                                    <th>Current Stock</th>
                                    <th>Add Quantity</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    List<StockBean> stockList = (List<StockBean>) request.getAttribute("stockList");
                                    if (stockList == null || stockList.isEmpty()) {
                                %>
                                    <tr><td colspan="4">Stock list is empty!</td></tr>
                                <%
                                    } else {
                                        for (int i = 0; i < stockList.size(); i++) {
                                            StockBean stock = stockList.get(i);
                                %>
                                    <tr>
                                        <td><%= stock.getFruitId() %></td>
                                        <td><%= stock.getFruitName() %></td>
                                        <td><%= stock.getCurrentStock() %></td>
                                        <td>
                                            <input type="number" class="form-control" name="fruit_<%= stock.getFruitId() %>" min="0" value="0" required>
                                        </td>
                                    </tr>
                                <%
                                        }
                                    }
                                %>
                            </tbody>
                        </table>
                        <button type="submit" class="btn btn-primary">Update Stock</button>
                    </form>
                </div>
            </div>
        </div>

        <div id="page-top">
            <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" alt="Return to Top" /></a>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>
    </body>
</html>