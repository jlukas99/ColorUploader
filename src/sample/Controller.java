package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Controller {

    @FXML
    ChoiceBox kategoriaFoliBox, odcienBox, jezykBox;
    @FXML
    TextField databaseIPField, databaseAdres, databaseDB;
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

    private int foliaInt;
    private int odcienInt;

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

        if(checkConnection()) {
            if (databaseIPField.getText().isEmpty() || databasePasswdField.getText().isEmpty()) {
                progressBar.setStyle("-fx-background-color: #ff5042");
                progressText.setText("Error: wpisz adres i hasło do bazy danych!");
                return;
            }

            if (kategoriaFoliBox.getValue() == null || odcienBox.getValue() == null || jezykBox == null) {
                progressBar.setStyle("-fx-background-color: #ff5042");
                progressText.setText("Error: wybierz wszystkie opcje");
                return;
            }

            if (colorFolderText.getText().equals("Folder z kolorami...")) {
                progressBar.setStyle("-fx-background-color: #ff5042");
                progressText.setText("Error: wybierz folder z kolorami");
                return;
            }

                progressBar.setStyle("-fx-background-color: #bf6400");
                progressText.setText("Wysyłanie, prosze czekac...");

                foliaString = kategoriaFoliBox.getValue().toString();
                odcienString = odcienBox.getValue().toString();
                jezykString = jezykBox.getValue().toString();

                switch (foliaString) {
                    case "Folie DC / DC Foils / DC folie / DC-Folien":
                        foliaString = "Folie DC";
                        foliaInt = 1;
                        break;
                    case "Folie DM / DM Foils / DM folie / DM-Folien":
                        foliaString = "Folie DM";
                        foliaInt = 2;
                        break;
                    case "Folie UN/PT / UN/PT Foils / UN/PT folie / UN/PT-Folien":
                        foliaString = "Folie UN_PT";
                        foliaInt = 3;
                        break;
                    case "Folie Półpołysk UN / UN semi gloss / UN pololeskl� folie / UN halbgl�nzende Folien":
                        foliaString = "Folie Polpolysk UN";
                        foliaInt = 4;
                        break;
                    case "Folie HG / HG high gloss / HG vysok� lesk / HG Hochglanz":
                        foliaString = "Folie HG";
                        foliaInt = 5;
                        break;
                    case "Lakier RAL / RAL lacquers / RAL laky / RAL-Lacke":
                        foliaString = "Folie RAL";
                        foliaInt = 6;
                        break;
                    case "Lakier NCS / NCS lacquers / NCS laky / NCS-Lacke":
                        foliaString = "Folie NCS";
                        foliaInt = 7;
                        break;
                }

            switch (odcienString) {
                case "Jasny":
                    odcienInt = 1;
                    break;
                case "Średni":
                    odcienInt = 2;
                    break;
                case "Ciemny":
                    odcienInt = 3;
                    break;
            }

                for (File f : fileList) {

                    switch (jezykString) {
                        case "PL, EN":
                            pl = f.getName().replace(".png", "");
                            en = f.getName().replace(".png", "");
                            setDatabase(f, foliaInt, pl, en, cz, de, f.getName(), odcienInt, 1, f.getName());
                            break;
                        case "PL, EN, CZ":
                            pl = f.getName().replace(".png", "");
                            en = f.getName().replace(".png", "");
                            cz = f.getName().replace(".png", "");
                            setDatabase(f, foliaInt, pl, en, cz, de, f.getName(), odcienInt, 2, f.getName());
                            break;
                        case "CZ":
                            cz = f.getName().replace(".png", "");
                            setDatabase(f, foliaInt, pl, en, cz, de, f.getName(), odcienInt, 3, f.getName());
                            break;
                        case "PL, EN, CZ, DE":
                            pl = f.getName().replace(".png", "");
                            en = f.getName().replace(".png", "");
                            cz = f.getName().replace(".png", "");
                            de = f.getName().replace(".png", "");
                            setDatabase(f, foliaInt, pl, en, cz, de, f.getName(), odcienInt, 4, f.getName());
                            break;
                    }
                }

                progressBar.setStyle("-fx-background-color: #6bd500");
                progressText.setText("Wysyłanie ukończono!");
        } else {
            progressBar.setStyle("-fx-background-color: #ff5042");
            progressText.setText("Nie ma połącenia z bazą danych, sprawdź login i hasło");
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

    private void upload(File f, String jezyk, String folia, String odcien, int i){

        path = new File("C:\\database\\" + jezyk + "\\" + folia + "\\" + odcien);
        path.mkdirs();

        File dir = new File(path + "\\" + i + ".png");

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

    private boolean checkConnection(){

        String polaczenieURL = "jdbc:mysql://" + databaseAdres.getText() + "/" + databaseDB.getText() + "?user=" + databaseIPField.getText() + "&password=" + databasePasswdField.getText() + "&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        Connection conn;

        try {

            conn = DriverManager.getConnection(polaczenieURL);

            conn.close();

            return true;
        }

        catch(SQLException wyjatek) {
            System.out.println("SQLException: " + wyjatek.getMessage());
            System.out.println("SQLState: " + wyjatek.getSQLState());
            System.out.println("VendorError: " + wyjatek.getErrorCode());

            return false;
        }
    }

    public void setDatabase(File f, int categoryId, String namePL, String nameEN, String nameCZ, String nameDE, String folia, int odcien, int languageType, String sort){
        try {

            String url = "jdbc:mysql://" + databaseAdres.getText() + "/" + databaseDB.getText() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

            Connection conn = DriverManager.getConnection(url,databaseIPField.getText(),databasePasswdField.getText());

            Statement st = conn.createStatement();

            String count = "SELECT MAX(`foliaId`) FROM folie";

            ResultSet rs = st.executeQuery(count);

            rs.next();
            String foundType = rs.getString(1);

            System.out.println(foundType);

            int i = Integer.parseInt(foundType) + 1;

            String sql = "INSERT INTO folie (categoryId, nameOLD, namePL, nameEN, nameCZ, nameDE, nameDA, shortNameOLD, shortNamePL, shortNameEN, shortNameCZ, shortNameDE, shortNameDA, folia, odcien, languageType, new, sorting) VALUES ('" + categoryId + "','','" + namePL + "','" + nameEN + "','" + nameCZ +
                    "','" + nameDE + "','','','" + namePL + "','" + nameEN + "','" + nameCZ + "','" + nameDE + "','','" + i + ".png" + "','" + odcien + "','" + languageType + "','" + 0 + "','" + i + "')";

            st.executeUpdate(sql);

            upload(f, jezykString, foliaString, odcienString, i);

            conn.close();
        }

        catch(SQLException wyjatek) {
            System.out.println("SQLException: " + wyjatek.getMessage());
            System.out.println("SQLState: " + wyjatek.getSQLState());
            System.out.println("VendorError: " + wyjatek.getErrorCode());
        }
    }
}
