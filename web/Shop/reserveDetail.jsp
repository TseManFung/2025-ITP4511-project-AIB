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
                <h2 class="mb-4">Reservation Details #<%= request.getAttribute("details") != null ? ((Map) request.getAttribute("details")).get("id") : ""%></h2>

                <div class="card mb-4">
                    <div class="card-header">Basic Info</div>
                    <div class="card-body">
                        <dl class="row">
                            <dt class="col-sm-3">Create Time:</dt>
                            <dd class="col-sm-9">
                                <%= request.getAttribute("details") != null ? dateFormat.format(((Map) request.getAttribute("details")).get("DT")) : "" %>
                            </dd>
                            
                            <dt class="col-sm-3">Reserve Time:</dt>
                            <dd class="col-sm-9">
                                <%= request.getAttribute("details") != null ? dateFormat.format(((Map) request.getAttribute("details")).get("reserveDT")) : "" %>
                            </dd>
                            <dt class="col-sm-3">Status:</dt>
                            <dd class="col-sm-9">
                                <span class="state-badge state-<%= request.getAttribute("details") != null ? ((Map) request.getAttribute("details")).get("state") : ""%>">
                                    <%
                                        String stateText = "Unknown";
                                        if (request.getAttribute("details") != null) {
                                            String state = (String) ((Map) request.getAttribute("details")).get("state");
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
                                    <%= stateText %>
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
                                    if (request.getAttribute("details") != null) {
                                        List<Map<String, Object>> items = (List<Map<String, Object>>) ((Map) request.getAttribute("details")).get("items");
                                        for (Map<String, Object> item : items) {
                                %>
                                <tr>
                                    <td><%= item.get("fruitName")%></td>
                                    <td><%= item.get("quantity")%></td>
                                </tr>
                                <%
                                        }
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>

                <a href="ReserveRecordServlet" class="btn btn-secondary mt-3">Back to List</a>
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
