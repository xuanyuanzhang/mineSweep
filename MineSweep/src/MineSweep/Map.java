package MineSweep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Map extends JFrame implements Runnable {

	private static int width = 9;
	private static int height = 9;
	private Grid[] Grids = new Grid[width*height+1];
	
	private static int WIDTH = 50*width + 10;
	private static int HEIGHT = 50*height + 30;
	
	boolean isWin = true;
	
	private boolean isStart;
	private boolean isStop;
	
	private long startTime;
	private long stopTime;
	
	Label label;//label that shows bombLeft.
	private int bombLeft = 10;// # of remaining bombs.
	
	//Initialize the matrix 
	private void initGrid() {
		for(int i=1; i<Grids.length; i++) {
			Grids[i] = new Grid(Grids);
			Grids[i].setBackground(Color.white);
			Grids[i].setIndexOfSquare(i);
		}
		
		for(int i=1; i<Grids.length; i++) {
			Grids[i].setNorthWest(Grids[i].getNearbyGrids(1));
			Grids[i].setNorth(Grids[i].getNearbyGrids(2));
			Grids[i].setNorthEast(Grids[i].getNearbyGrids(3));
			Grids[i].setEast(Grids[i].getNearbyGrids(4));
			Grids[i].setSouthEast(Grids[i].getNearbyGrids(5));
			Grids[i].setSouth(Grids[i].getNearbyGrids(6));
			Grids[i].setSouthWest(Grids[i].getNearbyGrids(7));
			Grids[i].setWest(Grids[i].getNearbyGrids(8));
		}
		
		// Generate 10 random numbers in 81 without repeating.
		Random r = new Random();
		Set<Integer> bombSet = new HashSet<Integer>();
		for(int i=0; i<bombLeft; i++) {
			int temp = r.nextInt(Grids.length-1);
			while(temp == 0 || bombSet.contains(temp)) {
				temp = r.nextInt(Grids.length-1);
			}
			bombSet.add(temp);
		}
		
		Iterator<Integer> iterator = bombSet.iterator();
		while(iterator.hasNext()) {
			int bombIndex = iterator.next();
			Grids[bombIndex].setBomb(true);
		}
	}
	
	// Main frame
	public void initialize() {
		this.setSize(WIDTH, HEIGHT);
		this.setLayout(new BorderLayout());
		
		Panel mainPanel = new Panel();
		mainPanel.setLayout(new GridLayout(width, height));
		Panel statePanel = new Panel();
		
		initGrid();
		
		label = new Label(Integer.toString(bombLeft));// Initially with 10 mines
		label.setSize(WIDTH, 20);
		statePanel.add(label);
		
		for(int i=1; i<Grids.length; i++) {
			Grids[i].addMouseListener(new MyMouseListener(i));
			mainPanel.add(Grids[i]);
		}
		
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(statePanel, BorderLayout.SOUTH);
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			
		});
		
		this.setVisible(true);
		
	}
	
	private int nearbyMinesFound = 0;
	public class MyMouseListener implements MouseListener {
		
		private int index;
		private Color color = Color.white;

		public MyMouseListener(int index) {
			this.index = index;
		}
		
		private void countNearbyMinesFound(Grid grid, int nearbyMinesFound){
			if(grid != null) {
				grid.setBackground(this.color);
				if(grid.isBomb() && grid.getLabel().equals("m")) {
					nearbyMinesFound++;
				}
			}
		}
		
		private void uncoverNearbyGrids(Grid grid){
			if(grid!=null && !grid.isChange() && !grid.isBomb()){
				grid.click();
				//Need to do bfs here.
				if(grid.getNearbyBombCount() == 0){
					Set<Grid> set = new HashSet<Grid>();
					Grids[index].getNorthWest().bfs(set);
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
			if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
				// bfs to invert all empty squares.
				if(Grids[index].isBomb() == false && Grids[index].getNearbyBombCount() == 0) {
					Grids[index].click();
					Set<Grid> set = new HashSet<Grid>();
					Grids[index].bfs(set);
					return;
				}
				
				if(Grids[index].click()) {
					isWin = false;
					isStop = true;// Stop timer
					stopTime = System.currentTimeMillis();
					Grids[index].setBackground(Color.yellow);
					for(int i=1; i<Grids.length; i++) {
						if(Grids[i].isBomb() && !Grids[i].isChange()) {
							Grids[i].setBackground(Color.red);
						}
					}
					JOptionPane.showMessageDialog(Map.this, "You hit a mine, the time takes is: " + (stopTime - startTime)/1000 + "s", "Warning!", JOptionPane.ERROR_MESSAGE, null);
					Map.this.dispose();
					new Map().initialize();
					
					return;
				}
			}
			
			//If you have successfully find all nearby mines, you can double click the button to clear all unclicked buttons in the nearby.
			if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				
				countNearbyMinesFound(Grids[index].getNorthWest(), nearbyMinesFound);
				countNearbyMinesFound(Grids[index].getNorth(), nearbyMinesFound);
				countNearbyMinesFound(Grids[index].getNorthEast(), nearbyMinesFound);
				countNearbyMinesFound(Grids[index].getEast(), nearbyMinesFound);
				countNearbyMinesFound(Grids[index].getSouthEast(), nearbyMinesFound);
				countNearbyMinesFound(Grids[index].getSouth(), nearbyMinesFound);
				countNearbyMinesFound(Grids[index].getSouthWest(), nearbyMinesFound);
				countNearbyMinesFound(Grids[index].getWest(), nearbyMinesFound);
				
				if(Grids[index].getNearbyBombCount() == nearbyMinesFound) {// All nearby mines have been found, do the uncover of all neighbor grids.
					uncoverNearbyGrids(Grids[index].getNorthWest());
					uncoverNearbyGrids(Grids[index].getNorth());
					uncoverNearbyGrids(Grids[index].getNorthEast());
					uncoverNearbyGrids(Grids[index].getEast());
					uncoverNearbyGrids(Grids[index].getSouthEast());
					uncoverNearbyGrids(Grids[index].getSouth());
					uncoverNearbyGrids(Grids[index].getSouthWest());
					uncoverNearbyGrids(Grids[index].getWest());
				}
				
				nearbyMinesFound = 0;
				
				return;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
			if(isStart == false) {
				isStart = true;// Timer starts.
				startTime = System.currentTimeMillis();
				new Thread(Map.this).start();
			}
			
			if(e.isPopupTrigger()) {// right click to mark a mine, change to question mark, then make it blank.
				if(!Grids[index].isChange()) {// only if it has not be inverted, it can be inverted.
					Grid d = (Grid) e.getSource();
					d.requestFocus();
					if(!d.getLabel().equals("m") && !d.getLabel().equals("?")) {
						d.setLabel("m");
						bombLeft --;
					} else if(d.getLabel().equals("m")) {
						d.setLabel("?");
						bombLeft ++;
					} else {
						d.setLabel("");
					}
					label.setText(bombLeft + "");
				}
				
				// Determine whether it is completed
				for(int i=1; i<Grids.length; i++) {
					if((Grids[i].isBomb() && !Grids[i].getLabel().equals("m")) 
							|| (!Grids[i].isBomb() && (Grids[i].getLabel().equals("m")) || Grids[i].getLabel().equals("?"))) {
						isWin = false;
						return;
					}
				}
				
				isWin = true;
				if(isWin) {
					isStop = true;// Timer stops when true
					stopTime = System.currentTimeMillis();
					for(int i=1; i<Grids.length; i++) {
						if(Grids[i].isBomb() == false && !Grids[i].isChange()) {
							Grids[i].click();
						}
					}
					// click yes to quit. 
					if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(Map.this, "You win, the time cost is: " + (stopTime - startTime)/1000 + "s", "Cong!", JOptionPane.OK_CANCEL_OPTION)) {
						Map.this.dispose();
						new Map().initialize();
					} else {
						System.exit(0);
					}
				}
			} 
		}
		
	}
	

	@Override
	public void run() {
		
		int time = 0;
		
		while(isStart && !isStop) {
			this.setTitle(time + "");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			time ++;
		}
	}
	
	public static void main(String[] args) {
		new Map().initialize();
	}

}
