package ur_os;

import java.util.ArrayList;
import java.util.Arrays;


public class PriorityQueue extends Scheduler {

    private int currentScheduler; // Índice del scheduler actualmente activo
    private ArrayList<Scheduler> schedulers; // Lista de schedulers (colas) por prioridad

    public PriorityQueue(OS os) {
        super(os);
        this.currentScheduler = -1;
        this.schedulers = new ArrayList<>();
    }

    public PriorityQueue(OS os, Scheduler... s) {
        this(os);
        schedulers.addAll(Arrays.asList(s));
        if (schedulers.isEmpty()) {
            throw new IllegalArgumentException("Debe proveerse al menos un scheduler.");
        }
        currentScheduler = 0;
    }

    @Override
    public void addProcess(Process p) {
        if (p == null) {
            return; // O lanzar una excepción según la lógica de la aplicación
        }
        int priority = p.getPriority();
        // Aseguramos que la prioridad no sea negativa
        if (priority < 0) {
            priority = 0;
        }
        // Si la prioridad está dentro del rango de schedulers, se usa esa cola,
        // de lo contrario se utiliza la última.
        int targetIndex = (priority < schedulers.size()) ? priority : schedulers.size() - 1;
        schedulers.get(targetIndex).addProcess(p);
    }

    private void defineCurrentScheduler() {
        for (int i = 0; i < schedulers.size(); i++) {
            if (!schedulers.get(i).isEmpty()) {
                currentScheduler = i;
                return;
            }
        }
        currentScheduler = -1; // Todas las colas están vacías
    }

    @Override
    public void getNext(boolean cpuEmpty) {
        if (cpuEmpty) {
            // CPU vacía: se busca el scheduler con mayor prioridad que tenga procesos
            defineCurrentScheduler();
            if (currentScheduler != -1) {
                schedulers.get(currentScheduler).getNext(true);
            }
        } else {
            // CPU no vacía: se obtiene el proceso actual
            Process currentProcess = os.getProcessInCPU();
            if (currentProcess == null) {
                // Por precaución, si por alguna razón no hay proceso, no se hace nada
                return;
            }
            int processPriority = currentProcess.getPriority();
            // Validamos que la prioridad no sea negativa
            if (processPriority < 0) {
                processPriority = 0;
            }
            
            // Buscamos si existe un scheduler con mayor prioridad (índice menor)
            defineCurrentScheduler();
            if (currentScheduler != -1 && currentScheduler < processPriority) {
                // Se encontró un proceso con mayor prioridad, se preempta el actual
                os.interrupt(InterruptType.SCHEDULER_CPU_TO_RQ, currentProcess);
                schedulers.get(currentScheduler).getNext(true);
            } else {
                // No hay un proceso de mayor prioridad esperando: se continúa con el proceso actual.
                // Usamos la cola correspondiente al proceso actual, verificando el límite de índices.
                int targetIndex = (processPriority < schedulers.size()) 
                                  ? processPriority 
                                  : schedulers.size() - 1;
                schedulers.get(targetIndex).getNext(false);
            }
        }
    }

    @Override
    public void newProcess(boolean cpuEmpty) {
        // Implementar si es necesario
    }

    @Override
    public void IOReturningProcess(boolean cpuEmpty) {
        // Implementar si es necesario
    }
}