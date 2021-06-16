package br.unb.cic.cpp.evolution.model;

import java.util.Date;

public class SummaryOfObservations {
    private String project;
    private String revision;
    private Date date;
    private long numberOfLambdaExpressions;
    private long numberOfAutoDeclarations;
    private long numberOfDeclType;
    private long numberOfForRangeStatements;
    private long numberOfConstExpressions;
    private long numberOfIfWithInitializerStatements;
    private long files;
    private long[] errors = new long[] {0, 0, 0};
    private long elapsedTime;

    public long getFiles() {
        return files;
    }

    public long[] getErrors() {
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

    public long getNumberOfLambdaExpressions() {
        return numberOfLambdaExpressions;
    }

    public void setNumberOfLambdaExpressions(long numberOfLambdaExpressions) {
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

    public void setFiles(long files) {
        this.files = files;
    }

    public void setError(int idx, int errors) {
        this.errors[idx] = errors;
    }

    public void setErrors(long[] errors) {
        this.errors = errors;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getNumberOfAutoDeclarations() {
        return numberOfAutoDeclarations;
    }

    public void setNumberOfAutoDeclarations(long numberOfAutoDeclarations) {
        this.numberOfAutoDeclarations = numberOfAutoDeclarations;
    }

    public long getNumberOfForRangeStatements() {
        return numberOfForRangeStatements;
    }

    public void setNumberOfForRangeStatements(long numberOfForRangeStatements) {
        this.numberOfForRangeStatements = numberOfForRangeStatements;
    }

    public long getNumberOfConstExpressions() {
        return numberOfConstExpressions;
    }

    public void setNumberOfConstExpressions(long numberOfConstExpressions) {
        this.numberOfConstExpressions = numberOfConstExpressions;
    }

    public long getNumberOfIfWithInitializerStatements() {
        return numberOfIfWithInitializerStatements;
    }

    public void setNumberOfIfWithInitializerStatements(long numberOfIfWithInitializerStatements) {
        this.numberOfIfWithInitializerStatements = numberOfIfWithInitializerStatements;
    }

    public long getNumberOfDeclType() {
        return numberOfDeclType;
    }

    public void setNumberOfDeclType(long numberOfDeclType) {
        this.numberOfDeclType = numberOfDeclType;
    }
}
