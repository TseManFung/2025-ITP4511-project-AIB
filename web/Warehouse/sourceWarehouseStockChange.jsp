<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.WarehouseStockChangeBean" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/component/head.jsp" />
    <title>Source Warehouse Stock Transfer</title>
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
                <h1>Stock Transfer</h1>

                <form action="sourceWarehouseStockChangeServlet" method="POST">
                    <div class="mb-3">
                        <label for="destinationWarehouseId" class="form-label">Destination Warehouse</label>
                        <select class="form-control" id="destinationWarehouseId" name="destinationWarehouseId" required>
                            <%
                                List<String> centralWarehouses = (List<String>) request.getAttribute("centralWarehouses");
                                if (centralWarehouses != null) {
                                    for (int i = 0; i < centralWarehouses.size(); i++) {
                                        String warehouse = centralWarehouses.get(i);
                                        String[] parts = warehouse.split(":");
                                        String id = parts[0];
                                        String name = parts[1];
                            %>
                                <option value="<%= id %>"><%= name %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="fruitId" class="form-label">Fruit</label>
                        <select class="form-control" id="fruitId" name="fruitId" required>
                            <%
                                List<String> fruits = (List<String>) request.getAttribute("fruits");
                                if (fruits != null) {
                                    for (int i = 0; i < fruits.size(); i++) {
                                        String fruit = fruits.get(i);
                                        String[] parts = fruit.split(":");
                                        String id = parts[0];
                                        String name = parts[1];
                            %>
                                <option value="<%= id %>"><%= name %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="num" class="form-label">Quantity</label>
                        <input type="number" class="form-control" id="num" name="num" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Create Transfer</button>
                </form>

                <h2 class="mt-5">Transfer History</h2>
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Start Time</th>
                            <th>End Time</th>
                            <th>Destination</th>
                            <th>Fruit</th>
                            <th>Quantity</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<WarehouseStockChangeBean> transfers = (List<WarehouseStockChangeBean>) request.getAttribute("transfers");
                            if (transfers != null) {
                                for (int i = 0; i < transfers.size(); i++) {
                                    WarehouseStockChangeBean transfer = transfers.get(i);
                        %>
                            <tr>
                                <td><%= transfer.getId() %></td>
                                <td><%= transfer.getDeliveryStartTime() %></td>
                                <td><%= transfer.getDeliveryEndTime() %></td>
                                <td>
                                    <%
                                        if (centralWarehouses != null) {
                                            for (int j = 0; j < centralWarehouses.size(); j++) {
                                                String warehouse = centralWarehouses.get(j);
                                                String[] parts = warehouse.split(":");
                                                String id = parts[0];
                                                if (id.equals(String.valueOf(transfer.getDestinationWarehouseId()))) {
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
                                <td><%= transfer.getState() %></td>
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