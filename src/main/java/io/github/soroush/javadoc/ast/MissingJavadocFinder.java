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

public final class MissingJavadocFinder {

    private MissingJavadocFinder() {
        // utility class
    }

    public static List<MissingDoc> find(Path file, CompilationUnit cu) {
        List<MissingDoc> missing = new ArrayList<>();

        // Classes & interfaces
        for (ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            if (cls.getJavadoc().isEmpty()) {
                missing.add(new MissingDoc(
                        file,
                        Kind.CLASS,
                        cls.getNameAsString(),
                        null,
                        line(cls)
                ));
            }
        }

        // Enums
        for (EnumDeclaration en : cu.findAll(EnumDeclaration.class)) {
            if (en.getJavadoc().isEmpty()) {
                missing.add(new MissingDoc(
                        file,
                        Kind.CLASS,
                        en.getNameAsString(),
                        null,
                        line(en)
                ));
            }
        }

        // Methods
        for (MethodDeclaration m : cu.findAll(MethodDeclaration.class)) {
            if (m.getJavadoc().isEmpty()) {
                missing.add(new MissingDoc(
                        file,
                        Kind.METHOD,
                        m.getNameAsString(),
                        signature(m),
                        line(m)
                ));
            }
        }

        // Constructors
        for (ConstructorDeclaration c : cu.findAll(ConstructorDeclaration.class)) {
            if (c.getJavadoc().isEmpty()) {
                missing.add(new MissingDoc(
                        file,
                        Kind.CONSTRUCTOR,
                        c.getNameAsString(),
                        signature(c),
                        line(c)
                ));
            }
        }

        return missing;
    }

    private static int line(com.github.javaparser.ast.Node node) {
        return node.getBegin().map(p -> p.line).orElse(-1);
    }

    private static String signature(MethodDeclaration m) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < m.getParameters().size(); i++) {
            sb.append(m.getParameter(i).getType());
            if (i < m.getParameters().size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private static String signature(ConstructorDeclaration c) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < c.getParameters().size(); i++) {
            sb.append(c.getParameter(i).getType());
            if (i < c.getParameters().size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
