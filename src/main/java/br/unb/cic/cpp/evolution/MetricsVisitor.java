package br.unb.cic.cpp.evolution;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIfStatement;

public class MetricsVisitor extends ASTVisitor {

    private int lambdaExpressions = 0;
    private int auto = 0;
    private int rangeForStatement = 0;
    private int constExpr = 0;
    private int ifStatementWithInitializer = 0;

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
            rangeForStatement++;
        }
        if(statement instanceof CPPASTIfStatement) {
            CPPASTIfStatement ifStatement = (CPPASTIfStatement)statement;
            if(ifStatement.getInitializerStatement() != null) {
                ifStatementWithInitializer++;
            }
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

    public int getRangeForStatement() {
        return rangeForStatement;
    }

    public int getConstExpr() {
        return constExpr;
    }

    public int getIfStatementWithInitializer() {
        return ifStatementWithInitializer;
    }
}
