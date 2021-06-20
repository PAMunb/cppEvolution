package br.unb.cic.cpp.evolution.parser;

import br.unb.cic.cpp.evolution.model.Observation;
import br.unb.cic.cpp.evolution.model.Type;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;

import java.util.HashSet;
import java.util.Set;

public class MetricsVisitor extends ASTVisitor {

    private Set<Observation> observations;
    private int classDeclarations = 0;
    private int statements = 0;

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
           else if (simpleDeclaration.getDeclSpecifier() instanceof CPPASTNamedTypeSpecifier) {
               CPPASTNamedTypeSpecifier namedTypeSpecifier = (CPPASTNamedTypeSpecifier)simpleDeclaration.getDeclSpecifier();
               if(namedTypeSpecifier.getName().toString().startsWith("thread") || namedTypeSpecifier.getName().toString().startsWith("std::thread")) {
                   observations.add(createObservation(Type.THREAD, declaration.getRawSignature()));
               }
               else if(namedTypeSpecifier.getName().toString().startsWith("promise") || namedTypeSpecifier.getName().toString().startsWith("std::promise")) {
                   observations.add(createObservation(Type.PROMISE, declaration.getRawSignature()));
               }
               else if(namedTypeSpecifier.getName().toString().startsWith("future") || namedTypeSpecifier.getName().toString().startsWith("std::future")) {
                   observations.add(createObservation(Type.FUTURE, declaration.getRawSignature()));
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
        statements++;
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

    public long getThreadDeclarations() {
        return getNumberOfObservationsOfType(Type.THREAD);
    }

    public long getFutureDeclarations() {
        return getNumberOfObservationsOfType(Type.FUTURE);
    }

    public long getSharedFutureDeclarations() {
        return getNumberOfObservationsOfType(Type.SHARED_FUTURE);
    }

    public long getPromiseDeclarations() {
        return getNumberOfObservationsOfType(Type.PROMISE);
    }

    public long getAsinc() {
        return getNumberOfObservationsOfType(Type.ASYNC);
    }

    public long getClassDeclarations() {
        return getNumberOfObservationsOfType(Type.CLASS_DECLARATION);
    }

    public long getStatements() {
        return statements;
    }

    private Observation createObservation(Type type, String code) {
        return new Observation(type, code);
    }

    public Set<Observation> getObservations() {
        return observations;
    }
}
