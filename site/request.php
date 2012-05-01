<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="keywords" content="augmented reality, wearable computer, wearable computing, дополненная реальность, носимый компьютер">
	<link rel="icon" type="image/png" href="argo-favicon.png" />
	<title>AR-Go - навигатор дополненной реальности</title>

	<script type="text/javascript">
		var _gaq = _gaq || [];
		_gaq.push(['_setAccount', 'UA-30498770-1']);
		_gaq.push(['_trackPageview']);

		(function() {
			var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
			ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
			var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		})();
	</script>

	<style type="text/css">
		.mainMenu a{
			background-color: #EEEEEE;
			color: #888888;
			font-size: small;
			font-family: arial;
			padding: 1px 1em;
		}
		.mainMenu a:hover{
			background-color: #AAEEEE;
		}
		.toMain{
			position:absolute;
			top:0px;
			left:0px;
		}
	</style>
</head>

<?php
$name=$_REQUEST["name"];
$email=$_REQUEST["email"];
$jabber=$_REQUEST["jabber"];
$comments=$_REQUEST["comments"];

$requests=fopen("requests.csv", "a");

if(isset($name)&&($name!="")) fwrite($requests, "\"$name\";"); else fwrite($requests, ";");
if(isset($email)&&($email!="")) fwrite($requests, "\"$email\";"); else fwrite($requests, ";");
if(isset($jabber)&&($jabber!="")) fwrite($requests, "\"$jabber\";"); else fwrite($requests, ";");
if(isset($comments)&&($comments!="")) fwrite($requests, "\"$comments\"");
fwrite($requests, "\n");

fclose($requests);
?>


<body>
<div class="mainMenu" style="text-align:center;">
	<a href="about_ru.html">что такое AR-Go</a>
	<a href="order_ru.html">предзаказ</a>
	<a href="blog_ru.html">блог</a>
	<a href="publications_ru.html">СМИ о нас</a>
	<a href="materials_ru.html">материалы для СМИ</a>
	<a href="contacts_ru.html">контакты</a>
</div>
<a href="index_ru.html" class="toMain"><img src="argo-logo-small.png"></a>

<div style="height: 5%;"><!-- VERTICAL SPACE --></div>

<h1 style="text-align:center;">Ваша заявка добавлена в очередь!</h1>

</body>
</html>
