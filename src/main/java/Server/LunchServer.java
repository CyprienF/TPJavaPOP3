package Server;

import java.io.IOException;

public class LunchServer {

    public static void main(String[] args) {
        try {
            MainServeur m = new MainServeur();
            m.runServeur();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
