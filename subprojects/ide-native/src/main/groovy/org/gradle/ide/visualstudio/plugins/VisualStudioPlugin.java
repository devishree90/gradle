/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.ide.visualstudio.plugins;

import org.gradle.api.*;
import org.gradle.api.plugins.AppliedPlugin;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.nativeplatform.plugins.NativeComponentModelPlugin;
import org.gradle.plugins.ide.internal.IdePlugin;


/**
 * A plugin for creating a Visual Studio solution for a gradle project.
 */
@Incubating
public class VisualStudioPlugin extends IdePlugin {
    private static final String LIFECYCLE_TASK_NAME = "visualStudio";

    @Override
    protected String getLifecycleTaskName() {
        return LIFECYCLE_TASK_NAME;
    }

    @Override
    protected void onApply(Project target) {
        project.getPluginManager().apply(LifecycleBasePlugin.class);
        
        project.getPluginManager().withPlugin("org.gradle.component-model-base", new Action<AppliedPlugin>() {
            @Override
            public void execute(AppliedPlugin appliedPlugin) {
                project.getPluginManager().apply(NativeComponentModelPlugin.class);
                project.getPluginManager().apply(VisualStudioPluginRules.class);
            }
        });
    }
}
