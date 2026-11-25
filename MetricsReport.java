public class MetricsReport {

    public static void printReport(Process[] processes) {

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
}
