package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Controller {

    @FXML
    ChoiceBox kategoriaFoliBox, odcienBox, jezykBox;
    @FXML
    TextField databaseIPField;
    @FXML
    PasswordField databasePasswdField;
    @FXML
    Button colorFolderButton;
    @FXML
    Label colorFolderText, progressText;
    @FXML
    ProgressBar progressBar;

    private ArrayList files;
    private File[] fileList;

    private String pl;
    private String en;
    private String cz;
    private String de;

    private String foliaString;
    private String odcienString;
    private String jezykString;

    private File path;


    ObservableList<String> folie = FXCollections
            .observableArrayList(
                    "Folie DC / DC Foils / DC folie / DC-Folien",
                    "Folie DM / DM Foils / DM folie / DM-Folien",
                    "Folie UN/PT / UN/PT Foils / UN/PT folie / UN/PT-Folien",
                    "Folie Półpołysk UN / UN semi gloss / UN pololeskl� folie / UN halbgl�nzende Folien",
                    "Folie HG / HG high gloss / HG vysok� lesk / HG Hochglanz",
                    "Lakier RAL / RAL lacquers / RAL laky / RAL-Lacke",
                    "Lakier NCS / NCS lacquers / NCS laky / NCS-Lacke");

    ObservableList<String> odcien = FXCollections
            .observableArrayList("Jasny", "Średni", "Ciemny");

    ObservableList<String> jezyk = FXCollections
            .observableArrayList("PL, EN", "PL, EN, CZ", "CZ", "PL, EN, CZ, DE");

    @FXML
    private void loadBox(){
        kategoriaFoliBox.setItems(folie);
        odcienBox.setItems(odcien);
        jezykBox.setItems(jezyk);
    }

    @FXML
    private void checkData(){
        if(databaseIPField.getText().isEmpty() || databasePasswdField.getText().isEmpty()){
            progressBar.setStyle("-fx-background-color: #ff5042");
            progressText.setText("Error: wpisz adres i hasło do bazy danych!");
            return;
        }

        if(kategoriaFoliBox.getValue() == null || odcienBox.getValue() == null || jezykBox == null){
            progressBar.setStyle("-fx-background-color: #ff5042");
            progressText.setText("Error: wybierz wszystkie opcje");
            return;
        }

        if(colorFolderText.getText().equals("Folder z kolorami...")){
            progressBar.setStyle("-fx-background-color: #ff5042");
            progressText.setText("Error: wybierz folder z kolorami");
            return;
        }

        if(databasePasswdField.getText().equals("admin") && databaseIPField.getText().equals("admin")){

            progressBar.setStyle("-fx-background-color: #bf6400");
            progressText.setText("Wysyłanie, prosze czekac...");

            foliaString = kategoriaFoliBox.getValue().toString();
            odcienString = odcienBox.getValue().toString();
            jezykString = jezykBox.getValue().toString();

            switch (foliaString) {
                case "Folie DC / DC Foils / DC folie / DC-Folien":
                    foliaString = "Folie DC";
                    break;
                case "Folie DM / DM Foils / DM folie / DM-Folien":
                    foliaString = "Folie DM";
                    break;
                case "Folie UN/PT / UN/PT Foils / UN/PT folie / UN/PT-Folien":
                    foliaString = "Folie UN_PT";
                    break;
                case "Folie Półpołysk UN / UN semi gloss / UN pololeskl� folie / UN halbgl�nzende Folien":
                    foliaString = "Folie Polpolysk UN";
                    break;
                case "Folie HG / HG high gloss / HG vysok� lesk / HG Hochglanz":
                    foliaString = "Folie HG";
                    break;
                case "Lakier RAL / RAL lacquers / RAL laky / RAL-Lacke":
                    foliaString = "Folie RAL";
                    break;
                case "Lakier NCS / NCS lacquers / NCS laky / NCS-Lacke":
                    foliaString = "Folie NCS";
                    break;
            }

            for (File f : fileList){

                switch (jezykString){
                    case "PL, EN":
                        pl = f.getName().replace(".png", "");
                        en = f.getName().replace(".png", "");
                        upload(f, "PL", foliaString, odcienString);
                        upload(f, "EN", foliaString, odcienString);
                        break;
                    case "PL, EN, CZ":
                        pl = f.getName().replace(".png", "");
                        en = f.getName().replace(".png", "");
                        cz = f.getName().replace(".png", "");
                        upload(f, "PL", foliaString, odcienString);
                        break;
                    case "CZ":
                        cz = f.getName().replace(".png", "");
                        upload(f, "PL", foliaString, odcienString);
                        upload(f, "EN", foliaString, odcienString);
                        upload(f, "CZ", foliaString, odcienString);
                        break;
                    case "PL, EN, CZ, DE":
                        pl = f.getName().replace(".png", "");
                        en = f.getName().replace(".png", "");
                        cz = f.getName().replace(".png", "");
                        de = f.getName().replace(".png", "");
                        upload(f, "PL", foliaString, odcienString);
                        upload(f, "EN", foliaString, odcienString);
                        upload(f, "CZ", foliaString, odcienString);
                        upload(f, "DE", foliaString, odcienString);
                        break;
                }

                System.out.println("INFO:\n" + "Folia: " + foliaString + "\nOdcien: " + odcienString + "\nJezyk: " + jezykString + "\nPL: " + pl + "\nEN: " + en + "\nCZ: " + cz + "\nDE: " + de);
            }

            progressBar.setStyle("-fx-background-color: #6bd500");
            progressText.setText("Wysyłanie ukończono!");
        } else {
            progressBar.setStyle("-fx-background-color: #ff5042");
            progressText.setText("Error: błędne Hasło lub login");
        }
    }

    @FXML
    private void folderChoice(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        if(selectedDirectory == null){
            progressBar.setStyle("-fx-background-color: #ff5042");
            progressText.setText("Error: wybierz folder z kolorami");
        }else{

            files = new ArrayList<File>();

            File repo = new File (selectedDirectory.getAbsolutePath());

            fileList = repo.listFiles();

            progressBar.setStyle("-fx-background-color: #d0e4ff");
            progressText.setText("");
            colorFolderText.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void upload(File f, String jezyk, String folia, String odcien){

        path = new File("C:\\database\\" + jezyk + "\\" + folia + "\\" + odcien);
        path.mkdirs();

        File dir = new File(path + "\\" + f.getName());

        if(dir.exists()){
            progressBar.setStyle("-fx-background-color: #ff5042");
            progressText.setText("Error: Ten plik już istnieje");
        } else {
            try {
                System.out.println(f.getAbsolutePath());
                System.out.println(dir.getAbsolutePath());
                Files.copy(f.toPath(), dir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
