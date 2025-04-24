<%-- 
    Document   : updateResult
    Created on : 2025年4月23日, 下午6:56:52
    Author     : andyt
--%>

<%@page import="java.util.Map"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Update Stock Result</title>
    </head>
    <body>
        <jsp:include page="/component/modal.jsp" />

        <component:navbar/>

        <!-- header -->
        <div style="height: calc(0lvh + 128px)" id="header"></div>
        <!-- /header -->

        <!-- content -->
        <div class="d-flex position-relative content-bg justify-content-center">
            <div class="container">
                <h2 class="mb-4">Stock Update Result</h2>

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
                            Map<Long, Map<String, Object>> result = (Map<Long, Map<String, Object>>) request.getAttribute("result");

                            if (result != null) {
                                for (Map.Entry<Long, Map<String, Object>> entry : result.entrySet()) {
                                    Long fruitId = entry.getKey();
                                    Map<String, Object> fruitData = entry.getValue();
                                    String fruitName = (String) fruitData.get("name");
                                    int originalQty = (int) fruitData.get("originalNum");
                                    int newQty = (int) fruitData.get("updatedNum");
                                    int consumed = (int) fruitData.get("consumeNum");
                        %>
                        <tr>
                            <td><%= fruitId %></td>
                            <td><%= fruitName %></td>
                            <td><%= originalQty %></td>
                            <td><%= newQty %></td>
                            <td><%= consumed %></td>
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