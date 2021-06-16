package br.unb.cic.cpp.evolution;

import br.unb.cic.cpp.evolution.git.RepositoryWalker;
import br.unb.cic.cpp.evolution.io.CSVUtil;
import br.unb.cic.cpp.evolution.io.FileUtil;

import java.io.File;

public class Main {
    public static void main(String args[]) {
        try {
            String path = args[0];

            File f = new File(path);
            if(f.exists() && f.isDirectory()) {
                File[] repositories = f.listFiles(File::isDirectory);
                CSVUtil csv = new CSVUtil(f.getAbsolutePath() + "/../out/results.csv");
                csv.printHeader();

                for(File repository: repositories) {
                    System.out.println("processing " + repository.getName());

                    RepositoryWalker walker = new RepositoryWalker(repository.getName(), repository.getAbsolutePath());

                    walker.walk();

                    csv.printSummary(walker.getSummary());
                    FileUtil.exportCode(f.getAbsolutePath() + "/../out/" + repository.getName() + ".md", walker.getObservations());
                }
                csv.close();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
