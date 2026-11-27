import java.util.ArrayList;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class GanttGUI extends VBox{

    private int selectedTime = 0; // stores the current time selected by user
    private int finalTime = 0; // stores the last time of Gantt chart

    // navigation buttons
    private Button previousButton;
    private Button nextButton;

    // slider to select simulation time
    private Slider timeSlider;             
    private Label currentInfoLabel;  // displays the selected time and running process beside the slider

    // display queue information
    private Label tier1Info;
    private Label tier2Info;

    // original scheduling logs received from Tracer.java
    private ArrayList<SimulationResult.logs> logs;

    // converted Gantt chart blocks
    private ArrayList<SimulationResult.TimeSlice> timeSlices;

    public GanttGUI(ArrayList<SimulationResult.logs> logs) {

        this.logs = logs;               // save the logs
        timeSlices = new ArrayList<>(); // create an empty list to store the converted blocks to draw
        getTimeSlices();
        calculateFinalTime();
        displayGantt();
    }

    // method to convert scheduling logs into Gantt blocks
    public void getTimeSlices(){
        for(SimulationResult.logs log : logs){

            String processName = log.runningProcess;
            int startTime = log.time;
            int endTime = startTime + 1; // bec. each log represents one time unit

            if (log.note.startsWith("Context switch")) processName = "CS";
            
            else if (processName.equals("IDLE")) {
                processName = "IDLE";
                String[] parts = log.note.split("="); // extract ending time from the note
                endTime = Integer.parseInt(parts[1]);
            }
            else if (processName.isEmpty()) continue; // ignore logs that don't represent a running process
            
            addTimeSlice(startTime, endTime, processName);
           
        }
    }

    // method to add a timeSlice or merges it with previous one
    public void addTimeSlice(int startTime, int endTime, String processName){

        // check if there is a previous block
        if (!timeSlices.isEmpty()) {

            SimulationResult.TimeSlice last = timeSlices.get(timeSlices.size() - 1);

            // merge consecutive blocks of the same process
            if (last.processName.equals(processName) && last.endTime == startTime) {
                last.endTime = endTime;
                return; // no need to create another block
            }
        }

        // create a new block
        timeSlices.add(new SimulationResult.TimeSlice(startTime, endTime, processName));
    }
    
    // method to creates and style one Gantt chart block
    private Label createBlock(SimulationResult.TimeSlice slice){

        Label block = new Label(slice.processName);     // name the block
        int duration = slice.endTime - slice.startTime; // calc duration of this block

        // -- Styling of blocks --

        // set the block width according to its duration
        block.setPrefWidth(duration * 60);
        block.setMinWidth(duration * 60);
        block.setMaxWidth(duration * 60);
        
        block.setAlignment(Pos.CENTER); // center the name

        // Add some styling
        if (slice.processName.equals("CS")) {
            block.setStyle(
                "-fx-background-color: lightgray;" +
                "-fx-border-color: black;" +
                "-fx-padding: 10;"
            );
        }
        else if (slice.processName.equals("IDLE")) {
            block.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-padding: 10;"
            );
        }
        else {
            block.setStyle(
                "-fx-background-color: lightblue;" +
                "-fx-border-color: black;" +
                "-fx-padding: 10;"
            );
        }

        return block;
    }

    // method to build and display the complete gantt interface 
    private void displayGantt() {

        HBox ganttRow = new HBox(); // gantt chart blocks be arranged horizontally 
        HBox timelineRow = new HBox(); // timeline underneath the blocks

        for (SimulationResult.TimeSlice slice : timeSlices) {

            // calculations of current time slice
            int duration = slice.endTime - slice.startTime;
            int width = duration * 60;

            // create its Gantt block
            Label block = createBlock(slice);
            ganttRow.getChildren().add(block);

            // add its starting time underneath (show 0 when there's chart, else hide it)
            String timeText = String.valueOf(slice.startTime);
            Label time = new Label(timeText);

            time.setMinWidth(width);
            time.setPrefWidth(width);
            time.setAlignment(Pos.CENTER_LEFT);

            timelineRow.getChildren().add(time);
        }

        // add the final ending time
        Label endTime = new Label(String.valueOf(finalTime));
        timelineRow.getChildren().add(endTime);

        // create label with information displayed beside the slider
        currentInfoLabel = new Label();

        // create the navigation components
        previousButton = new Button("←"); 
        nextButton = new Button("→");
        timeSlider = new Slider(0, finalTime, selectedTime);

        // their interaction logic
        previousButton.setOnAction(event -> {
            if (selectedTime > 0) {
                selectedTime--;
                updateSnapshot();
            }
        });

        nextButton.setOnAction(event -> {
            if (selectedTime < finalTime) {
                selectedTime++;
                updateSnapshot();
            }
        });

        // slider -> update selected time when moved
        timeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedTime = (int) Math.round(newValue.doubleValue());
            updateSnapshot();
        });


        // their styling

        // buttons -> make the arrows bold
        previousButton.setStyle("-fx-font-weight: bold;");
        nextButton.setStyle("-fx-font-weight: bold;");

        // slider -> no need for numbers or tick marks on the slider
        timeSlider.setShowTickLabels(false);
        timeSlider.setShowTickMarks(false);

        timeSlider.setMinWidth(Math.max(finalTime * 60, 1000));
        timeSlider.setPrefWidth(Math.max(finalTime * 60, 1000));
        timeSlider.setMaxWidth(Math.max(finalTime * 60, 1000));
        timeSlider.setBlockIncrement(1);
        timeSlider.setMajorTickUnit(1);
        timeSlider.setSnapToTicks(true);

        // arranging the components

        HBox navigation = new HBox(12);
        navigation.getChildren().addAll(previousButton, timeSlider, nextButton, currentInfoLabel);
        navigation.setAlignment(Pos.CENTER_LEFT);
        
        VBox ganttBox = new VBox(10); // one box for Gantt chart, timeline
        ganttBox.getChildren().addAll(ganttRow, timelineRow, navigation);

        ganttBox.setPadding(new Insets(15)); // add space between border and content

        // draw rounded border 
        ganttBox.setStyle(
            "-fx-border-color: black;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );

        ganttBox.setMaxWidth(Double.MAX_VALUE); // allow Gantt box to use the available width

        // Tier 1 information box
        VBox tier1Box = new VBox(10);
        Label tier1Title = new Label("Tier 1: round robin");

        tier1Info = new Label();
        tier1Info.setWrapText(true);
        tier1Info.setMaxWidth(Double.MAX_VALUE);

        tier1Box.getChildren().addAll(tier1Title, tier1Info);
        tier1Box.setPadding(new Insets(10));
        tier1Box.setStyle( "-fx-border-color: black;" + "-fx-border-radius: 12;" + "-fx-background-radius: 12;"); 

        // Tier 2 information box
        VBox tier2Box = new VBox(10);
        Label tier2Title = new Label("Tier 2: priority with aging");

        tier2Info = new Label(); // label contains real queue info
        tier2Info.setWrapText(true);  // allow queue text to move to another line
        tier2Info.setMaxWidth(Double.MAX_VALUE); // allow info label to use the available width

        tier2Box.getChildren().addAll(tier2Title, tier2Info);
        tier2Box.setPadding(new Insets(10));  // add space between content and box border
        tier2Box.setStyle( "-fx-border-color: black;" + "-fx-border-radius: 12;" + "-fx-background-radius: 12;"); // draw rounded border

        // put both queue boxes beside each other
        HBox queues = new HBox(15);
        queues.getChildren().addAll(tier1Box, tier2Box);

        // give both boxes the same width, based on the GanttGUI width
        tier1Box.prefWidthProperty().bind(this.widthProperty().multiply(0.47));
        tier1Box.minWidthProperty().bind(this.widthProperty().multiply(0.47));
        tier1Box.maxWidthProperty().bind(this.widthProperty().multiply(0.47));

        tier2Box.prefWidthProperty().bind(this.widthProperty().multiply(0.47));
        tier2Box.minWidthProperty().bind(this.widthProperty().multiply(0.47));
        tier2Box.maxWidthProperty().bind(this.widthProperty().multiply(0.47));

        // add everything to GanttGUI
        this.getChildren().clear(); // remove old displayed blocks
        this.setSpacing(15);
        this.getChildren().addAll(ganttBox, queues);
        updateSnapshot();
}

    // method to update the Gantt chart when a new scheduling simulation is executed
    public void updateGantt(ArrayList<SimulationResult.logs> logs) {

        this.logs = logs;   // replace the old logs with the new simulation logs
        timeSlices.clear(); // clear old data
        getTimeSlices();
        calculateFinalTime();
        selectedTime = 0;  // start navigation again from time zero
        displayGantt();    // display the new chart
    }

    private void calculateFinalTime() {
        finalTime = 0;
        for(SimulationResult.TimeSlice slice : timeSlices){ // check the ending time of every Gantt block
            if (slice.endTime > finalTime) finalTime = slice.endTime; // keep the largest ending time
        }
    }

    // method to refresh the information displayed for queues when the selected time changes
    private void updateSnapshot() {

        timeSlider.setValue(selectedTime);  // update slider to match the selected time

        // show the selected time
        String info = "t=" + selectedTime;

        if(!timeSlices.isEmpty() && selectedTime < finalTime) {

            String running = getRunningProcessAtTime(selectedTime);

            if (running.equals("CS")) {
                info += ", CS time";
            }
            else if (!running.equals("IDLE")) {
                info += ", runs " + running;
            }
            else {
                info += ", CPU idle";
            }
        }
        currentInfoLabel.setText(info); // display the info label beside the slider
        
        tier1Info.setText(getQueueStateAtTime(selectedTime, true));  // get and display tier1 queue state at that time
        tier2Info.setText(getQueueStateAtTime(selectedTime, false)); // get and display tier2 queue state at that time
        previousButton.setDisable(selectedTime == 0);  // disable previous button when time zero is reached
        nextButton.setDisable(selectedTime == finalTime);  // disable next button when final time is reached
    }

    // method to find the recent recorded queue state at/before the selected time
    private String getQueueStateAtTime(int time, boolean tier1) {

        String state = "[No snapshot recorded]";

        // search through the scheduling logs in chronological order
        for (SimulationResult.logs log : logs) {

            if (log.time > time) break; // Stop searching when the log belongs to a future time
    
            String currentState;

            if (tier1) currentState = log.tier1;  // if tier1 is true, get the Tier 1 queue snapshot
            else currentState = log.tier2;        // if not, get Tier2 queue
        
            // if this log contains info, save it
            if (currentState != null && !currentState.isEmpty()) {
                state = currentState;
            }
        }

        return state;
    }

    // method to get the displayed running process
    private String getRunningProcessAtTime(int time) {

        for (SimulationResult.TimeSlice slice : timeSlices) {
            if (time >= slice.startTime && time < slice.endTime) { // check if this time belongs to this block
                return slice.processName;
            }
        }

    return "Finished";
    }
}
