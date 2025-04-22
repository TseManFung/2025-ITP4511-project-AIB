<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/components" %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="content-type" content="text/html; charset=utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
  <title>page name</title>

  <!-- css -->
  <link rel="icon" href="${pageContext.request.contextPath}/images/common/logo-icon.png">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.5.2/css/all.css" crossorigin="anonymous">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
  <!-- /css -->

  <!-- js -->
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/common.js"></script>
  <!-- /js -->
</head>

<body>
  <component:modal />
  
  <component:navbar />


  <!-- header -->
  <div style="height: calc(0lvh + 128px)" id="header"></div>
  <!-- /header -->

  <!-- content -->
  <div class="d-flex position-relative content-bg justify-content-center">
    <div class="container">
      
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
</body>
</html>
