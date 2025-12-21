package io.github.soroush.javadoc.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import io.github.soroush.javadoc.model.MissingDoc;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static io.github.soroush.javadoc.model.MissingDoc.Kind;

/**
 * /**
 *  TODO: add documentation.
 * /
 */
public final class MissingJavadocFinder {

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private MissingJavadocFinder() {
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    public static List<MissingDoc> find(Path file, CompilationUnit cu) {
        List<MissingDoc> missing = new ArrayList<>();
        // Classes & interfaces
        for (ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            if (cls.getJavadoc().isEmpty()) {
                Kind kind = cls.isInterface() ? Kind.INTERFACE : Kind.CLASS;
                missing.add(new MissingDoc(file, kind, cls.getNameAsString(), null, line(cls)));
            }
        }
        // Enums
        for (EnumDeclaration en : cu.findAll(EnumDeclaration.class)) {
            if (en.getJavadoc().isEmpty()) {
                missing.add(new MissingDoc(file, Kind.ENUM, en.getNameAsString(), null, line(en)));
            }
        }
        // Methods
        for (MethodDeclaration m : cu.findAll(MethodDeclaration.class)) {
            if (m.getJavadoc().isEmpty()) {
                missing.add(new MissingDoc(file, Kind.METHOD, m.getNameAsString(), signature(m), line(m)));
            }
        }
        // Constructors
        for (ConstructorDeclaration c : cu.findAll(ConstructorDeclaration.class)) {
            if (c.getJavadoc().isEmpty()) {
                missing.add(new MissingDoc(file, Kind.CONSTRUCTOR, c.getNameAsString(), signature(c), line(c)));
            }
        }
        return missing;
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private static int line(com.github.javaparser.ast.Node node) {
        return node.getBegin().map(p -> p.line).orElse(-1);
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private static String signature(MethodDeclaration m) {
        return m.getParameters().stream().map(p -> p.getType().asString()).reduce("(", (a, b) -> a.equals("(") ? "(" + b : a + ", " + b) + ")";
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private static String signature(ConstructorDeclaration c) {
        return c.getParameters().stream().map(p -> p.getType().asString()).reduce("(", (a, b) -> a.equals("(") ? "(" + b : a + ", " + b) + ")";
    }
}
