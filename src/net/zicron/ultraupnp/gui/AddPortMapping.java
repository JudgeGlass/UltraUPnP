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


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.zicron.ultraupnp.Log;
import net.zicron.ultraupnp.Router;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddPortMapping {
    @FXML private ListView<String> saveList;
    @FXML private TextField txtHost;
    @FXML private TextField txtExternalPort;
    @FXML private TextField txtInternalPort;
    @FXML private TextField txtDescription;
    @FXML private CheckBox chkTCP;
    @FXML private CheckBox chkUDP;
    @FXML private Button btnAdd;
    @FXML private Button btnDelete;

    private TableView<PortMapping> tableView;
    private List<PortMapping> savedPortMappings;
    private Router router;

    public AddPortMapping(final TableView<PortMapping> tableView, Router router){
        this.tableView = tableView;
        this.router = router;
    }

    @FXML
    private void initialize() throws IOException {

        savedPortMappings = SaveData.getPortMappingsFromFile("ultraupnp-data.dat");
        if(savedPortMappings != null) {
            for (PortMapping p : savedPortMappings) {
                saveList.getItems().add(router.getExternalIPAddress() + ":" + p.getExternalPort() + " --> " + p.getHostname() + ":" + p.getInternalPort() + " Proto: " + p.getProtocol() + " Description: " + p.getDescription());
            }
        }

        txtHost.setText(Router.getInternalAddress());
    }

    @FXML
    private void selectMapping(){
        PortMapping portMapping = savedPortMappings.get(saveList.getSelectionModel().getSelectedIndex());
        chkUDP.setSelected(false);
        chkTCP.setSelected(false);
        txtHost.setText(portMapping.getHostname());
        txtInternalPort.setText(portMapping.getInternalPort());
        txtExternalPort.setText(portMapping.getExternalPort());
        txtDescription.setText(portMapping.getDescription());
        btnDelete.setDisable(false);
        if(portMapping.getProtocol().equals("both")){
            chkUDP.setSelected(true);
            chkTCP.setSelected(true);
        }else if(portMapping.getProtocol().equals(Router.TCP)){
            chkTCP.setSelected(true);
        }else{
            chkUDP.setSelected(true);
        }
    }

    @FXML
    private void deleteMapping(){
        int index = saveList.getSelectionModel().getSelectedIndex();
        savedPortMappings.remove(index);
        saveList.getItems().remove(index);

        StringBuilder stringBuilder = new StringBuilder();
        for(PortMapping p:  savedPortMappings){
            stringBuilder.append("PORT-ENTRY:");
            stringBuilder.append(p.getHostname()).append(":");
            stringBuilder.append(p.getProtocol()).append(":");
            stringBuilder.append(p.getExternalPort()).append(":");
            stringBuilder.append(p.getInternalPort()).append(":");
            stringBuilder.append(p.getDescription()).append(":");
            stringBuilder.append(p.getProtocol());

            stringBuilder.append("\n");
        }

        SaveData.writeFile("ultraupnp-data.dat", stringBuilder.toString());
    }

    @FXML
    private void add(){
        String hostname = txtHost.getText();
        String description = txtDescription.getText();
        String proto = (chkTCP.isSelected()) ? Router.TCP : Router.UDP;
        int externalPort = Integer.parseInt(txtExternalPort.getText());
        int internalPort = Integer.parseInt(txtInternalPort.getText());
        boolean bothProto = chkTCP.isSelected() && chkUDP.isSelected();

        if(savedPortMappings == null){
            savedPortMappings = new ArrayList<>();
        }
        savedPortMappings.add(new PortMapping(hostname, txtInternalPort.getText(), txtExternalPort.getText(), ((bothProto) ? "both" : proto), description));
        StringBuilder stringBuilder = new StringBuilder();
        for(PortMapping p:  savedPortMappings){

            stringBuilder.append("PORT-ENTRY:");
            stringBuilder.append(p.getHostname()).append(":");
            stringBuilder.append(p.getProtocol()).append(":");
            stringBuilder.append(p.getExternalPort()).append(":");
            stringBuilder.append(p.getInternalPort()).append(":");
            stringBuilder.append(p.getDescription()).append(":");
            stringBuilder.append(p.getProtocol());

            stringBuilder.append("\n");
        }

        SaveData.writeFile("ultraupnp-data.dat", stringBuilder.toString());

        Stage window = (Stage) btnAdd.getScene().getWindow();
        window.close();

        new Thread(() -> {
            try {
                if(!bothProto) {
                    router.portForward(internalPort, externalPort, hostname, proto, description);
                }else{
                    router.portForward(internalPort, externalPort, hostname, Router.TCP, description);
                    router.portForward(internalPort, externalPort, hostname, Router.UDP, description);
                }
            } catch (IOException e) {
                Log.error(e.getMessage());
                e.printStackTrace();
            }
            if(bothProto){
                tableView.getItems().add(new PortMapping(hostname, Integer.toString(internalPort), Integer.toString(externalPort), Router.TCP, description));
                tableView.getItems().add(new PortMapping(hostname, Integer.toString(internalPort), Integer.toString(externalPort), Router.UDP, description));
            }else{
                tableView.getItems().add(new PortMapping(hostname, Integer.toString(internalPort), Integer.toString(externalPort), proto, description));
            }
        }).start();
    }
}