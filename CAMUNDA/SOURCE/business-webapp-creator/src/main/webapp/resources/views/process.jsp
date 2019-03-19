<table class="table table-bordered  table-hover">
    <thead> 
        <tr>
            <th></th>
            <th>Process Name</th>
            <th>Process Key</th>
            <th>Resource</th>
            <th>Version</th>
            <th>Version Tag</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <tr ng-repeat="process in workspace.listProcess">
            <td style="width: 15px">{{$index + 1}}</td>
            <td> {{process.name}}</td>
            <td> {{process.key}} </td>
            <td> {{process.resource}}</td>
            <td> {{process.version}}</td>
            <td> {{process.versionTag}}</td>
            <td>
                <div class="btn-toolbar">
                    <div role="group" class="btn-group">
                        <!-- button designs -->
                        <a class="btn btn-default templatemo-blue-button" type="button" href="#/workspace/{{workspace.workspaceID}}/definitionId/{{process.id}}">
                            <i class="glyphicon glyphicon-pencil"></i>
                        </a>
                    </div>
                    <div role="group" class="btn-group">
                        <!-- button review -->
                        <button class="btn btn-default templatemo-blue-button" type="button">
                            <i class="glyphicon glyphicon-film"></i></button>
                        </button>
                    </div>
                    <div class="btn-group" role="group">
                        <!-- button delete -->
                        <button class="btn btn-default templatemo-blue-button" type="button"
                                ng-click="deleteProcess(process.deploymentId)">
                            <i class="glyphicon glyphicon-minus" style="color:red"></i>
                        </button>
                    </div>
                </div>
            </td>
        </tr>
    </tbody>
</table>