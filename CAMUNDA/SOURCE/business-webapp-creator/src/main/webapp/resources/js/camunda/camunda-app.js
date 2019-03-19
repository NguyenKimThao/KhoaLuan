"use strict";

// declare modules
var module_authen = angular.module("Authentication", []);
var module_home = angular.module("Home", ['cam.embedded.forms', 'ngSanitize', 'ngAnimate', 'ui.bootstrap']);

var basicHttpAuthApp = angular.module("camunda-extend", ["Authentication", "Home", "ngRoute", "ngCookies"]);

basicHttpAuthApp.config(["$routeProvider", function ($routeProvider) {
        $routeProvider
                .when("/login", {
                    controller: "LoginController",
                    templateUrl: "./resources/views/login.jsp"
                })
                .when("/", {
                    controller: "HomeController",
                    templateUrl: "./resources/views/home.jsp"
                })
                .when("/deploywordpress/:workspaceID", {
                    controller: "DeployWordpressController",
                    templateUrl: "./resources/views/deploywordpress.jsp"
                })
                .when("/workspace/:workspaceID", {
                    controller: "RunProcessController",
                    templateUrl: "./resources/views/runprocess.jsp"
                })
                .when("/workspace/:workspaceID/definitionId/:definitionId", {
                    controller: "RunProcessController",
                    templateUrl: "./resources/views/runprocess.jsp"
                })
                .when("/exit", {
                    controller: "LoginController",
                    templateUrl: "./resources/views/login.jsp"
                })
    }])

basicHttpAuthApp.run(["$rootScope", "$location", "$cookieStore", "$http",
    function ($rootScope, $location, $cookieStore, $http) {
        // keep user logged in after page refresh
        $rootScope.globals = $cookieStore.get("globals") || {};
        if ($rootScope.globals.currentUser) {
            $http.defaults.headers.common["Authorization"] = "Basic " + $rootScope.globals.currentUser.authdata; // jshint ignore:line
        }

        $rootScope.$on("$locationChangeStart", function (event, next, current) {
            // redirect to login page if not logged in
            console.log($location.path());
            if ($location.path() === "" && $rootScope.globals.currentUser) {
                $location.path("/");
            } else
            if ($location.path() === "/exit" && !$rootScope.globals.currentUser) {
                $location.path("/login");
            } else
            if ($location.path() === "/login" && $rootScope.globals.currentUser) {
                $location.path("/");
            } else
            if ($location.path() !== "/login" && !$rootScope.globals.currentUser) {
                $location.path("/login");
            }
        });
    }
]);

