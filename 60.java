import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

class VectorClock {
    static class EventLog {
        String id, pid, action, target;
        int[] clock = new int[3];
        EventLog(String id, String pid, String action, String target) {
            this.id = id; this.pid = pid; this.action = action; this.target = target;
        }
    }

    public static void main(String[] args) {
        String csvFile = "vector_events_60.csv";
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
                "E1,P1,INTERNAL,NONE",
                "E2,P1,SEND,P2",
                "E3,P2,RECV,P1",
                "E4,P3,INTERNAL,NONE",
                "E5,P2,SEND,P3",
                "E6,P3,RECV,P2"
            );
        }

        Map<String, int[]> vclocks = new HashMap<>();
        vclocks.put("P1", new int[]{0, 0, 0});
        vclocks.put("P2", new int[]{0, 0, 0});
        vclocks.put("P3", new int[]{0, 0, 0});

        Map<String, int[]> sent = new HashMap<>();
        List<EventLog> events = new ArrayList<>();

        for (String r : records) {
            String[] p = r.split(",");
            EventLog ev = new EventLog(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim());
            int idx = ev.pid.equals("P1") ? 0 : (ev.pid.equals("P2") ? 1 : 2);
            int[] vc = vclocks.get(ev.pid);

            if (ev.action.equalsIgnoreCase("INTERNAL") || ev.action.equalsIgnoreCase("SEND")) {
                vc[idx]++;
                if (ev.action.equalsIgnoreCase("SEND")) {
                    sent.put(ev.pid + "->" + ev.target, vc.clone());
                }
            } else if (ev.action.equalsIgnoreCase("RECV")) {
                vc[idx]++;
                int[] src = sent.get(ev.target + "->" + ev.pid);
                if (src != null) {
                    for (int k = 0; k < 3; k++) vc[k] = Math.max(vc[k], src[k]);
                }
            }
            ev.clock = vc.clone();
            events.add(ev);
            System.out.println("Updated vector clock [" + vc[0] + ", " + vc[1] + ", " + vc[2] + "] after event " + ev.id);
        }

        System.out.println("explicitly identifies pairs as Causally Preceding (V1 < V2) or Concurrent (E1 || E2):");
        for (int i = 0; i < events.size(); i++) {
            for (int j = i + 1; j < events.size(); j++) {
                EventLog e1 = events.get(i);
                EventLog e2 = events.get(j);
                boolean le = true, lt = false;
                for (int k = 0; k < 3; k++) {
                    if (e1.clock[k] > e2.clock[k]) le = false;
                    if (e1.clock[k] < e2.clock[k]) lt = true;
                }
                if (le && lt) {
                    System.out.println(e1.id + " and " + e2.id + " -> Causally Preceding (" + e1.id + " < " + e2.id + ")");
                } else {
                    System.out.println(e1.id + " and " + e2.id + " -> Concurrent (" + e1.id + " || " + e2.id + ")");
                }
            }
        }
    }
}

/*
CSV FILE NAME: vector_events_60.csv

Create this CSV only if the question requires an actual CSV file.

CSV COLUMNS:
event_id,process_id,event_action,target_process

SAMPLE CSV DATA:
event_id,process_id,event_action,target_process
E1,P1,INTERNAL,NONE
E2,P1,SEND,P2
E3,P2,RECV,P1
E4,P3,INTERNAL,NONE
E5,P2,SEND,P3
E6,P3,RECV,P2

HOW TO SAVE:
If the program reads an actual CSV file, save the sample data as vector_events_60.csv in the same directory as this Java file.
*/
