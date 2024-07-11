package Qt;

import io.qt.widgets.*;

public abstract class Popup extends QDialog {
    protected final QDialogButtonBox saveButton;
    protected final QLineEdit[] characteristics;
    protected static QVBoxLayout layout;

    /**
     * Constructor of a generic Popup (with only QLineEdit)
     * @param parent the parent of this QDialog
     * @param title of the popup
     * @param labels displayed on the popup (each connected to a QLineEdit)
     */
    protected Popup(QWidget parent, String title, String[] labels) {
        super(parent);
        setWindowTitle(title);

        layout = new QVBoxLayout(this);

        characteristics = new QLineEdit[labels.length];

        for (int i = 0; i < characteristics.length; i++) {
            QLabel label = new QLabel(labels[i]);
            characteristics[i] = new QLineEdit(this);

            //to make sure every field is filled
            characteristics[i].textChanged.connect(this, "checkFields()");

            layout.addWidget(label);
            layout.addWidget(characteristics[i]);
        }

        saveButton = new QDialogButtonBox(QDialogButtonBox.StandardButton.Save.asFlags(), this);
        saveButton.setEnabled(false);
        layout.addWidget(saveButton);

        saveButton.accepted.connect(this, "accept()");
        this.accepted.connect(this::onAccept);
    }

    /**
     * Saves the data of each field and sends it to the correct function
     */
    protected abstract void onAccept();

    /**
     * Checks if every field is filled before enabling the save button
     */
    protected void checkFields() {
        boolean allFilled = true;
        for (QLineEdit characteristic : characteristics) {
            if (characteristic.text().isEmpty()) {
                allFilled = false;
                break;
            }
        }
        saveButton.setEnabled(allFilled);
    }
}
