package control;

import java.util.ArrayList;

import data.Player;
import data.Stone;
import view.DominoLabel;

public final class DominoRules
{

	public static boolean checkCompatibility(DominoLabel dragged, DominoLabel target)
	{
		int d_p1 = dragged.getStone().getPips1();
		int d_p2 = dragged.getStone().getPips2();
		int t_p1 = target.getStone().getPips1();
		int t_p2 = target.getStone().getPips2();
		
		
		if (d_p1 == t_p1 || d_p1 == t_p2 || d_p2 == t_p1 || d_p2 == t_p2)
			return true;	
		else
			return false;
	}
	
	public static boolean snapRight(DominoLabel draggedStone, DominoLabel target)
	{
		Stone dStone = draggedStone.getStone();
		Stone tStone = target.getStone();
		
		int equalPip = giveEqualPip(dStone, tStone);
	
		if (equalPip == 0)
		{
			
			if (tStone.getRightNeighbour() == null)
			{
				
				dStone.checkRotationHorizontal(tStone, true);
				draggedStone.updateImage();	
				
				return true;	
			}
	
			else
			{
				dStone.checkRotationHorizontal(tStone, false);
				draggedStone.updateImage();
				
				return false;	
			}
		}
		
		if(tStone.getRightNeighbour() == null && equalPip == 2)
		{
			if (tStone.getPips2() != dStone.getPips1())
			{
				dStone.checkRotationHorizontal(target.getStone(), true);
				draggedStone.updateImage();
			}
			
			return true;	
		}	

		else
		{
			if (tStone.getPips1() != dStone.getPips2())
			{
				dStone.checkRotationHorizontal(target.getStone(), false);
				draggedStone.updateImage();
			}
			
			return false;
		}
	}

	public static boolean checkNeighboursHorizontal(DominoLabel dragged, DominoLabel target, boolean snapRight)
	{
		if (snapRight)
		{
			if (target.getStone().getRightNeighbour() == null)
				return true;	
			else
				return false;
		}
		else
		{
			if (target.getStone().getLeftNeighbour() == null)
				return true;
			else
				return false;
		}
	}
	
	public static boolean snapTop(DominoLabel draggedStone, DominoLabel target)
	{

		Stone dStone = draggedStone.getStone();
		Stone tStone = target.getStone();
		int equalPip = giveEqualPip(dStone, tStone);
		
		if (equalPip == 0)
		{
			if (tStone.getTopNeighbour() == null)
			{
				
				dStone.checkRotationVertical(tStone, true);
				draggedStone.updateImage();
				
				return true;	
			}
			else
			{
				dStone.checkRotationVertical(tStone, false);
				draggedStone.updateImage();
				
				return false;	
			}
		}
		
		if (tStone.getTopNeighbour() == null && equalPip == 1)
		{
			dStone.checkRotationVertical(tStone, true);
			draggedStone.updateImage();
			
			return true;	
		}
		else
		{
			dStone.checkRotationVertical(tStone, false);
			draggedStone.updateImage();
			
			return false;
		}
	}
	

	public static boolean checkNeighboursVertical(DominoLabel draggedStone, DominoLabel target, boolean snapTop)
	{

		if (snapTop)
		{

			if (target.getStone().getTopNeighbour() == null)
				return true;	
			else
				return false;	
		}
		else
		{

			if (target.getStone().getBottomNeighbour() == null)
				return true;	
			else
				return false;	
		}
	}
	
	private static int giveEqualPip(Stone dragged, Stone target)
	{
		if (target.isDoublestone())
			return 0;
		
		if (target.getPips1() == dragged.getPips1() || target.getPips1() == dragged.getPips2())
			return 1;
		else
			return 2;
	}
	
	public static boolean checkIfVertical(DominoLabel target)
	{

		Stone tStone = target.getStone();

		if (tStone.isSpinner() && tStone.getLeftNeighbour() != null && tStone.getRightNeighbour() != null)
			return true;

		else if (tStone.isVertical())
			return true;
		
		else
			return false;
	}
	

	public static boolean checkPossibleMove(Stone target, DominoLabel draggedStone)
	{
		String error = "Dieser Zug ist leider nicht moeglich";
		
		Stone left = target.getLeftNeighbour();
		Stone right = target.getRightNeighbour();
		Stone top = target.getTopNeighbour();
		Stone bottom = target.getBottomNeighbour();
		
		if (!target.isVertical())
			if (!target.isSpinner())		
			{
				if (left == null || right == null)
				{
					return true;
				}
				else
				{
					System.out.println(error);
					return false;
				}
			}
			else
			{
				
				if (left == null || right == null || top == null || bottom == null)
				{
					return true;
				}
				else
				{
					System.out.println(error);
					return false;
				}
			}

		else
		{

			if (top == null || bottom == null)
			{
				return true;
			}
			else
			{
				System.out.println(error);
				return false;
			}
		}
	}
	

	public static Stone checkSpinner(DominoLabel draggedStone, DominoLabel target, boolean hasSpinner)
	{
		if (!hasSpinner && draggedStone.getStone().isDoublestone())
		{

			draggedStone.getStone().setSpinner(true);
			return draggedStone.getStone();
		}
		else if (!hasSpinner && target.getStone().isDoublestone())
		{
			target.getStone().setSpinner(true);
			return target.getStone();
		}

		else 
			return null;
	}
	
	public static void calculatePointsLeft(DominoLabel draggedStone, DominoLabel target, 
											int[] edgePoints, boolean[] doublePoints)
	{

		edgePoints[0] = draggedStone.getStone().getPips1();

		if (draggedStone.getStone().isDoublestone())
			doublePoints[0] = true;	

		else
			doublePoints[0] = false;
	}
	
	 
	public static void calculatePointsRight(DominoLabel draggedStone, DominoLabel target,
											int[] edgePoints, boolean[] doublePoints)
	{
	
		edgePoints[1] = draggedStone.getStone().getPips2();
		if (draggedStone.getStone().isDoublestone())
			doublePoints[1] = true;
		else 
			doublePoints[1] = false;
	}
	

	public static void calculatePointsBottom(DominoLabel draggedStone, DominoLabel target, 
											int[] edgePoints, boolean[] doublePoints)
	{
		edgePoints[3] = draggedStone.getStone().getPips2();
		if (draggedStone.getStone().isDoublestone())
			doublePoints[3] = true;
		else
			doublePoints[3] = false;
	}
	
	public static void calculatePointsTop(DominoLabel draggedStone, DominoLabel target, 
											int[] edgePoints, boolean[] doublePoints)
	{

		edgePoints[2] = draggedStone.getStone().getPips1();
		if (draggedStone.getStone().isDoublestone())
			doublePoints[2] = true;
		else
			doublePoints[2] = false;
	}
	

	public static int calculatePoints(int[] edgePoints, boolean[] doublePoints, boolean firstStone)
	{
	
		int points = 0;
		int i = 0;
		
		if (!firstStone)
		{
			
			for (int p: edgePoints)
			{
				
				if (p != 7)
				{
					if (doublePoints[i] == false)
						
						points += p;
					
					else
					
						points += p*2;
				}
				i++;	
			}
		}

		else
			return edgePoints[0]+edgePoints[1];
		
		return points;	
	}

	public static boolean checkIfDroppable(Stone stone, int[] edgePoints, Stone spinner, int playedDominoes)
	{

		int i = 0;
		
		if (playedDominoes == 0)
			return true;
		

		for (int points: edgePoints)
		{
	
			if (i > 1 && spinner == null)
				break;
			
			if (points == stone.getPips1() || points == stone.getPips2())
				return true;
			
			i++;
		}
		
		
		if ((edgePoints[2] == 7 || edgePoints[3] == 7) && spinner != null)
		{

			if (spinner.getTopNeighbour() == null || spinner.getBottomNeighbour() == null)
			{
				
				if (stone.getPips1() == spinner.getPips1() || stone.getPips2() == spinner.getPips1())
					return true;	
			}
		}
		
		return false;	
	}

	public static int switchPlayer(Player[] allPlayers, int currentPlayerIndex)
	{


		allPlayers[currentPlayerIndex].setfirstMove(false);
		
		if ((allPlayers.length - 1) == currentPlayerIndex)
			return 0;

		else
			return ++currentPlayerIndex;
	}
	

	public static void firstStone(Stone stone, int[] edgePoints, boolean[] doublePoints)
	{

		edgePoints[0] = stone.getPips1();
		edgePoints[1] = stone.getPips2();
		
		if (stone.isDoublestone())
		{
			doublePoints[0] = true;
			stone.setSpinner(true);
		}
	}
	
	public static boolean calculatePlayerPoints(int[] edgePoints, boolean[] doublePoints
												, Player player, boolean firstMove)
	{
		int points = calculatePoints(edgePoints, doublePoints, firstMove);
		
		if (points % 5 == 0)
		{

			player.increasePoints(points);
			return true;
		}
		
		return false;
	}


	public static int calculateRoundPoints(Player[] allPlayers, boolean returnPoints)
	{

		int winner = 0;
		int loser = 1;
		
		if (allPlayers[0].getHand().size() > allPlayers[1].getHand().size())
		{
			winner = 1;
			loser = 0;
		}
		
		 
		int winnerPoints = calculateHandPoints(allPlayers[loser].getHand());
		
		
		if (returnPoints)
			return winnerPoints;	
		else
		{
			allPlayers[winner].increasePoints(winnerPoints);
			return winner;	
		}
	}

	
	private static int calculateHandPoints(ArrayList<Stone> hand)
	{
		int points = 0;
		
		for (Stone s: hand)
			points += s.getValue();
		
		while (points % 5 != 0)
			points += 1;
			
		return points;
	}
}