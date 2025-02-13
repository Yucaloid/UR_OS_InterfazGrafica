/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ur_os;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;


/**
 *
 * @author Johan Caro 
 */
public class SystemOS implements Runnable{

    static final int WAITINGTIME = 100;
    
    private static int clock = 0;
    private static final int MAX_SIM_CYCLES = 1000;
    private static final int MAX_SIM_PROC_CREATION_TIME = 50;
    private static final double PROB_PROC_CREATION = 0.1;
    private static Random r = new Random(1235);
    private OS os;
    private CPU cpu;
    private IOQueue ioq;
    private List<ProcessData> processDataList;
    private TextArea simulationOutput;
    private List<ProgressBar> cpuProgressBars;
    private List<ProgressBar> ioProgressBars;
    private ListView<String> processQueueView;
    private Label clockLabel;
    private TableView<MetricData> metricsTable;
    private ObservableList<MetricData> metricDataList;
    
    protected ArrayList<Process> processes;
    ArrayList<Integer> execution;

    public SystemOS() {
        cpu = new CPU();
        ioq = new IOQueue();
        os = new OS(this, cpu, ioq);
        cpu.setOS(os);
        ioq.setOS(os);
        execution = new ArrayList();
        processes = new ArrayList();
        initSimulationQueue();
        //initSimulationQueueSimple();
        //initSimulationQueueSimpler2();
        showProcesses();
    }

    public SystemOS(List<ProcessData> processDataList) {
        cpu = new CPU();
        ioq = new IOQueue();
        os = new OS(this, cpu, ioq);
        cpu.setOS(os);
        ioq.setOS(os);
        execution = new ArrayList();
        processes = new ArrayList();
        //initSimulationQueue();
        //initSimulationQueueSimple();
        //initSimulationQueueSimpler2();
        initSimulationQueueFromTable(processDataList);
        showProcesses();
    }

    public SystemOS(
    List<ProcessData> processDataList, TextArea simulationOutput, 
    List<ProgressBar> cpuProgressBars, List<ProgressBar> ioProgressBars,
    ListView<String> processQueueView, Label clockLabel,
    TableView<MetricData> metricsTable
    ) {

        cpu = new CPU();
        ioq = new IOQueue();
        os = new OS(this, cpu, ioq);
        cpu.setOS(os);
        ioq.setOS(os);
        execution = new ArrayList<>();
        processes = new ArrayList<>();
        this.processDataList = processDataList;
        this.simulationOutput = simulationOutput;
        this.cpuProgressBars = cpuProgressBars;
        this.ioProgressBars = ioProgressBars;
        this.processQueueView = processQueueView;
        this.clockLabel = clockLabel;
        this.metricsTable = metricsTable;


        metricDataList = FXCollections.observableArrayList(
        new MetricData("Utilización de CPU", "0.0"),
        new MetricData("Throughput", "0.0"),
        new MetricData("Turnaround Time Promedio", "0.0"),
        new MetricData("Tiempo de Espera Promedio", "0.0"),
        new MetricData("Cambios de Contexto", "0")
        );
    metricsTable.setItems(metricDataList);

        initSimulationQueueFromTable(processDataList);
        showProcesses();
    }
    
    public int getTime(){
        return clock;
    }
    
    public ArrayList<Process> getProcessAtI(int i){
        ArrayList<Process> ps = new ArrayList();
        
        for (Process process : processes) {
            if(process.getTime_init() == i){
                ps.add(process);
            }
        }
        
        return ps;
    }

    public void initSimulationQueue(){
        double tp;
        Process p;
        for (int i = 0; i < MAX_SIM_PROC_CREATION_TIME; i++) {
            tp = r.nextDouble();
            if(PROB_PROC_CREATION >= tp){
                p = new Process();
                p.setTime_init(clock);
                processes.add(p);
            }
            clock++;
        }
        clock = 0;
    }
    
    public void initSimulationQueueSimple(){
        Process p;
        int cont = 0;
        for (int i = 0; i < MAX_SIM_PROC_CREATION_TIME; i++) {
            if(i % 4 == 0){
                p = new Process(cont++,-1);
                p.setTime_init(clock);
                processes.add(p);
            }
            clock++;
        }
        clock = 0;
    }
    
    public void initSimulationQueueSimpler(){
        
        Process p = new Process(false);
        p.setPriority(0);
        ProcessBurst temp = new ProcessBurst(5,ProcessBurstType.CPU);    
        p.addBurst(temp);
        temp = new ProcessBurst(4,ProcessBurstType.IO);    
        p.addBurst(temp);
        temp = new ProcessBurst(3,ProcessBurstType.CPU);    
        p.addBurst(temp);
        p.setTime_init(0);
        processes.add(p);
        
        
        p = new Process(false);
        p.setPriority(1);
        temp = new ProcessBurst(3,ProcessBurstType.CPU);    
        p.addBurst(temp);
        temp = new ProcessBurst(5,ProcessBurstType.IO);    
        p.addBurst(temp);
        temp = new ProcessBurst(6,ProcessBurstType.CPU);    
        p.addBurst(temp);
        p.setTime_init(2);
        processes.add(p);
        
        p = new Process(false);
        p.setPriority(2);
        temp = new ProcessBurst(7,ProcessBurstType.CPU);    
        p.addBurst(temp);
        temp = new ProcessBurst(3,ProcessBurstType.IO);    
        p.addBurst(temp);
        temp = new ProcessBurst(5,ProcessBurstType.CPU);    
        p.addBurst(temp);
        p.setTime_init(6);
        processes.add(p);
        
        p = new Process(false);
        p.setPriority(3);
        temp = new ProcessBurst(4,ProcessBurstType.CPU);    
        p.addBurst(temp);
        temp = new ProcessBurst(3,ProcessBurstType.IO);    
        p.addBurst(temp);
        temp = new ProcessBurst(7,ProcessBurstType.CPU);    
        p.addBurst(temp);
        p.setTime_init(8);
        processes.add(p);
        
        clock = 0;
    }
    
    public void initSimulationQueueSimpler2(){
        
        Process p = new Process(false);
        p.setPriority(0);
        ProcessBurst temp = new ProcessBurst(15,ProcessBurstType.CPU);    
        p.addBurst(temp);
        temp = new ProcessBurst(12,ProcessBurstType.IO);    
        p.addBurst(temp);
        temp = new ProcessBurst(21,ProcessBurstType.CPU);    
        p.addBurst(temp);
        p.setTime_init(0);
        p.setPid(0);
        processes.add(p);
        
        
        p = new Process(false);
        p.setPriority(0);
        temp = new ProcessBurst(8,ProcessBurstType.CPU);    
        p.addBurst(temp);
        temp = new ProcessBurst(4,ProcessBurstType.IO);    
        p.addBurst(temp);
        temp = new ProcessBurst(16,ProcessBurstType.CPU);    
        p.addBurst(temp);
        p.setTime_init(2);
        p.setPid(1);
        processes.add(p);
        
        p = new Process(false);
        p.setPriority(1);
        temp = new ProcessBurst(10,ProcessBurstType.CPU);    
        p.addBurst(temp);
        temp = new ProcessBurst(5,ProcessBurstType.IO);    
        p.addBurst(temp);
        temp = new ProcessBurst(12,ProcessBurstType.CPU);    
        p.addBurst(temp);
        p.setTime_init(6);
        p.setPid(2);
        processes.add(p);
        
        p = new Process(false);
        p.setPriority(1);
        temp = new ProcessBurst(9,ProcessBurstType.CPU);    
        p.addBurst(temp);
        temp = new ProcessBurst(6,ProcessBurstType.IO);    
        p.addBurst(temp);
        temp = new ProcessBurst(17,ProcessBurstType.CPU);    
        p.addBurst(temp);
        p.setTime_init(8);
        p.setPid(3);
        processes.add(p);
        
        clock = 0;
    }

    public void initSimulationQueueFromTable(List<ProcessData> processDataList) {
    
        // Ordenar por tiempo de llegada para el orden de procesamiento
        processDataList.sort(Comparator.comparingInt(ProcessData::getTimeOfArrival));
    
        for (ProcessData data : processDataList) {
            // Crear el proceso utilizando el `pid` y `timeOfArrival` originales de `ProcessData`
            Process p = new Process(false);
            p.setPid(data.getPid());  // Asigna el `pid` directamente desde `ProcessData`
            p.setTime_init(data.getTimeOfArrival());  // Establece el tiempo de llegada
            
            // Establece la prioridad de manera independiente para no afectar el `pid`
            p.setPriority(data.getPid());  // Esto puede ajustarse según la lógica de prioridad deseada
    
            // Agregar los bursts de CPU e I/O
            List<Integer> cpuTimes = data.getCpuTimes();
            for (int i = 0; i < cpuTimes.size(); i++) {
                int cpuTime = cpuTimes.get(i);
                if (cpuTime > 0) {  // Añadir burst de CPU solo si el tiempo es mayor que 0
                    p.addBurst(new ProcessBurst(cpuTime, ProcessBurstType.CPU));
                }
    
                // Agregar el burst de I/O después de cada CPU burst excepto el último
                if (i < cpuTimes.size() - 1 && data.getIoTime() > 0) {
                    p.addBurst(new ProcessBurst(data.getIoTime(), ProcessBurstType.IO));
                }
            }
    
            // Agregar el proceso a la lista de procesos sin importar el `time_init`
            processes.add(p);
        }

        clock = 0;
    }
    
    public boolean isSimulationFinished() {
        for (Process p : processes) {
            if (!p.isFinished()) {
                return false; // Devuelve false en cuanto encuentra un proceso no terminado
            }
        }
        return true; // Si todos los procesos están terminados, devuelve true
    }
    
    
    @Override
    public void run() {
        
        int i = 0;
        while (!isSimulationFinished() && i < MAX_SIM_CYCLES) {
            ArrayList<Process> ps = getProcessAtI(i);
            for (Process p : ps) {
                os.create_process(p);
            }
    
            os.update();
            clock++;
    
            Process temp_exec = cpu.getProcess();
            int tempID = (temp_exec == null) ? -1 : temp_exec.getPid();
            execution.add(tempID);
            cpu.update();
            ioq.update();
    
            // Actualizar la interfaz gráfica sin imprimir en la consola
            int finalI = i;
            Platform.runLater(() -> {
                clockLabel.setText("Ciclo de reloj: " + clock);
                updateProgressBars();
                updateProcessQueueView(processQueueView);
                metricDataList.get(0).setMetricValue(String.format("%.2f", calcCPUUtilization()));
                metricDataList.get(1).setMetricValue(String.format("%.2f", calcThroughput()));
                metricDataList.get(2).setMetricValue(String.format("%.2f", calcTurnaroundTime()));
                metricDataList.get(3).setMetricValue(String.format("%.2f", calcAvgWaitingTime()));
                metricDataList.get(4).setMetricValue(String.format("%.2f", calcAvgContextSwitches()));
            });
    
            i++;
            try {
                Thread.sleep(WAITINGTIME); // Simulación lenta para observar cambios
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        // Mostrar solo la salida final en la interfaz
        Platform.runLater(this::finalizeSimulation);
        
        // Imprimir los resultados finales
        System.out.println("******SIMULATION FINISHES******");
        System.out.println("******Process Execution******");
        for (Integer num : execution) {
            System.out.print(num + " ");
        }
        System.out.println("");
    
        System.out.println("******Performance Indicators******");
        System.out.println("Total execution cycles: " + clock);
        System.out.println("CPU Utilization: " + this.calcCPUUtilization());
        System.out.println("Throughput: " + this.calcThroughput());
        System.out.println("Average Turnaround Time: " + this.calcTurnaroundTime());
        System.out.println("Average Waiting Time: " + this.calcAvgWaitingTime());
        System.out.println("Average Context Switches: " + this.calcAvgContextSwitches());
        System.out.println(execution.size());
    }


    private void updateSimulationOutput(int cycle) {
        StringBuilder sb = new StringBuilder();
        sb.append("******Clock: ").append(cycle).append("******\n");
        sb.append("CPU: ").append(cpu).append("\n");
        sb.append("IO: ").append(ioq).append("\n");
        
        simulationOutput.appendText(sb.toString());
    }


    private void updateProgressBars() {
        Platform.runLater(() -> {
            for (int i = 0; i < processes.size(); i++) {
                Process process = processes.get(i);
                ProgressBar cpuProgressBar = cpuProgressBars.get(i);
                ProgressBar ioProgressBar = ioProgressBars.get(i);
    
                // Obtener el progreso de CPU e I/O
                double cpuProgress = process.getCpuProgress();
                double ioProgress = process.getIoProgress();
    
                // Actualizar progreso de las barras
                cpuProgressBar.setProgress(cpuProgress);
                ioProgressBar.setProgress(ioProgress);
    
                // Remover todas las clases de estilo anteriores para evitar conflictos
                cpuProgressBar.getStyleClass().removeAll("progress-bar-running", "progress-bar-stopped", "progress-bar-finished");
                ioProgressBar.getStyleClass().removeAll("progress-bar-running", "progress-bar-stopped", "progress-bar-finished");
    
                // Asignar clases CSS según el estado del proceso
                if (process.isFinished()) {
                    // Verde claro si el proceso está finalizado
                    cpuProgressBar.getStyleClass().add("progress-bar-finished");
                    ioProgressBar.getStyleClass().add("progress-bar-finished");
                } else if (process.isCpuRunning()) {
                    // Azul claro si el proceso está corriendo en CPU
                    cpuProgressBar.getStyleClass().add("progress-bar-running");
                } else if (process.isIoRunning()) {
                    // Azul claro si el proceso está corriendo en I/O
                    ioProgressBar.getStyleClass().add("progress-bar-running");
                } else {
                    // Rojo claro si el proceso está en espera o detenido
                    cpuProgressBar.getStyleClass().add("progress-bar-stopped");
                    ioProgressBar.getStyleClass().add("progress-bar-stopped");
                }
    
                // Actualizar el texto de la fracción de CPU dentro del StackPane
                StackPane cpuStack = (StackPane) cpuProgressBar.getParent();
                Label cpuFractionLabel = (Label) cpuStack.getChildren().get(1); // La etiqueta está en el índice 1
                cpuFractionLabel.setText(String.format("%d/%d", process.getExecutedCpuCycles(), process.getTotalCpuCycles()));
    
                // Actualizar el texto de la fracción de I/O dentro del StackPane
                StackPane ioStack = (StackPane) ioProgressBar.getParent();
                Label ioFractionLabel = (Label) ioStack.getChildren().get(1); // La etiqueta está en el índice 1
                ioFractionLabel.setText(String.format("%d/%d", process.getExecutedIoCycles(), process.getTotalIoCycles()));
            }
        });
    }

    private void updateProcessQueueView(ListView<String> processQueueView) {
        ObservableList<String> processQueueItems = FXCollections.observableArrayList();

        for (Process p : os.getQueue()) { // Asegúrate de tener un método getQueue en OS para obtener la cola de procesos
            processQueueItems.add("PID: " + p.getPid() + " (Estado: " + p.getState() + ")");
        }

        processQueueView.setItems(processQueueItems);
    }
    
    private void finalizeSimulation() {
        simulationOutput.appendText("******SIMULATION FINISHES******\n");
    
        // Mostrar el orden de ejecución de procesos con salto de línea
        simulationOutput.appendText("******Process Execution******\n");
        int count = 0;
        for (Integer num : execution) {
            simulationOutput.appendText(num + " ");
            count++;
            if (count % 20 == 0) { // Salta de línea después de 20 procesos
                simulationOutput.appendText("\n");
            }
        }
        simulationOutput.appendText("\n\n");
    }



    
    public void showProcesses(){
        System.out.println("Process list:");
        StringBuilder sb = new StringBuilder();
        
        for (Process process : processes) {
            sb.append(process);
            sb.append("\n");
        }
        
        System.out.println(sb.toString());
    }
    
    
    public double calcCPUUtilization()
    {       
        int cont=0;
        for (Integer num : execution) 
        {
            if(num == -1)
                cont++;
        }

        return (execution.size()-cont)/(double)execution.size();
    }
    
    public double calcTurnaroundTime() {
        double totalTurnaroundTime = 0;
    
        for (Process p : processes) {
            int turnaroundTime = p.getTime_finished() - p.getTime_init();
            if (turnaroundTime < 0) turnaroundTime = 0; // Ajuste para evitar valores negativos
            totalTurnaroundTime += turnaroundTime;
        }
    
        return totalTurnaroundTime / processes.size();
    }
    
    public double calcThroughput()
    {
        return (double)processes.size()/execution.size();
    }
    
    public double calcAvgWaitingTime() {
        double totalWaitingTime = 0;
    
        for (Process p : processes) {
            int turnaroundTime = p.getTime_finished() - p.getTime_init();
            int waitingTime = turnaroundTime - p.getTotalExecutionTime();
            if (waitingTime < 0) waitingTime = 0; // Ajuste para evitar valores negativos
            totalWaitingTime += waitingTime;
        }
    
        return totalWaitingTime / processes.size();
    }
    
    public double calcAvgContextSwitches()
    {
        
        int cont = 1;
        int prev = execution.get(0);
        for (Integer i : execution) {
            if(prev != i){
                cont++;
                prev = i;
            }
        }
        
        return cont / (double)processes.size();
    }
    
    
}
