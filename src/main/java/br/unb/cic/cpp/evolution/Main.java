package br.unb.cic.cpp.evolution;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.unb.cic.cpp.evolution.git.RepositoryWalkerTask;
import br.unb.cic.cpp.evolution.io.FileCSV;
import lombok.val;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            usage();
            System.exit(1);
        }

        val path = args[0];
        val f = new File(path);

        if (f.exists() && f.isDirectory()) {
            val repositories = f.listFiles(File::isDirectory);

            try {
                val csv = new FileCSV(f.getAbsolutePath() + "/../out/results.csv");
                if (repositories != null) {
                    val cores = Runtime.getRuntime().availableProcessors();
                    val pool = Executors.newFixedThreadPool(cores + 1);

                    val futures = new ArrayList<Future>();

                    for (File repository : repositories) {
                        val outputFile = f.getAbsolutePath() + "/../out/" + repository.getName() + ".md";
                        val walker = RepositoryWalkerTask.builder()
                                .csv(csv)
                                .repositoryName(repository.getName())
                                .repositoryPath(repository.getAbsolutePath()).repositoryObservationsFile(outputFile)
                                .build();

                        futures.add(pool.submit(walker));
                    }
                    // We have to synchronize all threads before a call to csv.close()
                    for (Future future : futures) {
                        future.get();
                    }
					pool.shutdown();
                    csv.close();
                }
            } catch (Exception e) {
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
