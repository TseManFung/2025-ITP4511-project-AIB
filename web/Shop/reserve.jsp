<%-- 
    Document   : reserve
    Created on : 2025年4月23日, 上午10:01:30
    Author     : andyt
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/component/head.jsp" />
<body>
  <jsp:include page="/component/navbar.jsp" />
  
  <div class="container mt-5">
    <h2 class="mb-4">Fruit Reservation</h2>
    
    <c:if test="${not empty param.error}">
      <div class="alert alert-danger">
        <c:choose>
          <c:when test="${param.error == 1}">Invalid reservation quantity</c:when>
          <c:when test="${param.error == 2}">Database error occurred</c:when>
        </c:choose>
      </div>
    </c:if>

    <form method="post">
      <table class="table table-striped">
        <thead class="thead-dark">
          <tr>
            <th>Fruit Name</th>
            <th>Available Quantity</th>
            <th>Reserve Quantity</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${fruits}" var="entry">
            <c:set var="parts" value="${entry.key.split('\\|')}" />
            <tr>
              <td>${parts[0]}</td>
              <td>${entry.value}</td>
              <td>
                <input type="number" name="fruit_${parts[1]}" 
                       class="form-control" min="0" max="${entry.value}"
                       required oninput="validateQuantity(this)">
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      <button type="submit" class="btn btn-primary">Submit Reservation</button>
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
  </script>
</body>
</html>