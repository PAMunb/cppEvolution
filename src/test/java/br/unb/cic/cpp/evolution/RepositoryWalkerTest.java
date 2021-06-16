package br.unb.cic.cpp.evolution;

import br.unb.cic.cpp.evolution.git.RepositoryWalker;
import br.unb.cic.cpp.evolution.model.SummaryOfObservations;
import org.junit.Assert;
import org.junit.Test;

public class RepositoryWalkerTest {

    @Test
    public void testWalker() {
        try {
            RepositoryWalker walker = new RepositoryWalker("calligra",
                    "/Users/rbonifacio/Documents/papers/cpp+evolution/projects/calligra/");

            walker.walk();

            for(SummaryOfObservations o: walker.getSummary()) {
                System.out.println(o.toString());
            }

            Assert.assertTrue(true);
        }
        catch(Exception e) {
            e.printStackTrace();
            Assert.fail();
            //Assert.fail(e.getMessage());
        }
    }
}
