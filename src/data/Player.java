package data;

import java.util.ArrayList;


public class Player
{
	
	private ArrayList<Stone> hand = new ArrayList<Stone>();
	
	private String name;
	private boolean firstMove = true;
	private boolean droppedStone = false;
	private boolean blocked = false;
	private boolean noStones = false;
	private int points = 0;
	
	public Player(String name)
	{
		if (name != null && name != "")
			this.name = name;
	}
	

	public void addStone(Stone stone)
	{
		hand.add(stone);
	}
	

	public void deleteStone(Stone stone)
	{
		hand.remove(stone);
		
		if (hand.isEmpty())
			noStones = true;
	}
	
	public ArrayList<Stone> getHand()
	{
		return hand;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void increasePoints(int points)
	{
		this.points += points;
	}
	
	public int getPoints()
	{
		return points;
	}
	
	public Stone placeStone(Stone s)
	{
		return s;
	}
	
	public void setfirstMove(boolean isFirstMove)
	{
		this.firstMove = isFirstMove;
	}
	
	public boolean isFirstMove()
	{
		return firstMove;
	}

	public boolean isDroppedStone()
	{
		return droppedStone;
	}

	public void setDroppedStone(boolean droppedStone)
	{
		this.droppedStone = droppedStone;
	}

	public boolean isBlocked()
	{
		return blocked;
	}

	public void setBlocked(boolean blocked)
	{
		this.blocked = blocked;
	}
	
	public boolean isNoStones()
	{
		return noStones;
	}
	
	public void resetNoStones()
	{
		noStones = false;
	}
}