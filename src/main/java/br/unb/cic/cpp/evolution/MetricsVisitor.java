package br.unb.cic.cpp.evolution;

import org.eclipse.cdt.core.dom.ast.*;

public class MetricsVisitor extends ASTVisitor {

    private int lambdaExpressions = 0;
    private int auto = 0;
    private int rangeFor = 0;
    private int constExpr = 0;

    public MetricsVisitor() {
        this.shouldVisitStatements = true;
        this.shouldVisitDeclarations = true;
        this.shouldVisitExpressions = true;
    }

    @Override
    public int visit(IASTExpression expression) {
        if(expression instanceof org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLambdaExpression) {
            lambdaExpressions++;
        }
        return PROCESS_CONTINUE;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
        if(declaration.getRawSignature().contains("auto ")) {
            auto++;
        }
        if(declaration.getRawSignature().contains("constexpr")) {
            constExpr++;
        }
        return PROCESS_CONTINUE;
    }

    @Override
    public int visit(IASTStatement statement) {
        if(statement instanceof org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTRangeBasedForStatement) {
            rangeFor++;
        }
        return super.visit(statement);
    }

    public void reset() {
        lambdaExpressions = 0;
        auto = 0;
    }

    public int getLambdaExpressions() {
        return lambdaExpressions;
    }

    public int getAuto() {
        return auto;
    }

    public int getRangeFor() {
        return rangeFor;
    }

    public int getConstExpr() {
        return constExpr;
    }
}
