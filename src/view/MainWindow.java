package view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import control.DominoGame.ButtonListener;
import control.DominoGame.MouseClickMotionListener;
import control.DominoRules;
import data.Player;
import data.Stone;


public class MainWindow
{

	private PaintingComponent paintingComponent = new PaintingComponent();
	private ArrayList<RenderImage> renderedImages = new ArrayList<RenderImage>();
	private JFrame frame;
	private MouseClickMotionListener mouseHandler;
	private ButtonListener buttonListener;
	private JLabel lbl_mouseX = new JLabel("Position X: 0");
	private JLabel lbl_mouseY = new JLabel("Position Y: 0");
	private JLabel lbl_player1Points = new JLabel("Joueur 1: 0");
	private JLabel lbl_player2Points = new JLabel("Joueur 2: 0");
	private JLabel lbl_splash1 = new JLabel();
	private JLabel lbl_splash2 = new JLabel();
	private JButton btn_drawTalon = new JButton("Nouvelle pierre");
	private PointsLabel lbl_points = new PointsLabel("Points: 0");
	private DominoLabel targetLabel = null;
	private ArrayList<DominoLabel> dLabels = new ArrayList<DominoLabel>();
	private ArrayList<DominoLabel> handLabels = new ArrayList<DominoLabel>();
	private JPanel graphicsPane = new JPanel();
	private ScrollPane scrollPane = new ScrollPane();
	private JPanel handPane = new JPanel();
	private JPanel contentPane;
	
	public void initializeWindow(Stone[] allStones,
				MouseClickMotionListener mouseHandler, ButtonListener buttonListener)
	{
		this.mouseHandler = mouseHandler;
		this.setButtonListener(buttonListener);
		frame = new JFrame("Domino");
		contentPane = (JPanel) frame.getContentPane();

		scrollPane.setBounds(-2, 734, 1600, 141);
		
		JLabel lbl_help1 = new JLabel("Bouton droit de la souris "
				+ "Appuyez longtemps pour déplacer le champs de jeu");
		JLabel lbl_help2 = new JLabel("Bouton gauche "
				+ "appuyez longtemps pour déplacer la pierre");
		
		contentPane.setLayout(null);
		contentPane.setBounds(0, 0, 1600, 900);
		contentPane.add(lbl_mouseX);
		contentPane.add(lbl_mouseY);
		contentPane.add(lbl_points);
		contentPane.add(lbl_splash1);
		contentPane.add(lbl_splash2);
		contentPane.add(lbl_player1Points);
		contentPane.add(lbl_player2Points);
		contentPane.add(btn_drawTalon);
		contentPane.add(lbl_help1);
		contentPane.add(lbl_help2);
		contentPane.add(scrollPane);
		contentPane.add(graphicsPane);

		scrollPane.add(handPane);
		
		lbl_help1.setBounds(600, 5, 400, 20);
		lbl_help1.setHorizontalAlignment(JLabel.CENTER);
		lbl_help2.setBounds(575, 25, 450, 20);
		lbl_help2.setHorizontalAlignment(JLabel.CENTER);
		
		lbl_splash1.setBounds(0, (contentPane.getHeight()/2) - 200, contentPane.getWidth(), 100);
		lbl_splash1.setHorizontalAlignment(JLabel.CENTER);
		lbl_splash1.setFont(lbl_splash1.getFont().deriveFont(100.0f));

		lbl_splash2.setBounds(0, (contentPane.getHeight()/2) -100, contentPane.getWidth(), 150);
		lbl_splash2.setHorizontalAlignment(JLabel.CENTER);
		lbl_splash2.setFont(lbl_splash1.getFont().deriveFont(100.0f));

		lbl_mouseX.setBounds(0, 0, 100, 20);
		lbl_mouseY.setBounds(0, 20, 100, 20);

		lbl_points.setBounds(contentPane.getWidth() - 89, 13, 70, 21);
		lbl_points.setIcon(new ImageIcon("ImageSrc/BG_Points.png"));
		lbl_points.setIconTextGap(-65);
		
		lbl_player1Points.setBounds(contentPane.getWidth() - 93, 45, 105, 20);
		lbl_player2Points.setBounds(contentPane.getWidth() - 93, 65, 105, 20);
		
		btn_drawTalon.setBounds((contentPane.getWidth()/2) - 77, scrollPane.getY() - 40, 155, 40);
		btn_drawTalon.addActionListener(buttonListener);

		FlowLayout flow = new FlowLayout();
		flow.setVgap(10);
		flow.setHgap(15);

		handPane.setName("Hand");
		handPane.setBackground(new Color(140,100,40));
		handPane.setBounds(0, 580, 1280, 120);
		handPane.setLayout(flow);
		handPane.addMouseListener(mouseHandler);
		handPane.addMouseMotionListener(mouseHandler);
		
		graphicsPane.setBackground(Color.LIGHT_GRAY);
		graphicsPane.setBounds(-5500, -5500, 10000, 10000);
		graphicsPane.setLayout(null);
		graphicsPane.addMouseListener(mouseHandler);
		graphicsPane.addMouseMotionListener(mouseHandler);
		
		lbl_points.setIcon(new ImageIcon("ImageSrc/BG_Points.png"));
			
		graphicsPane.add(paintingComponent);
			
		paintingComponent.setSize(new Dimension(graphicsPane.getWidth(), graphicsPane.getHeight()));
			

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Properties p = System.getProperties();
		String os = p.getProperty("os.name");

		textOut("Résolution écran: " + (int) screen.getWidth() + "x" + (int) screen.getHeight());
		int x = (screen.width / 2) - (contentPane.getWidth() / 2);
		int y = (screen.height / 2) - (contentPane.getHeight() / 2);
		textOut(os);
		if(os.toLowerCase().contains("mac"))
		{
			x = contentPane.getWidth() - screen.width;
			y = contentPane.getHeight() - screen.height;
		}
		
		frame.setBounds(x, y, contentPane.getWidth(), contentPane.getHeight());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	class PaintingComponent extends JComponent
	{
		private static final long serialVersionUID = 1L;
		private Object[] intersections;
		private Object[] intersectionColors;
		

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);	
			
			Rectangle[] rects = new Rectangle[renderedImages.size()];
			Shape[] shapes = new Shape[renderedImages.size()];
			Graphics2D g2d = (Graphics2D) g;
			int i = 0;
			
			for (RenderImage ri: renderedImages)
			{
				double x = ri.getPos().getWidth();
				double y = ri.getPos().getHeight();
				double w = ri.getSize().getWidth();
				double h = ri.getSize().getHeight();
				
				rects[i] = new Rectangle((int)x, (int)y, (int)w, (int)h);
				
				Path2D.Double path = new Path2D.Double();
				path.append(rects[i], false);
				
				g2d.setColor(Color.GREEN);
				g2d.draw(shapes[i]);
				
				if (i >= 1)
				{
					if (shapes[i].intersects(shapes[i-1].getBounds2D()));
					{
						g2d.setColor(Color.RED);
						g2d.draw(shapes[i].getBounds().intersection(shapes[i-1].getBounds()));
					}
				}
				
				i++;
			}
			
			if (intersections != null)
			{
				int z = 0;
				
				for (Object s: intersections)
				{
					if ((Boolean) intersectionColors[z] == true)
						g2d.setColor(Color.GREEN);
					else
						g2d.setColor(Color.RED);
					g2d.fill((Shape)s);
					
					z++;
				}
			}
			
			g2d.dispose();
		}
		
		public void setIntersectionShapes (Object[] shapes, Object[] colors)
		{
			if (shapes != null)
			{
				this.intersections = shapes;
				this.intersectionColors = colors;
			}
			else
			{
				this.intersections = null;
				this.intersectionColors = null;
			}
		}
	}
	

	public void textOut(String s)
	{
		System.out.println(s);
	}
	

	public void showMousePosition(int x, int y)
	{
		lbl_mouseX.setText("Position X: " + x);
		lbl_mouseY.setText("Position Y: " + y);
		
		lbl_mouseX.setSize(lbl_mouseX.getText().length() * 6, 20);
		lbl_mouseY.setSize(lbl_mouseY.getText().length() * 6, 20);
	}
	

	public void updatePoints(boolean firstStone)
	{
		int points = DominoRules.calculatePoints(lbl_points.getPoints()
									, lbl_points.getDoublePoints(), firstStone);
		lbl_points.setText("Points: " + points);
	}

	public void addDominoeToHand(Stone s, boolean firstMove)
	{
		boolean abort = false;
		

		for (DominoLabel h: handLabels)
		{
			if (h.getStone() == s)
				abort = true;
		}
		

		if (!abort)
		{

			if (s.isDoublestone() && firstMove)
				s.rotateImage(90);
			

			handLabels.add(new DominoLabel(s));
			DominoLabel d = handLabels.get(handLabels.size() -1);
			
			textOut("HandLabels: " + handLabels.size());
				
			d.addMouseListener(mouseHandler);
			d.addMouseMotionListener(mouseHandler);

			handPane.add(d);
			
			updatePanels();
		}
	}
	

	public boolean checkIntersection(DominoLabel draggedStone,
						boolean released, int[] edgePoints, boolean[] doublePoints)
	{
		ArrayList<Shape> intersections = new ArrayList<Shape>();
		ArrayList<Boolean> intersectionColors = new ArrayList<Boolean>();
		
		int i = 0;
		int lastIndex = dLabels.size() - 1;
		
		for (DominoLabel d: dLabels)
		{
			if (d == null || (i+1) > lastIndex)
				break;
			else
			{

				for (int j = i+1; j < dLabels.size(); j++) 
				{
					if (dLabels.get(i).getBounds().intersects
												(dLabels.get(j).getBounds()))
					{
						intersections.add(dLabels.get(i).getBounds().intersection
													(dLabels.get(j).getBounds()));
						
						if (dLabels.get(j) == draggedStone)
						{
							targetLabel = dLabels.get(i);
							intersectionColors.add(DominoRules.checkCompatibility
													(dLabels.get(j), targetLabel));
						}
						else if (dLabels.get(i) == draggedStone)
						{
							targetLabel = dLabels.get(j);
							intersectionColors.add(DominoRules.checkCompatibility
													(dLabels.get(i), targetLabel));
						}
						else
							intersectionColors.add(DominoRules.checkCompatibility
													(dLabels.get(i), dLabels.get(j)));
					}

					if ((j+1) > lastIndex)
						break;
				}
			}
			i++;
		}

		if (!intersections.isEmpty())
		{

			paintingComponent.setIntersectionShapes(intersections.toArray(), 
											intersectionColors.toArray());

			if (intersections.size() > 1)
				return false;
			
			if (released == true)
			{
				if (intersectionColors.get(0))
				{
					if(!DominoRules.checkIfVertical(targetLabel))
						return moveStoneHorizontal(draggedStone, targetLabel,
								intersectionColors, edgePoints, doublePoints);
					else
						return moveStoneVertical(draggedStone, targetLabel,
								intersectionColors, edgePoints, doublePoints);
				}
			}
			
			return false;
		}

		else
		{
			targetLabel = null;
			paintingComponent.setIntersectionShapes(null, null);
			return false;
		}
	}
	

	public DominoLabel getCurrentTarget()
	{
		return targetLabel;
	}
	

	private boolean moveStoneHorizontal(DominoLabel draggedStone, DominoLabel target, 
			ArrayList<Boolean> intersectionColors, int[] edgePoints, boolean[] doublePoints)
	{

		boolean snapRight = DominoRules.snapRight(draggedStone, target);
		boolean noNeighbours = DominoRules.checkNeighboursHorizontal(draggedStone, target, snapRight);
		
		if (intersectionColors.get(0) == true && draggedStone.isDraggable() && noNeighbours)
		{
			int tPosX = target.getLocation().x;
			int tPosY = target.getLocation().y;
			int draggedWidth = draggedStone.getWidth();
			int draggedHeight = draggedStone.getHeight();
			int targetWidth = target.getWidth();
			int targetHeight = target.getHeight();
			
			if (snapRight)
			{
				if (!draggedStone.getStone().isDoublestone())
				{
					if (!target.getStone().isDoublestone())
						draggedStone.setLocation(tPosX+draggedWidth, tPosY);
					else
						draggedStone.setLocation(tPosX+(draggedWidth/2),
														tPosY+(draggedHeight/2));
				}
				else
				{
					draggedStone.setLocation(tPosX+targetWidth, tPosY-(targetHeight/2));
				}
				
				target.getStone().setRightNeighbour(draggedStone.getStone());
				draggedStone.getStone().setLeftNeighbour(target.getStone());
				
				DominoRules.calculatePointsRight(draggedStone, target, edgePoints, doublePoints);
			}
			else
			{
				if (!draggedStone.getStone().isDoublestone())
				{
					if (!target.getStone().isDoublestone())
						draggedStone.setLocation(tPosX-draggedWidth, tPosY);
					else
						draggedStone.setLocation(tPosX-draggedWidth, tPosY+(draggedHeight/2));
				}
				else
				{
					draggedStone.setLocation(tPosX-draggedWidth, tPosY-(targetHeight/2));
				}
				
				target.getStone().setLeftNeighbour(draggedStone.getStone());
				draggedStone.getStone().setRightNeighbour(target.getStone());
				
				DominoRules.calculatePointsLeft(draggedStone, target, edgePoints, doublePoints);
				
			}
			draggedStone.setNotDraggable();
			target.setNotDraggable();
			checkIntersection(draggedStone, false, edgePoints, doublePoints);		

			lbl_points.setPoints(edgePoints);
			lbl_points.setDoublePoints(doublePoints);
			updatePoints(false);
			
			return true;
		}
		else
		{
			textOut("Malheureusement, les pierres ne sont pas compatibles");
			return false;
		}
	}


	private boolean moveStoneVertical(DominoLabel draggedStone, DominoLabel target, 
			ArrayList<Boolean> intersectionColors, int[] edgePoints, boolean[] doublePoints)
	{

		
		boolean snapTop = DominoRules.snapTop(draggedStone, target);
		boolean noNeighbours = DominoRules.checkNeighboursVertical(draggedStone, target, snapTop);
		
		if (intersectionColors.get(0) == true && draggedStone.isDraggable() && noNeighbours)
		{
			int tPosX = target.getLocation().x;
			int tPosY = target.getLocation().y;
			int draggedWidth = draggedStone.getWidth();
			int draggedHeight = draggedStone.getHeight();
			int targetWidth = target.getWidth();
			int targetHeight = target.getHeight();
			
			if (snapTop)
			{
				if (!draggedStone.getStone().isDoublestone())
				{
					if(target.getStone().isSpinner() || !target.getStone().isDoublestone())
						draggedStone.setLocation(tPosX, tPosY-targetHeight);
					else
					{
						System.err.println("Pierre normale appliquée verticalement au sommet de la double pierre");
						draggedStone.setLocation(tPosX+(draggedWidth/2), tPosY-draggedHeight);
					}
				}
				else
				{
					System.err.println("Double pierre créé au-dessus");
					draggedStone.setLocation(tPosX-(targetWidth/2), tPosY-targetWidth);
				}

				target.getStone().setTopNeighbour(draggedStone.getStone());
				draggedStone.getStone().setBottomNeighbour(target.getStone());
				
				DominoRules.calculatePointsTop(draggedStone, target, edgePoints, doublePoints);
				
			}
			else
			{
				textOut("poser");
				if (!draggedStone.getStone().isDoublestone())
				{
					if(target.getStone().isSpinner() || !target.getStone().isDoublestone())
						draggedStone.setLocation(tPosX, tPosY+targetHeight);
					else
					{
						System.err.println("Pierre normale appliquée verticalement au-dessous de doubler la pierre");
						draggedStone.setLocation(tPosX+(draggedWidth/2), tPosY+targetHeight);
					}
				}
				else
				{
					System.err.println("Double pierre posée vers le bas");
					draggedStone.setLocation(tPosX-(targetWidth/2), tPosY+targetHeight);
				}
				
				target.getStone().setBottomNeighbour(draggedStone.getStone());
				draggedStone.getStone().setTopNeighbour(target.getStone());
				
				DominoRules.calculatePointsBottom(draggedStone, target, edgePoints, doublePoints);
			}
			
			draggedStone.setNotDraggable();
			target.setNotDraggable();
			checkIntersection(draggedStone, false, edgePoints, doublePoints);
			
			lbl_points.setPoints(edgePoints);
			lbl_points.setDoublePoints(doublePoints);
			updatePoints(false);
			
			return true;
		}
		else
		{
			textOut("Malheureusement, les pierres ne sont pas compatibles");
			return false;
		}
	}
	

	public void updatePanels()
	{
		contentPane.updateUI();
		graphicsPane.updateUI();
		handPane.updateUI();
	}

	public Point getFrameCoordinates()
	{
		return frame.getLocationOnScreen();
	}

	public void dropFromHand(DominoLabel clickedStone, int offsetX,
									int offsetY, int playedDominoes)
	{
		Stone s = clickedStone.getStone();
		Player p = s.getPlayer();
		textOut("Pierre: " + s);
		p.deleteStone(s);
		
		handPane.remove(clickedStone);
		handLabels.remove(clickedStone);
		
		dLabels.add(clickedStone);
		graphicsPane.add(clickedStone, 0);

		if (playedDominoes != 0)
			clickedStone.setLocation(offsetX + clickedStone.getX(),
						offsetY + 680 - clickedStone.getHeight());

		else
		{	
			int halfSizeX = frame.getWidth()/2;
			int halfSizeY = scrollPane.getY()/2;
			int halfStoneWidth = clickedStone.getWidth()/2;
			int halfStoneHeight = clickedStone.getHeight()/2;
			
			clickedStone.setLocation(offsetX + halfSizeX - halfStoneWidth, offsetY + halfSizeY - halfStoneHeight);
		}
		
		updatePanels();
	}
	

	public void clearHand()
	{
		handPane.removeAll();	
		handLabels.clear();		
		
		updatePanels();		
	}


	public void firstPoints(int[] edgePoints, boolean[] doublePoints)
	{
		lbl_points.setPoints(edgePoints);
		lbl_points.setDoublePoints(doublePoints);
	}

	public void updateButton(boolean isActive, String text)
	{
		if (text != "")
			btn_drawTalon.setText(text);
		
		btn_drawTalon.setEnabled(isActive);
		btn_drawTalon.setFocusPainted(isActive);
	}


	public void updatePlayerPoints(Player player, int playerIndex)
	{
		if (playerIndex == 0)
			lbl_player1Points.setText("Joueur 1: " + player.getPoints());
		else
			lbl_player2Points.setText("Joueur 2: " + player.getPoints());
	}

	public void updatePlayerArrow(int playerIndex, int playedDominoes)
	{

		ImageIcon arrow = null;
		int x1 = lbl_player1Points.getLocation().x;
		int y1 = lbl_player1Points.getLocation().y;
		int x2 = lbl_player2Points.getLocation().x;
		int y2 = lbl_player2Points.getLocation().y;
		

		try
		{
			arrow = new ImageIcon("ImageSrc/Arrow.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		if (playerIndex == 0)
		{

			if (lbl_player1Points.getIcon() == null)
			{

				lbl_player1Points.setIcon(arrow);
				lbl_player1Points.setLocation(x1 - 24, y1);
				lbl_player2Points.setIcon(null);
				

				if (playedDominoes != 0)
					lbl_player2Points.setLocation(x2 + 24, y2);
			}
		}

		else
		{
			if (lbl_player2Points.getIcon() == null)
			{
				lbl_player2Points.setIcon(arrow);
				lbl_player2Points.setLocation(x2 - 24, y2);
				lbl_player1Points.setIcon(null);
				
				if (playedDominoes != 0)
					lbl_player1Points.setLocation(x1 + 24, y1);
			}
		}
	}
	

	public void resetWindow()
	{
		lbl_points.setText("Points: 0");
		
		btn_drawTalon.setText("Tirage de pierres");
		lbl_player1Points.setBounds(contentPane.getWidth() - 93, 45, 105, 20);
		lbl_player2Points.setBounds(contentPane.getWidth() - 93, 65, 105, 20);
		lbl_player1Points.setIcon(null);
		lbl_player2Points.setIcon(null);
		dLabels.clear();
		clearHand();
		graphicsPane.setLocation(-5500, -5500);
		graphicsPane.removeAll();
		graphicsPane.add(paintingComponent);
		
		updatePanels();	
	}

	public void showStartSplash()
	{

		updateButton(false, "");
		
		int fontSize1 = 0;
		
		lbl_splash1.setVisible(true);	
		
		
		lbl_splash1.setText("Jouons au domino !");
		
		lbl_splash1.setFont(lbl_splash1.getFont().deriveFont((float)fontSize1));
		
		
		for (int i = 0; i <= 255; i++)
		{
			
			lbl_splash1.setForeground(new Color(255 - i,(i/2),(i/4),i));
				
			if (i % 3 == 0 && fontSize1 < 100)
				fontSize1++;
			
			lbl_splash1.setFont(lbl_splash1.getFont().deriveFont((float)fontSize1));
			
			delay(5);	
		}
		
		delay(1500);	
		
		
		for (int i = 255; i >= 0; i--)
		{
			lbl_splash1.setForeground(new Color(255 - i,(i/2),(i/4),i));
			if (i % 3 == 0 && fontSize1 >= 0)
			{
				fontSize1--;
			}
				
			lbl_splash1.setFont(lbl_splash1.getFont().deriveFont((float)fontSize1));
			
			delay(1);	
		}
		
		lbl_splash1.setVisible(false);
		
		delay(300);	
	}
	

	public void showGameInfo(final String text1, final String text2, 
			final float textSize, int delay, boolean fadeOut)
	{
		Timer t = new Timer(true);	
		
		t.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				lbl_splash1.setFont(lbl_splash1.getFont().deriveFont(textSize));
				lbl_splash2.setFont(lbl_splash2.getFont().deriveFont(textSize));
				lbl_splash1.setForeground(Color.BLACK);
				lbl_splash2.setForeground(Color.BLACK);
				lbl_splash1.setText(text1);
				lbl_splash2.setText(text2);
				lbl_splash1.setVisible(true);
				lbl_splash2.setVisible(true);
			}
		}, delay);
		
		if (fadeOut)
		{
			t.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					lbl_splash1.setVisible(false);
					lbl_splash2.setVisible(false);
				}
			}, delay + 2000);
		}
	}

	public void delay(int milliseconds)
	{
		try
		{
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}


	public ButtonListener getButtonListener() {
		return buttonListener;
	}


	public void setButtonListener(ButtonListener buttonListener) {
		this.buttonListener = buttonListener;
	}
}