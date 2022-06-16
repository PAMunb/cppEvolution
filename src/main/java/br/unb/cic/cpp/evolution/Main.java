package br.unb.cic.cpp.evolution;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
	private static String project = null;

	public static void main(final String[] args) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		int threads = 0;

		Date initialDate = formatter.parse("01-01-2010");
		Date finalDate = Calendar.getInstance().getTime();
		int step = 7;

		if (args.length == 0 || args[0].isEmpty()) {
			usage();
			System.exit(1);
		}
		for (int i = 1; i < args.length; i++) {
			if (args[i].startsWith("--threads")) {
				threads = Integer.parseInt(args[i].replace("--threads=", ""));
			} else if (args[i].startsWith("--date-init=")) {
				initialDate = formatter.parse(args[i].replace("--date-init=", ""));
			} else if (args[i].startsWith("--date-end=")) {
				finalDate = formatter.parse(args[i].replace("--date-end=", ""));
			} else if (args[i].startsWith("--step=")) {
				step = Integer.parseInt(args[i].replace("--step=", ""));
			} else if (args[i].startsWith("--project=")) {
				project = args[i].replace("--project=", "");
			}
		}

		logger.info("Init date {}", initialDate.toString());
		logger.info("End date {}", finalDate.toString());
		logger.info("Step {}", step);

		val path = args[0];
		val f = new File(path);

		if (f.exists() && f.isDirectory()) {

			File[] repositories = null;
			if (project == null) {
				repositories = f.listFiles(File::isDirectory);
			} else {
				repositories = f.listFiles(File::isDirectory);
				repositories = Arrays.stream(repositories).filter(t -> t.getName().equals(project))
						.toArray(File[]::new);
			}
			try {
				val csv = new FileCSV(f.getAbsolutePath() + osValidation.osBarLine() + ".." + osValidation.osBarLine()
						+ "out" + osValidation.osBarLine() + "results.csv");
				if (repositories != null) {
					val cores = threads == 0 ? Runtime.getRuntime().availableProcessors() : threads;
					val pool = Executors.newFixedThreadPool(cores);

					val futures = new ArrayList<Future>();

					for (File repository : repositories) {
						val outputFile = f.getAbsolutePath() + osValidation.osBarLine() + ".."
								+ osValidation.osBarLine() + "out" + osValidation.osBarLine() + repository.getName()
								+ ".md";
						val walker = RepositoryWalkerTask.builder().csv(csv).repositoryName(repository.getName())
								.repositoryPath(repository.getAbsolutePath()).repositoryObservationsFile(outputFile)
								.initDate(initialDate).endDate(finalDate).step(step).build();

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
