<%-- Document : updateResult Created on : 2025年4月23日, 下午6:56:52 Author : andyt --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@ page import="AIB.Bean.ShopBean" %>
<%@ page import="AIB.Bean.FruitBean" %>

<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Update Stock Result</title>
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

                <h2 class="mb-4">Stock Update Result</h2>

                <%-- 顯示錯誤訊息 --%>
                <%
                    String errorMessage = (String) request.getAttribute("error");
                    if (errorMessage != null) {
                %>
                <div class="alert alert-danger" role="alert">
                    <%= errorMessage %>
                </div>
                <% } %>

                <table class="table table-bordered">
                    <thead class="thead-dark">
                        <tr>
                            <th>Fruit ID</th>
                            <th>Fruit Name</th>
                            <th>Original Qty</th>
                            <th>New Qty</th>
                            <th>Consumed</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            ShopBean shop = (ShopBean) request.getAttribute("result");
                            if (shop != null && shop.getFruits() != null && !shop.getFruits().isEmpty()) {
                                for (FruitBean fruit : shop.getFruits()) {
                        %>
                        <tr>
                            <td><%= fruit.getId() %></td>
                            <td><%= fruit.getName() %></td>
                            <td><%= fruit.getOriginalNum() %></td>
                            <td><%= fruit.getUpdatedNum() %></td>
                            <td><%= fruit.getConsumeNum() %></td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="5" class="text-center">No update results available</td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>

                <a href="StockUpdateServlet" class="btn btn-secondary">Back to Update</a>
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