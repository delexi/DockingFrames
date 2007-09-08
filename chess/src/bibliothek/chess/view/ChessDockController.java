package bibliothek.chess.view;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DefaultDockRelocator;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.security.SecureDockController;

/**
 * A controller which replaces its {@link DockRelocator} in order to start
 * the drag &amp; drop operation as soon as the mouse is pressed. The mouse
 * does not have to be dragged like the in the original controller.
 * @author Benjamin Sigg
 *
 */
public class ChessDockController extends SecureDockController {
    /**
     * Creates a new controller
     */
    public ChessDockController() {
        super( false );
        initiate();
    }

    @Override
    protected DockRelocator createRelocator() {
        return new DefaultDockRelocator( this ){
            {
                setDragDistance( 0 );
            }
            
            @Override
            protected Reaction dragMousePressed( int x, int y, int dx, int dy, int modifiers, Dockable dockable ) {
                Reaction reaction = super.dragMousePressed( x, y, dx, dy, modifiers, dockable );
                if( reaction == Reaction.CONTINUE || reaction == Reaction.CONTINUE_CONSUMED ){
                    return createRemote( dockable ).drag( x, y, modifiers );
                }
                
                return reaction;
            }
        };
    }
}
