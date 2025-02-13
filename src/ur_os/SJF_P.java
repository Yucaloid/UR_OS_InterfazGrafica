package ur_os;

public class SJF_P extends Scheduler{

    
    SJF_P(OS os){
        super(os);
    }
    
    @Override
    public void newProcess(boolean cpuEmpty) 
    {
        if (cpuEmpty) 
        {
          getNext(true);
        } 
        else
        {
          Process incomingProcess = os.getCPU().extractProcess();
          processes.add(incomingProcess);
        }
    } 

    @Override
    public void IOReturningProcess(boolean cpuEmpty)
    {
        newProcess(cpuEmpty);   
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
        processes.remove(shortestProcess); // Eliminar el proceso seleccionado de la cola.
        return shortestProcess;
    }
    
    
}
