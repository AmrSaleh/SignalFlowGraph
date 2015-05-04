package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import model.InternalSolver;
import model.InternalSolving;
import model.Node;
import model.Path;




public class ScrollPanel extends JPanel implements MouseListener, MouseMotionListener{
	private static int currentMouseState;
	private static Node fakeNode;
	private static int selectedNodeKey;
	private static int selectedPathKey;
	private static JTextField nameField;
	private static JTextField gainField;
	private static JLabel nameLabel;
	private static JLabel gainLabel;
	private static int nodeKey;
	private static int pathKey;
	private static HashMap<Integer, Node> nodeMap;
	private static HashMap<Integer, Path> pathMap;
	private static ArrayList<Node> highlightedNode;
	private static ArrayList<Path> highlightedPath;
	private int nodeRadius;
	private static Node pathStartPointer;
	private static Node pathEndPointer;
	private static boolean drawingPath;
	private static boolean curvyPath;
	private boolean running;
	private static boolean pressedNode;
	private static boolean awaitBeginningNode;
	private static boolean awaitEndingNode;
	private static InternalSolving graph;
	private static int first,last;
	private static JButton calculate;
	private static String[] dropdownListItems;
	private static JComboBox dropdownBox;
	private static int dropdownFocus;
	private static DefaultListModel nodeList;
	private static JList list;
	private static JScrollPane listScroll;
	private static Point oldMouse;
	private static boolean drawCurve;
	public static boolean reverseCurve;
	public ScrollPanel()
	{
		reverseCurve = false;
		drawCurve=false;
		nodeList = new DefaultListModel<ArrayList<Node>>();
		list = new JList(nodeList);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL_WRAP);
		list.setVisibleRowCount(-1);
		listScroll=new JScrollPane(list);
		listScroll.setPreferredSize(new Dimension(200,100));
		oldMouse = new Point();
		dropdownListItems=new String[5];
		dropdownListItems[0]="Show forward paths";
		dropdownListItems[1]="Show individual loops";
		dropdownListItems[2]="Show combinations of nontouching loops";
		dropdownListItems[3]="Show delta values";
		dropdownListItems[4]="Show overall transfer function";
		dropdownBox= new JComboBox<>(dropdownListItems);
		dropdownBox.setEnabled(false);
		dropdownBox.setSelectedIndex(4);
		dropdownFocus=4;
		calculate = new JButton ("Calculate");
		awaitBeginningNode= false;
		awaitEndingNode = false;
		fakeNode = new Node();
		fakeNode.setNodeKey(-1);
		nameField = new JTextField();
		gainField = new JTextField();
		nameLabel = new JLabel();
		gainLabel = new JLabel();
		selectedNodeKey = -1;
		selectedPathKey = -1;
		running=true;
		nodeMap = new HashMap<Integer, Node>();
		pathMap = new HashMap<Integer, Path>();
		pathStartPointer = null;
		pathEndPointer = null;
		drawingPath = false;
		pressedNode = false;
		currentMouseState = 0;
		curvyPath = false;
		nodeKey = 0;
		pathKey = 0;
		nodeRadius = 10;
		highlightedNode = new ArrayList<Node>();
		highlightedPath = new ArrayList<Path>();
	}
	private static void reset()
	{
		fakeNode = new Node();
		fakeNode.setNodeKey(-1);
		selectedNodeKey = -1;
		selectedPathKey = -1;
		pathStartPointer = null;
		pathEndPointer = null;
		drawingPath = false;
		pressedNode = false;
		currentMouseState = 0;
	}
	private static boolean validateNodeName()
	{
		String toValidate = nameField.getText();
		for(int i = 0; i < nodeKey; i++)
		{
			if(nodeMap.containsKey(i))
			{
				Node node = nodeMap.get(i);
				if(node.getName().equalsIgnoreCase(toValidate))
				{
					return false;
				}
			}
		}
		return true;
	}
	private static boolean validatePathName()
	{
		String toValidate = nameField.getText();
		for(int i = 0; i < pathKey; i++)
		{
			if(pathMap.containsKey(i))
			{
				Path path = pathMap.get(i);
				if(path.getPathName().equalsIgnoreCase(toValidate))
				{
					return false;
				}
			}
		}
		return true;
	}
	private static void activateDropdownList() {
		// TODO Auto-generated method stub
		dropdownBox.setEnabled(true);
		clearHighlight();
		if(dropdownFocus==0)
		{
			ArrayList<ArrayList<Node>> forwardPaths = graph.getForwardPaths();
			nodeList.clear();
			System.out.println("Filling list!");
			System.out.println(forwardPaths.size());
			for(int i = 0; i < forwardPaths.size(); i++)
			{
				nodeList.addElement(forwardPaths.get(i));
			}
			System.out.println("listScroll updated");
		}
		else if(dropdownFocus==1)
		{
			ArrayList<ArrayList<Node>> individualLoops = graph.getIndividualLoops();
			nodeList.clear();
			System.out.println("Filling list!");
			System.out.println(individualLoops.size());
			for(int i = 0; i < individualLoops.size(); i++)
			{
				nodeList.addElement(individualLoops.get(i));
			}
			System.out.println("listScroll updated");
		}
		else if(dropdownFocus==2)
		{
			ArrayList<ArrayList<ArrayList<Node>>> nontouchingLoops = graph.getNonTouchingLoops();
			nodeList.clear();
			for(int i = 0; i < nontouchingLoops.size(); i++)
			{
				nodeList.addElement(nontouchingLoops.get(i));
			}
			
		}
		else if(dropdownFocus==3)
		{
			double totalDelta = graph.getTotalDelta();
			ArrayList<Double>deltaValues = graph.getDeltaValues();
			nodeList.clear();
			nodeList.addElement("Total delta = "+totalDelta);
			for(int i = 0; i < deltaValues.size(); i++)
			{
				nodeList.addElement("Delta "+i+" = "+deltaValues.get(i));
			}
		}
		else
		{
			double overallTF = graph.getOverall_TF_Value();
			nodeList.clear();
			nodeList.addElement("Total transfer function = "+overallTF);
		}
	}
	private static void deactivateDropdownList() {
		// TODO Auto-generated method stub
		clearHighlight();
		calculate.setText("Calculate");
		dropdownBox.setEnabled(false);
		dropdownBox.setSelectedIndex(4);
		dropdownFocus=4;
		nodeList.clear();
		for(int i = 0; i < nodeKey; i++)
		{
			if(nodeMap.containsKey(i))
			{
				Node node = nodeMap.get(i);
				node.setOutGoingPaths(new ArrayList<Path>());
			}
		}
		for(int i = 0; i < pathKey; i++)
		{
			if(pathMap.containsKey(i))
			{
				Path path = pathMap.get(i);
				path.setDestinationNode(new Node());
			}
		}
	}
	private void adjustPaths() {
		// TODO Auto-generated method stub
		for(int i = 0 ; i < pathKey; i++)
		{
			if(pathMap.containsKey(i))
			{
				Path path = pathMap.get(i);
				if(path.getFirstNodeKey()==fakeNode.getNodeKey())
				{
					path.setFirstX(fakeNode.getX());
					path.setFirstY(fakeNode.getY());
				}
				if(path.getLastNodeKey()==fakeNode.getNodeKey())
				{
					path.setLastX(fakeNode.getX());
					path.setLastY(fakeNode.getY());
				}
				if(path.getFirstNodeKey()==fakeNode.getNodeKey()&&
						path.getLastNodeKey()==fakeNode.getNodeKey())
				{
					
					
					
					path.setControlX((int) (path.getControlX()+(fakeNode.getX()-oldMouse.getX())));
					path.setControlY((int) (path.getControlY()+(fakeNode.getY()-oldMouse.getY())));
				}
			}
		}
	}
	public void run() {
		// TODO Auto-generated method stub

		// while (true) {
			
			Timer timer = new Timer(0, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try { Thread.sleep(20); } catch (InterruptedException e) {
				    e.printStackTrace(); }
				if (running)
					repaint();
				if(currentMouseState==2&&selectedNodeKey!=-1)
				{
					nameField.setEnabled(true);
					gainField.setEnabled(false);
					gainField.setText("");
					nameLabel.setText( nodeMap.get(selectedNodeKey).getName());
					gainLabel.setText("Gain");
				}
				else if(currentMouseState==2&&selectedPathKey!=-1)
				{
					nameField.setEnabled(true);
					gainField.setEnabled(true);
					nameLabel.setText( pathMap.get(selectedPathKey).getPathName());
					gainLabel.setText( String.valueOf(pathMap.get(selectedPathKey).getValue()));
				}
				else
				{
					nameField.setEnabled(false);
					gainField.setEnabled(false);
					nameField.setText("");
					gainField.setText("");
					nameLabel.setText("Name");
					gainLabel.setText("Gain");
				}
				// System.out.println("asdasd");
			}
		});
		timer.start();
		
		 try { Thread.sleep(100); } catch (InterruptedException e) {
		  e.printStackTrace(); }
		 
		// }
	}
	private static boolean checkFirstNode()
	{
		for(int i = 0; i < pathKey; i++)
		{
			if(pathMap.containsKey(i))
			{
				Path path = pathMap.get(i);
				if(path.getLastNodeKey()==selectedNodeKey)
				{
					return false;
				}
			}
		}
		return true;
	}
	private static void removePaths(int selectedNodeKey) {
		// TODO Auto-generated method stub
		for(int i = 0; i < pathKey; i++)
		{
			if(pathMap.containsKey(i))
			{
				Path path = pathMap.get(i);
				if(path.getFirstNodeKey()==selectedNodeKey
						|| path.getLastNodeKey()==selectedNodeKey)
				{
					pathMap.remove(i);
				}
			}
		}
		
	}
	int barb= 10; // barb length  
	 double phi=Math.PI/6;     // 30 degrees barb angle  
	           
	 private void drawArrow(Graphics2D g2, double theta, double x0, double y0)  
	    {  
	        double x = x0 - barb * Math.cos(theta + phi);  
	        double y = y0 - barb * Math.sin(theta + phi);  
	        g2.draw(new Line2D.Double(x0, y0, x, y));  
	        x = x0 - barb * Math.cos(theta - phi);  
	        y = y0 - barb * Math.sin(theta - phi);  
	        g2.draw(new Line2D.Double(x0, y0, x, y));  
	    }
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(1));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT,
		// BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
		setBackground(Color.WHITE);
		for (int i = 0; i < pathKey; i++) {
			if (pathMap.containsKey(i)) {
				Path path = pathMap.get(i);
				g2d.setColor(Color.BLACK);
					if (path.drawShape().getClass().equals(Line2D.Double.class)) {
				     double  theta = Math.atan2(((Line2D.Double)path.drawShape()).getY2() - ((Line2D.Double)path.drawShape()).getY1(), ((Line2D.Double)path.drawShape()).getX2() - ((Line2D.Double)path.drawShape()).getX1());  
				     double x =((((Line2D.Double)path.drawShape()).getX1() + (Math.abs(((Line2D.Double)path.drawShape()).getX2() -((Line2D.Double)path.drawShape()).getX1()) / 2) * ((((Line2D.Double)path.drawShape()).getX2() > ((Line2D.Double)path.drawShape()).getX1()) ? 1 : -1)));
				     double y =((((Line2D.Double)path.drawShape()).getY1() + (Math.abs(((Line2D.Double)path.drawShape()).getY2() - ((Line2D.Double)path.drawShape()).getY1()) / 2) * ((((Line2D.Double)path.drawShape()).getY2() > ((Line2D.Double)path.drawShape()).getY1()) ? 1 : -1)));
				     drawArrow(g2d, theta, x, y);
				    }
					
					if (path.drawShape().getClass().equals(QuadCurve2D.Double.class)) {
					     double  theta = Math.atan2(((QuadCurve2D.Double)path.drawShape()).getY2() - ((QuadCurve2D.Double)path.drawShape()).getY1(), ((QuadCurve2D.Double)path.drawShape()).getX2() - ((QuadCurve2D.Double)path.drawShape()).getX1());  
					    
						QuadCurve2D.Double test1=new QuadCurve2D.Double();
						QuadCurve2D.Double test2=new QuadCurve2D.Double();
						((QuadCurve2D.Double)path.drawShape()).subdivide(test1, test2);
						
						 drawArrow(g2d, theta, test1.getX2(), test1.getY2());
					
						
					}
					if (path.drawShape().getClass().equals(Ellipse2D.Double.class)) {
						double radius = (double) Math.sqrt((path.getControlX() - path.getFirstX()) * (path.getControlX() - path.getFirstX()) + (path.getControlY() - path.getFirstY()) * (path.getControlY() - path.getFirstY()));
					      double newradius = 2 * radius;
					      double x =  (((newradius * (path.getControlX() - path.getFirstX())) / radius) + path.getFirstX());
					      double y =  (((newradius * (path.getControlY() - path.getFirstY())) / radius) + path.getFirstY());
					      
					      double slope = -((x-path.getFirstX())/(y-path.getFirstY()));
					      double newx = 5;
					      double newy=slope*(newx-x)+y;
					      
					      
					      double  theta = Math.atan2((newy - y), (newx - x));
					      drawArrow(g2d, theta, x, y);
					}
				g2d.draw(path.drawShape());
				g2d.drawString(String.valueOf(path.getValue()),path.getTextXPos(),
						path.getTextYPos());
			}
			// (myLib.getElement(i).getxPos(), myLib.getElement(i).getyPos(),
			// myLib.getElement(i).getWidth(), myLib.getElement(i).getHeight());
		}
		if(highlightedPath!=null)
		{
			for (int i = 0; i < highlightedPath.size(); i++) {
					Path path = highlightedPath.get(i);
					g2d.setColor(new Color(0,191,255));
						if (path.drawShape().getClass().equals(Line2D.Double.class)) {
					     double  theta = Math.atan2(((Line2D.Double)path.drawShape()).getY2() - ((Line2D.Double)path.drawShape()).getY1(), ((Line2D.Double)path.drawShape()).getX2() - ((Line2D.Double)path.drawShape()).getX1());  
					     double x =((((Line2D.Double)path.drawShape()).getX1() + (Math.abs(((Line2D.Double)path.drawShape()).getX2() -((Line2D.Double)path.drawShape()).getX1()) / 2) * ((((Line2D.Double)path.drawShape()).getX2() > ((Line2D.Double)path.drawShape()).getX1()) ? 1 : -1)));
					     double y =((((Line2D.Double)path.drawShape()).getY1() + (Math.abs(((Line2D.Double)path.drawShape()).getY2() - ((Line2D.Double)path.drawShape()).getY1()) / 2) * ((((Line2D.Double)path.drawShape()).getY2() > ((Line2D.Double)path.drawShape()).getY1()) ? 1 : -1)));
					     drawArrow(g2d, theta, x, y);
					    }
						
						if (path.drawShape().getClass().equals(QuadCurve2D.Double.class)) {
						     double  theta = Math.atan2(((QuadCurve2D.Double)path.drawShape()).getY2() - ((QuadCurve2D.Double)path.drawShape()).getY1(), ((QuadCurve2D.Double)path.drawShape()).getX2() - ((QuadCurve2D.Double)path.drawShape()).getX1());  
						    
							QuadCurve2D.Double test1=new QuadCurve2D.Double();
							QuadCurve2D.Double test2=new QuadCurve2D.Double();
							((QuadCurve2D.Double)path.drawShape()).subdivide(test1, test2);
							
							 drawArrow(g2d, theta, test1.getX2(), test1.getY2());
						
							
						}
						if (path.drawShape().getClass().equals(Ellipse2D.Double.class)) {
							double radius = (double) Math.sqrt((path.getControlX() - path.getFirstX()) * (path.getControlX() - path.getFirstX()) + (path.getControlY() - path.getFirstY()) * (path.getControlY() - path.getFirstY()));
						      double newradius = 2 * radius;
						      double x =  (((newradius * (path.getControlX() - path.getFirstX())) / radius) + path.getFirstX());
						      double y =  (((newradius * (path.getControlY() - path.getFirstY())) / radius) + path.getFirstY());
						      
						      double slope = -((x-path.getFirstX())/(y-path.getFirstY()));
						      double newx = 5;
						      double newy=slope*(newx-x)+y;
						      
						      
						      double  theta = Math.atan2((newy - y), (newx - x));
						      drawArrow(g2d, theta, x, y);
						}
					g2d.draw(path.drawShape());
					g2d.drawString(String.valueOf(path.getValue()),path.getTextXPos(),
							path.getTextYPos());
			}
		}
		if(selectedPathKey!=-1)
		{
			if (pathMap.containsKey(selectedPathKey)) {
				Path path = pathMap.get(selectedPathKey);
				g2d.setColor(Color.ORANGE);
				g2d.draw(path.drawShape());
				g2d.drawString(String.valueOf(path.getValue()),path.getTextXPos(),
						path.getTextYPos());
				if(path.isCurve())
				{
					fakeNode = new Node();
					fakeNode.setNodeKey(-1);
					fakeNode.setX(path.getControlX());
					fakeNode.setY(path.getControlY());
					g2d.fill(fakeNode.drawControlPoint());
				}
				if (path.drawShape().getClass().equals(Line2D.Double.class)) {
				     double  theta = Math.atan2(((Line2D.Double)path.drawShape()).getY2() - ((Line2D.Double)path.drawShape()).getY1(), ((Line2D.Double)path.drawShape()).getX2() - ((Line2D.Double)path.drawShape()).getX1());  
				     double x =((((Line2D.Double)path.drawShape()).getX1() + (Math.abs(((Line2D.Double)path.drawShape()).getX2() -((Line2D.Double)path.drawShape()).getX1()) / 2) * ((((Line2D.Double)path.drawShape()).getX2() > ((Line2D.Double)path.drawShape()).getX1()) ? 1 : -1)));
				     double y =((((Line2D.Double)path.drawShape()).getY1() + (Math.abs(((Line2D.Double)path.drawShape()).getY2() - ((Line2D.Double)path.drawShape()).getY1()) / 2) * ((((Line2D.Double)path.drawShape()).getY2() > ((Line2D.Double)path.drawShape()).getY1()) ? 1 : -1)));
				     drawArrow(g2d, theta, x, y);
				    }
					
					if (path.drawShape().getClass().equals(QuadCurve2D.Double.class)) {
					     double  theta = Math.atan2(((QuadCurve2D.Double)path.drawShape()).getY2() - ((QuadCurve2D.Double)path.drawShape()).getY1(), ((QuadCurve2D.Double)path.drawShape()).getX2() - ((QuadCurve2D.Double)path.drawShape()).getX1());  
					    
						QuadCurve2D.Double test1=new QuadCurve2D.Double();
						QuadCurve2D.Double test2=new QuadCurve2D.Double();
						((QuadCurve2D.Double)path.drawShape()).subdivide(test1, test2);
						
						 drawArrow(g2d, theta, test1.getX2(), test1.getY2());
					
						
					}
					if (path.drawShape().getClass().equals(Ellipse2D.Double.class)) {
						double radius = (double) Math.sqrt((path.getControlX() - path.getFirstX()) * (path.getControlX() - path.getFirstX()) + (path.getControlY() - path.getFirstY()) * (path.getControlY() - path.getFirstY()));
					      double newradius = 2 * radius;
					      double x =  (((newradius * (path.getControlX() - path.getFirstX())) / radius) + path.getFirstX());
					      double y =  (((newradius * (path.getControlY() - path.getFirstY())) / radius) + path.getFirstY());
					      
					      double slope = -((x-path.getFirstX())/(y-path.getFirstY()));
					      double newx = 5;
					      double newy=slope*(newx-x)+y;
					      
					      
					      double  theta = Math.atan2((newy - y), (newx - x));
					      drawArrow(g2d, theta, x, y);
					}
			}
		}
		for (int i = 0; i < nodeKey; i++) {
			if (nodeMap.containsKey(i)) {
				Node node = nodeMap.get(i);
				g2d.setColor(Color.RED);
				g2d.fill(node.drawShape());
				g2d.drawString(node.getName(), node.getX(), node.getY()+20);
			}
			// (myLib.getElement(i).getxPos(), myLib.getElement(i).getyPos(),
			// myLib.getElement(i).getWidth(), myLib.getElement(i).getHeight());
		}
		if(highlightedNode!=null)
		{
			for (int i = 0; i < highlightedNode.size(); i++) {
					Node node = highlightedNode.get(i);
					g2d.setColor(new Color(0,191,255));
					g2d.fill(node.drawShape());
					g2d.drawString(node.getName(), node.getX(), node.getY()+20);
			}

			// (myLib.getElement(i).getxPos(), myLib.getElement(i).getyPos(),
			// myLib.getElement(i).getWidth(), myLib.getElement(i).getHeight());
		}
		if(selectedNodeKey!=-1)
		{
			if (nodeMap.containsKey(selectedNodeKey)) {
				Node node = nodeMap.get(selectedNodeKey);
				g2d.setColor(Color.BLUE);
				g2d.fill(node.drawShape());
				g2d.drawString(node.getName(), node.getX(), node.getY()+20);
			}
		}
		if(currentMouseState==0)
		{
			Node node = fakeNode;
			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
			g2d.setColor(Color.BLACK);
			g2d.draw(node.drawShape());
		}
		if(drawingPath==true)
		{
			Path path = new Path();
			try{
				path.setFirstX(pathStartPointer.getX());
			}
			catch(Exception e)
			{
				
			}
			try{
			path.setFirstY(pathStartPointer.getY());
			}
			catch(Exception e)
			{
				
			}
			try{
			path.setLastX(pathEndPointer.getX());
			}
			catch(Exception e)
			{
				path.setLastX(oldMouse.x);
			}
			try{
			path.setLastY(pathEndPointer.getY());
			}
			catch(Exception e)
			{
				path.setLastY(oldMouse.y);
			}
			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0));
			g2d.setColor(Color.BLACK);
			g2d.draw(path.drawShape());
		}
		if(pressedNode==true)
		{
			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
			g2d.setColor(Color.BLACK);
			g2d.draw(fakeNode.drawShape());
		}
		// if (width < 0 && height < 0) {
//		if (GFX2.tempShape != null) {
//			g2d.setColor(GFX2.tempShape.getColor());
//			// GFX2.koko.setColor(Color.BLACK);
//			g2d.draw(GFX2.tempShape.drawShape());
//		}
//		if (GFX2.editMode == 0 && GFX2.shapeIndex != -1) {
//			g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0));
//			g2d.setColor(PaintView.drawingColor);
//			g2d.draw(myLib.getElement(GFX2.shapeIndex).drawShape());
//			for(int i = 0 ; i < myLib.getElement(GFX2.shapeIndex).numOfResizePoints(); i++)
//			{
//				//System.out.println("HI");
//				g2d.fill(myLib.getElement(GFX2.shapeIndex).resizePoints(i));
//			}
//			
//		}
		// } else if (width < 0) {
		// g.fillOval(x + width, y, Math.abs(width), height);
		// } else if (height < 0) {
		// g.fillOval(x, y + height, width, Math.abs(height));
		// } else {
		// g.fillOval(x, y, width, height);
		// }
		// System.out.println("aaaaa");
	}
	public static void clearHighlight()
	{
		highlightedNode = new ArrayList<Node>();
		highlightedPath = new ArrayList<Path>();
	}
	public static void main (String[] args)
	{
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } 
                catch (Exception ex) {
                }
	                
					JFrame frame = new JFrame("Signal Flow Graph");
					frame.setMinimumSize(new Dimension(500, 300));
					JPanel motherPanel = new JPanel(new BorderLayout());
					motherPanel.setPreferredSize(new Dimension(800, 600));
					ScrollPanel panel = new ScrollPanel();
					//area = new JTextArea();
					//area.setPreferredSize(new Dimension(500,100));
					panel.setBackground(Color.WHITE);
					panel.setPreferredSize(new Dimension(8000,6000));
					JPanel toolBox = new JPanel(new GridLayout(15,10));
					list.addListSelectionListener(new ListSelectionListener() {
						
						@Override
						public void valueChanged(ListSelectionEvent arg0) {
							// TODO Auto-generated method stub
							if(dropdownFocus==0||dropdownFocus==1)
							{
								clearHighlight();
								highlightedNode=(ArrayList<Node>) list.getSelectedValue();
								if(highlightedNode!=null&&highlightedPath!=null)
								{
									for(int i = 0; i < highlightedNode.size()-1; i++)
									{
										for(int j = 0; j < pathKey; j++)
										{
											if(pathMap.containsKey(j))
											{
												Path path = pathMap.get(j);
												if(path.getFirstNodeKey()==highlightedNode.get(i).getNodeKey()
														&&path.getLastNodeKey()==highlightedNode.get(i+1).getNodeKey())
												{
													highlightedPath.add(path);
												}
											}
										}
									}
								}
							}
							else if(dropdownFocus==3)
							{
								clearHighlight();
								int index = list.getSelectedIndex();
								if(index<1)
								{
									return;
								}
								index--;
								highlightedNode=(ArrayList<Node>) graph.getForwardPaths().get(index);
								if(highlightedNode!=null&&highlightedPath!=null)
								{
									for(int i = 0; i < highlightedNode.size()-1; i++)
									{
										for(int j = 0; j < pathKey; j++)
										{
											if(pathMap.containsKey(j))
											{
												Path path = pathMap.get(j);
												if(path.getFirstNodeKey()==highlightedNode.get(i).getNodeKey()
														&&path.getLastNodeKey()==highlightedNode.get(i+1).getNodeKey())
												{
													highlightedPath.add(path);
												}
											}
										}
									}
								}
							}
							else if(dropdownFocus==2)
							{
								clearHighlight();
								int index = list.getSelectedIndex();
								if(index==-1)
								{
									return;
								}
								ArrayList<ArrayList<ArrayList<Node>>> nontouching = graph.getNonTouchingLoops();
								
								for(int i = 0; i < nontouching.get(index).size(); i++)
								{
									ArrayList<Node> tempNode = new ArrayList<Node>();
									for(int j = 0; j < nontouching.get(index).get(i).size(); j++)
									{
										highlightedNode.add(nontouching.get(index).get(i).get(j));
										tempNode.add(nontouching.get(index).get(i).get(j));
									}
									for(int j = 0; j < tempNode.size()-1; j++)
									{
										for(int k = 0; k < pathKey; k++)
										{
											if(pathMap.containsKey(k))
											{
												Path path = pathMap.get(k);
												if(path.getFirstNodeKey()==tempNode.get(j).getNodeKey()
														&&path.getLastNodeKey()==tempNode.get(j+1).getNodeKey())
												{
													highlightedPath.add(path);
												}
											}
										}

									}
								}
								
							}
							else
							{
								clearHighlight();
							}
						}
					});
					final JButton clearAll = new JButton("Clear all");
					clearAll.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							reset();
							highlightedNode.clear();
							highlightedPath.clear();
							nodeMap.clear();
							pathMap.clear();
							nodeKey=0;
							pathKey=0;
							deactivateDropdownList();
							awaitBeginningNode= false;
							awaitEndingNode = false;
							currentMouseState = 0;
							selectedNodeKey = -1;
							selectedPathKey = -1;
							pathStartPointer = null;
							pathEndPointer = null;
							drawingPath = false;
							return;
						}
					});
					final JButton circle  = new JButton("Node");
					circle.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							deactivateDropdownList();
							awaitBeginningNode= false;
							awaitEndingNode = false;
							currentMouseState = 0;
							selectedNodeKey = -1;
							selectedPathKey = -1;
							pathStartPointer = null;
							pathEndPointer = null;
							drawingPath = false;
							return;
						}
					});
					final JButton line = new JButton("Path");
					line.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							deactivateDropdownList();
							awaitBeginningNode= false;
							awaitEndingNode = false;
							currentMouseState = 1;
							selectedNodeKey = -1;
							selectedPathKey = -1;
							pathStartPointer = null;
							pathEndPointer = null;
							drawingPath = false;
							return;
						}
					});
					final JButton delete = new JButton("Delete selected");
					delete.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							deactivateDropdownList();
							awaitBeginningNode= false;
							awaitEndingNode = false;
							pathStartPointer = null;
							pathEndPointer = null;
							drawingPath = false;
							if(currentMouseState==2)
							{
								if(selectedNodeKey!=-1)
								{
									nodeMap.remove(selectedNodeKey);
									removePaths(selectedNodeKey);
									selectedNodeKey = -1;
								}
								if(selectedPathKey!=-1)
								{
									pathMap.remove(selectedPathKey);
									selectedPathKey=-1;
								}
							}
						}

					});
					final JButton select = new JButton("Edit");
					select.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							awaitBeginningNode= false;
							awaitEndingNode = false;
							currentMouseState = 2;
							selectedNodeKey = -1;
							selectedPathKey = -1;
							pathStartPointer = null;
							pathEndPointer = null;
							drawingPath = false;
							return;
						}
					});
					ButtonGroup drawGroup = new ButtonGroup();
					drawGroup.add(circle);
					drawGroup.add(line);
					drawGroup.add(select);
					calculate.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub							
							if(awaitBeginningNode==true&&
									awaitEndingNode==true&&
									selectedNodeKey!=-1)
							{
								Node firstNode = new Node();
								Node lastNode = new Node();
								last = selectedNodeKey;
								reset();
								graph = new InternalSolver();
								ArrayList<Node> nodeArr = new ArrayList<Node>();
								for(int i = 0 ; i < nodeKey; i++)
								{
									if(nodeMap.containsKey(i))
									{
										nodeArr.add(nodeMap.get(i));
										for(int j = 0; j < pathKey; j++)
										{
											if(pathMap.containsKey(j))
											{
												Path path = pathMap.get(j);
												if(path.getFirstNodeKey()==i)
												{
													nodeMap.get(i).addOutPath(path);
												}
												if(path.getLastNodeKey()==i)
												{
													path.setDestinationNode(nodeMap.get(i));
												}
											}
										}
									}
									if(i==first)
									{
										firstNode = nodeMap.get(i);
									}
									if(i==last)
									{
										lastNode= nodeMap.get(i);
									}
								}
								Node[] nodeArray = new Node[nodeArr.size()];
								for(int i = 0; i < nodeArr.size(); i++)
								{
									nodeArray[i]=nodeArr.get(i);
								}
								System.out.println(nodeArray.length);
								graph.setGraph( nodeArray, firstNode, lastNode);
								//System.out.println(graph.getForwardPaths().size());
								calculate.setText("Graph calculated!");
								currentMouseState=2;
								awaitBeginningNode=false;
								awaitEndingNode=false;
								activateDropdownList();
							}
							else if(awaitBeginningNode==true&&
									awaitEndingNode==false&&
									selectedNodeKey!=-1
									&&checkFirstNode())
							{
								first = selectedNodeKey;
								reset();
								currentMouseState=2;
								
								calculate.setText("Select ending node.");
								awaitEndingNode=true;
								return;
							}
							else if(awaitBeginningNode==true&&
									awaitEndingNode==false&&
									selectedNodeKey!=-1
									&&!checkFirstNode())
							{
								reset();
								currentMouseState=2;
								
								calculate.setText("Invalid node. Select beginning node.");
								return;
							}
							else
							{
								reset();
								deactivateDropdownList();
								currentMouseState=2;
								calculate.setText("Select beginning node.");
								awaitBeginningNode=true;
							}
							
						}

					});
					nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
					gainLabel.setHorizontalAlignment(SwingConstants.CENTER);
					nameField.addKeyListener(new KeyListener() {
						
						@Override
						public void keyTyped(KeyEvent arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void keyReleased(KeyEvent arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void keyPressed(KeyEvent arg0) {
							// TODO Auto-generated method stub
							deactivateDropdownList();
							awaitBeginningNode= false;
							awaitEndingNode = false;
							if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
							      if(selectedNodeKey !=-1)
							      {
							    	  if(nodeMap.containsKey(selectedNodeKey)&&validateNodeName())
							    	  {
							    		  nodeMap.get(selectedNodeKey).setName(nameField.getText());
							    		  nameLabel.setText(nameField.getText());
							    	  }
							      }
							      else if(selectedPathKey!=-1)
							      {
							    	  if(pathMap.containsKey(selectedPathKey)&&validatePathName())
							    	  {
							    		  pathMap.get(selectedPathKey).setPathName(nameField.getText());
							    		  nameLabel.setText(nameField.getText());
							    	  }							    	  
							      }
							      return;
							}
						}
					});
					gainField.addKeyListener(new KeyListener() {
						
						@Override
						public void keyTyped(KeyEvent arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void keyReleased(KeyEvent arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void keyPressed(KeyEvent arg0) {
							// TODO Auto-generated method stub
							deactivateDropdownList();
							awaitBeginningNode= false;
							awaitEndingNode = false;
							if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
								if(selectedPathKey!=-1)
							      {
							    	  if(pathMap.containsKey(selectedPathKey))
							    	  { 		  
							    		  try{
							    			  
							    			  gainLabel.setText(String.valueOf(Double.parseDouble(gainField.getText())));
							    			  pathMap.get(selectedPathKey).setValue(Double.parseDouble(gainField.getText()));
							    		  }
							    		  catch(Exception e)
							    		  {
							    			  gainLabel.setText(String.valueOf(pathMap.get(selectedPathKey).getValue()));
							    		  }
							    	  }
							      }
							}
						}
					});
					JRadioButton straight = new JRadioButton("Straight path");
					JRadioButton curve = new JRadioButton("Curved path");
					straight.setSelected(true);
					straight.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							awaitBeginningNode= false;
							awaitEndingNode = false;
							curvyPath = false;
							pathStartPointer = null;
							pathEndPointer = null;
							drawingPath = false;
							
							return;
						}
					});
					curve.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							awaitBeginningNode= false;
							awaitEndingNode = false;
							curvyPath = true;
							pathStartPointer = null;
							pathEndPointer = null;
							drawingPath = false;
							return;
						}
					});
					dropdownBox.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							JComboBox cb = (JComboBox)e.getSource();
					        dropdownFocus = cb.getSelectedIndex();
					        if(dropdownBox.isEnabled())
					        {
					        	activateDropdownList();
					        }
						}
					});
					ButtonGroup group = new ButtonGroup();
					group.add(straight);
					group.add(curve);
					toolBox.add(circle);
					toolBox.add(line);
					toolBox.add(select);
					toolBox.add(delete);
					toolBox.add(new JSeparator());
					toolBox.add(clearAll);
					toolBox.add(new JSeparator());
					toolBox.add(straight);
					toolBox.add(curve);
					toolBox.add(nameLabel);
					toolBox.add(nameField);
					toolBox.add(gainLabel);
					toolBox.add(gainField);
					toolBox.add(calculate);
					toolBox.add(dropdownBox);
					toolBox.setPreferredSize(new Dimension(200, motherPanel.getHeight()));
					JScrollPane scrollPane = new JScrollPane(panel);
					panel.addMouseListener(panel);
			        panel.addMouseMotionListener(panel);
					scrollPane.setPreferredSize(new Dimension(300,300));
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			        frame.getContentPane().setLayout (new BorderLayout());
			        frame.getContentPane().add(motherPanel);
			        motherPanel.add(toolBox, BorderLayout.WEST);
			        motherPanel.add(listScroll, BorderLayout.SOUTH);
			        motherPanel.add(scrollPane);
			        
			        frame.pack();
			        frame.setVisible (true);
			        panel.run();
            }});
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if (currentMouseState == 2 && pressedNode == true) {
			fakeNode = nodeMap.get(selectedNodeKey);
			if (e.getX() >= nodeRadius) {
				fakeNode.setX(e.getX());
			} else {
				fakeNode.setX(nodeRadius);
			}
			if (e.getY() >= nodeRadius) {
				fakeNode.setY(e.getY());
			} else {
				fakeNode.setY(nodeRadius);
			}
			adjustPaths();
		}
		if(currentMouseState==2&&fakeNode!=null&&selectedPathKey!=-1)
		{
			if (pathMap.containsKey(selectedPathKey)) {
				Path path = pathMap.get(selectedPathKey);
				path.setControlX(e.getX());
				path.setControlY(e.getY());
			}
		}
		
		int x ,y;
		if (e.getX() >= nodeRadius) {
			x=e.getX();
		} else {
			x=nodeRadius;
		}
		if (e.getY() >= nodeRadius) {
			y=e.getY();
		} else {
			y=nodeRadius;
		}
		oldMouse.setLocation(x,y);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		if(drawingPath==true)
		{
			pathEndPointer = new Node(e.getX(), e.getY(), -1, "N-1");
		}
		if(currentMouseState==0)
		{
			fakeNode.setX(e.getX());
			fakeNode.setY(e.getY());
		}
		oldMouse.setLocation(e.getX(), e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		gainField.setText("");
		nameField.setText("");
		selectedNodeKey = -1;
		selectedPathKey = -1;
		if(drawCurve)
		{
			currentMouseState=1;
			drawCurve=false;
		}
		// TODO Auto-generated method stub
		if(currentMouseState ==0)
		{
			Node node;
			for(int i = 0; i < nodeKey; i++)
			{
				
				if(nodeMap.containsKey(i))
				{
					node = nodeMap.get(i);
				}
				else
				{
					continue;
				}
				if(e.getX()<node.getX()+nodeRadius&&e.getX()>node.getX()-nodeRadius
						&&e.getY()<node.getY()+nodeRadius &&e.getY()>node.getY()-nodeRadius)
				{
					return;
				}
			}
			node = new Node(e.getX(), e.getY(), nodeKey, "N"+nodeKey);
			nodeMap.put(nodeKey, node);
			nodeKey++;
			return;
		}
		else if (currentMouseState == 1)
		{
			Node node;
			for(int i = 0; i < nodeKey; i++)
			{
				
				if(nodeMap.containsKey(i))
				{
					node = nodeMap.get(i);
				}
				else
				{
					continue;
				}
				if(e.getX()<node.getX()+nodeRadius&&e.getX()>node.getX()-nodeRadius
						&&e.getY()<node.getY()+nodeRadius &&e.getY()>node.getY()-nodeRadius)
				{
					if(drawingPath==false)
					{
						System.out.println("First point");
						drawingPath=true;
						pathStartPointer = node;
						return;
					}
					else if(drawingPath == true)
					{
						System.out.println("Second point");
						drawingPath = false;
						pathEndPointer = node;
						for(int j = 0; j < pathKey; j++)
						{
							if(pathMap.containsKey(j))
							{
								Path tempPath = pathMap.get(j);
								if(tempPath.getFirstNodeKey()==
										pathStartPointer.getNodeKey()&&
										tempPath.getLastNodeKey()==
										pathEndPointer.getNodeKey())
								{
									pathStartPointer = null;
									pathEndPointer = null;
									drawingPath = false;
									return;
								}
								else if(tempPath.getLastNodeKey()==
										pathStartPointer.getNodeKey()&&
										tempPath.getFirstNodeKey()==
										pathEndPointer.getNodeKey())
								{
									reverseCurve = true;
									break;
								}
							}
						}
						Path path;
						if(pathStartPointer==pathEndPointer||reverseCurve)
						{
							path = new Path(pathStartPointer, pathEndPointer, "P"+pathKey, true);
						}
						else
						{
							path = new Path(pathStartPointer, pathEndPointer, "P"+pathKey, curvyPath);
						}
						pathMap.put(pathKey, path);
						pathKey++;
						
						pathStartPointer = null;
						pathEndPointer = null;
						drawingPath = false;
						if(reverseCurve == true||curvyPath==true||path.getFirstNodeKey()==path.getLastNodeKey())
						{
							reverseCurve = false;
							currentMouseState=2;
							drawCurve=true;
							selectedPathKey=pathKey-1;
						}
						
						return;
					}
				}
				
			}
			pathStartPointer = null;
			pathEndPointer = null;
			drawingPath = false;
			return;
			
		}
		else if (currentMouseState == 2)
		{
			for(int i = 0; i < nodeKey; i++)
			{
				if(nodeMap.containsKey(i))
				{
					Node node = nodeMap.get(i);
					if(node.contains(e.getX(), e.getY()))
					{
						selectedNodeKey= i;
						selectedPathKey = -1;
						return;
					}
				}
			}
			for(int i = 0; i < pathKey; i++)
			{
				if(pathMap.containsKey(i))
				{
					Path path = pathMap.get(i);
					if(!path.isCurve()&&path.contains(e.getX(), e.getY()))
					{
						selectedNodeKey=-1;
						selectedPathKey = i;
						return;
					}
				}
			}
			for(int i = 0; i < pathKey; i++)
			{
				if(pathMap.containsKey(i))
				{
					Path path = pathMap.get(i);
					if(path.isCurve()&&path.contains(e.getX(), e.getY()))
					{
						selectedNodeKey=-1;
						selectedPathKey = i;
						return;
					}
				}
			}
		}
		oldMouse.setLocation(e.getX(), e.getY());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(currentMouseState==2)
		{
			for(int i = 0; i < nodeKey; i++)
			{
				if(nodeMap.containsKey(i))
				{
					Node node = nodeMap.get(i);
					if(node.contains(e.getX(), e.getY()))
					{
						selectedNodeKey= i;
						selectedPathKey = -1;
						pressedNode=true;
						return;
					}
				}
			}
		}
		oldMouse.setLocation(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(currentMouseState==2&&pressedNode==true)
		{
			pressedNode=false;
			fakeNode=new Node();
			fakeNode.setNodeKey(-1);
		}
		oldMouse.setLocation(e.getX(), e.getY());
	}
	
	
}
