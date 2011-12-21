package bibliothek.gui.dock.station.toolbar.group;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentListener;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.toolbar.title.ColumnDockActionSource;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemStrategyListener;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This is a helper class enabling support for {@link ExpandedState} of the children of a {@link ToolbarGroupDockStation}.
 * This class provides all the tasks that may be usefull:
 * <ul>
 * 	<li>It makes sure that during drag and drop operations the dragged dockable changes its state if necessary</li>
 *  <li>It provides a {@link ColumnDockActionSource} to allow implementation of a {@link DockTitle} showing
 *  actions for the different columns.</li>
 * </ul> 
 * @author Benjamin Sigg
 */
public class ToolbarGroupExpander {
	private Actions actions;
	private ToolbarGroupDockStation station;
	private DockController controller;

	private ColumnHandler columnHandler = new ColumnHandler();
	private StrategyHandler strategyHandler = new StrategyHandler();
	
	private PropertyValue<ExpandableToolbarItemStrategy> strategy = new PropertyValue<ExpandableToolbarItemStrategy>( ExpandableToolbarItemStrategy.STRATEGY ){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue, ExpandableToolbarItemStrategy newValue ){
			if( oldValue != null ){
				oldValue.removeExpandedListener( strategyHandler );
			}
			if( newValue != null ){
				newValue.addExpandedListener( strategyHandler );
				columnHandler.validateAll();
			}
		}
	};
	
	/**
	 * Creates a new expander
	 * @param station the station for which this expander will work
	 */
	public ToolbarGroupExpander( ToolbarGroupDockStation station ){
		this.station = station;
		station.getColumnModel().addListener( new ToolbarColumnModelListener<StationChildHandle>(){
			@Override
			public void removed( ToolbarColumnModel<StationChildHandle> model, ToolbarColumn<StationChildHandle> column, int index ){
				column.removeListener( columnHandler );
			}
			
			@Override
			public void inserted( ToolbarColumnModel<StationChildHandle> model, ToolbarColumn<StationChildHandle> column, int index ){
				column.addListener( columnHandler );
			}
		});
	}

	/**
	 * Sets the {@link DockController} in whose realm this expander works
	 * @param controller the controller
	 */
	public void setController( DockController controller ){
		if( this.controller != controller ) {
			if( actions != null ) {
				actions.setModel( null );
				actions.destroy();
				actions = null;
			}
			this.controller = controller;
			strategy.setProperties( controller );
			if( controller != null ) {
				actions = new Actions( controller );
				actions.setModel( station.getColumnModel() );
			}
		}
	}

	/**
	 * Gets a {@link ColumnDockActionSource} which allows users to change the {@link ExpandedState} of an entire
	 * column of {@link Dockable}s.
	 * @return the source
	 */
	public ColumnDockActionSource getActions(){
		return actions;
	}
	
	private ExpandableToolbarItemStrategy getStrategy(){
		return strategy.getValue();
	}
	
	private class StrategyHandler implements ExpandableToolbarItemStrategyListener{
		@Override
		public void enablementChanged( Dockable item, ExpandedState state, boolean enabled ){
			ToolbarColumn<StationChildHandle> column = station.getColumnModel().getColumn( item );
			if( column != null ){
				columnHandler.validate( column );
			}
		}
		
		@Override
		public void expanded( Dockable item ){
			// ignore
		}
		
		@Override
		public void shrunk( Dockable item ){
			// ignore			
		}
		
		@Override
		public void stretched( Dockable item ){
			// ignore
		}
	}
	
	/**
	 * Ensures that all the items of a column have the same {@link ExpandedState}.
	 */
	private class ColumnHandler implements ToolbarColumnListener<StationChildHandle>{
		@Override
		public void inserted( ToolbarColumn<StationChildHandle> column, StationChildHandle item, Dockable dockable, int index ){
			ExpandableToolbarItemStrategy strategy = getStrategy();
			if( strategy != null ){
				int count = 0;
				int length = column.getDockableCount();
				ExpandedState state = null;
				while( count < length && state == null ){
					if( count != index ){
						state = strategy.getState( column.getDockable( count++ ) );
					}
					else{
						count++;
					}
				}
				if( state != null ){
					set( strategy, dockable, state );
				}
			}
		}

		@Override
		public void removed( ToolbarColumn<StationChildHandle> column, StationChildHandle item, Dockable dockable, int index ){
			// ignore
		}
		
		public void validateAll(){
			ToolbarColumnModel<StationChildHandle> model = station.getColumnModel();
			for( int i = 0, n = model.getColumnCount(); i<n; i++ ){
				validate( model.getColumn( i ));
			}
		}
		
		/**
		 * Tries to ensure that all items in <code>column</code> have the same {@link ExpandedState}, namely the
		 * {@link ExpandedState} of the first item.
		 * @param column the column to validate
		 */
		public void validate( ToolbarColumn<StationChildHandle> column ){
			ExpandableToolbarItemStrategy strategy = getStrategy();
			if( strategy != null ){
				int index = 0;
				int length = column.getDockableCount();
				ExpandedState state = null;
				while( index < length && state == null ){
					state = strategy.getState( column.getDockable( index++ ) );
				}
				while( index < length ){
					Dockable item = column.getDockable( index++ );
					set( strategy, item, state );
				}
			}
		}
		
		private void set( ExpandableToolbarItemStrategy strategy, Dockable item, ExpandedState state ){
			if( strategy.isEnabled( item, state )){
				strategy.setState( item, state );
				return;
			}
			ExpandedState smaller = state;
			while( smaller != smaller.smaller() ){
				smaller = smaller.smaller();
				if( strategy.isEnabled( item, smaller )){
					strategy.setState( item, smaller );
					return;
				}
			}
			ExpandedState larger = state;
			while( larger != larger.larger() ){
				larger = larger.larger();
				if( strategy.isEnabled( item, larger )){
					strategy.setState( item, larger );
					return;
				}
			}
		}
	}
	
	/**
	 * The actions that are shown over the columns.
	 */
	private class Actions extends ExpandToolbarGroupActions<StationChildHandle> {
		public Actions( DockController controller ){
			super( controller, station );
		}

		@Override
		protected void uninstallListener( StationChildHandle item, ComponentListener listener ){
			item.getDockable().getComponent().removeComponentListener( listener );
		}

		@Override
		protected void installListener( StationChildHandle item, ComponentListener listener ){
			item.getDockable().getComponent().addComponentListener( listener );
		}

		@Override
		protected Rectangle getBoundaries( StationChildHandle item ){
			Rectangle result = getBoundaries( item.getDockable().getComponent() );
			Rectangle title = null;
			if( item.getTitle() != null ) {
				title = getBoundaries( item.getTitle().getComponent() );
				result = result.union( title );
			}
			return result;
		}

		private Rectangle getBoundaries( Component component ){
			Rectangle bounds = component.getBounds();
			if( SwingUtilities.isDescendingFrom( component, station.getComponent() ) ) {
				return SwingUtilities.convertRectangle( component.getParent(), bounds, station.getComponent() );
			}
			else {
				return null;
			}
		}
	}
}
