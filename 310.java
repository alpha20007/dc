import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

class LamportClock {
    static class Event {
        String id, pid, type, target;
        int time;
        Event(String id, String pid, String type, String target) {
            this.id = id; this.pid = pid; this.type = type; this.target = target;
        }
    }

    public static void main(String[] args) {
        String csvFile = "events_310.csv";
        List<String> records = new ArrayList<>();

        File file = new File(csvFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) records.add(line);
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            records = Arrays.asList(
                "E1,P1,SEND,P2,0",
                "E2,P2,RECV,P1,0",
                "E3,P1,INTERNAL,NONE,0",
                "E4,P2,SEND,P3,0",
                "E5,P3,RECV,P2,0"
            );
        }

        Map<String, Integer> clocks = new HashMap<>();
        clocks.put("P1", 0); clocks.put("P2", 0); clocks.put("P3", 0);
        Map<String, Integer> msgTimes = new HashMap<>();
        List<Event> events = new ArrayList<>();

        System.out.println("Step-by-step clock updates using L = max(L, T_msg) + 1:");
        for (String r : records) {
            String[] p = r.split(",");
            Event ev = new Event(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim());
            int cur = clocks.getOrDefault(ev.pid, 0);

            if (ev.type.equalsIgnoreCase("INTERNAL") || ev.type.equalsIgnoreCase("SEND")) {
                int next = cur + 1;
                clocks.put(ev.pid, next);
                ev.time = next;
                if (ev.type.equalsIgnoreCase("SEND")) msgTimes.put(ev.pid + "->" + ev.target, next);
                System.out.println("Event " + ev.id + " (" + ev.pid + "): L = " + cur + " + 1 = " + next);
            } else if (ev.type.equalsIgnoreCase("RECV")) {
                int tmsg = msgTimes.getOrDefault(ev.target + "->" + ev.pid, cur);
                int next = Math.max(cur, tmsg) + 1;
                clocks.put(ev.pid, next);
                ev.time = next;
                System.out.println("Event " + ev.id + " (" + ev.pid + "): L = max(" + cur + ", " + tmsg + ") + 1 = " + next);
            }
            events.add(ev);
        }

        events.sort((a, b) -> Integer.compare(a.time, b.time));
        System.out.println("final sorted causal order of all events:");
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            System.out.println((i + 1) + ". " + e.id + " [" + e.pid + "] timestamp = " + e.time);
        }
    }
}

/*
CSV FILE NAME: events_310.csv

Create this CSV only if the question requires an actual CSV file.

CSV COLUMNS:
event_id,process_id,event_type,message_to,initial_clock

SAMPLE CSV DATA:
event_id,process_id,event_type,message_to,initial_clock
E1,P1,SEND,P2,0
E2,P2,RECV,P1,0
E3,P1,INTERNAL,NONE,0
E4,P2,SEND,P3,0
E5,P3,RECV,P2,0

HOW TO SAVE:
If the program reads an actual CSV file, save the sample data as events_310.csv in the same directory as this Java file.
*/
