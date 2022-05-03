package br.unb.cic.cpp.evolution.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Observations {

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

    public String toString() {
        val pattern = "yyyy-MM-dd";
        val simpleDateFormat = new SimpleDateFormat(pattern);

        return  project + ","
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

    public void setError(final int i, final int value) {
        if (i > 3 || i < 0) {
            return;
        }

        errors[i] = value;
    }
}
