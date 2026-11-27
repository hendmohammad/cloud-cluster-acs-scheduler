public class PreemptiveSJF {

    Process[] processes;
    int context_switching;

    public PreemptiveSJF(Process[] processes, int cs) {
        this.processes = processes;
        this.context_switching = cs;
    }

    public void Schedule() {
        int curr_time = 0;          
        int finished_p = 0;        
        Process last_process = null;   

        while (finished_p < processes.length) {

            // 1) find the arrived process with the shortest remaining_time
            Process shortest_process = null;
            for (Process p : processes) {
                if (p.arrival_time <= curr_time && p.remaining_time > 0) {
                    if (shortest_process == null || p.remaining_time < shortest_process.remaining_time) {
                        shortest_process = p;
                    }
                }
            }

            // 2) nothing arrived, CPU waits
            if (shortest_process == null) {
                curr_time++;
                last_process = null;
                continue;
            }

            // 3) adding cs time between diff processes
            if (last_process != null && last_process != shortest_process) {
                curr_time += context_switching;
            }

            // 4) save first response time
            if (shortest_process.first_response_time == -1) {
                shortest_process.first_response_time = curr_time;
            }

            // 5) run for 1 time unit
            curr_time++;
            shortest_process.remaining_time--;

            // 6) finished?
            if (shortest_process.remaining_time == 0) {
                shortest_process.finish_time = curr_time;
                finished_p++;
            }

            last_process = shortest_process;
        }
    }
}

