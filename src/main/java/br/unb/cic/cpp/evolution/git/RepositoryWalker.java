package br.unb.cic.cpp.evolution.git;

import br.unb.cic.cpp.evolution.osValidation;
import br.unb.cic.cpp.evolution.io.FileUtil;
import br.unb.cic.cpp.evolution.model.Observation;
import br.unb.cic.cpp.evolution.model.Observations;
import br.unb.cic.cpp.evolution.parser.CPlusPlusParser;
import br.unb.cic.cpp.evolution.parser.MetricsVisitor;
import lombok.val;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
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
	private List<String> commits;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public RepositoryWalker(String project, String path, List<String> commits) throws Exception {
		this(project, path);
		this.commits = commits;
	}

	public RepositoryWalker(String project, String path) throws Exception {
		this.project = project;
		this.path = path;

		FileRepositoryBuilder builder = new FileRepositoryBuilder();

		repository = builder.setGitDir(new File(path + osValidation.osBarLine() + ".git")).readEnvironment()
				.findGitDir().build();
		observations = new HashSet<>();
		summary = new ArrayList<>();
	}

	public void walk() throws Exception {

		for (String cmt : commits) {

			Git git = new Git(repository);

			Long start = System.currentTimeMillis();

			ObjectId head = repository.resolve(Constants.HEAD);

			RevWalk walk = new RevWalk(repository);
			ObjectId obj = repository.resolve(cmt);
			RevCommit commit = walk.parseCommit(obj);

			git.checkout().setName(commit.getName()).call();

			Observations commitSummary = new Observations();

			commitSummary.setProject(project);
			commitSummary.setDate(commit.getAuthorIdent().getWhen());
			commitSummary.setRevision(commit.getName());

			int genericError = 0;
			int ioError = 0;
			int parserError = 0;

			val visitor = new MetricsVisitor();
			val files = FileUtil.listFiles(this.path);

			for (File f : files) {
				try {
					CPlusPlusParser parser = new CPlusPlusParser();
					IASTTranslationUnit unit = parser.parse(FileUtil.readContent(f));
					unit.accept(visitor);
				} catch (IOException e) {
					ioError++;
				} catch (CoreException e) {
					parserError++;
				} catch (Throwable t) {
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

			Collections.sort(summary, new Comparator<Observations>() {
				public int compare(Observations o1, Observations o2) {
					return o1.getDate().compareTo(o2.getDate());
				}
			});

			walk.close();
			git.close();
		}

	}

	public void walk(Date initDate, Date endDate, int step) throws Exception {
		logger.info("processing project {}", project);

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

		for (RevCommit c : git.log().add(repository.resolve(treeName)).call()) {
			PersonIdent author = c.getAuthorIdent();
			Date current = author.getWhen();
			if (current.compareTo(initDate) >= 0 && current.compareTo(endDate) <= 0) {
				commitDates.add(current);
				commits.put(current, c.toObjectId());
			}
		}

		Collections.sort(commitDates);
		long total = commitDates.size();
		logger.info("Number of commits {} ", commits.size());

		// Collections.reverse(commitDates);
		// int max = 10;
		long traversed = 0;
		for (Date current : commitDates) {
			if (traversed % 500 == 0) {
				logger.info(" - {}: visiting commit {} of {}", project, traversed, total);
			}
			traversed++;
			if (previous == null || (diffInDays(previous, current) >= step)) {

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

		Observations commitSummary = new Observations();

		commitSummary.setProject(project);
		commitSummary.setDate(commit.getAuthorIdent().getWhen());
		commitSummary.setRevision(id.name());

		Git git = new Git(repository);

		git.checkout().setName(id.getName()).call();

		int genericError = 0;
		int ioError = 0;
		int parserError = 0;

		val visitor = new MetricsVisitor();
		val files = FileUtil.listFiles(this.path);

		for (File f : files) {
			try {
				CPlusPlusParser parser = new CPlusPlusParser();
				IASTTranslationUnit unit = parser.parse(FileUtil.readContent(f));
				unit.accept(visitor);
			} catch (IOException e) {
				ioError++;
			} catch (CoreException e) {
				parserError++;
			} catch (Throwable t) {
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

	public Set<Observation> getObservations() {
		return observations;
	}
}
