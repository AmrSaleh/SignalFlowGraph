package model;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


public class Node {

	private String name;
	private int x;
	private int y;
	private ArrayList<Path> outGoingPaths = new ArrayList<Path>();
	private boolean visited =false;
	private int nodeKey;
	
	public Node(int xPos, int yPos, int nodeKey, String string)
	{
		setX(xPos);
		setY(yPos);
		this.setNodeKey(nodeKey);
		name = string;
	}
	public Node() {
		// TODO Auto-generated constructor stub
	}
	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Path> getOutGoingPaths() {
		return outGoingPaths;
	}

	public void setOutGoingPaths(ArrayList<Path> outGoingPaths) {
		this.outGoingPaths = outGoingPaths;
	}
	
	public void addOutPath(Path newPath){
		outGoingPaths.add(newPath);
	}
	public Shape drawShape() {
		Shape circle = null;
		circle = new Ellipse2D.Double(getX()-10, getY()-10, 20,
				20);
		return circle;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the nodeKey
	 */
	public int getNodeKey() {
		return nodeKey;
	}
	/**
	 * @param nodeKey the nodeKey to set
	 */
	public void setNodeKey(int nodeKey) {
		this.nodeKey = nodeKey;
	}
	public boolean contains(int x2, int y2) {
		// TODO Auto-generated method stub
		if(drawShape().contains(x2, y2))
		{
			return true;
		}
		return false;
	}
	public Shape drawControlPoint() {
		// TODO Auto-generated method stub
		Shape circle = null;
		circle = new Ellipse2D.Double(getX()-5, getY()-5, 10,
				10);
		return circle;
	}
	public Node clone()
	{
		Node clone = new Node(x,y,nodeKey,name);
		clone.setOutGoingPaths(getOutGoingPaths());
		return clone;
	}
	public String toString()
	{
		return "(Node "+name+" Key "+nodeKey+")";
	}
}
