import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Arrays; // for processes array sorting 

public class Scheduler {

    Process[] processes;
    int quantum_time;
    int context_switching;

    Queue<Process> RRQueue = new LinkedList<>();
    PriorityQueue<Process> PrQueue = new PriorityQueue<>( (p1, p2) -> Integer.compare(p1.eff_priority, p2.eff_priority) );

    int i = 0;           // used to loop on all the processes in the array (track what hasn't arrived yet) 
    int curr_time = 0;   // used to update time to check if it equals the arrival time of processes to enter RR_queue
    int finished_p = 0;  // to count the finished processes
    Process last = null; // the last process that ran

    
    public Scheduler(Process[] processes, int quantum, int cs) {
        this.processes = processes;
        this.quantum_time = quantum;
        this.context_switching = cs;

        Arrays.sort(processes, (p1, p2) -> Integer.compare(p1.arrival_time, p2.arrival_time));
    }


    // add all the arrived processes to RRQueue
    public void ifArrived(){
 
        while (i < processes.length && processes[i].arrival_time <= curr_time) {
            Process currProcess = processes[i];
            RRQueue.add(currProcess);
            i++;
        }
    }

    public void RoundRobin(){

        Process p = RRQueue.peek(); // to get the current "first" process, but don't remove it
        last = p;

        //calculate first response time
        if (p.first_response_time == -1) {
            p.first_response_time = curr_time;
        }

        // calculating execution time of that round of the process (remaining_time instead of burst bec. it's the value to be updated for further comparison)
        int execution_time;
        if (p.remaining_time > quantum_time) {execution_time = quantum_time;}
        else {execution_time = p.remaining_time;}

        // update remaining time of the whole burst time
        p.remaining_time -= execution_time;

        // update current time
        curr_time += execution_time;

        // demotion step
        if (p.remaining_time == 0){  // if remaining = 0, remove it from queue of tier 1 
            RRQueue.poll();
            finished_p++;
            p.finish_time = curr_time;
        }   
        else {                       // if not, move it to tier 2 queue >> priority , remaining
            RRQueue.poll();
            PrQueue.add(p);
            p.tier2_enter_time = curr_time; 
            p.eff_priority = p.priority;
        }
               
    }

    public void Demotion(){

        Process p = PrQueue.poll();
        last = p;
        curr_time += p.remaining_time;
        p.remaining_time = 0;
        finished_p++;

        p.finish_time = curr_time;

    }

    public void Aging(){

        Queue<Process> temp = new LinkedList<>();

        while(!PrQueue.isEmpty()){
            temp.add(PrQueue.poll());
        }

        for (Process p : temp){
            int waiting_time = curr_time - p.tier2_enter_time;
            p.eff_priority = p.priority - (waiting_time / 4);
        }

        while (!temp.isEmpty()) {
            PrQueue.add(temp.poll());
        }

    }

    public void Schedule(){
        
        boolean firstExecution = true; // it determines we should add cs or not 

        while (finished_p < processes.length) {

            ifArrived();

            // if both queues are empty, update time to the first/next arrival
            if (RRQueue.isEmpty() && PrQueue.isEmpty()) {
                curr_time = processes[i].arrival_time;
                firstExecution = true;
                continue;
            }

            // when RRQueue is empty / finishes working, update priorities before applying tier2
            if (RRQueue.isEmpty()) {
                Aging();
            }

            Process next;
            if (!RRQueue.isEmpty()) next = RRQueue.peek();
            else next = PrQueue.peek();

            // add cs time before the execution of the next process & also after aging calculations
            if (!firstExecution && next != last) { // guaranteed that the next process isn't the same last executed one
                curr_time += context_switching;
            }

            // checking the arrival of new processes during context switching
            ifArrived();

            if (!RRQueue.isEmpty())  RoundRobin();  // RR starts with the processes already arrived it
            else if (!PrQueue.isEmpty())  Demotion(); // if nothing's arrived RR yet, start its first process till the end

            firstExecution = false; // a process is executed, need to add cs before the next one
        }
 
    } 

}
