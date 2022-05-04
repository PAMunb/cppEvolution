package br.unb.cic.cpp.evolution.parser;

import br.unb.cic.cpp.evolution.io.FileUtil;
import br.unb.cic.cpp.evolution.parser.CPlusPlusParser;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

public class CPlusPlusParserTest {

    @Test
    public void testSimpleProgram() {
        try {
            String content = "int a; void test() {a++;}";
            CPlusPlusParser parser = new CPlusPlusParser();

            Assert.assertNotNull(parser.parse(content));
        } catch(Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testRealPrograms() {
        try {
            val file = getClass().getClassLoader().getResource("sample").getFile();

            Assert.assertNotNull(file);

            Collection<File> files = FileUtil.listFiles(file);

            for(File f: files) {
                val content = FileUtil.readContent(f);
                val parser = new CPlusPlusParser();

                Assert.assertNotNull(parser.parse(content));
            }
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
