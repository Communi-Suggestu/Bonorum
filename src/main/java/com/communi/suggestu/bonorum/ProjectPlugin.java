package com.communi.suggestu.bonorum;

import com.communi.suggestu.bonorum.extensions.BonorumExtension;
import net.neoforged.gradle.dsl.common.runs.ide.extensions.IdeaRunExtension;
import net.neoforged.gradle.dsl.common.runs.run.Run;
import net.neoforged.gradle.dsl.common.runs.run.RunManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFile;
import org.gradle.api.plugins.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ProjectPlugin implements Plugin<Project> {

    private static final String ENABLED_PROPERTY = "wiki_exporter.enabled";

    private static final String OUTPUT_PROPERTY = "wiki_exporter.output.path";

    private static final String CONFIG_PROPERTY = "wiki_exporter.config.path";

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
                            .map("org.sinytra:wiki-exporter-neoforge:%s"::formatted)
                            .map(project.getDependencies()::create));

            run.getSystemProperties()
                    .put(OUTPUT_PROPERTY, run.getWorkingDirectory().map(directory -> directory.dir("output").getAsFile().getAbsolutePath()));
            run.getSystemProperties()
                    .put(CONFIG_PROPERTY, extension.getConfigFile().map(RegularFile::getAsFile).map(File::getAbsolutePath));
            run.getSystemProperties()
                    .put(ENABLED_PROPERTY, "true");
        });
    }
}
