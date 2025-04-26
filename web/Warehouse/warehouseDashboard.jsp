

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<% 
String warehouseType = (session != null) ? (String) session.getAttribute("warehouseType") : null;
String url = request.getContextPath();
if ("S".equals(warehouseType)) { 
    url += "/sourceWarehouseStockChangeServlet";
} else if ("C".equals(warehouseType)) { 
    url += "/centralWarehouseReceiveServlet";
}
// Redirect and stop further execution
response.sendRedirect(url);

%><!-- 
<!DOCTYPE html>
<html>
    <head>
                <jsp:include page="/component/head.jsp" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Warehouse Dashboard</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            .content-bg {
                min-height: calc(100vh - 128px);
                background-color: #f8f9fa;
            }
            .dashboard-container {
                max-width: 800px;
                margin-top: 2rem;
            }
            .btn-test {
                margin: 0.5rem;
            }
        </style>
    </head>
    <body>
        <jsp:include page="/component/modal.jsp" />
        <component:navbar />

        <div style="height: 128px; background-color: white;" id="header"></div>

        <div class="d-flex position-relative content-bg justify-content-center">
            <div class="container dashboard-container">
                <h1 class="mb-4">This is the home page of warehouse clerk</h1>

                
              
            </div>
        </div>

        <div id="page-top">
            <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" alt="Return to top" /></a>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html> -->