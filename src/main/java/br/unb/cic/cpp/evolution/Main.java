package br.unb.cic.cpp.evolution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
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

        metrify(f);
    }

    public static void usage() {
        logger.error("java -jar cpp-evolution.jar <path>");
        logger.error("Arguments");
        logger.error("<path> - The path to a/set of git repository(ies) containing c++ code");
    }

    public static void metrify(final File f) {

        if (f.exists() && f.isDirectory()) {
            val repositories = f.listFiles(File::isDirectory);
            assert repositories != null;

            try (val csv = new FileCSV(f.getAbsolutePath() + "/../out/results.csv")) {
                val cores = Runtime.getRuntime().availableProcessors();
                val pool = Executors.newFixedThreadPool(cores + 1);

                val futures = new ArrayList<Future<?>>();

                for (File repository : repositories) {
                    val outputFile = f.getAbsolutePath() + "/../out/" + repository.getName() + ".md";
                    val walker = RepositoryWalkerTask.builder()
                            .csv(csv)
                            .repositoryName(repository.getName())
                            .repositoryPath(repository.getAbsolutePath())
                            .repositoryObservationsFile(outputFile)
                            .build();

                    futures.add(pool.submit(walker));
                }

                for (Future<?> future : futures) {
                    future.get();
                }

                pool.shutdown();
            } catch(IOException e) {
                logger.error("failed to create a CSV, reason {}", e.getMessage());
                e.printStackTrace();
            } catch (ExecutionException | InterruptedException e) {
                logger.error("failed to complete a future task, reason {}", e.getMessage());
                e.printStackTrace();

                Thread.currentThread().interrupt();
            }
        }
    }
}
