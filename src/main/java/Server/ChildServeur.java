package Server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

public class ChildServeur implements Runnable {
    private Socket sock;
    private OutputStream writer = null;
    private BufferedReader reader = null;
    private boolean continueState = false;
    private int APOPERRORCHECK = 0;
    private File userFile;
    private String userName;
    public ChildServeur(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run(){
        System.out.println("Initialisation connection Cliente");
        boolean closeConnexion = false;

        //while the cpnnection is still active we treat the different demands
        while(!sock.isClosed()) {
            try {
                //sock.setSoTimeout(10000);
                reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                writer  = sock.getOutputStream();
                //On attend la demande du client
                InetSocketAddress remote = (InetSocketAddress)sock.getRemoteSocketAddress();
                //On affiche quelques infos, pour le débuggage
                String debug = "";
                debug = "Thread : " + Thread.currentThread().getName() + ". ";
                debug += "Demande de l'adresse : " + remote.getAddress().getHostAddress() +".";
                debug += " Sur le port : " + remote.getPort() + ".\n";

                System.err.println("\n" + debug);

                sendMessage(serverReady());
                //On traite la demande du client en fonction de la commande envoyée

                //ETAT AUTHORIZATION

                authorizationState();

                //ETAT TRANSACTION
                closeConnexion = transactionState();

                //Il FAUT IMPERATIVEMENT UTILISER flush()
                //Sinon les données ne seront pas transmises au client
                //et il attendra indéfiniment
                //writer.flush();
                //writer.close();
                if(closeConnexion){
                    System.err.println("COMMANDE CLOSE DETECTEE ! ");
                    writer = null;
                    reader = null;
                    sock.close();
                    break;
                }
            } catch(SocketException e){
                System.err.println("LA CONNEXION A ETE INTERROMPUE ! ");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String data) throws IOException {
        this.writer.write(data.getBytes());
        writer.flush();
        //writer.close()
    }

    private boolean authorizationState() throws IOException {
        do {
            String reponse = read();
            if(checkAPOPComand(reponse)){
                sendMessage(commandAPOP(reponse));
                this.continueState = true;
            }
        } while (!this.continueState);

        this.continueState = false;
        return true;
    }
    private Boolean transactionState() throws IOException {

        do{
            String reponse = read();

            System.out.println("Command '"+ reponse+ "' received");
            switch(checkTransactionCommand(reponse)){
                case 0:
                    sendMessage(commandSTAT());
                    break;
                case 1:
                    sendMessage(commandRETR(reponse));
                    break;
                case 2:
                    sendMessage(commandQUIT(reponse));
                    this.continueState=true;
                    break;
                default:
                    System.out.println("IGNORED");
            }
        } while (!continueState);

        this.continueState = false;
        return true;
    }

    private boolean checkAPOPComand(String data){
        String[] reponse = data.split(" ");
        if(reponse.length == 2) {
            if(reponse[0].equals("APOP")){
                return checkFolderExists(reponse[1]);
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    private boolean checkFolderExists(String folderName){
        return Files.isDirectory(Paths.get("./src/main/resources/" + folderName));
    }

    private int checkTransactionCommand(String reponse){
        String[] data = reponse.split(" ");
        if(data.length > 0) {
            if(data[0].equals("STAT")){
                return 0;
            } else if(data[0].equals("RETR")) {
                return 1;
            } else if(data[0].equals("QUIT")) {
                return 2;
            }else{
                return -1;
            }
        } else {
            return -1;
        }

    }

    private String commandAPOP(String command){
        String[] reponse = command.split(" ");
        this.userName =  reponse[1];
        this.userFile = new File("./src/main/resources/" + reponse[1]);

        return "+OK mail drop has "+ this.userFile.list().length + " message \r\n";
    }

    private String commandSTAT(){
        return "+OK "+ this.userFile.list().length+" "+getFileSize()+" \r\n";
    }

    private long getFileSize(){
        long folderSize = 0;
        for(File f : this.userFile.listFiles()){
            folderSize += f.length();
        }
        return folderSize;
    }


    private String commandRETR(String command){
        String[] reponse = command.split(" ");
        String mailToSend= "";
        if(reponse.length == 1) {
            for( File f : this.userFile.listFiles()){
                mailToSend+=readEmail(f);
            }
        }else if(reponse.length > 1){
            try{
                int mailNumber= Integer.parseInt(reponse[1]);
                if(this.userFile.list().length>mailNumber && mailNumber>=0){
                    mailToSend+=readEmail(this.userFile.listFiles()[mailNumber]);
                }else{
                    return "-ERR invalid message number \r\n";
                }
            }catch (NumberFormatException ex){
                return "-ERR invalid RETR Command \r\n";
            }
        }else{
            return "-ERR invalid RETR Command \r\n";
        }
        return "+OK \n\n"+mailToSend + " \r\n";
    }
    private String readEmail(File f){
        String mail="";
        try {
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine())
                mail+=sc.nextLine()+"\r\n";

            mail+="\n\n";

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return mail;

    }
    private String commandQUIT(String command){
        return "QUIT";
    }

    private String serverReady(){
        return "+Ok POP3 server ready  \r\n";
    }

    private String read() throws IOException {
        String test = reader.readLine();
        return test;
    }

    private String getFile(String reponses){
        try{
            String toSend = "+OK POP3 " + reponses + "\r\n";
            //File file = new File("./src/main/resources"+route[1]);
            return toSend;
        } catch (Exception e) {
            return sendError();
        }

    }

    private String sendError() {
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
