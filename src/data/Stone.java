package data;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Stone
{

	private int pips1 = 36;
	private int pips2 = 36;
	private int value;
	private boolean doublestone;
	private boolean spinner;
	private boolean vertical;
	private Image icon;
	private BufferedImage rawImage;
	private Stone leftNeighbour;
	private Stone rightNeighbour;
	private Stone topNeighbour;
	private Stone bottomNeighbour;
	private Dimension imageSize;
	private Player player;
	
	public Stone(int pips1, int pips2, Dimension imageSize)
	{
		this.pips1 = pips1;
		this.pips2 = pips2;
		this.imageSize = imageSize;

		calculateValue();
		setDoublestone();
	}

	public void loadIcon()
	{
		BufferedImage image;
		
		String fileName = new String("ImageSrc/" + "new" + pips1 + "_" + pips2 + ".png");
		System.out.println("Dateiname: " + fileName);
		
		try {
			image = ImageIO.read(new File(fileName));
			rawImage = image;
			icon = Toolkit.getDefaultToolkit().createImage(image.getSource());
		} catch (IOException e) {
			System.err.println("Bild: " + fileName + " konnte nicht geladen werden");
			e.printStackTrace();
		}
	}
	
	public void rotateImage(int angle)
	{
		
		int z = angle / 90;
		for (int i = 1; i<=z; i++)
		{
			double degrees = 90;
			
			double radians = Math.toRadians(degrees);
			double sin = Math.abs(Math.sin(radians));
			double cos = Math.abs(Math.cos(radians));
			int newWidth = (int)Math.round(icon.getWidth(null) 
											* cos + icon.getHeight(null) * sin);
			int newHeight = (int)Math.round(icon.getWidth(null) 
											* sin + icon.getHeight(null) * cos);
			int x = (newWidth - icon.getWidth(null)) / 2;
			int y = (newHeight - icon.getHeight(null)) / 2;
			
			System.out.println("Breite: " + newWidth + ", Hoehe: " + newHeight);

			BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight,
														BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = rotatedImage.createGraphics();
			AffineTransform at = new AffineTransform();
			
			at.setToRotation(radians, x + (icon.getWidth(null) / 2), y 
											+ (icon.getHeight(null) / 2));
			at.translate(x, y);
			g2d.setTransform(at);
			g2d.drawImage(rawImage, 0, 0, null);
			g2d.dispose();

			rawImage = rotatedImage;

			icon = Toolkit.getDefaultToolkit().createImage(rotatedImage.getSource());
		}
	}
	
	public int getPips1()
	{
		return pips1;
	}
	
	public int getPips2()
	{
		return pips2;
	}

	public int getValue()
	{
		return value;
	}

	private void calculateValue()
	{
		value = pips1+pips2;
	}

	public boolean isDoublestone()
	{
		return doublestone;
	}

	private void setDoublestone()
	{
		if (pips1 == pips2)
			this.doublestone = true;
	}
	
	public Image getIcon()
	{
		return icon;
	}
	
	public void setPlayer(Player p)
	{
		if (p != null)
			this.player = p;
	}
	
	public Player getPlayer()
	{
		return player;
	}


	public boolean checkRotationHorizontal(Stone target, boolean snapRight)
	{

		if (snapRight)
		{

			if (pips1 != target.getPips2())
			{

				rotateImage(180);
				togglePips();
				
				return true;
			}
			else 
				return false;
		}
		else
		{
			if (pips2 != target.getPips1())
			{
				rotateImage(180);
				togglePips();
				
				return true;
			}
			else 
				return false;
		}
	}
	

	public boolean checkRotationVertical(Stone target, boolean snapTop)
	{
		setVertical(true);
		rotateImage(90);
		if (snapTop)
		{
			if (pips2 != target.getPips1())
			{
				rotateImage(180);
				togglePips();
				
				return true;
			}
			else
				return false;
		}
		else
		{
			if (pips1 != target.getPips2())
			{
				rotateImage(180);
				togglePips();
				
				return true;
			}
			else
				return false;
		}
	}
	
	private void togglePips()
	{
		int temp = pips1;
		pips1 = pips2;
		pips2 = temp;
	}

	public Stone getLeftNeighbour()
	{
		return leftNeighbour;
	}
	
	public void setLeftNeighbour(final Stone s)
	{
		leftNeighbour = s;
	}

	public Stone getRightNeighbour()
	{
		return rightNeighbour;
	}

	public void setRightNeighbour(final Stone s)
	{
		this.rightNeighbour = s;
	}

	public Stone getTopNeighbour()
	{
		return topNeighbour;
	}

	public void setTopNeighbour(final Stone s)
	{
		this.topNeighbour = s;
	}

	public Stone getBottomNeighbour()
	{
		return bottomNeighbour;
	}

	public void setBottomNeighbour(final Stone s)
	{
		this.bottomNeighbour = s;
	}
	
	public void setSpinner(boolean isSpinner)
	{
		this.spinner = isSpinner;
	}
	
	public boolean isSpinner()
	{
		return spinner;
	}
	
	public void setVertical (boolean isVertical)
	{
		this.vertical = isVertical;
	}
	
	public boolean isVertical()
	{
		return vertical;
	}
}