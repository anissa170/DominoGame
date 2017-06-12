package control;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JPanel;

import view.DominoLabel;
import view.MainWindow;
import data.Player;
import data.Stone;


public class DominoGame
{

	private Stone[] allStones = new Stone[28];
	private ArrayList<Stone> talon = new ArrayList<Stone>();
	private Player[] allPlayers;
	private int currentPlayerIndex;
	private int[] edgePoints = new int[4];
	private boolean[] doublePoints = new boolean[4];
	private boolean hasSpinner = false;
	private Stone spinner = null;
	private int playedDominoes = 0;
	private int round = 1;
	private MainWindow view;
	private MouseClickMotionListener mouseHandler = new MouseClickMotionListener();
	private final int winningPoints = 250;
	
	public DominoGame(MainWindow view, int numPlayers)
	{
		allPlayers = new Player[numPlayers];
		
		for (int i = 0; i < edgePoints.length; i++)
			edgePoints[i] = 7;
		
		this.view = view;
		
		initializeGame();
	}
	
	public void initializeGame()
	{
		initializeDominoes();
		initializePlayers();
		initializeHands();
		initializeTalon();
		view.initializeWindow(allStones, mouseHandler, new ButtonListener());
		currentPlayerIndex = chooseBeginner();
		
		view.showStartSplash();
		showRoundInfo("", "", 80f, 0);
		startMove();
	}
	

	private void showRoundInfo(String text1, String text2, float textSize, int delay)
	{
		String labelText1, labelText2;
		
		if (text1 == "" && text2 == "")
		{
			labelText1 = "Tour " + round;
			labelText2 = "Joueur " + (currentPlayerIndex+1) + " commence !";
		}
		else
		{
			labelText1 = text1;
			labelText2 = text2;
		}
		
		view.showGameInfo(labelText1, labelText2, textSize, delay, true);
	}
	

	private void startMove()
	{
		Player player = allPlayers[currentPlayerIndex];
		boolean blocked = false;
		int failCounter = 0;
		boolean showDominoes = false;
		boolean endRound = false;
		
		player.setDroppedStone(false);		
		
		view.textOut("Tour " + round + "!");
		view.textOut("Pierres jouées : " + playedDominoes);
		
		view.updatePlayerArrow(currentPlayerIndex, playedDominoes);
		player.setBlocked(false);

		for (Stone s: player.getHand())
		{
			if (!DominoRules.checkIfDroppable(s, edgePoints, spinner, playedDominoes))
				failCounter++;
		}
		
		if (failCounter == player.getHand().size())
		{
			blocked = true;
			player.setBlocked(true);
		}
		

		if (allPlayers[0].isBlocked() && allPlayers[1].isBlocked())
			endRound = true;
		
		if (!talon.isEmpty() && blocked == true)
			view.updateButton(true, "");
		else
			view.updateButton(false, "");
		
		if (!talon.isEmpty() || (talon.isEmpty() && blocked == false))
			showDominoes = true;	
		
		else if (talon.isEmpty() && blocked == true)
			showDominoes = false;	
		
		System.err.println("Stand showDominoes: " + showDominoes);

		if (!endRound)
			makeMove(showDominoes, player); 
		else
			endRound();
	}
	

	private void makeMove(boolean showDominoes, Player player)
	{
		if (showDominoes)
		{
			view.textOut("Joueur actuel: " + player.getName());
			drawStonesOnView(currentPlayerIndex);
		}
		else
		{
			int otherPlayer = DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
			String text1 = "Joueur " + (currentPlayerIndex+1) + " ne peut pas poser";
			String text2 = "Joueur " + (otherPlayer+1) + " est en train de jouer";
			view.showGameInfo(text1, text2, 40f, 0, true);
			currentPlayerIndex = DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
			allPlayers[currentPlayerIndex].setDroppedStone(false);
			startMove();
		}
	}
	

	private void endRound()
	{
		int winnerPoints = DominoRules.calculateRoundPoints(allPlayers, true);
		int winner = DominoRules.calculateRoundPoints(allPlayers, false);

		String winnerString = "Joueur " + (winner+1) + "remporte la manche";
		String pointsString = "et reçoit " + winnerPoints + " Points";
		

		view.showGameInfo(winnerString, pointsString, 40f, 0, true);
		view.updatePlayerPoints(allPlayers[winner], winner);
		

		if (allPlayers[winner].getPoints() < winningPoints)
		{
			resetRound();
			
			initializeDominoes();
			initializeHands();
			initializeTalon();
			currentPlayerIndex = chooseBeginner();
			round++;
			
			startMove();
		}
		
		else
			endGame(winner+1);
	}

	private void endGame(int winner)
	{
		view.resetWindow();
		String text1 = "Félicitation joueur" + winner;
		String text2 = "Ils ont gagné !";
		view.updateButton(false, "Jeu sur");
		view.showGameInfo(text1, text2, 60f, 0, true);
		view.showGameInfo("Bravo", "Merci d'avoir jouer !", 80f, 2100, false);
	}
	

	private void resetRound()
	{
		for (int i = 0; i < edgePoints.length; i++)
		{
			edgePoints[i] = 7;
			doublePoints[i] = false;
		}
		
		for (Player p: allPlayers)
		{
			p.getHand().clear();
			p.setBlocked(false);
			p.setfirstMove(true);
			p.setDroppedStone(false);
			p.resetNoStones();
		}
		
		hasSpinner = false;
		spinner = null;
		playedDominoes = 0;
		mouseHandler.panelOffsetX = 0;
		
		view.resetWindow();
	}
	
	
	private void initializeDominoes()
	{
		int w = 100;
		int h= 50;
		int current = 0;
		
		for (int x = 0; x <= 6; x++)
		{	
			for (int y = 0; y <= 6; y++)
			{
				if (y == 0)
					y = x;
				
				
				allStones[current] = new Stone(x, y, new Dimension(w, h));
				allStones[current].loadIcon();
				
				current++;
			}
		}
		

		for (Stone n : allStones)
		{
			System.out.println(n.getPips1() + "|" + n.getPips2() + " = " + n.getValue() + " | DS: " + n.isDoublestone());
		}
		
		shuffleDominoes();
		
		// Ausgabe nachdem gemischt wurde
		System.out.println("Mélange!:");
		
		for (Stone n : allStones)
		{
			System.out.println(n.getPips1() + "|" + n.getPips2() + " = " + n.getValue() + " | DS: " + n.isDoublestone());
		}
	}
	

	private void shuffleDominoes()
	{

		ArrayList<Stone> stoneShuffler = new ArrayList<Stone>();
		
		for(Stone s : allStones)
		{
			stoneShuffler.add(s);
		}

		Collections.shuffle(stoneShuffler);
		

		int z = 0;
		
		for (Stone s : stoneShuffler)
		{
			allStones[z] = s;
			z++;
		}
	}
	
	private int chooseBeginner()
	{

		Stone highest = null;
		
		for (Player p: allPlayers)
		{
			for (Stone s: p.getHand())
			{

				if (s.isDoublestone() == true)
				{
					if (highest != null)
					{
						if (s.getValue() > highest.getValue())
							highest = s; 
					}

					else

						highest = s;
				}
			}
		}
		
		if (highest == null)
		{
			for (Player p: allPlayers)
			{
				for (Stone s: p.getHand())
				{
					if (highest != null)
					{
						if (s.getValue() > highest.getValue())
							highest = s;
					}
					else
						highest = s;
				}
			}
		}
		
		int beginnerIndex = 0;
		
		for (Player p: allPlayers)
		{
			if (p == highest.getPlayer())
				break;
			else
				beginnerIndex++;
		}
		
		return beginnerIndex;
	}
	

	private void initializePlayers()
	{
		for (int i = 0; i < allPlayers.length; i++)
		{
			allPlayers[i] = new Player("Player_" + (i+1));
			view.textOut("Nouveau joueur: " + allPlayers[i].getName());
		}
	}
	
	private void initializeHands()
	{
		int numStones;
		int numPlayers = allPlayers.length;
		int playerID = 0;

		if (numPlayers == 2)
			numStones = 7;

		else
			numStones = 5;

		for (Player p: allPlayers)
		{
			view.textOut("");

			for (int i = 0; i < numStones; i++)
			{

				int pos = playerID + (numPlayers*i);
				
				p.addStone(allStones[pos]);
				allStones[pos].setPlayer(p);
				
				view.textOut(p.getName() + " pierre " + (i+1) + ": "
				+ allStones[pos].getPips1() + "|" + allStones[pos].getPips2());
			}
			playerID++;
		}
	}
	
	private void initializeTalon()
	{

		talon.clear();
		
		for (Stone s: allStones)
		{
			if (s.getPlayer() == null)
				talon.add(s);
		}
		
		view.textOut("");
		
		for (Stone s: talon)
		{
			view.textOut("Talon " + (talon.indexOf(s)+1) + ": " 
						+ s.getPips1() + "|" + s.getPips2());
		}
	}
	

	private void drawStonesOnView(int PlayerIndex)
	{

		for (Stone s: allPlayers[PlayerIndex].getHand())
		{

			view.addDominoeToHand(s, allPlayers[PlayerIndex].isFirstMove());
		}
	}


	public class MouseClickMotionListener implements MouseListener, MouseMotionListener
	{
		private PointerInfo mousePos;
		private DominoLabel draggedStone;
		private DominoLabel target;
		private int pressedButton;
		private int draggedAtX, draggedAtY;
		private int panelOffsetX = 0, panelOffsetY = 0;

		public void mouseDragged(MouseEvent e)
		{	
			mousePos = MouseInfo.getPointerInfo();	
			int mouseX = mousePos.getLocation().x;
			int mouseY = mousePos.getLocation().y;
			int offsetX, offsetY;
			
			view.showMousePosition(mouseX, mouseY);
			
			if (pressedButton == 1)
			{	

				if (e.getSource() instanceof DominoLabel)
				{
					DominoLabel dLabel = (DominoLabel) e.getSource();

		
					if (dLabel.isDraggable())
					{
				
						Point origin = view.getFrameCoordinates();
						 
						draggedStone = dLabel;
						
						if (!dLabel.getStone().isDoublestone() && !dLabel.getStone().isVertical())
						{
							offsetX = panelOffsetX + dLabel.getWidth()/2 - (origin.x + 102);
							offsetY = panelOffsetY + dLabel.getHeight() - (origin.y + 100);
						}
						
						else
						{
							offsetX = panelOffsetX + dLabel.getWidth()/2 - (origin.x + 52);
							offsetY = panelOffsetY + dLabel.getHeight() - (origin.y + 175);
						}
						
						
						dLabel.setLocation(offsetX + mouseX, offsetY + mouseY);
						
				
						view.checkIntersection(draggedStone, false, edgePoints, doublePoints);
						
						target = view.getCurrentTarget();
					}
					else
						view.textOut("ne peut pas être déplacé");
				}
			}
			
			else if (pressedButton == 3)
			{
				
				if (e.getSource() instanceof JPanel)
				{
					view.textOut("JPanel gedraggt");
					JPanel p = (JPanel) e.getSource();
					Point origin = view.getFrameCoordinates();
					
					view.textOut("OnScreen: " + p.getLocationOnScreen());
					view.textOut("MausPos: " + mousePos.getLocation().x + "|" + mousePos.getLocation().y);
					
		
					e.translatePoint(draggedAtX, draggedAtY);
					p.setLocation(mouseX - (draggedAtX + origin.x) + 320, mouseY - (draggedAtY + origin.y) + 178);

					panelOffsetX = - p.getLocation().x;
					panelOffsetY = - p.getLocation().y;
					view.textOut("panelOffset: " + panelOffsetX + "|" + panelOffsetY);
				}
			}
			
			view.updatePanels();	
			e.consume();
		}

		public void mouseMoved(MouseEvent e)
		{
			mousePos = MouseInfo.getPointerInfo();
			view.showMousePosition(mousePos.getLocation().x, mousePos.getLocation().y);
			
			if (e.getSource() instanceof JPanel && panelOffsetX == 0)
			{
				JPanel p = (JPanel) e.getSource();
				panelOffsetX = - p.getLocation().x;
				panelOffsetY = - p.getLocation().y;
				
				view.textOut("Panneau de décalage réglé");
			}
		}

		public void mouseClicked(MouseEvent e)
		{

			if (e.getButton() == 1)
			{
				Object c = e.getSource();

				if (c instanceof DominoLabel)
				{
					DominoLabel clickedStoneLabel = (DominoLabel) c;
					Stone clickedStone = clickedStoneLabel.getStone();
					Player player = allPlayers[currentPlayerIndex];

					if (clickedStoneLabel.getParent().getName() == "Hand")
					{
						if (DominoRules.checkIfDroppable(clickedStone, edgePoints, spinner, playedDominoes) && 
															!player.isDroppedStone())
						{
							view.textOut("peut être déplacé");

							view.dropFromHand(clickedStoneLabel, panelOffsetX, panelOffsetY, playedDominoes);
							clickedStone.getPlayer().deleteStone(clickedStone);
							player.setDroppedStone(true);
							
							if (playedDominoes == 0)
							{
								playedDominoes++;

								clickedStoneLabel.setNotDraggable();
								DominoRules.firstStone(clickedStone, edgePoints, doublePoints);
				
								hasSpinner = clickedStone.isSpinner();
								
				
								if (hasSpinner)
									spinner = clickedStone;
								
								view.firstPoints(edgePoints, doublePoints);
								view.updatePoints(true);
								
								if (!DominoRules.calculatePlayerPoints(edgePoints, doublePoints, player, true))
									System.err.println("Schade, leider diesmal keine Punkte");
								
								if (player.getPoints() >= winningPoints)
									endGame(currentPlayerIndex+1);
								
								view.updatePlayerPoints(player, currentPlayerIndex);
								
								view.clearHand();
								currentPlayerIndex = DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
								startMove();
							}
						}
					}
				}
				
				if (c instanceof JPanel)
				{
					JPanel p = (JPanel) c;
					
					if (p.getName() != "Hand")
					{

					}
				}
			}
			view.textOut(edgePoints[0] + ", " + edgePoints[1] 
						+ ", " + edgePoints[2] + ", " + edgePoints[3]);
			view.textOut(doublePoints[0] + ", " + doublePoints[1] 
						+ ", " + doublePoints[2] + ", " + doublePoints[3]);
			
			e.consume();			
		}

		public void mouseEntered(MouseEvent e)
		{

			if (e.getSource() instanceof DominoLabel)
			{
				DominoLabel label = (DominoLabel) e.getSource();
				
				if (label.getParent().getName() == "Hand")
				{
					view.textOut("" + DominoRules.checkIfDroppable(label.getStone(),
									edgePoints, spinner, playedDominoes));
					view.textOut("" + label.getStone().toString());
				}
			}
		}

		public void mouseExited(MouseEvent e)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.getSource() instanceof JPanel)
			{
				draggedAtX = e.getX() + 323;
				draggedAtY = e.getY() + 206;
			}
			
			view.textOut("draggedAt: " + draggedAtX + "|"+ draggedAtY);
			
			
			pressedButton = e.getButton();
			

			if (pressedButton == 3 && e.getSource() instanceof JPanel)
			{
				JPanel p = (JPanel) e.getSource();
				p.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
				
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			boolean endRound = false;
			boolean endGame = false;
			
			if (e.getSource() instanceof JPanel)
			{
				JPanel p = (JPanel) e.getSource();
				p.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
			
			if (pressedButton == 1 && playedDominoes > 0)
			{
				
				boolean hasSnapped = false;
				Player player = allPlayers[currentPlayerIndex];
				
				if (draggedStone != null)
					view.textOut("DraggedStone: " + draggedStone.getStone().getPips1()
									+ "|" + draggedStone.getStone().getPips2());
				
		
				if (target != null && draggedStone != null 
					&& draggedStone.getParent().getName() != "Hand")
				{
					view.textOut("Il y a une cible");
					
					if (DominoRules.checkPossibleMove(target.getStone(), draggedStone))
					{

						if (spinner == null)
						{

							spinner = DominoRules.checkSpinner(draggedStone, target, hasSpinner);
							if (spinner != null)
								hasSpinner = true;
						}
						
					
						hasSnapped = view.checkIntersection(draggedStone, true,
															edgePoints, doublePoints);
						view.textOut("Target: " + target.getStone().getPips1()
										+ "|" + target.getStone().getPips2());
					}
					
					if (hasSnapped)
					{
						playedDominoes++;	
						
					
						if (!DominoRules.calculatePlayerPoints(edgePoints, doublePoints,
																		player, false))
							System.err.println("Schade, leider diesmal keine Punkte");
						
						System.err.println("Punkte " + player.getName() + ": "
											+ player.getPoints());
						

						if (player.getPoints() >= winningPoints)
							endGame = true;
						
						view.updatePlayerPoints(player, currentPlayerIndex);
						view.clearHand();
						
						
						if (player.isNoStones() && endGame == false)
						{
							endRound = true;
						}
						else
						{
							currentPlayerIndex = DominoRules.switchPlayer(allPlayers,
																		currentPlayerIndex);
							startMove();
						}
					}
					
					else
						if (target != null)
						{
							draggedStone.setLocation(target.getX() + 150, target.getY() + 150);
							view.checkIntersection(draggedStone, false, edgePoints, doublePoints);
						}
				}
				
				
				draggedStone = null;
			}
			
			view.textOut(edgePoints[0] + ", " + edgePoints[1] + ", " 
							+ edgePoints[2] + ", " + edgePoints[3]);
			view.textOut(doublePoints[0] + ", " + doublePoints[1] + ", "
							+ doublePoints[2] + ", " + doublePoints[3]);
			
			
			if (endGame == true)
				endGame(currentPlayerIndex+1);
			
			
			if (endRound == true)
			{
				endRound();
				showRoundInfo("", "", 80f, 2100);
			}
		}
	}
	

	public class ButtonListener	implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			
			Stone stone = talon.get(0);
			Player player = allPlayers[currentPlayerIndex];
			
			view.textOut(player.getName() + player.getHand().size());

			talon.remove(stone);
			
			player.addStone(stone);
			stone.setPlayer(player);
			
			view.addDominoeToHand(stone, true);
			
			view.textOut(player.getName() + player.getHand().size());
			if (DominoRules.checkIfDroppable(stone, edgePoints,
											 spinner, playedDominoes))
			{
				
				view.updateButton(false, "");
				player.setBlocked(false);
			}

			if (talon.isEmpty())
			{
				
				view.updateButton(false, "Der Talon ist leer");
				
				if (player.isBlocked())
				{
					int otherPlayer = DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
					String text1 = "Joueur " + (currentPlayerIndex+1) + " ne peut pas poser";
					String text2 = "Joueur " + (otherPlayer+1) + " en train de jouer";
					view.showGameInfo(text1, text2, 40f, 0, true);
					DominoRules.switchPlayer(allPlayers, currentPlayerIndex);
					startMove();
				}
			}
		}
	}
}