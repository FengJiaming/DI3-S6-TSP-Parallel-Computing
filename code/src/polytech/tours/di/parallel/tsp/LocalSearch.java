package polytech.tours.di.parallel.tsp;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Callable;


public class LocalSearch implements Callable<Solution> {
	
	private Instance instance;
	private int nbTasks;
	private Random rnd;
	
	public LocalSearch(Instance instance,int nbTasksPerThreads,Random rnd){
		this.instance = instance;
		this.nbTasks = nbTasksPerThreads;
		this.rnd = rnd;
	}
	public Solution call() throws Exception{ 
		// this is the parallel task
		Solution bestSolution = generateRandomSolution();
		//System.out.println("Best Solution avant"+bestSolution);
		for(int i = 0;i < nbTasks;i++){
			Solution s = generateRandomSolution();
			//System.out.println("random solution="+s);
			Solution LocalSolution = Search(s);
			//System.out.println("i="+i);
			//System.out.println("i="+i+"LocalSolution="+LocalSolution);
			if(LocalSolution.getOF()<bestSolution.getOF()){
				bestSolution = LocalSolution.clone();
			}
		}
		System.out.println("The Best Solution of " + Thread.currentThread().getName() + " is:\n" + bestSolution + "\n");
		return bestSolution;
	}
	
	
	private Solution generateRandomSolution()
	{
		Solution s = new Solution();
		
		for (int i=0;i<instance.getN();i++){
			s.add(i);
		}
		Collections.shuffle(s,rnd);
		s.setOF(TSPCostCalculator.calcOF(instance.getDistanceMatrix(), s));
		
		return s;
	}
	
	private Solution Search(Solution s){
		boolean flag = true;
		while(flag){
			Solution neighborSolution = exploreNeighborhood(s);
			//System.out.println("neighborSolution="+neighborSolution);
			if (neighborSolution.getOF() < s.getOF()){
				s = neighborSolution.clone();
			}else{
				flag = false;
			}
		}
		//System.out.println("Search return="+s);
		return s;
	}
	
	
	private Solution exploreNeighborhood(Solution s){
		Solution bestLocal;
		bestLocal = s.clone();
		for(int i = 0;i < s.size();i++){
			for(int j = 0;j < s.size();j++){
				s.swap(i, j);
				Solution tempSolution = s.clone();
				tempSolution.setOF(TSPCostCalculator.calcOF(instance.getDistanceMatrix(), tempSolution));
				
				if(tempSolution.getOF()<bestLocal.getOF()){
					bestLocal = tempSolution.clone();
				}
			}
		}
			
		return bestLocal;
	}
	
	
	
	
	
	
	
	
	
	
	
}