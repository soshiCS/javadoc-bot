package io.github.soroush.javadoc.model;

import java.nio.file.Path;
import java.util.Objects;

/**
 * /**
 *  TODO: add documentation.
 * /
 */
public final class MissingDoc {

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    public enum Kind {

        CLASS, INTERFACE, ENUM, METHOD, CONSTRUCTOR
    }

    private final Path file;

    private final Kind kind;

    private final String name;

    private final String signature;

    private final int line;

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    public MissingDoc(Path file, Kind kind, String name, String signature, int line) {
        this.file = Objects.requireNonNull(file, "file");
        this.kind = Objects.requireNonNull(kind, "kind");
        this.name = Objects.requireNonNull(name, "name");
        this.signature = signature;
        this.line = line;
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    public Path getFile() {
        return file;
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    public String getName() {
        return name;
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    public String getSignature() {
        return signature;
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    public int getLine() {
        return line;
    }

    /**
     * /**
     *  TODO: add documentation.
     * /
     */
    @Override
    public String toString() {
        return switch(kind) {
            case CLASS, INTERFACE, ENUM ->
                String.format("%s %s (%s:%d)", kind, name, file, line);
            case METHOD, CONSTRUCTOR ->
                String.format("%s %s%s (%s:%d)", kind, name, signature != null ? signature : "", file, line);
        };
    }
}
