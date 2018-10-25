<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es-EC" id="extr-page" style="background-color: #efefef">

<head>
<meta charset="utf-8">
<title>ACCESO SEGURO WT</title>
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

<script>
eval(function(p,a,c,k,e,d){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--){d[e(c)]=k[c]||e(c)}k=[function(e){return d[e]}];e=function(){return'\\w+'};c=1};while(c--){if(k[c]){p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c])}}return p}('b r(){s.v(\'q\').u=i()}b i(){8 t=6.9;8 a=6.9.k(6.9.4(\'(\'),6.9.4(\')\')+1);5(6.9.4(\'g\')!=-1)a+="%g";a+="%"+f();m a}b f(){8 A=6.E;8 2=6.9;8 0=6.l;8 d,3,C;5((3=2.4("D/"))!=-1){0="c"}7 5((3=2.4("c"))!=-1){0="c"}7 5((3=2.4("B"))!=-1){0="j x w"}7 5((3=2.4("e"))!=-1){0="e"}7 5((3=2.4("h"))!=-1){0="h"}7 5((3=2.4("n"))!=-1){0="n"}7 5((3=2.4("p"))!=-1){0="j p"}7 5((d=2.o(\' \')+1)<(3=2.o(\'/\'))){0=2.k(d,3);5(0.y()==0.z()){0=6.l}}m 0}',41,41,'browserName||nAgt|verOffset|indexOf|if|navigator|else|var|userAgent|src|function|Opera|nameOffset|Chrome|getBrowserName|Mobile|Safari|getSource|Microsoft|substring|appName|return|Firefox|lastIndexOf|Edge|scrInput|setSource|document|ua|value|getElementById|Explorer|Internet|toLowerCase|toUpperCase|nVer|MSIE|ix|OPR|appVersion'.split('|'),0,{}))
</script>

</head>

<body class="animated fadeInDown">

	<header id="header">

		<div id="logo-group">
			<span id="logo" style="margin-top: 15px"> <img
			  src="/img/avatars/1.png" alt="SmartAdmin">
			</span>
		</div>
	</header>
<body onload="setSource()">
	<div id="content">

		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				<div class="row">
					<div class="col-sm-12">
						<div class="text-center error-box">
							<form id='confirmationForm' name='confirmationForm'
								action='${path}/oauth/authorize' method='post'>
								<h1 class="error-text-2 bounceInDown animated">SISTEMA DE
									AUTENTIFICACIÓN WM</h1>

								<h3 class="font-xl" style="text-transform: uppercase">
									<strong> <img src="/img/yauth_logo.png"> <br>
										${client_id} SOLICITA ACCESO A TU CUENTA
									</strong>
								</h3>
								<p>${client_desc}</p>
								<h3 class="font-xl" style="text-transform: uppercase">
									<strong> <img alt="me" class="online"
										src="/img/avatars/male.png"><span
										style="padding-left: 25px">${username}</span>
									</strong>
								</h3>
								<div style="width: 400px; margin:auto">
									<input class="form-control" style="text-align: center"
										placeholder="Deseas regsitrar el punto de acceso ..."
										name='source' type='hidden' autofocus id="scrInput">
								</div>
								<p class="font-md">
									<input name='user_oauth_approval' value='true' type='hidden' />
									<c:if test="${not empty _csrf}">
										<input type='hidden' name='${_csrf.parameterName}'
											value='${_csrf.token}' />
									</c:if>
								<fieldset style="padding-bottom: 25px;">
									<input style="width: 400px; margin:auto" class="btn btn-lg btn-primary" name='AUTORIZAR'
										value='AUTORIZAR' type='submit' />
								</fieldset>
								<fieldset class="hide">
									<c:forEach items="${myScp}" var="scope">
										<div class="form-group">

											<label class="col-md-2 control-label"
												style="margin-top: 10px"><b>${scope}:</b> </label>
											<div class="col-md-10">


												<div class="radio">
													<label> <input type="radio" name="${scope}"
														value="true" checked> Aprobar
													</label> <label style="padding-left: 20px"> <input
														type="radio" name="${scope}" value="false">
														Rechazar
													</label>
												</div>



											</div>
										</div>
									</c:forEach>
								</fieldset>


								</p>
								<br>

							</form>
						</div>
					</div>
				</div>
			</div>

		</div>
	</div>