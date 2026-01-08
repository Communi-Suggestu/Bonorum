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
import org.jspecify.annotations.NonNull;

import java.io.File;

public class ProjectPlugin implements Plugin<Project> {

    private static final String ENABLED_PROPERTY = "wiki_exporter.enabled";

    private static final String OUTPUT_PROPERTY = "wiki_exporter.output.path";

    private static final String CONFIG_PROPERTY = "wiki_exporter.config.path";

    @Override
    public void apply(@NotNull Project project) {
        final BonorumExtension extension = project.getExtensions().create("bonorum", BonorumExtension.class);

        RunManager runs = project.getExtensions().getByType(RunManager.class);

        project.getRepositories().exclusiveContent(exclusiveContentRepository -> {
            exclusiveContentRepository.forRepositories(
                project.getRepositories().maven(repository -> {
                    repository.setName("Sinytra - Wiki Exporter");
                    repository.setUrl("https://maven.sinytra.org");
                })
            );
            exclusiveContentRepository.filter(filter-> {
                filter.includeModule("org.sinytra", "wiki-exporter-neoforge");
            });
        });

        project.getConfigurations().getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
            .getDependencies().addLater(extension.getSinytraExporterVersion()
                .map("org.sinytra:wiki-exporter-neoforge:%s"::formatted)
                .map(project.getDependencies()::create));

        createExtractTextures(project, runs, extension);
        createExtractMetadata(project, runs, extension);
    }

    private static void createExtractTextures(final @NonNull Project project, final RunManager runs, final BonorumExtension extension)
    {
        final String name = "exportTextures";

        if (runs.findByName(name) != null)
            return;

        final Run clientRun = runs.getByName("client");
        runs.create(name, run -> {
            configureRun(project, extension, run, clientRun, "texture", "Export textures");
        });
    }

    private static void createExtractMetadata(final @NonNull Project project, final RunManager runs, final BonorumExtension extension)
    {
        final String name = "exportMetadata";

        if (runs.findByName(name) != null)
            return;

        final Run clientRun = runs.getByName("server");
        runs.create(name, run -> {
            configureRun(project, extension, run, clientRun, "metadata", "Export metadata");

            run.getArguments().add("--nogui");
        });
    }

    private static void configureRun(
        final @NonNull Project project,
        final BonorumExtension extension,
        final Run run,
        final Run templateRun,
        final String typeName,
        final String diplayName)
    {
        run.configure(templateRun);
        run.getModSources().addAllLater(templateRun.getModSources().all());
        run.configureFromTypeWithName(false);
        run.getIDERunName().set(diplayName);
        run.getExtensions().getByType(IdeaRunExtension.class).getPrimarySourceSet().set(templateRun.getExtensions().getByType(IdeaRunExtension.class).getPrimarySourceSet());
        run.getWorkingDirectory().set(
                project.file("runs/exporters/%s".formatted(
                    typeName
                ))
        );

        run.getSystemProperties()
                .put(OUTPUT_PROPERTY, run.getWorkingDirectory().map(directory -> directory.dir("output").getAsFile().getAbsolutePath()));
        run.getSystemProperties()
                .put(CONFIG_PROPERTY, extension.getConfigFile().map(RegularFile::getAsFile).map(File::getAbsolutePath));
        run.getSystemProperties()
                .put(ENABLED_PROPERTY, "true");
    }
}
