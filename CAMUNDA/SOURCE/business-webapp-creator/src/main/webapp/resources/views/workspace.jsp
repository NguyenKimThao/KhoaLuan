<div>
    <div class="templatemo-flex-row flex-content-row">
        <div class="col-1">
            <div class="panel panel-default templatemo-content-widget white-bg no-padding templatemo-overflow-hidden"
                 >
                <div class="panel-heading templatemo-position-relative row">
                    <!-- <div class="col-md-2 col-lg-2 col-sm-2 col-xs-2">
                        <a ng-show="prevProcessTab" style="float: left; font-size: 25px" ng-click="prevTab()">
                            <i class="glyphicon glyphicon-circle-arrow-left"></i>
                        </a>
                    </div> -->
                    <div class="col-md-8 col-lg-8 col-sm-8 col-xs-8">
                        <h2 class="text-uppercase" style="text-align : center">Workspace {{workspace.workspaceName}}</h2>
                    </div>
                    <div class="col-md-4 col-lg-4 col-sm-4 col-xs-4">
                        <div class="dropdown">
                            <button class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="true"
                                    type="button"><i class="glyphicon glyphicon-asterisk"></i></button>
                            <ul role="menu" class="dropdown-menu" style="left: 42px; top: -5px">
                                <li role="presentation" ><a href="#/workspace/{{workspace.workspaceID}}" class="templatemo-white-button">Run</a></li>
                                <li role="presentation" ng-click="chooseDeployBPMN()"><a href="#" class="templatemo-white-button">Deploy
                                        BPMN</a></li>
                                <li role="presentation" ng-click="chooseDeployWordpress()"><a href="#" class="templatemo-white-button">Deploy
                                        Wordpress </a></li>
                                <li role="presentation" ng-click="chooseAddDeployment()"><a href="#" class="templatemo-white-button">Add
                                        Deployment</a></li>
                                <li role="presentation" ng-click="chooseAddWordpress()"><a href="#" class="templatemo-white-button">Add
                                        Wordpress</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div>
                    <ul class="nav nav-tabs">
                        <li ng-class="{
                                    active : flagTab == 'tabProcessDefinition'
                                }">
                            <a href="#" ng-click="changeTab('tabProcessDefinition')">Process</a>
                        </li>
                        <li ng-class="{
                                    active : flagTab == 'tabDeyloyment'
                                }">
                            <a href="#" ng-click="changeTab('tabDeyloyment')">Deployment</a>
                        </li>
                        <li ng-class="{
                                    active : flagTab == 'tabWordpress'
                                }">
                            <a href="#" ng-click="changeTab('tabWordpress')">Wordpress</a>
                        </li>
                    </ul>
                </div>
                <!-- Table Process Group -->
                <div ng-show="flagTab == 'tabProcessDefinition'" class="table-responsive" ng-include="'./resources/views/process.jsp'"></div>
                <!-- Table Deployment Group -->
                <div ng-show="flagTab == 'tabDeyloyment'" class="table-responsive" ng-include="'./resources/views/deployment.jsp'"></div>
                <!-- Table Wordpress Group -->
                <div ng-show="flagTab == 'tabWordpress'" class="table-responsive" ng-include="'./resources/views/wordpress.jsp'"></div>

            </div>
        </div>
    </div>
</div>

<div id="idmodelChooseDeploy" class="modal modelDialog">

    <form class="modal-content animate">
        <div class="imgcontainer">
            <span onclick="document.getElementById('idmodelChooseDeploy').style.display = 'none'" class="close" title="Close Modal">&times;</span>
            <!--<img src="img_avatar2.png" alt="Avatar" class="avatar">-->
            <label class="avatar">Deploy Resources</label>
        </div>

        <div class="container">
            <label for="nameworkspace"><b>Name Workspace</b></label>
            <input type="text" ng-model="workspace.workspaceName" name="nameworkspace" required readonly>
            <label for="psw"><b>Deployment Name</b></label>
            <input type="text" placeholder="Enter Deployment Name" ng-model="deploymentName" id="deploymentName" name="psw"
                   required>
            <label for="tenantid"><b>Tenant Id</b></label>
            <input type="text" placeholder="Enter Tenant Id" ng-model="tenantid" id="tenantid" name="tenantid">
            <input type="file" onchange='selectFile(event)'>
        </div>

        <div class="container">
            <button type="button" onclick="document.getElementById('idmodelChooseDeploy').style.display = 'none'" class="cancelbtn">Cancel</button>
            <button type="button" class="sucessbtn" ng-click="AddFileBPMNToWorkspace()">Create</button>
        </div>
    </form>
</div>


<div id="idmodelChooseAddDeploy" class="modal modelDialog">

    <form class="modal-content animate">
        <div class="imgcontainer">
            <span onclick="document.getElementById('idmodelChooseAddDeploy').style.display = 'none'" class="close"
                  title="Close Modal">&times;</span>
            <label class="avatar">Choose Deployment</label>
        </div>

        <div class="container">
            <div class="well" style="overflow: auto; max-height: 500px">
                <ul class="list-group checked-list-box">
                    <li class="list-group-item" style="cursor: pointer;" ng-repeat="deployment in listDeployment | orderBy : '-deploymentTime'"
                        ng-click="chooseDeployment($event, deployment)">
                        <span class="state-icon glyphicon glyphicon-unchecked"></span>
                        {{deployment.name}} (TenantID: {{deployment.tenantId}} | Source: {{deployment.source}} | Time:
                        {{deployment.deploymentTime| date:'dd/MM/yyyy HH:mm:ss'}}
                    </li>

                </ul>
            </div>
        </div>

        <div class="container">
            <button type="button" onclick="document.getElementById('idmodelChooseAddDeploy').style.display = 'none'"
                    class="cancelbtn">Cancel</button>
            <button type="button" class="sucessbtn" ng-click="AddDeploymentToWorkspace()">Create</button>
        </div>
    </form>
</div>