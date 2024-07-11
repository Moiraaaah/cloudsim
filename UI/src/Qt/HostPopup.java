package Qt;

import io.qt.gui.QIntValidator;
import io.qt.widgets.*;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.List;

public class HostPopup extends Popup {
    private Host host;
    private final int index;

    /**
     * Constructor of a host popup
     *
     * @param parent the parent of this QDialog
     * @param index the index of the host in the datacenter
     */
    public HostPopup(QWidget parent, int index) {
        super(parent, "Create host #" + (index+1), new String[]{"Number of PE", "MIPS", "RAM", "Storage", "Bandwidth"});
        this.index = index;

        QIntValidator intValidator = new QIntValidator();

        for (QLineEdit characteristic : characteristics) {
            //to ensure it can be casted to the selected type
            characteristic.setValidator(intValidator);
        }
    }

    /**
     * Creates a new host
     *
     * @param data the information contained in the interactive widgets of the popup
     */
    private void createHost(String[] data) {
        List<Pe> pes = new ArrayList<>();
        int mips = Integer.parseInt(data[1]);
        for(int i = 0; i < Integer.parseInt(data[0]); i++) {
            pes.add(new Pe(i, new PeProvisionerSimple(mips)));
        }
        int ram = Integer.parseInt(data[2]);
        long storage = Integer.parseInt(data[3]);
        int bw = Integer.parseInt(data[4]);
        host = new Host(index, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, pes, new VmSchedulerTimeShared(pes));
    }

    @Override
    protected void onAccept() {
        String[] data = new String[5];
        for (int i = 0; i < data.length; i++) {
            data[i] = characteristics[i].getText();
        }
        createHost(data);
    }

    /**
     * @return the host created with the popup
     */
    public Host getHost() {
        return host;
    }
}
