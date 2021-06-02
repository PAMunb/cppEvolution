package br.unb.cic.cpp.evolution;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;

public class MetricsVisitor extends ASTVisitor {

    private int lambdaExpressions = 0;
    private int allExpressions;
    private int typeInference = 0;

    public MetricsVisitor() {
        this.shouldVisitDeclarations = true;
        this.shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if(expression instanceof org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLambdaExpression) {
            lambdaExpressions++;
        }
        allExpressions++;
        return PROCESS_CONTINUE;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        return PROCESS_CONTINUE;
    }

    public void reset() {
        lambdaExpressions = 0;
        typeInference = 0;
    }

    public int getLambdaExpressions() {
        return lambdaExpressions;
    }

    public int getAllExpressions() {
        return allExpressions;
    }
}
