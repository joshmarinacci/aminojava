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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.node.layout.mig.layout.ComponentWrapper;
import org.joshy.gfx.node.layout.mig.layout.ContainerWrapper;
import org.joshy.gfx.node.shape.Rectangle;

/**
 */
public final class AminoContainerWrapper extends AminoComponentWrapper implements ContainerWrapper
{

	public AminoContainerWrapper(Container p)
	{
		super(p);
	}

	public ComponentWrapper[] getComponents()
	{
		Container c = (Container) getComponent();
		List<ComponentWrapper> cws = new ArrayList<ComponentWrapper>();
		for (Control child : c.controlChildren()) {
			cws.add(new AminoComponentWrapper(child));
		}
		return cws.toArray(new AminoComponentWrapper[cws.size()]);
	}

	public int getComponentCount()
	{
		int count = 0;
		Iterator<? extends Control> it = ((Container)getComponent()).controlChildren().iterator();
		while (it.hasNext()) {
			count++;
			it.next();
		}
		return count;
	}

	public Object getLayout()
	{
		return this; // TODO rossi 14.12.2010 what is this for amino? 
		// ((Parent) getComponent()).getLayout();
	}

	public final boolean isLeftToRight()
	{
		return true; // TODO rossi 14.12.2010 does amino support this?
	}

	public final void paintDebugCell(int x, int y, int width, int height)
	{
		Container container = (Container) getComponent();
		Rectangle frame = new Rectangle(x, y, width, height);
		frame.setFill(new FlatColor(0, 0));
		frame.setStroke(FlatColor.RED);
		container.add(frame);
//		SwingStage stage = (SwingStage) container.getStage();
//		Window w = (Window) stage.getNativeWindow();
//
//		Graphics2D g = (Graphics2D) w.getGraphics();
//		if (g == null)
//			return;
//
//		g.setStroke(new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10f, new float[] {2f, 3f}, 0));
//		g.setPaint(DB_CELL_OUTLINE);
//		
//		Point2D pt = NodeUtils.convertToScene(container, 0, 0);
//		
//		g.drawRect((int)pt.getX()+x, (int)pt.getY()+y, width - 1, height - 1);
	}

	public int getComponetType(boolean disregardScrollPane)
	{
		return TYPE_CONTAINER;
	}

	// Removed for 2.3 because the parent.isValid() in MigLayout will catch this instead.
	public int getLayoutHashCode()
	{
		int h = super.getLayoutHashCode();
		if (isLeftToRight()) {
			h += 416343;
		}
		return 0;
	}
}
