<%-- 
    Document   : updateStock
    Created on : 2025年4月23日, 上午10:05:05
    Author     : andyt
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="/component/head.jsp" />
<title>page name</title>
</head>
<body>
  <jsp:include page="/component/modal.jsp" />
  
  <component:navbar />


  <!-- header -->
  <div style="height: calc(0lvh + 128px)" id="header"></div>
  <!-- /header -->

  <!-- content -->
  <div class="d-flex position-relative content-bg justify-content-center">
    <div class="container">
<h2 class="mb-4">Update Stock Level</h2>
      
      <%-- 宣告區塊，定義方法 --%>
      <%! 
      public String getErrorMessage(String error) {
          if ("invalid".equals(error)) {
              return "Invalid input or insufficient stock";
          }
          return null;
      }
      %>

      <%-- 顯示錯誤訊息 --%>
      <% 
      String errorMessage = getErrorMessage(request.getParameter("error"));
      %>
      <% if (errorMessage != null) { %>
        <div class="alert alert-danger"><%= errorMessage %></div>
      <% } %>

      <form method="post">
        <table class="table table-bordered">
          <thead class="thead-dark">
            <tr>
              <th>Fruit Name</th>
              <th>Current Stock</th>
              <th>Consume Quantity</th>
            </tr>
          </thead>
          <tbody>
            <% 
            java.util.Map<String, Integer> stock = 
                (java.util.Map<String, Integer>) request.getAttribute("stock");
            for (java.util.Map.Entry<String, Integer> entry : stock.entrySet()) {
                String fruitId = entry.getKey();
                int currentStock = entry.getValue();
            %>
              <tr>
                <td>Fruit ID: <%= fruitId %></td>
                <td><%= currentStock %></td>
                <td>
                  <input type="number" name="fruit_<%= fruitId %>" 
                         class="form-control" min="0" max="<%= currentStock %>"
                         required oninput="validateQuantity(this)">
                </td>
              </tr>
            <% } %>
          </tbody>
        </table>
        <button type="submit" class="btn btn-primary">Submit Update</button>
      </form>
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
  <script>
    function validateQuantity(input) {
      const max = parseInt(input.max);
      if (input.value > max) {
        input.setCustomValidity(`Cannot exceed ${max}`);
      } else {
        input.setCustomValidity('');
      }
    }
  </script>
</html>

