package io.github.soroush.javadoc.model;

import java.nio.file.Path;
import java.util.Objects;

public final class MissingDoc {

    public enum Kind {
        CLASS,
        METHOD,
        CONSTRUCTOR
    }

    private final Path file;
    private final Kind kind;
    private final String name;
    private final String signature;
    private final int line;

    public MissingDoc(Path file,
                      Kind kind,
                      String name,
                      String signature,
                      int line) {
        this.file = Objects.requireNonNull(file, "file");
        this.kind = Objects.requireNonNull(kind, "kind");
        this.name = Objects.requireNonNull(name, "name");
        this.signature = signature; // may be null for classes
        this.line = line;
    }

    public Path getFile() {
        return file;
    }

    public Kind getKind() {
        return kind;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return switch (kind) {
            case CLASS ->
                String.format("CLASS %s (%s:%d)",
                        name, file, line);
            case METHOD, CONSTRUCTOR ->
                String.format("%s %s%s (%s:%d)",
                        kind, name,
                        signature != null ? signature : "",
                        file, line);
        };
    }
}
