<%-- 
    Document   : borrowRecords
    Created on : 2025年4月23日, 下午7:23:30
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
<style>
            .state-badge {
                padding: 0.25em 0.4em;
                border-radius: 0.25rem;
            }
            .state-C { background-color: #0d6efd; }
            .state-A { background-color: #198754; }
            .state-R { background-color: #dc3545; }
        </style>

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
      <h3 class="mb-4">Borrow Records Management</h3>
            
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Date</th>
                        <th>From Shop</th>
                        <th>To Shop</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${records}" var="record">
                        <tr>
                            <td>${record.DT}</td>
                            <td>${record.fromShop}</td>
                            <td>${record.toShop}</td>
                            <td>
                                <span class="state-badge state-${record.state} text-white">
                                    <c:choose>
                                        <c:when test="${record.state == 'C'}">Pending</c:when>
                                        <c:when test="${record.state == 'A'}">Approved</c:when>
                                        <c:when test="${record.state == 'R'}">Rejected</c:when>
                                    </c:choose>
                                </span>
                            </td>
                            <td>
                                <a href="BorrowRecordsDetail?id=${record.id}" 
                                   class="btn btn-sm btn-outline-primary">View</a>
                                <c:if test="${record.state == 'A'}">
                                    <form method="post" class="d-inline">
                                        <input type="hidden" name="recordId" value="${record.id}">
                                        <button type="submit" name="action" value="complete" 
                                                class="btn btn-sm btn-success ms-2">Complete</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
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
