package net.zicron.ultraupnp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.zicron.ultraupnp.RouterFinder;
import net.zicron.ultraupnp.UltraUPnP;

public class AdvancedWindow {

    @FXML private TextField txtSSDPPort;
    @FXML private TextField txtSSDPIP;
    @FXML private TextField txtTimeout;
    @FXML private CheckBox chkShowDebug;
    @FXML private Button btnOK;
    @FXML private Button btnCancel;


    @FXML
    public void initialize(){
        txtSSDPPort.setText(Integer.toString(RouterFinder.SSDP_PORT));
        txtSSDPIP.setText(RouterFinder.SSDP_IP);
        txtTimeout.setText(Integer.toString(RouterFinder.TIMEOUT));
        chkShowDebug.setSelected(UltraUPnP.IS_BETA);
    }

    @FXML
    private void cancel(){
        Stage window = (Stage) btnCancel.getScene().getWindow();
        window.close();
    }

    @FXML
    private void exit(){
        RouterFinder.SSDP_PORT = Integer.parseInt(txtSSDPPort.getText());
        RouterFinder.SSDP_IP = txtSSDPIP.getText();
        RouterFinder.TIMEOUT = Integer.parseInt(txtTimeout.getText());
        UltraUPnP.IS_BETA = chkShowDebug.isSelected();
        cancel();
    }
}
