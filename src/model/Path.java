package model;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

public class Path {

	private String pathName;
	private double value;
	private Node destinationNode;
	private int firstX;
	private int firstY;
	private int lastX;
	private int lastY;
	private int firstNodeKey;
	private int lastNodeKey;
	private int controlX;
	private int controlY;
	private boolean curve;

	public Path(Node firstNode, Node lastNode, String string, boolean curve) {
		setFirstX(firstNode.getX());
		setFirstY(firstNode.getY());
		setLastX(lastNode.getX());
		setLastY(lastNode.getY());
		this.setFirstNodeKey(firstNode.getNodeKey());
		this.setLastNodeKey(lastNode.getNodeKey());
		controlX = ((getFirstX() + (Math.abs(getLastX() - getFirstX()) / 2)
				* ((getLastX() > getFirstX()) ? 1 : -1)));
		controlY = ((getFirstY() + (Math.abs(getLastY() - getFirstY()) / 2)
				* ((getLastY() > getFirstY()) ? 1 : -1)));
		pathName = string;
		value = 1;
		this.setCurve(curve);
	}

	public Path() {
		// TODO Auto-generated constructor stub
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Node getDestinationNode() {
		return destinationNode;
	}

	public void setDestinationNode(Node destinationNode) {
		this.destinationNode = destinationNode;
	}

	/**
	 * @return the lastX
	 */
	public int getLastX() {
		return lastX;
	}

	/**
	 * @param lastX
	 *            the lastX to set
	 */
	public void setLastX(int lastX) {
		this.lastX = lastX;
	}

	/**
	 * @return the firstX
	 */
	public int getFirstX() {
		return firstX;
	}

	/**
	 * @param firstX
	 *            the firstX to set
	 */
	public void setFirstX(int firstX) {
		this.firstX = firstX;
	}

	/**
	 * @return the firstY
	 */
	public int getFirstY() {
		return firstY;
	}

	/**
	 * @param firstY
	 *            the firstY to set
	 */
	public void setFirstY(int firstY) {
		this.firstY = firstY;
	}

	/**
	 * @return the lastY
	 */
	public int getLastY() {
		return lastY;
	}

	/**
	 * @param lastY
	 *            the lastY to set
	 */
	public void setLastY(int lastY) {
		this.lastY = lastY;
	}

	/**
	 * @return the firstNodeKey
	 */
	public int getFirstNodeKey() {
		return firstNodeKey;
	}

	/**
	 * @param firstNodeKey
	 *            the firstNodeKey to set
	 */
	public void setFirstNodeKey(int firstNodeKey) {
		this.firstNodeKey = firstNodeKey;
	}

	/**
	 * @return the lastNodeKey
	 */
	public int getLastNodeKey() {
		return lastNodeKey;
	}

	/**
	 * @param lastNodeKey
	 *            the lastNodeKey to set
	 */
	public void setLastNodeKey(int lastNodeKey) {
		this.lastNodeKey = lastNodeKey;
	}

	public Shape drawShape() { // need to handle both lines and curves
		// TODO Auto-generated method stub
		if (!curve) {
			Shape line = null;
			line = new Line2D.Double(getFirstX(), getFirstY(), getLastX(),
					getLastY());
			return line;
		} else if (firstNodeKey != lastNodeKey) {
			QuadCurve2D.Double curve;
			curve = new QuadCurve2D.Double(getFirstX(), getFirstY(), controlX,
					controlY, getLastX(), getLastY());
			return curve;
		} else {

			int radius = (int) Math.sqrt((controlX - firstX)
					* (controlX - firstX) + (controlY - firstY)
					* (controlY - firstY));
			Ellipse2D.Double circle;
			if (radius <= 20) {

				if (radius == 0) {
					radius = 20;
					controlX = firstX;
					controlY = firstY - 20;
				} else {
					int newradius = 20;
					controlX = (int) (((newradius * (controlX - firstX)) / radius) + firstX);
					controlY = (int) (((newradius * (controlY - firstY)) / radius) + firstY);
					radius = 20;

				}
			}
			circle = new Ellipse2D.Double(controlX - radius, controlY - radius,
					2 * radius, 2 * radius);

			return circle;
		}
	}

	public int getControlX() {
		return controlX;
	}

	public int getControlY() {
		return controlY;
	}

	public void setControlX(int x) {
		controlX = x;
	}

	public void setControlY(int y) {
		controlY = y;
	}

	@SuppressWarnings("unused")
	public int getTextXPos() {
		if (!curve) {
			return (getFirstX() + (Math.abs(getLastX() - getFirstX()) / 2)
					* ((getLastX() > getFirstX()) ? 1 : -1));
		} else if (firstNodeKey != lastNodeKey) {
			QuadCurve2D.Double test1 = new QuadCurve2D.Double();
			QuadCurve2D.Double test2 = new QuadCurve2D.Double();
			((QuadCurve2D.Double) drawShape()).subdivide(test1, test2);
			return (int) test1.getX2();
		} else {
			double radius = (double) Math.sqrt((getControlX() - getFirstX()) * (getControlX() - getFirstX()) + (getControlY() - getFirstY()) * (getControlY() - getFirstY()));
		      double newradius = 2 * radius;
		     return (int) (((newradius * (getControlX() - getFirstX())) / radius) + getFirstX())+10;
		      
		}

	}

	public int getTextYPos() {
		if (!curve) {
			return (getFirstY() + (Math.abs(getLastY() - getFirstY()) / 2)
					* ((getLastY() > getFirstY()) ? 1 : -1) + 20);

		} else if (firstNodeKey != lastNodeKey) {
			QuadCurve2D.Double test1 = new QuadCurve2D.Double();
			QuadCurve2D.Double test2 = new QuadCurve2D.Double();
			((QuadCurve2D.Double) drawShape()).subdivide(test1, test2);
			return (int) test1.getY2() + 20;
		} else {
			double radius = (double) Math.sqrt((getControlX() - getFirstX()) * (getControlX() - getFirstX()) + (getControlY() - getFirstY()) * (getControlY() - getFirstY()));
		      double newradius = 2 * radius;
		      return (int)(((newradius * (getControlY() - getFirstY())) / radius) + getFirstY())+10;
		}
	}

	/**
	 * @return the curve
	 */
	public boolean isCurve() {
		return curve;
	}

	/**
	 * @param curve
	 *            the curve to set
	 */
	public void setCurve(boolean curve) {
		this.curve = curve;
	}

	public boolean contains(int x, int y) {
		// TODO Auto-generated method stub
		Rectangle2D rectangle = new Rectangle2D.Double(x - 2, y - 2, 4, 4);
		if (drawShape().intersects(rectangle)) {
			return true;
		} else {
			return false;
		}
	}

}
