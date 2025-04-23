<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/component/head.jsp" />
    <title>Source Warehouse Stock Transfer</title>
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
                <h1>Stock Transfer</h1>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <form action="sourceWarehouseStockChangeServlet" method="POST">
                    <div class="mb-3">
                        <label for="destinationWarehouseId" class="form-label">Destination Warehouse</label>
                        <select class="form-control" id="destinationWarehouseId" name="destinationWarehouseId" required>
                            <c:forEach items="${centralWarehouses}" var="warehouse">
                                <option value="${warehouse.split(':')[0]}">${warehouse.split(':')[1]}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="fruitId" class="form-label">Fruit</label>
                        <select class="form-control" id="fruitId" name="fruitId" required>
                            <c:forEach items="${fruits}" var="fruit">
                                <option value="${fruit.split(':')[0]}">${fruit.split(':')[1]}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="num" class="form-label">Quantity</label>
                        <input type="number" class="form-control" id="num" name="num" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Create Transfer</button>
                </form>

                <h2 class="mt-5">Transfer History</h2>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Start Time</th>
                            <th>End Time</th>
                            <th>Destination</th>
                            <th>Fruit</th>
                            <th>Quantity</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${transfers}" var="transfer">
                            <tr>
                                <td>${transfer.id}</td>
                                <td>${transfer.deliveryStartTime}</td>
                                <td>${transfer.deliveryEndTime}</td>
                                <td>
                                    <c:forEach items="${centralWarehouses}" var="warehouse">
                                        <c:if test="${warehouse.split(':')[0] == transfer.destinationWarehouseId}">
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
                                <td>${transfer.state}</td>
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