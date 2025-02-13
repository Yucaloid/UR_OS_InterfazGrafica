package ur_os;

public class SJF_NP extends Scheduler{

    
    SJF_NP(OS os){
        super(os);
    }
    
   
    @Override
    public void getNext(boolean cpuEmpty) 
    {
        if (cpuEmpty && !processes.isEmpty()) 
        {
            Process nextProcess = getShortestProcess();
            os.getCPU().addProcess(nextProcess);
        }        
    }
    
    private Process getShortestProcess() 
    {
        Process shortestProcess = processes.get(0);
        for (int i = 1; i < processes.size(); i++) 
        {
            Process currentProcess = processes.get(i);
            if (currentProcess.getRemainingTimeInCurrentBurst() < shortestProcess.getRemainingTimeInCurrentBurst()) 
            {
                shortestProcess = currentProcess;
            }
        }
        processes.remove(shortestProcess);
        return shortestProcess;
    }
        
      
    
    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive
    
}
