package br.unb.cic.cpp.evolution.parser;

import br.unb.cic.cpp.evolution.model.Observation;
import br.unb.cic.cpp.evolution.model.Type;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTFunctionDefinition;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTIfStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;

import java.util.HashSet;
import java.util.Set;

public class MetricsVisitor extends ASTVisitor {

    private Set<Observation> observations;


    public MetricsVisitor() {
        this.shouldVisitStatements = true;
        this.shouldVisitDeclarations = true;
        this.shouldVisitExpressions = true;

        observations = new HashSet<>();
    }

    @Override
    public int visit(IASTExpression expression) {
        if(expression instanceof org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTLambdaExpression) {
            observations.add(createObservation(Type.LAMBDA_EXPRESSION, expression.getRawSignature()));
        }
        return PROCESS_CONTINUE;
    }

    @Override
    public int visit(IASTDeclaration declaration) {
       if(declaration instanceof CPPASTSimpleDeclaration) {
           CPPASTSimpleDeclaration simpleDeclaration = (CPPASTSimpleDeclaration)declaration;
           if(simpleDeclaration.getDeclSpecifier() instanceof CPPASTSimpleDeclSpecifier) {
               CPPASTSimpleDeclSpecifier declSpecifier = (CPPASTSimpleDeclSpecifier)simpleDeclaration.getDeclSpecifier();
               if(declSpecifier.getType() == CPPASTSimpleDeclSpecifier.t_auto) {
                   observations.add(createObservation(Type.AUTO, declaration.getRawSignature()));
               }
               if(declSpecifier.isConstexpr()) {
                   observations.add(createObservation(Type.CONST_EXPRESSION, declaration.getRawSignature()));
               }
               if(declSpecifier.getDeclTypeExpression() != null || declSpecifier.getType() == CPPASTSimpleDeclSpecifier.t_decltype) {
                   observations.add(createObservation(Type.DECLTYPE, declaration.getRawSignature()));
               }
           }
       }
       else if(declaration instanceof CPPASTFunctionDefinition) {
           CPPASTFunctionDefinition functionDefinition = (CPPASTFunctionDefinition)declaration;
           if(functionDefinition.getDeclSpecifier() instanceof  CPPASTSimpleDeclSpecifier) {
               CPPASTSimpleDeclSpecifier declSpecifier = (CPPASTSimpleDeclSpecifier)functionDefinition.getDeclSpecifier();
               if(declSpecifier.getType() == CPPASTSimpleDeclSpecifier.t_auto) {
                   observations.add(createObservation(Type.AUTO, declaration.getRawSignature()));
               }
           }
       }
       return PROCESS_CONTINUE;
    }

    @Override
    public int visit(IASTStatement statement) {
        if(statement instanceof org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTRangeBasedForStatement) {
            observations.add(createObservation(Type.RANGE_FOR_STATEMENT, statement.getRawSignature()));
        }
        if(statement instanceof CPPASTIfStatement) {
            CPPASTIfStatement ifStatement = (CPPASTIfStatement)statement;
            if(ifStatement.getInitializerStatement() != null) {
                observations.add(createObservation(Type.IF_STATEMENT_WITH_INITIALIZER, statement.getRawSignature()));
            }
        }
        return super.visit(statement);
    }

    public void reset() {
        observations.clear();
    }

    private long getNumberOfObservationsOfType(Type type) {
        return observations
                .stream()
                .filter(o -> o.getType().equals(type))
                .count();
    }

    public long getLambdaExpressions() {
        return getNumberOfObservationsOfType(Type.LAMBDA_EXPRESSION);
    }

    public long getAuto() {
        return getNumberOfObservationsOfType(Type.AUTO);
    }

    public long getDeclType() {
        return getNumberOfObservationsOfType(Type.DECLTYPE);
    }

    public long getRangeForStatement() {
        return getNumberOfObservationsOfType(Type.RANGE_FOR_STATEMENT);
    }

    public long getConstExpr() {
        return getNumberOfObservationsOfType(Type.CONST_EXPRESSION);
    }

    public long getIfStatementWithInitializer() {
        return getNumberOfObservationsOfType(Type.IF_STATEMENT_WITH_INITIALIZER);
    }

    private Observation createObservation(Type type, String code) {
        return new Observation(type, code);
    }

    public Set<Observation> getObservations() {
        return observations;
    }
}
