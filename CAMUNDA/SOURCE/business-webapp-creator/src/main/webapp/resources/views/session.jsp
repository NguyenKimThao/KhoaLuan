<div>
    <div class="templatemo-flex-row flex-content-row">
        <div class="col-1">
            <div class="panel panel-default templatemo-content-widget white-bg no-padding templatemo-overflow-hidden" style="width:100%">
                
                <div class="panel-heading templatemo-position-relative"><h2 class="text-uppercase">{{tableName}}</h2></div>
                <div class="table-responsive">
                    <table class="table table-striped table-bordered">
                        <thead>
                        <tr>
                            <th>Task Name</th>
                            <th>Process</th>
                            <th>Execute Date</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                            <tr ng-repeat="task in tasks">
                                
                                <td> <a href="/admin/product/listproduct/{{this._id}}/detail">{{task.name}}</a></td>
                                <td>{{findFunctionName(task.processDefinitionId).name}}</td>
                                <td >{{task.created}}</td>
                                
                                <td>
                                    <button type="button" class="templatemo-blue-button" ng-click="loadTask(task.processInstanceId)">Run</button>
                                </td>
                                <td>
                                    <button type="button" class="templatemo-blue-button" ng-click="deleteInstance(task.processInstanceId,$index)">Delete</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>