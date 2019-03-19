<?php
/**
 * Plugin Name: Camunda
 * Plugin URI: http://camunda.net
 * Description: Đây là plugin sử dụng camunda.
 * Version: 1.0 
 * Author: 
 * Author URI: http://camunda.com
 * License: GPLv2 or later 
 */
?>



<?php
if ( !function_exists( 'add_action' ) ) {
	echo 'Hi there!  I\'m just a plugin, not much I can do when called directly.';
	exit;
}

	define( 'CAMUNDA__PLUGIN_DIR', plugin_dir_path( __FILE__ ) );
	require_once( CAMUNDA__PLUGIN_DIR . 'camunda-init.php' );
	require_once( CAMUNDA__PLUGIN_DIR . 'camunda-class-process.php' );
	require_once( CAMUNDA__PLUGIN_DIR . 'camunda-class-process-type.php' );
	require_once( CAMUNDA__PLUGIN_DIR . 'camunda-class-task.php' );
	require_once( CAMUNDA__PLUGIN_DIR . 'camunda-class-process-definition.php' );
	require_once( CAMUNDA__PLUGIN_DIR . 'camunda-menu.php' );
	require_once( CAMUNDA__PLUGIN_DIR . 'camunda-desgin-process.php' );
	require_once( CAMUNDA__PLUGIN_DIR . 'camunda-function.php' );
	require_once( CAMUNDA__PLUGIN_DIR . 'camunda-handle.php' );





	
	
	add_action( 'init', 'create_initial_process_types' );
	add_action('init', 'register_my_session');

	add_action('admin_menu', 'camunda_create_menu');


	wp_register_style('vendor', plugin_dir_url( __FILE__ ) . 'assets/css/vendor.css');
	wp_register_style('body', plugin_dir_url( __FILE__ ) .'assets/css/body.css');
	wp_register_style('bootstrap', plugin_dir_url( __FILE__ ) .'assets/css/bootstrap.min.css');
	wp_register_script('jquerycamunda', plugin_dir_url( __FILE__ ) . 'assets/js/jquery.min.js');
	wp_register_script('bootstrapcamunda', plugin_dir_url( __FILE__ ) . 'assets/js/bootstrap.min.js');
	wp_register_script('jqueryangular', plugin_dir_url( __FILE__ ) . 'assets/js/angular.min.js');
	wp_register_script('desginer', plugin_dir_url( __FILE__ ) . 'assets/js/desginer.js');
	wp_register_script('homeController', plugin_dir_url( __FILE__ ) . 'assets/js/home-controller.js');
	wp_register_script('angularapp', plugin_dir_url( __FILE__ ) . 'assets/js/angular-app.js');
	wp_register_script('angularsanitize', plugin_dir_url( __FILE__ ) . 'assets/js/angular-sanitize.min.js');
	wp_register_script('angularanimate', plugin_dir_url( __FILE__ ) . 'assets/js/angular-animate.js');
	wp_register_script('camundabpmsdk', plugin_dir_url( __FILE__ ) . 'assets/js/camunda-bpm-sdk-angular.js');
	wp_register_script('uibootstrap', plugin_dir_url( __FILE__ ) . 'assets/js/ui-bootstrap-tpls.js');



	wp_enqueue_style('bootstrap');
    wp_enqueue_script('jquerycamunda');
	wp_enqueue_script('bootstrapcamunda');
	wp_enqueue_script('jqueryangular');
	wp_enqueue_script('angularsanitize');
	wp_enqueue_script('angularanimate');
	wp_enqueue_script('uibootstrap');
    wp_enqueue_script('angularapp');
	wp_enqueue_style('body');
	

?>