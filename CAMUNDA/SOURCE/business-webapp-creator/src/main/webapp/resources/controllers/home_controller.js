"use strict";
module_home.controller("HomeController", ["$scope", "$rootScope", "$interval", "$http", function ($scope, $rootScope, $interval, $http) {


        $scope.tableViewGroupProcess = false;
        $scope.prevProcessTab = false;
        $scope.flagTab = "tabProcessDefinition";
        $scope.workspace = null;
        var modal = document.getElementById('idmodel');
        $scope.dialog = [];
        function  LoadWorkSpaces() {
            var urlWorkspaces = "./engine/default/workspace/username/" + $rootScope.globals.currentUser.username;
            $http.get(urlWorkspaces).then(function (response) {
                IsLogin(response.data);
                $scope.workspaces = response.data;
                console.log($scope.workspaces);
                if ($scope.workspace != null)
                {
                    $scope.workspace = $scope.workspaces.filter(workspace => workspace.workspaceID == $scope.workspace.workspaceID)[0];
                }
            });
        }
        function IsLogin(data) {
            if (data.type && data.type === 'error') {
                window.location.href = "#/exit";
            }
        }

        function  LoadDeployment()
        {
            var urlWorkspaces = "./engine/default/deployment/";
            $http.get(urlWorkspaces).then(function (response) {
                IsLogin(response.data);
                $scope.listDeployment = response.data.filter(deployment =>
                    $scope.workspace.listDeployment.filter(item => item.id == deployment.id).length == 0
                );
            });
        }


        function CreateNewWorkspace() {
            $http({
                method: 'POST',
                url: 'engine/default/workspace/username/' + $rootScope.globals.currentUser.username + '/create/',
                data: $scope.nameworkspace
            }).then(function (response) {
                var data = response.data;
                IsLogin(response.data);
                if (data.type == 'sucess')
                {
                    modal.style.display = "none";
                    LoadWorkSpaces();
                } else
                    alert('Create no sucess \n Error: ' + data.message);
            }
            , function (error) {
                alert('Error in creating' + error);
            });
        }

        window.selectFile = function (event) {
            $scope.selectFile = event.target.files[0];
        }


        function CreateNewWorkspaceByWar() {

            var fd = new FormData();
            fd.append('deployment-name', 'aName');
            fd.append('enable-duplicate-filtering', false);
            fd.append('data', $scope.selectFile);
            $http({
                method: 'POST',
                url: '/engine-rest/engine/default/deployment/create',
                data: fd,
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }).then(function (response) {
                IsLogin(response.data);
                SendDeploymenResoucre(response.data.id);
            }
            , function (error) {
                alert('Error in creating' + error);
            });
        }
        $scope.AddFileBPMNToWorkspace = function () {

            var fd = new FormData();
            fd.append('deployment-name', $("#deploymentName").val());
            fd.append('tenant-id', $("#tenantid").val());
            fd.append('deployment-source', 'process application');
            fd.append('enable-duplicate-filtering', false);
            fd.append('data', $scope.selectFile);
            $http({
                method: 'POST',
                url: 'engine/default/workspace/' + $scope.workspace.workspaceID + '/deployment/',
                data: fd,
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }).then(function (response) {
                IsLogin(response.data);
                if (response.data.type = 'sucess')
                {
                    document.getElementById('idmodelChooseDeploy').style.display = 'none';
                    LoadWorkSpaces();
                }
                alert(response.data.message);

            }, function (error) {
                alert('Error in creating' + error);
            });
        }

        //start a process
        $scope.startWorkspace = function (workspace) {
            $scope.tableViewGroupProcess = true;
            $scope.workspace = workspace;
        }

        $scope.ActionWorkspace = function () {
            if ($scope.deployfile == false) {
                CreateNewWorkspace();
            } else
            {
                CreateNewWorkspaceByWar();
            }
        }
        $scope.showCreateNewWorkspace = function () {
            $scope.deployfile = false;
            document.getElementById('idmodel').style.display = 'block';
        }
        $scope.showDialog = function (id) {
            var dia = document.getElementById(id);
            var idx = $scope.dialog.indexOf(dia);
            if (idx == -1)
                $scope.dialog.push(dia);
            dia.style.display = 'block';
        }
        $scope.showDeployWarWorkspace = function () {
            $scope.deployfile = true;
            document.getElementById('idmodel').style.display = 'block';
        }
        $scope.chooseDeployBPMN = function () {
            document.getElementById('idmodelChooseDeploy').style.display = 'block';
        }
        $scope.chooseAddDeployment = function () {
            LoadDeployment();
            document.getElementById('idmodelChooseAddDeploy').style.display = 'block';
        }

        $scope.selectDeployment = [];

        $scope.chooseDeployment = function ($event, deployment) {
            var idx = $scope.selectDeployment.indexOf(deployment);
            $($event.currentTarget).toggleClass('list-group-item-primary');
            $($event.currentTarget).toggleClass('active');
            $($event.currentTarget).find('.state-icon').toggleClass('glyphicon-unchecked');
            $($event.currentTarget).find('.state-icon').toggleClass('glyphicon-check');
            console.log($($event.currentTarget).find('.state-icon'));
            // Is currently selected
            if (idx > -1) {
                $scope.selectDeployment.splice(idx, 1);
            }

            // Is newly selected
            else {
                $scope.selectDeployment.push(deployment);
            }
        }
        $scope.chooseDeployWordpress = function () {
            var ngay = new Date();
            window.open("#/deploywordpress/" + $scope.workspace.workspaceID, "_blank");
        }


        LoadWorkSpaces();
        window.onclick = function (event) {
            if (event.target == modal) {
                modal.style.display = "none";
                $scope.deployfile = false;
            }
            if (event.target == document.getElementById('idmodelChooseAddDeploy')) {
                document.getElementById('idmodelChooseAddDeploy').style.display = 'none';
            }
            if (event.target == document.getElementById('idmodelChooseDeploy')) {
                document.getElementById('idmodelChooseDeploy').style.display = 'none';
            }
            if ($scope.dialog.indexOf(event.target) > -1)
                event.target.style.display = 'none';
        }




        ///////////////////////////////Nhung thu can///////////////////////////
        $scope.prevTab = function () {
            $scope.prevProcessTab = false;
        }
        $scope.changeTab = function (tab) {
            $scope.flagTab = tab;
            $scope.prevTab();

        }
        $scope.deleteDeyloyment = function (deploymentId) {
            let result = window.confirm(`Do you want delete ${deploymentId}?`);
            if(result){
                $http({
                method: 'DELETE',
                url: 'engine/default/workspace/' + $scope.workspace.workspaceID + '/delete/deployment/' + deploymentId,
                headers: {'Content-Type': undefined}
            }).then(function (response) {
                IsLogin(response.data);
                if (response.data.type == 'sucess')
                        LoadWorkSpaces();
                else
                    alert(response.data.message);
            }, function (error) {
                alert('Error in creating' + error);
            });
            }
            
        }
        $scope.deleteLogicDeyloyment = function (deploymentId) {
            $http({
                method: 'DELETE',
                url: 'engine/default/workspace/' + $scope.workspace.workspaceID + '/deletelogic/' + deploymentId,
                headers: {'Content-Type': undefined}
            }).then(function (response) {
                IsLogin(response.data);
                if (response.data.type == 'sucess')
                    LoadWorkSpaces();
                else
                    alert(response.data.message);
            }, function (error) {
                alert('Error in creating' + error);
            });
        }

        $scope.RedeloyDeyloyment = function (deploymentId) {
            alert('Chuc nang chua lam');
        }

        $scope.ViewResourceDeyloyment = function (deploymentId) {
            console.log($scope.prevProcessTab);
            $http({
                method: 'GET',
                url: 'engine/default/deployment/' + deploymentId + '/resources',
            }).then(function (response) {
                IsLogin(response.data);
                $scope.listResourceDeployment = response.data;
                $scope.prevProcessTab = true;
            }, function (error) {
                console.log(error);
                alert('Error in creating' + error);
            });
        }
        $scope.AddDeploymentToWorkspace = function () {
            $http({
                method: 'POST',
                url: 'engine/default/workspace/' + $scope.workspace.workspaceID + '/deployment/list',
                data: $scope.selectDeployment.map(deploy => {
                    return deploy.id
                })
            }).then(function (response) {
                IsLogin(response.data);
                if (response.data.type == 'sucess')
                {
                    document.getElementById('idmodelChooseAddDeploy').style.display = 'none';
                    alert('thanhcong');
                    LoadWorkSpaces();

                } else
                    alert('Error: ' + response.data.message);
            }, function (error) {
                alert('Error when AddDeploymentToWorkspace');
                console.log(error);
            });
        }
        $scope.DeployFileWar = function () {
            var fd = new FormData();
            fd.append('data', $scope.selectFile);
            console.log($scope.selectFile);
            $http({
                method: 'POST',
                url: 'engine/default/deployment/createfilewar/' + $scope.selectFile.name,
                data: $scope.selectFile,
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            }).then(function (response) {
                IsLogin(response.data);
                if (response.data.type == 'sucess')
                {
                    document.getElementById('idmodelDeployWar').style.display = 'none';
                }
                alert(response.data.message);

            }, function (error) {
                alert('Error in creating' + error);
            });
        }

        $scope.GotoWordpress = function (wordpress) {
            window.open(wordpress.wphost + wordpress.directory, "_blank");
        }
        $scope.GotoAdminWordpress = function (wordpress) {
            window.open(wordpress.wphost + wordpress.directory + "/wp-admin/", "_blank");
        }
        $scope.deleteWordpress = function (wordpressID) {
            $http({
                method: 'DELETE',
                url: 'engine/default/workspace/' + $scope.workspace.workspaceID + '/delete/wordpress/' + wordpressID,
                headers: {'Content-Type': undefined}
            }).then(function (response) {
                IsLogin(response.data);
                if (response.data.type == 'sucess')
                    LoadWorkSpaces();
                alert(response.data.message);
            }, function (error) {
                alert('Error in creating' + error);
            });
        }
    }]);
