package Server;


import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Scanner;

public class ChildServeur implements Runnable {
    private Socket sock;
    private OutputStream writer = null;
    private BufferedReader reader = null;

    public ChildServeur(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run(){
        System.out.println("Initialisation connection Cliente");
        boolean closeConnexion = false;

        //while the cpnnection is still active we treat the different demands
        while(!sock.isClosed()){
            try {
                //sock.setSoTimeout(10000);
                reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                writer  = sock.getOutputStream();
                //On attend la demande du client
                String reponse = read();
                InetSocketAddress remote = (InetSocketAddress)sock.getRemoteSocketAddress();
                //On affiche quelques infos, pour le débuggage
                String debug = "";
                debug = "Thread : " + Thread.currentThread().getName() + ". ";
                debug += "Demande de l'adresse : " + remote.getAddress().getHostAddress() +".";
                debug += " Sur le port : " + remote.getPort() + ".\n";
                debug += "\t -> Commande reçue : " + reponse + "\n";

                if(reponse==null)
                    closeConnexion =true;

                System.err.println("\n" + debug);

                //On traite la demande du client en fonction de la commande envoyée
                String toSend =getFile(reponse);

                //Traitement des données reçues

                writer.write(toSend.getBytes());

                //Il FAUT IMPERATIVEMENT UTILISER flush()
                //Sinon les données ne seront pas transmises au client
                //et il attendra indéfiniment
                writer.flush();
                //writer.close();
                if(closeConnexion){
                    System.err.println("COMMANDE CLOSE DETECTEE ! ");
                    writer = null;
                    reader = null;
                    sock.close();
                    break;
                }
            }catch(SocketException e){
                System.err.println("LA CONNEXION A ETE INTERROMPUE ! ");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String commandAPOP(String command){


        return "APOP";
    }
    private String read() throws IOException {
        String test=reader.readLine();
        return test;
    };

    private String getFile(String reponses){
        try{
            String toSend="+OK POP3 "+ reponses +"\r\n";
            //File file = new File("./src/main/resources"+route[1]);


            return toSend;
        }catch (Exception e){
            return sendError();
        }

    }

    private String sendError(){
        return "Error";
    }

    private  String encodeFileToBase64Binary(File file) throws IOException {
        String encodedfile = null;

        FileInputStream fileInputStreamReader = new FileInputStream(file);
        byte[] bytes = new byte[(int)file.length()];
        fileInputStreamReader.read(bytes);
        encodedfile = new String(Base64.getMimeEncoder().encodeToString(bytes));

        return encodedfile;
    }
}
