"use strict";
module_home.controller("DeployWordpressController", ["$scope", "$rootScope", "$routeParams", "$interval", "$http", "$sanitize", "$sce", "$compile", "$q",
    function ($scope, $rootScope, $routeParams, $interval, $http, $sanitize, $sce, $compile, $q) {

        $scope.workspaceID = $routeParams.workspaceID;
        function IsLogin(data) {
            if (data.type && data.type === 'error') {
                window.location.href = "#/exit";
            }
        }
        function load() {
            var ngay = new Date();
            $scope.wordpressName = "wordpress" + ngay.getTime();
            $scope.dbname = $scope.workspaceID + "-" + $scope.wordpressName;
            $scope.wphost = "http://localhost:8888/";
            $scope.wpinstall = "wp-quick-install";

        }
        $('#checkboxDefault').click(function () {
            if ($(this).is(':checked')) {
                // Do stuff
                $("#formDefault").removeClass('hidden');

            } else
                $("#formDefault").addClass('hidden');

        });
        $("#formsubmit").submit(function (event) {
            var data = $('#formsubmit').serializeArray().reduce(function (obj, item) {
                obj[item.name] = item.value;
                return obj;
            }, {});

            document.getElementById('waitDeploy').style.display = 'block';
            $("#submit").attr("disabled", "disabled");
            $http({
                method: 'POST',
                url: 'engine/default/workspace/' + $scope.workspaceID + '/deployment/wordpress',
                data: data
            }).then(function (response) {
                IsLogin(response.data);
                if (response.data.type == 'sucess') {
                    $("#success").removeClass('hidden');
                    $("#idsubmit").html("");
                    $scope.urlAdmin = response.data.message + "/wp-login.php/";
                    $scope.urlWebsite = response.data.message + "/";
                    document.getElementById('waitDeploy').style.display = 'none';

                } else
                {
                    alert('Error in deployment wordpress \n' + response.data.message);
                }
            }
            , function (error) {
                alert('Error in deployment wordpress');
                console.log(error);
            });


            event.preventDefault();
        });

        function loadWorkspace() {
            $http.get("engine/default/workspace/" + $scope.workspaceID).then(function (response) {
                IsLogin(response.data);
                $scope.workspace = response.data;
                console.log($scope.workspace);
            });
        }
        function  init() {
            load();
            loadWorkspace();
        }
        init();
    }]);
