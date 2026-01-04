package com.communi.suggestu.bonorum.extensions;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

/**
 * Extension that configures Bonorum.
 */
public abstract class BonorumExtension {

    /**
     * Sets the sinytra exporter version for the project to use.
     *
     * @return The version to use.
     */
    @Input
    public abstract Property<String> getSinytraExporterVersion();

    /**
     * Defines the configuration file to use.
     */
    @Input
    public abstract RegularFileProperty getConfigFile();
}
