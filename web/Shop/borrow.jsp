<%-- 
    Document   : borrow
    Created on : 2025年4月23日, 上午10:05:53
    Author     : andyt
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/component/head.jsp" />
<body>
  <jsp:include page="/component/navbar.jsp" />
  
  <div class="container mt-5">
    <h2 class="mb-4">Borrow Fruits from Same City Shops</h2>
    
    <c:if test="${not empty param.error}">
      <div class="alert alert-danger">
        <c:choose>
          <c:when test="${param.error == 1}">Invalid borrow quantity</c:when>
          <c:when test="${param.error == 2}">Database error occurred</c:when>
        </c:choose>
      </div>
    </c:if>

    <form method="post">
      <div class="mb-3">
        <label class="form-label">Select Destination Shop:</label>
        <select name="destShopId" class="form-select" required>
          <c:forEach items="${stocks}" var="entry">
            <c:set var="shopId" value="${entry.key.split('\\|')[0]}" />
            <option value="${shopId}">${entry.value.shopName}</option>
          </c:forEach>
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
          <c:forEach items="${stocks}" var="entry">
            <c:set var="keys" value="${entry.key.split('\\|')}" />
            <tr>
              <td>${entry.value.shopName}</td>
              <td>${entry.value.fruitName}</td>
              <td>${entry.value.quantity}</td>
              <td>
                <input type="number" name="fruit_${keys[1]}" 
                       class="form-control" min="0" max="${entry.value.quantity}"
                       required oninput="validateQuantity(this)">
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      <button type="submit" class="btn btn-primary">Submit Borrow Request</button>
    </form>
  </div>

  <script>
    function validateQuantity(input) {
      const max = parseInt(input.max);
      if (input.value > max) {
        input.setCustomValidity(`Quantity cannot exceed ${max}`);
      } else {
        input.setCustomValidity('');
      }
    }
    
    // Remove duplicate shop options
    const select = document.querySelector('select[name="destShopId"]');
    const options = [...new Set([...select.options].map(opt => opt.value))];
    select.innerHTML = options.map(opt => 
      `<option value="${opt}">${select.querySelector(`option[value="${opt}"]`).textContent}</option>`
    ).join('');
  </script>
</body>
</html>
