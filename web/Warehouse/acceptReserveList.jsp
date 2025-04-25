<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.ReserveBean" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/component/head.jsp" />
    <title>Reserve Approval</title>
    <style>
        .content-bg {
            min-height: calc(100vh);
        }
    </style>
</head>
<body>
    <jsp:include page="/component/modal.jsp" />
    <component:navbar/>

    <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>

    <div class="d-flex position-relative content-bg justify-content-center">
        <div class="container">
            <div class="container mt-5">
                <h1>Reserve Approval</h1>

                <form action="acceptReserveListServlet" method="POST">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th><input type="checkbox" id="selectAll" onclick="toggleSelectAll()"></th>
                                <th>ID</th>
                                <th>Shop</th>
                                <th>Reserve Date</th>
                                <th>Fruit</th>
                                <th>Quantity</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<ReserveBean> reserves = (List<ReserveBean>) request.getAttribute("reserves");
                                List<String> shops = (List<String>) request.getAttribute("shops");
                                List<String> fruits = (List<String>) request.getAttribute("fruits");
                                if (reserves != null && !reserves.isEmpty()) {
                                    Long currentReserveId = null;
                                    int rowSpan = 0;
                                    for (int i = 0; i < reserves.size(); i++) {
                                        ReserveBean reserve = reserves.get(i);
                                        boolean isNewGroup = !reserve.getId().equals(currentReserveId);

                                        if (isNewGroup) {
                                            currentReserveId = reserve.getId();
                                            rowSpan = 1;
                                            // Count rows for this reserveId
                                            for (int j = i + 1; j < reserves.size() && reserves.get(j).getId().equals(currentReserveId); j++) {
                                                rowSpan++;
                                            }
                                        }
                            %>
                                <tr>
                                    <% if (isNewGroup) { %>
                                        <td rowspan="<%= rowSpan %>">
                                            <input type="checkbox" name="selectedReserves" value="<%= reserve.getId() %>">
                                        </td>
                                        <td rowspan="<%= rowSpan %>"><%= reserve.getId() %></td>
                                        <td rowspan="<%= rowSpan %>">
                                            <%
                                                if (shops != null) {
                                                    for (String shop : shops) {
                                                        String[] parts = shop.split(":");
                                                        String id = parts[0];
                                                        if (id.equals(String.valueOf(reserve.getShopId()))) {
                                                            out.print(parts[1]);
                                                            break;
                                                        }
                                                    }
                                                }
                                            %>
                                        </td>
                                        <td rowspan="<%= rowSpan %>"><%= reserve.getReserveDT() %></td>
                                    <% } %>
                                    <td>
                                        <%
                                            if (fruits != null) {
                                                for (String fruit : fruits) {
                                                    String[] parts = fruit.split(":");
                                                    String id = parts[0];
                                                    if (id.equals(String.valueOf(reserve.getFruitId()))) {
                                                        out.print(parts[1]);
                                                        break;
                                                    }
                                                }
                                            }
                                        %>
                                    </td>
                                    <td><%= reserve.getNum() %></td>
                                </tr>
                            <%
                                    }
                                }
                            %>
                        </tbody>
                    </table>

                    <div class="mb-3">
                        <button type="submit" name="action" value="approve" class="btn btn-success">Approve Selected</button>
                        <button type="submit" name="action" value="reject" class="btn btn-danger">Reject Selected</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div id="page-top">
        <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" /></a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>
    <script>
        function toggleSelectAll() {
            const selectAll = document.getElementById('selectAll');
            const checkboxes = document.getElementsByName('selectedReserves');
            for (let checkbox of checkboxes) {
                checkbox.checked = selectAll.checked;
            }
        }
    </script>
</body>
</html>