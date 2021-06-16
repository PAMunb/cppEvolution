package br.unb.cic.cpp.evolution.model;

public class Observation {

    private Type type;
    private String code;

    public Observation(Type type, String code) {
        this.type = type;
        this.code = code;
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
}
