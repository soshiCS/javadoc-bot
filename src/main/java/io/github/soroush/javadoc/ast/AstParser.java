package io.github.soroush.javadoc.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Path;

public final class AstParser {

    private static final JavaParser PARSER = new JavaParser();

    private AstParser() {
        // utility class
    }

    public static CompilationUnit parse(Path javaFile) throws IOException {
        ParseResult<CompilationUnit> result = PARSER.parse(javaFile);

        if (result.isSuccessful() && result.getResult().isPresent()) {
            return result.getResult().get();
        }

        throw new IOException("Failed to parse Java file: " + javaFile);
    }
}
