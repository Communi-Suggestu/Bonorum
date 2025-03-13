package com.communi.suggestu.bonorum;

import com.communi.suggestu.bonorum.extensions.BonorumExtension;
import net.neoforged.gradle.dsl.common.runs.ide.extensions.IdeaRunExtension;
import net.neoforged.gradle.dsl.common.runs.run.Run;
import net.neoforged.gradle.dsl.common.runs.run.RunManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ProjectPlugin implements Plugin<Project> {

    private static final String RENDER_PROPERTY = "item_asset_export.render.namespaces";
    private static final String OUTPUT_PROPERTY = "item_asset_export.render.output";
    private static final String ANIMATED_PROPERTY = "item_asset_export.render.outputs.gif";

    @Override
    public void apply(@NotNull Project project) {
        final BonorumExtension extension = project.getExtensions().create("bonorum", BonorumExtension.class);

        RunManager runs = project.getExtensions().getByType(RunManager.class);

        final String name = "exportTextures";

        if (runs.findByName(name) != null)
            return;

        final Run clientRun = runs.getByName("client");
        runs.create(name, run -> {
            run.configure(clientRun);
            run.getModSources().addAllLater(clientRun.getModSources().all());
            run.configureFromTypeWithName(false);
            run.getIDERunName().set("Export textures");
            run.getExtensions().getByType(IdeaRunExtension.class).getPrimarySourceSet().set(clientRun.getExtensions().getByType(IdeaRunExtension.class).getPrimarySourceSet());
            run.getWorkingDirectory().set(
                    project.file("runs/exporters/texture")
            );

            project.getConfigurations().getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
                            .getDependencies().addLater(extension.getSinytraExporterVersion()
                            .map("org.sinytra:item-asset-export-neoforge:%s"::formatted)
                            .map(project.getDependencies()::create));

            run.getSystemProperties()
                    .put(OUTPUT_PROPERTY, run.getWorkingDirectory().map(directory -> directory.dir("output").getAsFile().getAbsolutePath()));
            run.getSystemProperties()
                    .put(RENDER_PROPERTY, extension.getExportedNamespaces().map(namespaces -> String.join(",", namespaces)));
            run.getSystemProperties()
                    .put(ANIMATED_PROPERTY, "true");
        });
    }
}
