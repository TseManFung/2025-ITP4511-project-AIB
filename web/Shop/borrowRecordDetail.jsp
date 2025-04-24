<%-- 
    Document   : borrowRecordDetail
    Created on : 2025年4月23日, 下午7:29:10
    Author     : andyt
--%>

<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Map"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="true" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Borrow record Detail</title>

    </head>
    <body>
        <jsp:include page="/component/modal.jsp" />

        <component:navbar/>

        <!-- header -->
        <div style="height: calc(0lvh + 128px)" id="header"></div>
        <!-- /header -->
        <%
            Map<String, Object> detail = (Map<String, Object>) request.getAttribute("detail");
        %>
        <!-- content -->
        <div class="d-flex position-relative content-bg justify-content-center">
            <div class="container">
                <% if (detail != null) {%>
                <div class="card shadow">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">Borrow Record Details</h4>
                    </div>

                    <div class="card-body">
                        <dl class="row mb-4">
                            <dt class="col-sm-3">Record ID:</dt>
                            <dd class="col-sm-9"><%= detail.get("id")%></dd>

                            <dt class="col-sm-3">Date:</dt>
                            <dd class="col-sm-9"><%= detail.get("DT")%></dd>

                            <dt class="col-sm-3">Status:</dt>
                            <dd class="col-sm-9">
                                <span class="state-badge state-<%= detail.get("state")%> text-white">
                                    <%
                                        String state = (String) detail.get("state");
                                        if ("C".equals(state)) {
                                    %>
                                    Pending
                                    <%
                                    } else if ("A".equals(state)) {
                                    %>
                                    Approved
                                    <%
                                    } else if ("R".equals(state)) {
                                    %>
                                    Rejected
                                    <%
                                        }
                                    %>
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
                                <%
                                    List<Map<String, Object>> items = (List<Map<String, Object>>) detail.get("items");
                                    for (Map<String, Object> item : items) {
                                %>
                                <tr>
                                    <td><%= item.get("name")%></td>
                                    <td><%= item.get("quantity")%></td>
                                </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>

                        <%
                            if ("C".equals(detail.get("state"))
                                    && ((Long) detail.get("destShopId")).equals(session.getAttribute("shopId"))) {
                        %>
                        <div class="mt-4">
                            <form method="post" action="BorrowRecords">
                                <input type="hidden" name="recordId" value="<%= detail.get("id")%>">
                                <button type="submit" name="action" value="approve" 
                                        class="btn btn-success me-2">Approve</button>
                                <button type="submit" name="action" value="reject" 
                                        class="btn btn-danger">Reject</button>
                            </form>
                        </div>
                        <%
                            }
                        %>
                    </div>

                    <div class="card-footer text-end">
                        <a href="BorrowRecordsServlet" class="btn btn-secondary">Back to List</a>
                    </div>
                </div>
                <% } else { %>
                <div class="alert alert-danger text-center">
                    No details available for this record.
                </div>
                <% }%>
            </div> 
        </div>
        <!-- /content -->

        <!-- GoToTop -->
        <div id="page-top" style="">
            <a href="#header"><img src="<%= request.getContextPath()%>/images/common/returan-top.png" /></a>
        </div>
        <!-- /GoToTop -->

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>      
    </body>
</html>