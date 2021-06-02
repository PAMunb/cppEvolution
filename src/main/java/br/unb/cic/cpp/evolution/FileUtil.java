package br.unb.cic.cpp.evolution;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;

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

}
