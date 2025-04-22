<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Acer International Bakery - Login</title>

        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.5.2/css/all.css" crossorigin="anonymous">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="./css/reset.css">
        <link rel="stylesheet" href="./css/login.css">
        
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
        <script src="./js/common.js"></script>
        <script src="./js/login.js"></script>
    </head>
    <body>
        <form method="POST" action="loginServlet" class="login-form">
            <input type="hidden" name="action" value="login">
            <div class="card-container">
                <%-- Display error messages if any --%>
                <% if (request.getParameter("error") != null) { %>
                    <div class="alert alert-danger">
                        <% 
                            String error = request.getParameter("error");
                            if ("invalid_credentials".equals(error)) {
                                out.print("Invalid username or password");
                            } else if ("empty_fields".equals(error)) {
                                out.print("Please fill in all fields");
                            } else if ("not_logged_in".equals(error)) {
                                out.print("Please login first");
                            } else {
                                out.print("Login failed. Please try again.");
                            }
                        %>
                    </div>
                <% } %>
                
                <%-- Display success message on logout --%>
                <% if (request.getParameter("message") != null && "logout_success".equals(request.getParameter("message"))) { %>
                    <div class="alert alert-success">
                        You have been successfully logged out.
                    </div>
                <% } %>
                
                <span></span>
                <h1>Login</h1>
                <div class="ipt-box">
                    <input type="text" id="account" name="loginName" placeholder="Username" autocomplete="username" required>
                </div>
                <div class="ipt-box">
                    <input type="password" id="password" name="password" placeholder="Password" autocomplete="current-password" required>
                    <i class="fa-regular fa-eye-slash password-toggle"></i>
                    <div class="beam"></div>
                </div>
                <button type="submit" class="btn-login">Login</button>
                <span></span>
            </div>
        </form>
    </body>
</html>