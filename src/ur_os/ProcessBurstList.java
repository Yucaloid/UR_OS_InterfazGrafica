package ur_os;

import java.util.ArrayList;
import java.util.Random;

public class ProcessBurstList {
    
    private static final int BURSTS_SIMPLE_SIM = 6;
    ArrayList<ProcessBurst> bursts;
    Random r;
    int currentBurst;
    boolean finished;

    public ProcessBurstList() {
        bursts = new ArrayList();
        r = new Random();
        this.currentBurst = 0;
        finished = false;
    }
    
    public ProcessBurstList(ProcessBurstList b) {
        this(b.getList());
    }
    
    public ProcessBurstList(ArrayList<ProcessBurst> b) {
        this();
        for (ProcessBurst burst : b) {
            bursts.add(new ProcessBurst(burst));
        }
    }
    
    public boolean isFinished(){
        return finished;
    }
    
    public boolean advanceBurst(){
        if(currentBurst < bursts.size()){
            if(bursts.get(currentBurst).advanceBurst()){  //True means that the current burst is finished
                currentBurst++;
                if(currentBurst == bursts.size())
                    finished = true;
                return true; //True means that the current burst is finished
            }
        }
        return false;  //False means that the current burst is not finished
    }
    
    public void addBurst(ProcessBurst b){
        int last = bursts.size()-1;
        if(last >= 0 && bursts.get(last).getType() != b.getType()){
                bursts.add(b);
        }else if(last == -1 && b.getType() == ProcessBurstType.CPU){
            bursts.add(b);
        }else
            System.out.println("Error in burst addition");
    }
    
    private ArrayList<ProcessBurst> getList(){
        return this.bursts;
    }
    
    public int getRemainingTimeInCurrentBurst(){
        return bursts.get(currentBurst).getRemainingCycles();
    }
    
    public boolean isCurrentBurstCPU(){
        return bursts.get(currentBurst).getType() == ProcessBurstType.CPU;
    }
    
    public void generateSimpleBursts(){
        
        ProcessBurst temp = new ProcessBurst(BURSTS_SIMPLE_SIM,ProcessBurstType.CPU);    
        addBurst(temp);
        temp = new ProcessBurst(BURSTS_SIMPLE_SIM,ProcessBurstType.IO);    
        addBurst(temp);
        temp = new ProcessBurst(BURSTS_SIMPLE_SIM,ProcessBurstType.CPU);    
        addBurst(temp);
        
    }
    
    
    public void generateRandomBursts(int numCPUBursts, int maxCPUCycles, int maxIOCycles){
    
        
        if(numCPUBursts > 0){
            int temp_val;
            do{
                temp_val = r.nextInt(maxCPUCycles);
            }while(temp_val == 0);
            
            ProcessBurst temp = new ProcessBurst(temp_val,ProcessBurstType.CPU);    
            addBurst(temp);
            for (int i = 1; i < numCPUBursts; i++) {
                do{
                    temp_val = r.nextInt(maxIOCycles);
                }while(temp_val == 0);
                temp = new ProcessBurst(temp_val,ProcessBurstType.IO);    
                addBurst(temp);
                
                do{
                    temp_val = r.nextInt(maxCPUCycles);
                }while(temp_val == 0);
                temp = new ProcessBurst(temp_val,ProcessBurstType.CPU);    
                addBurst(temp);
            }
            
        
        }else{
            System.out.println("Error - Burst list empty!");
        }
        
    }
    
    public int getTotalExecutionTime(){
        int tot = 0;
        
        for (ProcessBurst burst : bursts) {
            tot = tot + burst.getCycles();
        }
        
        return tot;
    }
    
    public String toString(){
       StringBuilder sb = new StringBuilder();
       
       sb.append("Current burst: ");
       sb.append(this.currentBurst);
       sb.append("\n");
       sb.append("Burst list: ");
        for (ProcessBurst burst : bursts) {
            sb.append(burst.toString());
            sb.append("\t");
        }
       return sb.toString();
    }

    public ArrayList<ProcessBurst> getBursts() {
        return bursts;
    }

    public int getTotalCpuCycles() {
        int totalCycles = 0;
        for (ProcessBurst burst : bursts) {
            if (burst.getType() == ProcessBurstType.CPU) {
                totalCycles += burst.getCycles();
            }
        }
        return totalCycles;
    }

    // Ciclos de CPU ejecutados hasta el momento
    public int getExecutedCpuCycles() {
        int executedCycles = 0;
        for (int i = 0; i < currentBurst; i++) {
            ProcessBurst burst = bursts.get(i);
            if (burst.getType() == ProcessBurstType.CPU) {
                executedCycles += burst.getCycles();
            }
        }
        if (currentBurst < bursts.size() && bursts.get(currentBurst).getType() == ProcessBurstType.CPU) {
            executedCycles += bursts.get(currentBurst).getCycles() - bursts.get(currentBurst).getRemainingCycles();
        }
        return executedCycles;
    }

    // Total de ciclos de IO
    public int getTotalIoCycles() {
        int totalCycles = 0;
        for (ProcessBurst burst : bursts) {
            if (burst.getType() == ProcessBurstType.IO) {
                totalCycles += burst.getCycles();
            }
        }
        return totalCycles;
    }

    // Ciclos de IO ejecutados hasta el momento
    public int getExecutedIoCycles() {
        int executedCycles = 0;
        for (int i = 0; i < currentBurst; i++) {
            ProcessBurst burst = bursts.get(i);
            if (burst.getType() == ProcessBurstType.IO) {
                executedCycles += burst.getCycles();
            }
        }
        if (currentBurst < bursts.size() && bursts.get(currentBurst).getType() == ProcessBurstType.IO) {
            executedCycles += bursts.get(currentBurst).getCycles() - bursts.get(currentBurst).getRemainingCycles();
        }
        return executedCycles;
    }
    
    public int getCurrentBurstIndex() {
        return currentBurst;
    }

}
 