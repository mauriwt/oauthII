<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es-EC" id="extr-page">

<head>
<meta charset="utf-8">
<title>ACCESO SEGURO WM</title>
<meta name="description" content="">
<meta name="author" content="">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

<!-- #CSS Links -->
<!-- Basic Styles -->
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/font-awesome.min.css">

<!-- SmartAdmin Styles : Caution! DO NOT change the order -->
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/smartadmin-production.min.css">
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/smartadmin-skins.min.css">

<!-- SmartAdmin RTL Support -->
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/smartadmin-rtl.min.css">

<!-- We recommend you use "your_style.css" to override SmartAdmin
		     specific styles this will also ensure you retrain your customization with each SmartAdmin update.-->
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/style.css">

<!-- #FAVICONS -->
<link rel="shortcut icon" href="/img/favicon/favicon.ico"
	type="image/x-icon">
<link rel="icon" href="/img/favicon/favicon.ico" type="image/x-icon">

<!-- #GOOGLE FONT -->
<link rel="stylesheet"
	href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,700italic,300,400,700">


</head>

<body class="animated fadeInDown">

	<header id="header">

		<div id="logo-group">
			
		</div>
	</header>

	<div id="main" role="main">

		<!-- MAIN CONTENT -->
		<div id="content" class="container">

			<div class="row">
				<div
					class="col-xs-12 col-sm-12 col-md-7 col-lg-8 hidden-xs hidden-sm">
					<div class="hero">
					 <form action="/login" method="POST" class="smart-form client-form">
              <header> INICIAR SESION </header>
              <fieldset>

                <section>
                  <label class="label">Usuario</label> <label class="input">
                    <i class="icon-append fa fa-user"></i> <input type="text"
                    d="username" name="username"> <b
                    class="tooltip tooltip-top-right"><i
                      class="fa fa-user txt-color-teal"></i> Usuario</b>
                  </label>
                </section>

                <section>
                  <label class="label">Password</label> <label class="input">
                    <i class="icon-append fa fa-lock"></i> <input type="password"
                    id="pwd" name="password"> <b
                    class="tooltip tooltip-top-right"><i
                      class="fa fa-lock txt-color-teal"></i> Password</b>
                  </label>
                  <!--<div class="note">
                    <a href="forgotpassword.html">Forgot password?</a>
                  </div>-->
                </section>

                <!--<section>
                  <label class="checkbox">
                      <input type="checkbox" name="remember" checked="">
                      <i></i>Stay signed in</label>
                </section>-->
              </fieldset>
              <ul id="errors">
                <c:if test="${not empty sessionScope.auth_err}">
                  <li class="error">
                    <span class="label-danger">
                    <c:out value="${sessionScope.auth_err}"/>
                    </span>
                  </li>
                </c:if>
                <c:if test="${not empty msg}">
                  <li class="error"><span class="label-info">${msg}</span></li>
                </c:if>
              </ul>
              <footer>
                <button type="submit" class="btn btn-primary">ACCEDER</button>
              </footer>
            </form>
					</div>
				</div>
				<div class="col-xs-12 col-sm-12 col-md-5 col-lg-4">
					<div class="well no-padding" style="margin-top: 40px;">
					

					</div>
					<!--
						<h5 class="text-center"> - Or sign in using -</h5>
															
							<ul class="list-inline text-center">
								<li>
									<a href="javascript:void(0);" class="btn btn-primary btn-circle"><i class="fa fa-facebook"></i></a>
								</li>
								<li>
									<a href="javascript:void(0);" class="btn btn-info btn-circle"><i class="fa fa-twitter"></i></a>
								</li>
								<li>
									<a href="javascript:void(0);" class="btn btn-warning btn-circle"><i class="fa fa-linkedin"></i></a>
								</li>
							</ul>
							-->
				</div>
			</div>
		</div>

	</div>

	<!--================================================== -->

	<!-- Link to Google CDN's jQuery + jQueryUI; fall back to local -->
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
	<script>
		if (!window.jQuery) {
			document
					.write('<script type="text/javascript" src="/js/libs/jquery-2.1.1.min.js"><\/script>');
		}
	</script>

	<!--<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
		<script> if (!window.jQuery.ui) { document.write('<script src="js/libs/jquery-ui-1.10.3.min.js"><\/script>');} </script>-->

	<!-- IMPORTANT: APP CONFIG -->
	<script type="text/javascript" src="/js/app.config.seed.js"></script>

	<!-- JS TOUCH : include this plugin for mobile drag / drop touch events 		
		<script src="js/plugin/jquery-touch/jquery.ui.touch-punch.min.js"></script> -->

	<!-- BOOTSTRAP JS -->
	<script type="text/javascript" src="/js/bootstrap/bootstrap.min.js"></script>

	<!--[if IE 8]>
			
			<h1>Your browser is out of date, please update your browser by going to www.microsoft.com/download</h1>
			
		<![endif]-->

	<!-- MAIN APP JS FILE -->
	<script type="text/javascript" src="/js/app.seed.js"></script>

</body>

</html>
