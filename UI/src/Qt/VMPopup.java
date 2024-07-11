package Qt;

import io.qt.gui.QIntValidator;
import io.qt.widgets.QCheckBox;
import io.qt.widgets.QHBoxLayout;
import io.qt.widgets.QWidget;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Vm;

public class VMPopup extends Popup {
    private Vm vm;
    private final int id;
    private final QCheckBox time;
    private final QCheckBox space;

    public VMPopup(QWidget parent, int id) {
        super(parent, "Create VM", new String[]{"MIPS", "Size", "RAM", "Bandwidth", "PE number", "VMM"});
        this.id = id;
        QIntValidator intValidator = new QIntValidator();

        for (int i = 0; i < characteristics.length - 1; i++) {
            characteristics[i].setValidator(intValidator);
        }
        //Time/space shared shouldn't be a QLineEdit so can't be created with the abstract class
        time = new QCheckBox("Time shared", this);
        time.setChecked(true);
        space = new QCheckBox("Space shared", this);

        QHBoxLayout layout = new QHBoxLayout();
        layout.addWidget(time);
        layout.addWidget(space);

        //the QCheckBoxes are under the save button
        Popup.layout.addLayout(layout);

        time.stateChanged.connect(this, "onCheckBoxStateChanged()");
        space.stateChanged.connect(this, "onCheckBoxStateChanged()");
    }

    private void onCheckBoxStateChanged() {
        if (space.isChecked() && time.isChecked()) {
            QCheckBox sender = (QCheckBox) this.sender();
            if (sender == time) {
                space.setChecked(false);
            } else {
                time.setChecked(false);
            }
        }
    }

    @Override
    protected void onAccept() {
        String[] data = new String[characteristics.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = characteristics[i].getText();
        }
        createVM(data);
    }

    /**
     * Creates a new VM
     *
     * @param data the information contained in the interactive widgets of the popup
     */
    private void createVM(String[] data) {
        int mips = Integer.parseInt(data[0]);
        long size = Long.parseLong(data[1]);
        int ram = Integer.parseInt(data[2]);
        long bw = Integer.parseInt(data[3]);
        int pesNumber = Integer.parseInt(data[4]);
        String vmm = data[5];
        if (time.isChecked()) {
            vm = new Vm(id, CreateSimulationWindow.brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
        } else {
            vm = new Vm(id, CreateSimulationWindow.brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
        }
    }

    /**
     * @return the VM created with the popup
     */
    public Vm getVm() {
        return vm;
    }
}
