<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Management Dashboard</title>
        <style>
            .content-bg {
                min-height: calc(100vh);
            }
            </style>
    </head>
    <body>
    <jsp:include page="/component/modal.jsp" />
    <component:Managementnavbar/>

    <!-- header -->
     <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>
    <!-- /header -->

    <!-- content -->
    <div class="d-flex position-relative content-bg justify-content-center">
        <div class="container">
            <div class="container mt-5">
                <h1>Hi This is Management Dashboard</h1>

                <div class="alert alert-info mt-3">
                    <p>Logged in as: <strong>${userName}</strong></p>
                    <p>User type: <strong>${userType}</strong></p>
                </div>

                <c:if test="${userType == 'S'}">
                    <a href="${pageContext.request.contextPath}/userListServlet" class="btn btn-primary">View User List</a>
                </c:if>

                <form action="../loginServlet" method="POST">
                    <input type="hidden" name="action" value="logout">
                    <button type="submit" class="btn btn-danger">Logout</button>
                </form>
            </div>
        </div> 
    </div>
    <!-- /content -->
    
      <!-- GoToTop -->
  <div id="page-top" style="">
    <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" /></a>
  </div>
  <!-- /GoToTop -->


      <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>  
</body>
</html>