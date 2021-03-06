/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.api.internal.changedetection.state;

import org.gradle.internal.resource.ResourceContentMetadataSnapshot;

/**
 * An immutable snapshot of the type and content of a file. Does not include any information about the identity of the file. The file may not exist.
 *
 * Should implement {@link #equals(Object)} and {@link #hashCode()} to compare these.
 */
public interface FileContentSnapshot extends ResourceContentMetadataSnapshot, NormalizedFileSnapshot {
    boolean isContentUpToDate(FileContentSnapshot snapshot);

    boolean isContentAndMetadataUpToDate(FileContentSnapshot snapshot);
}
