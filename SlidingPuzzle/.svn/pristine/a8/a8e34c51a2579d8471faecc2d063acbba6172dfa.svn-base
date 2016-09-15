package edu.wm.cs.cs301.slidingpuzzle;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import edu.wm.cs.cs301.slidingpuzzle.PuzzleState.Operation;

import java.io.*;
import java.lang.Integer;

/**
 * A simple sliding puzzle game implementation.
 * 
 * Original source code downloaded from 
 * http://freesourcecode.net/javaprojects/72834/Sliding-puzzle-game-in-java#.Vv16t2PGNSU
 * 
 * Code has been refactored to work with PuzzleState interface to encapsulate state
 * representation and connection between states.
 */
public class PuzzleGameGUI extends Frame implements ActionListener, MouseListener, MouseMotionListener, ItemListener{
	// Serial version UID not used, generated to make static code checker happy.
	private static final long serialVersionUID = 1L;
	
	// UI elements
    private MenuBar menuBar;
    private Menu fileMenu, editMenu, viewMenu;
    private MenuItem mi;
    private CheckboxMenuItem automatic;
    private CheckboxMenuItem lines;
    private CheckboxMenuItem numbers;
    private CheckboxMenuItem drag;
    
    // Game representation
    private PuzzleState state; // current state of the game
    private PuzzleState finalState; // the initial state is the same as the final state where all tiles are ordered.
    // optional: user loads a picture, we store 15 square fragments in images[]
    // each tile has a number value (range 1,2,...,15) that has a corresponding little image
    // get/set methods encapsulate access to array
    private BufferedImage[] images = new BufferedImage[15];
    
    // Default setting: 4x4 board with 100x100 pixel squares and a 50 pixel border frame.
    // total dimensions: 500x500 as it is 500 = 4 x 100 + 2 x 50 for overall width and height
    // dimensions: w=width, h=height for each little square
    // keeping width and height separate is better for readability 
    // and naturally supports rectangle as well
    // dimensions may temporally change if image is loaded
    private int h = 100;
    private int w = 100;
    private int offX = 50;
    private int offY = 50;
    // number of empty squares, user can choose between 1,2, and 3. 
    // Default of 1 matches the common game setting
    private int emptysquares = 1; 
    
    // two squares or tiles need to be stored for a drag operation
    // the square where a movement starts: the selected square
    // the square where a movement currently is or finishes: the checksquare
    // Point is abused to store (x,y) coordinates, it is not used as a awt graphics object
    private Point selectedsquare;
    private Point checksquare;
    private boolean dontchange = false;
    private boolean close = true;

        
   /**
     * Constructor sets up UI and default board with numbers
     */
    public PuzzleGameGUI(){
    	super("Sliding Puzzle Game");
        //Create UI elements
        setupMenuBar();
        // Listener        
        addMouseListener(this); 
        addMouseMotionListener(this);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                cleanUpForTermination();
            }
        });
        
        setDimensions();
        resizeWindow();
        state = new SimplePuzzleState();
        setFinalState();
        initBoard(); 
    }
    
    //////////////////////////////////////////////////////////////////////////////////// 
    ///////////////////////////// Set up UI ////////////////////////////////////////////
    
    /**
     * Fills the menubar with pulldown menus for filehandling, 
     * organizing the board, setting visibility of lines and numbers
     * This object serves as the one and only listener for all menu items.
     */
	private void setupMenuBar() {
		setMenuBar(menuBar = new MenuBar());
        menuBar.add(fileMenu=new Menu("File"));
        fileMenu.add(mi=new MenuItem("Open"));
        mi.addActionListener(this);
        fileMenu.add(mi=new MenuItem("Close"));
        mi.addActionListener(this);
        fileMenu.add(mi=new MenuItem("Exit"));
        mi.addActionListener(this);
        menuBar.add(editMenu=new Menu("Edit"));
        editMenu.add(mi=new MenuItem("Shuffle"));
        mi.addActionListener(this);
        editMenu.add(mi=new MenuItem("Sort"));
        mi.addActionListener(this);
        editMenu.add(drag=new CheckboxMenuItem("Drag Mode"));
        drag.addItemListener(this);
        editMenu.add(automatic=new CheckboxMenuItem("Auto Mode"));
        drag.addItemListener(this);
        editMenu.add(mi=new MenuItem("Empty Squares"));
        mi.addActionListener(this);
        menuBar.add(viewMenu=new Menu("View"));
        viewMenu.add(lines=new CheckboxMenuItem("Lines", true));
        lines.addItemListener(this);
        viewMenu.add(numbers=new CheckboxMenuItem("Numbers", true));
        numbers.addItemListener(this);
	}
	/**
	 * Calculates and sets the overall window size from the size of a single square
	 */
	private void resizeWindow() {
		int width = 4*w + 2*offX;
		int height = 4*h + 2*offY;
		setBounds(0,0,width,height);
	}
	/**
	 * Sets dimensions to default values.
	 * Dimensions may change if a picture is loaded from file.
	 */
	private void setDimensions() {
	    h = 100;
	    w = 100;
	    offX = 100;
	    offY = 100;
	}
	/**
	 * Method encapsulates code to terminate application.
	 */
    private void cleanUpForTermination() {
    	setVisible(false); 
        dispose();
        System.exit(0);
    }
	////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////// UI Dialogs ////////////////////////////////////////////
	/**
	 * Operates a pop up dialog to congratulate the user
	 * for solving the game.
	 */
	private void runWinningDialog() {
        // IO Labels
        Object[] options = {"Yes", "No"};
        String title = "Congrulations";
        String text = "Hooray! You won!\nWould you like to continue?";
        
        int n = JOptionPane.showOptionDialog(this, text, title,
        	    JOptionPane.YES_NO_CANCEL_OPTION,
        	    JOptionPane.QUESTION_MESSAGE,
        	    null, options, options[0]);
        switch (n) {
        case 0: // yes
        	shuffleBoard();
            repaint();
        	break ;
        case 1: // no
        default: // same as case 1, means no
        	cleanUpForTermination();
        	break ;
        }
	}
	/**
	 * Operates a pop up dialog to let the user
	 * select the number of empty squares for the game.
	 * The board and display are updated accordingly.
	 * Changes field emptysquares.
	 */
	private void runDialogForEmpties() {
        JDialog empty = new JDialog() ;
        // IO Labels
        Object[] possibilities = {"1", "2", "3"};
        String title = "Set number of empty squares:";
        String text = "How many empty squares do you want?";
        Object initialValue = possibilities[0];
        Icon icon = null ;
        
        String s = (String)JOptionPane.showInputDialog(empty, text, title, 
        		JOptionPane.QUESTION_MESSAGE, icon, possibilities, initialValue);

        // Case 1: User clicks ok
        // grab value and update game
        if ((s != null) && (s.length() > 0)) {
            //System.out.println("Squares selected: " + s + "!");
            int val = Integer.parseInt(s);
            if (val == emptysquares)
            	return; // no change, done
            // update internal state
            emptysquares = val;
            setFinalState(); // influenced by emptysquares
            initBoard();
            repaint();
            // drag operation is only interesting if one can 
            // move a square over a number of empty slots
            if (emptysquares>1) 
            	drag.setState(true);
            else 
            	drag.setState(false);
            return;
        }
        // Case 2: User clicks cancel
        // No need to change current settings.
        // System.out.println("None selected");
	}
	
	/**
	 * Operates a pop up dialog to let the user
	 * select a file with a jpg figure to display.
	 * The board and display are updated accordingly.
	 */
	private void runFigureSelectionDialog() {
		//Create a file chooser
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		// for convenience, try to get to the pictures directory
		// for the Eclipse IDE setup
		// otherwise try the current working directory
		String workingdir = System.getProperty("user.dir");
		File file = new File(workingdir+"/pictures") ;
		if (null == file || !file.isDirectory())
			file = new File(workingdir) ; 
		if (null != file || file.isDirectory())
			fc.setCurrentDirectory(file);
		// proceed with or without directory being updated
		// the default setting is the home directory 
		// which is a reasonable fall back
		int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            //This is where we can use and open the file.
        	File f = fc.getSelectedFile();
            System.out.println("Opening: " + f.getName() + "." + "\n");
            loadImageFromFile(f); // reads f
        } else {
        	System.out.println("Open command cancelled by user." + "\n");
        }
	}



	////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////// File I/O /////////////////////////////////////////////
    /**
     * Loads an image from the given file and prepares a 4x4 board of images.
     * Method also resizes and updates the screen.
     * @param f is the file to load the image from
     */
	private void loadImageFromFile(File f) {
		BufferedImage image = null ;
		try {
            image = ImageIO.read(f);
            // Special case: file can be parsed but does not contain an image
    		if (null == image) {
    			throw new Exception() ;
    		}
        }
        catch (Exception e){
        	// handles IO exceptions, e.g. file not found exception
        	JOptionPane.showMessageDialog(this,
					"Sorry, could not load an image from selected file.\nPlease try again and choose a jpg file for example.",
				    "Error message:",
				    JOptionPane.ERROR_MESSAGE);
			return ;
        }
		// General case: file exists and contains an image
		// split the image into 16 square pieces stored in field images[]
		// resize the figure
		setUpImages(image);
		resizeWindow();
        // don't show numbers with pictures but show lines
        lines.setState(true);
        numbers.setState(false);
        close=false;
        repaint();
	}
	/**
	 * Split image into a 16 square fragments for a 4 x 4 board.
	 * If image is rectangular, we select the largest possible square image
	 * that is contained in the overall rectangular image.
	 * @param image whose pieces are used to put on display
	 */
	private void setUpImages(BufferedImage image) {
		// if figures are rectangular, 
		// select a square that has the same center point
		// (cx,cy) coordinates of center point
		int cx = image.getWidth()/2;
		int cy = image.getHeight()/2;
		// get the min of height and width as the maximum height and width of the square
		int min = (image.getWidth() < image.getHeight()) ? image.getWidth(): image.getHeight() ;
		// w, h is width == height of each little square in the 4x4 board
		// as these values are needed elsewhere, we store them in fields
		w = min/4;
		h = min/4;
		// calculate offset, the top-left corner of a square that fits
		// (ox,oy) are coordinates for the top-left corner for the part of the figure we want
		int ox = cx-2*w;
		int oy = cy-2*h;
		// split figure
		int m = 1 ;
		for (int r=0;r<=3;r++){
			for (int c=0;c<=3;c++) {
				if (m==16) {
					break; // skip the last one as it remains empty and stop
				}
				// note: m is the current value of the tile, range 1,2,...,15
				setImageForValue(m, image.getSubimage(ox+c*w, oy+r*h, w, h));
				m++;
			}
		}
	}
    /**
     * For a tile with the given value, this method stores the corresponding image.
     * @param value has a range 1,2,...,15
     * @param image is the image fragment to store
     */
	private void setImageForValue(int value, BufferedImage image) {
		images[value-1] = image;
	}
    /**
     * For a tile with the given value, this method retrieves the corresponding image.
     * @param value has a range 1,...,15
     * @return image is the image fragment for the given tile
     */
	private BufferedImage getImageForValue(int value) {
		return images[value-1];
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////// Methods that override inherited methods or interfaces ///////
    /**
     * Draw the board with squares, pictures, lines, numbers, highlighting.
     */
    @Override
    public void paint(Graphics g){
    	int value;
        for (int r=0;r<=3;r++){
            for (int c=0;c<=3;c++){
            	value = state.getValue(r, c);
                if (value == 0) {
                    //empty square
                    g.setColor(Color.gray);
                    g.fillRect(getTopLeftX(c), getTopLeftY(r),w,h);
                }
                else{
                    if (!close) {
                        //images
                    	g.drawImage(getImageForValue(value),getTopLeftX(c),getTopLeftY(r),null);
                    }
                    if (numbers.getState()){
                        //Numbers
                        g.setColor(Color.black);
                        g.drawString(""+value,getCenterX(c),getCenterY(r));
                    }
                }
            }
        }
        // draw grid lines
        if (lines.getState()){
            for (int i=0;i<=4;i++){
                //Lines
                g.setColor(Color.black);
                g.drawLine(offX,i*h+offY,w*4+offX,i*h+offY);
                g.drawLine(i*w+offX, offY, i*w+offX, (h*4)+offY);
            }
        }
        // if squares are dragged, highlight selected ones with a red frame
        if (selectedsquare!=null){
            //Paint Selected Square with red border
            g.setColor(Color.red);
            g.drawRect(getTopLeftX(selectedsquare.x), getTopLeftY(selectedsquare.y),w,h);
            if (checksquare!=null){
                //Paint check square with red border
                g.drawRect(getTopLeftX(checksquare.x), getTopLeftY(checksquare.y),w,h);
            }
        }
    }

	////////////////////////////////////////////////////////////////////////////////////////
	//////////////////// Methods for ActionListener interface //////////////////
	/**
	 * Listener method, called by GUI whenever user provides input
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
        String item = ae.getActionCommand();
        if (item.equals("Open")){
            runFigureSelectionDialog() ;
        }
        else if (item.equals("Close")){
            close=true;
            lines.setState(true);
            numbers.setState(true);
            w=100;
            h=100;
            resizeWindow();
            repaint();
        }
        else if (item.equals("Exit")){
        	cleanUpForTermination();
        }
        else if (item.equals("Shuffle")){
            shuffleBoard();
            repaint();
        }
        else if (item.equals("Sort")){
            initBoard();
            repaint();
        }
        else if (item.equals("Empty Squares")){
            runDialogForEmpties();
        }
    }
	////////////////////////////////////////////////////////////////////////////////////////
    ///////////////// Methods for ItemListener interface /////////////////////////////////
	/**
	 * Called when user selects a menu item in the GUI
	 */
	@Override
    public void itemStateChanged(ItemEvent e) {
        repaint();
    }
	////////////////////////////////////////////////////////////////////////////////////////
    ///////////////// Methods for MouseListener interface /////////////////////////////////
    /**
     * If user clicks on a tile, it moves into an adjacent empty slot
     * @param e provides information for the event, 
     * e.g. if user clicked on the frame or the board.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    	Point pixelPosition = e.getPoint();
    	// If user selected the automated mode, each click will
    	// make the game perform a step towards the solution (the initial state)
    	if (automatic.getState()) {
    		// get one step closer to the final state
    		PuzzleState parent = state.getParent();
    		if (null != parent) {
    			System.out.println("Distance to finish: " + parent.getPathLength());
    			state = parent;
    			repaint();
    		}
    		else
    			System.out.println("No parent to proceed");
    		if (gameOver()){
                runWinningDialog();
            }
    	}
    	// Otherwise if user clicks on a tile
    	// we try to move the tile to some adjacent empty slot.
    	// If there are multiple options, we simply pick one.
    	else if (checkValid(pixelPosition)){
            if (moveOnClick(getSquareAt(pixelPosition))){
                repaint();
                if (gameOver()){
                    runWinningDialog();
                }
            }
        }
    }
 
    /**
     * If user drags a tile, it moves into an adjacent empty slot. 
     * This method is called when the movement starts.
     * @param e provides information for the event, 
     * e.g. if user clicked on the frame or the board.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //Check if dragging is selected and valid area on the plane
    	Point pixelPosition = e.getPoint();
        if (drag.getState() && checkValid(pixelPosition)) {
        	// make sure intermediate squares don't get updated
            dontchange=true;
            //Selects if an empty square is next to the current position 
            // and if the current position is not empty
            Point gridPosition = getSquareAt(pixelPosition);
            if(!isEmpty(gridPosition) && hasEmptyNeighbor(gridPosition)) {
                selectedsquare = gridPosition; // starting position
                repaint();
            }
            dontchange=false;
        }
    }
    /**
     * If user drags a tile, it moves into an adjacent empty slot.
     * This method is called when the movement stops.
     *  @param e provides information for the event, not used here
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (drag.getState()){
            if (selectedsquare!=null){
                if (checksquare!=null){
                    //Move selected and check square
                	if (isEmpty(checksquare)) {
                		PuzzleState tmp = state.flip(selectedsquare.y, selectedsquare.x, checksquare.y, checksquare.x);
                		if (null != tmp)
                			state = tmp;
                		else
                			System.out.println("Error: could not flip states after drag operation");
                	}
                	// reset selections and update graphics
                    checksquare=null;
                    selectedsquare=null;
                    repaint();
                    // run winning dialog only after update if needed
                    if (gameOver()){
                        runWinningDialog();
                    }
                    return ;
                }
                selectedsquare=null;
                repaint();
                
            }
        }
    }
    /**
     * Called when user drags the mouse, picks up intermediate tiles
     * @param e provides information for the event, 
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (drag.getState()) {
            if(selectedsquare != null) {
            	// starting square was selected
                dontchange=true;
                //Selects checksquare if it is valid on the plane, not the selected square, and not an already checked square.
                //This avoids repaint while it is being dragged.
                Point pixelPosition = e.getPoint();
                if (checkValid(pixelPosition)) {
                	Point gridPosition = getSquareAt(pixelPosition);
                	// intermediate position must be empty 
                	if (!isEmpty(gridPosition))
                		return;
                	// intermediate position either adjacent to starting position
                	// or previous intermediate position
                	// last position that was an empty slot
                	if (isNeighbor((null != checksquare) ? checksquare : selectedsquare, gridPosition)) {
                		// update intermediate position
                		checksquare = gridPosition;
                        repaint();
                	}
                }
                dontchange=false;
            }
        }
    }


    // unused, empty default implementations
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
    
    /////////////// internal, private methods /////////////////////
    // Helper methods to translate row and column indices into pixel coordinates
    // Note: (0,0) are the coordinates of the top-left corner of the window
    // x coordinate grows to the right, so x relates to columns
    // y coordinate grows to the bottom, so y relates to rows
    // offX and offY describe offsets to account for the frame surrounding the board
    //
    /**
     * Compute the top-left y coordinate for the current row y.
     * Used for drawing a tile.
     * @param r current row
     * @return y pixel coordinate for top left corner of this row
     */
	private int getTopLeftY(int r) {
		return r*h+offY;
	}
	/**
	 * Compute the y coordinate for the center point in this row
	 * @param r current row
	 * @return y pixel coordinate for center point of this row
	 */
	private int getCenterY(int r) {
		return r*h+(offY+h/2);
	}
    /**
     * Compute the top-left x coordinate for the current column c.
     * Used for drawing a tile.
     * @param c current column
     * @return x pixel coordinate for top left corner of this column
     */
	private int getTopLeftX(int c) {
		return c*w+offX;
	}
	/**
	 * Compute the x coordinate for the center point in this column
	 * @param c current column
	 * @return x pixel coordinate for center point of this column
	 */
	private int getCenterX(int c) {
		return c*w+(offX+w/2);
	}
	/**
	 * tell if selected position is empty
	 * @param p carries (x=column,y=row) coordinates
	 * @return true if position is an empty slot, false otherwise
	 */
    private boolean isEmpty(Point p) {
    	return state.isEmpty(p.y, p.x);
    }
	/**
	 * tell if selected position is has an adjacent position that is an empty slot
	 * @param p carries (x=column,y=row) coordinates
	 * @return true if there is a neighboring empty slot, false otherwise
	 */
    private boolean hasEmptyNeighbor(Point p) {
    	// above
    	if (p.y > 0 && state.isEmpty(p.y-1, p.x))
    		return true;
    	// below
    	if (p.y < 3 && state.isEmpty(p.y+1, p.x))
    		return true;
    	// left
    	if (p.x > 0 && state.isEmpty(p.y, p.x-1))
    		return true;
    	// right
       	if (p.x < 3 && state.isEmpty(p.y, p.x+1))
    		return true;
    	return false;
    }
    /**
     * Computes row,column coordinates for a given points wiht pixel coordinates
     * @param p graphics point selected on window
     * @return point with (column,row) index pair
     * TODO: fix this counterintuitive order
     */
    private Point getSquareAt(Point p){
        return new Point(getColumnForPixelX(p.x),getRowForPixelY(p.y));
    }
    /**
     * Compute row index that corresponds to y pixel coordinate
     * @param y pixel coordinate
     * @return row index, range 0,1,...,dimension-1
     */
	private int getRowForPixelY(int y) {
		return (y-offY)/h;
	}
    /**
     * Compute column index that corresponds to x pixel coordinate
     * @param x pixel coordinate
     * @return column index, range 0,1,...,dimension-1
     */
	private int getColumnForPixelX(int x) {
		return (x-offX)/w;
	}
	/**
	 * Check if the given two points are neighbors
	 * @param lastPoint with row column coordinates
	 * @param currentPoint with row column coordinates
	 * @return true if points are neighbors, false otherwise
	 */
	private boolean isNeighbor(Point lastPoint, Point currentPoint) {
		int last = getNumAt(lastPoint); 
		int index = getNumAt(currentPoint);
		return last-1 == index || last+1 == index || last-4 == index || last+4 == index;
	}
	/**
	 * Translate row, column coordinates into an index ranging from 0,1,..., dimension^2-1
	 * @param p with row column coordinates
	 * @return index position
	 */
	private int getNumAt(Point p){
        //returns number as it  0   1   2   3
        //is on the grid like   4   5   6   7
        //this:                 8   9   10  11
        //                      12  13  14  15
    	return p.y*4+p.x;
    }
    /**
     * Checks if a given position as pixel coordinates is on the board and not on the frame.
     * @param p pixel coordinates of a point
     * @return true if position hits the board, false if it hits the frame
     */
    private boolean checkValid(Point p){
        //Checks if point is on the image or plane
    	// min x = offX, max x = 4 w + offX
    	// min y = offY, max y = 4 h + offY
    	return ( (offX < p.x) && (p.x < 4*w + offX)) &&
    			((offY < p.y) && (p.y < 4*h + offY)) ;
    }
    /**
     * Initializes or resets the field finalState. 
     * Uses field emptysquares to adjust number of empty squares for the
     * final state. 
     */
    private void setFinalState() {
    	finalState = new SimplePuzzleState();
    	finalState.setToInitialState(4, emptysquares);
    }

    /**
     * Resets game to 4x4 board and a state where squares are ordered.
     * Resets field state, uses field emptysquares to determine number 
     * of empty squares.
     */   
    private void initBoard() {
    	state.setToInitialState(4, emptysquares);
    }
    /**
     * Perform a number of random flips between tiles to start the game.
     * Pathlength is chosen to be 15.
     */
    private void shuffleBoard(){
    	// we use 15 as path length, no particular reason.
    	// Value can be changed here as needed.
    	state = state.shuffleBoard(15);
    }
    /**
     * Attempts to move the square at the given position.
     * @param p encodes current row, column coordinates
     * @return true if movement was possible, false otherwise
     */
    private boolean moveOnClick(Point p) {
    	System.out.println("moveOnClick: " + p.y + ", " + p.x);
    	if (isEmpty(p)) {
    		System.out.println("moveOnClick: empty tile return");
    		return false;
    	}
    	// if there are several options for a movement, just pick the first one
    	PuzzleState ps;
    	for (Operation op : Operation.values()) {
    		ps = state.move(p.y,p.x, op);
    		if (null != ps) {
    			// be defensive here, if move fails and returns null, 
    			// don't destroy valid current state.
    			state = ps;
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Check termination condition: grid values form an ordered sequence 1,2,...
     * following the reading direction on the board (left to right, top to bottom)
     * @return true if all positions are in order, false otherwise
     */
    private boolean gameOver(){
    	return state.equals(finalState);
    }

    /**
     * Main method to start the application.
     * @param args not used
     */
    public static void main(String[] args){
        PuzzleGameGUI pgf = new PuzzleGameGUI();
        pgf.setVisible(true);
    }
}