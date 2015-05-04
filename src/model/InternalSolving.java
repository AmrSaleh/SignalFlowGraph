package model;
import java.util.ArrayList;


public  interface InternalSolving {

	public void setGraph(Node[] newGraph, Node first , Node last);
	
	public ArrayList<ArrayList<Node>> getForwardPaths();
	
	public ArrayList<ArrayList<Node>> getIndividualLoops();
	
	public ArrayList<ArrayList<ArrayList<Node>>> getNonTouchingLoops();
	
	public ArrayList<Double> getDeltaValues();
	
	public double getTotalDelta();
	
	public double getOverall_TF_Value();
	
	public ArrayList<Double> getLoopsGains();
	
	public ArrayList<Double> getForwardPathsGains();
	
	public ArrayList<Double> getproductsOfNontouchingLoopsGains();
}
