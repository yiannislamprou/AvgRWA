 // Name:           AvgRWA.java
 // Author:         Ioannis Lamprou
 // Description:    Experiments on the cover time of a Random Walk on what's Available
 
 import java.lang.Math;
 import java.util.ArrayList;
 
 public class AvgRWA{
     
     private double randomThreshold; // for random graph construction
     private long numIterations;     // for averaging over cover time
     
     public static void main(String [] args){
        // Run as AvgRWA <threshold> <number-of-nodes> <p> <q>
        // threshold = 1 for clique, threshold = 0 for path
        
        final long numIterations = 1000; // modify accordingly
         
        double randomThreshold = Double.parseDouble(args[0]);
        int n = Integer.parseInt(args[1]);
        double p = Double.parseDouble(args[2]);
        double q = Double.parseDouble(args[3]);
         
        AvgRWA avg = new AvgRWA(numIterations, randomThreshold);
        ArrayList<Edge> G = new ArrayList<Edge>(avg.createGraph(n));  // created once; experiments on same graph; "warm start"
         
        // Static cover time
        double staticCover = 0.0;
        for(int i = 0; i < numIterations; i++){
           staticCover += avg.coverTime(G, n, 1, 0);
        }     
        staticCover = 1.0*staticCover/numIterations;
         
        // Temporal cover time
        double temporalCover = 0.0;
        for(int i = 0; i < numIterations; i++){
           temporalCover += avg.coverTime(G, n, p, q); 
        }
        temporalCover = 1.0*temporalCover/numIterations;
         
        // Compute Min/Max Degree
        int Delta = 0, delta = n;
        int [] degree = new int[n];
        // Initialize to 0
        for(int i = 0; i < n; i++)
            degree[i] = 0;
        // Count degree
        for(int i = 0; i < G.size(); i++){
            degree[G.get(i).getEndpoint1()] += 1;
            degree[G.get(i).getEndpoint2()] += 1;
        }
        // Export min/max
        for(int i = 0; i < n; i++){
            if(degree[i] < delta)
                delta = degree[i];
            if(degree[i] > Delta)
                Delta = degree[i];
        }
        
        // Printouts
        System.out.println("-------------------------------------------------------");
        System.out.println("Graph on " + n + " nodes with p = " + p + " and q = " + q);   
        System.out.println("Min degree: " + delta + " Max degree: " + Delta);      
        System.out.println("Experimental static cover time averaged over " + numIterations + " iterations: " + staticCover);        
        System.out.println("Experimental temporal cover time averaged over " + numIterations + " iterations: " + temporalCover);
        System.out.println("Lower bound: " + 1.0*staticCover/(1-Math.pow(1.0-Math.max(p, 1-q), Delta)) + "\nUpper bound: " + 1.0*staticCover/(1-Math.pow(1.0-Math.min(p, 1-q), delta)));
        System.out.println("-------------------------------------------------------");
     }
     
     public AvgRWA(long n, double r){
         numIterations = n;
         randomThreshold = r;
     }
     
     public ArrayList<Edge> createGraph(int n){
         ArrayList<Edge> G = new ArrayList<Edge>();
         
         // Path to ensure connectivity
         for(int i = 0; i < n-1; i++){
            Edge e = new Edge(i, i+1, false);
            G.add(e);
         }
         // Randomly assign rest of edges
         for(int i = 0; i < n; i++){
            for(int j = i+2; j < n; j++){
                if(Math.random() < this.randomThreshold){
                    Edge e = new Edge(i, j, false);
                    G.add(e);
                 }
             }
         }  
         return G;
     }
     
     public int coverTime(ArrayList<Edge> G, int n, double p, double q){
         
         boolean [] covered = new boolean[n];
         int coverTime = 0;
         // Walk starts from random node
         int position = (int) Math.round(Math.random()*(n-1));
         //System.out.println("Initial position is " + position);
         
         // Initializing cover vector
         for(int i = 1; i < n; i++)
             covered[i] = false;
         covered[position] = true;
         
         // Main loop
         while(!allTrue(covered, n)){
             
            // First Phase: Edge Evolution
            for(int i = 0; i < G.size(); i++){
                Edge e = G.get(i);
                if(e.isAlive() == false){
                    e.setAlive(p);
                }    
                else{
                    e.setAlive(1.0-q);
                }    
                G.set(i, e);
            }
            
            // Print Graph
            /*
            for(int i = 0; i < G.size(); i++){
                Edge e = G.get(i); 
                System.out.println("Edge (" + e.getEndpoint1() + ", " + e.getEndpoint2() + ") " + e.isAlive());
            }
            */
            
            // Second Phase: Walk Evolution
            ArrayList<Integer> neighbors = new ArrayList<Integer>();
            for(int i = 0; i < G.size(); i++){
                Edge e = G.get(i);
                // Filling in a list of current instance neighbors
                if(e.isAlive()){
                    if(e.getEndpoint1() == position)
                        neighbors.add(e.getEndpoint2());
                    if(e.getEndpoint2() == position)
                        neighbors.add(e.getEndpoint1());
                }
            }   
            
            // Print neighbors
            /*
            System.out.println("The available neighbors are " + neighbors);
            */
            
            // Increment Counter
            coverTime += 1;
            
            // Selecting a neighbor to visit
            if(neighbors.size() == 0)
                continue;
            else{ // there exists at least one neighbor to go to
                position = neighbors.get((int) Math.round((neighbors.size()-1)*Math.random()));
                covered[position] = true;
                //System.out.println("The new position is " + position);
            }    
         } 
         return coverTime; 
     }
    
     public static boolean allTrue(boolean [] b, int n){
         for(int i = 0; i < n; i++)
             if(b[i] == false)
                 return false;
         return true;    
     }
    
 }