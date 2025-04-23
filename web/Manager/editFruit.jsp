<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Fruit</title>
          <jsp:include page="/component/head.jsp" />
          <title>Edit Fruit </title>
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
                <h1>Edit Fruit: <%= ((AIB.Bean.FruitBean)request.getAttribute("fruit")).getName() %></h1>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <form action="fruitServlet" method="post">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="id" value="${fruit.id}">
                    <div class="mb-3">
                        <label class="form-label">Source Country</label>
                        <select name="sourceCountryId" class="form-select" required>
                            <c:forEach items="${countries}" var="country">
                                <option value="${country.key}" ${fruit.sourceCountryId == country.key ? 'selected' : ''}>
                                    ${country.value} (ID: ${country.key})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Name</label>
                        <input type="text" name="name" class="form-control" value="${fruit.name}" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Unit</label>
                        <input type="text" name="unit" class="form-control" value="${fruit.unit}" required>
                    </div>
                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                        <button type="submit" class="btn btn-primary">Save Changes</button>
                        <a href="fruitServlet" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
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