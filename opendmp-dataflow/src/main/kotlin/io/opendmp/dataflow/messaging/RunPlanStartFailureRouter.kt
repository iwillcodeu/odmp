/*
 * Copyright (c) 2020. James Adam and the Open Data Management Platform contributors.
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

package io.opendmp.dataflow.messaging

import io.opendmp.dataflow.handler.RunPlanStatusHandler
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("!test")
@Component
class RunPlanStartFailureRouter(
        @Autowired val runPlanStatusHandler: RunPlanStatusHandler
) : RouteBuilder(){

    @Value("\${odmp.pulsar.namespace}")
    lateinit var pulsarNamespace: String

    fun endPointUrl() : String =
            "pulsar:persistent://$pulsarNamespace/runplan_start_failure"

    override fun configure() {
        from(endPointUrl()).to("bean:runPlanStatusHandler?method=receiveStartFailure")
    }
}