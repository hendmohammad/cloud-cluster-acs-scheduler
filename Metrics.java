public class Metrics {
    public static int turnaroundTime(Process p){
        return p.finish_time - p.arrival_time;
    }

    public static int waitingTime(Process p){
        return turnaroundTime(p)-p.burst_time;
    }
    public static int responseTime(Process p){
        return p.first_response_time-p.arrival_time;
    }

    public static double averageWaitingTime(Process[] processes){ 
        int total=0;
        for(Process p:processes){
            total+=waitingTime(p);
        }
        return (double)  total/processes.length;
    }
   
   public static double averageTurnaroundTime(Process[] processes){
       int total=0;
        for(Process p:processes){
            total+=turnaroundTime(p);
        }
        return (double)  total/processes.length;
   }

   public static double averageResponseTime(Process[] processes) {
    int total = 0;
    for (Process p : processes) {
        total += responseTime(p);
    }
    return (double) total / processes.length;
}


}
