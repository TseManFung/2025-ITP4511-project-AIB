<%@ page contentType="text/html;charset=UTF-8" %>
<%@page import="AIB.Bean.ConsumptionBean" %>
<%@ page import="java.util.List" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<jsp:useBean id="consumptionReport" scope="request" type="java.util.List<AIB.Bean.ConsumptionBean>" />
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
    <title>Consumption Report</title>
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
        <h2>Consumption Report - ${param.type.toUpperCase()}</h2>
        <table class="table table-striped">
            <thead class="thead-dark">
                <tr>
                    <th>Season</th>
                    <th>Fruit Name</th>
                    <th>Total Consumed</th>
                    <th>Location</th>
                </tr>
            </thead>
            <tbody>
                <% 
                    for (ConsumptionBean item : consumptionReport) { 
                %>
                    <tr>
                        <td><%= item.getSeason() %></td>
                        <td><%= item.getFruitName() %></td>
                        <td><%= item.getTotalConsumed() %></td>
                        <td><%= item.getLocationName() %></td>
                    </tr>
                <% 
                    } 
                %>
            </tbody>
        </table>
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
