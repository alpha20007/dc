import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class RPCExample {
    // Simulated remote procedure
    static int processRemote(int param) {
        return param * 2;
    }

    public static void main(String[] args) {
        String csvFile = "rpc_calls_212.csv";
        List<String> records = new ArrayList<>();

        File file = new File(csvFile);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line = br.readLine(); // skip header
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) records.add(line);
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            // In-code sample data matching CSV dataset
            records = Arrays.asList(
                "101,Client_A,processData,450,10:00:01",
                "102,Client_B,processData,300,10:00:02",
                "103,Client_C,processData,250,10:00:03"
            );
        }

        for (String record : records) {
            String[] p = record.split(",");
            String callId = p[0].trim();
            String client = p[1].trim();
            String method = p[2].trim();
            int val = Integer.parseInt(p[3].trim());
            String time = p[4].trim();

            System.out.println("[DISPATCH] Client invoking method -> " + client + " calling " + method + "(" + val + ") [CallID: " + callId + " at " + time + "]");
            int result = processRemote(val);
            System.out.println("[SERVER EXECUTION] Processing parameter -> " + val + " executed, calculated: " + result);
            System.out.println("[RETURN] Result printed to terminal -> Response: " + result);
        }
    }
}

/*
CSV FILE NAME: rpc_calls_212.csv

Create this CSV only if the question requires an actual CSV file.

CSV COLUMNS:
call_id,client_name,method_name,param_value,timestamp

SAMPLE CSV DATA:
call_id,client_name,method_name,param_value,timestamp
101,Client_A,processData,450,10:00:01
102,Client_B,processData,300,10:00:02
103,Client_C,processData,250,10:00:03
104,Client_D,processData,180,10:00:04
105,Client_E,processData,520,10:00:05

HOW TO SAVE:
If the program reads an actual CSV file, save the sample data as rpc_calls_212.csv in the same directory as this Java file.
*/
