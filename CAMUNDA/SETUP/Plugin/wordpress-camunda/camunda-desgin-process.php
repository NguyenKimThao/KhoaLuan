<?php 

    function camunda_desgin_process(){   


    $locations_screen = ( isset( $_GET['action'] ) && 'locations' == $_GET['action'] ) ? true : false;
    $process_login_id = isset( $_POST['process_login_id'] ) ? $_POST['process_login_id']  : -1;

    if(!$locations_screen)
        $locations_screen = ( isset( $_POST['actionType'] ) && 'locations' == $_POST['actionType'] ) ? true : false;
    if (isset($_GET['action'])) {

        switch ($_GET['action']) {

            case "editform" :
                $data = array();
                wp_update_post( array( 'ID' => $_POST['process'], 'post_content' => $_POST['data'] ) );
                // We send the response
                echo json_encode($data);
                exit();
            case "updateFormTask":
                if (isset($_GET['process']) && isset($_GET['idTask']) &&isset($_POST['post_content']) )
                {
                    $post_content = str_replace('\\"','"',trim($_POST['post_content']));
                    $post_content = str_replace("\'","'",$post_content);
                    $post_content= htmlspecialchars_decode($post_content);
                    $res=WP_Task::updateFormTask($_GET['process'],$_GET['idTask'],$post_content);
                    $data = array('result'=>$res);
                    echo json_encode($data);
                }
                exit();
            case "createTask":
                if (isset($_GET['process']) && isset($_POST['idTask']) &&isset($_POST['nameTask']) )
                    WP_Task::createTask($_GET['process'],$_POST['idTask'],$_POST['nameTask']);
                break;
            case "updateTask":
                if (isset($_GET['process']) && isset($_POST['idTaskOld']) && isset($_POST['idTask']) &&isset($_POST['nameTaskNew']) )
                    WP_Task::updateTask($_GET['process'],$_POST['idTaskOld'],$_POST['idTask'],$_POST['nameTaskNew']);
                break;
            case "deleteTask":
                    if (isset($_GET['process']) && isset($_POST['idTask']) )
                    WP_Task::deleteTask($_GET['process'],$_POST['idTask']);
                break;
            case "updateProcessLogin":
                if (isset($process_login_id) && $process_login_id!=-1 )
                    WP_Processdefinition::updateProcessLogin( $process_login_id);
                break;
            case "updateProcessContent":
                if (isset( $_POST['idprocessdefinition'] )&& isset( $_POST['process_content'] )  )
                    WP_Processdefinition::updateProcessContent( $_POST['idprocessdefinition'],$_POST['process_content'] );
                break;
        }
 
    }


	wp_enqueue_style('vendor');
	wp_enqueue_style('body');

    wp_enqueue_script('desginer');
    
    $processes=wp_get_processes();
    $process_selected_id=getProcessSelectID($processes);
    $processIndex=getProcessIndex($processes,$process_selected_id);
    $countTask=getCountTask($processes,$processIndex);
    $task_selected_id=getTaskSelectID($processes,$processIndex);
    $task_selected=getTaskSelect($processes,$processIndex,$task_selected_id);
    $process_login=getProcessLogin($processes);
    $process_login_id=$process_login->process->ID;
    $locations = get_registered_nav_menus();
    $menu_locations = get_nav_menu_locations();
    $num_locations = count( array_keys( $locations ) );
    $nav_menus = wp_get_nav_menus();
    $menu_count = count( $nav_menus );
    $messages = array();
   
    ?>
    <div class="wrap">
	<h1 class="wp-heading-inline"><?php echo esc_html( __( 'Process' ) ); ?></h1>
    
    <?php
	if ( current_user_can( 'customize' ) ) :
		$focus = $locations_screen ? array( 'section' => 'menu_locations' ) : array( 'panel' => 'nav_menus' );
		printf(
			' <a class="page-title-action hide-if-no-customize" href="%1$s">%2$s</a>',
			esc_url( add_query_arg( array(
				array( 'autofocus' => $focus ),
				'return' => urlencode( remove_query_arg( wp_removable_query_args(), wp_unslash( $_SERVER['REQUEST_URI'] ) ) ),
			), admin_url( 'customize.php' ) ) ),
			__( 'Manage with Live Preview' )
		);
	endif;
	?>
	<hr class="wp-header-end">
	<h2 class="nav-tab-wrapper wp-clearfix">
		<a href="<?php echo admin_url( 'admin.php' ) .'?page=wordpress-camunda'; ?>" class="nav-tab<?php if ( ! isset( $_GET['action'] ) || isset( $_GET['action'] ) && 'locations' != $_GET['action'] ) echo ' nav-tab-active'; ?>"><?php esc_html_e( 'Chỉnh sửa Process' ); ?></a>
        <?php if ( $num_locations && $menu_count ) : ?>
			<a href="<?php echo esc_url( add_query_arg( array( 'action' => 'locations' ), admin_url( 'admin.php' ) .'?page=wordpress-camunda' ) ); ?>" class="nav-tab<?php if ( $locations_screen ) echo ' nav-tab-active'; ?>"><?php esc_html_e( 'Quản Lý Process' ); ?></a>
		<?php
			endif;
		?>
	</h2>

    <?php
	foreach ( $messages as $message ) :
		echo $message . "\n";
	endforeach;
	?>
    <?php
        if ( $locations_screen ):
	?>
            <div id="menu-locations-wrap">
            
            <?php if ( $process_selected_id == 0 ) : ?>
            <div class="manage-menus">
                <span class="add-edit-menu-action">
			    <?php printf( __( 'Edit your menu below, or <a href="%s">create a new menu</a>.' ), esc_url( add_query_arg( array( 'action' => 'edit', 'menu' => 0 ), admin_url( 'nav-menus.php' ) ) ) ); ?>
		        </span><!-- /add-edit-menu-action -->
            </div>
            <?php else : ?>
            <div class="manage-menus">
			    <form method="post" action="<?php echo admin_url( 'admin.php' ).'?page=wordpress-camunda&action=updateProcessLogin'; ?>">
                    <input type="hidden" name="actionType" value="locations" />
                    <label for="select-menu-to-edit" class="selected-menu"><?php _e( 'Select a process to login:' ); ?></label>
                    <select name="process_login_id" id="select-menu-to-edit">
                        <?php echo_processes($processes,$process_login_id); ?>
                    </select>
                    <span class="submit-btn"><input type="submit" class="button" value="<?php esc_attr_e( 'Select' ); ?>"></span>
                    </span>
                </form>
            </div>
            <?php echo_authorprocess($processes)?>
	        <?php endif; ?>

            </div><!-- #menu-locations-wrap -->
    <?php
	do_action( 'after_menu_locations_table' ); ?>
      <?php else: ?>

          	<div class="manage-menus">
              <?php if ( $process_selected_id == 0 ) : ?>
              <span class="add-edit-menu-action">
			<?php printf( __( 'Edit your menu below, or <a href="%s">create a new menu</a>.' ), esc_url( add_query_arg( array( 'action' => 'edit', 'menu' => 0 ), admin_url( 'nav-menus.php' ) ) ) ); ?>
		</span><!-- /add-edit-menu-action -->
        <?php else : ?>
			<form method="get" action="<?php echo admin_url( 'admin.php' ); ?>">
			<input type="hidden" name="page" value="wordpress-camunda" />
			<input type="hidden" name="action" value="edit" />
			<label for="select-menu-to-edit" class="selected-menu"><?php _e( 'Select a process to edit:' ); ?></label>
            <select name="process" id="select-menu-to-edit">
							<?php echo_processes($processes,$process_selected_id); ?>
			</select>
            <span class="submit-btn"><input type="submit" class="button" value="<?php esc_attr_e( 'Select' ); ?>"></span>
            <span class="submit-btn"><input type="button" class="button" onclick="ShowTaskDialog('modelcreateTask')" value="<?php esc_attr_e( 'Create Task' ); ?>"></span>
            <span class="submit-btn"><input type="button" class="button" onclick="PublishProcess()" value="<?php esc_attr_e( 'Publish' ); ?>"></span>
			<span class="submit-btn"><a class="button" href="<?php echo esc_url( get_permalink($process_selected_id) ); ?>"><?php esc_attr_e( 'Preview' ); ?></a>
            </span>
		</form>
           
	<?php endif; ?>
    </div>
    <?php  if ($task_selected_id!=-1 &&$countTask>0): ?>
	<div id="nav-menus-frame" class="wp-clearfix">
        <div id="menu-settings-column" class="metabox-holder">
            <div class="clear"></div>
            <form id="nav-menu-meta" class="nav-menu-meta" action="<?php echo esc_url( add_query_arg( array( 'page'=>'wordpress-camunda', 'action' => 'deleteTask', 'process' => $process_selected_id ), admin_url( 'admin.php' ) ) ); ?>" method="POST" >
                            <input class='hidden' value="<?php echo $task_selected_id ?>" name="idTask">
            <div id="side-sortables" class="accordion-container">
		                <ul class="outer-border">
                            <li class="control-section accordion-section " id="add-post-type-post">
                                <div style='width: 100%; height: 46px;'>
                                    <div class='task-title'>Tasks</div>
                                    <button type='button' class='button submit-add-to-menu right' style='margin-right: 10px; margin-top: 10px;' onclick='ShowCheckBoxTask()'>Select</button>
                                </div>
                                <div class="accordion-section-content " style="display: block;">
                                    <div class="inside">
                                        <?php  wp_nav_menu_item_task_type_meta_box($processes[$processIndex]->tasks,$task_selected_id) ?>
                                    </div>
                                    <!-- /#inside-->
                                </div>
                               
                                <!-- /#accordion-section-content -->

                            </li>
                            <!-- /#outer-border -->
                        </ul>
                        <!-- /#outer-border -->
                    </div>
                    <!-- /#side-sortables -->
            </form>
            <!-- /#nav-menu-meta -->

        </div>
        <!-- /#menu-settings-column -->

        <div id="menu-management-liquid">

        <div class="clear"></div>

            <div id="menu-management">
            <div class="form-wrap form-builder container" style="margin-left:0">
                <div class="header"></div>
                <div class="body" style="cursor: auto;">
                <div class="col-xs-3 col-sm-3 col-m-3 col-l-3 menu-components" style='min-width: 285px'>
                    <div class="cb-wrap pull-left">
                            <ul id="frmb-1547368915478-control-box" class="frmb-control ui-sortable">
                                <li class="icon-button input-control input-control-1 ui-sortable-handle" data-type="button" id="button"
                                    onclick="Show('button')"><span>Button</span></li>
                                    <li class="icon-select input-control input-control-5 ui-sortable-handle" data-type="select" id="select"
                                        onclick="Show('select')"><span>Select</span></li>
                                    <li class="icon-text input-control input-control-9 ui-sortable-handle" data-type="text" id="text"
                                        onclick="Show('input')"><span>Text Field</span></li>
                                    <li class="icon-date input-control input-control-11 ui-sortable-handle" data-type="date" id="date"
                                    onclick="Show('date')"><span>Date Field</span></li>
                                <li class="icon-paragraph input-control input-control-3 ui-sortable-handle" data-type="paragraph"
                                    id="radio-group" onclick="Show('p')"><span>Paragraph</span></li>
                                <li class="icon-radio-group input-control input-control-7 ui-sortable-handle" data-type="radio-group"
                                    id="radio-group" onclick="Show('radiogroup')"><span>Radio Group</span></li>
                                <li class="icon-checkbox-group input-control input-control-6 ui-sortable-handle" data-type="checkbox-group"
                                    id="checkbox-group" onclick="Show('checkbox')"><span>Checkbox</span></li>
                                <li class="icon-date input-control input-control-2 ui-sortable-handle" data-type="table" id="table"
                                    onclick="Show('table')"><span>Table</span></li>
                                    <li class="icon-date input-control input-control-2 ui-sortable-handle" data-type="code" id="code"
                                    onclick="Show('code')"><span>Custom code</span></li>
                            </ul>

                            <p class="button-controls wp-clearfix" style="margin-top:10px">
                                <span class="">
                                    <input type="button" class="button submit-add-to-menu left" value="Update Form" onclick="UpdateForm()" style="width:auto">
                                </span>
                                <span class="add-to-menu">
                                    <input type="button" class="button submit-add-to-menu right" value="Clear Form" onclick="ClearForm()">
                                    <span class="spinner"></span>
                                </span>
                            </p>
                    </div>
                </div>
                <div class="col-xs-9 col-sm-9 col-m-9 col-l-3 container-components" id="container-components" style='min-width: 855px'>
                        <?php echo $task_selected->post_content; ?>
                    </div>
                </div>
                <div class="modal fade item-tmp" id="modelfade" role="dialog" style="overflow: auto;">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h4 class="modal-title">Detail Components</h4>
                            </div>
                            <div class="modal-body">
                                <div class="frmb stage-wrap ui-sortable">
                                    <div class="frm-holder" data-field-id="frmb-1547363610495-fld-1" style="display: block;">
                                        <div class="form-elements" id="form-elements">
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button id="saveDialog" class="save" type="button" onclick="SaveDialog()" class="btn btn-default">Save</button>
                                <button id="updateDialog" class="save" type="button" onclick="SaveUpdateDialog()" class="btn btn-default">Update</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            </div>
            <!-- /#menu-management -->
        </div>
        <!-- /#menu-management-liquid -->
    </div>
	<?php endif; ?>
    <div id='loading-wrapper' class='loading-wrapper hide'>
        <div class='loading-dialog'>
            <button id='closeBtn' class='hide' style='font-size: 2em; position: absolute; top: 0; left: 100%; color: white; transform: translateX(-100%); padding-right: 10px; cursor: pointer; background-color:#352576;border: 0' onclick="CloseDialogLoading()">X</button>
            <div id='spinner' class="lds-hourglass"></div>
            <svg id="successAnimation" class="animated hide" xmlns="http://www.w3.org/2000/svg" width="70" height="70" viewBox="0 0 70 70">
  <path id="successAnimationResult" fill="#D8D8D8" d="M35,60 C21.1928813,60 10,48.8071187 10,35 C10,21.1928813 21.1928813,10 35,10 C48.8071187,10 60,21.1928813 60,35 C60,48.8071187 48.8071187,60 35,60 Z M23.6332378,33.2260427 L22.3667622,34.7739573 L34.1433655,44.40936 L47.776114,27.6305926 L46.223886,26.3694074 L33.8566345,41.59064 L23.6332378,33.2260427 Z"/>
  <circle id="successAnimationCircle" cx="35" cy="35" r="24" stroke="#979797" stroke-width="2" stroke-linecap="round" fill="transparent"/>
  <polyline id="successAnimationCheck" stroke="#979797" stroke-width="2" points="23 34 34 43 47 27" fill="transparent"/>
</svg>
            <div id='loading-text' class="loading-text">Loading</div>               
        </div>
    </div>
    <div class="modal fade item-tmp in" id="modelcreateTask" role="dialog" style="overflow: auto;">
        <div class="modal-dialog" style="transform: translateY(50%)">
            <div class="modal-content">
                <form action="<?php echo esc_url( add_query_arg( array( 'page'=>'wordpress-camunda', 'action' => 'createTask', 'process' => $process_selected_id ), admin_url( 'admin.php' ) ) ); ?>" method="POST">
                    <div class="modal-header">
                        <h4 class="modal-title">CreateTask</h4>
                    </div>
                    <div class="modal-body">
                        <div class="frmb stage-wrap ui-sortable">
                            <div class="frm-holder" style="display: block;">
                                <div class="form-elements" >
                                    <div class="form-group id-wrap"><label for="idTask">ID Task</label>
                                        <div class="input-wrap"><input class="fld-value form-control" id="id-wrap" value="" name="idTask" required></div>
                                    </div>
                                    <div class="form-group label-wrap"><label for="nameTask">Name Task</label>
                                        <div class="input-wrap"><input class="fld-label form-control" id="label-wrap" value="" name="nameTask" required></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button  class="save" type="button" onclick="CloseDialogTask('modelcreateTask')">Close</button>
                        <button  class="save" type="submit">Save</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="modal fade item-tmp in" id="modelupdateTask" role="dialog" style="overflow: auto;">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="<?php echo esc_url( add_query_arg( array( 'page'=>'wordpress-camunda', 'action' => 'updateTask', 'process' => $process_selected_id), admin_url( 'admin.php' ) ) ); ?>" method="POST">
                    <div class="modal-header">
                        <h4 class="modal-title">Update Task</h4>
                    </div>
                    <div class="modal-body">
                        <div class="frmb stage-wrap ui-sortable">
                            <div class="frm-holder" style="display: block;">
                                <div class="form-elements"  >
                                <div class="form-group id-wrap hidden">
                                        <div class="input-wrap"><input class="fld-value form-control" id="id-wrap-update" value="<?php echo $task_selected->idTask?>" name="idTaskOld" required></div>
                                    </div>
                                    <div class="form-group id-wrap"><label for="idTask">ID Task</label>
                                        <div class="input-wrap"><input class="fld-value form-control" id="id-wrap-update" value="<?php echo $task_selected->idTask?>" name="idTask" required></div>
                                    </div>
                                    <div class="form-group label-wrap"><label for="nameTask">Name Task</label>
                                        <div class="input-wrap"><input class="fld-label form-control" id="name-wrap-update" value="<?php echo $task_selected->nameTask?>" name="nameTaskNew" required></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button  class="save" type="button" onclick="CloseDialogTask('modelupdateTask')">Close</button>
                        <button  class="save" type="submit">Save</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <!-- /#nav-menus-frame -->
      <script>
        var $ = jQuery.noConflict();

        ShowTaskDialog  = function(str){
            $("#"+str).css("display", "block");
            $("#"+str).modal({ backdrop: true });
        }
        // $("#processchecklist-most-recent-degisner").find( "input[type=checkbox]" ).on( "click", function(){
                
        //         $("#processchecklist-most-recent-degisner").find( "input[type=checkbox]" ).prop('checked', false);
        //         $(this).prop('checked', true);
        //         var url="<?php echo admin_url( 'admin.php' ); ?>?page=wordpress-camunda&process=<?php echo $process_selected_id; ?>&idTask=" +$("#processchecklist-most-recent-degisner").find( "input[type=checkbox]:checked" ).val();
        //         window.location.href=url;
        // } );
        $("#processchecklist-most-recent-degisner").find( ".task_name" ).on( "click", function(){
                var url=`<?php echo admin_url( 'admin.php' ); ?>?page=wordpress-camunda&process=<?php echo $process_selected_id; ?>&idTask=${$(this)[0].attributes['name'].value}`;
                window.location.href=url;
        } );
     
        CloseDialogTask =function(name)
        {
            $("#"+name).modal('toggle');
        }
        CloseDialogLoading = function()
        {
            $('#loading-wrapper').addClass('hide');
            $('#spinner').removeClass('hide');
            $('#closeBtn').addClass('hide');
            $('#successAnimation').addClass('hide');
        }
        ShowCheckBoxTask = function(){
            $('.task-checkbox').prop('checked', false)
            var hide = $('.task-checkbox').attr('class').includes('hide')
            if(hide){
                $('.task-checkbox').removeClass('hide');
                hide = false;
            }
            else
                $('.task-checkbox').addClass('hide');

        }
        ClearForm = function()
        {
            var htmlParse=$("#container-components").find("form");
            htmlParse.empty();
        }
        UpdateForm= function()
        {
            var url="<?php echo admin_url( 'admin.php' ); ?>?page=wordpress-camunda&action=updateFormTask&process=<?php echo $process_selected_id; ?>&idTask=<?php echo $task_selected_id; ?>";
            var htmlData=$("#container-components").clone();
            var htmlParse=$(htmlData).find("form");
            
            htmlParse.removeClass("ng-pristine");
            htmlParse.removeClass("ng-valid");
            htmlParse.children(".form-group").removeClass("formReview");
            htmlParse.children(".form-group").removeAttr("draggable");
            htmlParse.children(".form-group").removeAttr("ondragstart");
            htmlParse.children(".form-group").removeAttr("ondragover");
            htmlParse.children(".form-group").removeAttr("ondrop");
            htmlParse.children(".form-group").find('.prev-holder').remove();
            htmlParse.children(".form-group").find('label').removeClass('type-label');
            htmlParse.children(".form-group").find(".has-error").removeClass("hidden");
            htmlParse.children(".form-group").find(".help-block").removeClass("hidden");

            var ld_wrapper = $('#loading-wrapper');
            ld_wrapper.removeClass('hide');
            var success = $('#successAnimation');
            var spinner = $('#spinner');
            var closeBtn = $('#closeBtn');
            $.ajax({
                    url: url,
                    type: 'POST',
                    data:{post_content:htmlData.html()},
                    success:function (data) {
                        spinner.addClass('hide');
                        success.removeClass('hide');
                        closeBtn.removeClass('hide');
                        $('#loading-text').html('Success')
                    },
            }).done(function() {
                spinner.addClass('hide');
                        success.removeClass('hide');
                        closeBtn.removeClass('hide');
                        $('#loading-text').html('Success')
                }).fail(function() {
                    spinner.addClass('hide');
                        success.removeClass('hide');
                        $('#loading-text').html('Fail')
                    });
           
        }

        var preview = `<div class='prev-holder'>
                    <div class='actions '>
                        <div class='field-actions' >
                            <a type='remove' class='del-button btn icon-pencil ' onclick='updateElement(this)'></a>
                            <a type='remove' class='del-button btn icon-cancel delete-confirm' onclick='deleteElement(this)'></a>
                        </div>
                    </div>
            </div>`;
        var formButtonPrev = '<div class="form-group"><label class="fb-date-label hidden"></label>';
        var formButtonNew='</div>';

        resetForm= function ()
        {
            var htmlParse=$("#container-components").find("form");
            $button = htmlParse.children('button');
            if($button&&$button.length!=0)
            {
                var bt=formButtonPrev+ "<div style='margin-right: 100px'>" +   $button[0].outerHTML+ "</div>" + formButtonNew;
                $button.replaceWith($(bt));
            }

            htmlParse.children(".form-group").addClass("formReview");
            htmlParse.children(".form-group").attr("draggable", "true");
            htmlParse.children(".form-group").attr("ondragstart", "drag(event)");
            htmlParse.children(".form-group").attr("ondragover", "allowDrop(event)");
            htmlParse.children(".form-group").attr("ondrop", "drop(event)");

            htmlParse.children(".form-group.formReview").prepend($(preview));
            htmlParse.children(".form-group.formReview").find('label').addClass('type-label');

            htmlParse.children(".form-group").find(".has-error").addClass("hidden");
            htmlParse.children(".form-group").find(".help-block").addClass("hidden");
            htmlParse.submit(function(e){
                e.preventDefault();
            });

           
        }
        
        resetForm();
      </script>                      
<?php
  endif;
?>
    </div>
    




<?php } ?>