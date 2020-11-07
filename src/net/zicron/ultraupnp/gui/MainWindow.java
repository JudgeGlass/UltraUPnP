package net.zicron.ultraupnp.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import net.zicron.ultraupnp.UltraUPnP;

public class MainWindow extends Application{

    public static MainWindow currentMainWindow = new MainWindow();

    public Stage primaryStage;

    @FXML private ListView portMappingList;


    public MainWindow(){

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("UltraUPnP GUI - v" + UltraUPnP.VERSION);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
