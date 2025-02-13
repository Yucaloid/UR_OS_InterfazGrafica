package ur_os;

import java.util.ArrayList;
import java.util.Arrays;

public class MFQ extends Scheduler {

    private int currentScheduler;
    private ArrayList<Scheduler> schedulers;

    public MFQ(OS os) {
        super(os);
        this.currentScheduler = -1;
        this.schedulers = new ArrayList<>();
    }

    public MFQ(OS os, Scheduler... s) {
        this(os);
        schedulers.addAll(Arrays.asList(s));
        if (s.length > 0) {
            currentScheduler = 0;
        }
    }

    @Override
    public void addProcess(Process p) {
        if (p == null) {
            // Opcional: puedes registrar el error o lanzar una excepción personalizada.
            return;
        }
        
        if(p.getState() == ProcessState.NEW || p.getState() == ProcessState.IO) {
            p.setState(ProcessState.READY);
            schedulers.get(0).addProcess(p);
            p.setCurrentScheduler(0);
        } else if(p.getState() == ProcessState.CPU) {
            int tempcurrent = p.getCurrentScheduler();
            if(tempcurrent < schedulers.size() - 1) { // Si no es el último scheduler, pasa al siguiente
                tempcurrent++;
            }
            schedulers.get(tempcurrent).addProcess(p);
            p.setCurrentScheduler(tempcurrent);
            p.setState(ProcessState.READY);
        }
    }

    /**
     * Busca el primer scheduler (mayor prioridad, es decir, índice menor) que no esté vacío.
     * Si no encuentra ninguno, asigna -1 a currentScheduler.
     */
    private void defineCurrentScheduler() {
        for (int i = 0; i < schedulers.size(); i++) {
            if (!schedulers.get(i).isEmpty()) {
                this.currentScheduler = i;
                return;
            }
        }
        // Si todos están vacíos, se asigna -1.
        this.currentScheduler = -1;
    }

    @Override
    public void getNext(boolean cpuEmpty) {
        if (!cpuEmpty) {
            Process tempp = os.getProcessInCPU();
            currentScheduler = tempp.getCurrentScheduler();
            schedulers.get(currentScheduler).getNext(cpuEmpty);

            if (!os.isCPUEmpty() && tempp != os.getProcessInCPU()) {
                int temp = currentScheduler;
                defineCurrentScheduler();
                if (currentScheduler != -1 && currentScheduler < temp) {
                    tempp = os.getProcessInCPU();
                    schedulers.get(tempp.getCurrentScheduler()).returnProcess(tempp);
                    os.removeProcessFromCPU();
                    schedulers.get(currentScheduler).getNext(cpuEmpty);
                }
            }
        }

        if (os.isCPUEmpty()) {
            defineCurrentScheduler();
            if (currentScheduler != -1) {
                schedulers.get(currentScheduler).getNext(true);
            }
        }
    }

    @Override
    public void newProcess(boolean cpuEmpty) {
        // Evento no preemptivo, sin acción.
    }

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
        // Evento no preemptivo, sin acción.
    }
}