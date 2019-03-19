<div class="templatemo-flex-row">
    <div class="templatemo-sidebar">
        <header class="templatemo-site-header">
            <h1 style="color:blue">{{workspace.workspaceName}}</h1>
        </header>
        <div class="profile-photo-container">
            <img src="./resources/images/profile-photo.jpg" alt="Profile Photo" class="img-responsive">
            <div class="profile-photo-overlay"></div>
        </div>
        <!-- Search box -->
        <form class="templatemo-search-form" role="search">
            <div class="input-group">
                <button type="button" class="fa fa-search"></button>
                <input type="text" ng-model="searchConstrains" class="form-control" placeholder="Search" name="srch-term" id="srch-term">
            </div>
        </form>
        <div class="mobile-menu-icon">
            <i class="fa fa-bars"></i>
        </div>
        <nav class="templatemo-left-nav">
            <ul>
                <li ng-repeat="definition in definitions" ng-show="definition.name==null||definition.name.toLowerCase().search(searchConstrains.toLowerCase()) >= 0" data-process-id="{{ definition.id}}" ng-click="startProcess(definition.id)">
                    <a href=""><i class="fa fa-home fa-fw"></i>{{ (definition.name || definition.id)}}</a>
                </li>
            </ul>
        </nav>
    </div>
    <!-- Main content -->
    <div class="templatemo-content col-1 light-gray-bg">
        <div class="templatemo-top-nav-container">
            <div class="row">
                <nav class="templatemo-top-nav col-lg-12 col-md-12">
                    <ul class="text-uppercase">
                        <li><a href="" ng-click="showTask()">History<span ng-show="history != 0" class="badge">{{history}}</span></a></li>
                        <li><a href="" ng-click="showNewTasks()">Your New task<span ng-show="newtasks.length != 0" class="badge">{{newtasks.length}}</span></a></li>
                        <li><a href="./#/exit">Sign Out</a></li>
                    </ul>
                </nav>
            </div>
        </div>

        <div class="templatemo-content-container">
            <div ng-show="!tableView" class="templatemo-content-widget white-bg" style="display: inline-block; width: 100%">

                <h2 class="margin-bottom-10">{{taskName}}</h2>
                <div class="column right"></div>

            </div>
            <div ng-show="tableView" ng-include="'./resources/views/session.jsp'"></div>
        </div>
    </div>
</div>