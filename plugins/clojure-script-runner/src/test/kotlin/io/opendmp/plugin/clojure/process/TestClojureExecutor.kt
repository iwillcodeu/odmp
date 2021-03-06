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

package io.opendmp.plugin.clojure.process

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
class TestClojureExecutor {

    @Test
    fun `Clojure executor should return result as byte array`(){
        val cljEx = ClojureExecutor()
        val script = """
            (defn process [xs] (map #(* % 2 ) xs))
        """.trimIndent()
        val data = listOf(1, 2, 3, 4, 5).map{it.toByte()}.toByteArray()
        val result = cljEx.executeScript(script, data)
        assertNotNull(result)
        val resultInts = result.toList().map { it.toInt() }
        assertEquals(listOf(2, 4, 6, 8, 10), resultInts)
    }

    @Test
    fun `Clojure executor should be able to use a library`() {
        val cljEx = ClojureExecutor()
        val script = """
            (defn process [xs]
              (let [input (slurp xs)]
                (cheshire/generate-string {:data input})))
        """.trimIndent()
        val data = "I am me".toByteArray()
        val result = cljEx.executeScript(script, data)
        val resultStr = String(result, Charsets.UTF_8)
        assertEquals("""{"data":"I am me"}""", resultStr)
    }

}