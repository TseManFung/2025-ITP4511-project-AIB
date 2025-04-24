<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.BorrowBean" %>
<%! SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd"); %>
<!DOCTYPE html>
<html>

<head>
<jsp:include page="/component/head.jsp" />
<title>Borrow Records</title>
<style>
.state-C {
background-color: rgb(129, 143, 230);
}

.state-A {
background-color: greenyellow;
}

.state-R {
background-color: #dc3545;
}

.state-F {
background-color: grey;
}

.state-badge {
padding: 3px 8px;
border-radius: 12px;
}
</style>
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
<h2 class="mb-4">Borrow Records</h2>

<%-- 顯示成功或錯誤訊息 --%>
<% String successMessage=request.getParameter("success"); String
errorMessage=null; if (request.getParameter("error") !=null) { int
errorCode=Integer.parseInt(request.getParameter("error")); switch
(errorCode) { case 1: errorMessage="Invalid borrow ID." ; break; case 2:
errorMessage="Database error occurred." ; break; default:
errorMessage="An unknown error occurred." ; break; } } %>
<% if (successMessage !=null) { %>
<div class="alert alert-success">Borrow #<%= successMessage %>
completed successfully</div>
<% } %>
<% if (errorMessage !=null) { %>
<div class="alert alert-danger">
<%= errorMessage %>
</div>
<% } %>

<div class="card mb-4">
<div class="card-header">Filter & Sort</div>
<div class="card-body">
<form method="get" class="row g-3">
<div class="col-md-4">
<label class="form-label">Filter by
Status:</label>
<select name="filter"
class="form-select"
onchange="this.form.submit()">
<option value="">Default</option>
<option value="C" <%="C"
    .equals(request.getParameter("filter"))
    ? "selected" : "" %>>Created
</option>
<option value="A" <%="A"
    .equals(request.getParameter("filter"))
    ? "selected" : "" %>
>Approved</option>
<option value="R" <%="R"
    .equals(request.getParameter("filter"))
    ? "selected" : "" %>
>Rejected</option>
<option value="F" <%="F"
    .equals(request.getParameter("filter"))
    ? "selected" : "" %>
>Finished</option>
</select>
</div>
<div class="col-md-4">
<label class="form-label">Sort
by:</label>
<select name="sort" class="form-select"
onchange="this.form.submit()">
<option value="">Default</option>
<option value="date" <%="date"
    .equals(request.getParameter("sort"))
    ? "selected" : "" %>>Borrow
Date</option>
<option value="itemCount"
    <%="itemCount"
    .equals(request.getParameter("sort"))
    ? "selected" : "" %>>Item
Count</option>
<option value="status" <%="status"
    .equals(request.getParameter("sort"))
    ? "selected" : "" %>>Status
</option>
</select>
</div>
</form>
</div>
</div>

<table class="table table-bordered">
<thead class="thead-dark">
<tr>
<th>Borrow ID</th>
<th>Borrow Date</th>
<th>Source Shop</th>
<th>Destination Shop</th>
<th>Items Count</th>
<th>Status</th>
<th>Action</th>
</tr>
</thead>
<tbody>
<%-- 使用 <jsp:useBean> 和 BorrowBean 來顯示資料 --%>
<jsp:useBean id="records"
    type="java.util.List"
    scope="request" />
<% List<BorrowBean> borrowRecords = (List
<BorrowBean>)
request.getAttribute("records");
if (borrowRecords != null &&
!borrowRecords.isEmpty()) {
for (BorrowBean record :
borrowRecords) {
%>
<tr
class="state-<%= record.getBorrowState() %>">
<td>
    <%= record.getBorrowId() %>
</td>
<td>
    <%= dateFormat.format(record.getBorrowDate())
        %>
</td>
<td>
    <%= record.getSourceShopName()
        %>
</td>
<td>
    <%= record.getDestinationShopName()
        %>
</td>
<td>
    <%= record.getItemCount() %>
</td>
<td>
    <span
            class="state-badge state-<%= record.getBorrowState() %>">
        <% String stateText;
            switch
            (record.getBorrowState())
            { case "C" :
            stateText="Created" ;
            break; case "A" :
            stateText="Approved"
            ; break; case "R" :
            stateText="Rejected"
            ; break; case "F" :
            stateText="Finished"
            ; break; default:
            stateText="Unknown" ;
            break; } %>
            <%= stateText %>
    </span>
</td>
<td>
    <% if
        (!("F".equals(record.getBorrowState())))
        { %>
        <form method="post"
                action="BorrowRecordsServlet"
                style="display:inline">
        
        <% if
            ("A".equals(record.getBorrowState())&& 
            record.getDestinationShopId().equals(session.getAttribute("shopId"))) 
            { %>

            <input type="hidden"
                    name="recordId"
                    value="<%= record.getBorrowId() %>">
            <input type="hidden"
                    name="action"
                    value="complete">
            <button type="submit"
                    class="btn btn-sm btn-success"
                    onclick="return confirm('Mark as completed?')">Complete</button>
                </form>
            <% } %>
                <!-- 如果state is C and souceshopid != shopid, show two button for accept or reject -->

            <% if
                ("C".equals(record.getBorrowState())
                && record.getSourceShopId().equals(session.getAttribute("shopId"))) { %>

                <input type="hidden"
                        name="recordId"
                        value="<%= record.getBorrowId() %>">
                <input type="hidden"
                        name="action"
                        value="accept">
                <button type="submit"
                        class="btn btn-sm btn-primary">Accept</button>
                </form>
                <form method="post"
                    action="BorrowRecordsServlet"
                    style="display:inline">
                <input type="hidden"
                        name="recordId"
                        value="<%= record.getBorrowId() %>">
                <input type="hidden"
                        name="action"
                        value="reject">
                <button type="submit"
                        class="btn btn-sm btn-danger">Reject</button> 
                    </form>
            
            
                <% }} %>
                    <a href="BorrowRecordDetailServlet?id=<%= record.getBorrowId() %>"
                        class="btn btn-sm btn-info">Details</a>
</td>
</tr>
<% } } else { %>
<tr>
    <td colspan="7"
        class="text-center">No
        borrow records available
    </td>
</tr>
<% } %>
</tbody>
</table>
</div>
</div>
<!-- /content -->

<!-- GoToTop -->
<div id="page-top" style="">
<a href="#header"><img
src="<%= request.getContextPath() %>/images/common/returan-top.png" /></a>
</div>
<!-- /GoToTop -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
crossorigin="anonymous"></script>
</body>

</html>