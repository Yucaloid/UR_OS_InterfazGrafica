package ur_os;

import java.util.Random;

public class Process implements Comparable{
    public static final int NUM_CPU_CYCLES = 3;
    public static final int MAX_CPU_CYCLES = 10;
    public static final int MAX_IO_CYCLES = 10;
    int pid;
    int time_init;
    int time_finished;
    ProcessBurstList pbl;
    ProcessState state;
    int currentScheduler;
    int priority;
    Random r;


    public Process() {
        r = new Random();
        pid = -1;
        time_init = 0;
        time_finished = -1;
        pbl = new ProcessBurstList();
        pbl.generateRandomBursts(NUM_CPU_CYCLES, MAX_CPU_CYCLES, MAX_IO_CYCLES);
        //pbl.generateSimpleBursts(); //Generates process with 3 bursts (CPU, IO, CPU) with 5 cycles each
        state = ProcessState.NEW;
        currentScheduler = 0;
        priority = 0;
    }
    
    public Process(boolean auto) {
        pid = -1;
        time_init = 0;
        time_finished = -1;
        pbl = new ProcessBurstList();
        if(auto){
            pbl.generateRandomBursts(NUM_CPU_CYCLES, MAX_CPU_CYCLES, MAX_IO_CYCLES);
            //pbl.generateSimpleBursts(); //Generates process with 3 bursts (CPU, IO, CPU) with 5 cycles each
            priority = r.nextInt(10);
        }
        state = ProcessState.NEW;
    }
    
    public Process(int pid, int time_init) {
        this();
        this.pid = pid;
        this.time_init = time_init;
    }
    
    public Process(Process p) {
        this.pid = p.pid;
        this.time_init = p.time_init;
        this.pbl = new ProcessBurstList(p.getPBL());
    }

    public int getId() {
        return this.pid;
    }

    public boolean advanceBurst(){
        return pbl.advanceBurst();
    }
    
    public boolean isFinished(){
        return pbl.isFinished();
    }

    public void setTime_finished(int time_finished) {
        this.time_finished = time_finished;
    }
    
    public void addBurst(ProcessBurst pb){
        pbl.addBurst(pb);
    }
    
    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getTime_init() {
        return time_init;
    }

    public void setTime_init(int time_init) {
        this.time_init = time_init;
    }
    
    public ProcessBurstList getPBL(){
        return pbl;
    }

    public ProcessState getState() {
        return state;
    }

    public int getTime_finished() {
        return time_finished;
    }

    public int getTotalExecutionTime(){
        return pbl.getTotalExecutionTime();
    }
    
    public void setState(ProcessState state) {
        this.state = state;
    }
        
    
    public int getRemainingTimeInCurrentBurst(){
        return pbl.getRemainingTimeInCurrentBurst();
    }
    
    public boolean isCurrentBurstCPU(){
        return pbl.isCurrentBurstCPU();
    }

    public ProcessBurstList getPbl() {
        return pbl;
    }

    public void setPbl(ProcessBurstList pbl) {
        this.pbl = pbl;
    }

    public int getCurrentScheduler() {
        return currentScheduler;
    }

    public void setCurrentScheduler(int currentScheduler) {
        this.currentScheduler = currentScheduler;
    }
    
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public String toString(){
        return "PID: "+pid+" t: "+time_init+" "+pbl;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Process){
            Process p = (Process)o;
            return this.getPid() - p.getPid();
        }
        
        return -1;
    }
    
    @Override
    public boolean equals(Object o){
    
        if(o instanceof Process){
            Process p = (Process)o;
            return this.getPid() == p.getPid();
        }
        
        return false;
        
    }

    public double getCpuProgress() {
        int totalCpuTime = 0;
        int remainingCpuTime = 0;
    
        for (ProcessBurst burst : pbl.getBursts()) {
            if (burst.getType() == ProcessBurstType.CPU) {
                totalCpuTime += burst.getInitialTime();
                remainingCpuTime += burst.getRemainingTime();
            }
        }
        
        if (totalCpuTime == 0) {
            return 1.0; // Si no hay CPU bursts, consideramos el progreso como completo.
        }
    
        return (totalCpuTime - remainingCpuTime) / (double) totalCpuTime;
    }
    
    public double getIoProgress() {
        int totalIoTime = 0;
        int remainingIoTime = 0;
    
        for (ProcessBurst burst : pbl.getBursts()) {
            if (burst.getType() == ProcessBurstType.IO) {
                totalIoTime += burst.getInitialTime();
                remainingIoTime += burst.getRemainingTime();
            }
        }
        
        if (totalIoTime == 0) {
            return 1.0; // Si no hay IO bursts, consideramos el progreso como completo.
        }
    
        return (totalIoTime - remainingIoTime) / (double) totalIoTime;
    }


    public int getTotalCpuCycles() {
        return pbl.getTotalCpuCycles();
    }

    public int getExecutedCpuCycles() {
        return pbl.getExecutedCpuCycles();
    }

    public int getTotalIoCycles() {
        return pbl.getTotalIoCycles();
    }

    public int getExecutedIoCycles() {
        return pbl.getExecutedIoCycles();
    }

    public boolean isCpuRunning() {
        if (pbl.getCurrentBurstIndex() < pbl.getBursts().size()) { // Validación del índice
            return pbl.isCurrentBurstCPU();
        }
        return false;
    }
    
    public boolean isIoRunning() {
        if (pbl.getCurrentBurstIndex() < pbl.getBursts().size()) { // Validación del índice
            return !pbl.isCurrentBurstCPU();
        }
        return false;
    }
        
}
