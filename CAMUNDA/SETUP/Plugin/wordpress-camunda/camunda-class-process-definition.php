<?php

final class WP_Processdefinition  {
	
	public $id;
	public $idprocessdefinition;
    public $idworkspace;
    public $hostworkspace;
    public $content;
    public $idwordpress;

    
    public function parse($args){
        $this->id=$args->id;
        $this->idprocessdefinition=$args->idprocessdefinition;
        $this->idworkspace=$args->idworkspace;
        $this->hostworkspace=$args->hostworkspace;
        $this->content=$args->content;
        $this->idwordpress=$args->idwordpress;
    }
    public function __construct( $args ) {
        $this->parse($args);
	}
	public static function getProcessdefinition($ID){
		global $wpdb;
        $query="SELECT * FROM {$wpdb->prefix}processdefinition	WHERE id = {$ID}";
        $processDefinitionQuery = $wpdb->get_results($query);
        foreach($processDefinitionQuery  as $key => $row)
        {
            return new WP_Processdefinition($row);
        }
        return null;
    }
    public static function getProcessdefinitions(){
		global $wpdb;
        $query="SELECT * FROM {$wpdb->prefix}processdefinition";
        $processDefinitionQuery = $wpdb->get_results($query);
        $processDefinition=[];
        foreach($processDefinitionQuery  as $key => $row)
        {
            $processDefinition[$row->id] = new WP_Processdefinition($row);
        }
        return $processDefinition;
    }
    public static function getProcessdefinitionLogin(){
		global $wpdb;
        $query="SELECT * FROM {$wpdb->prefix}processdefinition WHERE content='login'";
        $processDefinitionQuery = $wpdb->get_results($query);
        $processDefinition=[];
        foreach($processDefinitionQuery  as $key => $row)
        {
           return  new WP_Processdefinition($row);
        }
        return null;
    }
    public static function updateProcessLogin($idprocess){
        if(get_option('process_login')){
            update_option('process_login', $idprocess);
       }
       else {
         add_option('process_login', $idprocess);
       }
    }   
    public static function updateProcessContent($idprocessdefinition,$content){
        global $wpdb;
        $data=array(
            'content' => $content,
        );
        $where =array(
            "id"=>$idprocessdefinition
        );
        $wpdb->update($wpdb->prefix.'processdefinition',$data, $where);
    }   
}
