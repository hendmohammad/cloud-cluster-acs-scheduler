import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int p_num = input.nextInt();

        Process[] processes = new Process[p_num];
        for(int i=0; i < p_num; i++){
            System.out.print("Enter the arrival time: ");
            int arrival_time = input.nextInt();
            System.out.print("Enter burst time: ");
            int burst_time = input.nextInt();
            System.out.print("Enter the priority: ");
            int priority = input.nextInt();

            processes[i] = new Process("P"+(i+1), arrival_time, burst_time, priority);
        }

        System.out.print("Enter the time quantum of RoundRobin: ");
        int RR_Q = input.nextInt();

        System.out.print("Enter the context switching: ");
        int CS = input.nextInt();

        Scheduler scheduler = new Scheduler(processes, RR_Q, CS);
        scheduler.Schedule();
        
        Output.printReport(scheduler.tracer.logs, processes);

        input.close();
        
    }
}
