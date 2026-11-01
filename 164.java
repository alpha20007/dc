import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

class RingAlgorithm {
    static class RingNode {
        int id, nextId, priority;
        boolean active;
        RingNode(int id, int nextId, boolean active, int priority) {
            this.id = id; this.nextId = nextId; this.active = active; this.priority = priority;
        }
    }

    public static void main(String[] args) {
        String csvFile = "ring_nodes_164.csv";
        Map<Integer, RingNode> ring = new LinkedHashMap<>();

        File file = new File(csvFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(",");
                    ring.put(Integer.parseInt(p[0].trim()), new RingNode(Integer.parseInt(p[0].trim()), Integer.parseInt(p[1].trim()), Boolean.parseBoolean(p[2].trim()), Integer.parseInt(p[3].trim())));
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            ring.put(1, new RingNode(1, 2, true, 60));
            ring.put(2, new RingNode(2, 3, true, 75));
            ring.put(3, new RingNode(3, 4, true, 90));
            ring.put(4, new RingNode(4, 5, false, 99));
            ring.put(5, new RingNode(5, 1, true, 40));
        }

        System.out.println("Detected failure of coordinator: Node 4");
        int initiator = 1;
        System.out.println("token message circulation across active ring nodes:");

        List<Integer> token = new ArrayList<>();
        int curr = initiator;
        token.add(curr);

        do {
            int next = ring.get(curr).nextId;
            while (ring.containsKey(next) && !ring.get(next).active) {
                next = ring.get(next).nextId;
            }
            System.out.println("Node " + curr + " sends token " + token + " to Node " + next);
            curr = next;
            if (curr != initiator) token.add(curr);
        } while (curr != initiator);

        int leader = Collections.max(token);
        System.out.println("final election of node with the highest ID: Node " + leader + " elected Leader");
        System.out.println("Broadcasting COORDINATOR message to ring.");
    }
}

/*
CSV FILE NAME: ring_nodes_164.csv

Create this CSV only if the question requires an actual CSV file.

CSV COLUMNS:
node_id,next_node_id,is_active,priority_rank

SAMPLE CSV DATA:
node_id,next_node_id,is_active,priority_rank
1,2,TRUE,60
2,3,TRUE,75
3,4,TRUE,90
4,5,FALSE,99
5,1,TRUE,40

HOW TO SAVE:
If the program reads an actual CSV file, save the sample data as ring_nodes_164.csv in the same directory as this Java file.
*/
