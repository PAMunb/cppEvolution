package br.unb.cic.cpp.evolution.io;

import br.unb.cic.cpp.evolution.model.Observation;
import br.unb.cic.cpp.evolution.model.Observations;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

import static br.unb.cic.cpp.evolution.model.ObservationType.*;

public class FileCSVTest {

    @Test
    public void testHeader() {
        val receiver = new CharArrayWriter();
        val writer = new PrintWriter(receiver);

        val csv = new FileCSV(writer);

        Assert.assertNotNull(receiver.toString());
        Assert.assertFalse(receiver.toString().isEmpty());

        System.out.println(receiver);

        csv.close();
    }

    @Test
    public void testContents() {
        val receiver = new CharArrayWriter();
        val writer = new PrintWriter(receiver);

        val summary = new ArrayList<Observations>();

        for(int i = 0; i < 10; i++) {
            summary.add(new Observations("test-project", "test-revision", LocalDate.now(), i, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, new long[3], 5));
        }

        try(val csv = new FileCSV(writer)) {
            csv.print(summary);

            Assert.assertNotNull(receiver.toString());
            Assert.assertFalse(receiver.toString().isEmpty());
            Assert.assertEquals(summary.get(0).toString(), receiver.toString().split("\n")[1]);
        }

        System.out.println(receiver);
    }
}