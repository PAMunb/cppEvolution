package br.unb.cic.cpp.evolution.io;

import br.unb.cic.cpp.evolution.model.Observations;
import lombok.val;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class FileCSV implements Closeable {

    private PrintWriter pw;

    private final Map<String, List<String>> entries = new LinkedHashMap<>();

    public FileCSV(final PrintWriter writer) {
        pw = writer;

        init();
    }

    public FileCSV(final String path) throws IOException  {
        pw = new PrintWriter(new FileWriter(path));

        init();
    }

    public synchronized void print(List<Observations> summary) {
        for(Observations s: summary) {
            pw.println(s.toString());
        }
    }

    public void close() {
        pw.close();
    }

    private void init() {
        entries.put("project", new Vector<>());
        entries.put("date", new Vector<>());
        entries.put("revision", new Vector<>());
        entries.put("files", new Vector<>());
        entries.put("lambda", new Vector<>());
        entries.put("auto", new Vector<>());
        entries.put("decl_type", new Vector<>());
        entries.put("range_for", new Vector<>());
        entries.put("const_expr", new Vector<>());
        entries.put("if_with_initializer", new Vector<>());
        entries.put("thread_declarations", new Vector<>());
        entries.put("future_declarations", new Vector<>());
        entries.put("shared_future_declarations", new Vector<>());
        entries.put("promise_declarations", new Vector<>());
        entries.put("async", new Vector<>());
        entries.put("class_declarations", new Vector<>());
        entries.put("statements", new Vector<>());
        entries.put("error1", new Vector<>());
        entries.put("error2", new Vector<>());
        entries.put("error3", new Vector<>());
        entries.put("time", new Vector<>());

        val keys = String.join(",", entries.keySet());

        pw.println(keys);
    }

}
