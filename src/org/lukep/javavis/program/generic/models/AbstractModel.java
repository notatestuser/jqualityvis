/*
 * AbstractModel.java (JMetricVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.HashSet;
import java.util.Set;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;
import org.lukep.javavis.util.JavaVisConstants;

abstract class AbstractModel implements IMeasurableNode {

	public String APPLIES_TO_STR = "generic";
	
	protected String simpleName;
	protected String qualifiedName;
	protected AbstractModelSourceLang sourceLang;
	protected IGenericModelNode parent;
	protected HashSet<Relationship> children;
	
	private boolean publicFlag = false;
	private boolean protectedFlag = false;
	private boolean finalFlag = false;
	private boolean nativeFlag = false;
	private boolean staticFlag = false;
	private boolean privateFlag = false;
	private boolean abstractFlag = false;

	public AbstractModel(AbstractModelSourceLang sourceLang, String appliesToString) {
		super();
		this.sourceLang = sourceLang;
		APPLIES_TO_STR = appliesToString;
	}
	
	public AbstractModel(AbstractModelSourceLang sourceLang, String appliesToString, IGenericModelNode parent) {
		this(sourceLang, appliesToString);
		this.parent = parent;
	}
	
	///////////////////////////////////////////////////////
	
	@Override
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute) {
		if (attribute != null)
			return MetricRegistry.getInstance().getCachedMeasurement(this, attribute);
		return null;
	}
	
	///////////////////////////////////////////////////////

	public void setParent(IGenericModelNode parent) {
		this.parent = parent;
	}
	
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	
	///////////////////////////////////////////////////////
	
	@Override
	public void addChild(IGenericModelNode child, RelationshipType type) {
		// lazy instantiated list of child models
		if (children == null)
			children = new HashSet<Relationship>();
		children.add( new Relationship(this, child, type) );
	}
	
	@Override
	public IGenericModelNode getParent() {
		return parent;
	}
	
	@Override
	public IGenericModelNode getRootNode() {
		IGenericModelNode parent = this;
		do {
			parent = parent.getParent();
			assert(parent != null);
		} while (!parent.isRootNode());
		return parent;
	}

	@Override
	public boolean isRootNode() {
		return false;
	}

	@Override
	public Set<Relationship> getChildren() {
		return children;
	}
	
	@Override
	public int getChildCount() {
		if (children == null)
			return 0;
		return children.size();
	}
	
	@Override
	public String getModelTypeName() {
		return APPLIES_TO_STR;
	}
	
	@Override
	public String getContainerName() {
		if (parent != null)
			return parent.getQualifiedName();
		return "";
	}
	
	@Override
	public String getSimpleName() {
		return this.simpleName;
	}
	
	@Override
	public String getQualifiedName() {
		return this.qualifiedName;
	}
	
	@Override
	public String getModifierNames(String delimiter) {
		
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		
		if (isPublic() || isProtected() || isPrivate()) {
			if (isPublic())
				sb.append(JavaVisConstants.MODEL_MODIFIER_PUBLIC);
			else if (isProtected())
				sb.append(JavaVisConstants.MODEL_MODIFIER_PROTECTED);
			else if (isPrivate())
				sb.append(JavaVisConstants.MODEL_MODIFIER_PRIVATE);
			first = false;
		}
		
		if (isAbstract()) {
			sb.append((!first ? delimiter : "")
				+ JavaVisConstants.MODEL_MODIFIER_ABSTRACT);
			first = false;
		}
		if (isFinal()) {
			sb.append((!first ? delimiter : "")
				+ JavaVisConstants.MODEL_MODIFIER_FINAL);
			first = false;
		}
		if (isNative()) {
			sb.append((!first ? delimiter : "")
				+ JavaVisConstants.MODEL_MODIFIER_NATIVE);
			first = false;
		}
		if (isStatic()) {
			sb.append((!first ? delimiter : "")
				+ JavaVisConstants.MODEL_MODIFIER_STATIC);
			first = false;
		}
		
		return sb.toString();
	}
	
	@Override
	public boolean isPublic() {
		return publicFlag;
	}

	@Override
	public boolean isProtected() {
		return protectedFlag;
	}

	@Override
	public boolean isFinal() {
		return finalFlag;
	}

	@Override
	public boolean isNative() {
		return nativeFlag;
	}

	@Override
	public boolean isStatic() {
		return staticFlag;
	}

	@Override
	public boolean isPrivate() {
		return privateFlag;
	}

	@Override
	public boolean isAbstract() {
		return abstractFlag;
	}

	@Override
	public void setPublic(boolean publicFlag) {
		this.publicFlag = publicFlag;
	}

	@Override
	public void setProtected(boolean protectedFlag) {
		this.protectedFlag = protectedFlag;
	}

	@Override
	public void setFinal(boolean finalFlag) {
		this.finalFlag = finalFlag;
	}

	@Override
	public void setNative(boolean nativeFlag) {
		this.nativeFlag = nativeFlag;
	}

	@Override
	public void setStatic(boolean staticFlag) {
		this.staticFlag = staticFlag;
	}

	@Override
	public void setPrivate(boolean privateFlag) {
		this.privateFlag = privateFlag;
	}

	@Override
	public void setAbstract(boolean abstractFlag) {
		this.abstractFlag = abstractFlag;
	}
	
	///////////////////////////////////////////////////////
	
	public AbstractModelSourceLang getSourceLang() {
		return sourceLang;
	}
	
}
