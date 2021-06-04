package br.unb.cic.cpp.evolution;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class FileUtilTest {

    @Test
    public void testListFiles() {
        Collection<File> files = FileUtil.listFiles(getClass().getClassLoader().getResource("sample").getFile());
        Assert.assertEquals(16, files.size());
    }

    @Test
    public void testReadContents() {

        try {
            Collection<File> files = FileUtil.listFiles(getClass().getClassLoader().getResource("sample").getFile());
            for(File f: files) {
                String content = FileUtil.readContent(f);
            }
            Assert.assertTrue(true);
        }
        catch(IOException e) {
            Assert.fail(e.getMessage());
        }


    }
}
