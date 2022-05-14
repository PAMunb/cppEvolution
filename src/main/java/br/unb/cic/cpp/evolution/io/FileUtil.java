package br.unb.cic.cpp.evolution.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import br.unb.cic.cpp.evolution.model.Observation;
import br.unb.cic.cpp.evolution.model.Observations;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtil {
    public static Collection<File> listFiles(final String path) {
        val base = new File(path);

        if (base.exists() && base.isDirectory()) {
            return FileUtils.listFiles(base, new String[]{"cpp", "hpp", "h"}, true);
        }

        throw new IllegalArgumentException(path + " is not a valid directory");
    }

    public static String readContent(File file) throws IOException  {
        return String.join("\n", Files.readAllLines(file.toPath()));
    }

    public static void exportSummary(String path, List<Observations> summary) throws IOException {
        try (val pw = new PrintWriter(new FileWriter(path))) {
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

            for (Observations s : summary) {
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
        }
    }

    public static void exportCode(String path, Set<Observation> observations) throws IOException {
        try (val pw = new PrintWriter(new FileWriter(path))) {
            for (Observation o : observations) {
                pw.println(String.format("#### %s \n\n", o.getType().toString().replace("_", " ")));
                pw.println("```{c}");
                pw.println(o.getCode());
                pw.println("```");
                pw.println();
            }
        }
    }
}
