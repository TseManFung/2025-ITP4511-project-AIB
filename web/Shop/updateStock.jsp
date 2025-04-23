<%-- 
    Document   : updateStock
    Created on : 2025年4月23日, 上午10:05:05
    Author     : andyt
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/component/head.jsp" />
<body>
  <jsp:include page="/component/navbar.jsp" />
  
  <div class="container mt-5">
    <h2 class="mb-4">Update Stock Level</h2>
    
    <c:if test="${not empty param.error}">
      <div class="alert alert-danger">Invalid input or insufficient stock</div>
    </c:if>

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
          <c:forEach items="${stock}" var="entry">
            <tr>
              <td>Fruit ID: ${entry.key}</td>
              <td>${entry.value}</td>
              <td>
                <input type="number" name="fruit_${entry.key}" 
                       class="form-control" min="0" max="${entry.value}"
                       required oninput="validateQuantity(this)">
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      <button type="submit" class="btn btn-primary">Submit Update</button>
    </form>
  </div>

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
</body>
</html>