<%-- 
    Document   : reserveRecords
    Created on : 2025年4月23日, 上午10:05:23
    Author     : andyt
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/component/head.jsp" />
<style>
    .state-C { background-color: #bde5ff; }
    .state-A { background-color: #c3e6cb; }
    .state-R { background-color: #f8d7da; }
    .state-F { background-color: #d6d8db; }
    .state-badge { padding: 3px 8px; border-radius: 12px; }
</style>
<body>
  <jsp:include page="/component/navbar.jsp" />
  
  <div class="container mt-5">
    <h2 class="mb-4">Reservation Records</h2>
    
    <c:if test="${not empty param.success}">
      <div class="alert alert-success">Reservation completed successfully</div>
    </c:if>
    <c:if test="${not empty param.error}">
      <div class="alert alert-danger">
        <c:choose>
          <c:when test="${param.error == 1}">Operation failed</c:when>
          <c:when test="${param.error == 2}">Database error</c:when>
        </c:choose>
      </div>
    </c:if>

    <div class="card mb-4">
      <div class="card-header">Filter & Sort</div>
      <div class="card-body">
        <form method="get" class="row g-3">
          <div class="col-md-4">
            <label class="form-label">Filter by Status:</label>
            <select name="filter" class="form-select" onchange="this.form.submit()">
              <option value="">All</option>
              <option value="C" ${param.filter == 'C' ? 'selected' : ''}>Created</option>
              <option value="A" ${param.filter == 'A' ? 'selected' : ''}>Approved</option>
              <option value="R" ${param.filter == 'R' ? 'selected' : ''}>Rejected</option>
            </select>
          </div>
          <div class="col-md-4">
            <label class="form-label">Sort by:</label>
            <select name="sort" class="form-select" onchange="this.form.submit()">
              <option value="">Default</option>
              <option value="date" ${param.sort == 'date' ? 'selected' : ''}>Create Date</option>
              <option value="reserveDate" ${param.sort == 'reserveDate' ? 'selected' : ''}>Reserve Date</option>
              <option value="status" ${param.sort == 'status' ? 'selected' : ''}>Status</option>
            </select>
          </div>
        </form>
      </div>
    </div>

    <table class="table table-bordered">
      <thead class="thead-dark">
        <tr>
          <th>Reservation ID</th>
          <th>Create Date</th>
          <th>Reserve Date</th>
          <th>Items Count</th>
          <th>Status</th>
          <th>Action</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach items="${records}" var="record">
          <tr class="state-${record.state}">
            <td>${record.id}</td>
            <td>${record.DT}</td>
            <td>${record.reserveDT}</td>
            <td>${record.itemCount}</td>
            <td>
              <span class="state-badge state-${record.state}">
                ${record.state}
              </span>
            </td>
            <td>
              <c:if test="${record.state == 'A'}">
                <form method="post" style="display:inline">
                  <input type="hidden" name="reserveId" value="${record.id}">
                  <button type="submit" class="btn btn-sm btn-success" 
                          onclick="return confirm('Mark as completed?')">Complete</button>
                </form>
              </c:if>
              <a href="reserveDetail?id=${record.id}" class="btn btn-sm btn-info">Details</a>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </div>
</body>
</html>
