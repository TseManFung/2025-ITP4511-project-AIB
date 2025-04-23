
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">


<!-- css -->
<link rel="icon" href="${pageContext.request.contextPath}/images/common/logo-icon.png">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
<link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.5.2/css/all.css" crossorigin="anonymous">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
      integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-table@1.24.1/dist/bootstrap-table.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css" />
<!-- /css -->

<!-- js -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/common.js"></script>
<script> document.addEventListener("DOMContentLoaded", () => {

        const header = document.querySelector("header");
        const menuIcon = document.querySelector('.menu-icon');


        const handleScroll = () => {
            header.classList.toggle("sticky", window.scrollY > 0);
        };

        handleScroll();


        window.addEventListener("scroll", handleScroll);


        menuIcon.addEventListener('click', () => {
            header.classList.toggle('active');
        });


        window.addEventListener("resize", (e) => {
            if (window.innerWidth > 768) {
                header.classList.remove('active');
            }
        });
    });
</script>
<!-- /js -->
