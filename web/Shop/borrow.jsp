<%-- 
    Document   : borrow
    Created on : 2025年4月23日, 上午10:05:53
    Author     : andyt
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Borrow fruits</title>
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
                <h2>Borrow Fruits from Same City Shops</h2>
                <%-- 宣告區塊，定義方法 --%>
                <%!
                    public String getErrorMessage(int errorCode) {
                        switch (errorCode) {
                            case 1:
                                return "Invalid reservation quantity";
                            case 2:
                                return "Database error occurred";
                            default:
                                return "Unknown error";
                        }
                    }
                %>

                <%-- 顯示錯誤訊息 --%>
                <%
                    String errorMessage = null;
                    if (request.getParameter("error") != null) {
                        int errorCode = Integer.parseInt(request.getParameter("error"));
                        errorMessage = getErrorMessage(errorCode);
                    }

                    if(request.getParameter("success") != null) {
                        
                %>
                <div class="alert alert-success">
               Reservation successful! Your reservation ID is: <%= request.getParameter("success") %>
            </div>
               <%  } %>
                
                <% if (errorMessage != null) {%>
                <div class="alert alert-danger">
                    <%= errorMessage%>
                </div>
                <% } %>
                <form method="post">
                    <div class="mb-3">
                        <label for="destShopId" class="form-label">Select Destination Shop:</label>
                        <select name="destShopId" id="destShopId" class="form-select" required>
                            <jsp:useBean id="stocks" scope="request" type="java.util.ArrayList" />
                            <% if (stocks != null && !stocks.isEmpty()) { %>
                                <% for (AIB.Bean.ShopBean shop : (java.util.ArrayList<AIB.Bean.ShopBean>) stocks) { %>
                                    <option value="<%= shop.getShopid() %>"><%= shop.getShopName() %></option>
                                <% } %>
                            <% } else { %>
                                <option value="">No shops available</option>
                            <% } %>
                        </select>
                    </div>
    
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Shop Name</th>
                                <th>Fruit Name</th>
                                <th>Available Quantity</th>
                                <th>Borrow Quantity</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (stocks != null && !stocks.isEmpty()) { %>
                                <% for (AIB.Bean.ShopBean shop : (java.util.ArrayList<AIB.Bean.ShopBean>) stocks) { %>
                                    <% for (AIB.Bean.FruitBean fruit : shop.getFruits()) { %>
                                        <tr>
                                            <td><%= shop.getShopName() %></td>
                                            <td><%= fruit.getName() %> (<%= fruit.getUnit() %>)</td>
                                            <td><%= fruit.getQuantity() %></td>
                                            <td>
                                                <input type="number" name="fruit_<%= fruit.getId() %>" class="form-control" min="0" max="<%= fruit.getQuantity() %>" required>
                                            </td>
                                        </tr>
                                    <% } %>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="4" class="text-center">No stock data available</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                    <button type="submit" class="btn btn-primary" onclick="hideButtonAndSubmit(this)">Submit Borrow Request</button>
                </form>
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