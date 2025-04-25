<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.WarehouseStockChangeBean" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/component/head.jsp" />
    <title>Central Warehouse Receive</title>
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
                <h1>Receive Stock</h1>

                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Start Time</th>
                            <th>Source</th>
                            <th>Fruit</th>
                            <th>Quantity</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<WarehouseStockChangeBean> pendingTransfers = (List<WarehouseStockChangeBean>) request.getAttribute("pendingTransfers");
                            List<String> warehouses = (List<String>) request.getAttribute("warehouses");
                            List<String> fruits = (List<String>) request.getAttribute("fruits");
                            if (pendingTransfers != null) {
                                for (int i = 0; i < pendingTransfers.size(); i++) {
                                    WarehouseStockChangeBean transfer = pendingTransfers.get(i);
                        %>
                            <tr>
                                <td><%= transfer.getId() %></td>
                                <td><%= transfer.getDeliveryStartTime() %></td>
                                <td>
                                    <%
                                        if (warehouses != null) {
                                            for (int j = 0; j < warehouses.size(); j++) {
                                                String warehouse = warehouses.get(j);
                                                String[] parts = warehouse.split(":");
                                                String id = parts[0];
                                                if (id.equals(String.valueOf(transfer.getSourceWarehouseId()))) {
                                                    out.print(parts[1]);
                                                    break;
                                                }
                                            }
                                        }
                                    %>
                                </td>
                                <td>
                                    <%
                                        if (fruits != null) {
                                            for (int j = 0; j < fruits.size(); j++) {
                                                String fruit = fruits.get(j);
                                                String[] parts = fruit.split(":");
                                                String id = parts[0];
                                                if (id.equals(String.valueOf(transfer.getFruitId()))) {
                                                    out.print(parts[1]);
                                                    break;
                                                }
                                            }
                                        }
                                    %>
                                </td>
                                <td><%= transfer.getNum() %></td>
                                <td>
                                    <form action="centralWarehouseReceiveServlet" method="POST" style="display:inline;">
                                        <input type="hidden" name="transferId" value="<%= transfer.getId() %>">
                                        <input type="hidden" name="action" value="receive">
                                        <button type="submit" class="btn btn-success btn-sm">Receive</button>
                                    </form>
                                    <form action="centralWarehouseReceiveServlet" method="POST" style="display:inline;">
                                        <input type="hidden" name="transferId" value="<%= transfer.getId() %>">
                                        <input type="hidden" name="action" value="reject">
                                        <button type="submit" class="btn btn-danger btn-sm">Reject</button>
                                    </form>
                                </td>
                            </tr>
                        <%
                                }
                            }
                        %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div id="page-top">
        <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" /></a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.js"></script>
</body>
</html>