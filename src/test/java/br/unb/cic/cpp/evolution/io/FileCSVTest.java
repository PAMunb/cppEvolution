package br.unb.cic.cpp.evolution.io;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

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
}