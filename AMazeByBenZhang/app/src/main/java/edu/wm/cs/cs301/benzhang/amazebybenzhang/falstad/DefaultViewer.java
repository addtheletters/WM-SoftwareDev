package edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad;

import edu.wm.cs.cs301.benzhang.amazebybenzhang.falstad.Constants.StateGUI;

/**
 * This is a default implementation of the Viewer interface
 * with methods that do nothing but providing debugging output
 * such that subclasses of this class can selectively overwrite
 * those methods that are truly needed.
 * 
 * TODO: use logger instead of Sys.out
 * 
 * @author Kemper
 *
 */
public class DefaultViewer implements Viewer {

	@Override
	public void redraw(MazePanel mazePanel, StateGUI state, int px, int py,
					   int view_dx, int view_dy, int walk_step, int view_offset, RangeSet rset, int ang) {
		dbg("redraw") ;
	}

	@Override
	public void incrementMapScale(int amount) {
		dbg("incrementMapScale") ;
	}

	@Override
	public void decrementMapScale(int amount) {
		dbg("decrementMapScale") ;
	}


	private void dbg(String str) {
		//System.out.println("DefaultViewer" + str);
	}
}
