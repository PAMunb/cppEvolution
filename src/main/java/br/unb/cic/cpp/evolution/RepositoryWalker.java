package br.unb.cic.cpp.evolution;


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
    private List<SummaryOfObservations> observations;
    Logger logger = LoggerFactory.getLogger(getClass());



    // private static final Date baseDate = Calendar.getInstance().set(2010, 01, 01);

    public RepositoryWalker(String project, String path) throws Exception {
        this.project = project;
        this.path = path;
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(new File(path + ".git")).readEnvironment().findGitDir().build();
        observations = new ArrayList<>();
    }

    public void walk() throws Exception {
        logger.info("Processing " + project);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, Calendar.JANUARY, 1);
        Date base = calendar.getTime();

        Date previous = null;

        RevWalk walk = new RevWalk(repository);
        walk.markStart(heads());

        ObjectId head = repository.resolve(Constants.HEAD);

        walk.sort(RevSort.TOPO, true);
        walk.sort(RevSort.COMMIT_TIME_DESC, true);

        HashMap<Date, ObjectId> commits = new HashMap<>();
        List<Date> commitDates = new ArrayList<>();

        for(RevCommit c: walk) {
            PersonIdent author = c.getAuthorIdent();
            Date current = author.getWhen();
            if(current.compareTo(base) > 0) {
                commitDates.add(current);
                commits.put(current, c.toObjectId());
            }
        }
        Collections.sort(commitDates);
        Collections.reverse(commitDates);
        int max = 1;
        for(Date current: commitDates) {
            if(previous == null || (diffInDays(previous, current) >= 7)) {
                logger.info(" - revision " + commits.get(current).getName() + " " + current);
                collectMetrics(head, current, commits);
                previous = current;
                totalCommits++;
                max--;
                if(max == 0) break;
            }
        }
    }

    private void collectMetrics(ObjectId head, Date current, HashMap<Date, ObjectId> commits) throws Exception {
        Long start = System.currentTimeMillis();
        ObjectId id = commits.get(current);

        RevCommit commit = repository.parseCommit(id);

        SummaryOfObservations o = new SummaryOfObservations();
        o.setProject(project);
        o.setDate(commit.getAuthorIdent().getWhen());
        o.setRevision(id.name());

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
        o.setNumberOfLambdaExpressions(visitor.getLambdaExpressions());
        o.setFiles(files.size());
        o.setError(0, ioError);
        o.setError(1, parserError);
        o.setError(2, genericError);
        o.setElapsedTime(System.currentTimeMillis() - start);

        observations.add(o);
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

    public List<SummaryOfObservations> getObservations() {
        return observations;
    }
}
