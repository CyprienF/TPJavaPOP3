package Client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private String checkUser, checkPw, checkHostServer, checkHostPort;

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Client POP3");

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10,50,50,50));

        HBox hb = new HBox();
        hb.setPadding(new Insets(20,20,20,30));

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20,20,20,20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        Label lblUserName = new Label("Utilisateur");
        final TextField txtUserName = new TextField();
        Label lblPassword = new Label("Mot de passe");
        final PasswordField pf = new PasswordField();
        final Button btnLogin = new Button("Connexion");
        final Label lblMessage = new Label();

        Label lblHostServer = new Label("Adresse serveur");
        final TextField txtHostServer = new TextField();
        Label lblHostPort = new Label("Port serveur");
        final TextField txtHostPort = new TextField();

        gridPane.add(lblUserName, 0, 0);
        gridPane.add(txtUserName, 1, 0);
        gridPane.add(lblPassword, 0, 1);
        gridPane.add(pf, 1, 1);
        gridPane.add(lblHostServer, 0, 2);
        gridPane.add(txtHostServer, 1, 2);
        gridPane.add(lblHostPort, 0, 3);
        gridPane.add(txtHostPort, 1, 3);

        gridPane.add(btnLogin, 2, 3);

        gridPane.add(lblMessage, 1, 4);

        Text text = new Text("Client POP3");
        text.setFont(Font.font(30));

        hb.getChildren().add(text);

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                checkUser = txtUserName.getText();
                checkPw = pf.getText();
                checkHostServer = txtHostServer.getText();
                checkHostPort = txtHostPort.getText();
                if(checkUser.equals(user) && checkPw.equals(pw)) {
                    lblMessage.setText("Congratulations!");
                    lblMessage.setTextFill(Color.GREEN);

                    ListView<String> listView;

                    Group root = new Group();

                    HBox listViewPanel = new HBox();
                    listViewPanel.setSpacing(10);

                    final Text label = new Text("Nothing selected.");

                    listView = new ListView<String>(FXCollections.observableArrayList("Item 1", "Item 2", "Item 3", "Item 4"));
                    listView.prefWidth(100);
                    listView.setMaxWidth(100);
                    listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            label.setText("You selected " + newValue);
                        }
                    });

                    listViewPanel.getChildren().addAll(listView, label);

                    GridPane gridPane = new GridPane();
                    gridPane.setPadding(new Insets(20,20,20,20));
                    gridPane.setHgap(5);
                    gridPane.setVgap(5);

                    Button refreshBtn = new Button("Rafraîchir");

                    btnLogin.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                        }
                    });

                    gridPane.add(listViewPanel, 0, 0);
                    gridPane.add(refreshBtn, 0, 1);

                    root.getChildren().add(gridPane);

                    Scene scene = new Scene(root, 300, 500);
                    primaryStage.setTitle("Client POP3");
                    primaryStage.setScene(scene);
                    primaryStage.show();

                    txtUserName.setText("");
                    pf.setText("");
                    txtHostServer.setText("");
                    txtHostPort.setText("");
                }
                else if ("".equals(checkUser) && "".equals(checkPw)) {
                    lblMessage.setText("User ou mdp incorrect.");
                    lblMessage.setTextFill(Color.RED);
                }
                else if ("".equals(checkHostServer) && "".equals(checkHostPort)) {
                    lblMessage.setText("Serveur ou port incorrect.");
                    lblMessage.setTextFill(Color.RED);
                } else {
                    lblMessage.setText("Impossible de se connecter.");
                    lblMessage.setTextFill(Color.RED);
                }
            }
        });

        bp.setTop(hb);
        bp.setCenter(gridPane);

        Scene scene = new Scene(bp);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
