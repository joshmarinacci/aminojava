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

import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.util.IdentityHashMap;


import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.NodeUtils;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.control.ListView;
import org.joshy.gfx.node.control.PopupMenuButton;
import org.joshy.gfx.node.control.ProgressBar;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.node.control.Scrollbar;
import org.joshy.gfx.node.control.Slider;
import org.joshy.gfx.node.control.SpinBox;
import org.joshy.gfx.node.control.TableView;
import org.joshy.gfx.node.control.TextControl;
import org.joshy.gfx.node.control.Textarea;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.control.Togglebutton;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.mig.layout.ComponentWrapper;
import org.joshy.gfx.node.layout.mig.layout.ContainerWrapper;
import org.joshy.gfx.node.layout.mig.layout.PlatformDefaults;
import org.joshy.gfx.node.shape.Rectangle;

/**
 * A wrapper to wrap the amino components for usage in miglayout.
 */
public class AminoComponentWrapper implements ComponentWrapper
{
	private static boolean vp = true;

	private final Control c;
	private int compType = TYPE_UNSET;
	private boolean prefCalled = false;

	public AminoComponentWrapper(Control c)
	{
		this.c = c;
	}

	public final int getBaseline(int width, int height)
	{
		return (int) c.getBaseline();
	}

	public final Object getComponent()
	{
		return c;
	}

	/** Cache.
	 */
	private final static IdentityHashMap<Font, Point.Float> FM_MAP = new IdentityHashMap<Font, Point.Float>(4);
	private final static Font SUBST_FONT =  Font.DEFAULT;

	public final float getPixelUnitFactor(boolean isHor)
	{
		switch (PlatformDefaults.getLogicalPixelBase()) {
			case PlatformDefaults.BASE_FONT_SIZE:
				Font font = SUBST_FONT;
				if (c instanceof TextControl) {
					font = ((TextControl)c).getFont();
				}
				Point.Float p = FM_MAP.get(font);
				if (p == null) {
					double fw = font.calculateWidth("X");
					double fh = font.calculateHeight("X");
					p = new Point.Float((float)(fw / 6f), (float)(fh / 13.27734375f));
					FM_MAP.put(font, p);
				}
				return isHor ? p.x : p.y;

			case PlatformDefaults.BASE_SCALE_FACTOR:

				Float s = isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
				if (s != null)
					return s.floatValue();
				return (isHor ? getHorizontalScreenDPI() : getVerticalScreenDPI()) / (float) PlatformDefaults.getDefaultDPI();

			default:
				return 1f;
		}
	}

//	/** Cache.
//	 */
//	private final static IdentityHashMap<FontMetrics, Point.Float> FM_MAP2 = new IdentityHashMap<FontMetrics, Point.Float>(4);
//	private final static Font SUBST_FONT2 = new Font("sansserif", Font.PLAIN, 11);
//
//	public float getDialogUnit(boolean isHor)
//	{
//		Font font = c.getFont();
//		FontMetrics fm = c.getFontMetrics(font != null ? font : SUBST_FONT2);
//		Point.Float dluP = FM_MAP2.get(fm);
//		if (dluP == null) {
//			float w = fm.charWidth('X') / 4f;
//			int ascent = fm.getAscent();
//			float h = (ascent > 14 ? ascent : ascent + (15 - ascent) / 3) / 8f;
//
//			dluP = new Point.Float(w, h);
//			FM_MAP2.put(fm, dluP);
//		}
//		return isHor ? dluP.x : dluP.y;
//	}

	public final int getX()
	{
		return (int) c.getTranslateX();
	}

	public final int getY()
	{
		return (int) c.getTranslateY();
	}

	public final int getHeight()
	{
		return (int) c.getHeight();
	}

	public final int getWidth()
	{
		return (int) c.getWidth();
	}

	public final int getScreenLocationX()
	{
		Point2D p = NodeUtils.convertToScreen(c, 0, 0);
		return (int) p.getX();
	}

	public final int getScreenLocationY()
	{
		Point2D p = NodeUtils.convertToScreen(c, 0, 0);
		return (int) p.getY();
	}

	public final int getMinimumHeight(int sz)
	{
		return 10; // TODO rossi 14.12.2010 does amino support this.
	}

	public final int getMinimumWidth(int sz)
	{
		return 10; // TODO rossi 14.12.2010 does amino support this.
	}
	public final int getPreferredHeight(int sz)
	{
		if (prefCalled == false) {
			c.doPrefLayout(); // To defeat a bug where the minimum size is different before and after the first call to getPreferredSize();
			prefCalled = true;
		}
		
		return (int) c.getHeight();
	}

	public final int getPreferredWidth(int sz)
	{
		if (prefCalled == false) {
			c.doPrefLayout(); // To defeat a bug where the minimum size is different before and after the first call to getPreferredSize();
			prefCalled = true;
		}

		return (int) c.getWidth();
	}

	public final int getMaximumHeight(int sz)
	{
		return Short.MAX_VALUE;
	}

	public final int getMaximumWidth(int sz)
	{
		return Short.MAX_VALUE;
	}

	public final ContainerWrapper getParent()
	{
		Container p = (Container) c.getParent();
		return p != null ? new AminoContainerWrapper(p) : null;
	}

	public final int getHorizontalScreenDPI()
	{
		return PlatformDefaults.getDefaultDPI();
	}

	public final int getVerticalScreenDPI()
	{
		return PlatformDefaults.getDefaultDPI();
	}

	public final int getScreenWidth()
	{
		try {
			return Toolkit.getDefaultToolkit().getScreenSize().width;
		} catch (HeadlessException ex) {
			return 1024;
		}
	}

	public final int getScreenHeight()
	{
		try {
			return Toolkit.getDefaultToolkit().getScreenSize().height;
		} catch (HeadlessException ex) {
			return 768;
		}
	}

	public final boolean hasBaseline()
	{
		return true; // TODO rossi 14.12.2010 is Control.getBaseline reliable yet?
	}

	public final String getLinkId()
	{
		return c.getId();
	}

	public final void setBounds(int x, int y, int width, int height)
	{
		c.setTranslateX(x);
		c.setTranslateY(y);
		c.setWidth(width);
		c.setHeight(height);
	}

	public boolean isVisible()
	{
		return c.isVisible();
	}

	public final int[] getVisualPadding()
	{
		if (vp) {
			Bounds lb = c.getLayoutBounds();
			Bounds vb = c.getVisualBounds();
			return new int[]{
				(int) (lb.getX()-vb.getX()), 
				(int) (lb.getY()-vb.getY()), 
				(int) (lb.getX2()-vb.getX2()), 
				(int) (lb.getY2()-vb.getY2())
			};
		}
		return null;
	}

	public static boolean isVisualPaddingEnabled()
	{
		return vp;
	}

	public static void setVisualPaddingEnabled(boolean b)
	{
		vp = b;
	}

	public final void paintDebugOutline()
	{
		Container container = (Container) ((Control)getComponent()).getParent();
		Rectangle frame = new Rectangle(getX(), getY(), getWidth(), getHeight());
		frame.setFill(new FlatColor(0, 0));
		frame.setStroke(FlatColor.BLUE);
		container.add(frame);
	}

	public int getComponetType(boolean disregardScrollPane)
	{
		if (compType == TYPE_UNSET)
			compType = checkType(disregardScrollPane);

		return compType;
	}

	public int getLayoutHashCode()
	{
		int h = c.getLayoutBounds().hashCode();
		if (c.isVisible()) {
			h += 1324511;
		}
		String id = getLinkId();
		if (id != null) {
			h += id.hashCode();
		}
		return h;

		// Since 2.3 will check the isValid instead everything that affects that can be removed from the layout hashcode.

//		String id = getLinkId();
//		return id != null ? id.hashCode() : 1;
	}

	private final int checkType(boolean disregardScrollPane)
	{
		Control cmp = this.c;
		if (disregardScrollPane) {
			if (cmp instanceof ScrollPane) {
				cmp = (Control) ((ScrollPane)cmp).children().iterator().next();
			}
		}

		if (cmp instanceof Textbox) {
			return TYPE_TEXT_FIELD;
		} else if (cmp instanceof Label) {
			return TYPE_LABEL;
		} else if (cmp instanceof Togglebutton) {
			return TYPE_CHECK_BOX;
		} else if (cmp instanceof Button) {
			return TYPE_BUTTON;
		} else if (cmp instanceof PopupMenuButton) {
			return TYPE_LABEL;
		} else if (cmp instanceof Textarea) {
			return TYPE_TEXT_AREA;
		} else if (cmp instanceof Panel) {
			return TYPE_PANEL;
		} else if (cmp instanceof ListView) {
			return TYPE_LIST;
		} else if (cmp instanceof TableView) {
			return TYPE_TABLE;
//		} else if (c instanceof Separator) {
//			return TYPE_SEPARATOR;
		} else if (cmp instanceof SpinBox) {
			return TYPE_SPINNER;
		} else if (cmp instanceof ProgressBar) {
			return TYPE_PROGRESS_BAR;
		} else if (cmp instanceof Slider) {
			return TYPE_SLIDER;
		} else if (cmp instanceof ScrollPane) {
			return TYPE_SCROLL_PANE;
		} else if (cmp instanceof Scrollbar) {
			return TYPE_SCROLL_BAR;
		} else if (cmp instanceof Container) {
			return TYPE_CONTAINER;
		}
		return TYPE_UNKNOWN;
	}

	public final int hashCode()
	{
		return getComponent().hashCode();
	}

	public final boolean equals(Object o)
	{
		if (o instanceof ComponentWrapper == false)
			return false;

		return getComponent().equals(((ComponentWrapper) o).getComponent());
	}
	
}
