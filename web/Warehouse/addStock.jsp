<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Add Stock - Warehouse</title>
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
                    <h1>Add Stock</h1>

                    <c:if test="${param.success == 'true'}">
                        <div class="alert alert-success">Stock updated successfully!</div>
                    </c:if>
                    <c:if test="${not empty param.error}">
                        <div class="alert alert-danger">
                            <c:choose>
                                <c:when test="${param.error == 'invalid'}">Invalid quantity entered. Please enter non-negative numbers.</c:when>
                                <c:when test="${param.error == 'database'}">Database error occurred. Please try again later.</c:when>
                                <c:otherwise>Unknown error occurred.</c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">${errorMessage}</div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/AddStockServlet" method="POST">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Fruit ID</th>
                                    <th>Fruit Name</th>
                                    <th>Current Stock</th>
                                    <th>Add Quantity</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:if test="${empty stockList}">
                                <p>Stock list is empty!</p>
                            </c:if>
                            <c:forEach items="${stockList}" var="stock">
                                <tr>
                                    <td>${stock.fruitId}</td>
                                    <td>${stock.fruitName}</td>
                                    <td>${stock.currentStock}</td>
                                    <td>
                                        <input type="number" class="form-control" name="fruit_${stock.fruitId}" min="0" value="0" required>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        <button type="submit" class="btn btn-primary">Update Stock</button>
                    </form>
                </div>
            </div>
        </div>

        <div id="page-top">
            <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" alt="Return to Top" /></a>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>
    </body>
</html>