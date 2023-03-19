package org.waveapi.impltester;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;

import java.io.File;

public class GitOperator {
    public static void clone(String url, File outputDirectory) {
        try {
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(outputDirectory)
                    .setProgressMonitor(new TextProgressMonitor())
                    .call();
        } catch (GitAPIException e) {
            Main.LOGGER.info("Failed to clone API repo. " + e.getMessage());
        }
    }
}
