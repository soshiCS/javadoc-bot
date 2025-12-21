package io.github.soroush.javadoc.diff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ChangedFilesDetector {

    private ChangedFilesDetector() {
        // utility class
    }

    public static List<Path> detect(String baseRef) throws IOException, InterruptedException {
        List<Path> changedJavaFiles = new ArrayList<>();

        ProcessBuilder pb = new ProcessBuilder(
                "git",
                "diff",
                "--name-only",
                baseRef
        );
        pb.redirectErrorStream(true);

        Process process = pb.start();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {

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
