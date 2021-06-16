package br.unb.cic.cpp.evolution.io;

import br.unb.cic.cpp.evolution.model.SummaryOfObservations;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

public class CSVUtil {
    private PrintWriter pw;

    public CSVUtil(String path) throws IOException  {
        pw = new PrintWriter(new FileWriter(path));
    }

    public void printHeader() {
        pw.println("project, " +
                "date, " +
                "revision, " +
                "files, " +
                "lambda, " +
                "auto, " +
                "range_for, " +
                "const_expr, " +
                "if_with_initializer, " +
                "error1, " +
                "error2, " +
                "error3, " +
                "time");
    }

    public void printSummary(List<SummaryOfObservations> summary) {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        for(SummaryOfObservations s: summary) {
            pw.println(String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
                    s.getProject(),
                    simpleDateFormat.format(s.getDate()),
                    s.getRevision(),
                    s.getFiles(),
                    s.getNumberOfLambdaExpressions(),
                    s.getNumberOfAutoDeclarations(),
                    s.getNumberOfForRangeStatements(),
                    s.getNumberOfConstExpressions(),
                    s.getNumberOfIfWithInitializerStatements(),
                    s.getErrors()[0],
                    s.getErrors()[1],
                    s.getErrors()[2],
                    s.getElapsedTime()));
        }
    }

    public void close() {
        pw.close();
    }

}
