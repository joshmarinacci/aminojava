package org.joshy.gfx.node.layout.mig;
/*
 * License (BSD):
 * ==============
 *
 * Copyright (c) 2004, Mikael Grev, MiG InfoCom AB. (miglayout (at) miginfocom (dot) com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * Neither the name of the MiG InfoCom AB nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @version 1.0
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 2006-sep-08
 */

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.Timer;


import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Textarea;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.mig.layout.AC;
import org.joshy.gfx.node.layout.mig.layout.CC;
import org.joshy.gfx.node.layout.mig.layout.ComponentWrapper;
import org.joshy.gfx.node.layout.mig.layout.ConstraintParser;
import org.joshy.gfx.node.layout.mig.layout.ContainerWrapper;
import org.joshy.gfx.node.layout.mig.layout.Grid;
import org.joshy.gfx.node.layout.mig.layout.LC;
import org.joshy.gfx.node.layout.mig.layout.LayoutCallback;
import org.joshy.gfx.node.layout.mig.layout.LayoutUtil;
import org.joshy.gfx.node.layout.mig.layout.PlatformDefaults;


/*****************************************************************************
 * <p>This panel is based on the MigLayout that is a grid/cell based layout that 
 * has a lot of powerful features. You can define constraints for 
 * the layout, the rows and then per cell.</p>
 * <p>
 * This panel is a direct port and supports all the features of the original MigLayout.
 * Please read the documentation at http://www.miglayout.com/ 
 * </p>
 * 
 * <p>
 * Additionally this panel adds a fluid API to define cell constraints.
 * There are three ways to define cell constraints:
 * </p>
 * <p>
 * <b>String based:</b><br/>
 * <code>
 *   panel.add(new Button("Test"), "growx, wrap"):
 * </code>
 * </p><p>
 * <b>API:</b><br/>  
 * <code>
 *   panel.add(new Button("Test"), new CC().growX().wrap()):
 * </code>
 * </p><p>
* <b>Amino API:</b><br/>  
 * <code>
 *   panel.nextCell().growX().wrap().content(new Button("Test")):
 * </code> 
 * </p>
 *
 * @author  Bernd Rosstauscher 
 *         (svg2java(@)rosstauscher.de)
 ****************************************************************************/

public class MigPanel extends Panel implements Externalizable
{
	// ******** Instance part ********

	/** The component to string constraints mappings.
	 */
	private final Map<Control, Object> scrConstrMap = new IdentityHashMap<Control, Object>(8);

	/** Hold the serializable text representation of the constraints.
	 */
	private Object layoutConstraints = "", colConstraints = "", rowConstraints = "";    // Should never be null!

	// ******** Transient part ********

	private transient ContainerWrapper cacheParentW = null;

	private transient final Map<ComponentWrapper, CC> ccMap = new HashMap<ComponentWrapper, CC>(8);
	private transient javax.swing.Timer debugTimer = null;

	private transient LC lc = null;
	private transient AC colSpecs = null, rowSpecs = null;
	private transient Grid grid = null;
	private transient int lastModCount = PlatformDefaults.getModCount();
	private transient int lastHash = -1;
	private transient Dimension lastInvalidSize = null;
	private transient boolean lastWasInvalid = false;  // Added in 3.7.1. May have regressions

	private transient ArrayList<LayoutCallback> callbackList = null;

	private transient boolean dirty = true;
	
	private CC nextCellConstraints = null;

	/** Constructor with no constraints.
	 */
	public MigPanel()
	{
		this("", "", "");
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as "".
	 */
	public MigPanel(String layoutConstraints)
	{
		this(layoutConstraints, "", "");
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as "".
	 * @param colConstraints The constraints for the columns in the grid. <code>null</code> will be treated as "".
	 */
	public MigPanel(String layoutConstraints, String colConstraints)
	{
		this(layoutConstraints, colConstraints, "");
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as "".
	 * @param colConstraints The constraints for the columns in the grid. <code>null</code> will be treated as "".
	 * @param rowConstraints The constraints for the rows in the grid. <code>null</code> will be treated as "".
	 */
	public MigPanel(String layoutConstraints, String colConstraints, String rowConstraints)
	{
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		setRowConstraints(rowConstraints);
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as an empty cosntraint.
	 */
	public MigPanel(LC layoutConstraints)
	{
		this(layoutConstraints, null, null);
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as an empty cosntraint.
	 * @param colConstraints The constraints for the columns in the grid. <code>null</code> will be treated as an empty constraint.
	 */
	public MigPanel(LC layoutConstraints, AC colConstraints)
	{
		this(layoutConstraints, colConstraints, null);
	}

	/** Constructor.
	 * @param layoutConstraints The constraints that concern the whole layout. <code>null</code> will be treated as an empty cosntraint.
	 * @param colConstraints The constraints for the columns in the grid. <code>null</code> will be treated as an empty constraint.
	 * @param rowConstraints The constraints for the rows in the grid. <code>null</code> will be treated as an empty constraint.
	 */
	public MigPanel(LC layoutConstraints, AC colConstraints, AC rowConstraints)
	{
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		setRowConstraints(rowConstraints);
	}

	/** Returns layout constraints either as a <code>String</code> or {@link org.joshy.gfx.node.layout.mig.layout.LC} depending what was sent in
	 * to the constructor or set with {@link #setLayoutConstraints(Object)}.
	 * @return The layout constraints either as a <code>String</code> or {@link org.joshy.gfx.node.layout.mig.layout.LC} depending what was sent in
	 * to the constructor or set with {@link #setLayoutConstraints(Object)}. Never <code>null</code>.
	 */
	protected Object getLayoutConstraints()
	{
		return layoutConstraints;
	}

	/** Sets the layout constraints for the layout manager instance as a String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @param constr The layout constraints as a String representation. <code>null</code> is converted to <code>""</code> for storage.
	 * @throws RuntimeException if the constraint was not valid.
	 */
	public void setLayoutConstraints(Object constr)
	{
		if (constr == null || constr instanceof String) {
			constr = ConstraintParser.prepare((String) constr);
			lc = ConstraintParser.parseLayoutConstraint((String) constr);
		} else if (constr instanceof LC) {
			lc = (LC) constr;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
		}
		layoutConstraints = constr;
		dirty = true;
	}

	/** Returns the column layout constraints either as a <code>String</code> or {@link org.joshy.gfx.node.layout.mig.layout.AC}.
	 * @return The column constraints either as a <code>String</code> or {@link org.joshy.gfx.node.layout.mig.layout.LC} depending what was sent in
	 * to the constructor or set with {@link #setLayoutConstraints(Object)}. Never <code>null</code>.
	 */
	protected Object getColumnConstraints()
	{
		return colConstraints;
	}

	/** Sets the column layout constraints for the layout manager instance as a String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @param constr The column layout constraints as a String representation. <code>null</code> is converted to <code>""</code> for storage.
	 * @throws RuntimeException if the constraint was not valid.
	 */
	protected void setColumnConstraints(Object constr)
	{
		if (constr == null || constr instanceof String) {
			constr = ConstraintParser.prepare((String) constr);
			colSpecs = ConstraintParser.parseColumnConstraints((String) constr);
		} else if (constr instanceof AC) {
			colSpecs = (AC) constr;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
		}
		colConstraints = constr;
		dirty = true;
	}

	/** Returns the row layout constraints as a String representation. This string is the exact string as set with {@link #setRowConstraints(Object)}
	 * or sent into the constructor.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @return The row layout constraints as a String representation. Never <code>null</code>.
	 */
	protected Object getRowConstraints()
	{
		return rowConstraints;
	}

	/** Sets the row layout constraints for the layout manager instance as a String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @param constr The row layout constraints as a String representation. <code>null</code> is converted to <code>""</code> for storage.
	 * @throws RuntimeException if the constraint was not valid.
	 */
	public void setRowConstraints(Object constr)
	{
		if (constr == null || constr instanceof String) {
			constr = ConstraintParser.prepare((String) constr);
			rowSpecs = ConstraintParser.parseRowConstraints((String) constr);
		} else if (constr instanceof AC) {
			rowSpecs = (AC) constr;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
		}
		rowConstraints = constr;
		dirty = true;
	}

	/** Returns a shallow copy of the constraints map.
	 * @return A  shallow copy of the constraints map. Never <code>null</code>.
	 */
	protected Map<Control, Object> getConstraintMap()
	{
		return new IdentityHashMap<Control, Object>(scrConstrMap);
	}

	/** Sets the constraints map.
	 * @param map The map. Will be copied.
	 */
	protected void setConstraintMap(Map<Control, Object> map)
	{
		scrConstrMap.clear();
		ccMap.clear();
		for (Map.Entry<Control, Object> e : map.entrySet())
			setComponentConstraintsImpl(e.getKey(), e.getValue(), true);
	}

	/** Returns the component constraints as a String representation. This string is the exact string as set with {@link #setComponentConstraints(java.awt.Component, Object)}
	 * or set when adding the component to the parent component.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @param comp The component to return the constraints for.
	 * @return The component constraints as a String representation or <code>null</code> if the component is not registered
	 * with this layout manager. The returned values is either a String or a {@link org.joshy.gfx.node.layout.mig.layout.CC}
	 * depending on what constraint was sent in when the component was added. May be <code>null</code>.
	 */
	protected Object getComponentConstraints(Control comp)
	{
		synchronized(this) {
			return scrConstrMap.get(comp);
		}
	}

	/** Sets the component constraint for the component that already must be handled by this layout manager.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @param constr The component constraints as a String or {@link org.joshy.gfx.node.layout.mig.layout.CC}. <code>null</code> is ok.
	 * @param comp The component to set the constraints for.
	 * @throws RuntimeException if the constraint was not valid.
	 * @throws IllegalArgumentException If the component is not handlering the component.
	 */
	protected void setComponentConstraints(Control comp, Object constr)
	{
		setComponentConstraintsImpl(comp, constr, false);
	}

	/** Sets the component constraint for the component that already must be handled by this layout manager.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 * @param constr The component constraints as a String or {@link org.joshy.gfx.node.layout.mig.layout.CC}. <code>null</code> is ok.
	 * @param comp The component to set the constraints for.
	 * @param noCheck Doe not check if the component is handled if true
	 * @throws RuntimeException if the constraint was not valid.
	 * @throws IllegalArgumentException If the component is not handling the component.
	 */
	private void setComponentConstraintsImpl(Control comp, Object constr, boolean noCheck)
	{
		synchronized(this) { // 3.7.2. No sync if not added to a hierarchy. Defeats a NPE.
			if (noCheck == false && scrConstrMap.containsKey(comp) == false)
				throw new IllegalArgumentException("Component must already be added to parent!");

			ComponentWrapper cw = new AminoComponentWrapper(comp);

			if (constr == null || constr instanceof String) {
				String cStr = ConstraintParser.prepare((String) constr);

				scrConstrMap.put(comp, constr);
				ccMap.put(cw, ConstraintParser.parseComponentConstraint(cStr));

			} else if (constr instanceof CC) {

				scrConstrMap.put(comp, constr);
				ccMap.put(cw, (CC) constr);

			} else {
				throw new IllegalArgumentException("Constraint must be String or ComponentConstraint: " + constr.getClass().toString());
			}

			dirty = true;
		}
	}

	/** Returns if this layout manager is currently managing this component.
	 * @param c The component to check. If <code>null</code> then <code>false</code> will be returned.
	 * @return If this layout manager is currently managing this component.
	 */
	protected boolean isManagingComponent(Control c)
	{
		return scrConstrMap.containsKey(c);
	}

	/** Adds the callback function that will be called at different stages of the layout cylce.
	 * @param callback The callback. Not <code>null</code>.
	 */
	public void addLayoutCallback(LayoutCallback callback)
	{
		if (callback == null)
			throw new NullPointerException();

		if (callbackList == null)
			callbackList = new ArrayList<LayoutCallback>(1);

		callbackList.add(callback);
	}

	/** Removes the callback if it exists.
	 * @param callback The callback. May be <code>null</code>.
	 */
	public void removeLayoutCallback(LayoutCallback callback)
	{
		if (callbackList != null)
			callbackList.remove(callback);
	}

	/** Sets the debugging state for this layout manager instance. If debug is turned on a timer will repaint the last laid out parent
	 * with debug information on top.
	 * <p>
	 * Red fill and dashed darked red outline is used to indicate occupied cells in the grid. Blue dashed outline indicate indicate
	 * component bounds set.
	 * <p>
	 * Note that debug can also be set on the layout constraints. There it will be persisted. The value set here will not. See the class
	 * JavaDocs for information.
	 * @param parentW The parent to set debug for.
	 * @param b <code>true</code> means debug is turned on.
	 */
	private synchronized void setDebug(final ComponentWrapper parentW, boolean b)
	{
		// TODO rossi 14.12.2010 how to support this in amino.
		if (b && (debugTimer == null || debugTimer.getDelay() != getDebugMillis())) {
			if (debugTimer != null)
				debugTimer.stop();

			ContainerWrapper pCW = parentW.getParent();
			final Control parent = pCW != null ? (Control) pCW.getComponent() : null;

			debugTimer = new Timer(getDebugMillis(), new MyDebugRepaintListener(MigPanel.this));

			if (parent != null) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Parent p = parent.getParent();
						if (p != null) {
							if (p instanceof Control) {
								((Control) p).doSkins();
							}
						}
					}
				});
			}

			debugTimer.setInitialDelay(100);
			debugTimer.start();

		} else if (!b && debugTimer != null) {
			debugTimer.stop();
			debugTimer = null;
		}
	}

	/** Returns the current debugging state.
	 * @return The current debugging state.
	 */
	private boolean getDebug()
	{
		return debugTimer != null;
	}

	/** Returns the debug millis. Combines the value from {@link org.joshy.gfx.node.layout.mig.layout.LC#getDebugMillis()} and {@link org.joshy.gfx.node.layout.mig.layout.LayoutUtil#getGlobalDebugMillis()}
	 * @return The combined value.
	 */
	private int getDebugMillis()
	{
		int globalDebugMillis = LayoutUtil.getGlobalDebugMillis();
		return globalDebugMillis > 0 ? globalDebugMillis : lc.getDebugMillis();
	}

	/** Check if something has changed and if so recrete it to the cached objects.
	 * @param parent The parent that is the target for this layout manager.
	 */
	private void checkCache(Container parent)
	{
		if (parent == null)
			return;

		if (dirty)
			grid = null;

		// Check if the grid is valid
		int mc = PlatformDefaults.getModCount();
		if (lastModCount != mc) {
			grid = null;
			lastModCount = mc;
		}

		if (((Control)parent).isVisible() == false) {
			if (lastWasInvalid == false) {
				lastWasInvalid = true;

				int hash = 0;
				boolean resetLastInvalidOnParent = false; // Added in 3.7.3 to resolve a timing regression introduced in 3.7.1
				for (Iterator<ComponentWrapper> it = ccMap.keySet().iterator(); it.hasNext();) {
					ComponentWrapper wrapper = it.next();
					Object component = wrapper.getComponent();
					if (component instanceof Textarea)
						resetLastInvalidOnParent = true;
					hash += wrapper.getLayoutHashCode();
				}
				if (resetLastInvalidOnParent)
					resetLastInvalidOnParent(parent);

				if (hash != lastHash) {
					grid = null;
					lastHash = hash;
				}

				Bounds lb = ((Control)parent).getLayoutBounds();
				Dimension ps = new Dimension((int)lb.getWidth(), (int)lb.getHeight());
				if (lastInvalidSize == null || !lastInvalidSize.equals(ps)) {
					if (grid != null)
						grid.invalidateContainerSize();
					lastInvalidSize = ps;
				}
			}
		} else {
			lastWasInvalid = false;
		}

		ContainerWrapper par = checkParent(parent);

		setDebug(par, getDebugMillis() > 0);

		if (grid == null)
			grid = new Grid(par, lc, rowSpecs, colSpecs, ccMap, callbackList);

		dirty = false;
	}

	/**
	 * @since 3.7.3
	 */
	private void resetLastInvalidOnParent(Container parent)
	{
		while (parent != null) {
			if (parent instanceof MigPanel) {
				((MigPanel) parent).lastWasInvalid = false;
			}
		}
	}

	private ContainerWrapper checkParent(Container parent)
	{
		if (parent == null)
			return null;

		if (cacheParentW == null || cacheParentW.getComponent() != parent)
			cacheParentW = new AminoContainerWrapper(parent);

		return cacheParentW;
	}

	/*************************************************************************
	 * doLayout
	 * @see org.joshy.gfx.node.layout.Panel#doLayout()
	 ************************************************************************/
	@Override
	public void doLayout()
	{
		MigPanel parent = this;
		synchronized(this) {
			checkCache(parent);

			Insets i = new Insets(0, 0, 0, 0);
			int[] b = new int[] {
					i.left,
					i.top,
					(int) (parent.getWidth() - i.left - i.right),
					(int) (parent.getHeight() - i.top - i.bottom)
			};

			if (grid.layout(b, lc.getAlignX(), lc.getAlignY(), getDebug(), true)) {
				grid = null;
				checkCache(parent);
				grid.layout(b, lc.getAlignX(), lc.getAlignY(), getDebug(), false);
			}

			lastInvalidSize = null;
		}
		super.doLayout();
	}
	
	/*************************************************************************
	 * doPrefLayout
	 * @see org.joshy.gfx.node.layout.Panel#doPrefLayout()
	 ************************************************************************/
	@Override
	public void doPrefLayout() {
		super.doPrefLayout();
		synchronized(this) {
			Dimension prefSize = getSizeImpl(this, LayoutUtil.PREF);
			setWidth(prefSize.width);
	        setHeight(prefSize.height);
	        sizeInfo.width = prefSize.width;
	        sizeInfo.height = prefSize.height;
		}
	}

	// Implementation method that does the job.
	private Dimension getSizeImpl(Container parent, int sizeType)
	{
		checkCache(parent);
		int w = LayoutUtil.getSizeSafe(grid != null ? grid.getWidth() : null, sizeType);
		int h = LayoutUtil.getSizeSafe(grid != null ? grid.getHeight() : null, sizeType);

		return new Dimension(w, h);
	}
	
	/*************************************************************************
	 * Adds a component with the given constraints.
	 * @param c
	 * @param componentConstraint
	 * @return
	 ************************************************************************/
	
	public MigPanel add(Control c, CC componentConstraint) {
		synchronized(this) {
			this.nextCellConstraints = null;
			this.add(c);
			setComponentConstraintsImpl(c, componentConstraint, true);
		}
		return this;
	}
	
	/*************************************************************************
	 * Adds a component with the given constraints.
	 * @param c
	 * @param componentConstraint
	 * @return
	 ************************************************************************/
	
	public MigPanel add(Control c, String componentConstraint) {
		synchronized(this) {
			this.nextCellConstraints = null;
			this.add(c);
			setComponentConstraintsImpl(c, componentConstraint, true);
		}
		return this;
	}
	
	/*************************************************************************
	 * Creates some constraints that will be applied to the next control that
	 * is added to this container with the add(Node n) method.
	 * If you add a new Control with an add method that accepts also own constraints
	 * then these will override the previously set constraints. 
	 * @return the constraints object to use for the next cell. 
	 ************************************************************************/
	
	public CC nextCell() {
		this.nextCellConstraints = new CC(this);
		return nextCellConstraints;
	}

	/*************************************************************************
	 * add
	 * @see org.joshy.gfx.node.layout.Container#add(org.joshy.gfx.node.Node)
	 ************************************************************************/
	@Override
	public Container add(Node node) {
		CC cellConstraints = this.nextCellConstraints;
		this.nextCellConstraints = null;
		if (cellConstraints != null && node instanceof Control) {
			return add((Control) node, cellConstraints);
		} else {
			return super.add(node);
		}
	}
	
	
	// ************************************************
	// Persistence Delegate and Serializable combined.
	// ************************************************
	
	private Object readResolve()
	{
		return LayoutUtil.getSerializedObject(this);
	}

	/*************************************************************************
	 * readExternal
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 ************************************************************************/
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(in));
	}

	/*************************************************************************
	 * writeExternal
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 ************************************************************************/
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		if (getClass() == MigPanel.class)
			LayoutUtil.writeAsXML(out, this);
	}

	private static class MyDebugRepaintListener implements ActionListener
	{
		private final WeakReference<MigPanel> layoutRef;

		private MyDebugRepaintListener(MigPanel layout)
		{
			this.layoutRef = new WeakReference<MigPanel>(layout);
		}

		public void actionPerformed(ActionEvent e)
		{
			MigPanel layout = layoutRef.get();
			if (layout != null && layout.grid != null)
				layout.grid.paintDebug();
		}
	}
}