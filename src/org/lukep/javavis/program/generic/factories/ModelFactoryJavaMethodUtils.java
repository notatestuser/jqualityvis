/*
 * ModelFactoryJavaMethodUtils.java (JMetricVis)
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

public class ModelFactoryJavaMethodUtils extends TreeScanner<Object, MethodModel> {

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

	@Override
	public Object visitAssert(AssertTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitAssert(tree, methodModel);
	}
	
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

	@Override
	public Object visitBreak(BreakTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for break statement
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitBreak(tree, methodModel);
	}

	@Override
	public Object visitCase(CaseTree tree, MethodModel methodModel) {
		
		// code paths: +1 for case statement
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitCase(tree, methodModel);
	}

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

	@Override
	public Object visitContinue(ContinueTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for continue
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitContinue(tree, methodModel);
	}

	@Override
	public Object visitDoWhileLoop(DoWhileLoopTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for do-while loop
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitDoWhileLoop(tree, methodModel);
	}

	@Override
	public Object visitEmptyStatement(EmptyStatementTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitEmptyStatement(tree, methodModel);
	}

	@Override
	public Object visitEnhancedForLoop(EnhancedForLoopTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for for loop
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitEnhancedForLoop(tree, methodModel);
	}

	@Override
	public Object visitExpressionStatement(ExpressionStatementTree tree,
			MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// TODO get rid of this disgusting thing
		//if (! (tree.getExpression() instanceof LetExpr) )
		//	return super.visitExpressionStatement(tree, methodModel);
		
		return super.visitExpressionStatement(tree, methodModel);
	}

	@Override
	public Object visitForLoop(ForLoopTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for for loop
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitForLoop(tree, methodModel);
	}

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

	@Override
	public Object visitLabeledStatement(LabeledStatementTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitLabeledStatement(tree, methodModel);
	}

	@Override
	public Object visitReturn(ReturnTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// +1 for a return that isn't the last statement of a method
		// TODO remedy the fact that this doesn't take into account returns as the last statement in a method
		//methodModel.incIndependentCodePaths(1);
		
		return super.visitReturn(tree, methodModel);
	}

	@Override
	public Object visitSwitch(SwitchTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitSwitch(tree, methodModel);
	}

	@Override
	public Object visitSynchronized(SynchronizedTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitSynchronized(tree, methodModel);
	}

	@Override
	public Object visitThrow(ThrowTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for throw
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitThrow(tree, methodModel);
	}

	@Override
	public Object visitTry(TryTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for each catch
		int numCatches = tree.getCatches().size();
		if (numCatches > 0)
			methodModel.incIndependentExecutionPaths(numCatches);
		
		return super.visitTry(tree, methodModel);
	}

	@Override
	public Object visitVariable(VariableTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		return super.visitVariable(tree, methodModel);
	}

	@Override
	public Object visitWhileLoop(WhileLoopTree tree, MethodModel methodModel) {
		methodModel.incStatementCount(1);
		
		// code paths: +1 for while loop
		methodModel.incIndependentExecutionPaths(1);
		
		return super.visitWhileLoop(tree, methodModel);
	}
	
}
