package br.unb.cic.cpp.evolution;

import java.util.Date;

public class Observation {
    private String project;
    private String revision;
    private Date date;
    private int numberOfLambdaExpressions;

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
        return date.toString() + " - " + numberOfLambdaExpressions;
    }
}
