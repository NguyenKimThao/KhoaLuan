<div  ng-show="!prevProcessTab">
    <table class="table table-striped table-bordered table-hover">
        <thead> 
            <tr>
                <th></th>
                <th>Deployment Name</th>
                <th>Deployment Tenant Id</th>
                <th>Source</th>
                <th>Time</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <tr ng-repeat="deployment in workspace.listDeployment">
                <td style="width: 15px">{{$index + 1}}</td>
                <td> {{deployment.name}}</td>
                <td> {{deployment.tenantId}} </td>
                <td> {{deployment.source}}</td>
                <td> {{deployment.deploymentTime| date:'dd/MM/yyyy HH:mm:ss'}}</td>
                <td>
                    <div class="btn-toolbar">
                        <div role="group" class="btn-group">
                            <!-- button designs -->
                            <button class="btn btn-default templatemo-blue-button" type="button"
                                    ng-click="RedeloyDeyloyment(deployment.id)">
                                <i class="glyphicon glyphicon-pencil"></i>
                            </button>
                        </div>
                        <div role="group" class="btn-group">
                            <!-- button review -->
                            <button class="btn btn-default templatemo-blue-button" type="button"
                                    ng-click="ViewResourceDeyloyment(deployment.id)">
                                <i class="glyphicon glyphicon-film"></i></button>
                            </button>
                        </div>
                        <div class="btn-group" role="group">
                            <!-- button delete -->
                            <button class="btn btn-default templatemo-blue-button" type="button"
                                    ng-click="deleteDeyloyment(deployment.id)">
                                <i class="glyphicon glyphicon-minus" style="color:red"></i>
                            </button>
                        </div>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
</div>
<div ng-show="prevProcessTab">
    <table class="table table-striped table-bordered table-hover">
        <thead> 
            <tr>
                <th>Name Resource</th>
                <th>View Resource</th>
            </tr>
        </thead>
        <tbody>
            <tr ng-repeat="resources in listResourceDeployment">
                <td> {{resources.name}}</td>
                <td ng-if="$index == 0">dasj;ldjsa</td>
            </tr>
        </tbody>
    </table>

</div>