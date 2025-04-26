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
                // 從表格中提取資料
                const labels = [];
                const data = [];
            
                // 遍歷表格的每一行，提取年份和季節作為 labels，提取總消耗量作為 data
                document.querySelectorAll('#chart-table tbody tr').forEach(row => {
                    const year = row.children[0].innerText.trim(); // 年份
                    const season = row.children[1].innerText.trim(); // 季節
                    const totalConsumed = parseInt(row.children[3].innerText.trim(), 10); // 總消耗量
            
                    labels.push(`${year} ${season}`); // 合併年份和季節作為標籤
                    data.push(totalConsumed); // 添加總消耗量
                });
            
                // 繪製圖表
                const ctx = document.getElementById('myChart').getContext('2d');
                new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Total Consumed',
                            data: data,
                            borderColor: 'rgba(75, 192, 192, 1)',
                            backgroundColor: 'rgba(75, 192, 192, 0.2)',
                            tension: 0.4
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
                                text: 'Consumption Report by Year and Season'
                            }
                        },
                        scales: {
                            x: {
                                title: {
                                    display: true,
                                    text: 'Year and Season' // X 軸標籤
                                }
                            },
                            y: {
                                beginAtZero: true,
                                title: {
                                    display: true,
                                    text: 'Total Consumed' // Y 軸標籤
                                }
                            }
                        }
                    }
                });
            </script>



            // Initialize Bootstrap Table
            $(document).ready(function() {
                $('#chart-table').bootstrapTable();
            });
        </script>
    </body>
</html>