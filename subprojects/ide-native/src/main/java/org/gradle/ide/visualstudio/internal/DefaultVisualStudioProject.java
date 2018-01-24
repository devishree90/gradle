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

package org.gradle.ide.visualstudio.internal;

import org.gradle.api.Action;
import org.gradle.api.XmlProvider;
import org.gradle.api.internal.AbstractBuildableComponentSpec;
import org.gradle.ide.visualstudio.VisualStudioProject;
import org.gradle.ide.visualstudio.XmlConfigFile;
import org.gradle.internal.file.PathToFileResolver;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.platform.base.internal.ComponentSpecIdentifier;
import org.gradle.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A VisualStudio project represents a set of binaries for a component that may vary in build type and target platform.
 */
public class DefaultVisualStudioProject extends AbstractBuildableComponentSpec implements VisualStudioProject {
    private final DefaultConfigFile projectFile;
    private final DefaultConfigFile filtersFile;
    private final String projectPath;
    private final String componentName;
    private final List<File> additionalFiles = new ArrayList<File>();
    private final Map<VisualStudioTargetBinary, VisualStudioProjectConfiguration> configurations = new LinkedHashMap<VisualStudioTargetBinary, VisualStudioProjectConfiguration>();

    public DefaultVisualStudioProject(ComponentSpecIdentifier componentIdentifier, String projectPath, String componentName, PathToFileResolver fileResolver, Instantiator instantiator) {
        super(componentIdentifier, VisualStudioProject.class);
        this.projectPath = projectPath;
        this.componentName = componentName;
        projectFile = instantiator.newInstance(DefaultConfigFile.class, fileResolver, getName() + ".vcxproj");
        filtersFile = instantiator.newInstance(DefaultConfigFile.class, fileResolver, getName() + ".vcxproj.filters");
    }

    @Override
    public String getComponentName() {
        return componentName;
    }

    public DefaultConfigFile getProjectFile() {
        return projectFile;
    }

    public DefaultConfigFile getFiltersFile() {
        return filtersFile;
    }

    public void addSourceFile(File sourceFile) {
        additionalFiles.add(sourceFile);
    }

    public String getUuid() {
        String vsComponentPath = projectPath + ":" + getName();
        return "{" + UUID.nameUUIDFromBytes(vsComponentPath.getBytes()).toString().toUpperCase() + "}";
    }

    public Set<File> getSourceFiles() {
        Set<File> allSources = new LinkedHashSet<File>();
        for (VisualStudioTargetBinary binary : configurations.keySet()) {
            allSources.addAll(binary.getSourceFiles().getFiles());
        }
        allSources.addAll(additionalFiles);
        return allSources;
    }

    public Set<File> getResourceFiles() {
        Set<File> allResources = new LinkedHashSet<File>();
        for (VisualStudioTargetBinary binary : configurations.keySet()) {
            allResources.addAll(binary.getResourceFiles().getFiles());
        }
        return allResources;
    }

    public Set<File> getHeaderFiles() {
        Set<File> allHeaders = new LinkedHashSet<File>();
        for (VisualStudioTargetBinary binary : configurations.keySet()) {
            allHeaders.addAll(binary.getHeaderFiles().getFiles());
        }
        return allHeaders;
    }

    public List<VisualStudioProjectConfiguration> getConfigurations() {
        return CollectionUtils.toList(configurations.values());
    }

    public void addConfiguration(VisualStudioTargetBinary nativeBinary, VisualStudioProjectConfiguration configuration) {
        configurations.put(nativeBinary, configuration);
        builtBy(nativeBinary.getSourceFiles());
        builtBy(nativeBinary.getResourceFiles());
        builtBy(nativeBinary.getHeaderFiles());
    }

    public VisualStudioProjectConfiguration getConfiguration(VisualStudioTargetBinary nativeBinary) {
        return configurations.get(nativeBinary);
    }

    public static class DefaultConfigFile implements XmlConfigFile {
        private final List<Action<? super XmlProvider>> actions = new ArrayList<Action<? super XmlProvider>>();
        private final PathToFileResolver fileResolver;
        private Object location;

        public DefaultConfigFile(PathToFileResolver fileResolver, String defaultLocation) {
            this.fileResolver = fileResolver;
            this.location = defaultLocation;
        }

        public File getLocation() {
            return fileResolver.resolve(location);
        }

        public void setLocation(Object location) {
            this.location = location;
        }

        public void withXml(Action<? super XmlProvider> action) {
            actions.add(action);
        }

        public List<Action<? super XmlProvider>> getXmlActions() {
            return actions;
        }
    }
}
