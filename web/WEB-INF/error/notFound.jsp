<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>404 - Page Not Found</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="card shadow">
            <div class="card-body text-center">
                <h1 class="text-danger">⚠️ 404 - Page Not Found</h1>
                <p class="lead">The requested resource could not be found.</p>
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Return to Home</a>
            </div>
        </div>
    </div>
</body>
</html>