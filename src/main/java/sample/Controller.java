package sample;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.sun.glass.ui.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    boolean is_auto_copy, is_fast_translate, is_save_file,is_hide_toolbar,is_open_startup;
    String shortcut="Ctrl + B",FromLang="English",ToLang="Vietnamese",fileLocation=(Paths.get("").toAbsolutePath().toString());
    KeyCombination kb = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);
    String dataloc = (Paths.get("").toAbsolutePath().toString()+"\\text\\saved.txt");
    @FXML
    private AnchorPane Container;

    @FXML
    private AnchorPane toolBar;

    @FXML
    private ImageView btn_settings;

    @FXML
    private ImageView btn_minimize;

    @FXML
    private ImageView btn_close;

    @FXML
    private AnchorPane SettingBox;

    @FXML
    private JFXToggleButton saveToggle;

    @FXML
    private JFXButton btn_browse;

    @FXML
    private TextField fileField;


    @FXML
    private JFXToggleButton hideToogle;

    @FXML
    private JFXToggleButton owwToggle;

    @FXML
    private AnchorPane MainBox;

    @FXML
    private ImageView btn_OCR;

    @FXML
    private JFXComboBox<String> Sourcelang;

    @FXML
    private JFXComboBox<String> Destlang;

    @FXML
    private JFXToggleButton autoToggle;


    @FXML
    private JFXToggleButton ftransToggle;



    @FXML
    private void handleButtonAction(MouseEvent event) throws IOException {
        if(event.getTarget()==btn_settings) {
            if(SettingBox.isVisible()){
                SettingBox.setVisible(false);
                MainBox.setVisible(true);
            } else{
                MainBox.setVisible(false);
                SettingBox.setVisible(true);
            }
        }
        else if(event.getTarget()==btn_minimize){
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setIconified(true);
        }
        else if(event.getTarget()==btn_close){
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            BufferedWriter out = new BufferedWriter(new FileWriter(new File(dataloc)));
            out.write(is_auto_copy+ "," +is_fast_translate+ "," +is_save_file+ "," +is_hide_toolbar+ "," +is_open_startup+ "," +shortcut+ "," +FromLang+ "," +ToLang+ "," +fileLocation);
            out.close();
            Platform.exit();
        }
    }

    @FXML
    public  void fileBrowser(MouseEvent event){
        final DirectoryChooser dirchosen = new DirectoryChooser();
        File file = dirchosen.showDialog((Stage)Container.getScene().getWindow());
        if(file !=null){
            fileLocation = file.getAbsolutePath();
            fileField.setText(fileLocation);
        //    System.out.println(is_auto_copy + " \n " + fileLocation);
        }
    }


    @FXML
    private void startOCR(MouseEvent event) throws AWTException, IOException, InterruptedException {
        Stage cur_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("OCRR.fxml"));
        Parent root = loader.load();
        OCRRController ocrcontroller = loader.getController();
        Scene cn = new Scene(root);
        Scene cur_scene = cur_stage.getScene();
        cn.setFill(Color.TRANSPARENT);
        cn.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.ESCAPE) {
                try {
                    ocrcontroller.Close(cur_stage,cur_scene);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        cur_stage.hide();
        Robot bot = new Robot();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final BufferedImage screen = bot.createScreenCapture(new Rectangle(screenSize));
        cur_stage.setScene(cn);
        ocrcontroller.handleOCR(screen,is_auto_copy,is_save_file,is_fast_translate,fileLocation,FromLang,ToLang);
        cur_stage.setMaximized(true);
        cur_stage.show();





    }
    public void handleOn(KeyEvent keyEvent) {
        if(kb.match(keyEvent)){
            Stage cur_stage = (Stage) Container.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OCRR.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            OCRRController ocrcontroller = loader.getController();
            Scene cn = new Scene(root);
            Scene cur_scene = cur_stage.getScene();
            cn.setFill(Color.TRANSPARENT);
            cn.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                if(e.getCode() == KeyCode.ESCAPE) {
                    try {
                        ocrcontroller.Close(cur_stage,cur_scene);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });
            cur_stage.hide();
            Robot bot = null;
            try {
                bot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
            final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            assert bot != null;
            final BufferedImage screen = bot.createScreenCapture(new Rectangle(screenSize));
            cur_stage.setScene(cn);
            try {
                ocrcontroller.handleOCR(screen,is_auto_copy,is_save_file,is_fast_translate,fileLocation,FromLang,ToLang);
            } catch (AWTException e) {
                e.printStackTrace();
            }
            cur_stage.setMaximized(true);
            cur_stage.show();
        }
    }

    @Override
    public void initialize (URL url, ResourceBundle rb){

        try {
            BufferedReader br=new BufferedReader(new FileReader(dataloc));
            String[] str = br.readLine().split(",",12);
            is_auto_copy = Boolean.parseBoolean(str[0]);
            is_fast_translate = Boolean.parseBoolean(str[1]);
            is_save_file = Boolean.parseBoolean(str[2]);
            is_hide_toolbar = Boolean.parseBoolean(str[3]);
            is_open_startup = Boolean.parseBoolean(str[4]);
            shortcut = str[5];
            FromLang = str[6];
            ToLang = str[7];
            fileLocation = str[8];
         //   System.out.println(is_auto_copy+ ","+is_fast_translate+ ","+is_save_file+ ","+is_hide_toolbar+ ","+is_open_startup+ ","+shortcut+ ","+FromLang+ ","+ToLang+ ","+fileLocation);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sourcelang.getItems().addAll("English","Vietnamese","Japanese","Chinese","Korean","Russian");
        Sourcelang.getSelectionModel().select(FromLang);
        Sourcelang.getButtonCell().setStyle("-fx-text-fill: #ffd52e;");
        Sourcelang.setOnAction(event ->{
            FromLang = Sourcelang.getSelectionModel().getSelectedItem();
            switch (FromLang){
                case "English":
                    FromLang = "eng";
                    break;
                case "Vietnamese":
                    FromLang = "vie";
                    break;
                case "Japanese":
                    FromLang = "jpn";
                    break;
                case "Chinese":
                    FromLang = "chi_sim";
                    break;
                case "Korean":
                    FromLang = "kor";
                    break;
                case "Russian":
                    FromLang = "rus";
                    break;
            }
        });
        Destlang.getItems().addAll("English","Vietnamese","Japanese","Chinese","Korean","Russian");
        Destlang.getSelectionModel().select(ToLang);
        Destlang.getButtonCell().setStyle("-fx-text-fill: #ffd52e;");
        Destlang.setOnAction(event ->{
            ToLang = Destlang.getSelectionModel().getSelectedItem();
            switch (ToLang){
                case "English":
                    ToLang = "eng";
                    break;
                case "Vietnamese":
                    ToLang = "vie";
                    break;
                case "Japanese":
                    ToLang = "jpn";
                    break;
                case "Chinese":
                    ToLang = "chi_sim";
                    break;
                case "Korean":
                    ToLang = "kor";
                    break;
                case "Russian":
                    ToLang = "rus";
                    break;
            }
        });
        ftransToggle.setSelected(is_fast_translate);
        autoToggle.setSelected(is_auto_copy);
        saveToggle.setSelected(is_save_file);
        hideToogle.setSelected(is_hide_toolbar);
        owwToggle.setSelected(is_open_startup);
        fileField.setText(fileLocation);
        if(is_save_file){
            fileField.setDisable(false);
            btn_browse.setDisable(false);
        }

        ftransToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    is_fast_translate = ftransToggle.isSelected();
            }
        });
        autoToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                is_auto_copy = autoToggle.isSelected();
            }
        });
        saveToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                is_save_file = saveToggle.isSelected();
                if(is_save_file){
                    fileField.setDisable(false);
                    btn_browse.setDisable(false);
                }else{
                    fileField.setDisable(true);
                    btn_browse.setDisable(true);
                }
            }
        });
        hideToogle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                is_hide_toolbar = hideToogle.isSelected();
            }
        });
        owwToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                is_open_startup = owwToggle.isSelected();
            }
        });

    }
}
