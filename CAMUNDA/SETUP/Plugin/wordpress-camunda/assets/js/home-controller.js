"use strict";

module_home.controller('HomeController', function ($scope, $sce, $interpolate, $location) {

    var $ = jQuery.noConflict();

    $scope.processInstanceId = "";
    var $formContainer = $("#formProcess");
    form_data["definitionID"] = $formContainer.attr("data-process-id");
    var camClient = new CamSDK.Client({
        mock: false,
        apiUri: ajaxurl + "?definitionID=" + form_data["definitionID"] + "&" + url_camunda_client,
    });

    function startProcess() {
        $.ajax({
            type: "POST",
            url: ajaxurl,
            data: form_data,
            success: function (response) {
                if (!response || response == '' || response == false) {
                    alert('Không có phản hồi từ server camunda');
                    return;
                }
                var data = JSON.parse(response.trim());
                if (data.type == 'error') {
                    alert(data.message);
                } else
                    loadTasks(data.id);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown);
            }
        })
    }
    function loadTasks(processInstanceId) {
        $scope.processInstanceId = processInstanceId;
        form_data["processInstanceId"] = processInstanceId;
        form_data['actionCamunda'] = 'loadTask';
        $.ajax({
            type: "POST",
            url: ajaxurl,
            data: form_data,
            success: function (response) {
                if (response == false) {
                    alert('Không có phản hồi từ server camunda');
                    return;
                }
                var data = JSON.parse(response.trim());
                if (data.length == 0) {
                    completedTask();
                }
                else {
                    loadTaskForm(data[0]);
                }
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert(errorThrown);
            }
        })

    }
    function completedTask() {
        if (isLogin == true) {
            form_data["actionCamunda"] = 'loginSuccess';
            $.ajax({
                type: "POST",
                url: ajaxurl,
                data: form_data,
                success: function (response) {
                    if (response == false) {
                        alert('Không có phản hồi từ server camunda');
                        return;
                    }
                    var data = JSON.parse(response.trim());
                    if (data.isLogin == true) {
                        if ($location.search().url_redirect) {
                            window.location.href = $location.search().url_redirect;
                        }
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    alert(errorThrown);
                }
            })
            return;
        }
        if (!$formContainer.find('.autosubmit') || $formContainer.find('.autosubmit').length == 0) {
            $formContainer.html('<h3>Completed!</h3>');
        }
    }

    function loadTaskForm(task) {

        form_data['actionCamunda'] = 'loadTaskForm';
        form_data['taskID'] = task.id;
        form_data['taskDefinitionKey'] = task.taskDefinitionKey;
        form_data['taskName'] = task.name;

        const searchParams = new URLSearchParams();
        Object.keys(form_data).forEach(key => searchParams.append(key, form_data[key]));


        $formContainer.html('');
        new CamSDK.Form({
            client: camClient,
            formUrl: ajaxurl + "?" + searchParams.toString(),
            taskId: task.id,
            containerElement: $formContainer,
            // continue the logic with the callback
            done: doneLoadForm
        });

    };

    function doneLoadForm(err, camForm) {

        // var $submit = $formContainer.find("button[type='submit']");
        // if ($submit) {
        //     $submit.click(function () {
        //         camForm.submit(function (err) {
        //             if (err)
        //                 alert('Đã có lỗi submit');
        //         });
        //     });
        // }

        camForm.on('submit-success', function (err) {
            loadTasks($scope.processInstanceId);
        });
        if ($formContainer.find('.autosubmit') && $formContainer.find('.autosubmit').length > 0) {
            camForm.submit();
        };

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
    function init() {
        startProcess();
    }


    init();
});