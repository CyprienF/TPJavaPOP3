package Client;

import sun.misc.BASE64Decoder;
import sun.misc.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private OutputStream out=null;
    private InputStream in=null;
    public Client() {

    }

    public Client(Socket socket) {
        this.socket = socket;
    }

    public String runClient(String host, int port, String userName) throws IOException {

        this.socket = new Socket(host, port);

        this.out = this.socket.getOutputStream();
        this.in  = this.socket.getInputStream();
        String response= readResponse(in);
        System.out.println(response);

        String commandAPOP="APOP "+userName+ "\r\n";
        sendMessage( commandAPOP);
        response= readResponse(in);
        if(((response.split(" "))[0]).equals("-ERR")){
            this.out.close();
            this.in.close();
            this.closeSocket();
            return "-ERR";
        }
        System.out.println(response);
        sendMessage("RETR \r\n");

        response=readResponse(in);
        System.out.println(response);
        sendMessage("QUIT \r\n");
        this.out.close();
        this.in.close();
        this.closeSocket();
        return  response;
    }

    private void sendMessage(String request) throws IOException {
        System.out.println("* Request");
        System.out.println(request);

        this.out.write(request.getBytes());
        this.out.flush();
    }

    private String readResponse(InputStream in) throws IOException {
        System.out.println(in.available());
        int available = in.available();
        while(available == 0) {
            available = in.available();
        }
        byte[] b=new byte[in.available()];
        in.read(b);
        String result=new String(b, "UTF-8");
        return result;
    }

    private String getBody(String s){
        String body = "";
        String tmp="";
        Scanner sc = new Scanner(s);
        boolean bodyFound=false;
        while (sc.hasNextLine()){
            tmp=sc.nextLine();
            if(bodyFound){
                body+=tmp+"\r\n";
            }
            if(tmp.isEmpty()&& !bodyFound){
                bodyFound=!bodyFound;
            }
        }
        return body;
    }

    private String writeFile(String path,String body, boolean isImage){

        String newPath ="/download"+path;
        try {

        if(isImage){

            File outputfile = new File("./src/main/resources/download"+path);
            BufferedImage image = null;
            byte[] imageByte;

            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(body);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
            // write the image to a file

            String[] decomposedPath= path.split("/");
            String fileName[]= (decomposedPath[decomposedPath.length-1]).split("\\.");
            String formatName = fileName[fileName.length-1];
            ImageIO.write(image, formatName, outputfile);


        }else{
            System.out.println(body);
            String[] decomposedPath= path.split("/");
            String fileName[]= (decomposedPath[decomposedPath.length-1]).split("\\.");
            String formatName = fileName[fileName.length-1];
            File outputfile;
            if(formatName.equals("txt") || formatName.equals("html") ){
                outputfile = new File("./src/main/resources/download"+path);
            }else{
                newPath="/download/error.html";
                outputfile= new File("./src/main/resources/download/error.html");
            }

            FileWriter fileWriter = new FileWriter(outputfile);
            fileWriter.write(body);
            fileWriter.flush();
            fileWriter.close();
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newPath;
    }

    private void closeSocket() {
        try {
            this.socket.close();
            System.out.println("The socket is closed");
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}