package Qt;

import io.qt.gui.QDoubleValidator;
import io.qt.gui.QIntValidator;
import io.qt.widgets.*;
import io.qt.core.QLocale;
import org.cloudbus.cloudsim.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DatacenterPopup extends Popup {
    Datacenter datacenter;

    /**
     * Constructor of a datacenter popup
     *
     * @param parent the parent of this QDialog
     */
    public DatacenterPopup(QWidget parent) {
        super(parent, "Create datacenter", new String[]{"Name", "Number of hosts", "Architecture", "OS", "VMM", "Time Zone", "Cost", "Cost per mem", "Cost per storage", "Cost per bandwidth"});

        //to ensure it can be casted to the selected type
        QIntValidator intValidator = new QIntValidator();
        QDoubleValidator doubleValidator = new QDoubleValidator();
        doubleValidator.setLocale(QLocale.c()); //to write 0.1 instead of 0,1

        for (int i = 1; i < characteristics.length; i++) {
            //ensure the values can be casted to the right type
            if (i == 1) characteristics[i].setValidator(intValidator);
            else if (i >= 5) characteristics[i].setValidator(doubleValidator);
        }
    }

    /**
     * Creates a datacenter
     *
     * @param data the data from the different QLineEdit
     */
    private void createDatacenter(String[] data) {
        List<Host> hosts = new ArrayList<>();

        //create as many hosts as wanted
        for (int i = 0; i < Integer.parseInt(data[1]); i++) {
            HostPopup popupHost = new HostPopup(this, i);
            popupHost.exec();
            hosts.add(popupHost.getHost());
        }
        LinkedList<Storage> storageList = new LinkedList<>();
        String architecture = data[2];
        String OS = data[3];
        String VMM = data[4];
        double timeZone = Double.parseDouble(data[5]);
        double cost = Double.parseDouble(data[6]);
        double costPerMem = Double.parseDouble(data[7]);
        double costPerStorage = Double.parseDouble(data[8]);
        double costPerBandwidth = Double.parseDouble(data[9]);
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(architecture, OS, VMM, hosts, timeZone, cost, costPerMem, costPerStorage, costPerBandwidth);
        datacenter = null;
        try {
            datacenter = new Datacenter(data[0], characteristics, new VmAllocationPolicySimple(hosts), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAccept() {
        String[] data = new String[characteristics.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = characteristics[i].getText();
        }
        createDatacenter(data);
    }

    /**
     * @return the datacenter created with the popup
     */
    public Datacenter getDatacenter(){
        return datacenter;
    }

}
