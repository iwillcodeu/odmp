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

package io.opendmp.processor.messaging

import io.opendmp.processor.handler.RunPlanRequestHandler
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Profile("!test")
@Component
class RunPlanStopRequestRouter(
        @Autowired val runPlanRequestHandler: RunPlanRequestHandler) : RouteBuilder() {

    @Value("\${odmp.pulsar.namespace}")
    val pulsarNamespace: String = "public/default"

    val consumerName = "processor-" + UUID.randomUUID().toString().replace("-","")

    fun endPointUrl(): String =
            "pulsar:persistent://$pulsarNamespace/runplan_stop_request?consumerName=$consumerName"

    override fun configure() {
        from(endPointUrl()).to("bean:runPlanRequestHandler?method=receiveStopRequest")
    }

}
