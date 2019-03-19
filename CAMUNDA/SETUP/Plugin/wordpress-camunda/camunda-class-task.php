<?php

final class WP_Task  {
	
	public $idTask;
	public $nameTask;
    public $idProcess;
    public $post_content;
    
    public function parse($args){
        $this->idTask=$args->idTask;
        $this->nameTask=$args->nameTask;
        $this->idProcess=$args->idProcess;
        $this->post_content=$args->post_content;
    }
    public function __construct( $args ) {
        $this->parse($args);
	}
	public static function getTask($processID){
		global $wpdb;
        $query="SELECT * FROM {$wpdb->prefix}tasks	WHERE idProcess = {$processID}";
        $taskes= array();
        $taskQuery = $wpdb->get_results($query);
        foreach($taskQuery  as $key => $row)
        {
            $taskes[]=new WP_Task($row);
        }
        return $taskes;
    }

    public static function getOneTask($processID,$taskID){
		global $wpdb;
        $query="SELECT * FROM {$wpdb->prefix}tasks	WHERE idProcess = {$processID} AND idTask= '{$taskID}'";
        $taskes= array();
        $taskQuery = $wpdb->get_results($query);
        foreach($taskQuery  as $key => $row)
        {
            return new WP_Task($row);
        }
        return null;
    }

    public static function createTask($processID,$idTaskNew,$nameTaskNew)
    {	
        global $wpdb;
        $data=array(
            'idTask' => $idTaskNew,
            'nameTask' => $nameTaskNew,
            'idProcess' => $processID, 
            'post_content' =>"<form name='generatedForm' role='form'> </form>"
            );
            $wpdb->insert($wpdb->prefix.'tasks',$data, array( '%s','%s','%d','%s'));
    }
    public static function updateTask($processID,$idTaskOld,$idTaskNew,$nameTaskNew)
    {	
        global $wpdb;
        $data=array(
            'idTask' => $idTaskNew,
            'nameTask' => $nameTaskNew,
        );
        $where =array(
            "idTask" =>$idTaskOld,
            "idProcess"=>$processID
        );
        $wpdb->update($wpdb->prefix.'tasks',$data, $where);
    }

    public static function deleteTask($processID,$idTaskDelete)
    {	
        global $wpdb;
        $where =array(
            "idTask" =>$idTaskDelete,
            "idProcess"=>$processID
        );
        $wpdb->delete($wpdb->prefix.'tasks', $where);
    }

    public static function updateFormTask($processID,$idTaskUpdateForm,$post_content_form)
    {	
        global $wpdb;
        $data=array(
            'post_content' => $post_content_form,
        );
        $where =array(
            "idTask" =>$idTaskUpdateForm,
            "idProcess"=>$processID
        );
        return  $wpdb->update($wpdb->prefix.'tasks', $data, $where);
    }

    public static function insertTask($processID,$idTaskNew,$nameTaskNew,$post_content_form)
    {	
        global $wpdb;
        $data=array(
            'idTask' => $idTaskNew,
            'nameTask' => $nameTaskNew,
            'idProcess' => $processID, 
            'post_content' =>$post_content_form
        );
         $wpdb->insert($wpdb->prefix.'tasks',$data, array( '%s','%s','%d','%s'));
    }
    
}
