<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Fruit List</title>
       <jsp:include page="/component/head.jsp" />
            <title>Fruit Lists </title>
    <style>
        .content-bg {
            min-height: calc(100vh);
        }
    </style>
</head>
<body>
    <jsp:include page="/component/modal.jsp" />
        <component:Managementnavbar/>

    <!-- Header -->
    <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>

    <!-- Content -->
    <div class="d-flex position-relative content-bg justify-content-center">
        <div class="container">
            <div class="container mt-5">
                <h1>Fruit List</h1>
                <div class="mb-3">
                    <a href="fruitServlet?action=add" class="btn btn-success">Add New Fruit</a>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Source Country ID</th>
                            <th>Name</th>
                            <th>Unit</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${fruits}" var="fruit">
                            <tr>
                                <td><%= ((AIB.Bean.FruitBean)pageContext.getAttribute("fruit")).getId() %></td>
                                <td><%= ((AIB.Bean.FruitBean)pageContext.getAttribute("fruit")).getSourceCountryId() %></td>
                                <td><%= ((AIB.Bean.FruitBean)pageContext.getAttribute("fruit")).getName() %></td>
                                <td><%= ((AIB.Bean.FruitBean)pageContext.getAttribute("fruit")).getUnit() %></td>
                                <td>
                                    <a href="fruitServlet?action=edit&id=${fruit.id}" class="btn btn-warning btn-sm">Edit</a>
                                    <a href="fruitServlet?action=delete&id=${fruit.id}" class="btn btn-danger btn-sm">Delete</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- GoToTop -->
    <div id="page-top">
        <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" /></a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>