package br.unb.cic.cpp.evolution;

import br.unb.cic.cpp.evolution.io.FileUtil;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class FileUtilTest {

    @Test
    public void testListFiles() {
        val file = getClass().getClassLoader().getResource("sample").getFile();

        Assert.assertNotNull(file);

        val files = FileUtil.listFiles(file);

        Assert.assertEquals(16, files.size());
    }

    @Test
    public void testReadContents() {
        try {
            val file = getClass().getClassLoader().getResource("sample").getFile();

            Assert.assertNotNull(file);

            val files = FileUtil.listFiles(file);

            for(File f: files) {
                val contents = FileUtil.readContent(f);

                Assert.assertNotNull(contents);
                Assert.assertFalse(contents.isEmpty());
            }
        } catch(IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
