package io.github.soroush.javadoc;

import com.github.javaparser.ast.CompilationUnit;
import io.github.soroush.javadoc.ast.AstParser;
import io.github.soroush.javadoc.ast.JavadocInserter;
import io.github.soroush.javadoc.ast.MissingJavadocFinder;
import io.github.soroush.javadoc.diff.ChangedFilesDetector;
import io.github.soroush.javadoc.llm.LlmClient;
import io.github.soroush.javadoc.llm.PromptBuilder;
import io.github.soroush.javadoc.model.MissingDoc;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * /**
 *  Generated JavaDoc (stub).
 *
 *  This documentation was generated automatically.
 * /
 */
public final class JavadocBot {

    /**
     * /**
     *  Generated JavaDoc (stub).
     *
     *  This documentation was generated automatically.
     * /
     */
    public static void main(String[] args) {
        try {
            String baseRef = parseBaseArg(args);
            List<Path> changedFiles = ChangedFilesDetector.detect(baseRef);
            boolean modified = false;
            for (Path file : changedFiles) {
                CompilationUnit cu = AstParser.parse(file);
                List<MissingDoc> missingInFile = MissingJavadocFinder.find(file, cu);
                for (MissingDoc doc : missingInFile) {
                    String prompt = PromptBuilder.buildPrompt(cu, doc);
                    String generated = LlmClient.generateJavadoc(prompt);
                    boolean inserted = JavadocInserter.insertJavadoc(cu, doc, generated);
                    modified |= inserted;
                }
                if (!missingInFile.isEmpty()) {
                    Files.writeString(file, cu.toString());
                }
            }
            if (modified) {
                System.out.println("Generated JavaDoc inserted.");
            } else {
                System.out.println("No missing JavaDoc found.");
            }
        } catch (Exception e) {
            System.err.println("javadoc-bot failed:");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /**
     * /**
     *  Generated JavaDoc (stub).
     *
     *  This documentation was generated automatically.
     * /
     */
    private static String parseBaseArg(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if ("--base".equals(args[i])) {
                return args[i + 1];
            }
        }
        throw new IllegalArgumentException("Missing required argument: --base <git-ref>");
    }
}
