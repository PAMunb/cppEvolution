package br.unb.cic.cpp.evolution.git;


import br.unb.cic.cpp.evolution.io.FileUtil;
import br.unb.cic.cpp.evolution.model.Observation;
import br.unb.cic.cpp.evolution.model.SummaryOfObservations;
import br.unb.cic.cpp.evolution.parser.CPPParser;
import br.unb.cic.cpp.evolution.parser.MetricsVisitor;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RepositoryWalker {
    private String project;
    private String path;
    private int totalCommits = 0;
    private Repository repository;
    private List<Observation> observations;
    private List<SummaryOfObservations> summary;
    Logger logger = LoggerFactory.getLogger(getClass());

    public RepositoryWalker(String project, String path) throws Exception {
        this.project = project;
        this.path = path;
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(new File(path + "/.git")).readEnvironment().findGitDir().build();
        observations = new ArrayList<>();
        summary = new ArrayList<>();
    }

    public void walk() throws Exception {
        logger.info("Processing " + project);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, Calendar.JANUARY, 1);
        Date base = calendar.getTime();

        Date previous = null;

//        RevWalk walk = new RevWalk(repository);
//        walk.markStart(heads());
//
        ObjectId head = repository.resolve(Constants.HEAD);
//
//        walk.sort(RevSort.TOPO, true);
//        walk.sort(RevSort.COMMIT_TIME_DESC, true);

        Git git = new Git(repository);

        String treeName = "refs/heads/master";

        HashMap<Date, ObjectId> commits = new HashMap<>();
        List<Date> commitDates = new ArrayList<>();

        for(RevCommit c: git.log().add(repository.resolve(treeName)).call()) {
            PersonIdent author = c.getAuthorIdent();
            Date current = author.getWhen();
            if(current.compareTo(base) > 0) {
                commitDates.add(current);
                commits.put(current, c.toObjectId());
            }
        }
        Collections.sort(commitDates);
        System.out.println("Number of commits: " + commits.size());

        //Collections.reverse(commitDates);
        //int max = 10;
        for(Date current: commitDates) {
            if(previous == null || (diffInDays(previous, current) >= 7)) {
                System.out.println(" - revision " + commits.get(current).getName() + " " + current);
                collectMetrics(head, current, commits);
                previous = current;
                totalCommits++;
//                max--;
//                if(max == 0) break;
            }
        }
    }

    private void collectMetrics(ObjectId head, Date current, HashMap<Date, ObjectId> commits) throws Exception {
        Long start = System.currentTimeMillis();
        ObjectId id = commits.get(current);

        RevCommit commit = repository.parseCommit(id);

        SummaryOfObservations summary = new SummaryOfObservations();
        summary.setProject(project);
        summary.setDate(commit.getAuthorIdent().getWhen());
        summary.setRevision(id.name());

        Git git = new Git(repository);

        git.checkout().setName(id.getName()).call();

        int genericError = 0;
        int ioError = 0;
        int parserError = 0;
        Collection<File> files = FileUtil.listFiles(this.path);

        MetricsVisitor visitor = new MetricsVisitor();
        for(File f: files) {
            try {
                CPPParser parser = new CPPParser();
                IASTTranslationUnit unit = parser.parse(FileUtil.readContent(f));
                unit.accept(visitor);
            }
            catch(IOException e) {
                ioError++;
            }
            catch(CoreException e) {
                parserError++;
            }
            catch(Throwable t) {
                genericError++;
            }
        }
        git.checkout().setName(head.getName()).call();
        observations.addAll(visitor.getObservations());
        summary.setNumberOfLambdaExpressions(visitor.getLambdaExpressions());
        summary.setNumberOfAutoDeclarations(visitor.getAuto());
        summary.setNumberOfDeclType(visitor.getDeclType());
        summary.setNumberOfForRangeStatements(visitor.getRangeForStatement());
        summary.setNumberOfConstExpressions(visitor.getConstExpr());
        summary.setNumberOfIfWithInitializerStatements(visitor.getIfStatementWithInitializer());
        summary.setFiles(files.size());
        summary.setNumberOfThreadDeclarations(visitor.getThreadDeclarations());
        summary.setNumberOfSharedFutureDeclarations(0);
        summary.setNumberOfPromiseDeclarations(visitor.getPromiseDeclarations());
        summary.setNumberOfAsync(0);
        summary.setNumberOfClassDeclarations(0);
        summary.setNumberOfStatements(visitor.getStatements());
        summary.setError(0, ioError);
        summary.setError(1, parserError);
        summary.setError(2, genericError);
        summary.setElapsedTime(System.currentTimeMillis() - start);

        this.summary.add(summary);
    }

    public long diffInDays(Date previous, Date current) {
        long diff = Math.abs(previous.getTime() - current.getTime());
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private List<RevCommit> heads() throws Exception {
        List<RevCommit> commits = new ArrayList<>();
        ObjectId head = repository.resolve( "HEAD" );
        commits.add(repository.parseCommit(head));
        return commits;
    }

    public List<SummaryOfObservations> getSummary() {
        return summary;
    }
    public List<Observation> getObservations() { return observations; }
}
