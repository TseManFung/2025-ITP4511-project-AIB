<%-- 
    Document   : updateStock
    Created on : 2025年4月23日, 上午10:05:05
    Author     : andyt
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Update stock level</title>
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
                <h2 class="mb-4">Update Stock Level</h2>

                <%-- 宣告區塊，定義方法 --%>
                <%!
                    public String getErrorMessage(String error) {
                        if ("invalid".equals(error)) {
                            return "Invalid input or insufficient stock";
                        }
                        return null;
                    }
                %>

                <%-- 顯示錯誤訊息 --%>
                <%
                    String errorMessage = getErrorMessage(request.getParameter("error"));
                %>
                <% if (errorMessage != null) {%>
                <div class="alert alert-danger"><%= errorMessage%></div>
                <% } %>

                <form method="post">
                    <table class="table table-bordered">
                        <thead class="thead-dark">
                            <tr>
                                <th>Fruit ID</th>
                                <th>Fruit Name</th>
                                <th>Current Stock</th>
                                <th>Consume Quantity</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                java.util.Map<Long, java.util.Map<String, Object>> stock =
                                    (java.util.Map<Long, java.util.Map<String, Object>>) request.getAttribute("stock");
                                if (stock != null && !stock.isEmpty()) {
                                    for (java.util.Map.Entry<Long, java.util.Map<String, Object>> entry : stock.entrySet()) {
                                        Long fruitId = entry.getKey();
                                        java.util.Map<String, Object> fruitData = entry.getValue();
                                        String name = (String) fruitData.get("name");
                                        int num = (int) fruitData.get("num");
                            %>
                            <tr>
                                <td><%= fruitId %></td>
                                <td><%= name %></td>
                                <td><%= num %></td>
                                <td>
                                    <input type="number" name="fruit_<%= fruitId %>" value="0"
                                           class="form-control" max="<%= num %>"
                                           required oninput="validateQuantity(this)">
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="4" class="text-center">No stock data available</td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                    <button type="submit" class="btn btn-primary" onclick="hideButtonAndSubmit(this)">Submit Update</button>
                </form>
            </div> 
        </div>
        <!-- /content -->

        <!-- GoToTop -->
        <div id="page-top" style="">
            <a href="#header"><img src="<%= request.getContextPath()%>/images/common/returan-top.png" /></a>
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