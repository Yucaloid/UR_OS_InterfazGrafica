package ur_os;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Johan Caro 
 */

public class UR_OS extends Application {

    private static final String VERSION = "0.0.4.5";
    public static ProcessType simulationType;
    public static boolean firstExecution;
    public static int quantum;
    private TextField quantumField;
    private TableView<ProcessData> processTable;
    private ObservableList<ProcessData> processDataList;
    private ComboBox<Integer> processCountComboBox;
    private ComboBox<Integer> cpuTimeCountComboBox;
    private TextArea simulationOutput;
    private VBox progressBox;
    private List<ProgressBar> cpuProgressBars;
    private List<ProgressBar> ioProgressBars;
    private ListView<String> processQueueView;
    private Label clockLabel;
    private TableView<MetricData> metricsTable;
    private ObservableList<MetricData> metricDataList;

    @Override
    public void start(Stage primaryStage) {

        try {
            FileInputStream input = new FileInputStream("resources/icon.png");
            Image icon = new Image(input);
            primaryStage.getIcons().add(icon);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        firstExecution = true;

        
        clockLabel = new Label("Ciclo de reloj: 0");

        Label processTypeLabel = new Label("Seleccione el tipo de simulación:");
        ComboBox<String> processSelector = new ComboBox<>();
        processSelector.getItems().addAll("FCFS", "SJF P", "SJF NP", "Round Robin", "Multi Queue", "Priority Queue");
        processSelector.setPromptText("Seleccionar proceso");

        Label quantumLabel = new Label("Quantum:");
        quantumField = new TextField();
        quantumLabel.setVisible(false);
        quantumField.setVisible(false);

        processSelector.setOnAction(e -> {
            boolean isRoundRobin = "Round Robin".equals(processSelector.getValue());
            quantumLabel.setVisible(isRoundRobin);
            quantumField.setVisible(isRoundRobin);
        });

        Label processCountLabel = new Label("Número de procesos:");
        processCountComboBox = new ComboBox<>();
        processCountComboBox.setPromptText("Seleccione el número de procesos");
        for (int i = 1; i <= 10; i++) {
            processCountComboBox.getItems().add(i);
        }
        processCountComboBox.setOnAction(e -> updateProcessTable(processCountComboBox.getValue()));

        Label cpuTimeCountLabel = new Label("Número de tiempos de CPU:");
        cpuTimeCountComboBox = new ComboBox<>();
        cpuTimeCountComboBox.setPromptText("Seleccione el número de CPU Times");
        for (int i = 1; i <= 5; i++) {
            cpuTimeCountComboBox.getItems().add(i);
        }
        cpuTimeCountComboBox.setOnAction(e -> updateProcessTableWithCpuTimes(cpuTimeCountComboBox.getValue()));

        processTable = new TableView<>();
        createProcessTable(1);

        // Botones para cargar los casos de prueba y valores aleatorios
        Button randomizeButton = new Button("Inicializar valores aleatorios");
        randomizeButton.setOnAction(e -> initializeRandomValues());

        Button loadTestCase1Button = new Button("Test 1");
        loadTestCase1Button.setOnAction(e -> loadTestCase1());

        Button loadTestCase2Button = new Button("Test 2");
        loadTestCase2Button.setOnAction(e -> loadTestCase2());

        // Colocar los tres botones en una fila
        HBox buttonRow = new HBox(10, randomizeButton, loadTestCase1Button, loadTestCase2Button);

        Button executeButton = new Button("Ejecutar código");
        executeButton.setOnAction(e -> executeSimulation(processSelector));

        primaryStage.setOnCloseRequest(event -> {
            // Cierra correctamente la aplicación y asegura que los procesos se terminen
            System.out.println("Cerrando la aplicación...");
            Platform.exit(); // Cierra correctamente el entorno JavaFX
            System.exit(0);   // Termina el proceso de la JVM
        });

        simulationOutput = new TextArea();
        simulationOutput.setEditable(false);
        simulationOutput.setPrefHeight(100);

        progressBox = new VBox(10);
        cpuProgressBars = new ArrayList<>();
        ioProgressBars = new ArrayList<>();

        // Organizar los controles en el panel izquierdo
        VBox leftPane = new VBox(10, processTypeLabel, processSelector, quantumLabel, 
                quantumField, processCountLabel, processCountComboBox, 
                cpuTimeCountLabel, cpuTimeCountComboBox, 
                processTable, buttonRow, executeButton, 
                clockLabel);

        leftPane.setPadding(new Insets(15));
        leftPane.setPrefWidth(400);

        processQueueView = new ListView<>();
        processQueueView.setPrefHeight(100);
        processQueueView.setPrefWidth(200);
        processQueueView.setPlaceholder(new Label("No hay procesos en la cola"));

        // Crear la tabla de métricas
        metricsTable = new TableView<>();
        metricsTable.setPrefHeight(150); // Ajustar la altura según sea necesario

        // Columna para el nombre de la métrica
        TableColumn<MetricData, String> metricNameColumn = new TableColumn<>("Métrica");
        metricNameColumn.setCellValueFactory(cellData -> cellData.getValue().metricNameProperty());

        // Columna para el valor de la métrica
        TableColumn<MetricData, String> metricValueColumn = new TableColumn<>("Valor");
        metricValueColumn.setCellValueFactory(cellData -> cellData.getValue().metricValueProperty());

        // Agregar columnas a la tabla
        metricsTable.getColumns().add(metricNameColumn);
        metricsTable.getColumns().add(metricValueColumn);

        // Crear datos iniciales para las métricas
        metricDataList = FXCollections.observableArrayList(
            new MetricData("Utilización de CPU", "0.0"),
            new MetricData("Throughput", "0.0"),
            new MetricData("Turnaround Time Promedio", "0.0"),
            new MetricData("Tiempo de Espera Promedio", "0.0"),
            new MetricData("Cambios de Contexto Promedio", "0.0")
        );
        metricsTable.setItems(metricDataList);


        // Organizar el panel derecho para la salida de simulación y las barras de progreso
        VBox rightPane = new VBox(10, simulationOutput, progressBox, processQueueView, metricsTable);
        rightPane.setPadding(new Insets(15));
        rightPane.setPrefWidth(400);

        // Crear el diseño principal
        HBox mainLayout = new HBox(10, leftPane, rightPane);
        mainLayout.setPadding(new Insets(15));
        Scene scene = new Scene(mainLayout, 1000, 600);
        
        try {
            // Primero, intenta cargar el CSS desde la carpeta "resources" en el sistema de archivos.
            File cssFile = new File("resources/styles.css");
            if (cssFile.exists()) {
                String css = cssFile.toURI().toURL().toExternalForm();
                scene.getStylesheets().add(css);
                System.out.println("CSS cargado desde el sistema de archivos: " + css);
            } else {
                // Si no se encuentra en el sistema de archivos, intenta cargarlo desde el classpath (dentro del JAR)
                cssFile = new File("styles.css");
                if (cssFile != null) {
                    String css = cssFile.toURI().toURL().toExternalForm();
                    scene.getStylesheets().add(css);
                    System.out.println("CSS cargado desde el classpath: " + css);
                } 
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        

        

        primaryStage.setTitle("UR_OS Process Management 1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void createProcessTable(int cpuTimes) {
        processTable.getColumns().clear();

        TableColumn<ProcessData, Integer> pidCol = new TableColumn<>("PID");
        pidCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(processTable.getItems().indexOf(cellData.getValue())));
        processTable.getColumns().add(pidCol);

        TableColumn<ProcessData, Integer> arrivalCol = new TableColumn<>("Time of Arrival");
        arrivalCol.setCellValueFactory(cellData -> cellData.getValue().timeOfArrivalProperty().asObject());
        arrivalCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        arrivalCol.setOnEditCommit(e -> e.getRowValue().setTimeOfArrival(e.getNewValue()));
        processTable.getColumns().add(arrivalCol);

        for (int i = 1; i <= cpuTimes; i++) {
            TableColumn<ProcessData, Integer> cpuCol = new TableColumn<>("CPU " + i);
            int finalI = i;
            cpuCol.setCellValueFactory(cellData -> cellData.getValue().getCpuTimeProperty(finalI).asObject());
            cpuCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
            cpuCol.setOnEditCommit(e -> e.getRowValue().setCpuTime(finalI, e.getNewValue()));
            processTable.getColumns().add(cpuCol);
        }

        TableColumn<ProcessData, Integer> ioCol = new TableColumn<>("I/O");
        ioCol.setCellValueFactory(cellData -> cellData.getValue().ioTimeProperty().asObject());
        ioCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        ioCol.setOnEditCommit(e -> e.getRowValue().setIoTime(e.getNewValue()));
        processTable.getColumns().add(ioCol);
        processTable.setEditable(true);

        if (processCountComboBox.getValue() != null) {
            updateProcessTable(processCountComboBox.getValue());
        }
    }

    private void loadTestCase1() {
        // Configuraciones para el caso de prueba 1: 4 procesos, 2 tiempos de CPU
        processCountComboBox.setValue(4);
        cpuTimeCountComboBox.setValue(2);
        updateProcessTable(4); // Actualizar la tabla para reflejar 4 procesos y crear barras de progreso
    
        // Datos del caso de prueba
        processDataList.clear();
    
        ProcessData p1 = new ProcessData(1, 2);
        p1.setTimeOfArrival(0);
        p1.setCpuTime(1, 5);
        p1.setIoTime(4);
        p1.setCpuTime(2, 3);
        processDataList.add(p1);
    
        ProcessData p2 = new ProcessData(2, 2);
        p2.setTimeOfArrival(2);
        p2.setCpuTime(1, 3);
        p2.setIoTime(5);
        p2.setCpuTime(2, 6);
        processDataList.add(p2);
    
        ProcessData p3 = new ProcessData(3, 2);
        p3.setTimeOfArrival(6);
        p3.setCpuTime(1, 7);
        p3.setIoTime(3);
        p3.setCpuTime(2, 5);
        processDataList.add(p3);
    
        ProcessData p4 = new ProcessData(4, 2);
        p4.setTimeOfArrival(8);
        p4.setCpuTime(1, 4);
        p4.setIoTime(3);
        p4.setCpuTime(2, 7);
        processDataList.add(p4);
    
        // Actualizar la tabla y barras de progreso
        processTable.setItems(processDataList);
        processTable.refresh();
        createProgressBars(4);
    }
    
    // Método para cargar el segundo caso de prueba
    private void loadTestCase2() {
        // Configuraciones para el caso de prueba 2: 4 procesos, 2 tiempos de CPU
        processCountComboBox.setValue(4);
        cpuTimeCountComboBox.setValue(2);
        updateProcessTable(4); // Actualizar la tabla para reflejar 4 procesos y crear barras de progreso
    
        // Datos del caso de prueba
        processDataList.clear();
    
        ProcessData p1 = new ProcessData(1, 2);
        p1.setTimeOfArrival(0);
        p1.setCpuTime(1, 15);
        p1.setIoTime(12);
        p1.setCpuTime(2, 21);
        processDataList.add(p1);
    
        ProcessData p2 = new ProcessData(2, 2);
        p2.setTimeOfArrival(2);
        p2.setCpuTime(1, 8);
        p2.setIoTime(4);
        p2.setCpuTime(2, 16);
        processDataList.add(p2);
    
        ProcessData p3 = new ProcessData(3, 2);
        p3.setTimeOfArrival(6);
        p3.setCpuTime(1, 10);
        p3.setIoTime(5);
        p3.setCpuTime(2, 12);
        processDataList.add(p3);
    
        ProcessData p4 = new ProcessData(4, 2);
        p4.setTimeOfArrival(8);
        p4.setCpuTime(1, 9);
        p4.setIoTime(6);
        p4.setCpuTime(2, 17);
        processDataList.add(p4);
    
        // Actualizar la tabla y barras de progreso
        processTable.setItems(processDataList);
        processTable.refresh();
        createProgressBars(4);
    }

    private void updateProcessTable(Integer numProcesses) {
        if (numProcesses == null) return;
        createProgressBars(numProcesses);
    
        int cpuTimes = cpuTimeCountComboBox.getValue() != null ? cpuTimeCountComboBox.getValue() : 1;
        processDataList = FXCollections.observableArrayList();
    
        for (int i = 0; i < numProcesses; i++) {
            processDataList.add(new ProcessData(i + 1, cpuTimes));
        }
        processTable.setItems(processDataList);
    }

    private void createProgressBars(int numProcesses) {
        progressBox.getChildren().clear();
        cpuProgressBars.clear();
        ioProgressBars.clear();
    
        for (int i = 0; i < numProcesses; i++) {
            // Contenedor para el proceso completo en una línea
            HBox processContainer = new HBox(10); // Espaciado de 10 entre elementos
            Label processLabel = new Label("Proceso " + i);
    
            // Contenedor de CPU
            Label cpuLabel = new Label("CPU:");
            ProgressBar cpuProgressBar = new ProgressBar(0);
            cpuProgressBar.setPrefWidth(100); // Ajusta el ancho según sea necesario
    
            // Texto de fracción centrado en la barra de progreso de CPU
            Label cpuFractionLabel = new Label("0/0");
            StackPane cpuStack = new StackPane(cpuProgressBar, cpuFractionLabel);
            cpuFractionLabel.setStyle("-fx-text-fill: black;"); // Cambia el color del texto a negro
    
            // Contenedor de I/O
            Label ioLabel = new Label("I/O:");
            ProgressBar ioProgressBar = new ProgressBar(0);
            ioProgressBar.setPrefWidth(100); // Ajusta el ancho según sea necesario
    
            // Texto de fracción centrado en la barra de progreso de I/O
            Label ioFractionLabel = new Label("0/0");
            StackPane ioStack = new StackPane(ioProgressBar, ioFractionLabel);
            ioFractionLabel.setStyle("-fx-text-fill: black;"); // Cambia el color del texto a negro
    
            // Agregar cada componente al contenedor del proceso en el orden deseado
            processContainer.getChildren().addAll(processLabel, cpuLabel, cpuStack, ioLabel, ioStack);
    
            // Agregar el contenedor del proceso al VBox principal
            progressBox.getChildren().add(processContainer);
    
            // Guardar las barras de progreso en las listas para su actualización posterior
            cpuProgressBars.add(cpuProgressBar);
            ioProgressBars.add(ioProgressBar);
        }
    }
    private void updateProcessTableWithCpuTimes(int cpuTimes) {
        createProcessTable(cpuTimes);
    }

    private void initializeRandomValues() {
        Random random = new Random();
        int previousArrivalTime = 0;

        for (int i = 0; i < processDataList.size(); i++) {
            ProcessData data = processDataList.get(i);

            if (i == 0) {
                data.setTimeOfArrival(0);
                previousArrivalTime = 0;
            } else {
                int timeOfArrival = previousArrivalTime + random.nextInt(3) + 1;
                data.setTimeOfArrival(timeOfArrival);
                previousArrivalTime = timeOfArrival;
            }
            
            data.setIoTime(random.nextInt(10) + 1);
            
            for (int j = 1; j <= cpuTimeCountComboBox.getValue(); j++) {
                data.setCpuTime(j, random.nextInt(10) + 1);
            }
        }
        processTable.refresh();
    }

    private void executeSimulation(ComboBox<String> processSelector) {
        String selectedProcess = processSelector.getValue();
        if (selectedProcess == null) {
            System.out.println("Por favor selecciona un proceso antes de ejecutar.");
            return;
        }

        if ("Round Robin".equals(selectedProcess)) {
            String quantumText = quantumField.getText();
            if (quantumText.isEmpty()) {
                System.out.println("Por favor ingresa un valor de quantum para Round Robin.");
                return;
            }
            try {
                quantum = Integer.parseInt(quantumText);
            } catch (NumberFormatException ex) {
                System.out.println("El quantum debe ser un número entero.");
                return;
            }
        }


        System.out.println("************************************");
        System.out.println("         UR_OS V." + VERSION);
        System.out.println("************************************");
        System.out.println("Proceso seleccionado: " + selectedProcess);
        System.out.println("Datos de los procesos ingresados:");
        processDataList.forEach(System.out::println);

        simulationType = determineSimulationType(selectedProcess);

        SystemOS system = new SystemOS(
        new ArrayList<>(processDataList), 
        simulationOutput, 
        cpuProgressBars, 
        ioProgressBars, 
        processQueueView, 
        clockLabel, 
        metricsTable);

        clockLabel.setText("Ciclo de reloj: 0");
        simulationOutput.clear();
        processQueueView.getItems().clear();

        // Reiniciar lista de métricas a valores iniciales
        for (MetricData metric : metricDataList) {
            metric.setMetricValue("0.0");
        }
        metricsTable.refresh();

        new Thread(system).start();
    }

    private ProcessType determineSimulationType(String selectedProcess) {
        switch (selectedProcess) {
            case "FCFS": return ProcessType.FCFS;
            case "SJF P": return ProcessType.SJF_P;
            case "SJF NP": return ProcessType.SJF_NP;
            case "Round Robin": return ProcessType.RR;
            case "Multi Queue": return ProcessType.MFQ;
            case "Priority Queue": return ProcessType.PQ;
            default: return ProcessType.FCFS;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    
}