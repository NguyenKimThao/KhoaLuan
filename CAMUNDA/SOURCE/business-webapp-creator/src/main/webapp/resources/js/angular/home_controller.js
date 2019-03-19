"use strict";

module_home.controller("HomeController", ["$scope", "$rootScope", "$interval", function ($scope,$rootScope,$interval) {
	var camClient = new CamSDK.Client({
    mock: false,
    apiUri: '/engine-rest',
	headers : {
		Authorization: "Basic "+$rootScope.globals.currentUser.authdata
	}
	});
	var taskService = new camClient.resource('task');
	var processDefinition = new camClient.resource('process-definition');
	var processInstance = new camClient.resource('process-instance');
	var $formContainer = $('.column.right');
	
	$scope.camForm = null;
	$scope.tableView=false;
    
    function loadProcessDefinitions() {
        processDefinition.list({}, function (err, results) {
            if (err) {
                throw err;
            }
            $scope.$apply(function () {
                $scope.definitions = results.items;
            });
        });
    }
    
    function loadProcessInstance(){
    	processInstance.list({}, function (err, results) {
            if (err) {
                throw err;
            }
            $scope.$apply(function () {
                $scope.instances = results.items;
                var filtered=findIntanceByUserName($rootScope.globals.currentUser.username);
                $scope.history=filtered.length;
                $scope.instances=filtered;
                loadNewTask();
            });
        });
    }

    function loadTasks(processInstanceId) {
    	$scope.processInstanceId=processInstanceId
    	$scope.tableView=false;
        taskService.list({processInstanceId: processInstanceId}, function (err, results) {
            if (err) {
                throw err;
            }
			if(results.count==0){
				$formContainer.html('<h3>Completed!</h3>');
			}else{
              $scope.$apply(function () {
                  loadTaskForm(results._embedded.task[0].id);
                  $scope.taskName = results._embedded.task[0].name;
              });
			}
        });
    }
    
    function getTasks(done){
    	taskService.list({}, function (err, results) {
            if (err) {
                throw err;
            }
            var tasks=results._embedded.task;
            for(var i=0;i<tasks.length;i++){
            	var date=new Date(tasks[i].created);
            	tasks[i].created=date.toLocaleDateString()+" "+date.toLocaleTimeString();
            }
            results._embedded.task=tasks;
            done(results);
        });
    }

    function addFormButton(err, camForm) {
        if (err) {
            throw err;
        }
        // create a button element
        var formName = $('form').attr('name');
        var $submitBtn = $('<button type="button" ng-disabled="' + formName + '.$invalid" class="templatemo-blue-button pull-right">Complete</button>')
        // with a click handler to submit the form
            .click(function () {
                camForm.submit(function (err) {
                    if (err) {
                        throw err;
                    }
                    // clear the form
                    $formContainer.html('');
                    $scope.taskName = null;
                    loadTasks($scope.processInstanceId)
                });
            });
			camForm.on('submit-success',function(err){
				$formContainer.html('');
            $scope.taskName = null;
        	loadTasks($scope.processInstanceId);
        });
        // and append it to the form
        camForm.formElement.append($submitBtn);
    }
	
	function doneLoadForm(err, camForm){
		camForm.on('submit-success',function(err){
			$formContainer.html('');
            $scope.taskName = null;
        	loadTasks($scope.processInstanceId);
		});
	}


    function loadTaskForm(taskId) {

        // clear the form container content
        $formContainer.html('');
        // loads the task form using the task ID provided
        taskService.form(taskId, function (err, taskFormInfo) {
        	var key=taskFormInfo.key;
        	var url;
			var doneFunction;
        	if(key.includes('embedded:engine://engine/:engine')){
                var url = key.replace('embedded:engine://engine/:engine', window.location.origin+'/engine-rest');
				doneFunction = addFormButton;
            }else{
        	    var url = key.replace('embedded:app:', window.location.origin+"/"+taskFormInfo.contextPath+"/");
				doneFunction=doneLoadForm;
            }

            new CamSDK.Form({
                client: camClient,
                formUrl: url,
                taskId: taskId,
                containerElement: $formContainer,

                // continue the logic with the callback
                done: doneFunction
            });
        });
    };

    //start a process
    $scope.startProcess = function ($event) {
        //delete running instance
        if ($scope.processInstanceId) {
            processInstance.delete($scope.processInstanceId, function (err, result) {
                if (err) {
                    throw err;
                }

            })
        }
        var date=new Date();
        var dateTime=date.getDate()+"/"+date.getMonth()+1+"/"+date.getYear()+1900+" "+ date.toTimeString().slice("0:8")
        var processId = $($event.currentTarget).attr("data-process-id");
        processDefinition.start({id: processId,businessKey: $rootScope.globals.currentUser.username+" "+ dateTime}, function (err, results) {
            if (err) {
                throw err;
            }
            $scope.processInstanceId = results.id;
            loadTasks(results.id);
        });
    }
    
    $scope.showTask=function(){
    	getTasks(function(data){
    		 $scope.$apply(function () {
    		$scope.tasks=data._embedded.task.filter(function(data){
    			if($scope.instances.length>0){
    				return $scope.instances.find(function(instance){
        				return data.processInstanceId==instance.id;
        			})!=null;
    			}else{
    				return false;
    			} 
    		});
    		$scope.tableView=true;
    		 });
    	});
    }
    
    $scope.findFunctionName=function(id){
    	return $scope.definitions.find(function(data){
    		return data.id==id;
    	});
    }
    function findIntanceByUserName(username){
    	return $scope.instances.filter(function(data){
    		if(data.businessKey){
    		  return data.businessKey.search(username)>=0;
    		}
    		return false;
    	})
    }
    $scope.loadTask=loadTasks;
    $scope.deleteInstance=function(id,index){
    	processInstance.delete(id, function (err, result) {
            if (err) {
                throw err;
                return;
            }
            $scope.$apply(function(){
            	$scope.tasks.splice(index,1);
            	$scope.history--;
            });
            
        });
    }
    
    $scope.showNewTasks=function(){
    	$scope.tasks=$scope.newtasks;
    	$scope.tableView=true;
    }
    
    function loadNewTask(){
    	getTasks(function(result){
    		$scope.newtasks=result._embedded.task.filter(function(data){
    			if($scope.instances.length>0){
    				return $scope.instances.find(function(instance){
        				return data.processInstanceId==instance.id;
        			})==null;
    			}else{
    				return true;
    			}
    		});
    	});
    }
    $interval(function () {
        loadProcessInstance();
    }, 5000);
    
    //init
	loadProcessDefinitions();
	loadProcessInstance();
	
}]);
