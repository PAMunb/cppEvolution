package br.unb.cic.cpp.evolution.git;

import br.unb.cic.cpp.evolution.git.RepositoryWalker;
import br.unb.cic.cpp.evolution.model.Observations;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RepositoryWalkerTest {

    @Test
    public void testWalker() {
        try {
            val walker = new RepositoryWalker("calligra",
                    "/Users/rbonifacio/Documents/papers/cpp+evolution/projects/calligra/");

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date initialDate = formatter.parse("01-01-2010");
            Date finalDate = Calendar.getInstance().getTime();
            int step = 7;

            walker.walk(initialDate, finalDate, step);

            for(Observations o: walker.getSummary()) {
                System.out.println(o.toString());
            }

            Assert.assertTrue(true);
        }
        catch(Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
