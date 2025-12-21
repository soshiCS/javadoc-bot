package io.github.soroush.javadoc.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Path;

import static com.github.javaparser.ParserConfiguration.LanguageLevel.JAVA_21;

public final class AstParser {

    private static final JavaParser PARSER;

    static {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(JAVA_21);
        PARSER = new JavaParser(config);
    }

    private AstParser() {
        // utility class
    }

    public static CompilationUnit parse(Path javaFile) throws IOException {
        ParseResult<CompilationUnit> result = PARSER.parse(javaFile);

        if (result.isSuccessful() && result.getResult().isPresent()) {
            return result.getResult().get();
        }

        throw new IOException("Failed to parse Java file:  " + javaFile);
    }
}
