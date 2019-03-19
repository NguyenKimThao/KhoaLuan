<div ng-show="!prevProcessTab">
    <table class="table table-striped table-bordered table-hover">
        <thead> 
            <tr>
                <th></th>
                <th>Wordpress Name</th>
                <th>Wordpress Host</th>
                <th>Directory</th>
                <th>Database Host</th>
                <th>Database Name</th>
                <th>Username</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <tr ng-repeat="wordpress in workspace.listWordpress">
                <td style="width: 15px">{{$index + 1}}</td>
                <td> {{wordpress.name}}</td>
                <td> {{wordpress.wphost}}</td>
                <td> {{wordpress.directory}}</td>
                <td> {{wordpress.dbhost}}</td>
                <td> {{wordpress.dbname}}</td>
                <td> {{wordpress.uname}}</td>

                <td>
                    <div class="btn-toolbar">
                        <div role="group" class="btn-group">
                            <!-- button designs -->
                            <button class="btn btn-default templatemo-blue-button" type="button"
                                    ng-click="GotoAdminWordpress(wordpress)">
                                <i class="glyphicon glyphicon-pencil"></i>
                            </button>
                        </div>
                        <div role="group" class="btn-group">
                            <!-- button review -->
                            <button class="btn btn-default templatemo-blue-button" type="button" ng-click="GotoWordpress(wordpress)">
                                <i class="glyphicon glyphicon-film"></i></button>
                            </button>
                        </div>
                        <div class="btn-group" role="group">
                            <!-- button delete -->
                            <button class="btn btn-default templatemo-blue-button" type="button"
                                    ng-click="deleteWordpress(wordpress.id)">
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
    <section class="column column-left deployments">
        <ul>
            <li class="resource clickable" ng-repeat="resource in listResourceDeployment">
                <h4>
                    <a href="" >{{resource.name}}</a>
                </h4>
            </li>

        </ul>
    </section>

    <section class="column column-right deployments">
    </section>
</div>