<?php
function ajax_plugin_call_callback(){
    if (isset($_REQUEST['actionCamunda'])) {
        global $wpdb;
        $process =  WP_Processdefinition::getProcessdefinition($_REQUEST["definitionID"]);

        switch ($_REQUEST['actionCamunda']) {
            case "start":
                header('Content-Type: application/json');
                $urlApiExtend = $process->hostworkspace . "api-extend/workspace/" .$process->idworkspace."/wordpress/".$process->idwordpress;
                $url=$urlApiExtend. "/process-definition/". $process->idprocessdefinition ."/start";
                $response = wp_remote_post($url, array('body' => $process));

                if ( is_wp_error( $response ) ) {
                } else {
                    echo json_encode($response['body']);
                }
                break;
            case "loadTask":
                    header('Content-Type: application/json');
                    $url=$process->hostworkspace . "api/task/processInstance/".$_POST["processInstanceId"];
                    $response = wp_remote_get($url);

                    if ( is_wp_error( $response ) ) {
                    } else {
                        echo json_encode($response['body']);
                    }
                break;
            case "loadTaskForm":
                $task=  WP_Task::getOneTask($_REQUEST["processID"],$_REQUEST["taskDefinitionKey"]);
                if($task!=null)
                {
                    echo $task->post_content;
                    break;
                }
                $url=$process->hostworkspace . "api/task/".$_REQUEST["taskID"]."/form";

                $response = wp_remote_get($url);

                if ( is_wp_error( $response ) ) {
                } else {
                    echo $response['body'];
                    $post_content = str_replace('\\"','"',$response['body']);
                    $post_content = str_replace("\'","'",$response['body']);
                    $post_content= htmlspecialchars_decode($post_content);
                    WP_Task::insertTask($_REQUEST['processID'],$_REQUEST['taskDefinitionKey'],$_REQUEST['taskName'],$post_content);
                }
                break;
            case "loginSuccess":
                $_SESSION['processInstanceId']=$_REQUEST["processInstanceId"];
                $urlCamunda=$_SERVER['REQUEST_URI'];
                $url = $process->hostworkspace. "/api/engine/default/history/variable-instance?processInstanceId=".$_REQUEST["processInstanceId"];
                $response = wp_remote_get($url);
                if ( is_wp_error( $response ) ) {
                } else {
                    $_SESSION['dataLogin']= $response['body'];
                    $_SESSION['isLogin']=true;
                    echo '{"isLogin": true}';
                }
                break;
            case "apiCamunda":
                $urlCamunda=$_SERVER['REQUEST_URI'];
                $index=strpos($urlCamunda,"&url=/api/");
                $urlCamunda=substr($urlCamunda,$index+6);
                $index=strpos($urlCamunda,"&");
                if(isset($index)&&$index)
                    $urlCamunda[$index]="?";
                $url=$process->hostworkspace . $urlCamunda;
                
                $response=null;
                switch($_SERVER['REQUEST_METHOD']){
                    case "GET":
                        $response = wp_remote_get($url);
                        echo $response['body'];
                        break;
                    case "POST":
                        $response = wp_remote_post($url, array('body' => file_get_contents("php://input"),
                                'headers' => array(
                                    'Content-Type' => $_SERVER["CONTENT_TYPE"],
                                )));
                                if ( is_wp_error( $response ) ) {
                                } else {
                                    echo json_encode($response['body']);
                                }
                            break;
                }
                
        }
    }
    die;
 
 }



 // define the pre_handle_404 callback 
function filter_pre_handle_404( $handle_404, $wp_query ) { 
    $name=$wp_query->query['name'];
    global $post;
    if(substr($name,0,8)==='process_'){
        $processDef= WP_Processdefinition::getProcessdefinition(substr($name,8));
        if(isset($processDef->content)&&$processDef->content!='all'&&$processDef->content!="''")
        {
            if($_SESSION['isLogin'])
                return $handle_404;
            $option_login= get_option('process_login');
            if(isset($option_login))
            {
                $post_login = get_post( $option_login);
                $redirect_url=$post_login->guid;
                wp_safe_redirect( $redirect_url, 301 );
                exit();
            }
            $wp_query->set_404();
            status_header( 404 );
            nocache_headers();
            return true;
        }
        return $handle_404;
        
    };
    return $handle_404; 
}; 

function register_my_session()
{
  if( !session_id() )
  {
    session_start();
  }
}

function add_post_type_is_process( $wp_query ) {
    $name=$wp_query->query['name'];
    if(substr($name,0,8)==='process_'){
        $wp_query->query_vars['post_type']='process';
        wp_enqueue_script('homeController');
        wp_enqueue_script('camundabpmsdk');
        wp_enqueue_script('angularanimate');

    };
}
function my_js_variables(){
    global $post;
    $name=$post->post_name;
    if(substr($name,0,8)==='process_'){
    ?>
    <script type="text/javascript">
        var form_data={};
        var form_camunda_client={};
        var ajaxurl = '<?php echo admin_url( "admin-ajax.php" ); ?>';
        var ajaxnonce = '<?php echo wp_create_nonce( "itr_ajax_nonce" ); ?>';
        var url_camunda_client ='action=ajax_plugin_call&actionCamunda=apiCamunda&url=/api/';
        var isLogin=false;
        <?php
            $option_login= get_option('process_login');
            if(isset($option_login)&&$option_login==$post->ID)
            {
                session_destroy ();
                echo "isLogin = true;";
            }
            ?>
                
        form_data['processID']=<?php echo $post->ID ;?>;
        form_data['action']='ajax_plugin_call'; 
        form_data['actionCamunda']='start';
        form_camunda_client['action']='ajax_plugin_call'; 

    </script><?php
    }
}

    add_action("wp_ajax_ajax_plugin_call", "ajax_plugin_call_callback"); 

    add_action("wp_ajax_nopriv_ajax_plugin_call", "ajax_plugin_call_callback");

        // add the filter 
    add_action( 'pre_handle_404', 'filter_pre_handle_404',10,2 );
    add_action( 'pre_get_posts', 'add_post_type_is_process' );
    add_action ( 'wp_head', 'my_js_variables' )

?>
