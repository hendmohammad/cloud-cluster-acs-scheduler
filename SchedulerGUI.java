import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import javafx.scene.layout.Priority;


public class SchedulerGUI extends Application {

    @Override
    public void start(Stage stage) {

        // create main components
        InputTableGUI input = new InputTableGUI(); 
        MetricsGUI metrics = new MetricsGUI();
        GanttGUI gantt = new GanttGUI(new ArrayList<>());
        
        // connect the components
        input.setMetricsGUI(metrics);
        input.setGanttGUI(gantt);
        
        VBox rightSection = new VBox(metrics);
        HBox topSection = new HBox(20, input, rightSection);

        VBox.setVgrow(metrics, Priority.ALWAYS); // allow MetricsGUI to grow & fill the available height in rightSection

        // give both sections equal widths
        input.prefWidthProperty().bind(topSection.widthProperty().multiply(0.48));
        rightSection.prefWidthProperty().bind(topSection.widthProperty().multiply(0.48));

        // give both sections same fixed height
        input.setPrefHeight(400);
        input.setMinHeight(400);
        input.setMaxHeight(400);

        rightSection.setPrefHeight(400);
        rightSection.setMinHeight(400);
        rightSection.setMaxHeight(400);

        // make the metrics component fill the right section
        metrics.prefHeightProperty().bind(rightSection.heightProperty());

        // it's the main layout
        VBox root = new VBox(20, topSection, gantt); // 20 for spacing

        root.setPadding(new Insets(20)); // for spacing

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("ACS CPU Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
