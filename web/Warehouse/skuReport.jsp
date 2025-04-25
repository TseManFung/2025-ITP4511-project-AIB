<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page isELIgnored="false" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.SkuReportBean" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="/component/head.jsp" />
    <title>SKU Delivery Forecast Report</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .content-bg {
            min-height: calc(100vh - 128px);
            background-color: #f8f9fa;
        }
        .dashboard-container {
            max-width: 1200px;
            margin-top: 2rem;
        }
        .chart-container {
            max-width: 800px;
            margin: 2rem auto;
        }
        .available {
            color: green;
            font-weight: bold;
        }
        .unavailable {
            color: red;
        }
    </style>
</head>
<body>
    <jsp:include page="/component/modal.jsp" />
    <component:navbar />

    <div style="height: 128px; background-color: white;" id="header"></div>

    <div class="d-flex position-relative content-bg justify-content-center">
        <div class="container dashboard-container">
            <h1 class="mb-4">SKU Delivery Forecast Report</h1>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
            <% } %>

            <!-- Filter Form -->
            <form action="skuReportServlet" method="GET" class="mb-4">
                <div class="row">
                    <div class="col-md-4">
                        <label for="countryId" class="form-label">Country</label>
                        <select class="form-control" id="countryId" name="countryId">
                            <option value="">All Countries</option>
                            <%
                                List<String> countries = (List<String>) request.getAttribute("countries");
                                String selectedCountryId = request.getParameter("countryId");
                                if (countries != null) {
                                    for (int i = 0; i < countries.size(); i++) {
                                        String country = countries.get(i);
                                        String[] parts = country.split(":");
                                        String id = parts[0];
                                        String name = parts[1];
                                        boolean isSelected = selectedCountryId != null && id.equals(selectedCountryId);
                            %>
                                <option value="<%= id %>" <%= isSelected ? "selected" : "" %>><%= name %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label for="sortBy" class="form-label">Sort By</label>
                        <select class="form-control" id="sortBy" name="sortBy">
                            <%
                                String[] sortOptions = {"countryName:Country Name", "fruitName:Fruit Name", "avgDeliveryDays:Avg Delivery Days", "totalNeeded:Total Needed"};
                                String selectedSortBy = request.getParameter("sortBy") != null ? request.getParameter("sortBy") : "countryName";
                                for (int i = 0; i < sortOptions.length; i++) {
                                    String[] parts = sortOptions[i].split(":");
                                    String value = parts[0];
                                    String label = parts[1];
                                    boolean isSelected = value.equals(selectedSortBy);
                            %>
                                <option value="<%= value %>" <%= isSelected ? "selected" : "" %>><%= label %></option>
                            <%
                                }
                            %>
                        </select>
                    </div>
                    <div class="col-md-4 align-self-end">
                        <button type="submit" class="btn btn-primary">Apply Filters</button>
                    </div>
                </div>
            </form>

            <!-- Report Table -->
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Country</th>
                        <th>Fruit</th>
                        <th>Avg Delivery Days</th>
                        <th>Total Needed</th>
                        <th>Stock Available</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<SkuReportBean> skuReports = (List<SkuReportBean>) request.getAttribute("skuReports");
                        if (skuReports != null) {
                            for (int i = 0; i < skuReports.size(); i++) {
                                SkuReportBean report = skuReports.get(i);
                    %>
                        <tr>
                            <td><%= report.getCountryName() %></td>
                            <td><%= report.getFruitName() %></td>
                            <td><%= report.getAvgDeliveryDays() %></td>
                            <td><%= report.getTotalNeeded() != null ? report.getTotalNeeded() : 0 %></td>
                            <td class="<%= report.isStockAvailable() ? "available" : "unavailable" %>">
                                <%= report.isStockAvailable() ? "Yes" : "No" %>
                            </td>
                        </tr>
                    <%
                            }
                        }
                    %>
                </tbody>
            </table>

            <!-- Bar Chart -->
            <div class="chart-container">
                <canvas id="skuChart"></canvas>
            </div>
        </div>
    </div>

    <div id="page-top">
        <a href="#header"><img src="${pageContext.request.contextPath}/images/common/returan-top.png" alt="Return to top" /></a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <script>
        const ctx = document.getElementById('skuChart').getContext('2d');
        const data = {
            labels: [
                <%
                    if (skuReports != null) {
                        for (int i = 0; i < skuReports.size(); i++) {
                            SkuReportBean report = skuReports.get(i);
                            out.print("'" + report.getCountryName() + " - " + report.getFruitName() + "'");
                            if (i < skuReports.size() - 1) {
                                out.print(",");
                            }
                        }
                    }
                %>
            ],
            datasets: [{
                label: 'Total Needed Quantity',
                data: [
                    <%
                        if (skuReports != null) {
                            for (int i = 0; i < skuReports.size(); i++) {
                                SkuReportBean report = skuReports.get(i);
                                out.print(report.getTotalNeeded() != null ? report.getTotalNeeded() : 0);
                                if (i < skuReports.size() - 1) {
                                    out.print(",");
                                }
                            }
                        }
                    %>
                ],
                backgroundColor: 'rgba(54, 162, 235, 0.5)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        };
        new Chart(ctx, {
            type: 'bar',
            data: data,
            options: {
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Total Needed Quantity'
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: true
                    }
                }
            }
        });
    </script>
</body>
</html>