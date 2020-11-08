package net.zicron.ultraupnp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.zicron.ultraupnp.Router;

public class AddPortMapping {
    @FXML private ListView saveList;
    @FXML private TextField txtHost;
    @FXML private TextField txtExternalPort;
    @FXML private TextField txtInternalPort;
    @FXML private TextField txtDescription;
    @FXML private CheckBox chkTCP;
    @FXML private CheckBox chkUDP;
    @FXML private Button btnAdd;

    private TableView<PortMapping> tableView;
    private PortMapping portMapping;

    public AddPortMapping(final TableView<PortMapping> tableView){
        this.tableView = tableView;
    }

    @FXML
    private void initialize(){

    }

    @FXML
    private void add(){
        String hostname = txtHost.getText();
        String description = txtDescription.getText();
        String proto = (chkTCP.isSelected()) ? Router.TCP : Router.UDP;
        int externalPort = Integer.parseInt(txtExternalPort.getText());
        int internalPort = Integer.parseInt(txtInternalPort.getText());
        boolean bothProto = chkTCP.isSelected() && chkUDP.isSelected();
    }
}
