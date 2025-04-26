<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="AIB.Bean.ConsumptionBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>

<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<jsp:useBean id="consumptionReport" scope="request" type="java.util.List<AIB.Bean.ConsumptionBean>" />
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Consumption Report</title>
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
                <h2>Consumption Report - ${param.type.toUpperCase()}</h2>

                <!-- Chart Section -->
                <div class="mb-5">
                    <canvas id="myChart"></canvas>
                </div>

                <!-- Table Section -->
                <div id="chart-container" class="mb-5">
                    <table id="chart-table" 
                           data-toggle="table"
                           data-show-columns="true"
                           data-show-export="true"
                           data-search="true"
                           data-pagination="true"
                           class="table table-striped">
                        <thead>
                            <tr>
                                <th data-field="year">Year</th>
                                <th data-field="season">Season</th>
                                <th data-field="fruitName">Fruit Name</th>
                                <th data-field="totalConsumed">Total Consumed</th>
                                <th data-field="location">Location</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                                if (consumptionReport != null) {
                                    for (ConsumptionBean item : consumptionReport) { 
                            %>
                            <tr>
                                <td><%= item.getYear() %></td>
                                <td><%= item.getSeason() %></td>
                                <td><%= item.getFruitName() %></td>
                                <td><%= item.getTotalConsumed() %></td>
                                <td><%= item.getLocationName() %></td>
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
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

        <script>
            // Initialize labels and datasets
            const labels = [];
            const datasets = {};

            <% 
            ArrayList<String> labels = new ArrayList<>();
            HashMap<String, Object> datasets = new HashMap<>();
            if (consumptionReport != null) {
                for (ConsumptionBean item : consumptionReport) { 
                    String label = item.getYear() + " - " + item.getSeason();
                    if (!labels.contains(label)) {
                        labels.add(label);
        %>
                    labels.push('<%= label %>');
        <% 
                    }
                    String fruitName = item.getFruitName();
                    if (!datasets.containsKey(fruitName)) {
                        datasets.put(fruitName, new Object()); // Corrected line
                        double r = Math.random();
                        double g = Math.random();
                        double b = Math.random();
                        %>
                    datasets['<%= fruitName %>'] = {
                        label: '<%= fruitName %>',
                        data: [],
                        borderColor: <%= "'rgba(" + (int)(r * 255) + "," + (int)(g * 255) + "," + (int)(b * 255) + ",1)'" %>,
                        backgroundColor: <%= "'rgba(" + (int)(r * 255) + "," + (int)(g * 255) + "," + (int)(b * 255) + ",0.2)'" %>,
                        tension: 0.1,
                        fill: false
                    };
        <% 
                    }
        %>
                    datasets['<%= fruitName %>'].data.push(<%= item.getTotalConsumed() %>);
        <% 
                }
            } 
        %>

            // Convert datasets object to array
            const datasetArray = Object.values(datasets);

            // Draw the chart
            const ctx = document.getElementById('myChart').getContext('2d');
            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: datasetArray
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                        title: {
                            display: true,
                            text: 'Consumption Report by Year and Season'
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });

            // Initialize Bootstrap Table
            $(document).ready(function() {
                $('#chart-table').bootstrapTable();
            });
        </script>
    </body>
</html>