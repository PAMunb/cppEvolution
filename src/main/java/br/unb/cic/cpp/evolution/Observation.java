package br.unb.cic.cpp.evolution;

public class Observation {

    enum Type {
        LAMBDA_EXPRESSION,
        AUTO,
        RANGE_FOR_STATEMENT,
        CONST_EXPRESSION,
        IF_STATEMENT_WITH_INITIALIZER
    }

    private Type type;
    private String code;
    private String file;
    private String revision;

    public Observation(Type type, String code) {
        this.type = type;
        this.code = code;
        this.file = file;
        this.revision = revision;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }
}
