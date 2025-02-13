package ur_os;

import java.util.ArrayList;
import java.util.List;

public class ReadyQueue {
    
    Scheduler s;
    OS os;
    
    
    public ReadyQueue(OS os){
        this.os = os;

        if( UR_OS.simulationType == ProcessType.FCFS)
        {
            s = new FCFS(os);
        }
        else if( UR_OS.simulationType == ProcessType.SJF_NP)
        {
            s = new SJF_NP(os);
        }
        else if( UR_OS.simulationType == ProcessType.SJF_P)
        {
            s = new SJF_P(os);
        }
        else if( UR_OS.simulationType == ProcessType.RR)
        {
            s = new RoundRobin(os,UR_OS.quantum);
        }
        else if( UR_OS.simulationType == ProcessType.MFQ)
        {
            s = new MFQ(os,new RoundRobin(os,3),new RoundRobin(os,6),new FCFS(os));
        }
        else if( UR_OS.simulationType == ProcessType.PQ)
        {
            s = new PriorityQueue(os,new RoundRobin(os,9),new RoundRobin(os,6),new RoundRobin(os,3));
        }
        else
        {
            s = new FCFS(os); 
        }
    }
    
    public ReadyQueue(OS OS, Scheduler s){
        this.os = OS;
        this.s = s;
    }
    
    public void addProcess(Process p){
        s.addProcess(p);
    }
    
    public Process removeProcess(Process p){
        return s.removeProcess(p);
    }
    
    public void update(){
        s.update();
    }

    public List<Process> getProcesses() {
        return s.getProcesses();  // Devuelve la lista de procesos directamente desde el scheduler
    }
        
    public String toString(){
        
        return s.toString();
    }
    
   
    
}
