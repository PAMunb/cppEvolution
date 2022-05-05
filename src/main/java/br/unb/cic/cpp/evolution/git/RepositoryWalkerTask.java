package br.unb.cic.cpp.evolution.git;


import br.unb.cic.cpp.evolution.io.FileCSV;
import br.unb.cic.cpp.evolution.io.FileUtil;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Builder
@RequiredArgsConstructor
public class RepositoryWalkerTask implements Runnable {

    private final FileCSV csv;

    private final String repositoryName;
    private final String repositoryPath;
    private final String repositoryObservationsFile;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {
        logger.info("processing repository {}", repositoryName);

        try {
            val walker = new RepositoryWalker(repositoryName, repositoryPath);
            walker.walk();

            csv.print(walker.getSummary());
            FileUtil.exportCode(repositoryObservationsFile, walker.getObservations());
        } catch (Exception e) {
            logger.error("failed to create repository walker, reason {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
