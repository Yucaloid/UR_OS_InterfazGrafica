package ur_os;

public class ProcessBurst {
    
    private int cycles;
    private int remainingCycles;
    private ProcessBurstType type;
    private boolean finished;

    public ProcessBurst(int cycles, ProcessBurstType type) {
        this.cycles = cycles;
        this.type = type;
        this.remainingCycles = cycles;
        this.finished = false;
    }
    
    public ProcessBurst(ProcessBurst p) {
        this.cycles = p.cycles;
        this.type = p.type;
        this.remainingCycles = p.remainingCycles;
        this.finished = p.finished;
    }

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public ProcessBurstType getType() {
        return type;
    }

    public void setType(ProcessBurstType type) {
        this.type = type;
    }

    // Método para obtener el tiempo inicial del burst (ciclos totales)
    public int getInitialTime() {
        return cycles;
    }

    // Método para obtener el tiempo restante del burst
    public int getRemainingTime() {
        return remainingCycles;
    }

    public boolean advanceBurst() {
        if (remainingCycles > 0) {
            this.remainingCycles--;
            if (remainingCycles == 0) {
                finished = true;
            }
        } else {
            System.out.println("Error in burst!");
        }
        return finished;
    }

    public int getRemainingCycles() {
        return remainingCycles;
    }

    public boolean isFinished() {
        return finished;
    }
    
    @Override
    public String toString() {
        return "C: " + cycles + " RC: " + this.remainingCycles + " T: " + this.type + " F: " + this.finished;
    }
}