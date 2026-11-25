import java.util.ArrayList;

public class LogsReport {

   public static void printReport(ArrayList<logs> logs) {
    System.out.println("\n===== Scheduling Log =====");
    System.out.printf("%-6s %-9s %-55s %-55s %s%n",
            "Time", "Running", "Tier 1 Queue", "Tier 2 Queue", "Note");

    String previousProcess = "";
    for (logs log : logs) {
        String shown = log.runningProcess;
        if (shown.equals(previousProcess)) {
            shown = "";
        } else if (!shown.isEmpty()) {
            previousProcess = shown;
        }

        System.out.printf("%-6d %-9s %-55s %-55s %s%n",
                log.time, shown, log.tier1, log.tier2, log.note);
    }
}
}