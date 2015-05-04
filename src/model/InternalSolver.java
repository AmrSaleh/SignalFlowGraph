package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class InternalSolver implements InternalSolving {

	private Node[] graph;
	private Node firstNode;
	private Node lastNode;
	private Node tempNode;
	private ArrayList<Node> currentAnswer = new ArrayList<Node>();
	private ArrayList<ArrayList<Node>> forwardPaths = new ArrayList<ArrayList<Node>>();
	private ArrayList<ArrayList<Node>> loops = new ArrayList<ArrayList<Node>>();
	private ArrayList<ArrayList<ArrayList<Node>>> nonTouchingLoops = new ArrayList<ArrayList<ArrayList<Node>>>();
	private ArrayList<Integer> nontouchTemp = new ArrayList<Integer>();
	private ArrayList<ArrayList<Node>> nontouchingSegment = new ArrayList<ArrayList<Node>>();
	private boolean[][] isNontouching;
	private double LoopGain = 1;
	private ArrayList<Double> individualLoopGains = new ArrayList<Double>();
	private double forwardGain = 1;
	private ArrayList<Double> forwardPathsGains = new ArrayList<Double>();
	private double nontouchingProduct = 1;
	private ArrayList<Double> productsOfNontouchingLoopsGains = new ArrayList<Double>();
	private ArrayList<Double> deltaForEachPath = new ArrayList<Double>();
	private double delta = 1;
	private double TF=0;

	@Override
	public void setGraph(Node[] newGraph, Node first, Node last) {
		// TODO Auto-generated method stub
		graph = newGraph;
		firstNode = first;
		lastNode = last;

		initializeAndCalculate();
	}

	// private void setFirstNode(String name) {
	// // TODO Auto-generated method stub
	// for (int i = 0; i < graph.length; i++) {
	// if (graph[i].getName().equals(name)) {
	// firstNode = graph[i];
	// if (i != 0) {
	// swap(i, 0);
	// }
	// return;
	// }
	// }
	//
	// }

	// private void swap(int i, int j) {
	// // TODO Auto-generated method stub
	// Node temp = graph[j];
	// graph[j] = graph[i];
	// graph[i] = temp;
	// }

	// private void setLastNode() {
	// // TODO Auto-generated method stub
	//
	// }

	private void initializeAndCalculate() {
		// TODO Auto-generated method stub
		forwardGain = 1;
		forwardPathsGains.clear();
		forwardPaths.clear();
		currentAnswer.clear();
		findForwardPathsRecursively(firstNode);
		// ////////////////////////////////////////////////////////////
		LoopGain = 1;
		individualLoopGains.clear();
		loops.clear();
		for (int i = 0; i < graph.length; i++) {
			tempNode = graph[i];
			currentAnswer.clear();
			findIndividualLoops(tempNode);
		}
		removeDuplicateLoops();

		// ////////////////////////////////////////////////////////////
		nontouchingProduct = 1;
		productsOfNontouchingLoopsGains.clear();
		nonTouchingLoops.clear();
		isNontouching = new boolean[loops.size()][loops.size()];

		for (int i = 0; i < loops.size(); i++) {
			for (int j = i + 1; j < loops.size(); j++) {
				if (checkNonTouching(loops.get(i), loops.get(j))) {
					// ArrayList<ArrayList<Node>> temp =new
					// ArrayList<ArrayList<Node>>();
					// temp.add(loops.get(i));
					// temp.add(loops.get(j));
					// nonTouchingLoops.add(temp);
					isNontouching[i][j] = true;
					isNontouching[j][i] = true;
				}
			}
		}

		for (int i = 0; i < isNontouching.length; i++) {
			nontouchTemp.clear();
			nontouchTemp.add(i);
			findNonTouchingloops(i, i);
		}

		sortNonTouchingLoops();
		
		// ////////////////////////////////////////////////////////////
		deltaForEachPath.clear();
		ArrayList<Integer> loopsIndicesOfPath;
		ArrayList<Integer> NontouchingloopsOfPathIndices;

		for (int i = 0; i < forwardPaths.size(); i++) {
			loopsIndicesOfPath = getLoopsIndicesForPath(i);
			NontouchingloopsOfPathIndices = getNontouchingloopsOfPathIndices(i);
			deltaForEachPath.add(calculateDeltaForPath(loopsIndicesOfPath,NontouchingloopsOfPathIndices));
		}
		
		////////////////////////////////////////////////////////////////
		delta = 1;
		
		int sign = +1;
		int order = 2;
		double container = 0;
		for (int i = 0; i < individualLoopGains.size(); i++) {
			container += individualLoopGains.get(i);
		}
		delta = delta - container;
		container = 0;

		for (int i = 0; i < productsOfNontouchingLoopsGains.size(); i++) {
			if (nonTouchingLoops.get(i).size() > order) {
				order = nonTouchingLoops.get(i).size();
				delta = delta + (sign * container);
				container = 0;
				sign *= -1;
				
			}
			
			container += productsOfNontouchingLoopsGains.get(i);
		}
		delta= delta + (sign * container);
		/////////////////////////////////////////////////
		TF=0;
		double delta = getTotalDelta();
		ArrayList<Double> pathsGains =getForwardPathsGains();
		ArrayList<Double> deltas=getDeltaValues();
		for (int i = 0; i < pathsGains.size(); i++) {
			TF+= ((pathsGains.get(i)*deltas.get(i))/delta);
		}
	}

	@Override
	public ArrayList<ArrayList<Node>> getForwardPaths() {
		// TODO Auto-generated method stub

		return forwardPaths;
	}

	@SuppressWarnings("unchecked")
	private void findForwardPathsRecursively(Node currentNode) {
		// TODO Auto-generated method stub
		if (currentNode.isVisited())
			return;
		currentNode.setVisited(true);
		currentAnswer.add(currentNode);
		if (currentNode.getNodeKey()==(lastNode.getNodeKey())) {
			forwardPaths.add((ArrayList<Node>) currentAnswer.clone());
			forwardPathsGains.add(forwardGain);
		}
		int paths = currentNode.getOutGoingPaths().size();
		for (int i = 0; i < paths; i++) {
			forwardGain *= currentNode.getOutGoingPaths().get(i).getValue();
			findForwardPathsRecursively(currentNode.getOutGoingPaths().get(i).getDestinationNode());
			forwardGain /= currentNode.getOutGoingPaths().get(i).getValue();
		}
		currentNode.setVisited(false);
		currentAnswer.remove(currentAnswer.size() - 1);
	}

	@Override
	public ArrayList<ArrayList<Node>> getIndividualLoops() {
		// TODO Auto-generated method stub

		return loops;
	}

	private void removeDuplicateLoops() {
		// TODO Auto-generated method stub
		boolean duplicate=true;
		for (int i = 0; i < loops.size(); i++) {
			for (int j = i + 1; j < loops.size(); j++) {
				if (loops.get(i).size() == loops.get(j).size()) {
					duplicate = true;
					for (int j2 = 0; j2 < loops.get(i).size(); j2++) {
						if (!loops.get(j).contains(loops.get(i).get(j2))) {
							duplicate = false;
							break;
						}
					}
					if (duplicate) {
						loops.remove(j);
						individualLoopGains.remove(j);
						j--;
						
					}
				}
			}
			
		}
	}

	private void findIndividualLoops(Node currentNode) {
		// TODO Auto-generated method stub

		if (currentNode.isVisited()) {
			if (currentNode.getNodeKey()==(tempNode.getNodeKey())) {
				currentAnswer.add(currentNode);
				loops.add((ArrayList<Node>) currentAnswer.clone());
				currentAnswer.remove(currentAnswer.size() - 1);
				individualLoopGains.add(LoopGain);
			}

			return;
		}

		currentAnswer.add(currentNode);
		// if (currentNode.getNodeKey()==(tempNode.getNodeKey()) &&
		// currentNode.isVisited()) {
		// loops.add((ArrayList<Node>) currentAnswer.clone());
		// }
		currentNode.setVisited(true);
		int paths = currentNode.getOutGoingPaths().size();
		for (int i = 0; i < paths; i++) {
			LoopGain *= currentNode.getOutGoingPaths().get(i).getValue();
			findIndividualLoops(currentNode.getOutGoingPaths().get(i).getDestinationNode());
			LoopGain /= currentNode.getOutGoingPaths().get(i).getValue();
		}
		currentNode.setVisited(false);
		currentAnswer.remove(currentAnswer.size() - 1);

	}

	@Override
	public ArrayList<Double> getLoopsGains() {
		return individualLoopGains;
	}

	@Override
	public ArrayList<Double> getForwardPathsGains() {
		return forwardPathsGains;
	}

	@Override
	public ArrayList<ArrayList<ArrayList<Node>>> getNonTouchingLoops() {
		// TODO Auto-generated method stub

		return nonTouchingLoops;
	}

	@Override
	public ArrayList<Double> getproductsOfNontouchingLoopsGains() {
		return productsOfNontouchingLoopsGains;
	}

	private void findNonTouchingloops(int index, int pos) {
		// TODO Auto-generated method stub

		for (int i = pos + 1; i < isNontouching.length; i++) {
			if (isNontouching[index][i]) {
				boolean nontouch = true;
				for (int j = 1; j < nontouchTemp.size(); j++) {
					if (!isNontouching[nontouchTemp.get(j)][i]) {
						nontouch = false;
						break;
					}
				}
				if (nontouch) {
					nontouchTemp.add(i);
					findNonTouchingloops(index, i);
				}
			}
		}
		if (nontouchTemp.size() > 1) {
			nontouchingSegment.clear();
			nontouchingProduct = 1;
			for (int i = 0; i < nontouchTemp.size(); i++) {
				nontouchingSegment.add(loops.get(nontouchTemp.get(i)));
				nontouchingProduct *= individualLoopGains.get(nontouchTemp.get(i));
			}
			nonTouchingLoops.add((ArrayList<ArrayList<Node>>) nontouchingSegment.clone());
			productsOfNontouchingLoopsGains.add(nontouchingProduct);
			nontouchTemp.remove(nontouchTemp.size() - 1);
		}
	}

	private boolean checkNonTouching(ArrayList<Node> first, ArrayList<Node> second) {
		for (int i = 0; i < first.size(); i++) {
			for (int j = 0; j < second.size(); j++) {
				if (first.get(i).getNodeKey()==(second.get(j).getNodeKey())) {
					return false;
				}
			}
		}
		return true;
	}

	private void sortNonTouchingLoops() {
		// TODO Auto-generated method stub
		if (nonTouchingLoops.size() <= 1) {
			return;
		}
		ArrayList<ArrayList<Node>> temp;
		int comparisons = 0;
		double tempValue;
		for (int i = 0; i < nonTouchingLoops.size(); i++) {
			comparisons = 0;
			for (int j = 1; j < nonTouchingLoops.size(); j++) {
				if (nonTouchingLoops.get(j - 1).size() > nonTouchingLoops.get(j).size()) {
					comparisons++;
					temp = nonTouchingLoops.get(j);
					nonTouchingLoops.set(j, nonTouchingLoops.get(j - 1));
					nonTouchingLoops.set(j - 1, temp);

					tempValue = productsOfNontouchingLoopsGains.get(j);
					productsOfNontouchingLoopsGains.set(j, productsOfNontouchingLoopsGains.get(j - 1));
					productsOfNontouchingLoopsGains.set(j - 1, tempValue);
				}
			}
			if (comparisons == 0) {
				break;
			}
		}
	}

	@Override
	public ArrayList<Double> getDeltaValues() {
		// TODO Auto-generated method stub
		

		

		
		return deltaForEachPath;
	}

	private Double calculateDeltaForPath(ArrayList<Integer> loopsIndicesOfPath, ArrayList<Integer> nontouchingloopsOfPathIndices) {
		// TODO Auto-generated method stub
		double delta = 1;
		int sign = +1;
		int order = 2;
		double container = 0;
		for (int i = 0; i < loopsIndicesOfPath.size(); i++) {
			container += individualLoopGains.get(loopsIndicesOfPath.get(i));
		}
		delta = delta - container;
		container = 0;

		for (int i = 0; i < nontouchingloopsOfPathIndices.size(); i++) {
			if (nonTouchingLoops.get(nontouchingloopsOfPathIndices.get(i)).size() > order) {
				order = nonTouchingLoops.get(nontouchingloopsOfPathIndices.get(i)).size();
				delta = delta + (sign * container);
				container = 0;
				sign *= -1;
			}

			container += productsOfNontouchingLoopsGains.get(nontouchingloopsOfPathIndices.get(i));
		}
		delta = delta + (sign * container);
		return delta;
	}

	private ArrayList<Integer> getLoopsIndicesForPath(int pathIndex) {
		// TODO Auto-generated method stub
		ArrayList<Integer> loopsOfPath = new ArrayList<Integer>();
		boolean takeLoop = true;
		for (int i = 0; i < loops.size(); i++) {
			takeLoop = true;
			for (int j = 0; j < forwardPaths.get(pathIndex).size(); j++) {
				for (int j2 = 0; j2 < loops.get(i).size(); j2++) {
					if (forwardPaths.get(pathIndex).get(j).getNodeKey()==(loops.get(i).get(j2).getNodeKey())) {
						takeLoop = false;
						break;
					}
				}
				if (!takeLoop) {
					break;
				}
			}
			if (takeLoop) {
				loopsOfPath.add(i);
			}
		}

		return loopsOfPath;
	}

	private ArrayList<Integer> getNontouchingloopsOfPathIndices(int pathIndex) {
		// TODO Auto-generated method stub
		ArrayList<Integer> NontouchingloopsOfPathIndices = new ArrayList<Integer>();

		boolean takeLoop = true;
		for (int i = 0; i < nonTouchingLoops.size(); i++) {
			takeLoop = true;
			for (int x = 0; x < nonTouchingLoops.get(i).size(); x++) {
				for (int j = 0; j < forwardPaths.get(pathIndex).size(); j++) {
					for (int j2 = 0; j2 < nonTouchingLoops.get(i).get(x).size(); j2++) {
						if (forwardPaths.get(pathIndex).get(j).getNodeKey()==(nonTouchingLoops.get(i).get(x).get(j2).getNodeKey())) {
							takeLoop = false;
							break;
						}
					}
					if (!takeLoop) {
						break;
					}
				}
				if (!takeLoop) {
					break;
				}
			}
			if (takeLoop) {
				NontouchingloopsOfPathIndices.add(i);
			}
		}
		return NontouchingloopsOfPathIndices;
	}

	@Override
	public double getTotalDelta() {
		// TODO Auto-generated method stub
		
		

		return delta;
	}

	@Override
	public double getOverall_TF_Value() {
		// TODO Auto-generated method stub
		
		
		
		return TF;
	}

	public static void main(String[] args) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < 6; i++) {
			nodes.add(new Node());
			nodes.get(i).setName("y" + i);
			nodes.get(i).setNodeKey(i);
		}

		ArrayList<Path> paths = new ArrayList<Path>();
		for (int i = 0; i < 10; i++) {
			paths.add(new Path());
			paths.get(i).setPathName("a" + i);
		}
		 paths.get(0).setDestinationNode(nodes.get(1));
		 paths.get(1).setDestinationNode(nodes.get(2));
		 paths.get(2).setDestinationNode(nodes.get(3));
		 paths.get(3).setDestinationNode(nodes.get(4));
		 paths.get(4).setDestinationNode(nodes.get(5));
		 paths.get(5).setDestinationNode(nodes.get(4));
		 paths.get(6).setDestinationNode(nodes.get(2));
		 paths.get(7).setDestinationNode(nodes.get(3));
		 paths.get(8).setDestinationNode(nodes.get(1));
		 paths.get(9).setDestinationNode(nodes.get(5));
		 
		 
		
		 nodes.get(0).addOutPath(paths.get(0));
		 nodes.get(1).addOutPath(paths.get(1));
		 nodes.get(1).addOutPath(paths.get(4));
		 nodes.get(2).addOutPath(paths.get(2));
		 nodes.get(3).addOutPath(paths.get(3));
		 nodes.get(3).addOutPath(paths.get(6));
		 nodes.get(4).addOutPath(paths.get(7));
		 nodes.get(4).addOutPath(paths.get(8));
		 nodes.get(5).addOutPath(paths.get(5));
		 nodes.get(5).addOutPath(paths.get(9));

//		paths.get(0).setDestinationNode(nodes.get(1));
//		paths.get(1).setDestinationNode(nodes.get(2));
//		paths.get(2).setDestinationNode(nodes.get(3));
//		paths.get(3).setDestinationNode(nodes.get(4));
//		paths.get(4).setDestinationNode(nodes.get(0));
//		paths.get(5).setDestinationNode(nodes.get(1));
//		paths.get(6).setDestinationNode(nodes.get(2));
//		paths.get(7).setDestinationNode(nodes.get(3));
//		paths.get(8).setDestinationNode(nodes.get(4));
//
		paths.get(0).setValue(1);
		paths.get(1).setValue(5);
		paths.get(2).setValue(10);
		paths.get(3).setValue(2);
		paths.get(4).setValue(10);
		paths.get(5).setValue(2);
		paths.get(6).setValue(-1);
		paths.get(7).setValue(-2);
		paths.get(8).setValue(-1);
		paths.get(9).setValue(-1);
//
//		nodes.get(0).addOutPath(paths.get(0));
//		nodes.get(1).addOutPath(paths.get(1));
//		nodes.get(1).addOutPath(paths.get(4));
//		nodes.get(2).addOutPath(paths.get(2));
//		nodes.get(2).addOutPath(paths.get(5));
//		nodes.get(2).addOutPath(paths.get(6));
//		nodes.get(3).addOutPath(paths.get(3));
//		nodes.get(3).addOutPath(paths.get(7));
//		nodes.get(4).addOutPath(paths.get(8));
		InternalSolving graph = new InternalSolver();

		
		Node[] nodeArray = new Node[6];
		graph.setGraph(nodes.toArray(nodeArray), nodes.get(0), nodes.get(4));

		ArrayList<ArrayList<Node>> result = graph.getForwardPaths();

		for (int i = 0; i < result.size(); i++) {
			System.out.println("path " + i);
			for (int j = 0; j < result.get(i).size(); j++) {
				System.out.print(result.get(i).get(j).getName() + ", ");

			}
			System.out.print("   --->  gain: " + graph.getForwardPathsGains().get(i));
			System.out.println();
		}
		ArrayList<ArrayList<Node>> resultLoops = graph.getIndividualLoops();
		System.out.println();
		for (int i = 0; i < resultLoops.size(); i++) {
			System.out.println("loop " + i);
			for (int j = 0; j < resultLoops.get(i).size(); j++) {
				System.out.print(resultLoops.get(i).get(j).getName() + ", ");

			}
			System.out.print("   --->  gain: " + graph.getLoopsGains().get(i));
			System.out.println();
		}
		ArrayList<ArrayList<ArrayList<Node>>> nontouchingLoops = graph.getNonTouchingLoops();
		System.out.println();
		for (int i = 0; i < nontouchingLoops.size(); i++) {
			System.out.println("Nontouching loops " + i);
			for (int j = 0; j < nontouchingLoops.get(i).size(); j++) {
				for (int j2 = 0; j2 < nontouchingLoops.get(i).get(j).size(); j2++) {
					System.out.print(nontouchingLoops.get(i).get(j).get(j2).getName() + ", ");
				}
				System.out.print(" --- ");
			}
			System.out.print("   ###  gain: " + graph.getproductsOfNontouchingLoopsGains().get(i));
			System.out.println();
		}

		System.out.println();
		System.out.println("Total delta = " + graph.getTotalDelta());
		
		
		ArrayList<Double> deltasForPath = graph.getDeltaValues();
		System.out.println();
		for (int i = 0; i < deltasForPath.size(); i++) {
			System.out.println("path "+i +" delta = " + deltasForPath.get(i));
		}
		
		System.out.println();
		System.out.println("Total TF = " + graph.getOverall_TF_Value());
	}
}
