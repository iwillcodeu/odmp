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

import io.opendmp.processor.handler.ProcessRequestHandler
import org.apache.camel.CamelContext
import org.apache.camel.ConsumerTemplate
import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ProcessRequestRouter(
        @Autowired val processRequestHandler: ProcessRequestHandler,
        @Autowired val camel: CamelContext,
        @Autowired val consumer: ConsumerTemplate
) : RouteBuilder() {

    @Value("\${odmp.pulsar.namespace}")
    val pulsarNamespace: String = "public/default"

    fun endPointUrl() : String =
            "pulsar:persistent://$pulsarNamespace/process_request"

    override fun configure() {
        from(endPointUrl()).to("bean:processRequestHandler")
    }

}