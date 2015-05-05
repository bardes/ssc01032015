package TTT;

public class TTTMain {
    public static void main(String args[]) {
        if(args.length >= 2 && args[0].equals("--server")) {
            try {
                TTTServer s = new TTTServer(Integer.parseInt(args[1]));
                s.run();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            javafx.application.Application.launch(TTTClient.class);
        }
    }
}
