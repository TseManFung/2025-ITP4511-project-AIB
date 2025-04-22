
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Acer International Bakery</title>

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
        <form method="post" id = "LoginForm">
            <div class="card-container">
                <span></span>
                <h1>Login</h1>
                <div class="ipt-box">
                    <input type="text" id="account" name = "LoginName" placeholder="account" autocomplete="off">
                </div>
                <div class="ipt-box">
                    <input type="password" id="password" name = "Password" placeholder="password" autocomplete="off">
                    <i class="fa-regular fa-eye-slash" for="password" style></i>
                    <div class="beam" for="password"></div>
                </div>
                <p>if you do not have a account, please <a href="./registrar.html" class="link-underline-primary link-offset-2" id="other">sign up</a></p>
                <button type="button" class="btn-login">login</button>
                <span></span>
            </div></form>
    </body>
</body>
</html>
