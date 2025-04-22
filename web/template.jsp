<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="component" uri="/WEB-INF/tlds/component" %>
<!DOCTYPE html>
<html>
<jsp:include page="/WEB-INF/include/head.jsp" />

<body>
  <jsp:include page="/component/modal.jsp" />
  
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
