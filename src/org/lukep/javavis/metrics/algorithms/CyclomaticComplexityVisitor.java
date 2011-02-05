/*
 * CyclomaticComplexityVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.ClassModel;
import org.lukep.javavis.program.generic.models.MethodModel;
import org.lukep.javavis.util.JavaVisConstants;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.WhileLoopTree;

public class CyclomaticComplexityVisitor extends AbstractMeasurableVisitor {
	
	protected float complexity = 1; // we always start out with 1 path through a method
	
	@Override
	public MetricMeasurement visit(MetricAttribute metric, ClassModel clazz) {

		// if we're dealing with a class - we just return its total or avg complexity
		if (metric.getInternalName().equals(JavaVisConstants.METRIC_CYCLO_COMPLEX_AVG)) {
			complexity = clazz.getAvgCyclomaticComplexity();
		} else if (metric.getInternalName().equals(JavaVisConstants.METRIC_CYCLO_COMPLEX_MAX)) {
			complexity = clazz.getMaxCyclomaticComplexity();
		} else {
			return null;
		}
		return new MetricMeasurement(clazz, metric, complexity);
	}

	@Override
	public MetricMeasurement visit(MetricAttribute metric, MethodModel method) {
		
		switch (method.getSourceLang()) {
		case JAVA:
			return calculateForJavaMethod(metric, method);
		}
		return null;
	}
	
	private MetricMeasurement calculateForJavaMethod(MetricAttribute metric, MethodModel method) {
		MetricMeasurement result = new MetricMeasurement(method, metric);
		BlockTree methodTree = (BlockTree) method.getRootStatementBlock();
		if (methodTree != null)
			methodTree.accept(this, null);
		result.setResult(complexity);
		return result;
	}
	
	/*
	 * Tree Visitor Implementations
	 */

	@Override
	public Object visitBinary(BinaryTree arg0, Object arg1) {
		
		// +1 for operators &&, ||
		switch (arg0.getKind()) {
		case CONDITIONAL_AND:
			complexity += 1;
			break;
		case CONDITIONAL_OR:
			complexity += 1;
			break;
		}
		
		return super.visitBinary(arg0, arg1);
	}

	@Override
	public Object visitBreak(BreakTree arg0, Object arg1) {
		
		// +1 for break statement
		complexity += 1;
		
		return super.visitBreak(arg0, arg1);
	}

	@Override
	public Object visitCase(CaseTree arg0, Object arg1) {

		// +1 for case statement
		complexity += 1;
		
		return super.visitCase(arg0, arg1);
	}

	@Override
	public Object visitConditionalExpression(ConditionalExpressionTree arg0,
			Object arg1) {

		// conditional expression: 
		//  condition ? trueExpression : falseExpression
		// +1 for ?, +1 for :
		complexity += 2;
		
		return super.visitConditionalExpression(arg0, arg1);
	}

	@Override
	public Object visitContinue(ContinueTree arg0, Object arg1) {

		// +1 for continue
		complexity += 1;
		
		return super.visitContinue(arg0, arg1);
	}

	@Override
	public Object visitDoWhileLoop(DoWhileLoopTree arg0, Object arg1) {

		// +1 for do-while loop
		complexity += 1;
		
		return super.visitDoWhileLoop(arg0, arg1);
	}

	@Override
	public Object visitEnhancedForLoop(EnhancedForLoopTree arg0, Object arg1) {

		// +1 for for loop
		complexity += 1;
		
		return super.visitEnhancedForLoop(arg0, arg1);
	}

	@Override
	public Object visitForLoop(ForLoopTree arg0, Object arg1) {

		// +1 for for loop
		complexity += 1;
		
		return super.visitForLoop(arg0, arg1);
	}

	@Override
	public Object visitIf(IfTree arg0, Object arg1) {

		// +1 for if statement
		complexity += 1;
		// +1 for else statement
		if (arg0.getElseStatement() != null)
			complexity += 1;
		
		return super.visitIf(arg0, arg1);
	}

	@Override
	public Object visitReturn(ReturnTree arg0, Object arg1) {

		// +1 for a return that isn't the last statement of a method
		// TODO remedy the fact that this doesn't take into account returns as the last statement in a method
		//complexity += 1;
		
		return super.visitReturn(arg0, arg1);
	}

	@Override
	public Object visitThrow(ThrowTree arg0, Object arg1) {

		// +1 for throw
		complexity += 1;
		
		return super.visitThrow(arg0, arg1);
	}

	@Override
	public Object visitTry(TryTree arg0, Object arg1) {

		// +1 for each catch
		int numCatches = arg0.getCatches().size();
		if (numCatches > 0)
			complexity += numCatches;
		
		return super.visitTry(arg0, arg1);
	}

	@Override
	public Object visitWhileLoop(WhileLoopTree arg0, Object arg1) {

		// +1 for while loop
		complexity += 1;
		
		return super.visitWhileLoop(arg0, arg1);
	}

}
