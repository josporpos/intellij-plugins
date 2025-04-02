package es.japp.intellij.plugins.worktree;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeList;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;

import java.util.List;
import java.util.stream.Collectors;


public class NoSkipWorktreeAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;

        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        ChangeList activeChangelist = changeListManager.getDefaultChangeList();
        List<String> files = activeChangelist.getChanges().stream()
                .map(Change::getVirtualFile)
                .filter(vf -> vf != null)
                .map(vf -> vf.getPath())
                .collect(Collectors.toList());

        if (!files.isEmpty()) {
            Git git = Git.getInstance();
            for (String file : files) {
                GitLineHandler handler = new GitLineHandler(project, project.getBaseDir(), GitCommand.UPDATE_INDEX);
                handler.addParameters("--no-skip-worktree", file);
                git.runCommand(handler);
            }
        }
    }
}
