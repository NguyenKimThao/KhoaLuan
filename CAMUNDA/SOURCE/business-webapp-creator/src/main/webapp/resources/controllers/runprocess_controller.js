"use strict";

module_home.controller("RunProcessController", ["$scope", "$rootScope", "$interval", "$routeParams", "$http", function ($scope, $rootScope, $interval, $routeParams, $http) {
        var camClient = new CamSDK.Client({
            mock: false,
            apiUri: '/engine-rest',
            headers: {
                Authorization: "Basic " + $rootScope.globals.currentUser.authdata
            }
        });
        var taskService = new camClient.resource('task');
        var processDefinition = new camClient.resource('process-definition');
        var processInstance = new camClient.resource('process-instance');
        var $formContainer = $('.column.right');

        $scope.camForm = null;
        $scope.tableView = false;
        $scope.workspaceID = null;
        var isInit = false;
        function IsLogin(data) {
            if (data.type && data.type === 'error') {
                window.location.href = "#/exit";
            }
        }
        function loadProcessDefinitions() {
            if ($scope.workspaceID)
            {
                $http.get("engine/default/workspace/" + $scope.workspaceID).then(function (response) {
                    IsLogin(response.data);
                    $scope.workspace = response.data;
                    $scope.definitions = $scope.workspace.listProcess;
                    loadProcessInstanceInit();
                });
            }
        }
        function loadExec() {
            if ($routeParams.definitionId)
            {
                $scope.startProcess($routeParams.definitionId);
            }
        }
        function loadProcessInstanceInit() {
            if ($scope.workspace && $scope.definitions) {
                processInstance.list({}, function (err, results) {
                    if (err) {
                        throw err;
                    }
                    $scope.$apply(function () {
                        $scope.processinstances = results;
                        $scope.processinstances = findIntanceByUserName($rootScope.globals.currentUser.username);
                        $scope.processinstances = findIntanceByWordpress();
                        $scope.history = $scope.processinstances.length;
                        loadNewTask();
                        loadExec();
                        isInit = true;
                    });
                });
            }
        }

        function loadProcessInstance() {
            if ($scope.workspace && $scope.definitions) {
                processInstance.list({}, function (err, results) {
                    if (err) {
                        throw err;
                    }
                    $scope.$apply(function () {
                        $scope.processinstances = results;
                        $scope.processinstances = findIntanceByUserName($rootScope.globals.currentUser.username);
                        $scope.processinstances = findIntanceByWordpress();
                        $scope.history = $scope.processinstances.length;
                        loadNewTask();
                    });
                });
            }
        }

        function loadTasks(processInstanceId) {
            $scope.processInstanceId = processInstanceId
            $scope.tableView = false;
            taskService.list({processInstanceId: processInstanceId}, function (err, results) {
                if (err) {
                    throw err;
                }
                if (results.count == 0) {
                    if (!$formContainer.find('.autosubmit') || $formContainer.find('.autosubmit').length == 0)
                        $formContainer.html('<h3>Completed!</h3>');
                } else {
                    $scope.$apply(function () {
                        loadTaskForm(results._embedded.task[0].id);
                        $scope.taskName = results._embedded.task[0].name;
                    });
                }
            });
        }

        function getTasks(done) {
            taskService.list({}, function (err, results) {
                if (err) {
                    throw err;
                }
                var tasks = results._embedded.task.filter(function (data) {
                    return checkDefinitionExits(data.processDefinitionId);
                });
                for (var i = 0; i < tasks.length; i++) {
                    var date = new Date(tasks[i].created);
                    tasks[i].created = date.toLocaleDateString() + " " + date.toLocaleTimeString();
                }
                results._embedded.task = tasks;
                done(results);
            });
        }

        function doneLoadForm(err, camForm) {

            camForm.on('submit-success', function (err) {
                loadTasks($scope.processInstanceId);
            });
            if ($formContainer.find('.autosubmit') && $formContainer.find('.autosubmit').length > 0) {
                camForm.submit();
            };
        }


        function loadTaskForm(taskId) {
            $formContainer.html('');
            // loads the task form using the task ID provided
            taskService.form(taskId, function (err, taskFormInfo) {
                var key = taskFormInfo.key;
                var url = window.location.origin + "/engine-rest/task/" + taskId + "/rendered-form";
                if (key != null) {
                    if (key.includes('embedded:engine://engine/:engine')) {
                        url = key.replace('embedded:engine://engine/:engine', window.location.origin + '/engine-rest');
                    } else {
                        url = key.replace('embedded:app:', window.location.origin + "/" + taskFormInfo.contextPath + "/");
                    }
                }
                new CamSDK.Form({
                    client: camClient,
                    formUrl: url,
                    taskId: taskId,
                    containerElement: $formContainer,

                    // continue the logic with the callback
                    done: doneLoadForm
                });
            });
        }
        ;

        //start a process
        $scope.startProcess = function (processId) {
            //delete running instance
            if ($scope.processInstanceId) {
                processInstance.delete($scope.processInstanceId, function (err, result) {
                    if (err) {
                        throw err;
                    }
                })
            }
            var date = new Date();
            var dateTime = date.getDate() + "/" + date.getMonth() + 1 + "/" + date.getYear() + 1900 + " " + date.toTimeString().slice("0:8")
            processDefinition.start({id: processId, businessKey: $rootScope.globals.currentUser.username + " " + dateTime}, function (err, results) {
                if (err) {
                    throw err;
                }
                $scope.processInstanceId = results.id;
                loadTasks(results.id);
            });
        }

        $scope.showTask = function () {
            getTasks(function (data) {
                $scope.$apply(function () {
                    $scope.tasks = data._embedded.task.filter(function (data) {
                        if ($scope.processinstances.length > 0) {
                            return $scope.processinstances.find(function (instance) {
                                return data.processInstanceId == instance.id;
                            }) != null;
                        } else {
                            return false;
                        }
                    });
                    $scope.tableView = true;
                });
            });
        }

        $scope.findFunctionName = function (id) {
            return $scope.definitions.find(function (data) {
                return data.id == id;
            });
        }
        function findIntanceByUserName(username) {
            return  $scope.processinstances.filter(function (data) {
                if (data.businessKey) {
                    return data.businessKey.search(username) >= 0;
                }
                return false;
            });
        }
        function  checkDefinitionExits(definitionId)
        {
            var item = $scope.definitions.find(item => item.id === definitionId);
            if (item)
                return true;
            return false;
        }
        function  findIntanceByWordpress() {
            return  $scope.processinstances.filter(function (data) {
                return checkDefinitionExits(data.definitionId);
            });
        }

        $scope.loadTask = loadTasks;
        $scope.deleteInstance = function (id, index) {
            processInstance.delete(id, function (err, result) {
                if (err) {
                    throw err;
                    return;
                }
                $scope.$apply(function () {
                    $scope.tasks.splice(index, 1);
                    $scope.history--;
                });

            });
        }

        $scope.showNewTasks = function () {
            $scope.tasks = $scope.newtasks;
            $scope.tableView = true;
        }

        function loadNewTask() {
            getTasks(function (result) {
                $scope.newtasks = result._embedded.task.filter(function (data) {
                    if ($scope.processinstances.length > 0) {
                        return $scope.processinstances.find(function (instance) {
                            return data.processInstanceId == instance.id;
                        }) == null;
                    } else {
                        return true;
                    }
                });
            });
        }
        $interval(function () {
            if (isInit)
                loadProcessInstance();
        }, 5000);
        function  init() {
            loadInit();
            loadProcessDefinitions();
        }
        function  loadInit() {
            $scope.workspaceID = $routeParams.workspaceID;
        }

        $scope.actionForm = function (camForm, item, name, value) {

            if (name != undefined) {
                if (camForm.variableManager.variable(name) !== undefined)
                    camForm.variableManager.destroyVariable(name);
                camForm.variableManager.createVariable({
                    name: name,
                    type: 'string',
                    value: value
                });
            }


            if (item !== undefined) {
                for (var attr in item) {
                    if (camForm.variableManager.variable(attr) !== undefined)
                        camForm.variableManager.destroyVariable(attr);

                    camForm.variableManager.createVariable({
                        name: attr,
                        type: 'String',
                        value: item[attr]
                    });
                }
            }
            camForm.submit();
        }
        $scope.chooseDeployBPMN = function () {
            document.getElementById('idmodelChooseDeploy').style.display = 'block';
        }



        init();
        //init

    }]);
