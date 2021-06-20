package br.unb.cic.cpp.evolution.model;

import java.text.SimpleDateFormat;
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
    private long numberOfThreadDeclarations;
    private long numberOfFutureDeclarations;
    private long numberOfSharedFutureDeclarations;
    private long numberOfPromiseDeclarations;
    private long numberOfAsync;
    private long numberOfClassDeclarations;
    private long numberOfStatements;
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
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return    project + ","
                + simpleDateFormat.format(date) + ","
                + revision + ","
                + files + ","
                + numberOfLambdaExpressions + ","
                + numberOfAutoDeclarations  + ","
                + numberOfDeclType + ","
                + numberOfForRangeStatements + ","
                + numberOfConstExpressions + ","
                + numberOfIfWithInitializerStatements + ","
                + numberOfThreadDeclarations + ","
                + numberOfFutureDeclarations + ","
                + numberOfSharedFutureDeclarations + ","
                + numberOfPromiseDeclarations + ","
                + numberOfAsync + ","
                + numberOfClassDeclarations + ","
                + numberOfStatements + ","
                + errors[0] + ","
                + errors[1] + ","
                + errors[2] + ","
                + elapsedTime;
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

    public long getNumberOfThreadDeclarations() {
        return numberOfThreadDeclarations;
    }

    public void setNumberOfThreadDeclarations(long numberOfThreadDeclarations) {
        this.numberOfThreadDeclarations = numberOfThreadDeclarations;
    }

    public long getNumberOfFutureDeclarations() {
        return numberOfFutureDeclarations;
    }

    public void setNumberOfFutureDeclarations(long numberOfFutureDeclarations) {
        this.numberOfFutureDeclarations = numberOfFutureDeclarations;
    }

    public long getNumberOfSharedFutureDeclarations() {
        return numberOfSharedFutureDeclarations;
    }

    public void setNumberOfSharedFutureDeclarations(long numberOfSharedFutureDeclarations) {
        this.numberOfSharedFutureDeclarations = numberOfSharedFutureDeclarations;
    }

    public long getNumberOfPromiseDeclarations() {
        return numberOfPromiseDeclarations;
    }

    public void setNumberOfPromiseDeclarations(long numberOfPromiseDeclarations) {
        this.numberOfPromiseDeclarations = numberOfPromiseDeclarations;
    }

    public long getNumberOfAsync() {
        return numberOfAsync;
    }

    public void setNumberOfAsync(long numberOfAsync) {
        this.numberOfAsync = numberOfAsync;
    }

    public long getNumberOfClassDeclarations() {
        return numberOfClassDeclarations;
    }

    public void setNumberOfClassDeclarations(long numberOfClassDeclarations) {
        this.numberOfClassDeclarations = numberOfClassDeclarations;
    }

    public long getNumberOfStatements() {
        return numberOfStatements;
    }

    public void setNumberOfStatements(long numberOfStatements) {
        this.numberOfStatements = numberOfStatements;
    }
}
