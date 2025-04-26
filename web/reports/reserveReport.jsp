<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.ReserveReportBean" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%!
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
    <title>Reserve Report</title>
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
        <h2>Reservation Report - ${param.type.toUpperCase()}</h2>
        <table class="table table-striped">
            <thead class="thead-dark">
                <tr>
                    <th>Location</th>
                    <th>Fruit Name</th>
                    <th>Total Reserved</th>
                    <th>Latest Reservation</th>
                </tr>
            </thead>
<jsp:useBean id="reserveReport" scope="request" type="List<ReserveReportBean>" />

    <tbody>
        <% 
            for (ReserveReportBean item : reserveReport) { 
        %>
            <tr>
                <td><%= item.getLocationName() %></td>
                <td><%= item.getFruitName() %></td>
                <td><%= item.getTotalReserved() %></td>
                <td><%= item.getLatestReserveDate() != null ? dateFormat.format(item.getLatestReserveDate()) : "N/A" %></td>
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
