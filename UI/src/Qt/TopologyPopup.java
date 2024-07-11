package Qt;

import io.qt.widgets.*;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.NetworkTopology;

import java.util.List;

public class TopologyPopup extends Popup {
    private final QComboBox listSrc;
    private final QComboBox listDst;

    /**
     * Constructor of a generic Popup
     *
     * @param parent the parent of this QDialog
     */
    protected TopologyPopup(QWidget parent, List<Datacenter> datacenters) {
        super(parent, "Create a topology", new String[]{"Bandwidth", "Latency"});

        //We add the elements to the layout in the opposite order
        listDst = new QComboBox();
        for(Datacenter datacenter : datacenters) {
            listDst.addItem(datacenter.getName());
            listDst.setItemData(listDst.count()-1, datacenter.getId());
        }
        layout.insertWidget(0,listDst);
        QLabel dstIdLabel = new QLabel("Destination");
        layout.insertWidget(0,dstIdLabel);

        listSrc = new QComboBox();
        for(Datacenter datacenter : datacenters) {
            listSrc.addItem(datacenter.getName());
        }
        layout.insertWidget(0,listSrc);

        QLabel srcIdLabel = new QLabel("Source");
        layout.insertWidget(0,srcIdLabel);
    }

    @Override
    protected void onAccept() {
        String[] data = new String[4];
        data[0] = (String) listSrc.itemData(listSrc.currentIndex());
        data[1] = (String) listDst.itemData(listDst.currentIndex());
        for(int i = 2; i < 4; i++) {
            data[i] = characteristics[i].getText();
        }
        createTopology(data);
    }

    /**
     * Creates a new topology between two datacenters
     *
     * @param data the information contained in the interactive widgets of the popup
     */
    private void createTopology(String[] data) {
        NetworkTopology.addLink(Integer.parseInt(data[0]),Integer.parseInt(data[1]),
                Double.parseDouble(data[2]), Double.parseDouble(data[3]));
    }
}
