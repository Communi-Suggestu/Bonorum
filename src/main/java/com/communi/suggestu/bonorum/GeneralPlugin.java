package com.communi.suggestu.bonorum;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public class GeneralPlugin implements Plugin<Object> {

    @Override
    public void apply(@NotNull Object target) {
        if (target instanceof Project project) {
            project.getPlugins().apply(ProjectPlugin.class);
            return;
        }

        throw new IllegalArgumentException("Plugin needs to be applied to a project");
    }
}
