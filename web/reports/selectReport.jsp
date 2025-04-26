<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
    <title>Report Center</title>
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <style>
        .report-card {
            transition: transform 0.3s, box-shadow 0.3s;
            cursor: pointer;
        }
        .report-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
        }
        .selected-card {
            border: 2px solid #0d6efd !important;
            background-color: #f8f9fa;
        }
        #locationSelection { display: none; }
    </style>
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
        <div class="text-center mb-5">
            <h1 class="display-5 fw-bold text-primary">AIB Analytics Center</h1>
            <p class="lead">Select report type and location parameters</p>
        </div>

        <!-- Report Type Selection -->
        <div class="row g-4 mb-5" id="reportTypeSelection">
            <div class="col-md-6">
                <div class="card report-card h-100" data-report-type="reserve">
                    <div class="card-body text-center">
                        <svg class="bi mb-3" width="40" height="40" fill="#0d6efd">
                            <use href="#box-seam"/>
                        </svg>
                        <h3 class="card-title">Reservation Needs</h3>
                        <p class="text-muted">View aggregated reservation requests by location</p>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card report-card h-100" data-report-type="consumption">
                    <div class="card-body text-center">
                        <svg class="bi mb-3" width="40" height="40" fill="#0d6efd">
                            <use href="#pie-chart"/>
                        </svg>
                        <h3 class="card-title">Consumption Analysis</h3>
                        <p class="text-muted">Analyze consumption patterns by season</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Location Selection -->
        <div class="card shadow-lg" id="locationSelection">
            <div class="card-header bg-primary text-white">
                <h4 class="mb-0">Select Location Scope</h4>
            </div>
            <div class="card-body">
                <form id="reportForm" action="${pageContext.request.contextPath}/reports" method="GET">
                    <input type="hidden" name="report" id="selectedReport">
                    
                    <div class="row g-3">
                        <!-- Location Type Selection -->
                        <div class="col-md-4">
                            <label class="form-label">Scope Type:</label>
                            <select class="form-select" id="locationType" name="type" required>
                                <option value="">Select Scope</option>
                                <option value="shop">Shop Level</option>
                                <option value="city">City Level</option>
                                <option value="country">Country Level</option>
                            </select>
                        </div>

                        <!-- Dynamic Location Selector -->
                        <div class="col-md-8">
                            <label class="form-label">Select Location:</label>
                            <select class="form-select" id="locationSelect" name="id" required 
                                    style="width: 100%" disabled>
                                <option value="">Select location...</option>
                            </select>
                        </div>
                    </div>

                    <div class="d-grid mt-4">
                        <button type="submit" class="btn btn-primary btn-lg">
                            <svg class="bi me-2" width="20" height="20">
                                <use href="#file-earmark-bar-graph"/>
                            </svg>
                            Generate Report
                        </button>
                    </div>
                </form>
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
    <svg xmlns="http://www.w3.org/2000/svg" style="display: none;">
        <symbol id="box-seam" viewBox="0 0 16 16">
            <path d="M8.186 1.113a.5.5 0 0 0-.372 0L1.846 3.5l2.404.961L10.404 2zm3.564 1.426L5.596 5 8 5.961 14.154 3.5zm3.25 1.7-6.5 2.6v7.922l6.5-2.6V4.24zM7.5 14.762V6.838L1 4.239v7.923zM7.443.184a1.5 1.5 0 0 1 1.114 0l7.129 2.852A.5.5 0 0 1 16 3.5v8.662a1 1 0 0 1-.629.928l-7.185 2.874a.5.5 0 0 1-.372 0L.63 13.09a1 1 0 0 1-.63-.928V3.5a.5.5 0 0 1 .314-.464z"/>
        </symbol>
        <symbol id="pie-chart" viewBox="0 0 16 16">
            <path d="M7.5 1.018a7 7 0 0 0-4.79 11.566L7.5 7.793zm1 0V7.5h6.482A7.001 7.001 0 0 0 8.5 1.018M14.982 8.5H8.207l-4.79 4.79A7 7 0 0 0 14.982 8.5M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8"/>
        </symbol>
        <symbol id="file-earmark-bar-graph" viewBox="0 0 16 16">
            <path d="M10 13.5a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-6a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5zm-2.5.5a.5.5 0 0 1-.5-.5v-4a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v4a.5.5 0 0 1-.5.5zm-3 0a.5.5 0 0 1-.5-.5v-2a.5.5 0 0 1 .5-.5h1a.5.5 0 0 1 .5.5v2a.5.5 0 0 1-.5.5z"/>
            <path d="M14 14V4.5L9.5 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2M9.5 3A1.5 1.5 0 0 0 11 4.5h2V14a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h5.5z"/>
        </symbol>
    </svg>

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <script>
        $(document).ready(function() {
            // Report Type Selection
            $('.report-card').click(function() {
                $('.report-card').removeClass('selected-card');
                $(this).addClass('selected-card');
                const reportType = $(this).data('report-type');
                $('#selectedReport').val(reportType);
                $('#locationSelection').slideDown();
            });

            // Location Type Change Handler
            $('#locationType').change(function() {
                const locationType = $(this).val();
                $('#locationSelect').prop('disabled', true).empty();

                if (locationType) {
                    $.getJSON('${pageContext.request.contextPath}/locations', 
                        { type: locationType }, 
                        function(data) {
                                                        $('#locationSelect').empty(); // 清空選擇器
                            if (data.length > 0) {
                                data.forEach(item => {
                                    $('#locationSelect').append(new Option(item.name, item.id));
                                });
                                $('#locationSelect').prop('disabled', false).trigger('change');
                            } else {
                                $('#locationSelect').append(new Option("No locations available", "")).prop('disabled', true);
                            }
                        }
                    );
                }
            });

            // Initialize Select2
            $('#locationSelect').select2({
                placeholder: "Search location...",
                allowClear: true
            });
        });
    </script>
</body>
</html>

