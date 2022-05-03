package br.unb.cic.cpp.evolution.parser;

import br.unb.cic.cpp.evolution.model.Observation;
import br.unb.cic.cpp.evolution.model.ObservationType;
import lombok.Getter;
import lombok.val;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class MetricsVisitor extends ASTVisitor {

    @Getter
    private int statements = 0;

    @Getter
    private final Set<Observation> observations;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public MetricsVisitor() {
        this.shouldVisitStatements = true;
        this.shouldVisitDeclarations = true;
        this.shouldVisitExpressions = true;

        observations = new HashSet<>();
    }

    @Override
    public int visit(IASTExpression expression) {

        if(expression instanceof CPPASTLambdaExpression) {
            observations.add(createObservation(ObservationType.LAMBDA_EXPRESSION, expression.getRawSignature()));
        }

        return PROCESS_CONTINUE;
    }

    @Override
    public int visit(final IASTDeclaration declaration) {

        val isSimpleDeclaration = declaration instanceof CPPASTSimpleDeclaration;
        val isFunctionDefinition = declaration instanceof CPPASTFunctionDefinition;

        if (isSimpleDeclaration) {
            return visitSimpleDeclaration((CPPASTSimpleDeclaration)declaration);
        } else if (isFunctionDefinition)  {
            return visitFunction((CPPASTFunctionDefinition)declaration);
        }

       return PROCESS_CONTINUE;
    }

    @Override
    public int visit(IASTStatement statement) {

        if(statement instanceof CPPASTRangeBasedForStatement) {
            observations.add(createObservation(ObservationType.RANGE_FOR_STATEMENT, statement.getRawSignature()));
        }

        if(statement instanceof CPPASTIfStatement) {
            val ifStatement = (CPPASTIfStatement)statement;
            if(ifStatement.getInitializerStatement() != null) {
                observations.add(createObservation(ObservationType.IF_STATEMENT_WITH_INITIALIZER, statement.getRawSignature()));
            }
        }

        statements++;

        return super.visit(statement);
    }

    public int visitSimpleDeclaration(final CPPASTSimpleDeclaration declaration) {

        if(declaration.getDeclSpecifier() instanceof CPPASTSimpleDeclSpecifier) {
            val declSpecifier = (CPPASTSimpleDeclSpecifier)declaration.getDeclSpecifier();

            if(declSpecifier.getType() == IASTSimpleDeclSpecifier.t_auto) {
                observations.add(createObservation(ObservationType.AUTO, declaration.getRawSignature()));
            }
            if(declSpecifier.isConstexpr()) {
                observations.add(createObservation(ObservationType.CONST_EXPRESSION, declaration.getRawSignature()));
            }
            if(declSpecifier.getDeclTypeExpression() != null || declSpecifier.getType() == IASTSimpleDeclSpecifier.t_decltype) {
                observations.add(createObservation(ObservationType.DECLTYPE, declaration.getRawSignature()));
            }
        } else if (declaration.getDeclSpecifier() instanceof CPPASTNamedTypeSpecifier) {
            val typeSpecifier = (CPPASTNamedTypeSpecifier)declaration.getDeclSpecifier();
            val typeSpecifierName = typeSpecifier.getName().toString();

            switch (typeSpecifierName) {
                case "thread":
                case "std::thread":
                    observations.add(createObservation(ObservationType.THREAD, declaration.getRawSignature()));
                    break;
                case "promise":
                case "std::promise":
                    observations.add(createObservation(ObservationType.PROMISE, declaration.getRawSignature()));
                    break;
                case "future":
                case "std::future":
                    observations.add(createObservation(ObservationType.FUTURE, declaration.getRawSignature()));
                    break;
                default:
                    logger.debug("type specifier {} is not being captured", typeSpecifierName);
            }
        }

        return PROCESS_CONTINUE;
    }

    public int visitFunction(final CPPASTFunctionDefinition declaration) {

        if(declaration.getDeclSpecifier() instanceof  CPPASTSimpleDeclSpecifier) {
            CPPASTSimpleDeclSpecifier declSpecifier = (CPPASTSimpleDeclSpecifier)declaration.getDeclSpecifier();
            if(declSpecifier.getType() == IASTSimpleDeclSpecifier.t_auto) {
                observations.add(createObservation(ObservationType.AUTO, declaration.getRawSignature()));
            }
        }

        return PROCESS_CONTINUE;
    }

    public void reset() {
        observations.clear();
    }

    private long getNumberOfObservationsOfType(ObservationType type) {
        return observations
                .stream()
                .filter(o -> o.getType().equals(type))
                .count();
    }

    public long getLambdaExpressions() {
        return getNumberOfObservationsOfType(ObservationType.LAMBDA_EXPRESSION);
    }

    public long getAuto() {
        return getNumberOfObservationsOfType(ObservationType.AUTO);
    }

    public long getDeclType() {
        return getNumberOfObservationsOfType(ObservationType.DECLTYPE);
    }

    public long getRangeForStatement() {
        return getNumberOfObservationsOfType(ObservationType.RANGE_FOR_STATEMENT);
    }

    public long getConstExpr() {
        return getNumberOfObservationsOfType(ObservationType.CONST_EXPRESSION);
    }

    public long getIfStatementWithInitializer() {
        return getNumberOfObservationsOfType(ObservationType.IF_STATEMENT_WITH_INITIALIZER);
    }

    public long getThreadDeclarations() {
        return getNumberOfObservationsOfType(ObservationType.THREAD);
    }

    public long getFutureDeclarations() {
        return getNumberOfObservationsOfType(ObservationType.FUTURE);
    }

    public long getSharedFutureDeclarations() {
        return getNumberOfObservationsOfType(ObservationType.SHARED_FUTURE);
    }

    public long getPromiseDeclarations() {
        return getNumberOfObservationsOfType(ObservationType.PROMISE);
    }

    public long getAsync() {
        return getNumberOfObservationsOfType(ObservationType.ASYNC);
    }

    public long getClassDeclarations() {
        return getNumberOfObservationsOfType(ObservationType.CLASS_DECLARATION);
    }

    private Observation createObservation(ObservationType type, String code) {
        return new Observation(type, code);
    }
}
