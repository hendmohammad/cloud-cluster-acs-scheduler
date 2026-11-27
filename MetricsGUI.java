import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;

public class MetricsGUI extends VBox {

    private ObservableList<Process> processes;  // to store the processes
    private int[][] results;    // to store the metrics for each process
    private double[] averages;  // to store average numbers

    // components to display
    private TableView<Process> metricsTable;

    private Label avgTATLabel;
    private Label avgWTLabel;
    private Label avgRTLabel;

    public MetricsGUI() {

        processes = FXCollections.observableArrayList();

        avgWTLabel = new Label("0.00");
        avgTATLabel = new Label("0.00");
        avgRTLabel = new Label("0.00");

        displayAvgBoxes();
        displayMetricsTable();
   
        setSpacing(15); // space between averages and table
        setPadding(new Insets(15)); // space between border and content

    
        setStyle( // add a rounded border around metrics section
            "-fx-border-color: black;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );
    }

    public void calc(Process[] processes){

        this.processes.setAll(processes);
        results = new int[processes.length][5];
        averages = new double[3];

        for(int i=0; i < processes.length; i++){

            Process p = processes[i];

            results[i][0] = p.arrival_time;
            results[i][1] = p.finish_time;
            results[i][2] = Metrics.turnaroundTime(p);
            results[i][3] = Metrics.waitingTime(p);
            results[i][4] = Metrics.responseTime(p);
        }

        averages[0] = Metrics.averageTurnaroundTime(processes);
        averages[1] = Metrics.averageWaitingTime(processes);
        averages[2] = Metrics.averageResponseTime(processes);

        avgWTLabel.setText(String.format("%.2f", averages[1]));
        avgTATLabel.setText(String.format("%.2f", averages[0]));
        avgRTLabel.setText(String.format("%.2f", averages[2]));

    }

    public void displayAvgBoxes(){

        VBox waitingBox = new VBox(10);
        VBox turnaroundBox = new VBox(10);
        VBox responseBox = new VBox(10);

        Label waitingTitle = new Label("Avg Waiting");
        Label turnaroundTitle = new Label("Avg Turnaround");
        Label responseTitle = new Label("Avg Response");

        waitingBox.getChildren().addAll(waitingTitle, avgWTLabel);
        turnaroundBox.getChildren().addAll(turnaroundTitle, avgTATLabel);
        responseBox.getChildren().addAll(responseTitle, avgRTLabel);

        avgWTLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        avgTATLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        avgRTLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox averageBoxes = new HBox(10);

        // make all three boxes share the available width equally
        HBox.setHgrow(waitingBox, Priority.ALWAYS);
        HBox.setHgrow(turnaroundBox, Priority.ALWAYS);
        HBox.setHgrow(responseBox, Priority.ALWAYS);

        waitingBox.setMaxWidth(Double.MAX_VALUE);
        turnaroundBox.setMaxWidth(Double.MAX_VALUE);
        responseBox.setMaxWidth(Double.MAX_VALUE);

        
        String boxStyle =  // add a border and padding to each average box
            "-fx-border-color: black;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;";

        waitingBox.setStyle(boxStyle);
        turnaroundBox.setStyle(boxStyle);
        responseBox.setStyle(boxStyle);

        waitingBox.setPadding(new Insets(10));
        turnaroundBox.setPadding(new Insets(10));
        responseBox.setPadding(new Insets(10));

        averageBoxes.getChildren().addAll(waitingBox, turnaroundBox, responseBox);

        // allow average boxes to use the full available width
        averageBoxes.setMaxWidth(Double.MAX_VALUE);

        getChildren().add(averageBoxes);
    }

    public void displayMetricsTable(){

        metricsTable = new TableView<>();

        TableColumn<Process, String> processColumn = new TableColumn<>("Process");
        TableColumn<Process, Integer> arrivalColumn = new TableColumn<>("Arrival");
        TableColumn<Process, Integer> finishColumn = new TableColumn<>("Finish");
        TableColumn<Process, Integer> tatColumn = new TableColumn<>("TAT");
        TableColumn<Process, Integer> wtColumn = new TableColumn<>("WT");
        TableColumn<Process, Integer> rtColumn = new TableColumn<>("RT");

        processColumn.setCellValueFactory(data ->
            new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().name));

        arrivalColumn.setCellValueFactory(data ->
            new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().arrival_time));

        finishColumn.setCellValueFactory(data ->
            new javafx.beans.property.ReadOnlyObjectWrapper<>(data.getValue().finish_time));

        tatColumn.setCellValueFactory(data ->
            new javafx.beans.property.ReadOnlyObjectWrapper<>(Metrics.turnaroundTime(data.getValue())));

        wtColumn.setCellValueFactory(data ->
            new javafx.beans.property.ReadOnlyObjectWrapper<>(Metrics.waitingTime(data.getValue())));

        rtColumn.setCellValueFactory(data ->
            new javafx.beans.property.ReadOnlyObjectWrapper<>(Metrics.responseTime(data.getValue())));

        metricsTable.getColumns().addAll(processColumn, arrivalColumn, finishColumn, tatColumn, wtColumn, rtColumn);

        metricsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        metricsTable.setItems(processes);

        // set a minimum height so the table remains usable
        metricsTable.setMinHeight(100);

        // allow the table to occupy the remaining vertical space
        VBox.setVgrow(metricsTable, Priority.ALWAYS);
        
        getChildren().add(metricsTable);
    }

    public int[][] getResults() {
        return results;
    }

    public double[] getAverages() {
        return averages;
    }

    public ObservableList<Process> getProcesses() {
        return processes;
    }
}