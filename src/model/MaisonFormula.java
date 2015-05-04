package model;

import java.util.ArrayList;

public class MaisonFormula implements InternalSolving {

	private InternalSolver graph = new InternalSolver(); 
	@Override
	public void setGraph(Node[] newGraph, Node first, Node last) {
		// TODO Auto-generated method stub
		if (isStartingNode(newGraph,first)) {
			
		}
	}

	private boolean isStartingNode(Node[] newGraph,Node first) {
		// TODO Auto-generated method stub
		for (int i = 0; i < newGraph.length; i++) {
			
		}
		
		
		return false;
	}

	@Override
	public ArrayList<ArrayList<Node>> getForwardPaths() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ArrayList<Node>> getIndividualLoops() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ArrayList<ArrayList<Node>>> getNonTouchingLoops() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Double> getDeltaValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTotalDelta() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getOverall_TF_Value() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<Double> getLoopsGains() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Double> getForwardPathsGains() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Double> getproductsOfNontouchingLoopsGains() {
		// TODO Auto-generated method stub
		return null;
	}

}
