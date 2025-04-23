<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Add New User</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .dynamic-field { display: none; }
        .required-star { color: red; }
    </style>
</head>
<body>
    <div class="container mt-5">
        <h1>Add New User</h1>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="addUserServlet" method="POST" onsubmit="return validateForm()">
            <div class="mb-3">
                <label class="form-label">Login Name:</label>
                <input type="text" name="loginName" class="form-control">
                <small class="text-muted">Leave empty for auto-generation</small>
            </div>

            <div class="mb-3">
                <label class="form-label">Full Name <span class="required-star">*</span></label>
                <input type="text" name="name" class="form-control" required>
            </div>

            <div class="mb-3">
                <label class="form-label">Password <span class="required-star">*</span></label>
                <input type="password" name="password" class="form-control" required>
            </div>

            <div class="mb-3">
                <label class="form-label">Role <span class="required-star">*</span></label>
                <select name="type" class="form-select" required id="roleSelect">
                    <option value="">-- Select Role --</option>
                    <option value="B">Bakery Staff</option>
                    <option value="W">Warehouse Staff</option>
                    <option value="S">Senior Management</option>
                    <option value="M">Manager</option>
                </select>
            </div>

            <div class="mb-3 dynamic-field" id="shopField">
                <label class="form-label">Select Shop <span class="required-star">*</span></label>
                <select name="shopId" class="form-select">
                    <option value="">-- Select Shop --</option>
                    <c:forEach items="${shops}" var="shop">
                        <option value="${shop.key}">${shop.value} (ID: ${shop.key})</option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3 dynamic-field" id="warehouseField">
                <label class="form-label">Select Warehouse <span class="required-star">*</span></label>
                <select name="warehouseId" class="form-select">
                    <option value="">-- Select Warehouse --</option>
                    <c:forEach items="${warehouses}" var="warehouse">
                        <option value="${warehouse.key}">${warehouse.value} (ID: ${warehouse.key})</option>
                    </c:forEach>
                </select>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                <button type="submit" class="btn btn-primary me-md-2">Create User</button>
                <a href="userListServlet" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>

    <script>
        function updateFieldVisibility() {
            const role = document.getElementById('roleSelect').value;
            const shopField = document.getElementById('shopField');
            const warehouseField = document.getElementById('warehouseField');
            
            shopField.style.display = role === 'B' ? 'block' : 'none';
            warehouseField.style.display = role === 'W' ? 'block' : 'none';
            
            shopField.querySelector('select').required = role === 'B';
            warehouseField.querySelector('select').required = role === 'W';
        }

        function validateForm() {
            const role = document.getElementById('roleSelect').value;
            let isValid = true;

            if (role === 'B') {
                const shopSelect = document.querySelector('#shopField select');
                if (shopSelect.value === '') {
                    alert('Please select a shop for Bakery Staff');
                    isValid = false;
                }
            }

            if (role === 'W') {
                const warehouseSelect = document.querySelector('#warehouseField select');
                if (warehouseSelect.value === '') {
                    alert('Please select a warehouse for Warehouse Staff');
                    isValid = false;
                }
            }

            return isValid;
        }

        document.getElementById('roleSelect').addEventListener('change', updateFieldVisibility);
        document.addEventListener('DOMContentLoaded', updateFieldVisibility);
    </script>
</body>
</html>