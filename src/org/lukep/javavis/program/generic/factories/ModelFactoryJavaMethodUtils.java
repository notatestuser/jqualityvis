/*
 * ModelFactoryJavaMethodUtils.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.factories;

import org.lukep.javavis.program.generic.models.MethodModel;

import com.sun.source.tree.AssertTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.TreeScanner;

/**
 * Carries out some token visitation outside of the compiling task that calculates the total number of statements 
 * in each method and the number of independent code paths though each.
 */
public class ModelFactoryJavaMethodUtils extends TreeScanner<Object, MethodModel> {

	/**
	 * Populate method statement heuristics counters.
	 *
	 * @param method the method
	 * @param methodModel the method model
	 */
	public void populateMethodStatementHeuristics(MethodTree method, 
			MethodModel methodModel) {
		BlockTree methodTree = method.getBody();
		if (methodTree != null)
			methodTree.accept(this, methodModel);
	}
	
	/*
	 * Tree Visitor Implementations
	 * Execution paths (cyclomatic complexity) implementation refs:
	 * 		http://www.boyet.com/Articles/CyclomaticComplexity.html
	 * 		http://leepoint.net/notes-java/principles_and_practices/complexity/complexity-java-method.html
	 */

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitAssert(com.sun.source.tree.AssertTree, java.lang.Object)
	 */
	@Override
	public Object visitAssert(AssertTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitAssert(tree, methodModel);
	}
	
	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitBinary(com.sun.source.tree.BinaryTree, java.lang.Object)
	 */
	@Override
	public Object visitBinary(BinaryTree tree, MethodModel methodModel) {
		
		// code paths: +1 for operators &&, ||
		switch (tree.getKind()) {
		case CONDITIONAL_AND:
			methodModel.incIndependentExecutionPaths(1);
			break;
		case CONDITIONAL_OR:
			methodModel.incIndependentExecutionPaths(1);
			break;
		}
		
		return super.visitBinary(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitBreak(com.sun.source.tree.BreakTree, java.lang.Object)
	 */
	@Override
	public Object visitBreak(BreakTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for break statement
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitBreak(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitCase(com.sun.source.tree.CaseTree, java.lang.Object)
	 */
	@Override
	public Object visitCase(CaseTree tree, MethodModel methodModel) {
		
		// code paths: +1 for case statement
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitCase(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitConditionalExpression(com.sun.source.tree.ConditionalExpressionTree, java.lang.Object)
	 */
	@Override
	public Object visitConditionalExpression(ConditionalExpressionTree tree,
			MethodModel methodModel) {
		
		// code paths: conditional expression 
		//  condition ? trueExpression : falseExpression
		// +1 for ?, +1 for :
		// : is always required in Java, thus +2
		methodModel.incIndependentExecutionPaths(2);
		
		return super.visitConditionalExpression(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitContinue(com.sun.source.tree.ContinueTree, java.lang.Object)
	 */
	@Override
	public Object visitContinue(ContinueTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for continue
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitContinue(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitDoWhileLoop(com.sun.source.tree.DoWhileLoopTree, java.lang.Object)
	 */
	@Override
	public Object visitDoWhileLoop(DoWhileLoopTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for do-while loop
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitDoWhileLoop(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitEmptyStatement(com.sun.source.tree.EmptyStatementTree, java.lang.Object)
	 */
	@Override
	public Object visitEmptyStatement(EmptyStatementTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitEmptyStatement(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitEnhancedForLoop(com.sun.source.tree.EnhancedForLoopTree, java.lang.Object)
	 */
	@Override
	public Object visitEnhancedForLoop(EnhancedForLoopTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for for loop
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitEnhancedForLoop(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitExpressionStatement(com.sun.source.tree.ExpressionStatementTree, java.lang.Object)
	 */
	@Override
	public Object visitExpressionStatement(ExpressionStatementTree tree,
			MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// TODO get rid of this disgusting thing
		//if (! (tree.getExpression() instanceof LetExpr) )
		//	return super.visitExpressionStatement(tree, methodModel);
		
		return super.visitExpressionStatement(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitForLoop(com.sun.source.tree.ForLoopTree, java.lang.Object)
	 */
	@Override
	public Object visitForLoop(ForLoopTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for for loop
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitForLoop(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitIf(com.sun.source.tree.IfTree, java.lang.Object)
	 */
	@Override
	public Object visitIf(IfTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for if statement
		methodModel.incIndependentExecutionPaths(1);
		// +1 for else statement
		if (tree.getElseStatement() != null)
			methodModel.incIndependentExecutionPaths(1);
		
		return super.visitIf(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitLabeledStatement(com.sun.source.tree.LabeledStatementTree, java.lang.Object)
	 */
	@Override
	public Object visitLabeledStatement(LabeledStatementTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitLabeledStatement(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitReturn(com.sun.source.tree.ReturnTree, java.lang.Object)
	 */
	@Override
	public Object visitReturn(ReturnTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// +1 for a return that isn't the last statement of a method
		// TODO remedy the fact that this doesn't take into account returns as the last statement in a method
		//methodModel.incIndependentCodePaths(1);
		
		return super.visitReturn(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitSwitch(com.sun.source.tree.SwitchTree, java.lang.Object)
	 */
	@Override
	public Object visitSwitch(SwitchTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitSwitch(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitSynchronized(com.sun.source.tree.SynchronizedTree, java.lang.Object)
	 */
	@Override
	public Object visitSynchronized(SynchronizedTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitSynchronized(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitThrow(com.sun.source.tree.ThrowTree, java.lang.Object)
	 */
	@Override
	public Object visitThrow(ThrowTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for throw
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitThrow(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitTry(com.sun.source.tree.TryTree, java.lang.Object)
	 */
	@Override
	public Object visitTry(TryTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for each catch
		int numCatches = tree.getCatches().size();
		if (numCatches > 0)
			methodModel.incIndependentExecutionPaths(numCatches);
		
		return super.visitTry(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitVariable(com.sun.source.tree.VariableTree, java.lang.Object)
	 */
	@Override
	public Object visitVariable(VariableTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitVariable(tree, methodModel);
	}

	/* (non-Javadoc)
	 * @see com.sun.source.util.TreeScanner#visitWhileLoop(com.sun.source.tree.WhileLoopTree, java.lang.Object)
	 */
	@Override
	public Object visitWhileLoop(WhileLoopTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for while loop
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitWhileLoop(tree, methodModel);
	}
	
}
