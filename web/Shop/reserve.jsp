<%-- 
    Document   : reserve
    Created on : 2025年4月23日, 上午10:01:30
    Author     : andyt
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.FruitBean" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Fruit Reservation</title>
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
                <h2 class="mb-4">Fruit Reservation</h2>

                <%-- 顯示錯誤訊息 --%>
                <%
                    String errorMessage = null;
                    if (request.getParameter("error") != null) {
                        int errorCode = Integer.parseInt(request.getParameter("error"));
                        switch (errorCode) {
                            case 1:
                                errorMessage = "Invalid reservation quantity";
                                break;
                            case 2:
                                errorMessage = "Database error occurred";
                                break;
                            default:
                                errorMessage = "Unknown error";
                        }
                    }

                    if (request.getParameter("success") != null) {
                %>
                <div class="alert alert-success">
                    Reservation successful! Your reservation ID is: <%= request.getParameter("success") %>
                </div>
                <% } %>
                
                <% if (errorMessage != null) { %>
                <div class="alert alert-danger">
                    <%= errorMessage %>
                </div>
                <% } %>

                <%-- 使用 jsp:useBean 來處理 FruitBean 資料 --%>
                <jsp:useBean id="fruits" type="java.util.List<FruitBean>" scope="request" />
                <form method="post">
                    <div class="mb-3">
                        <label for="reserveDT" class="form-label">Reservation Date</label>
                        <input type="date" id="reserveDT" name="reserveDT" class="form-control" required
                               min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) %>"
                               max="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000)) %>"
                               value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000)) %>">
                    </div>
                    <table class="table table-striped">
                        <thead class="thead-dark">
                            <tr>
                                <th>Fruit Name</th>
                                <th>Available Quantity</th>
                                <th>Unit</th>
                                <th>Reserve Quantity</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (fruits != null && !fruits.isEmpty()) {
                                    for (FruitBean fruit : fruits) {
                            %>
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
                            <% 
                                    }
                                } else { 
                            %>
                            <tr>
                                <td colspan="3" class="text-center">No fruits available for reservation</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                    <button type="submit" class="btn btn-primary" id="submitButton" onclick="hideButtonAndSubmit(this)">Submit Reservation</button>
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
        <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>      
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