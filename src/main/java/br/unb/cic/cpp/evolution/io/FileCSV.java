package br.unb.cic.cpp.evolution.io;

import br.unb.cic.cpp.evolution.model.Observations;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FileCSV {

    private final PrintWriter pw;

    public FileCSV(String path) throws IOException  {
        pw = new PrintWriter(new FileWriter(path));
    }

    public void printHeader() {
        pw.println("project," +
                "date," +
                "revision," +
                "files," +
                "lambda," +
                "auto," +
                "decl_type," +
                "range_for," +
                "const_expr," +
                "if_with_initializer," +
                "thread_declarations," +
                "future_declarations," +
                "shared_future_declarations," +
                "promise_declarations," +
                "async," +
                "class_declarations," +
                "statements," +
                "error1," +
                "error2," +
                "error3," +
                "time");
    }

    public synchronized void printSummary(List<Observations> summary) {
        for(Observations s: summary) {
            pw.println(s.toString());
        }
    }

    public void close() {
        pw.close();
    }

}
