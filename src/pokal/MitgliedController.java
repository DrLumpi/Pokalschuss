package pokal;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import pokal.model.Mitglied;

public class MitgliedController {
    private Mitglied mitglied;

    @FXML
    public Label numLabel;
    @FXML
    public TextField nameField;
    @FXML
    public ImageView nameToggle;
    @FXML
    public TextField scoreField;
    @FXML
    public ImageView scoreToggle;
    @FXML
    public CheckBox youthToggle;

    void init(final Mitglied mitglied) {
        this.mitglied = mitglied;
        numLabel.setText(String.format("%03d", mitglied.getId()));
        if (!mitglied.getName().equals("PLACEHOLDER")) {
            nameField.setText(mitglied.getName());
        }
        if (mitglied.getShot() != -1) {
            scoreField.setText(String.valueOf(mitglied.getShot()));
        }
        if (mitglied.isYouth()) {
            youthToggle.setSelected(true);
        }
    }

    public void toggleEditName(MouseEvent mouseEvent) {
        if (nameField.isDisabled()) {
            nameField.setDisable(false);
            nameToggle.setImage(new Image(getClass().getResourceAsStream("/assets/tick.png")));
        } else {
            if (nameField.getText() != null && !nameField.getText().equals("")) {
                mitglied.setName(nameField.getText());

            }
            nameField.setDisable(true);
            nameToggle.setImage(new Image(getClass().getResourceAsStream("/assets/edit.png")));
        }
        if (mitglied.getName().equals("PLACEHOLDER")) {
            nameField.setText(null);
        }
        mouseEvent.consume();
    }

    public void toggleEdit(MouseEvent mouseEvent) {
        if (scoreField.isDisabled()) {
            scoreField.setDisable(false);
            scoreToggle.setImage(new Image(getClass().getResourceAsStream("/assets/tick.png")));
        } else {
            int val;
            try {
                val = Integer.valueOf(scoreField.getText());
            } catch (NumberFormatException e) {
                val = -1;
            }
            mitglied.setShot(val);
            scoreField.setDisable(true);
            scoreToggle.setImage(new Image(getClass().getResourceAsStream("/assets/edit.png")));
        }
        if (mitglied.getShot() == -1) {
            scoreField.setText(null);
        }
        mouseEvent.consume();
    }

    public void toggleYouth(MouseEvent mouseEvent) {
        mitglied.setYouth(!mitglied.isYouth());
        mouseEvent.consume();
    }
}
