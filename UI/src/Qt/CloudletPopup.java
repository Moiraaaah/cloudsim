package Qt;

import io.qt.gui.QIntValidator;
import io.qt.widgets.QLineEdit;
import io.qt.widgets.QWidget;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;

public class CloudletPopup extends Popup {
    private Cloudlet cloudlet;
    private final int id;

    /**
     * Constructor of a cloudlet popup
     *
     * @param parent the parent of this QDialog
     * @param id the id of the cloudlet
     */
    public CloudletPopup(QWidget parent, int id) {
        super(parent, "Create cloudlet", new String[]{"Length", "File size", "Output size", "Number of PE"});
        this.id = id;
        QIntValidator intValidator = new QIntValidator();

        for (QLineEdit characteristic : characteristics) {
            //to ensure it can be casted to the selected type
            characteristic.setValidator(intValidator);
        }
    }

    @Override
    protected void onAccept() {
        String[] data = new String[characteristics.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = characteristics[i].getText();
        }
        createCloudlet(data);
    }

    /**
     * Creates a cloudlet with the correct data
     * @param data the data from the different QLineEdit
     */
    private void createCloudlet(String[] data) {
        long length = Integer.parseInt(data[0]);
        long fileSize = Integer.parseInt(data[1]);
        long outputSize = Integer.parseInt(data[2]);
        int pesNumber = Integer.parseInt(data[3]);
        UtilizationModel utilizationModel = new UtilizationModelFull();

        cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
        cloudlet.setUserId(CreateSimulationWindow.brokerId);
    }

    /**
     * @return the cloudlet created with the popup
     */
    public Cloudlet getCloudlet() {
        return cloudlet;
    }
}
