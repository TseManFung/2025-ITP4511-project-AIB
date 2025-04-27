<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Edit Fruit</title>
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
        <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>

        <div class="d-flex position-relative content-bg justify-content-center">
            <div class="container">
                <div class="container mt-5">
                    <h1>Edit Fruit: <%= ((AIB.Bean.FruitBean)request.getAttribute("fruit")).getName() %></h1>

                    <%
                        String error = (String) request.getAttribute("error");
                        if (error != null && !error.isEmpty()) {
                    %>
                    <div class="alert alert-danger"><%= error %></div>
                    <%
                        }
                    %>

                    <form action="fruitServlet" method="post">
                        <input type="hidden" name="action" value="edit">
                        <input type="hidden" name="id" value="${fruit.id}">
                        <div class="mb-3">
                            <label class="form-label">Source Country</label>
                            <select name="sourceCountryId" class="form-select" required>
                                <%
         AIB.Bean.FruitBean fruit = (AIB.Bean.FruitBean) request.getAttribute("fruit");
         java.util.Map<Long, String> countries = (java.util.Map<Long, String>) request.getAttribute("countries");
         for (java.util.Map.Entry<Long, String> entry : countries.entrySet()) {
             Long key = entry.getKey();
             String value = entry.getValue();
             String selected = (key.equals(fruit.getSourceCountryId())) ? "selected" : "";
                                %>
                                <option value="<%=key%>" <%=selected%>><%=value%></option>
                                <%  } %>
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

        <div id="page-top">
            <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" /></a>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>