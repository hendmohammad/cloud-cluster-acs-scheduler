import java.util.ArrayList;

public class Output {

    // for printing LogsReport
    public static void printLogs(ArrayList<SimulationResult.logs> logs) {

        System.out.println("\n===== Scheduling Log =====");
        System.out.printf("%-6s %-9s %-75s %-75s %s%n",
                "Time", "Running", "Tier 1 Queue", "Tier 2 Queue", "Note");

        String previousProcess = "";
        for (SimulationResult.logs log : logs) {
            String shown = log.runningProcess;
            if (shown.equals(previousProcess)) {
                shown = "";
            } else if (!shown.isEmpty()) {
                previousProcess = shown;
            }

            System.out.printf("%-6d %-9s %-75s %-75s %s%n",
                    log.time, shown, log.tier1, log.tier2, log.note);
        }
    }

    // for MetricsReport
    public static void printMetrics(Process[] processes) {

        System.out.println("\n===== Scheduling Metrics =====");

        System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s%n",
                "Process", "Arrival", "Finish", "TAT", "WT", "RT");

        for (Process p : processes) {

            int wt = Metrics.waitingTime(p);
            int tat = Metrics.turnaroundTime(p);
            int rt = Metrics.responseTime(p);

            System.out.printf("%-10s %-10d %-10d %-10d %-10d %-10d%n",
                    p.name, p.arrival_time, p.finish_time, tat, wt, rt);
        }

        System.out.println("-----------------------------------------------");

        System.out.printf("Average Turnaround Time: %.2f%n",
                Metrics.averageTurnaroundTime(processes));

        System.out.printf("Average Waiting Time: %.2f%n",
                Metrics.averageWaitingTime(processes));

        System.out.printf("Average Response Time: %.2f%n",
                Metrics.averageResponseTime(processes));
    }

    // one method to print everything in order
    public static void printReport(ArrayList<SimulationResult.logs> logs, Process[] processes) {
        printLogs(logs);
        printMetrics(processes);
    }
}
