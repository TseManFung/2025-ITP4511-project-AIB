<%-- 
    Document   : updateResult
    Created on : 2025年4月23日, 下午6:56:52
    Author     : andyt
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="/component/head.jsp" />
<title>page name</title>


</head>
<body>
  <jsp:include page="/component/modal.jsp" />
  
 <component:Managementnavbar/>

  <!-- header -->
  <div style="height: calc(0lvh + 128px)" id="header"></div>
  <!-- /header -->

  <!-- content -->
  <div class="d-flex position-relative content-bg justify-content-center">
    <div class="container">
<h2 class="mb-4">Stock Update Result</h2>

<table class="table table-bordered">
  <thead class="thead-dark">
    <tr>
      <th>Fruit ID</th>
      <th>Original Qty</th>
      <th>New Qty</th>
      <th>Consumed</th>
    </tr>
  </thead>
  <tbody>
    <%
      Map<String, Integer> original = (Map<String, Integer>) request.getAttribute("original");
      Map<String, Integer> updated = (Map<String, Integer>) request.getAttribute("updated");

      if (original != null && updated != null) {
        for (Map.Entry<String, Integer> entry : original.entrySet()) {
          String fruitId = entry.getKey();
          int originalQty = entry.getValue();
          int newQty = updated.get(fruitId) != null ? updated.get(fruitId) : 0;
          int consumed = originalQty - newQty;
    %>
      <tr>
        <td><%= fruitId %></td>
        <td><%= originalQty %></td>
        <td><%= newQty %></td>
        <td><%= consumed %></td>
      </tr>
    <%
        }
      }
    %>
  </tbody>
</table>

<a href="updateStock" class="btn btn-secondary">Back to Update</a>
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
