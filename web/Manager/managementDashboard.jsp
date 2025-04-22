<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Management Dashboard</title>
        <!-- 添加 Bootstrap CSS 确保按钮样式正常 -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <div class="container mt-5">
            <h1>Hi This is Management Dashboard</h1>
            
            <!-- 显示当前用户信息 -->
            <div class="alert alert-info mt-3">
                <p>Logged in as: <strong>${userName}</strong></p>
                <p>User type: <strong>${userType}</strong></p>
            </div>
            
            <!-- Logout 按钮 -->
            <form action="../loginServlet" method="POST">
                <input type="hidden" name="action" value="logout">
                <button type="submit" class="btn btn-danger">Logout</button>
            </form>
        </div>
        
        <!-- 添加 Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>