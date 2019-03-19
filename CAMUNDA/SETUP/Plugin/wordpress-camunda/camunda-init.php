<?php

function create_initial_process_types() {
    register_process_type( 'process', array(
		'labels' => array(
			'name_admin_bar' => _x( 'Process', 'add new from admin bar' ),
		),
		'public'  => true,
		'_builtin' => true, /* internal use only. don't use this when registering your own post type. */
		'_edit_link' => 'process.php?process=%d', /* internal use only. don't use this when registering your own post type. */
		'capability_type' => 'process',
		'map_meta_cap' => true,
		'menu_position' => 5,
		'hierarchical' => false,
		'rewrite' => false,
		'query_var' => false,
		'delete_with_user' => true,
		'supports' => array( 'title', 'editor', 'author', 'thumbnail', 'excerpt', 'trackbacks', 'custom-fields', 'comments', 'revisions', 'post-formats' ),
		'show_in_rest' => true,
		'rest_base' => 'processes',
		'rest_controller_class' => 'WP_REST_Posts_Controller',
    ) );
}

function register_process_type( $post_type, $args = array() ) {
	global $wp_post_types;

	if ( ! is_array( $wp_post_types ) ) {
		$wp_post_types = array();
	}

	// Sanitize post type name
	$post_type = sanitize_key( $post_type );

	if ( empty( $post_type ) || strlen( $post_type ) > 20 ) {
		_doing_it_wrong( __FUNCTION__, __( 'Post type names must be between 1 and 20 characters in length.' ), '4.2.0' );
		return new WP_Error( 'post_type_length_invalid', __( 'Post type names must be between 1 and 20 characters in length.' ) );
	}

	$post_type_object = new WP_Process_Type( $post_type, $args );
	$post_type_object->add_supports();
	$post_type_object->add_rewrite_rules();
	$post_type_object->register_meta_boxes();
	
	$wp_post_types[ $post_type ] = $post_type_object;

	$post_type_object->add_hooks();
	$post_type_object->register_taxonomies();

	/**
	 * Fires after a post type is registered.
	 *
	 * @since 3.3.0
	 * @since 4.6.0 Converted the `$post_type` parameter to accept a WP_Post_Type object.
	 *
	 * @param string       $post_type        Post type.
	 * @param WP_Post_Type $post_type_object Arguments used to register the post type.
	 */
	do_action( 'registered_process_type', $post_type, $post_type_object );

	return $post_type_object;
}

function get_process_type_labels( $post_type_object ) {
	$nohier_vs_hier_defaults = array(
		'name' => array( _x('Processes', 'post type general name'), _x('Pages', 'post type general name') ),
		'singular_name' => array( _x('Processes', 'post type singular name'), _x('Page', 'post type singular name') ),
		'add_new' => array( _x('Add New', 'post'), _x('Add New', 'page') ),
		'add_new_item' => array( __('Add New Post'), __('Add New Page') ),
		'edit_item' => array( __('Edit Processes'), __('Edit Page') ),
		'new_item' => array( __('New Processes'), __('New Page') ),
		'view_item' => array( __('View Processes'), __('View Page') ),
		'view_items' => array( __('View Processes'), __('View Pages') ),
		'search_items' => array( __('Search Processes'), __('Search Pages') ),
		'not_found' => array( __('No posts found.'), __('No pages found.') ),
		'not_found_in_trash' => array( __('No posts found in Trash.'), __('No pages found in Trash.') ),
		'parent_item_colon' => array( null, __('Parent Page:') ),
		'all_items' => array( __( 'All Processes' ), __( 'All Pages' ) ),
		'archives' => array( __( 'Post Archives' ), __( 'Page Archives' ) ),
		'attributes' => array( __( 'Post Attributes' ), __( 'Page Attributes' ) ),
		'insert_into_item' => array( __( 'Insert into post' ), __( 'Insert into page' ) ),
		'uploaded_to_this_item' => array( __( 'Uploaded to this post' ), __( 'Uploaded to this page' ) ),
		'featured_image' => array( _x( 'Featured Image', 'post' ), _x( 'Featured Image', 'page' ) ),
		'set_featured_image' => array( _x( 'Set featured image', 'post' ), _x( 'Set featured image', 'page' ) ),
		'remove_featured_image' => array( _x( 'Remove featured image', 'post' ), _x( 'Remove featured image', 'page' ) ),
		'use_featured_image' => array( _x( 'Use as featured image', 'post' ), _x( 'Use as featured image', 'page' ) ),
		'filter_items_list' => array( __( 'Filter posts list' ), __( 'Filter pages list' ) ),
		'items_list_navigation' => array( __( 'Posts list navigation' ), __( 'Pages list navigation' ) ),
		'items_list' => array( __( 'Posts list' ), __( 'Pages list' ) ),
	);
	$nohier_vs_hier_defaults['menu_name'] = $nohier_vs_hier_defaults['name'];

	$labels = _get_custom_object_labels( $post_type_object, $nohier_vs_hier_defaults );
	
	$post_type = $post_type_object->name;

	$default_labels = clone $labels;

	/**
	 * Filters the labels of a specific post type.
	 *
	 * The dynamic portion of the hook name, `$post_type`, refers to
	 * the post type slug.
	 *
	 * @since 3.5.0
	 *
	 * @see get_post_type_labels() for the full list of labels.
	 *
	 * @param object $labels Object with labels for the post type as member variables.
	 */
	$labels = apply_filters( "post_type_labels_{$post_type}", $labels );

	// Ensure that the filtered labels contain all required default values.
	$labels = (object) array_merge( (array) $default_labels, (array) $labels );

	return $labels;
}

function camunda_create_menu() {
	add_menu_page('Camunda Process', 'Camunda', 'manage_options', 'wordpress-camunda', 'camunda_desgin_process',plugins_url('/assets/images/camunda.ico', __FILE__), 1);
	add_submenu_page( 'wordpress-camunda', 'Desgin Process', 'Desgin Process', 'manage_options', 'camunda-desgin-process', 'camunda_desgin_process');
	add_submenu_page( 'wordpress-camunda', 'Manager Resource', 'Resources', 'manage_options', 'camunda-resource', 'camunda_setting_subpage_1');
}

?>