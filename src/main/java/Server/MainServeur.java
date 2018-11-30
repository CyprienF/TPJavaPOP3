package Server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServeur {
    private static final int PORTECOUTE=110;
    private ServerSocket socketEcouteSeveur;
    private boolean isRunning = true;
    public MainServeur() throws IOException {
        this.socketEcouteSeveur = new ServerSocket(PORTECOUTE,6);
    }

    public void runServeur() throws IOException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning){
                    //Une fois reçue, on la traite dans un thread séparé
                    try {
                        Socket nouvelleConnection= socketEcouteSeveur.accept();
                        System.out.println("Connexion cliente reçue.");
                        Thread t = new Thread(new ChildServeur(nouvelleConnection));
                        t.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();

    }


    public void closeConnection(){
        isRunning= false;
    }

}
