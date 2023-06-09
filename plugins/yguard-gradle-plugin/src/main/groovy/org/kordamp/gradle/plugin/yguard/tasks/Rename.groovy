/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2022 Andres Almiray.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.gradle.plugin.yguard.tasks

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property

import javax.inject.Inject

/**
 *
 * @author Andres Almiray
 * @since 0.1.0
 */
@CompileStatic
class Rename {
    final Property<String> logFile
    final Property<String> mainClass
    final Property<Boolean> conserveManifest
    final Property<Boolean> replaceClassNameStrings
    final Property<String> annotationClass
    final Property<Boolean> enabled

    final Props props
    final Keep keep
    final List<Adjust> adjusts = []

    private final Project project

    @Inject
    Rename(Project project) {
        this.project = project
        logFile = project.objects.property(String).convention('rename.xml')
        mainClass = project.objects.property(String)
        conserveManifest = project.objects.property(Boolean).convention(false)
        replaceClassNameStrings = project.objects.property(Boolean).convention(false)
        annotationClass = project.objects.property(String).convention('com.yworks.util.annotation.Obfuscation')
        enabled = project.objects.property(Boolean).convention(true)

        props = project.objects.newInstance(Props, project)
        keep = project.objects.newInstance(Keep, project)
    }

    void props(Action<? extends Props> action) {
        action.execute(props)
    }

    void keep(Action<? extends Keep> action) {
        action.execute(keep)
    }

    void adjust(Action<? extends Adjust> action) {
        Adjust adjust = project.objects.newInstance(Adjust, project)
        action.execute(adjust)
        adjusts.add(adjust)
    }

    @CompileDynamic
    List<String> validate() {
        List<String> errors = []
        errors.addAll(props.validate())
        errors.addAll(keep.validate())
        errors.addAll(adjusts*.validate())
        errors
    }
}