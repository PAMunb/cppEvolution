package br.unb.cic.cpp.evolution;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

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
            Assert.assertEquals(2, visitor.getAuto());
        }
        catch(Exception e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void computeRangeForStatement() {
        try {
            File f = new File(getClass().getClassLoader().getResource("sample/basic/rangefor.cpp").getFile());
            Assert.assertNotNull(f);

            String content = FileUtil.readContent(f);
            CPPParser parser = new CPPParser();

            IASTTranslationUnit unit = parser.parse(content);

            MetricsVisitor visitor = new MetricsVisitor();
            unit.accept(visitor);

            Assert.assertEquals(1, visitor.getRangeForStatement());
        }
        catch(Exception e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void computeConstExprDeclaration() {
        try {
            File f = new File(getClass().getClassLoader().getResource("sample/basic/constexpr.cpp").getFile());
            Assert.assertNotNull(f);

            String content = FileUtil.readContent(f);
            CPPParser parser = new CPPParser();

            IASTTranslationUnit unit = parser.parse(content);

            MetricsVisitor visitor = new MetricsVisitor();
            unit.accept(visitor);

            Assert.assertEquals(3, visitor.getConstExpr());
        }
        catch(Exception e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void computeIfStatementWithInitializer() {
        try {
            File f = new File(getClass().getClassLoader().getResource("sample/basic/ifStatementWithInitializer.cpp").getFile());
            Assert.assertNotNull(f);

            String content = FileUtil.readContent(f);
            CPPParser parser = new CPPParser();

            IASTTranslationUnit unit = parser.parse(content);

            MetricsVisitor visitor = new MetricsVisitor();
            unit.accept(visitor);

            Assert.assertEquals(2, visitor.getIfStatementWithInitializer());
        }
        catch(Exception e) {
            Assert.fail(e.getMessage());
        }

    }
}
