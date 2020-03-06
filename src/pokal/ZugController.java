package pokal;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pokal.model.Mitglied;
import pokal.model.Zug;
import pokal.pdf.PDFGenerator;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static javafx.scene.control.Alert.AlertType;

public class ZugController {
    private ZugListeController zugListeController;
    private Zug zug;

    @FXML
    public AnchorPane container;
    @FXML
    private Label nameLabel;
    @FXML
    private Label numLabel;
    @FXML
    public VBox detailView;
    @FXML
    public ImageView toggle;

    void init(Zug zug, ZugListeController zugListeController, AnchorPane pane) {
        this.zug = zug;
        this.zugListeController = zugListeController;
        nameLabel.setText(zug.getName());
        numLabel.setText(zug.getIdFormatted());
        container.prefWidthProperty().bind(pane.widthProperty());
    }

    public void delete(MouseEvent mouseEvent) {
        final Alert alert = new Alert(AlertType.CONFIRMATION, "Willst du diesen Zug wirklich löschen?");
        alert.setHeaderText("");
        alert.showAndWait()
             .filter(response -> response == ButtonType.OK)
             .ifPresent(response -> zugListeController.removeZug(zug));
        mouseEvent.consume();
    }

    public void expand(MouseEvent mouseEvent) throws IOException {
        mouseEvent.consume();
        if (detailView.getChildren().size() > 0) {
            detailView.getChildren().clear();
            toggle.setImage(new Image(getClass().getResourceAsStream("/assets/right-arrow.png")));
            return;
        }

        for (Mitglied mitglied : zug.getMitglieder()) {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("mitglied.fxml"));
            final Pane newLoadedPane = loader.load();
            final MitgliedController mitgliedController = loader.getController();
            mitgliedController.init(mitglied);
            detailView.getChildren().add(newLoadedPane);
            toggle.setImage(new Image(getClass().getResourceAsStream("/assets/down-arrow.png")));
        }
    }

    public void print(MouseEvent mouseEvent) {
        final File file;
        try {
            file = File.createTempFile("pokalschuss-", ".pdf");
            file.deleteOnExit();
            PDFGenerator.generateSingleZugPDF(file, zug);
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
            final Alert alert = new Alert(Alert.AlertType.ERROR, "Es ist ein unerwarteter Fehler aufgetreten!\nBitte versuche es nochmal.");
            alert.setHeaderText("");
            alert.showAndWait();
        }
        mouseEvent.consume();
    }
}
