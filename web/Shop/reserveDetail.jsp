<%-- 
    Document   : reserveDetail
    Created on : 2025年4月23日, 下午6:56:00
    Author     : andyt
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%!
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Reservation detail</title>
        <style>
            .state-badge {
                padding: 0.25em 0.4em;
                border-radius: 0.25rem;
            }

            .state-C {
                background-color: blue;
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
        </style>
    </head>
    <body>
        <jsp:include page="/component/modal.jsp" />

        <component:navbar/>

        <!-- header -->
        <div style="height: calc(0lvh + 128px)" id="header"></div>
        <!-- /header -->

        <!-- content -->
        <div class="d-flex position-relative content-bg justify-content-center">
            <div class="container">
                <%
                    Map<String, Object> details = (Map<String, Object>) request.getAttribute("details");
                    Long id = details != null ? (Long) details.get("id") : null;
                    String state = details != null ? (String) details.get("state") : null;
                %>
                <h2 class="mb-4">Reservation Details #<%= id != null ? id : "" %></h2>

                <div class="card mb-4">
                    <div class="card-header">Basic Info</div>
                    <div class="card-body">
                        <dl class="row">
                            <dt class="col-sm-3">Create Time:</dt>
                            <dd class="col-sm-9">
                                <%= details != null && details.get("DT") != null ? dateFormat.format(details.get("DT")) : "" %>
                            </dd>
                            
                            <dt class="col-sm-3">Reserve Time:</dt>
                            <dd class="col-sm-9">
                                <%= details != null && details.get("reserveDT") != null ? dateFormat.format(details.get("reserveDT")) : "" %>
                            </dd>
                            <dt class="col-sm-3">Status:</dt>
                            <dd class="col-sm-9">
                                <span class="state-badge state-<%= state != null ? state : "" %>">
                                    <%
                                        String stateText = "Unknown";
                                        if (state != null) {
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
                                        }
                                    %>
                                    <span class="state-badge state-<%= record.get("state") %> text-white">
                                        <%= stateText %>
                                    </span>
                                    
                                </span>
                            </dd>
                        </dl>
                    </div>
                </div>

                <div class="card">
                    <div class="card-header">Items</div>
                    <div class="card-body">
                        <table class="table table-bordered">
                            <thead>
                                <tr>
                                    <th>Fruit Name</th>
                                    <th>Quantity</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    if (details != null) {
                                        List<Map<String, Object>> items = (List<Map<String, Object>>) details.get("items");
                                        if (items != null) {
                                            for (Map<String, Object> item : items) {
                                %>
                                <tr>
                                    <td><%= item.get("fruitName") %></td>
                                    <td><%= item.get("quantity") %></td>
                                </tr>
                                <%
                                            }
                                        }
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>

                <a href="ReserveRecordServlet" class="btn btn-secondary mt-3">Back to List</a>
            
                <% if ("A".equals(state)) { %>
                        <form method="post" action="ReserveRecordServlet" style="display:inline">
                            <input type="hidden" name="reserveId" value="<%= id %>">
                            <button type="submit" class="btn btn-success" onclick="return confirm('Mark as completed?')">Complete</button>
                        </form>
                    
                <% } %>
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