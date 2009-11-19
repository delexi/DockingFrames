/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.themes.basic.action;

import javax.swing.Icon;

import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A simple implementation of {@link BasicButtonModelListener} forwarding
 * any event to {@link #changed()}.
 * @author Benjamin Sigg
 */
public class BasicButtonModelAdapter implements BasicButtonModelListener{

	public void disabledIconChanged( BasicButtonModel model, Icon oldIcon, Icon icon ){
		changed();
	}

	public void enabledStateChanged( BasicButtonModel model, boolean enabled ){
		changed();
	}

	public void iconChanged( BasicButtonModel model, Icon oldIcon, Icon icon ){
		changed();
	}

	public void mouseInside( BasicButtonModel model, boolean mouseInside ){
		changed();
	}

	public void mousePressed( BasicButtonModel model, boolean mousePressed ){
		changed();
	}

	public void orientationChanged( BasicButtonModel model, Orientation old, Orientation orientation ){
		changed();
	}

	public void selectedDisabledIconChanged( BasicButtonModel model, Icon oldIcon, Icon icon ){
		changed();
	}

	public void selectedIconChanged( BasicButtonModel model, Icon oldIcon, Icon icon ){
		changed();
	}

	public void selectedStateChanged( BasicButtonModel model, boolean selected ){
		changed();
	}

	public void tooltipChanged( BasicButtonModel model, String old, String tooltip ){
		changed();
	}

	public void triggered(){
		// nothing
	}

	/**
	 * Called by all methods except {@link #triggered()} of this
	 * adapter.
	 */
	protected void changed(){
		// nothing
	}
}