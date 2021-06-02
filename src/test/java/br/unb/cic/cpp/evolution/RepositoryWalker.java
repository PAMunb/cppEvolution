package br.unb.cic.cpp.evolution;


import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RepositoryWalker {


    private String project;
    private String path;
    private int totalCommits = 0;
    private Repository repository;
    private List<Observation> observations;

    // private static final Date baseDate = Calendar.getInstance().set(2010, 01, 01);

    public RepositoryWalker(String project, String path) throws Exception {
        this.project = project;
        this.path = path;
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(new File(path + ".git")).readEnvironment().findGitDir().build();
        observations = new ArrayList<>();
    }

    public void walk() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2010, Calendar.JANUARY, 1);
        Date base = calendar.getTime();

        Date previous = null;

        RevWalk walk = new RevWalk(repository);
        walk.markStart(heads());

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
        int max = 100;
        for(Date current: commitDates) {
            if(previous == null || (diffInDays(previous, current) >= 7)) {
                collectMetrics(current, commits);
                previous = current;
                totalCommits++;
                max--;
                if(max == 0) break;
            }
        }
    }

    private void collectMetrics(Date current, HashMap<Date, ObjectId> commits) throws Exception {
        ObjectId id = commits.get(current);

        RevCommit commit = repository.parseCommit(id);

        Observation o = new Observation();
        o.setProject(project);
        o.setDate(commit.getAuthorIdent().getWhen());
        o.setRevision(id.name());

        // Git git = new Git(repository);

        // git.checkout().setName(id.getName()).call();

        Collection<File> files = FileUtil.listFiles(this.path);
        MetricsVisitor visitor = new MetricsVisitor();
        for(File f: files) {
            CPPParser parser = new CPPParser();
            IASTTranslationUnit unit = parser.parse(FileUtil.readContent(f));
            unit.accept(visitor);
        }
        o.setNumberOfLambdaExpressions(visitor.getLambdaExpressions());
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

    public List<Observation> getObservations() {
        return observations;
    }
}
