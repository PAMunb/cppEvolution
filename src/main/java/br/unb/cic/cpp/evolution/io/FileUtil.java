package br.unb.cic.cpp.evolution.io;

import br.unb.cic.cpp.evolution.model.Observation;
import br.unb.cic.cpp.evolution.model.SummaryOfObservations;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

public class FileUtil {

    public static Collection<File> listFiles(String path) {
        File base = new File(path);
        if(base.exists() && base.isDirectory()) {
            return FileUtils.listFiles(base, new String[]{"cpp", "hpp", "h"}, true);
        }
        throw new RuntimeException(path + " is not a valid directory");
    }

    public static String readContent(File file) throws IOException  {
        return String.join("\n", Files.readAllLines(file.toPath()));
    }

    public static void exportSummary(String path, List<SummaryOfObservations> summary) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(path));

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

        for(SummaryOfObservations s: summary) {
            pw.println(String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
                    s.getProject(),
                    s.getDate(),
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
        pw.close();
    }

    public static void exportCode(String path, List<Observation> observations) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(path));

        for(Observation o: observations) {
            pw.println(String.format("#### %s \n\n", o.getType().toString().replace("_", " ")));
            pw.println("```{c}");
            pw.println(o.getCode());
            pw.println("```");
            pw.println();
        }
        pw.close();
    }
}
