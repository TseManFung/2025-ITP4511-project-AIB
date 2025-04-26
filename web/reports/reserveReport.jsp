<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@ page import="java.util.List" %>
<%@ page import="AIB.Bean.ReserveReportBean" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%!
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Reserve Report</title>
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
                <h2>Reservation Report - ${param.type.toUpperCase()}</h2>

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
                                <th data-field="location">Location</th>
                                <th data-field="fruitName">Fruit Name</th>
                                <th data-field="totalReserved">Total Reserved</th>
                                <th data-field="latestReservation">Latest Reservation</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                                List<ReserveReportBean> reserveReport = (List<ReserveReportBean>) request.getAttribute("reserveReport");
                                if (reserveReport != null) {
                                    for (ReserveReportBean item : reserveReport) { 
                            %>
                            <tr>
                                <td><%= item.getLocationName() %></td>
                                <td><%= item.getFruitName() %></td>
                                <td><%= item.getTotalReserved() %></td>
                                <td><%= item.getLatestReserveDate() != null ? dateFormat.format(item.getLatestReserveDate()) : "N/A" %></td>
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
            // Prepare data for the chart
            const labels = [];
            const data = [];
            <% 
                if (reserveReport != null) {
                    for (ReserveReportBean item : reserveReport) { 
            %>
            labels.push('<%= item.getFruitName() %> (<%= item.getLocationName() %>)');
            data.push(<%= item.getTotalReserved() %>);
            <% 
                    }
                } 
            %>

            // Draw the chart
            const ctx = document.getElementById('myChart').getContext('2d');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Total Reserved',
                        data: data,
                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                        title: {
                            display: true,
                            text: 'Reservation Report'
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