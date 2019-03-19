<?php

final class WP_Process  {
	
	public $process;
	public $workspace;
	public $tasks;

	public function parse($pro){
		$this->process=$pro;
		$idprocessDefinition=substr($pro->post_name,8);
		$this->workspace = WP_Processdefinition::getProcessdefinition( $idprocessDefinition);
		$this->tasks = WP_Task::getTask($pro->ID);
	}
}
