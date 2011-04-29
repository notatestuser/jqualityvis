/*
 * AbstractModel.java (JQualityVis)
 * Copyright 2011 Luke Plaster. All rights reserved.
 */
package org.lukep.javavis.program.generic.models;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lukep.javavis.metrics.IMeasurableNode;
import org.lukep.javavis.metrics.MetricAttribute;
import org.lukep.javavis.metrics.MetricMeasurement;
import org.lukep.javavis.metrics.MetricRegistry;
import org.lukep.javavis.program.generic.models.Relationship.RelationshipType;
import org.lukep.javavis.util.JavaVisConstants;

/**
 * The Class AbstractModel.
 */
abstract class AbstractModel implements IMeasurableNode {

	private static final long serialVersionUID = 3811512186983329425L;
	
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
	
	private transient boolean metricsPreloaded = false;

	/**
	 * Instantiates a new abstract model.
	 *
	 * @param sourceLang the source lang
	 * @param appliesToString the applies to string
	 */
	public AbstractModel(AbstractModelSourceLang sourceLang, String appliesToString) {
		super();
		this.sourceLang = sourceLang;
		APPLIES_TO_STR = appliesToString;
	}
	
	/**
	 * Instantiates a new abstract model.
	 *
	 * @param sourceLang the source lang
	 * @param appliesToString the applies to string
	 * @param parent the parent
	 */
	public AbstractModel(AbstractModelSourceLang sourceLang, String appliesToString, IGenericModelNode parent) {
		this(sourceLang, appliesToString);
		this.parent = parent;
	}
	
	///////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.IMeasurableNode#getMetricMeasurement(org.lukep.javavis.metrics.MetricAttribute)
	 */
	@Override
	public MetricMeasurement getMetricMeasurement(MetricAttribute attribute) {
		if (attribute != null)
			return MetricRegistry.getInstance().getCachedMeasurement(this, attribute);
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.IMeasurableNode#isMetricsPreloaded()
	 */
	@Override
	public boolean isMetricsPreloaded() {
		return metricsPreloaded;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.metrics.IMeasurableNode#setMetricsPreloaded(boolean)
	 */
	@Override
	public void setMetricsPreloaded(boolean metricsPreloaded) {
		this.metricsPreloaded = metricsPreloaded;
	}
	
	///////////////////////////////////////////////////////

	/**
	 * Sets the parent.
	 *
	 * @param parent the new parent
	 */
	public void setParent(IGenericModelNode parent) {
		this.parent = parent;
	}
	
	/**
	 * Sets the simple name.
	 *
	 * @param simpleName the new simple name
	 */
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	
	/**
	 * Sets the qualified name.
	 *
	 * @param qualifiedName the new qualified name
	 */
	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}
	
	///////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#addChild(org.lukep.javavis.program.generic.models.IGenericModelNode, org.lukep.javavis.program.generic.models.Relationship.RelationshipType)
	 */
	@Override
	public void addChild(IGenericModelNode child, RelationshipType type) {
		// lazy instantiated list of child models
		if (children == null)
			children = new LinkedHashSet<Relationship>();
		children.add( new Relationship(this, child, type) );
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#getParent()
	 */
	@Override
	public IGenericModelNode getParent() {
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#getRootNode()
	 */
	@Override
	public IGenericModelNode getRootNode() {
		IGenericModelNode parent = this;
		do {
			parent = parent.getParent();
			assert(parent != null);
		} while (!parent.isRootNode());
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#isRootNode()
	 */
	@Override
	public boolean isRootNode() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#getChildren()
	 */
	@Override
	public Set<Relationship> getChildren() {
		return children;
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#getChildCount()
	 */
	@Override
	public int getChildCount() {
		if (children == null)
			return 0;
		return children.size();
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#getModelTypeName()
	 */
	@Override
	public String getModelTypeName() {
		return APPLIES_TO_STR;
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#getContainerName()
	 */
	@Override
	public String getContainerName() {
		if (parent != null)
			return parent.getQualifiedName();
		return "";
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#getSimpleName()
	 */
	@Override
	public String getSimpleName() {
		return this.simpleName;
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#getQualifiedName()
	 */
	@Override
	public String getQualifiedName() {
		return this.qualifiedName;
	}
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#getModifierNames(java.lang.String)
	 */
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
	
	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#isPublic()
	 */
	@Override
	public boolean isPublic() {
		return publicFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#isProtected()
	 */
	@Override
	public boolean isProtected() {
		return protectedFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#isFinal()
	 */
	@Override
	public boolean isFinal() {
		return finalFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#isNative()
	 */
	@Override
	public boolean isNative() {
		return nativeFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#isStatic()
	 */
	@Override
	public boolean isStatic() {
		return staticFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#isPrivate()
	 */
	@Override
	public boolean isPrivate() {
		return privateFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#isAbstract()
	 */
	@Override
	public boolean isAbstract() {
		return abstractFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#setPublic(boolean)
	 */
	@Override
	public void setPublic(boolean publicFlag) {
		this.publicFlag = publicFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#setProtected(boolean)
	 */
	@Override
	public void setProtected(boolean protectedFlag) {
		this.protectedFlag = protectedFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#setFinal(boolean)
	 */
	@Override
	public void setFinal(boolean finalFlag) {
		this.finalFlag = finalFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#setNative(boolean)
	 */
	@Override
	public void setNative(boolean nativeFlag) {
		this.nativeFlag = nativeFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#setStatic(boolean)
	 */
	@Override
	public void setStatic(boolean staticFlag) {
		this.staticFlag = staticFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#setPrivate(boolean)
	 */
	@Override
	public void setPrivate(boolean privateFlag) {
		this.privateFlag = privateFlag;
	}

	/* (non-Javadoc)
	 * @see org.lukep.javavis.program.generic.models.IGenericModelNode#setAbstract(boolean)
	 */
	@Override
	public void setAbstract(boolean abstractFlag) {
		this.abstractFlag = abstractFlag;
	}
	
	///////////////////////////////////////////////////////
	
	/**
	 * Gets the source lang.
	 *
	 * @return the source lang
	 */
	public AbstractModelSourceLang getSourceLang() {
		return sourceLang;
	}
	
}
