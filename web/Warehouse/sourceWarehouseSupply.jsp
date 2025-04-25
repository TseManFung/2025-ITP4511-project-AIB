<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Source Warehouse Supply</title>
        <style>
            .content-bg {
                min-height: calc(100vh);
            }
            .insufficient {
                color: red;
            }
        </style>
    </head>
    <body>
        <jsp:include page="/component/modal.jsp" />
        <component:navbar />

        <!-- header -->
        <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>
        <!-- /header -->

        <!-- content -->
        <div class="d-flex position-relative content-bg justify-content-center">
            <div class="container">
                <h1 class="mb-4">Source Warehouse Supply to Central Warehouse</h1>

                <!-- Country Selection -->
                <form id="countryForm" action="sourceWarehouseSupplyServlet" method="GET">
                    <div class="mb-3">
                        <label for="targetCountryId" class="form-label">Select Country:</label>
                        <select name="targetCountryId" id="targetCountryId" class="form-select" onchange="this.form.submit()">
                            <option value="">All Countries</option>
                            <%
                                Map<Long, String> countryNames = (Map<Long, String>) request.getAttribute("countryNames");
                                Map<Long, Map<Long, Integer>> demandByCountry = (Map<Long, Map<Long, Integer>>) request.getAttribute("demandByCountry");
                                String selectedCountryId = request.getParameter("targetCountryId");
                                if (countryNames != null && demandByCountry != null) {
                                    for (Long countryId : demandByCountry.keySet()) {
                                        String countryName = countryNames.get(countryId);
                                        String selected = countryId.toString().equals(selectedCountryId) ? "selected" : "";
                            %>
                            <option value="<%= countryId %>" <%= selected %>><%= countryName %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                </form>

                <!-- Supply Form -->
                <form id="supplyForm" action="sourceWarehouseSupplyServlet" method="POST">
                    <%
                        Map<Long, Integer> availableStock = (Map<Long, Integer>) request.getAttribute("availableStock");
                        Map<Long, String> fruitNames = (Map<Long, String>) request.getAttribute("fruitNames");

                        if (demandByCountry != null && !demandByCountry.isEmpty()) {
                            if (selectedCountryId != null && !selectedCountryId.isEmpty()) {
                                Long countryId = Long.parseLong(selectedCountryId);
                                Map<Long, Integer> demand = demandByCountry.get(countryId);
                                String countryName = countryNames.get(countryId);
                                if (demand != null) {
                    %>
                    <h2>Demand for <%= countryName %></h2>
                    <input type="hidden" name="targetCountryId" value="<%= countryId %>">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Fruit ID</th>
                                <th>Fruit Name</th>
                                <th>Demand Quantity</th>
                                <th>Available Stock</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (Map.Entry<Long, Integer> fruitEntry : demand.entrySet()) {
                                    Long fruitId = fruitEntry.getKey();
                                    Integer needed = fruitEntry.getValue();
                                    Integer available = availableStock.getOrDefault(fruitId, 0);
                                    String fruitName = fruitNames.get(fruitId);
                            %>
                            <tr>
                                <td><%= fruitId %></td>
                                <td><%= fruitName %></td>
                                <td><%= needed %></td>
                                <td class="<%= available < needed ? "insufficient" : "" %>"><%= available %></td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                    <button type="submit" class="btn btn-primary">Supply to <%= countryName %></button>
                    <%
                                } else {
                    %>
                    <p>No demand found for the selected country.</p>
                    <%
                                }
                            } else {
                                for (Map.Entry<Long, Map<Long, Integer>> countryEntry : demandByCountry.entrySet()) {
                                    Long countryId = countryEntry.getKey();
                                    Map<Long, Integer> demand = countryEntry.getValue();
                                    String countryName = countryNames.get(countryId);
                    %>
                    <h2>Demand for <%= countryName %></h2>
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Fruit ID</th>
                                <th>Fruit Name</th>
                                <th>Demand Quantity</th>
                                <th>Available Stock</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (Map.Entry<Long, Integer> fruitEntry : demand.entrySet()) {
                                    Long fruitId = fruitEntry.getKey();
                                    Integer needed = fruitEntry.getValue();
                                    Integer available = availableStock.getOrDefault(fruitId, 0);
                                    String fruitName = fruitNames.get(fruitId);
                            %>
                            <tr>
                                <td><%= fruitId %></td>
                                <td><%= fruitName %></td>
                                <td><%= needed %></td>
                                <td class="<%= available < needed ? "insufficient" : "" %>"><%= available %></td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                    <%
                                }
                    %>
                    <button type="submit" name="acceptAll" value="true" class="btn btn-success">Accept All Requests</button>
                    <%
                            }
                        } else {
                    %>
                    <p>No demand found for your country's fruits.</p>
                    <%
                        }
                    %>
                </form>
            </div>
        </div>
        <!-- /content -->

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>