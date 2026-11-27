import javafx.util.converter.IntegerStringConverter;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.Priority;


public class InputTableGUI extends VBox {

    // create a process "input" table & a list to store processes
    private TableView<Process> inputTable;
    private ObservableList<Process> processes;

    private TextField QField;
    private TextField CSField;

    private ComboBox<String> algorithmBox;

    private Button addButton;
    private Button runButton;

    // for connecting the processes with its metrics & gantt chart
    private MetricsGUI metricsGUI;
    private GanttGUI ganttGUI;

    public InputTableGUI() {

        inputTable = new TableView<>();

        // create columns
        TableColumn<Process, String> processColumn = new TableColumn<>("Process");
        TableColumn<Process, Integer> arrivalColumn = new TableColumn<>("Arrival Time");
        TableColumn<Process, Integer> burstColumn = new TableColumn<>("Burst Time");
        TableColumn<Process, Integer> prioColumn = new TableColumn<>("Initial Priority");

        // connect columns to process.java class getters
        processColumn.setCellValueFactory(
            new PropertyValueFactory<>("name"));

        arrivalColumn.setCellValueFactory(
            new PropertyValueFactory<>("arrival"));

        burstColumn.setCellValueFactory(
            new PropertyValueFactory<>("burst"));

        prioColumn.setCellValueFactory(
            new PropertyValueFactory<>("prio"));

        // enable editing the table
        inputTable.setEditable(true);

        // make each column editable
        processColumn.setCellFactory( // Name column
            TextFieldTableCell.forTableColumn());

        processColumn.setOnEditCommit(event ->
            event.getRowValue().setName(event.getNewValue()));
        
        arrivalColumn.setCellFactory( // Arrival column
            TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        arrivalColumn.setOnEditCommit(event ->
            event.getRowValue().setArrival(event.getNewValue()));

        burstColumn.setCellFactory( // Burst column
            TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        burstColumn.setOnEditCommit(event -> {
            event.getRowValue().setBurst(event.getNewValue());
            event.getRowValue().remaining_time = event.getNewValue();
        });

        prioColumn.setCellFactory( // Priority column
            TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        prioColumn.setOnEditCommit(event ->
            event.getRowValue().setPrio(event.getNewValue()));

        // add columns
        inputTable.getColumns().addAll(processColumn, arrivalColumn, burstColumn, prioColumn);

        // store processes in the list
        processes = FXCollections.observableArrayList();
        processes.add(new Process("P1", 0, 1, 1));
        processes.add(new Process("P2", 1, 1, 1));
        processes.add(new Process("P3", 4, 1, 1));
        inputTable.setItems(processes); //connect the list to the table

        // numerical input fields
        Label QLabel = new Label("RR Quantum Time");
        QField = new TextField();

        Label CSLabel = new Label("Context Switching");
        CSField = new TextField();

        VBox QInput = new VBox(5);
        QInput.getChildren().addAll(QLabel, QField);

        VBox CSInput = new VBox(5);
        CSInput.getChildren().addAll(CSLabel, CSField);

        QField.setMaxWidth(Double.MAX_VALUE);
        CSField.setMaxWidth(Double.MAX_VALUE);
       
        // Scheduling algorithm dropdown
        algorithmBox = new ComboBox<>();
        algorithmBox.getItems().add("ACS");
        algorithmBox.setValue("ACS");
        // algorithmBox.getItems().add("PRE-SJF");
        // algorithmBox.setValue("PRE-SJF");

        VBox algorithmInput = new VBox(5);
        algorithmInput.getChildren().addAll(algorithmBox);

        // buttons
        addButton = new Button("+ Add Process");
        runButton = new Button("Run Scheduler");

        addButton.setOnAction(event -> {
            int num = processes.size() + 1;
            Process p = new Process("P" + num, 0, 1, 1);
            processes.add(p);
            //inputTable.setPrefHeight(35 * processes.size() + 30);
        });

        runButton.setOnAction(event -> {
            System.out.println("Run button clicked");

                if (algorithmBox.getValue().equals("ACS")){

                    // convert the table's ObservableList into a Process array
                    Process[] processArray = new Process[processes.size()];

                    for(int i=0; i < processes.size(); i++) {
                        Process p = processes.get(i);
                        processArray[i] = new Process(p.getName(), p.getArrival(), p.getBurst(), p.getPrio());
                    }

                    // get the input values
                    int quantum = getQuantumTime();
                    int cs = getContextSwitching();

                    // run ACS
                    Scheduler scheduler = new Scheduler(processArray, quantum, cs);
                    scheduler.Schedule();

                    metricsGUI.calc(processArray); // update metrics
                    ganttGUI.updateGantt(scheduler.tracer.logs); // update Gantt chart
                }else{}
        });

        // arrange buttons horizontally
        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(addButton, runButton);

        // arrange others vertically
        setSpacing(15);
        setPadding(new Insets(15));

        getChildren().addAll(inputTable, QInput, CSInput, algorithmInput, buttons);

        // resized table
        inputTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        inputTable.setFixedCellSize(30);
        inputTable.setMinHeight(0);
        inputTable.setPrefHeight(130);
        // set the columns width
        processColumn.setPrefWidth(120);
        arrivalColumn.setPrefWidth(150);
        burstColumn.setPrefWidth(150);
        prioColumn.setPrefWidth(150);
        // align them to the center
        processColumn.setStyle("-fx-alignment: CENTER;");
        arrivalColumn.setStyle("-fx-alignment: CENTER;");
        burstColumn.setStyle("-fx-alignment: CENTER;");
        prioColumn.setStyle("-fx-alignment: CENTER;");

        // allow the table to use the available vertical space
        VBox.setVgrow(inputTable, Priority.ALWAYS);

        setStyle(  // add rounded border
            "-fx-border-color: black;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );
        setPadding(new Insets(15)); // space between border and component
        setSpacing(15);  // space between components
    }

    // Accessors for scheduler connection later
    // Allow the main GUI to access the table's processes
    public ObservableList<Process> getProcesses() {
        return processes;
    }

    public int getQuantumTime() {
        return Integer.parseInt(QField.getText());
    }

    public int getContextSwitching() {
        return Integer.parseInt(CSField.getText());
    }

    public void setMetricsGUI(MetricsGUI metricsGUI) {
        this.metricsGUI = metricsGUI;
    }

    public void setGanttGUI(GanttGUI ganttGUI) {
        this.ganttGUI = ganttGUI;
    }
}
