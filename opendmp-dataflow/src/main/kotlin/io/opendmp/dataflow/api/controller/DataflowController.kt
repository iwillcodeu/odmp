/*
 * Copyright (c) 2020. The Open Data Management Platform contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.opendmp.dataflow.api.controller

import com.mongodb.client.result.DeleteResult
import io.opendmp.dataflow.api.request.CreateDataflowRequest
import io.opendmp.dataflow.api.request.UpdateDataflowRequest
import io.opendmp.dataflow.api.response.DataflowListItem
import io.opendmp.dataflow.api.response.RunPlanStatus
import io.opendmp.dataflow.model.DataflowModel
import io.opendmp.dataflow.model.ProcessorModel
import io.opendmp.dataflow.model.runplan.RunPlanModel
import io.opendmp.dataflow.service.DataflowService
import io.opendmp.dataflow.service.RunPlanService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/dataflow_api/dataflow")
class DataflowController(private val dataflowService: DataflowService,
                         private val runPlanService: RunPlanService) {

    @GetMapping
    suspend fun findAll(@RequestParam(required = false) enabled: Boolean?) : Flow<DataflowListItem> {
        return dataflowService.getList(enabled)
    }
    
    @GetMapping("/{id}")
    fun findOne(@PathVariable("id") id: String) : Mono<DataflowModel> {
        return dataflowService.get(id)
    }

    /**
     * Gets the current RunPlan associated with a dataflow
     */
    @GetMapping("/{id}/run_plan")
    fun getRunPlan(@PathVariable("id") id: String) : Mono<RunPlanModel> {
        return runPlanService.getForDataflow(id)
    }

    /**
     * Get a brief status of the current run plan
     */
    @GetMapping("/{id}/run_plan_status")
    fun getRunPlanStatus(@PathVariable("id") id: String) : Mono<RunPlanStatus> {
        return runPlanService.getStatusForDataflow(id)
    }

    @PutMapping("/{id}")
    fun updateDataflow(@PathVariable("id") id: String,
                       @Valid @RequestBody data : UpdateDataflowRequest,
                       authentication: Authentication) : Mono<DataflowModel> {
        return dataflowService.updateDataflow(id, data, authentication)
    }

    @PostMapping
    fun insertOne(@Valid @RequestBody data : CreateDataflowRequest,
                  authentication: Authentication) : Mono<DataflowModel> {
        return dataflowService.createDataflow(data, authentication)
    }

    @DeleteMapping("/{id}")
    fun deleteOne(@PathVariable("id") id: String) : Mono<DeleteResult> {
        return dataflowService.delete(id)
    }

    @GetMapping("/{id}/processors")
    fun getProcessors(@PathVariable("id") id: String,
                      @RequestParam (required = false) phase: Int?) : Flow<ProcessorModel> {
        return dataflowService.getProcessors(id, phase)
    }
}