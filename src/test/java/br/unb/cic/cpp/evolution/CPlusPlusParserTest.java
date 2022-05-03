package br.unb.cic.cpp.evolution;

import br.unb.cic.cpp.evolution.io.FileUtil;
import br.unb.cic.cpp.evolution.parser.CPlusPlusParser;
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
        }
        catch(Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testRealPrograms() {
        try {
            Collection<File> files = FileUtil.listFiles(getClass().getClassLoader().getResource("sample").getFile());
            for(File f: files) {
                String content = FileUtil.readContent(f);
                CPlusPlusParser parser = new CPlusPlusParser();
                Assert.assertNotNull((parser.parse(content)));
            }
        }
        catch(Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
