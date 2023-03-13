package me.exemple.clientchatjavafx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientChatController implements Initializable {

    @FXML
    private TextField hostTxt,portTxt,tf_message;
    @FXML
    private Button connectBtn,button_send,deconnectBtn;

    @FXML
    private VBox vbox_message;
    @FXML
    private HBox hbox_texting;
    @FXML
    private ScrollPane sp_main;


    Socket socket;
    PrintWriter pw;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deconnectBtn.setDisable(true);
        hbox_texting.setDisable(true);
        connectBtn.setOnMouseClicked((mouseEvent -> {
            handlConnection();
            connectBtn.setDisable(true);
            deconnectBtn.setDisable(false);
            hbox_texting.setDisable(false);
            hostTxt.setDisable(true);
            portTxt.setDisable(true);
            }));

        deconnectBtn.setOnAction((event -> {
            try {
                socket.close();
                deconnectBtn.setDisable(true);
                connectBtn.setDisable(false);
                hbox_texting.setDisable(true);
                hostTxt.setDisable(false);
                portTxt.setDisable(false);
            } catch (IOException e) {

            }

        }));

        vbox_message.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            sp_main.setVvalue((Double)newValue);

        });

        button_send.setOnAction((event)->{
             String message = tf_message.getText();
             if(!message.isEmpty()){
                 HBox hBox = new HBox();
                 hBox.setAlignment(Pos.CENTER_RIGHT);
                 hBox.setPadding(new Insets(5,5,5,10));
                 Text text = new Text(message);
                 TextFlow textFlow = new TextFlow(text);

                 textFlow.setStyle("-fx-color:rgb(239,242,255);"+
                         "-fx-background-color:rgb(15,125,242);" +
                         "-fx-background-radius: 20px");

                 textFlow.setPadding(new Insets(5,10,5,10));
                 text.setFill(Color.color(0.934,0.945,0.996));
                 hBox.getChildren().add(textFlow);
                 vbox_message.getChildren().add(hBox);
                pw.println(message);
                tf_message.clear();
             }
        });


    }

    private void handlConnection() {

        String host = hostTxt.getText();
        int port = Integer.parseInt(portTxt.getText());
        try {
            socket = new Socket(host,port);
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            pw = new PrintWriter(os,true);
            new Thread(()->{
                while (true) {
                    try {
                        String response = br.readLine();
                        if (!response.isEmpty()){
                            HBox hBox = new HBox();
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.setPadding(new Insets(5,5,5,10));
                            Text text = new Text(response);
                            TextFlow textFlow = new TextFlow(text);

                            textFlow.setStyle("-fx-background-color:rgb(233,233,235);" +
                                    "-fx-background-radius: 20px");

                            textFlow.setPadding(new Insets(5,10,5,10));

                            hBox.getChildren().add(textFlow);
                            Platform.runLater(()->{
                                vbox_message.getChildren().add(hBox);
                            });

                        }
                    } catch (IOException e) {

                    }
                }
            }).start();



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
