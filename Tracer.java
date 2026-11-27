import java.util.ArrayList;
import java.util.Queue;

public class Tracer {

    Scheduler s;                                    // reads the queues and the clock from the scheduler
    ArrayList<SimulationResult.logs> logs = new ArrayList<>();   // the output rows
    boolean afterCS = false;                    // true if a CS row was just logged
    String lastOthers = "";                     // Tier 1 (without the running process) at the last shown row

    public Tracer(Scheduler s) {
        this.s = s;
    }

    // sorts a list by the time the process entered Tier 2 (insertion sort)
    private void sortByEntryTime(ArrayList<Process> list) {
    for (int round = 0; round < list.size(); round++) {
        for (int k = 0; k < list.size() - 1; k++) {
            Process left = list.get(k);
            Process right = list.get(k + 1);

            if (left.tier2_enter_time > right.tier2_enter_time) {   // out of order, so swap
                list.set(k, right);
                list.set(k + 1, left);
            }
        }
    }
}

    // one queue as text. Tier 2 is listed by entry time (as in the PDF) and shows the effective priority
    private String format(ArrayList<Process> list, boolean tier2) {
        if (list.size() == 0) {
            return "[Empty]";
        }

        if (tier2) {
            sortByEntryTime(list);
        }

        String text = "[";
        for (int i = 0; i < list.size(); i++) {
            Process p = list.get(i);

            int priority;
            if (tier2) {
                priority = p.eff_priority;
            } else {
                priority = p.priority;
            }

            if (i > 0) {
                text += ", ";
            }
            text += p.name + " (remaining: " + p.remaining_time + ", Priority: " + priority + ")";
        }
        text += "]";
        return text;
    }

    // the whole queue
    public String queueState(Queue<Process> q) {
        ArrayList<Process> list = new ArrayList<Process>();
        for (Process p : q) {
            list.add(p);
        }
        return format(list, q == s.PrQueue);
    }

    // the queue without one process
    public String queueStateExcept(Queue<Process> q, Process excluded) {
        ArrayList<Process> list = new ArrayList<Process>();
        for (Process p : q) {
            if (p != excluded) {
                list.add(p);
            }
        }
        return format(list, q == s.PrQueue);
    }

    // the queue plus the running process (Demotion() polls it out of Tier 2 before it runs)
    public String queueStateWith(Queue<Process> q, Process running) {
        ArrayList<Process> list = new ArrayList<Process>();
        boolean found = false;
        for (Process p : q) {
            list.add(p);
            if (p == running) {
                found = true;
            }
        }
        if (!found) {
            list.add(running);
        }
        return format(list, q == s.PrQueue);
    }

    // one row per time unit of a run, called BEFORE the unit executes
    public void logStep(Process running, boolean dispatch) {
        boolean tier1Run = s.RRQueue.contains(running);
        String t1 = "";
        String t2 = "";
        String note = "";

        if (dispatch) {                                    // first unit: full state
            if (afterCS) {                                 // the CS rows already listed the running process
                t1 = queueStateExcept(s.RRQueue, running);
                t2 = queueStateExcept(s.PrQueue, running);
            } else {
                t1 = queueState(s.RRQueue);
                if (tier1Run) {
                    t2 = queueState(s.PrQueue);
                } else {
                    t2 = queueStateWith(s.PrQueue, running);
                }
            }

            if (tier1Run) {
                note = "Tier 1 run";
            } else {
                note = "Tier 2 run (non-preemptive)";
            }

            lastOthers = queueStateExcept(s.RRQueue, running);
            afterCS = false;
        } else {                                           // later units: show only an arrival
            String others = queueStateExcept(s.RRQueue, running);
            if (!others.equals(lastOthers)) {
                t1 = others;
                note = "arrival";
                lastOthers = others;
            }
        }

        logs.add(new SimulationResult.logs(s.curr_time, running.name, t1, t2, note));
    }

    public void logCS(Process from, Process to) {
        logs.add(new SimulationResult.logs(s.curr_time, "", queueState(s.RRQueue), queueState(s.PrQueue),
        "Context switch " + from.name + " -> " + to.name));
    }

    public void logIdle(int until) {
        logs.add(new SimulationResult.logs(s.curr_time, "IDLE", "[Empty]", "[Empty]", "CPU idle until t=" + until));
    }

    public void logEnd() {
        logs.add(new SimulationResult.logs(s.curr_time, "", queueState(s.RRQueue), queueState(s.PrQueue), ""));
    }
}