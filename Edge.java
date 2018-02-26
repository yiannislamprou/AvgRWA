
import java.lang.*;
import java.util.*;

public class Edge {
    
    private int endpoint1;
    private int endpoint2;
    private boolean alive;
    
    public Edge(int e1, int e2, boolean a){
        endpoint1 = e1;
        endpoint2 = e2;
        alive = a;
    }
    
    public Edge(Edge e){
        this.endpoint1 = e.getEndpoint1();
        this.endpoint2 = e.getEndpoint2();
        this.alive = e.isAlive();
    }
    
    public int getEndpoint1(){
        return endpoint1;
    }
    
    public int getEndpoint2(){
        return endpoint2;
    } 
    
    public boolean isAlive(){
        return alive;
    }
    
    public void setAlive(double p){
        if(Math.random() < p)
            alive = true;
        else
            alive = false;
    }
       
}