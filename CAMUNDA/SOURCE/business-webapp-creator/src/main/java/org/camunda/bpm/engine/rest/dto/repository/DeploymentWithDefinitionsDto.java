package org.camunda.bpm.engine.rest.dto.repository;

import java.util.List;

/**
 * @author Christopher Zell <christopher.zell@camunda.com>
 */
public class DeploymentWithDefinitionsDto extends DeploymentDto {

    protected List<ProcessDefinitionDto> deployedProcessDefinitions;

    public List<ProcessDefinitionDto> getDeployedProcessDefinitions() {
        return deployedProcessDefinitions;
    }

    public void setDeployedProcessDefinitions(List<ProcessDefinitionDto> deployedProcessDefinitions) {
        this.deployedProcessDefinitions = deployedProcessDefinitions;
    }

}
