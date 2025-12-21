package io.github.soroush.javadoc.llm;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import io.github.soroush.javadoc.model.MissingDoc;
import java.util.Optional;
import static io.github.soroush.javadoc.model.MissingDoc.Kind;

/**
 * /**
 *  TODO: add documentation.
 * /
 */
public final class PromptBuilder {

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private PromptBuilder() {
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    public static String buildPrompt(CompilationUnit cu, MissingDoc doc) {
        return switch(doc.getKind()) {
            case CLASS, INTERFACE ->
                buildClassPrompt(cu, doc);
            case ENUM ->
                buildEnumPrompt(cu, doc);
            case METHOD ->
                buildMethodPrompt(cu, doc);
            case CONSTRUCTOR ->
                buildConstructorPrompt(cu, doc);
        };
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private static String buildClassPrompt(CompilationUnit cu, MissingDoc doc) {
        Optional<ClassOrInterfaceDeclaration> cls = cu.findFirst(ClassOrInterfaceDeclaration.class, c -> c.getNameAsString().equals(doc.getName()));
        if (cls.isEmpty()) {
            throw new IllegalStateException("Class not found for " + doc);
        }
        ClassOrInterfaceDeclaration c = cls.get();
        String kind = c.isInterface() ? "interface" : "class";
        return """
            Write a concise JavaDoc comment for the following Java %s.
            
            Name:
            %s
            
            Rules:
            - Do not guess behavior not visible in the name.
            - Do not mention implementation details.
            - Keep it short and factual.
            - Return ONLY the JavaDoc comment.
            """.formatted(kind, c.getNameAsString());
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private static String buildEnumPrompt(CompilationUnit cu, MissingDoc doc) {
        Optional<EnumDeclaration> en = cu.findFirst(EnumDeclaration.class, e -> e.getNameAsString().equals(doc.getName()));
        if (en.isEmpty()) {
            throw new IllegalStateException("Enum not found for " + doc);
        }
        return """
            Write a concise JavaDoc comment for the following Java enum.
            
            Name:
            %s
            
            Rules:
            - Do not guess behavior not visible in the name.
            - Do not mention implementation details.
            - Keep it short and factual.
            - Return ONLY the JavaDoc comment.
            """.formatted(en.get().getNameAsString());
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private static String buildMethodPrompt(CompilationUnit cu, MissingDoc doc) {
        Optional<MethodDeclaration> method = cu.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals(doc.getName()) && signature(m).equals(doc.getSignature()));
        if (method.isEmpty()) {
            throw new IllegalStateException("Method not found for " + doc);
        }
        MethodDeclaration m = method.get();
        return """
            Write a concise JavaDoc comment for the following Java method.
            
            Enclosing class:
            %s
            
            Method:
            %s
            
            Method body:
            %s
            
            Rules:
            - Do not guess behavior not visible in the code.
            - Do not mention implementation details.
            - Include @param and @return tags if applicable.
            - Keep it short and factual.
            - Return ONLY the JavaDoc comment.
            """.formatted(enclosingClassName(m), m.getDeclarationAsString(), m.getBody().map(Object::toString).orElse("{}"));
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private static String buildConstructorPrompt(CompilationUnit cu, MissingDoc doc) {
        Optional<ConstructorDeclaration> ctor = cu.findFirst(ConstructorDeclaration.class, c -> c.getNameAsString().equals(doc.getName()) && signature(c).equals(doc.getSignature()));
        if (ctor.isEmpty()) {
            throw new IllegalStateException("Constructor not found for " + doc);
        }
        ConstructorDeclaration c = ctor.get();
        return """
            Write a concise JavaDoc comment for the following Java constructor.
            
            Enclosing class:
            %s
            
            Constructor:
            %s
            
            Constructor body:
            %s
            
            Rules:
            - Do not guess behavior not visible in the code.
            - Include @param tags if applicable.
            - Keep it short and factual.
            - Return ONLY the JavaDoc comment.
            """.formatted(enclosingClassName(c), c.getDeclarationAsString(), c.getBody().toString());
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private static String enclosingClassName(MethodDeclaration m) {
        return m.findAncestor(ClassOrInterfaceDeclaration.class).map(ClassOrInterfaceDeclaration::getNameAsString).orElse("<unknown>");
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    private static String enclosingClassName(ConstructorDeclaration c) {
        return c.findAncestor(ClassOrInterfaceDeclaration.class).map(ClassOrInterfaceDeclaration::getNameAsString).orElse("<unknown>");
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
