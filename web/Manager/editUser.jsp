<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/component/head.jsp" />
        <title>Edit User</title>
        <style>
            .dynamic-field {
                display: none;
            }
            .required-star {
                color: red;
            }
             .content-bg {
                min-height: calc(100vh);
            }
        </style>
    </head>
    <body>
        <jsp:include page="/component/modal.jsp" />
       <component:navbar/>
        <!-- header -->
        <div style="height: calc(0lvh + 128px); background-color: white;" id="header"></div>
        <!-- /header -->

        <!-- content -->
        <div class="d-flex position-relative content-bg justify-content-center">
            <div class="container">
                <div class="container mt-5">
                    <h1>Edit User: ${user.loginName}</h1>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <form action="editUserServlet" method="POST" onsubmit="return validateForm()">
                        <input type="hidden" name="originalLoginName" value="${user.loginName}">

                        <div class="mb-3">
                            <label class="form-label">New Login Name:</label>
                            <input type="text" name="loginName" class="form-control" 
                                   value="${user.loginName}" required>
                            <small class="text-muted">Leave empty for auto-generation when changing role</small>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Full Name:</label>
                            <input type="text" name="name" class="form-control" 
                                   value="${user.name}" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Password:</label>
                            <input type="password" name="password" class="form-control" 
                                   placeholder="Leave empty to keep current password">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Role <span class="required-star">*</span></label>
                            <select name="type" class="form-select" required id="roleSelect">
                                <option value="B" ${user.type.toString() == 'B' ? 'selected' : ''}>Bakery Staff</option>
                                <option value="W" ${user.type.toString() == 'W' ? 'selected' : ''}>Warehouse Staff</option>
                                <option value="S" ${user.type.toString() == 'S' ? 'selected' : ''}>Senior Management</option>
                                <option value="M" ${user.type.toString() == 'M' ? 'selected' : ''}>Manager</option>
                            </select>
                        </div>

                        <div class="mb-3 dynamic-field" id="shopField">
                            <label class="form-label">Select Shop <span class="required-star">*</span></label>
                            <select name="shopId" class="form-select">
                                <option value="">-- Select Shop --</option>
                                <c:forEach items="${shops}" var="shop">
                                    <option value="${shop.key}" ${user.shopId == shop.key ? 'selected' : ''}>
                                        ${shop.value} (ID: ${shop.key})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3 dynamic-field" id="warehouseField">
                            <label class="form-label">Select Warehouse <span class="required-star">*</span></label>
                            <select name="warehouseId" class="form-select">
                                <option value="">-- Select Warehouse --</option>
                                <c:forEach items="${warehouses}" var="warehouse">
                                    <option value="${warehouse.key}" ${user.warehouseId == warehouse.key ? 'selected' : ''}>
                                        ${warehouse.value} (ID: ${warehouse.key})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <button type="submit" class="btn btn-primary me-md-2">Save Changes</button>
                            <a href="userListServlet" class="btn btn-secondary">Cancel</a>
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
        <script>
                        // Initialize field visibility
                        function updateFieldVisibility() {
                            const role = document.getElementById('roleSelect').value;
                            const shopField = document.getElementById('shopField');
                            const warehouseField = document.getElementById('warehouseField');

                            shopField.style.display = role === 'B' ? 'block' : 'none';
                            warehouseField.style.display = role === 'W' ? 'block' : 'none';

                            shopField.querySelector('select').required = role === 'B';
                            warehouseField.querySelector('select').required = role === 'W';
                        }

                        // Form validation
                        function validateForm() {
                            const role = document.getElementById('roleSelect').value;
                            let isValid = true;

                            if (role === 'B') {
                                const shopSelect = document.querySelector('#shopField select');
                                if (shopSelect.value === '') {
                                    alert('Please select a shop');
                                    isValid = false;
                                }
                            }

                            if (role === 'W') {
                                const warehouseSelect = document.querySelector('#warehouseField select');
                                if (warehouseSelect.value === '') {
                                    alert('Please select a warehouse');
                                    isValid = false;
                                }
                            }

                            return isValid;
                        }

                        // Event listeners
                        document.getElementById('roleSelect').addEventListener('change', updateFieldVisibility);
                        document.addEventListener('DOMContentLoaded', updateFieldVisibility);
        </script>
    </body>
</html>