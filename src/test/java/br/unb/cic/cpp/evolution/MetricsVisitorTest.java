package br.unb.cic.cpp.evolution;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class MetricsVisitorTest {

    @Test
    public void computeLambdaExpressions() {
        try {
            File f = new File(getClass().getClassLoader().getResource("sample/basic/lambda.cpp").getFile());
            Assert.assertNotNull(f);

            String content = FileUtil.readContent(f);
            CPPParser parser = new CPPParser();

            IASTTranslationUnit unit = parser.parse(content);

            MetricsVisitor visitor = new MetricsVisitor();
            unit.accept(visitor);

            Assert.assertEquals(1, visitor.getLambdaExpressions());
        }
        catch(Exception e) {
            Assert.fail(e.getMessage());
        }

    }
}
