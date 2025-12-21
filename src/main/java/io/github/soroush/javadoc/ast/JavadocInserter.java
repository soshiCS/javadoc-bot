package io.github.soroush.javadoc.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import io.github.soroush.javadoc.model.MissingDoc;

import java.util.Optional;

import static io.github.soroush.javadoc.model.MissingDoc.Kind;

public final class JavadocInserter {

    private static final String PLACEHOLDER =
            "/**\n * TODO: add documentation.\n */";

    private JavadocInserter() {}

    public static boolean insertPlaceholder(CompilationUnit cu, MissingDoc doc) {
        Optional<? extends NodeWithJavadoc<?>> target = findTargetNode(cu, doc);

        if (target.isEmpty()) {
            throw new IllegalStateException(
                    "Could not find AST node for " + doc);
        }

        NodeWithJavadoc<?> node = target.get();

        // Safety check: never overwrite existing Javadoc
        if (node.getJavadoc().isPresent()) {
            return false;
        }

        node.setComment(new JavadocComment(PLACEHOLDER));
        return true;
    }

    private static Optional<? extends NodeWithJavadoc<?>> findTargetNode(
            CompilationUnit cu,
            MissingDoc doc
    ) {
        return switch (doc.getKind()) {
            case CLASS ->
                    cu.findAll(ClassOrInterfaceDeclaration.class).stream()
                            .filter(c -> c.getNameAsString().equals(doc.getName()))
                            .findFirst();

            case METHOD ->
                    cu.findAll(MethodDeclaration.class).stream()
                            .filter(m ->
                                    m.getNameAsString().equals(doc.getName())
                                            && signature(m).equals(doc.getSignature())
                            )
                            .findFirst();

            case CONSTRUCTOR ->
                    cu.findAll(ConstructorDeclaration.class).stream()
                            .filter(c ->
                                    c.getNameAsString().equals(doc.getName())
                                            && signature(c).equals(doc.getSignature())
                            )
                            .findFirst();
        };
    }

    private static String signature(MethodDeclaration m) {
        return m.getParameters().stream()
                .map(p -> p.getType().asString())
                .reduce("(", (a, b) -> a.equals("(") ? "(" + b : a + ", " + b) + ")";
    }

    private static String signature(ConstructorDeclaration c) {
        return c.getParameters().stream()
                .map(p -> p.getType().asString())
                .reduce("(", (a, b) -> a.equals("(") ? "(" + b : a + ", " + b) + ")";
    }
}
