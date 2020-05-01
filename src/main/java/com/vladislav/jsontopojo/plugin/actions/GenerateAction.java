package com.vladislav.jsontopojo.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.vladislav.jsontopojo.plugin.ui.GeneratorDialog;
import org.jetbrains.annotations.NotNull;

public class GenerateAction extends com.intellij.openapi.actionSystem.AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final VirtualFile actionFolder = e.getData(LangDataKeys.VIRTUAL_FILE);

        if (validateActionFolder(project, actionFolder)) {
            final ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
            final VirtualFile moduleSourceRoot = projectRootManager.getFileIndex().getSourceRootForFile(actionFolder);
            assert moduleSourceRoot != null;
            final String packageName = getPackageName(projectRootManager, actionFolder);
            final GeneratorDialog generatorDialog = new GeneratorDialog(project, packageName, moduleSourceRoot);
            generatorDialog.setVisible(true);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final VirtualFile actionFolder = e.getData(LangDataKeys.VIRTUAL_FILE);

        if (validateActionFolder(project, actionFolder)) {
            final String packageName = getPackageName(ProjectRootManager.getInstance(project), actionFolder);
            e.getPresentation().setVisible(packageName != null);
        } else {
            e.getPresentation().setVisible(false);
        }
    }

    private static boolean validateActionFolder(Project project, VirtualFile actionFolder) {
        return project != null && actionFolder != null && actionFolder.isDirectory();
    }

    private static String getPackageName(ProjectRootManager projectRootManager, VirtualFile actionFolder) {
        return projectRootManager.getFileIndex().getPackageNameByDirectory(actionFolder);
    }

}
