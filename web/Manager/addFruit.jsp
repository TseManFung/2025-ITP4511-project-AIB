<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add New Fruit</title>
    <jsp:include page="/component/head.jsp" />
    <style>
        .content-bg {
            min-height: calc(100vh);
        }
    </style>
</head>
<body>
    <jsp:include page="/component/modal.jsp" />
    <component:navbar/>

    <!-- Header -->
    <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>

    <!-- Content -->
    <div class="d-flex position-relative content-bg justify-content-center">
        <div class="container">
            <div class="container mt-5">
                <h1>Add New Fruit</h1>

                <form action="fruitServlet" method="post">
                    <input type="hidden" name="action" value="add">
                    <div class="mb-3">
                        <label class="form-label">Source Country</label>
                        <select name="sourceCountryId" class="form-select" required>
                            <option value="">-- Select Country --</option>
                            <%
                                Map<Long, String> countries = (Map<Long, String>) request.getAttribute("countries");
                                if (countries != null) {
                                    for (Map.Entry<Long, String> country : countries.entrySet()) {
                                        Long key = country.getKey();
                                        String value = country.getValue();
                            %>
                                <option value="<%= key %>"><%= value %> (ID: <%= key %>)</option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Name</label>
                        <input type="text" name="name" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Unit</label>
                        <input type="text" name="unit" class="form-control" required>
                    </div>
                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                        <button type="submit" class="btn btn-primary">Add Fruit</button>
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