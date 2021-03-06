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

import clojure.java.api.Clojure
import clojure.lang.IFn


class ClojureExecutor {

    private val crNs = "io.opendmp.plugin.clojure.clj-runner"

    init {
        val require: IFn = Clojure.`var`("clojure.core", "require")
        require.invoke(Clojure.read(crNs))
    }

    fun executeScript(code: String, data: ByteArray): ByteArray  {
        val runnerFn: IFn = Clojure.`var`(crNs, "execute")
        return runnerFn.invoke(code, data) as ByteArray
    }

}