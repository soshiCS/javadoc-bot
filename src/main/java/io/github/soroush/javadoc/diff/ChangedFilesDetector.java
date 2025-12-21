package io.github.soroush.javadoc.diff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * /**
 *  Detects changes in files.
 * /
 */
public final class ChangedFilesDetector {

    /**
     * /**
     *  Private constructor for the ChangedFilesDetector class.
     *  This class is intended to be a utility class and cannot be instantiated.
     * /
     */
    private ChangedFilesDetector() {
        // utility class
    }

    /**
     * /**
     *  Detects and returns a list of Java files that have changed compared to the specified base reference.
     *
     *  @param baseRef the base reference to compare against, typically a commit or branch name
     *  @return a list of paths to changed Java files
     *  @throws IOException if an I/O error occurs during the process
     *  @throws InterruptedException if the process is interrupted
     * /
     */
    public static List<Path> detect(String baseRef) throws IOException, InterruptedException {
        List<Path> changedJavaFiles = new ArrayList<>();
        ProcessBuilder pb = new ProcessBuilder("git", "diff", "--name-only", baseRef);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(".java")) {
                    changedJavaFiles.add(Path.of(line));
                }
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("git diff failed with exit code " + exitCode);
        }
        return changedJavaFiles;
    }
}
