package de.gruppe12.gui;

import java.awt.Point;
import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingUtilities;

import de.gruppe12.shared.Cell;
import de.gruppe12.shared.Move;

public class GuiController extends Observable implements Observer{
	private int[][] dummyBoard;
	boolean defendersTurn;
	private final MoveAnimation anim;
	private GameGui gui;
	
	public GuiController() {
		addObserver(this);
		defendersTurn= false;
		anim= new MoveAnimation(this);
		
		int k= 1;
		int d= 2;
		int a= 3;
		dummyBoard= new int[13][];
		
		dummyBoard[0]=  new int[]{0,0,0,0,a,a,a,a,a,0,0,0,0};
		dummyBoard[1]=  new int[]{0,0,0,0,0,0,a,0,0,0,0,0,0};
		dummyBoard[2]=  new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0};
		dummyBoard[3]=  new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0};
		dummyBoard[4]=  new int[]{a,0,0,0,0,0,d,0,0,0,0,0,a};
		dummyBoard[5]=  new int[]{a,0,0,0,0,d,d,d,0,0,0,0,a};
		dummyBoard[6]=  new int[]{a,a,0,0,d,d,k,d,d,0,0,a,a};
		dummyBoard[7]=  new int[]{a,0,0,0,0,d,d,d,0,0,0,0,a};
		dummyBoard[8]=  new int[]{a,0,0,0,0,0,d,0,0,0,0,0,a};
		dummyBoard[9]=  new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0};
		dummyBoard[10]= new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0};
		dummyBoard[11]= new int[]{0,0,0,0,0,0,a,0,0,0,0,0,0};
		dummyBoard[12]= new int[]{0,0,0,0,a,a,a,a,a,0,0,0,0};
	}
	
	protected void setGameGui(GameGui gui) {
		this.gui= gui;
	}
	
	protected boolean isPlayersTurn(int cellX, int cellY) {
		boolean result=false;
		if (((dummyBoard[cellX][cellY]==2 || dummyBoard[cellX][cellY]==1) && defendersTurn) || (
		dummyBoard[cellX][cellY]==3 && !defendersTurn))
		{
			result= true;
		}
		return result;
	}
	
	protected int[][] getBoard() {
		return dummyBoard;
	}
	
	protected String getLog() {
		//TODO: zeug
		return null;
	}
	
	protected void doMove(int srcX, int srcY, int destX, int destY) {
		//###############
		dummyBoard[destX][destY]= dummyBoard[srcX][srcY];
		dummyBoard[srcX][srcY]= 0;
		
		setChanged();
		notifyObservers(new Move(new Cell(srcX, srcY, null), new Cell(destX, destY, null)));
		defendersTurn= !defendersTurn;
		//###############
	}

	@Override
	public void update(Observable obsSrc, Object obj) {
		if (obj instanceof Move) {
			Move move= (Move)obj;
			Point sourceCell= new Point(move.getFromCell().getCol(), move.getFromCell().getRow());
			Point destCell= new Point(move.getToCell().getCol(), move.getToCell().getRow());
			anim.startAnimation(sourceCell, destCell);
		}
	}
	
	protected void update() {
		gui.redraw();
	}
	
	protected MoveAnimation getAnimation() {
		return anim;
	}

}