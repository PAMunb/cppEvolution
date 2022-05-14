package br.unb.cic.cpp.evolution.git;


import br.unb.cic.cpp.evolution.io.FileUtil;
import br.unb.cic.cpp.evolution.model.Observation;
import br.unb.cic.cpp.evolution.model.Observations;
import br.unb.cic.cpp.evolution.parser.CPlusPlusParser;
import br.unb.cic.cpp.evolution.parser.MetricsVisitor;
import lombok.val;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RepositoryWalker {

    private int totalCommits = 0;

    private final String project;
    private final String path;

    private final Repository repository;
    private final Set<Observation> observations;
    private final List<Observations> summary;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public RepositoryWalker(String project, String path) throws IOException {

        this.project = project;
        this.path = path;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        repository = builder.setGitDir(new File(path + "/.git")).readEnvironment().findGitDir().build();
        observations = new HashSet<>();
        summary = new ArrayList<>();
    }

    public void walk() throws IOException, GitAPIException {

        val git = new Git(repository);
        val treeName = Constants.R_HEADS + "master";

        ObjectId head = repository.resolve(Constants.HEAD);

        val commits = new TreeMap<LocalDate, ObjectId>();

        for(RevCommit c: git.log().add(repository.resolve(treeName)).call()) {
            val author = c.getAuthorIdent();
            val date = Calendar.getInstance();

            date.setTime(author.getWhen());

            val current = LocalDate.of(date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH)+1, date.get(Calendar.DAY_OF_MONTH));

            if(current.isAfter(LocalDate.of(2010, 1, 1))) {
                commits.put(current, c.toObjectId());
            }
        }

        logger.info("{} - number of commits: {}", project, commits.size());

        var previous = commits.firstKey();

        for(Map.Entry<LocalDate, ObjectId> entry : commits.entrySet()) {
            val date = entry.getKey();

            if(previous.plusDays(7).isBefore(date)) {
                val hash = commits.get(date).getName();

                logger.info("{} - revision {} {}", project, hash, date);

                try {
                    collectMetrics(head, date, commits);
                } catch(Exception ex) {
                    logger.error("{} - failed to collect metrics for revision {}", project, hash);
                }
                previous = date;
            }
        }

        totalCommits += commits.size();
    }

    private void collectMetrics(ObjectId head, LocalDate current, Map<LocalDate, ObjectId> commits) throws Exception {

        val start = System.currentTimeMillis();
        val id = commits.get(current);

        val commitSummary = new Observations();

        commitSummary.setProject(project);
        commitSummary.setDate(current);
        commitSummary.setRevision(id.name());

        Git git = new Git(repository);

        git.checkout().setName(id.getName()).call();

        int genericError = 0;
        int ioError = 0;
        int parserError = 0;

        val visitor = new MetricsVisitor();
        val files = FileUtil.listFiles(this.path);

        for(File f: files) {
            try {
                CPlusPlusParser parser = new CPlusPlusParser();
                IASTTranslationUnit unit = parser.parse(FileUtil.readContent(f));
                unit.accept(visitor);
            } catch(IOException e) {
                ioError++;
            } catch(CoreException e) {
                parserError++;
            } catch(Exception t) {
                genericError++;
            }
        }

        git.checkout().setName(head.getName()).call();

        observations.addAll(visitor.getObservations());

        commitSummary.setNumberOfLambdaExpressions(visitor.getLambdaExpressions());
        commitSummary.setNumberOfAutoDeclarations(visitor.getAuto());
        commitSummary.setNumberOfDeclType(visitor.getDeclType());
        commitSummary.setNumberOfForRangeStatements(visitor.getRangeForStatement());
        commitSummary.setNumberOfConstExpressions(visitor.getConstExpr());
        commitSummary.setNumberOfIfWithInitializerStatements(visitor.getIfStatementWithInitializer());
        commitSummary.setFiles(files.size());
        commitSummary.setNumberOfThreadDeclarations(visitor.getThreadDeclarations());
        commitSummary.setNumberOfSharedFutureDeclarations(0);
        commitSummary.setNumberOfPromiseDeclarations(visitor.getPromiseDeclarations());
        commitSummary.setNumberOfAsync(0);
        commitSummary.setNumberOfClassDeclarations(0);
        commitSummary.setNumberOfStatements(visitor.getStatements());
        commitSummary.setError(0, ioError);
        commitSummary.setError(1, parserError);
        commitSummary.setError(2, genericError);
        commitSummary.setElapsedTime(System.currentTimeMillis() - start);

        this.summary.add(commitSummary);
    }

    private List<RevCommit> heads() throws IOException {

        val commits = new ArrayList<RevCommit>();
        val head = repository.resolve("HEAD");

        commits.add(repository.parseCommit(head));

        return commits;
    }

    public List<Observations> getSummary() {
        return summary;
    }
    public Set<Observation> getObservations() { return observations; }
}
