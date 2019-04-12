package polytech.tours.di.parallel.tsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;




public class MLS implements Algorithm{

	public Solution run(Properties config){
		//1 build instance
		InstanceReader ir=new InstanceReader();
		ir.buildInstance(config.getProperty("instance"));
		//1.1 read instance file
		//1.2 obtain instance
		Instance instance=ir.getInstance();
		
		//print some instances
		System.out.println("d(1,2)="+instance.getDistance(1, 2));
		System.out.println("d(10,19)="+instance.getDistance(10, 19));
		
		
		long max_cpu=Long.valueOf(config.getProperty("maxcpu"));
		
		//2 set up random number generator
		//2.1 read seed from config
		Random rnd=new Random(Long.valueOf(config.getProperty("seed")));
		//2.2 instantiate a random number generator
		Solution s=new Solution();
		Solution best=null;
		
		//3 set up an executor
		
		//3.1 read the number of threads from config
		int nbThreads=Integer.valueOf(config.getProperty("thread"));;
		
		//3.2 instantiate an executorService
		ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
		//4 build parallel tasks
		//4.1 create a list of LocalSearch tasks
		List<Future<Solution>> results = null;
		List<Callable<Solution>> tasks=new ArrayList<Callable<Solution>>();
		int nbTasks=120;
		for(int t=1; t<=nbThreads; t++){
			tasks.add(new LocalSearch(instance,(nbTasks/nbThreads),rnd));
		}
		//5 execute the tasks
		//5.1 call invokeAll() on the executor
		try {
			//Ask the executor to execute the tasks and store the results on a list
			results=executor.invokeAll(tasks);
			executor.shutdown();
		} catch (InterruptedException e) {
			System.out.println("bizare");
		}
		
		//6 collect results 
		//6.1 compare the best local optimum returned by each task
		try {
			//long counter=0l;
			for(Future<Solution> t:results){
				//counter=counter+t.get();
				if(best==null)
					best=t.get().clone();
				else if(t.get().getOF()<best.getOF())
					best=t.get().clone();
			}
		}
		catch (InterruptedException | ExecutionException e) {
			System.out.println("Je ne comprend pas.");
		}
		//7 return the best local optimum
		return  best;
	}
}