package ur_os;

public class RoundRobin extends Scheduler{

    int q;
    int cont;
    
    RoundRobin(OS os){
        super(os);
        q = 5;
        cont=0;
    }
    
    RoundRobin(OS os, int q){
        this(os);
        this.q = q;
    }
    

    
    void resetCounter(){
        cont=0;
    }
   
    @Override
    public void getNext(boolean cpuEmpty) {
        if(cpuEmpty){
           if(processes.isEmpty())
           {
               cont=0;
           }
           else
           {

               Process p = processes.get(0);
               processes.remove();
               cont = 0;
               os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
           }
       }
       else
       {
           cont++;
           if(cont == q)
           {
                if(processes.isEmpty())
                {
                    os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, null);
                    if(!processes.isEmpty())
                    { 
                        Process p = processes.get(0);
                        processes.remove();
                        cont = 0; 
                        os.interrupt(InterruptType.SCHEDULER_RQ_TO_CPU, p);
                    }
                }
                else
                {
                    Process p = processes.get(0);
                    processes.remove();
                    os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, p);
                }
                cont = 0;
            }
           

       }
        
    }
    
    
    @Override
    public void newProcess(boolean cpuEmpty) {} //Non-preemtive 

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {} //Non-preemtive
    
}
