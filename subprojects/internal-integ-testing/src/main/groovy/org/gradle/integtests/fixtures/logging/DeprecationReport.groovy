/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.gradle.integtests.fixtures.logging

import org.gradle.internal.featurelifecycle.LoggingDeprecatedFeatureHandler

class DeprecationReport {
    static class Deprecation {
        String message
        String stacktrace
    }

    List<Deprecation> deprecations = []

    DeprecationReport(File reportFile) {
        if (reportFile != null && reportFile.exists()) {
            extractDeprecations(reportFile)
        }
    }

    def extractDeprecations(File file) {
        List<String> deprecationBlocks = file.text.split(LoggingDeprecatedFeatureHandler.BLOCK_SEPARATOR)

        deprecationBlocks.each {
            def firstLine = it.split("\n")[0]
            deprecations.add(new Deprecation(message: firstLine, stacktrace: it))
        }
    }

    def validate(int expectWarningCount) {
        if (expectWarningCount != deprecations.size()) {
            throw new AssertionError("Expected ${expectWarningCount} deprecation warnings, found ${deprecations.size()}:\n${deprecations*.message.join('\n')}")
        }
    }

    boolean contains(String message) {
        return deprecations.any { it.stacktrace.contains(message) }
    }

    int count(String message) {
        int result = 0
        deprecations*.stacktrace.each {
            result += it.count(message)
        }
        return result
    }
}