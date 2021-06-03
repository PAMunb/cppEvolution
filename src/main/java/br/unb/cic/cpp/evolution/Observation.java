package br.unb.cic.cpp.evolution;

import java.util.Date;

public class Observation {
    private String project;
    private String revision;
    private Date date;
    private int numberOfLambdaExpressions;
    private int files;
    private int[] errors = new int[] {0, 0, 0};
    private long elapsedTime;

    public int getFiles() {
        return files;
    }

    public int[] getErrors() {
        return errors;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumberOfLambdaExpressions() {
        return numberOfLambdaExpressions;
    }

    public void setNumberOfLambdaExpressions(int numberOfLambdaExpressions) {
        this.numberOfLambdaExpressions = numberOfLambdaExpressions;
    }

    public String toString() {
        return date.toString()
                + " Lambda Expressions " + numberOfLambdaExpressions + ", "
                + " errors ("
                + errors[0] + ", "
                + errors[1] + ", "
                + errors[2] + ") "
                + elapsedTime / 1000.0 + "s";
    }

    public void setFiles(int files) {
        this.files = files;
    }

    public void setError(int idx, int errors) {
        this.errors[idx] = errors;
    }

    public void setErrors(int[] errors) {
        this.errors = errors;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
