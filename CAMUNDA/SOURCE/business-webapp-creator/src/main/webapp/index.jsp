<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html>

    <head>
        <title>BPMN Custom Execute</title>

        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta charshet="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Visual Admin Dashboard - Home</title>
        <meta name="description" content="">
        <meta name="author" content="templatemo">
        <link href="./resources/css/font-awesome.css" rel="stylesheet">
        <link href="./resources/css/bootstrap.css" rel="stylesheet">
        <link href="./resources/css/templatemo-style.css" rel="stylesheet">
        <link href="./resources/css/main.css" rel="stylesheet">
        <link href="./resources/css/util.css" rel="stylesheet">
        <link href="./resources/css/responsive.css" rel="stylesheet">
        <link href="./resources/css/preview.css" rel="stylesheet">
        <link href="./resources/css/In_out.css" rel="stylesheet">
        <link href="./resources/css/login.css" rel="stylesheet">


    </head>

    <body ng-app="camunda-extend">
        <nav class="navbar navbar-default navbar-static-top">
            <div class="container-fluid">
                <div class="row navbar-header">
                    <a href="#" class="navbar-brand navbar-link">
                        <img width="25px " src="./resources/images/camunda.60.png" />
                        <!-- <span>Camunda Extend</span> -->
                    </a>
                    <button data-toggle="collapse" data-target="#navcol-1" class="navbar-toggle collapsed">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                </div>
                <div class="collapse navbar-collapse" id="navcol-1">
                    <ul class="nav navbar-nav navbar-right navbar-header-ul">
                        <li class="dropdown"><a data-toggle="dropdown" aria-expanded="false" href="#" class="dropdown-toggle">
                                <i class="glyphicon glyphicon-user"></i>{{globals.currentUser.username}}</a>
                            <ul role="menu" class="dropdown-menu">
                                <li role="presentation"><a href="#">My profile</a></li>
                                <li role="presentation"><a href="#/exit">Sign out</a></li>
                            </ul>
                        </li>
                        <li class="dropdown"><a data-toggle="dropdown" aria-expanded="false" href="#" class="dropdown-toggle"><i
                                    class="glyphicon glyphicon-home"></i> <span class="caret"></span></a>
                            <ul role="menu" class="dropdown-menu">
                                <li role="presentation"><a href="#">Admin</a></li>
                                <li role="presentation"><a href="#">Cokkit</a></li>
                                <li role="presentation"><a href="#/exit">Exit</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div ng-view></div>
        <footer class="text-center navbar-static-bottom">
            <p style="text-align: center">Copyright &copy; 2018</p>
            <p style="text-align: center">Khóa Luận T�?t Nghiẹp 2018-2019</p>
        </footer>
        <!-- JS -->
        <script type="text/javascript" src="./resources/js/jquery/jquery-1.11.2.min.js"></script>
        <script type="text/javascript" src="./resources/js/jquery/jquery-migrate-1.2.1.min.js"></script>
        <script type="text/javascript" src="./resources/js/vendor/templatemo-script.js"></script>
        <script type="text/javascript" src="./resources/js/vendor/bootstrap.min.js"></script>
        <script type="text/javascript" src="./resources/js/angular/angular.js"></script>
        <script type="text/javascript" src="./resources/js/angular/angular-cookies.js"></script>
        <script type="text/javascript" src="./resources/js/angular/angular-route.js"></script>
        <script type="text/javascript" src="./resources/js/angular/angular-animate.js"></script>
        <script type="text/javascript" src="./resources/js/angular/angular-sanitize.js"></script>
        <script type="text/javascript" src="./resources/js/angular/ui-bootstrap-tpls-0.13.4.js"></script>
        <script type="text/javascript" src="./resources/js/camunda/camunda-app.js"></script>
        <script type="text/javascript" src="./resources/js/camunda/camunda-bpm-sdk-angular.js"></script>
        <script type="text/javascript" src="./resources/js/camunda/authentication_service.js"></script>
        <script type="text/javascript" src="./resources/controllers/login_controller.js"></script>
        <script type="text/javascript" src="./resources/controllers/home_controller.js"></script>
        <script type="text/javascript" src="./resources/controllers/deploywordpress_controller.js"></script>
        <script type="text/javascript" src="./resources/controllers/runprocess_controller.js"></script>

    </body>

</html>