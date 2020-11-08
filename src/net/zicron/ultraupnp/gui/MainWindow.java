package net.zicron.ultraupnp.gui;

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
    }


    @FXML
    private void connect(){
        new Thread(() -> {
            routerFinder = new RouterFinder();
            try {
                if(routerFinder.search()){
                    router = new Router(routerFinder.getUPNPUrlDescriptor());
                    Platform.runLater(() -> {
                        btnAddPort.setDisable(false);
                        btnRemovePort.setDisable(false);
                        btnConnect.setDisable(true);
                    });
                    listPortMappings();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void remove(){
        PortMapping selectedPortMapping = tableView.getSelectionModel().getSelectedItem();
        btnAddPort.setDisable(true);
        btnRemovePort.setDisable(true);
        Log.debug("PORT HOST: " + selectedPortMapping.getHostname());

        new Thread(() -> {
            try {
                router.removeMapping(Integer.parseInt(selectedPortMapping.getExternalPort()), selectedPortMapping.getHostname(), selectedPortMapping.getProtocol());
                Platform.runLater(() -> {
                    btnAddPort.setDisable(false);
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
        loader.setController(new AddPortMapping(tableView));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("UltraUPnP GUI - v" + UltraUPnP.VERSION + " - Add Port Mapping");
        stage.setScene(new Scene(root, 600, 320));
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
                //Log.info("RESPONSE: <" + ra.getArgName() + ">" + ra.getArgValue() + "</" + ra.getArgName() + ">");

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

    public static void main(String[] args){
        launch(args);
    }
}
