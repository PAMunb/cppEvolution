package br.unb.cic.cpp.evolution;

import br.unb.cic.cpp.evolution.git.RepositoryWalkerTask;
import br.unb.cic.cpp.evolution.io.FileCSV;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            usage();
        }

        val path = args[0];
        val f = new File(path);

        if(f.exists() && f.isDirectory()) {
            val repositories = f.listFiles(File::isDirectory);

            try {
                try (val csv = new FileCSV(f.getAbsolutePath() + "/../out/results.csv")) {
                    if (repositories != null) {
                        val cores = Runtime.getRuntime().availableProcessors();
                        val pool = Executors.newFixedThreadPool(cores + 1);

                        for (File repository : repositories) {
                            val outputFile = f.getAbsolutePath() + "/../out/" + repository.getName() + ".md";
                            val walker = RepositoryWalkerTask.builder()
                                    .csv(csv)
                                    .repositoryName(repository.getName())
                                    .repositoryPath(repository.getAbsolutePath())
                                    .repositoryObservationsFile(outputFile)
                                    .build();

                            pool.submit(walker);
                        }

                        pool.shutdown();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void usage() {
        logger.error("java -jar cpp-evolution.jar <path>\n");
        logger.error("\nArguments\n");
        logger.error("<path> - The path to a/set of git repository(ies) containing c++ code");
    }
}
