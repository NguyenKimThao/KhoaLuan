<?php
    function wp_get_processes()
    {
        require_once( ABSPATH . 'wp-admin/admin.php' );
        require_once( ABSPATH . 'wp-admin/includes/nav-menu.php' ); 

        wp_nav_menu_setup();
        wp_initial_nav_menu_meta_boxes();
    
        global $wp_meta_boxes;
        $context='side';
        if ( empty( $screen ) )
            $screen = get_current_screen();
        elseif ( is_string( $screen ) )
            $screen = convert_to_screen( $screen );
        $page ='nav-menus';
        $first_open = false;

        if ( isset( $wp_meta_boxes[$page][ $context ] ) ) {
    
            foreach ( array( 'high', 'core', 'default', 'low' ) as $priority ) {
                if ( isset( $wp_meta_boxes[ $page ][ $context ][ $priority ] ) ) {
                    foreach ( $wp_meta_boxes[ $page ][ $context ][ $priority ] as $box ) {
                        if ( false == $box || ! $box['title'] )
                            continue;
                        if($box['args']->name=='process') {
                            return wp_nav_menu_item_process_type_meta_box($box);
                        }
                    }
                }
            }
        }
    }
    function wp_nav_menu_item_process_type_meta_box($box) {
        $post_type_name = $box['args']->name;
        $process_selected_id = isset( $_REQUEST['process'] ) ? (int) $_REQUEST['process'] : 0;
        
        // Paginate browsing for large numbers of post objects.
        $per_page = 50;
        $pagenum = isset( $_REQUEST[$post_type_name . '-tab'] ) && isset( $_REQUEST['paged'] ) ? absint( $_REQUEST['paged'] ) : 1;
        $args = array(
            'offset' => 0,
            'order' => 'ASC',
            'orderby' => 'title',
            'posts_per_page' => $per_page,
            'post_type' => $post_type_name,
            'suppress_filters' => true,
            'update_post_term_cache' => false,
            'update_post_meta_cache' => false
        );
        if ( isset( $box['args']->_default_query ) )
            $args = array_merge($args, (array) $box['args']->_default_query );
            $get_processes = new WP_Query;
        $processes = $get_processes->query( $args );
        $processFulles=array();
        foreach ( $processes as $process ) {
           $wpro= new WP_Process();
           $wpro->parse($process); 
           $processFulles[]=$wpro;
        }
        return $processFulles;
       
    }
    function getProcessSelectID($processes){
        if( isset( $_REQUEST['process'] ) )
            return  (int) $_REQUEST['process'];
        if(count($processes)==0)
            return -1;
        return $processes[0]->process->ID;
    }

    function getProcessIndex($processes,$selectID){
        if(count($processes)==0)
            return -1;
        $i=0;
        foreach($processes as $pro){
            if($pro->process->ID==$selectID)
                return $i;
            $i++;
        }
        return-1;
    }
    function getProcessLogin($processes){
        if(count($processes)==0)
            return null;
        $id= get_option('process_login');
        foreach($processes as $pro){
            if($pro->process->ID==$id)
                return $pro;
        }
        return null;
    }


    function getCountTask($processes,$processIndex){
        if(count($processes)==0||$processIndex==-1)
            return -1;
        return count($processes[$processIndex]->tasks);
    }

    function getTaskSelectID($processes,$processIndex){
        if(count($processes)==0||$processIndex==-1)
            return -1;
        if( isset( $_REQUEST['idTask'] ) )
        {
            foreach($processes[$processIndex]->tasks as $task){
                if($task->idTask==$_REQUEST['idTask'])
                    return $task->idTask;
            }
        }
        if(count($processes[$processIndex]->tasks)==0)
            return -1;
        return  $processes[$processIndex]->tasks[0]->idTask;
    }
    function getTaskSelect($processes,$processIndex,$task_selected_id){
        if(count($processes)==0||$processIndex==-1||$task_selected_id==-1)
            return null;
        foreach($processes[$processIndex]->tasks as $task){
            if($task->idTask==$task_selected_id)
                return $task;
        }
        return null;
    }
    function echo_processes($processes,$process_selected_id){
        foreach ( $processes as $ps ) {
            $process=$ps->process;
            ?>

        <option value="<?php echo esc_attr( $process->ID ); ?>" <?php selected( $process->ID, $process_selected_id ); ?>>
            <?php echo esc_html($process->post_title) ?>
            <?php echo ' ('.  esc_html($process->post_name) . ')' ;?>	
        </option>
        <?php }
    }
    function echo_authorprocess($processes){
        foreach ( $processes as $ps ) {
            echo_authorprocess_one($ps);
        }
    }
  

    function echo_authorprocess_one($pro){
        ?>
            <div class="manage-menus">
                <form method="post" action="<?php echo admin_url( 'admin.php' ).'?page=wordpress-camunda&action=updateProcessContent'; ?>">
                    <input type="hidden" name="actionType" value="locations" />
                    <label for="select-menu-to-edit" class="selected-menu"><?php echo $pro->process->post_title .'('. $pro->process->post_name. ')' ?></label>
                    <input  type="hidden"  name="idprocessdefinition" value="<?php echo $pro->workspace->id ?>" ></label>
                    <select name="process_content" id="select-menu-to-edit">
                        <option value="all" <?php selected( 'all',$pro->workspace->content); ?>>all</option>
                        <option value="author" <?php selected('author',$pro->workspace->content ); ?>>author</option>
                    </select>
                    <span class="submit-btn"><input type="submit" class="button" value="<?php esc_attr_e( 'Select' ); ?>"></span>
                    </span>
                </form>
            </div>
        <?php
    }


   function wp_nav_menu_item_task_type_meta_box($tasks,$task_selected_id)
   {
    ?>
    <div id="posttype-process" class="posttypediv">
        <div id="tabs-panel-posttype-process-most-recent" class="tabs-panel tabs-panel-active">
            <ul id="processchecklist-most-recent-degisner" class="categorychecklist form-no-clear">
                <?php foreach($tasks as $key => $task)
                    {?>
                        <li>
                            <label class="menu-item-title">
                                <input type="checkbox" class="menu-item-checkbox task-checkbox hide" value="<?php echo $task->idTask; ?>" value="select-name-task"  <?php if($task->idTask===$task_selected_id) echo 'checked' ?> >
                                <div class="task_name" name="<?php echo $task->idTask; ?>" style='display: inline-block'><?php echo $task->nameTask . ' ( '. $task->idTask .' )'; ?></div>

                            </label>    
                        </li>
                    <?php 
                    }?>

                </ul>
             <!-- /#"processchecklist-most-recent -->
        </div>
        <!-- /#"tabs-panel-posttype-process-most-recent -->
        <p class="button-controls wp-clearfix">
            <span class="">
                <input type="button" class="button submit-add-to-menu left" value="Update Task" onclick="ShowTaskDialog('modelupdateTask')">
            </span>
            <span class="add-to-menu">
                <input type="submit" class="button submit-add-to-menu right" value="Delete Task">
                <span class="spinner"></span>
            </span>
        </p>            
    </div>
        <!-- /#"posttype-process -->

    <?php
   }
    
?>