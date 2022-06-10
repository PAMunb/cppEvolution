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
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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

    public RepositoryWalker(String project, String path) throws Exception {
        this.project = project;
        this.path = path;

        FileRepositoryBuilder builder = new FileRepositoryBuilder();

        repository = builder.setGitDir(new File(path + "/.git")).readEnvironment().findGitDir().build();
        observations = new HashSet<>();
        summary = new ArrayList<>();
    }

    public void walk(Date initDate, Date endDate, int step) throws Exception {
        logger.info("processing project {}", project);

        Date previous = null;

        String treeName = "refs/heads/master";
        ObjectId head = repository.resolve(Constants.HEAD);

        try(RevWalk revWalk = new RevWalk(repository)) {
            ObjectId commitId = repository.resolve(treeName);
            revWalk.markStart( revWalk.parseCommit( commitId ) );

            long traversed = 0;
            for( RevCommit commit: revWalk) {
                if(traversed % 500 == 0) {
                    logger.info(" - {}: visiting commit {}",  project, traversed);
                }
                traversed++;
                collectMetrics(head, commit);
            }
        }
    }

    private void collectMetrics(ObjectId head, RevCommit commit) throws Exception {
        Long start = System.currentTimeMillis();

        Observations commitSummary = new Observations();

        commitSummary.setProject(project);
        commitSummary.setDate(commit.getAuthorIdent().getWhen());
        commitSummary.setRevision(commit.getId().name());

        Git git = new Git(repository);

        git.checkout().setName(commit.getId().getName()).call();

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
            } catch(Throwable t) {
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

    public long diffInDays(Date previous, Date current) {
        long diff = Math.abs(previous.getTime() - current.getTime());
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
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
