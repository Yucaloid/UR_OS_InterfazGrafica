package ur_os;

import static ur_os.InterruptType.SCHEDULER_CPU_TO_RQ;

import java.util.List;


public class OS {
    
    ReadyQueue rq;
    IOQueue ioq;
    private static int process_count = 0;
    SystemOS system;
    CPU cpu;
    
    public OS(SystemOS system, CPU cpu, IOQueue ioq){
        rq = new ReadyQueue(this);
        this.ioq = ioq;
        this.system = system;
        this.cpu = cpu;
        process_count = 0;
    }
    
    public void update(){
        rq.update();
    }

    public CPU getCPU() {
        return cpu;
    }
    
    public boolean isCPUEmpty(){
        return cpu.isEmpty();
    }

    public List<Process> getQueue() {
        return rq.getProcesses(); // Llama al método de ReadyQueue
    }
    
    public Process getProcessInCPU(){
        return cpu.getProcess();
    }
    
    public void interrupt(InterruptType t, Process p){
        
        switch(t){
        
            case CPU: //It is assumed that the process in CPU is done and it has been removed from the cpu
                if(p.isFinished()){//The process finished completely
                    p.setState(ProcessState.FINISHED);
                    p.setTime_finished(system.getTime());
                }else{
                    ioq.addProcess(p);
                    
                }
            break;
            
            case IO: //It is assumed that the process in IO is done and it has been removed from the queue
                rq.addProcess(p);
            break;
            
            case SCHEDULER_CPU_TO_RQ:
                //When the scheduler is preemptive and will send the current process in CPU to the Ready Queue
                Process temp = cpu.extractProcess();
                rq.addProcess(temp);
                if(p != null){
                    cpu.addProcess(p);
                }
                
            break;
            
            
            case SCHEDULER_RQ_TO_CPU:
                //When the scheduler defined which process will go to CPU
                cpu.addProcess(p);
                
            break;
            
            
        }
        
    }
    
    public void removeProcessFromCPU(){
        cpu.removeProcess();
    }
    
    public void create_process(){
        rq.addProcess(new Process(process_count++, system.getTime()));
    }
    
    public void create_process(Process p){
        p.setPid(process_count++);
        rq.addProcess(p);
    }
    
    
    public void showProcesses(){
        System.out.println("Process list:");
        System.out.println(rq.toString());
    }
    
    
    
    
}
