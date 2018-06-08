package com.kascend.mvphelper.action;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.kascend.mvphelper.ui.GeneratorHelper;

import java.util.HashMap;
import java.util.Map;

public class MvpAction extends AnAction implements GeneratorHelper.OnDismissListener {

    private AnActionEvent event;
    private Project project;
    private Map<String, String> map = new HashMap<>();


    @Override
    public void actionPerformed(AnActionEvent event) {
        this.event = event;
        project = event.getData(PlatformDataKeys.PROJECT);
        boolean isDir = isDirectory(event);
        if (isDir) {
            GeneratorHelper dialog = new GeneratorHelper();
            dialog.setOnDismissListener(this);
            dialog.pack();
            dialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(event.getProject()));
            dialog.setVisible(true);

        }

    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);

        Project project = e.getData(CommonDataKeys.PROJECT);
        Presentation presentation = e.getPresentation();
        if (project != null && isDirectory(e)) {
            presentation.setEnabledAndVisible(true);
        } else {
            presentation.setEnabledAndVisible(false);
        }
    }

    @Override
    public void onConfirm(String name, boolean isActivity, boolean shouldNewPackage) {
        VirtualFile file = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (file != null) {
            String path = file.getPath();

            String packagePath = path.substring(path.indexOf("com/"));
            String packageName = packagePath.replace("/", ".");


            map.put("NAME", name);
            map.put("PACKAGE_NAME", packageName);

            //鼠标右键所选择的路径
            IdeView ideView = event.getRequiredData(LangDataKeys.IDE_VIEW);


            WriteCommandAction.runWriteCommandAction(project, () -> {
                PsiDirectory directory = ideView.getOrChooseDirectory();
                assert directory != null;
                if (shouldNewPackage) {
                    directory = directory.createSubdirectory(name.toLowerCase());
                    map.put("PACKAGE_NAME", packageName + "." + name.toLowerCase());
                }
                JavaDirectoryService directoryService = JavaDirectoryService.getInstance();

                if (directory.findFile(name + "Contract.java") == null) {
                    directoryService.createClass(directory, name + "Contract", "TemplateContract", true, map);
                }

                if (directory.findFile(name + "Model.java") == null) {
                    directoryService.createClass(directory, name + "Model", "TemplateModel", true, map);
                }

                if (directory.findFile(name + "Presenter.java") == null) {
                    directoryService.createClass(directory, name + "Presenter", "TemplatePresenter", true, map);
                }
                if (isActivity) {
                    if (directory.findFile(name + "Activity.java") == null) {
                        directoryService.createClass(directory, name + "Activity", "TemplateActivity", true, map);
                    }
                } else {
                    if (directory.findFile(name + "Fragment.java") == null) {
                        directoryService.createClass(directory, name + "Fragment", "TemplateFragment", true, map);
                    }
                }
                Messages.showMessageDialog(project, "Success!Generate Mvp code I need only one second,now it's your time!", "KasMvpHelper", null);
            });


        }
    }

    @Override
    public void onCancel() {

    }

    private boolean isDirectory(AnActionEvent event) {
        VirtualFile data = event.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean isDir = false;
        if (data != null && data.isDirectory()) {
            isDir = true;
        }
        return isDir;
    }

}
