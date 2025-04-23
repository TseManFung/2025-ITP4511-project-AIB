<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/component/head.jsp" />
    <title>Reserve Approval</title>
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
                <h1>Reserve Approval</h1>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Shop</th>
                            <th>Reserve Date</th>
                            <th>Fruit</th>
                            <th>Quantity</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${reserves}" var="reserve">
                            <tr>
                                <td>${reserve.id}</td>
                                <td>
                                    <c:forEach items="${shops}" var="shop">
                                        <c:if test="${shop.split(':')[0] == reserve.shopId}">
                                            ${shop.split(':')[1]}
                                        </c:if>
                                    </c:forEach>
                                </td>
                                <td>${reserve.reserveDT}</td>
                                <td>
                                    <c:forEach items="${fruits}" var="fruit">
                                        <c:if test="${fruit.split(':')[0] == reserve.fruitId}">
                                            ${fruit.split(':')[1]}
                                        </c:if>
                                    </c:forEach>
                                </td>
                                <td>${reserve.num}</td>
                                <td>
                                    <form action="acceptReserveListServlet" method="POST" style="display:inline;">
                                        <input type="hidden" name="reserveId" value="${reserve.id}">
                                        <input type="hidden" name="action" value="approve">
                                        <button type="submit" class="btn btn-success btn-sm">Approve</button>
                                    </form>
                                    <form action="acceptReserveListServlet" method="POST" style="display:inline;">
                                        <input type="hidden" name="reserveId" value="${reserve.id}">
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