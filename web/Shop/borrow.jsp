<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.ShopBean" %>
<%@ page import="AIB.Bean.FruitBean" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Borrow Fruits</title>
    </head>
    <body>
        <jsp:include page="/component/modal.jsp" />

        <component:navbar />

        <!-- header -->
        <div style="height: calc(0lvh + 128px)" id="header"></div>
        <!-- /header -->

        <!-- content -->
        <div class="d-flex position-relative content-bg justify-content-center">
            <div class="container">
                <h2 class="mb-4">Borrow Fruits</h2>

                <%-- 顯示成功或錯誤訊息 --%>
                <%
                String targetShopId = request.getParameter("targetShopId");
                    String successMessage = request.getParameter("success");
                    String errorMessage = null;
                    if (request.getParameter("error") != null) {
                        int errorCode = Integer.parseInt(request.getParameter("error"));
                        switch (errorCode) {
                            case 1:
                                errorMessage = "Invalid borrow quantity.";
                                break;
                            case 2:
                                errorMessage = "Database error occurred.";
                                break;
                            default:
                                errorMessage = "Unknown error.";
                        }
                    }
                %>
                <% if (successMessage != null) { %>
                <div class="alert alert-success">
                    Borrow request submitted successfully! Your borrow ID is: <%= successMessage %>
                </div>
                <% } %>
                <% if (errorMessage != null) { %>
                <div class="alert alert-danger">
                    <%= errorMessage %>
                </div>
                <% } %>

                <%-- 使用 jsp:useBean 來處理 ShopBean 資料 --%>
                <jsp:useBean id="stocks" type="java.util.List<AIB.Bean.ShopBean>" scope="request" />
                <form method="get" action="BorrowServlet">
                    <div class="mb-3">
                        <label for="targetShopId" class="form-label">Select Target Shop</label>
                        <select id="targetShopId" name="targetShopId" class="form-select" required>
                            <option value="" disabled selected>Select a shop</option>
                            <% if (stocks != null && !stocks.isEmpty()) {
                                for (ShopBean shop : stocks) { %>
                                    <option value="<%= shop.getShopid() %>" 
                                            <%= targetShopId != null && Long.parseLong(targetShopId) == shop.getShopid() ? "selected" : "" %>>
                                        <%= shop.getShopName() %> - <%= shop.getCityName() %>, <%= shop.getCountryName() %>
                                    </option>
                            <% } } %>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-secondary">Load Fruits</button>
                </form>

                <br><br>
                <%-- 如果有選擇目標商店，顯示水果列表 --%>
                <%
                    
                    if (targetShopId != null && !targetShopId.isEmpty()) {
                        %>
                <form method="post" action="BorrowServlet">
                    <input type="hidden" name="sourceShopId" value="<%= targetShopId %>">

                    <table class="table table-striped">
                        <thead class="thead-dark">
                            <tr>
                                <th>Fruit Name</th>
                                <th>Available Quantity</th>
                                <th>Unit</th>
                                <th>Borrow Quantity</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (stocks != null && !stocks.isEmpty()) {
                                for (ShopBean shop : stocks) {
                                    for (FruitBean fruit : shop.getFruits()) { 
                                        if (fruit.getQuantity() != 0) { %>
                                    
                            <tr>
                                <td><%= fruit.getName() %></td>
                                <td><%= fruit.getQuantity() %></td>
                                <td><%= fruit.getUnit() %></td>
                                <td>
                                    <input type="number" name="fruit_<%= fruit.getId() %>" value="0"
                                           class="form-control" min="0" max="<%= fruit.getQuantity() %>"
                                           required oninput="validateQuantity(this)">
                                </td>
                            </tr>
                            <% } } } } else { %>
                            <tr>
                                <td colspan="4" class="text-center">No fruits available for borrowing</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                    <button type="submit" class="btn btn-primary" id="submitButton" onclick="hideButtonAndSubmit(this)">Submit Borrow Request</button>
                </form>
                <% } %>
            </div>
        </div>
        <!-- /content -->

        <!-- GoToTop -->
        <div id="page-top" style="">
            <a href="#header"><img src="<%= request.getContextPath() %>/images/common/returan-top.png" /></a>
        </div>
        <!-- /GoToTop -->

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
                crossorigin="anonymous"></script>
        <script>
            function validateQuantity(input) {
                const max = parseInt(input.max);
                if (input.value > max) {
                    input.setCustomValidity(`Quantity cannot exceed ${max}`);
                } else {
                    input.setCustomValidity('');
                }
            }

            function hideButtonAndSubmit(button) {
                const form = button.form;

                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                button.style.display = 'none';
                form.submit();
            }
        </script>
    </body>
</html>