<%-- 
    Document   : borrow
    Created on : 2025年4月23日, 上午10:05:53
    Author     : andyt
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="/component/head.jsp" />
<title>Borrow Fruits</title>
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
      <h2 class="mb-4">Borrow Fruits from Same City Shops</h2>
      
      <%-- 顯示錯誤訊息 --%>
      <% 
      String errorMessage = null;
      if (request.getParameter("error") != null) {
          int errorCode = Integer.parseInt(request.getParameter("error"));
          errorMessage = getErrorMessage(errorCode);
      }
      %>
      <% if (errorMessage != null) { %>
        <div class="alert alert-danger">
          <%= errorMessage %>
        </div>
      <% } %>

      <form method="post">
        <div class="mb-3">
          <label class="form-label">Select Destination Shop:</label>
          <select name="destShopId" class="form-select" required>
            <%-- 檢查 stocks 是否為 null --%>
            <% 
            java.util.Map<String, Object> stocks = (java.util.Map<String, Object>) request.getAttribute("stocks");
            if (stocks != null) {
                for (java.util.Map.Entry<String, Object> entry : stocks.entrySet()) {
                    String shopId = entry.getKey().split("\\|")[0];
                    String shopName = ((java.util.Map<String, String>) entry.getValue()).get("shopName");
            %>
                    <option value="<%= shopId %>"><%= shopName %></option>
            <% 
                }
            } else { 
            %>
                <option value="">No shops available</option>
            <% } %>
          </select>
        </div>

        <table class="table table-striped">
          <thead class="thead-dark">
            <tr>
              <th>Shop Name</th>
              <th>Fruit Name</th>
              <th>Available Quantity</th>
              <th>Borrow Quantity</th>
            </tr>
          </thead>
          <tbody>
            <% 
            if (stocks != null) {
                for (java.util.Map.Entry<String, Object> entry : stocks.entrySet()) {
                    String[] keys = entry.getKey().split("\\|");
                    String shopName = ((java.util.Map<String, String>) entry.getValue()).get("shopName");
                    String fruitName = ((java.util.Map<String, String>) entry.getValue()).get("fruitName");
                    int quantity = Integer.parseInt(((java.util.Map<String, String>) entry.getValue()).get("quantity"));
            %>
                  <tr>
                    <td><%= shopName %></td>
                    <td><%= fruitName %></td>
                    <td><%= quantity %></td>
                    <td>
                      <input type="number" name="fruit_<%= keys[1] %>" 
                             class="form-control" min="0" max="<%= quantity %>"
                             required oninput="validateQuantity(this)">
                    </td>
                  </tr>
            <% 
                }
            } else { 
            %>
              <tr>
                <td colspan="4" class="text-center">No stock data available</td>
              </tr>
            <% } %>
          </tbody>
        </table>
        <button type="submit" class="btn btn-primary">Submit Borrow Request</button>
      </form>
    </div> 
  </div>
  <!-- /content -->

  <!-- GoToTop -->
  <div id="page-top" style="">
    <a href="#header"><img src="<%= request.getContextPath() %>/images/common/returan-top.png" /></a>
  </div>
  <!-- /GoToTop -->

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>      
  <script>
    function validateQuantity(input) {
      const max = parseInt(input.max);
      if (input.value > max) {
        input.setCustomValidity(`Quantity cannot exceed ${max}`);
      } else {
        input.setCustomValidity('');
      }
    }
  </script>
</body>
</html>