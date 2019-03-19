/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.rest.dto.repository;

import java.util.*;

public class DeploymentDto {

    protected String id;
    protected String name;
    protected String source;
    protected Date deploymentTime;
    protected String tenantId;

    public DeploymentDto() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public Date getDeploymentTime() {
        return deploymentTime;
    }

    public String getTenantId() {
        return tenantId;
    }

}