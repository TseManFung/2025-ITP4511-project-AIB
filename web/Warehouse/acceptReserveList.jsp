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

                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Shop</th>
                            <th>Reserve Date</th>
                            <th>Fruit</th>
                            <th>Quantity</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<ReserveBean> reserves = (List<ReserveBean>) request.getAttribute("reserves");
                            List<String> shops = (List<String>) request.getAttribute("shops");
                            List<String> fruits = (List<String>) request.getAttribute("fruits");
                            if (reserves != null) {
                                for (int i = 0; i < reserves.size(); i++) {
                                    ReserveBean reserve = reserves.get(i);
                        %>
                            <tr>
                                <td><%= reserve.getId() %></td>
                                <td>
                                    <%
                                        if (shops != null) {
                                            for (int j = 0; j < shops.size(); j++) {
                                                String shop = shops.get(j);
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
                                <td><%= reserve.getReserveDT() %></td>
                                <td>
                                    <%
                                        if (fruits != null) {
                                            for (int j = 0; j < fruits.size(); j++) {
                                                String fruit = fruits.get(j);
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
                                <td>
                                    <form action="acceptReserveListServlet" method="POST" style="display:inline;">
                                        <input type="hidden" name="reserveId" value="<%= reserve.getId() %>">
                                        <input type="hidden" name="action" value="approve">
                                        <button type="submit" class="btn btn-success btn-sm">Approve</button>
                                    </form>
                                    <form action="acceptReserveListServlet" method="POST" style="display:inline;">
                                        <input type="hidden" name="reserveId" value="<%= reserve.getId() %>">
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