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

package io.opendmp.processor.run.processors

import io.opendmp.common.model.DataEvent
import io.opendmp.common.model.ProcessorRunModel
import io.opendmp.common.model.ProcessorType
import io.opendmp.common.model.properties.DestinationType
import io.opendmp.processor.TestUtils
import io.opendmp.processor.config.RedisConfig
import io.opendmp.processor.domain.DataEnvelope
import io.opendmp.processor.handler.RunPlanRequestHandler
import io.opendmp.processor.messaging.RunPlanRequestRouter
import io.opendmp.processor.messaging.RunPlanStatusDispatcher
import org.apache.camel.CamelContext
import org.apache.camel.EndpointInject
import org.apache.camel.ProducerTemplate
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.support.DefaultExchange
import org.apache.camel.test.spring.junit5.CamelSpringBootTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.consul.serviceregistry.ConsulAutoServiceRegistration
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.nio.charset.Charset
import java.util.*

@SpringBootTest
@CamelSpringBootTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TestCollectProcessor @Autowired constructor(
        private val testCamelContext: CamelContext
) {

    @EndpointInject("mock:a")
    protected val mockA = MockEndpoint()

    @AfterEach
    fun cleanUp() {}

    @Test
    fun testCollectProcessor() {
        val exchange = DefaultExchange(testCamelContext)
        val properties: Map<String, Any> = mapOf(
                "type" to DestinationType.FOLDER.toString(),
                "location" to "/tmp/testoutput"
        )
        exchange.getIn().body = "I'm Data!".toByteArray()
        exchange.setProperty("dataEnvelope", TestUtils.createDataEnvelope())
        val processor = ProcessorRunModel(
                id = UUID.randomUUID().toString(),
                flowId = UUID.randomUUID().toString(),
                inputs = listOf(),
                name = "Test Collection Processor",
                type = ProcessorType.COLLECT,
                properties = properties)

        val collectProcessor = CollectProcessor(processor)
        collectProcessor.process(exchange)
        val envelopeOut = exchange.getProperty("dataEnvelope") as DataEnvelope
        val data = exchange.getIn().getBody(ByteArray::class.java)
        assertEquals(2, envelopeOut.history.size)
        assertEquals("I'm Data!", String(data, Charsets.UTF_8))
    }

}