package net.zicron.ultraupnp.gui;

/*
 ***********************************************************************
 * Copyright 2020 Hunter Wilcox
 * Copyright 2020 Zicron-Technologies
 *
 * This file is part of UltraUPNP.
 *
 * UltraUPNP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraUPNP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UltraUPNP.  If not, see <http://www.gnu.org/licenses/>.
 *
 *******************************************************************
 *
 * Ref: http://upnp.org/specs/gw/UPnP-gw-WANIPConnection-v2-Service.pdf
 *      http://upnp.org/resources/documents/UPnP_UDA_tutorial_July2014.pdf
 */


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.zicron.ultraupnp.Log;
import net.zicron.ultraupnp.Router;
import net.zicron.ultraupnp.RouterFinder;
import net.zicron.ultraupnp.UltraUPnP;

import java.io.IOException;
import java.util.List;

public class MainWindow extends Application{

    public boolean isGUIRunning = false;
    public static MainWindow currentMainWindow;

    public Stage primaryStage;


    public Router router;

    @FXML public TableView<PortMapping> tableView;
    @FXML private TableColumn<PortMapping, String> tcHost;
    @FXML private TableColumn<PortMapping, String> tcInternal;
    @FXML private TableColumn<PortMapping, String> tcExternal;
    @FXML private TableColumn<PortMapping, String> tcProtocol;
    @FXML private TableColumn<PortMapping, String> tcDescription;

    @FXML private Button btnConnect;
    @FXML private Button btnAddPort;
    @FXML private Button btnRemovePort;
    @FXML private Button btnRefresh;
    @FXML private Button btnAdvanced;

    @FXML public TextArea txtLog;

    private RouterFinder routerFinder;


    public MainWindow(){

    }

    @FXML
    private void initialize(){
        MainWindow.currentMainWindow = this;
        MainWindow.currentMainWindow.isGUIRunning = true;
        tcHost.setCellValueFactory(new PropertyValueFactory<>("hostname"));
        tcInternal.setCellValueFactory(new PropertyValueFactory<>("internalPort"));
        tcExternal.setCellValueFactory(new PropertyValueFactory<>("externalPort"));
        tcProtocol.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        txtLog.clear();
        txtLog.appendText("UltraUPnP GUI v" + UltraUPnP.VERSION + " - IS_BETA: " + UltraUPnP.IS_BETA + "\n");
    }


    @FXML
    private void connect(){
        btnConnect.setDisable(true);
        new Thread(() -> {
            routerFinder = new RouterFinder();
            try {
                if(routerFinder.search()){
                    router = new Router(routerFinder.getUPNPUrlDescriptor());
                    Platform.runLater(() -> {
                        btnRemovePort.setDisable(false);
                        btnRefresh.setDisable(false);
                        btnConnect.setDisable(true);
                    });
                    listPortMappings();
                }else{
                    Platform.runLater(() -> btnConnect.setDisable(false));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void refresh(){
        tableView.getItems().clear();
        listPortMappings();
    }

    @FXML
    private void remove(){
        PortMapping selectedPortMapping = tableView.getSelectionModel().getSelectedItem();
        if(selectedPortMapping == null){
            Log.error("Couldn't remove mapping: Nothing selected!");
            return;
        }

        btnRemovePort.setDisable(true);
        Log.debug("PORT HOST: " + selectedPortMapping.getHostname());

        new Thread(() -> {
            try {
                router.removeMapping(Integer.parseInt(selectedPortMapping.getExternalPort()), selectedPortMapping.getHostname(), selectedPortMapping.getProtocol());
                Platform.runLater(() -> {
                    btnRemovePort.setDisable(false);
                    tableView.getItems().remove(selectedPortMapping);
                });
                //listPortMappings();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void add() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddMappingWindow.fxml"));
        loader.setController(new AddPortMapping(tableView, router));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("UltraUPnP GUI - v" + UltraUPnP.VERSION + " - Add Port Mapping");
        stage.setScene(new Scene(root, 600, 320));
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void advanced() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AdvanceWindow.fxml"));
        loader.setController(new AdvancedWindow());
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("UltraUPnP GUI - v" + UltraUPnP.VERSION + " - Advanced Settings");
        stage.setScene(new Scene(root, 433, 99));
        stage.setResizable(false);
        stage.show();
    }

    private void listPortMappings(){
        try {
            List<Router.RouterArgument> routerArguments = router.getPortMappings();

            String host = "";
            String proto = "";
            String externalPort = "";
            String internalPort = "";
            String description = "";

            for(Router.RouterArgument ra: routerArguments) {
                switch (ra.getArgName()) {
                    case "NewInternalClient":
                        host = ra.getArgValue();
                        break;
                    case "NewProtocol":
                        proto = ra.getArgValue();
                        break;
                    case "NewInternalPort":
                        internalPort = ra.getArgValue();
                        break;
                    case "NewExternalPort":
                        externalPort = ra.getArgValue();
                        break;
                    case "NewPortMappingDescription":
                        description = ra.getArgValue();
                        break;
                }

                if (!proto.isEmpty() && !host.isEmpty() && !externalPort.isEmpty() && !internalPort.isEmpty() && !proto.isEmpty()) {
                    tableView.getItems().add(new PortMapping(host, internalPort, externalPort, proto, description));
                    host = "";
                    proto = "";
                    externalPort = "";
                    internalPort = "";
                    description = "";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void appendToLog(String message){
        Platform.runLater(() -> txtLog.appendText(message));
        Platform.runLater(() -> txtLog.selectPositionCaret(txtLog.getLength()));
        Platform.runLater(() -> txtLog.deselect());
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("UltraUPnP GUI - v" + UltraUPnP.VERSION);
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void run(String[] args){
        launch(args);
    }
}
