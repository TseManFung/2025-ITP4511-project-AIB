<%-- Document : reserveRecords Created on : 2025年4月23日, 上午10:05:23 Author : andyt --%>
    <%@ page contentType="text/html; charset=UTF-8" %>
        <%@ page isELIgnored="false" %>
            <%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
            <%@ page import="java.text.SimpleDateFormat" %>
<%!
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
%>
                <!DOCTYPE html>
                <html>

                <head>
                    <jsp:include page="/component/head.jsp" />
                    <title>Reservation Records</title>
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
                    <%! public String getErrorMessage(int errorCode) { switch (errorCode) { case 1:
                        return "Invalid reservation ID." ; case 2: return "Database error occurred." ; default:
                        return "An unknown error occurred." ; } } %>
                        <jsp:include page="/component/modal.jsp" />

                        <component:navbar />

                        <!-- header -->
                        <div style="height: calc(0lvh + 128px)" id="header"></div>
                        <!-- /header -->

                        <!-- content -->
                        <div class="d-flex position-relative content-bg justify-content-center">
                            <div class="container">
                                <h2 class="mb-4">Reservation Records</h2>

                                <%-- 顯示成功或錯誤訊息 --%>
                                    <% String successMessage=request.getParameter("success"); String errorMessage=null;
                                       if (request.getParameter("error") !=null) { int
                                       errorCode=Integer.parseInt(request.getParameter("error"));
                                       errorMessage=getErrorMessage(errorCode); } %>
                                        <% if (successMessage !=null) { %>
                                            <div class="alert alert-success">Reservation #<%= successMessage %> completed successfully</div>
                                            <% } %>
                                                <% if (errorMessage !=null) {%>
                                                    <div class="alert alert-danger">
                                                        <%= errorMessage%>
                                                    </div>
                                                    <% }%>

                                                        <div class="card mb-4">
                                                            <div class="card-header">Filter & Sort</div>
                                                            <div class="card-body">
                                                                <form method="get" class="row g-3">
                                                                    <div class="col-md-4">
                                                                        <label class="form-label">Filter by
                                                                            Status:</label>
                                                                        <select name="filter" class="form-select"
                                                                                onchange="this.form.submit()">
                                                                            <option value="">Default</option>
                                                                            <option value="C" <%="C"
                                                                                    .equals(request.getParameter("filter"))
                                                                                    ? "selected" : "" %>>Created
                                                                            </option>
                                                                            <option value="A" <%="A"
                                                                                    .equals(request.getParameter("filter"))
                                                                                    ? "selected" : "" %>>Approved
                                                                            </option>
                                                                            <option value="R" <%="R"
                                                                                    .equals(request.getParameter("filter"))
                                                                                    ? "selected" : "" %>>Rejected
                                                                            </option>
                                                                            <option value="F" <%="F"
                                                                                    .equals(request.getParameter("filter"))
                                                                                    ? "selected" : "" %>>Finished
                                                                            </option>
                                                                        </select>
                                                                    </div>
                                                                    <div class="col-md-4">
                                                                        <label class="form-label">Sort by:</label>
                                                                        <select name="sort" class="form-select"
                                                                                onchange="this.form.submit()">
                                                                            <option value="">Default</option>
                                                                            <option value="date" <%="date"
                                                                                    .equals(request.getParameter("sort"))
                                                                                    ? "selected" : "" %>>Create Date
                                                                            </option>
                                                                            <option value="reserveDate" <%="reserveDate"
                                                                                    .equals(request.getParameter("sort"))
                                                                                    ? "selected" : "" %>>Reserve Date
                                                                            </option>
                                                                            <option value="status" <%="status"
                                                                                    .equals(request.getParameter("sort"))
                                                                                    ? "selected" : "" %>>Status</option>
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
                                                                <% java.util.List<java.util.Map<String, Object>> records
                                                                    = (java.util.List<java.util.Map<String, Object>>)
                                                                        request.getAttribute("records");
                                                                        if (records != null && !records.isEmpty()) {
                                                                        for (java.util.Map<String, Object> record :
                                                                            records) {
                                                                            Long id = (Long) record.get("id");
                                                                            java.sql.Timestamp createDate =
                                                                            (java.sql.Timestamp) record.get("DT");
                                                                            java.sql.Timestamp reserveDate =
                                                                            (java.sql.Timestamp)
                                                                            record.get("reserveDT");
                                                                            int itemCount = (int)
                                                                            record.get("itemCount");
                                                                            String state = (String) record.get("state");
                                                                            %>
                                                                            <tr class="state-<%= state%>">
                                                                                <td>
                                                                                    <%= id%>
                                                                                </td>
                                                                                <td>
                                                                                    <%= dateFormat.format(createDate) %>
                                                                                </td>
                                                                                <td>
                                                                                    <%= dateFormat.format(reserveDate) %>
                                                                                </td>
                                                                                <td>
                                                                                    <%= itemCount%>
                                                                                </td>
                                                                                <td>
                                                                                    <span class="state-badge state-<%= state %>">
                                                                                        <%
                                                                                            String stateText;
                                                                                            switch (state) {
                                                                                                case "C":
                                                                                                    stateText = "Created";
                                                                                                    break;
                                                                                                case "A":
                                                                                                    stateText = "Approved";
                                                                                                    break;
                                                                                                case "R":
                                                                                                    stateText = "Rejected";
                                                                                                    break;
                                                                                                case "F":
                                                                                                    stateText = "Finished";
                                                                                                    break;
                                                                                                default:
                                                                                                    stateText = "Unknown";
                                                                                                    break;
                                                                                            }
                                                                                        %>
                                                                                        <%= stateText %>
                                                                                    </span>
                                                                                </td>
                                                                                <td>
                                                                                    <% if ("A".equals(state)) {%>
                                                                                        <form method="post"
                                                                                              style="display:inline">
                                                                                            <input type="hidden"
                                                                                                   name="reserveId"
                                                                                                   value="<%= id%>">
                                                                                            <button type="submit"
                                                                                                    class="btn btn-sm btn-success"
                                                                                                    onclick="return confirm('Mark as completed?')">Complete</button>
                                                                                        </form>
                                                                                        <% }%>
                                                                                            <a href="ReserveDetailServlet?id=<%= id%>"
                                                                                               class="btn btn-sm btn-info">Details</a>
                                                                                </td>
                                                                            </tr>
                                                                            <% } } else { %>
                                                                                <tr>
                                                                                    <td colspan="6" class="text-center">
                                                                                        No reservation records available
                                                                                    </td>
                                                                                </tr>
                                                                                <% }%>
                                                            </tbody>
                                                        </table>
                            </div>
                        </div>
                        <!-- /content -->

                        <!-- GoToTop -->
                        <div id="page-top" style="">
                            <a href="#header"><img
                                     src="<%= request.getContextPath()%>/images/common/returan-top.png" /></a>
                        </div>
                        <!-- /GoToTop -->

                        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                                integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
                                crossorigin="anonymous"></script>
                        <script
                                src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>
                </body>

                </html>