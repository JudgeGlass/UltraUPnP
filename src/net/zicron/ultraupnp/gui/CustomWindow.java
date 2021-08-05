package net.zicron.ultraupnp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.zicron.ultraupnp.Log;
import net.zicron.ultraupnp.Router;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomWindow {
    private Router router;


    @FXML private TextArea txtArgs;
    @FXML private TextField txtCommand;

    public CustomWindow(final Router router){
        this.router = router;
    }

    @FXML
    private void send(){
        String action = txtCommand.getText().trim();

        String[] lines = txtArgs.getText().split("\n");
        List<Router.RouterArgument> routerArguments = new ArrayList<>();
        for(String t: lines){
            String arg = t.substring(1, t.indexOf('>'));
            String value = t.substring(t.indexOf('>') + 1, t.lastIndexOf('<'));

            routerArguments.add(new Router.RouterArgument(arg, value));
        }

        try {
            List<Router.RouterArgument> routerResponse = router.sendCommand(action, routerArguments);
            for(Router.RouterArgument ra: routerResponse){
                Log.info("<" + ra.getArgName() + ">" + ra.getArgValue() + "</" + ra.getArgName() + ">");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        cancel();
    }

    @FXML
    private void cancel(){
        Stage window = (Stage) txtArgs.getScene().getWindow();
        window.close();
    }

}
