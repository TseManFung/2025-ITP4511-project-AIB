<%-- 
    Document   : borrowRecordDetail
    Created on : 2025年4月23日, 下午7:29:10
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
      <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">Borrow Record Details</h4>
                </div>
                
                <div class="card-body">
                    <dl class="row mb-4">
                        <dt class="col-sm-3">Record ID:</dt>
                        <dd class="col-sm-9">${detail.id}</dd>
                        
                        <dt class="col-sm-3">Date:</dt>
                        <dd class="col-sm-9">${detail.DT}</dd>
                        
                        <dt class="col-sm-3">Status:</dt>
                        <dd class="col-sm-9">
                            <span class="state-badge state-${detail.state} text-white">
                                <c:choose>
                                    <c:when test="${detail.state == 'C'}">Pending</c:when>
                                    <c:when test="${detail.state == 'A'}">Approved</c:when>
                                    <c:when test="${detail.state == 'R'}">Rejected</c:when>
                                </c:choose>
                            </span>
                        </dd>
                    </dl>

                    <h5 class="mb-3">Borrowed Items</h5>
                    <table class="table table-bordered">
                        <thead class="table-light">
                            <tr>
                                <th>Fruit Name</th>
                                <th>Quantity</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${detail.items}" var="item">
                                <tr>
                                    <td>${item.name}</td>
                                    <td>${item.quantity}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <c:if test="${detail.state == 'C' && detail.destShopId == sessionScope.shopId}">
                        <div class="mt-4">
                            <form method="post" action="BorrowRecords">
                                <input type="hidden" name="recordId" value="${detail.id}">
                                <button type="submit" name="action" value="approve" 
                                        class="btn btn-success me-2">Approve</button>
                                <button type="submit" name="action" value="reject" 
                                        class="btn btn-danger">Reject</button>
                            </form>
                        </div>
                    </c:if>
                </div>
                
                <div class="card-footer text-end">
                    <a href="BorrowRecords" class="btn btn-secondary">Back to List</a>
                </div>
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

