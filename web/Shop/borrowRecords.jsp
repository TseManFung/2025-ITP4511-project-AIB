<%-- Document : borrowRecords Created on : 2025年4月23日, 下午7:23:30 Author : andyt --%>

<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="true" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Borrow record</title>
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
        <component:navbar />

        <!-- header -->
        <div style="height: calc(0lvh + 128px)" id="header"></div>
        <!-- /header -->

        <%
            // 從 request 中獲取 currentShopId 和 records
            Long currentShopId = (Long) request.getAttribute("currentShopId");
            List<Map<String, Object>> records = (List<Map<String, Object>>) request.getAttribute("records");
        %>

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
                        <% if (records != null) { %>
                            <% for (Map<String, Object> record : records) { %>
                                <tr>
                                    <td><%= record.get("DT") %></td>
                                    <td><%= record.get("fromShop") %></td>
                                    <td><%= record.get("toShop") %></td>
                                    <td>
                                        <span class="state-badge state-<%= record.get("state") %> text-white">
                                            <% 
                                                String state = (String) record.get("state");
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
                                    </td>
                                    <td>
                                        <a href="BorrowRecordsDetailServlet?id=<%= record.get("id") %>" class="btn btn-sm btn-outline-primary">View</a>
                                        <% if ("A".equals(record.get("state")) && record.get("toShop").equals(currentShopId.toString())) { %>
                                            <form method="post" class="d-inline">
                                                <input type="hidden" name="recordId" value="<%= record.get("id") %>">
                                                <button type="submit" name="action" value="complete" class="btn btn-sm btn-success">Complete</button>
                                            </form>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        <% } else { %>
                        <tr>
                            <td colspan="5" class="text-center">No records found.</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>