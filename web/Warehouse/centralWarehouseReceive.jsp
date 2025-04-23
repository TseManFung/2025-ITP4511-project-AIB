<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/component/head.jsp" />
    <title>Central Warehouse Receive</title>
    <style>
        .content-bg {
            min-height: calc(100vh);
        }
    </style>
</head>
<body>
    <jsp:include page="/component/modal.jsp" />
    <component:navbar/>

    <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>

    <div class="d-flex position-relative content-bg justify-content-center">
        <div class="container">
            <div class="container mt-5">
                <h1>Receive Stock</h1>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Start Time</th>
                            <th>Source</th>
                            <th>Fruit</th>
                            <th>Quantity</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${pendingTransfers}" var="transfer">
                            <tr>
                                <td>${transfer.id}</td>
                                <td>${transfer.deliveryStartTime}</td>
                                <td>
                                    <c:forEach items="${warehouses}" var="warehouse">
                                        <c:if test="${warehouse.split(':')[0] == transfer.sourceWarehouseId}">
                                            ${warehouse.split(':')[1]}
                                        </c:if>
                                    </c:forEach>
                                </td>
                                <td>
                                    <c:forEach items="${fruits}" var="fruit">
                                        <c:if test="${fruit.split(':')[0] == transfer.fruitId}">
                                            ${fruit.split(':')[1]}
                                        </c:if>
                                    </c:forEach>
                                </td>
                                <td>${transfer.num}</td>
                                <td>
                                    <form action="centralWarehouseReceiveServlet" method="POST" style="display:inline;">
                                        <input type="hidden" name="transferId" value="${transfer.id}">
                                        <input type="hidden" name="action" value="receive">
                                        <button type="submit" class="btn btn-success btn-sm">Receive</button>
                                    </form>
                                    <form action="centralWarehouseReceiveServlet" method="POST" style="display:inline;">
                                           <form action="centralWarehouseReceiveServlet" method="POST" style="display:inline;">
                                            <input type="hidden" name="transferId" value="${transfer.id}">
                                            <input type="hidden" name="action" value="reject">
                                            <button type="submit" class="btn btn-danger btn-sm">Reject</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div id="page-top">
            <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" /></a>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>
    </body>
</html>