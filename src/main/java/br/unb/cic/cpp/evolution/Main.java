package br.unb.cic.cpp.evolution;

import br.unb.cic.cpp.evolution.git.RepositoryWalker;
import br.unb.cic.cpp.evolution.io.CSVUtil;
import br.unb.cic.cpp.evolution.io.FileUtil;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        try {
            if (args.length == 0 || args[0].isEmpty()) {
                usage();
            }

            val path = args[0];
            val f = new File(path);

            if(f.exists() && f.isDirectory()) {
                val repositories = f.listFiles(File::isDirectory);
                val csv = new CSVUtil(f.getAbsolutePath() + "/../out/results.csv");

                csv.printHeader();

               if (repositories != null) {
                    for(File repository: repositories) {
                        logger.info("processing repository {}", repository.getName());

                        val walker = new RepositoryWalker(repository.getName(), repository.getAbsolutePath());

                        walker.walk();

                        csv.printSummary(walker.getSummary());
                        FileUtil.exportCode(f.getAbsolutePath() + "/../out/" + repository.getName() + ".md", walker.getObservations());
                    }
               }
                csv.close();

            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void usage() {
        logger.error("java -jar cpp-evolution.jar <path>\n");
        logger.error("\nArguments\n");
        logger.error("<path> - The path to a cplusplus git repository");
    }
}
