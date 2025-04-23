

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
                <h1 class="mb-4">倉庫控制面板</h1>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <h3>測試功能</h3>
                <div class="d-flex flex-wrap">
                    <c:choose>
                        <c:when test="${sessionScope.warehouseType == 'S'}">
                            <!-- 源倉庫員工：僅顯示庫存轉移按鈕 -->
                            <a href="${pageContext.request.contextPath}/sourceWarehouseStockChangeServlet" 
                               class="btn btn-primary btn-test">測試庫存轉移 (Source Warehouse)</a>
                        </c:when>
                        <c:when test="${sessionScope.warehouseType == 'C'}">
                            <!-- 中央倉庫員工：顯示接收庫存和預訂批准按鈕 -->
                            <a href="${pageContext.request.contextPath}/centralWarehouseReceiveServlet" 
                               class="btn btn-primary btn-test">測試庫存接收 (Central Warehouse)</a>
                            <a href="${pageContext.request.contextPath}/acceptReserveListServlet" 
                               class="btn btn-primary btn-test">測試預訂批准 (Central Warehouse)</a>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning">無法確定倉庫類型，請聯繫管理員</div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div id="page-top">
            <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" alt="Return to top" /></a>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>