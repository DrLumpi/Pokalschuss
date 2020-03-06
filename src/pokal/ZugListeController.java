package pokal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import pokal.model.Mitglied;
import pokal.model.Zug;
import pokal.pdf.PDFGenerator;
import pokal.persistence.SaveFileHandler;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class ZugListeController {
    private ArrayList<Zug> Zuege = new ArrayList<>();
    private int startNummer = 1;
    private File lastFile = null;

    private int getNextStartNumber() {
        final int ret = startNummer;
        startNummer += 10;
        return ret;
    }

    void removeZug(Zug z) {
        final int index = Zuege.indexOf(z);
        Zuege.remove(z);
        list.getChildren().remove(index);
    }

    @FXML
    public AnchorPane container;
    @FXML
    private VBox list;
    @FXML
    private TextField zugName;

    public void addZug(MouseEvent event) throws IOException {
        add();
        event.consume();
    }

    public void addZugByPressingEnter(KeyEvent event) throws IOException {
        if (event.getCode().equals(KeyCode.ENTER)) {
            add();
        }
    }

    private void add() throws IOException {
        final Zug add = new Zug(getNextStartNumber(), zugName.getText());
        zugName.setText("");
        Zuege.add(add);
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("zug.fxml"));
        final Pane newLoadedPane = loader.load();
        final ZugController zugController = loader.getController();
        zugController.init(add, this, container);
        list.getChildren().add(newLoadedPane);
    }

    public void evaluate(MouseEvent mouseEvent) {
        if (Zuege.size() == 0) {
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("PDF speichern");
        chooser.setInitialDirectory((lastFile != null && !lastFile.isDirectory()) ? lastFile.getParentFile() : lastFile);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF-Datei", "*.pdf"));
        File file = chooser.showSaveDialog(((Node) mouseEvent.getSource()).getScene().getWindow());

        final ArrayList<Zug> sort = new ArrayList<>(Zuege);
        sort.sort(Comparator.comparingInt(Zug::getScore).thenComparing(Zug::getZehner).reversed());
        Mitglied mitglied = null;
        Mitglied jungSchuetze = null;
        for (Zug z : sort) {
            final Mitglied best = z.getSortedMitglieder().get(0);
            if (mitglied == null || best.getShot() > mitglied.getShot()) {
                mitglied = best;
            }
            for (Mitglied m : z.getMitglieder()) {
                if ((jungSchuetze == null || m.getShot() > jungSchuetze.getShot()) && m.isYouth()) {
                    jungSchuetze = m;
                }
            }
        }

        try {
            PDFGenerator.generatePDF(file, sort, mitglied, jungSchuetze);
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
            final Alert alert = new Alert(Alert.AlertType.ERROR, "Es ist ein unerwarteter Fehler aufgetreten!\nBitte versuche es nochmal.");
            alert.setHeaderText("");
            alert.showAndWait();
        }
        mouseEvent.consume();
    }

    public void save(MouseEvent mouseEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Sichern");
        chooser.setInitialDirectory((lastFile != null && !lastFile.isDirectory()) ? lastFile.getParentFile() : lastFile);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup-Datei", "*.json"));
        File file = chooser.showSaveDialog(((Node) mouseEvent.getSource()).getScene().getWindow());
        if (file != null) {
            try {
                SaveFileHandler.save(file, Zuege);
                lastFile = file;
            } catch (Exception e) {
                e.printStackTrace();
                final Alert alert = new Alert(Alert.AlertType.ERROR, "Beim Speichern ist ein Fehler aufgetreten!\nBitte versuche es nochmal.");
                alert.setHeaderText("");
                alert.showAndWait();
            }
        }
    }

    public void load(MouseEvent mouseEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Backup laden");
        chooser.setInitialDirectory((lastFile != null && !lastFile.isDirectory()) ? lastFile.getParentFile() : lastFile);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup-Datei", "*.json"));
        File file = chooser.showOpenDialog(((Node) mouseEvent.getSource()).getScene().getWindow());

        if (file != null) {
            try {
                final ArrayList<Zug> res = SaveFileHandler.loadSaveFile(file);
                lastFile = file;
                list.getChildren().clear();
                startNummer = res.get(res.size() - 1).getId() + 10;
                Zuege = res;
                for (Zug zug : Zuege) {
                    final FXMLLoader loader = new FXMLLoader(getClass().getResource("zug.fxml"));
                    final Pane newLoadedPane = loader.load();
                    final ZugController zugController = loader.getController();
                    zugController.init(zug, this, container);
                    list.getChildren().add(newLoadedPane);
                }
            } catch (Exception e) {
                e.printStackTrace();
                final Alert alert = new Alert(Alert.AlertType.ERROR, "Beim Laden des Backups ist ein Fehler aufgetreten!\nBitte versuche es nochmal.");
                alert.setHeaderText("");
                alert.showAndWait();
            }
        }
    }
}
