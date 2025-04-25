<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="AIB.Bean.BorrowBean" %>
<%@ page import="AIB.Bean.FruitBean" %>
<%!
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
%>

<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Borrow Detail</title>
        <style>
            .state-badge {
                padding: 0.25em 0.4em;
                border-radius: 0.25rem;
            }

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
                <%-- 使用 <jsp:useBean> 來處理 BorrowBean 資料 --%>
                <jsp:useBean id="details" type="AIB.Bean.BorrowBean" scope="request" />
                <h2 class="mb-4">Borrow Details #<%= details.getBorrowId() != null ? details.getBorrowId() : "" %></h2>

                <div class="card mb-4">
                    <div class="card-header">Basic Info</div>
                    <div class="card-body">
                        <dl class="row">
                            <dt class="col-sm-3">Borrow Date:</dt>
                            <dd class="col-sm-9">
                                <%= details.getBorrowDate() != null ? dateFormat.format(details.getBorrowDate()) : "" %>
                            </dd>
                            
                            <dt class="col-sm-3">From Source Shop:</dt>
                            <dd class="col-sm-9"><%= details.getSourceShopName() %></dd>

                            <dt class="col-sm-3">To Destination Shop:</dt>
                            <dd class="col-sm-9"><%= details.getDestinationShopName() %></dd>

                            <dt class="col-sm-3">Status:</dt>
                            <dd class="col-sm-9">
                                <span class="state-badge state-<%= details.getBorrowState() != null ? details.getBorrowState() : "" %>">
                                    <%
                                        String stateText = "Unknown";
                                        if (details.getBorrowState() != null) {
                                            switch (details.getBorrowState()) {
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
                                    <th>Unit</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%-- 使用 BorrowBean 的 fruits 屬性來顯示水果列表 --%>
                                <%
                                    if (details.getFruits() != null && !details.getFruits().isEmpty()) {
                                        for (FruitBean fruit : details.getFruits()) {
                                %>
                                <tr>
                                    <td><%= fruit.getName() %></td>
                                    <td><%= fruit.getQuantity() %></td>
                                    <td><%= fruit.getUnit() %></td>
                                </tr>
                                <%
                                        }
                                    } else {
                                %>
                                <tr>
                                    <td colspan="3" class="text-center">No items found</td>
                                </tr>
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>

                <a href="BorrowRecordServlet" class="btn btn-secondary mt-3">Back to List</a>
            
                <% if ("A".equals(details.getBorrowState()) &&
                details.getDestinationShopId().equals(session.getAttribute("shopId"))) { %>
            <form method="post" action="BorrowRecordServlet" style="display:inline">
                <input type="hidden" name="recordId" value="<%= details.getBorrowId() %>">
                <input type="hidden" name="action" value="complete">
                <button type="submit" class="btn mt-3 btn-success" onclick="return confirm('Mark as completed?')">Complete</button>
            </form>
        <% } %>
        
        <% if ("C".equals(details.getBorrowState()) &&
                details.getSourceShopId().equals(session.getAttribute("shopId"))) { %>
            <form method="post" action="BorrowRecordsServlet" style="display:inline">
                <input type="hidden" name="recordId" value="<%= details.getBorrowId() %>">
                <input type="hidden" name="action" value="accept">
                <button type="submit" class="btn mt-3 btn-primary">Accept</button>
            </form>
            <form method="post" action="BorrowRecordsServlet" style="display:inline">
                <input type="hidden" name="recordId" value="<%= details.getBorrowId() %>">
                <input type="hidden" name="action" value="reject">
                <button type="submit" class="btn mt-3 btn-danger">Reject</button>
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
    </body>
</html>