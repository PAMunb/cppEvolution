package br.unb.cic.cpp.evolution;

import org.junit.Assert;
import org.junit.Test;

public class RepositoryWalkerTest {

    @Test
    public void testWalker() {
        try {
            RepositoryWalker walker = new RepositoryWalker("calligra",
                    "/Users/rbonifacio/Documents/papers/cpp+evolution/projects/calligra/");

            walker.walk();

            for(Observation o: walker.getObservations()) {
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
