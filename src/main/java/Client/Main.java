package Client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    private String user = "admin";
    private String pw = "admin";
    private String checkUser, checkPw;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Client POP3");

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10,50,50,50));

        HBox hb = new HBox();
        hb.setPadding(new Insets(20,20,20,30));

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20,20,20,20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        Label lblUserName = new Label("Username");
        final TextField txtUserName = new TextField();
        Label lblPassword = new Label("Password");
        final PasswordField pf = new PasswordField();
        Button btnLogin = new Button("Login");
        final Label lblMessage = new Label();

        gridPane.add(lblUserName, 0, 0);
        gridPane.add(txtUserName, 1, 0);
        gridPane.add(lblPassword, 0, 1);
        gridPane.add(pf, 1, 1);
        gridPane.add(btnLogin, 2, 1);
        gridPane.add(lblMessage, 1, 2);

        Text text = new Text("Client POP3");
        text.setFont(Font.font(30));

        hb.getChildren().add(text);

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                checkUser = txtUserName.getText();
                checkPw = pf.getText();
                if(checkUser.equals(user) && checkPw.equals(pw)){
                    lblMessage.setText("Congratulations!");
                    lblMessage.setTextFill(Color.GREEN);
                }
                else{
                    lblMessage.setText("Incorrect user or pw.");
                    lblMessage.setTextFill(Color.RED);
                }
                txtUserName.setText("");
                pf.setText("");
            }
        });

        bp.setTop(hb);
        bp.setCenter(gridPane);

        Scene scene = new Scene(bp);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
