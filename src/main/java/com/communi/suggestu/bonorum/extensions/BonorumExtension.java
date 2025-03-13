package com.communi.suggestu.bonorum.extensions;

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
     * Defines the namespaces to export.
     * <p>
     *     Minecraft has "minecraft" as namespace.
     *     Your mod generally uses its mod id.
     * </p>
     *
     * @return The name of the namespaces to export.
     */
    @Input
    public abstract ListProperty<String> getExportedNamespaces();
}
