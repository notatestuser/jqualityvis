/*
 * StatementCountVisitor.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.metrics.algorithms;

import org.lukep.javavis.metrics.IMeasurableVisitor;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.program.generic.models.measurable.MeasurableClassInfo;
import org.lukep.javavis.program.generic.models.measurable.MeasurableMethodInfo;

import com.sun.source.tree.AssertTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.util.TreeScanner;

public class StatementCountVisitor extends TreeScanner<Object, Object> 
		implements IMeasurableVisitor {
	
	// TODO abstract this class to make this generic?
	protected static final MetricAttribute METRIC_ATTRIBUTE = 
		MetricAttribute.NUMBER_OF_STATEMENTS;
	
	protected int statementCount = 0;
	
	@Override
	public MetricMeasurement visit(MeasurableClassInfo clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetricMeasurement visit(MeasurableMethodInfo method) {
		
		switch (method.getSourceLang()) {
		case JAVA:
			return calculateForJavaMethod(method);
		}
		return null;
	}
	
	private MetricMeasurement calculateForJavaMethod(MeasurableMethodInfo method) {
		MetricMeasurement result = new MetricMeasurement(method, METRIC_ATTRIBUTE);
		BlockTree methodTree = (BlockTree) method.getRootStatementBlock();
		if (methodTree != null)
			methodTree.accept(this, null);
		result.setResult(statementCount);
		return result;
	}
	
	private void incrementStatementCount() {
		statementCount++;
	}
	
	// Tree visitor implementations

	@Override
	public Object visitAssert(AssertTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitAssert(arg0, arg1);
	}

	@Override
	public Object visitBreak(BreakTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitBreak(arg0, arg1);
	}

	@Override
	public Object visitContinue(ContinueTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitContinue(arg0, arg1);
	}

	@Override
	public Object visitDoWhileLoop(DoWhileLoopTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitDoWhileLoop(arg0, arg1);
	}

	@Override
	public Object visitEmptyStatement(EmptyStatementTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitEmptyStatement(arg0, arg1);
	}

	@Override
	public Object visitEnhancedForLoop(EnhancedForLoopTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitEnhancedForLoop(arg0, arg1);
	}

	@Override
	public Object visitExpressionStatement(ExpressionStatementTree arg0,
			Object arg1) {
		incrementStatementCount();
		return super.visitExpressionStatement(arg0, arg1);
	}

	@Override
	public Object visitForLoop(ForLoopTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitForLoop(arg0, arg1);
	}

	@Override
	public Object visitIf(IfTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitIf(arg0, arg1);
	}

	@Override
	public Object visitLabeledStatement(LabeledStatementTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitLabeledStatement(arg0, arg1);
	}

	@Override
	public Object visitReturn(ReturnTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitReturn(arg0, arg1);
	}

	@Override
	public Object visitSwitch(SwitchTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitSwitch(arg0, arg1);
	}

	@Override
	public Object visitSynchronized(SynchronizedTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitSynchronized(arg0, arg1);
	}

	@Override
	public Object visitThrow(ThrowTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitThrow(arg0, arg1);
	}

	@Override
	public Object visitTry(TryTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitTry(arg0, arg1);
	}

	@Override
	public Object visitVariable(VariableTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitVariable(arg0, arg1);
	}

	@Override
	public Object visitWhileLoop(WhileLoopTree arg0, Object arg1) {
		incrementStatementCount();
		return super.visitWhileLoop(arg0, arg1);
	}
	
}
