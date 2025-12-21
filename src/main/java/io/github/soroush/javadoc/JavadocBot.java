package io.github.soroush.javadoc;

import com.github.javaparser.ast.CompilationUnit;
import io.github.soroush.javadoc.ast.AstParser;
import io.github.soroush.javadoc.ast.MissingJavadocFinder;
import io.github.soroush.javadoc.diff.ChangedFilesDetector;
import io.github.soroush.javadoc.model.MissingDoc;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class JavadocBot {

    public static void main(String[] args) {
        try {
            String baseRef = parseBaseArg(args);

            List<Path> changedFiles = ChangedFilesDetector.detect(baseRef);

            List<MissingDoc> allMissing = new ArrayList<>();

            for (Path file : changedFiles) {
                CompilationUnit cu = AstParser.parse(file);
                List<MissingDoc> missingInFile =
                        MissingJavadocFinder.find(file, cu);
                allMissing.addAll(missingInFile);
            }

            printReport(allMissing);

        } catch (Exception e) {
            System.err.println("javadoc-bot failed:");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static String parseBaseArg(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if ("--base".equals(args[i])) {
                return args[i + 1];
            }
        }

        throw new IllegalArgumentException(
                "Missing required argument: --base <git-ref>");
    }

    private static void printReport(List<MissingDoc> missingDocs) {
        if (missingDocs.isEmpty()) {
            System.out.println("No missing Javadoc found. ");
            return;
        }

        System.out.println("Missing Javadoc found:");
        for (MissingDoc doc : missingDocs) {
            System.out.println(" - " + doc);
        }
    }
}
