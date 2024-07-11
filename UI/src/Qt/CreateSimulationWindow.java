package Qt;

import io.qt.core.Qt;
import io.qt.widgets.*;
import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateSimulationWindow extends QWidget {
    private final List<Datacenter> datacenters;
    private final List<Cloudlet> cloudlets;
    private final List<Vm> vms;
    private static DatacenterBroker broker;
    static int brokerId;

    static QTextEdit results;
    private final QListWidget datacenterList;
    private final QListWidget cloudletList;
    private final QListWidget vmList;

    /**
     * Constructor of the CreateSimulationWindow
     *
     * @param parent the parent of this widget
     */
    public CreateSimulationWindow(QWidget parent) {
        super(parent);

        //Initialise variables and simulation
        CloudSim.init(1, Calendar.getInstance(), false);
        broker = createBroker();
        brokerId = broker.getId();
        cloudlets = new ArrayList<>();
        vms = new ArrayList<>();
        datacenters = new ArrayList<>();

        QWidget centralWidget = new QWidget(this);

        QHBoxLayout mainLayout = new QHBoxLayout(centralWidget);

        //Left part of the layout buttons to create objects
        QVBoxLayout verticalLayout = new QVBoxLayout();
        QPushButton addDC = new QPushButton("Create datacenter");
        verticalLayout.addWidget(addDC);
        addDC.clicked.connect(this, "showDC()");

        QPushButton addCloudlet = new QPushButton("Create cloudlet");
        verticalLayout.addWidget(addCloudlet);
        addCloudlet.clicked.connect(this, "showCloudlet()");

        QPushButton addVM = new QPushButton("Create VM");
        verticalLayout.addWidget(addVM);
        addVM.clicked.connect(this, "showVM()");

        QPushButton addTopology = new QPushButton("Create topology");
        verticalLayout.addWidget(addTopology);
        addTopology.clicked.connect(this, "showTopology()");

        QPushButton startSim = new QPushButton("Start simulation");
        verticalLayout.addWidget(startSim);
        startSim.clicked.connect(this, "startSim()");

        //Right part to see the objects created
        QVBoxLayout listLayout = new QVBoxLayout();
        QLabel dc = new QLabel("Datacenters");
        listLayout.addWidget(dc);
        datacenterList = new QListWidget();
        listLayout.addWidget(datacenterList);
        QHBoxLayout buttonDCLayout = new QHBoxLayout();
        //TODO: find a way to delete a data center for real
        //QPushButton deleteDC = new QPushButton("Delete datacenter");
        //deleteDC.clicked.connect(this, "deleteDC()");
        //buttonDCLayout.addWidget(deleteDC);
        QPushButton duplicateDC = new QPushButton("Duplicate datacenter");
        duplicateDC.clicked.connect(this, "duplicateDC()");
        buttonDCLayout.addWidget(duplicateDC);
        listLayout.addLayout(buttonDCLayout);

        QLabel cloudlet = new QLabel("Cloudlets");
        listLayout.addWidget(cloudlet);
        cloudletList = new QListWidget();
        listLayout.addWidget(cloudletList);
        QHBoxLayout buttonsCloudletLayout = new QHBoxLayout();
        QPushButton deleteCloudlet = new QPushButton("Delete cloudlet");
        deleteCloudlet.clicked.connect(this, "deleteCloudlet()");
        buttonsCloudletLayout.addWidget(deleteCloudlet);
        QPushButton duplicateCloudlet = new QPushButton("Duplicate cloudlet");
        duplicateCloudlet.clicked.connect(this, "duplicateCloudlet()");
        buttonsCloudletLayout.addWidget(duplicateCloudlet);
        listLayout.addLayout(buttonsCloudletLayout);

        QLabel vm = new QLabel("VMs");
        listLayout.addWidget(vm);
        vmList = new QListWidget();
        listLayout.addWidget(vmList);
        QHBoxLayout buttonsVMLayout = new QHBoxLayout();
        QPushButton deleteVM = new QPushButton("Delete VM");
        deleteVM.clicked.connect(this, "deleteVM()");
        buttonsVMLayout.addWidget(deleteVM);
        QPushButton duplicateVM = new QPushButton("Duplicate VM");
        duplicateVM.clicked.connect(this, "duplicateVM()");
        buttonsVMLayout.addWidget(duplicateVM);
        listLayout.addLayout(buttonsVMLayout);

        //TODO: remove this and put it in an another class/widget
        QLabel resLabel = new QLabel("Results");
        listLayout.addWidget(resLabel);
        results = new QTextEdit();
        listLayout.addWidget(results);

        //Add the different layouts to the main one
        QWidget leftPart = new QWidget(this);
        leftPart.setLayout(verticalLayout);
        mainLayout.addWidget(leftPart, 1);

        QWidget rightPart = new QWidget(this);
        rightPart.setLayout(listLayout);
        mainLayout.addWidget(rightPart, 1);

        setLayout(mainLayout);
    }

    /**
     * Creates a popup to create a new Datacenter
     */
    private void showDC() {
        DatacenterPopup popupDC = new DatacenterPopup(this);
        popupDC.exec();
        datacenters.add(popupDC.getDatacenter());
        updateListWidget(datacenterList, datacenters);
    }

    /**
     * Creates a popup to create a new Cloudlet
     */
    private void showCloudlet() {
        CloudletPopup popupCloudlet = new CloudletPopup(this, cloudlets.size());
        popupCloudlet.exec();
        cloudlets.add(popupCloudlet.getCloudlet());
        updateListWidget(cloudletList, cloudlets);
    }

    /**
     * Creates a popup to create a new VM
     */
    private void showVM() {
        VMPopup popupVM = new VMPopup(this, vms.size());
        popupVM.exec();
        vms.add(popupVM.getVm());
        updateListWidget(vmList, vms);
    }

    /**
     * Creates a popup to create a new Topology
     */
    private void showTopology(){
        TopologyPopup popupTopo = new TopologyPopup(this, datacenters);
        popupTopo.exec();
        //TODO: add the topology to a QListWidget to display it
    }

    /**
     * Updates the lists to display the objects created
     *
     * @param widget the QListWidget to update
     * @param list the data to display on the QListWidget
     */
    public void updateListWidget(QListWidget widget, List list) {
        widget.clear();
        for (Object o : list) {
            QListWidgetItem item = new QListWidgetItem(o.toString());
            item.setData(Qt.ItemDataRole.UserRole, o);
            widget.addItem(item);
        }
    }

    /*
    private void deleteDC() {
        deleteItem(datacenterList);
    }*/

    /**
     * Deletes a selected Cloudlet
     */
    private void deleteCloudlet() {
        deleteItem(cloudletList);
    }

    /**
     * Deletes a selected VM
     */
    private void deleteVM() {
        deleteItem(vmList);
    }

    /**
     * Deletes the selected item on the list
     *
     * @param list the QListWidget on which it should delete an item
     */
    private void deleteItem(QListWidget list) {
        int currentRow = list.getCurrentRow();
        if (currentRow >= 0) {
            QListWidgetItem item = list.takeItem(currentRow);
            if (item != null) {
                if (list == datacenterList) {
                    Datacenter delete = (Datacenter) item.data(Qt.ItemDataRole.UserRole);
                    datacenters.remove(delete);
                    //delete.shutdownEntity();
                    List<Host> hosts = delete.getHostList();
                    System.out.println(hosts.size());
                    while (!hosts.isEmpty()) {
                        Host host = hosts.remove(0);
                    }
                    System.out.println(delete.getHostList().size());
                    delete.shutdownEntity();
                } else if (list == cloudletList) {
                    Cloudlet delete = (Cloudlet) item.data(Qt.ItemDataRole.UserRole);
                    cloudlets.remove(delete);
                } else if (list == vmList) {
                    Vm delete = (Vm) item.data(Qt.ItemDataRole.UserRole);
                    vms.remove(delete);
                } else System.out.println(item);
            }
        }
    }

    /**
     * Duplicates the selected Datacenter creating a new one
     *
     * @throws Exception
     */
    private void duplicateDC() throws Exception {
        duplicateItem(datacenterList);
    }

    /**
     * Duplicates the selected Cloudlet creating a new one
     *
     * @throws Exception
     */
    private void duplicateCloudlet() throws Exception {
        duplicateItem(cloudletList);
    }

    /**
     * Duplicates the selected VM creating a new one
     *
     * @throws Exception
     */
    private void duplicateVM() throws Exception {
        duplicateItem(vmList);
    }

    /**
     * Duplicates an object in a QListWidget
     * @param list the QListWidget in which we want to duplicate an item
     * @throws Exception
     */
    private void duplicateItem(QListWidget list) throws Exception {
        int currentRow = list.getCurrentRow();
        if (currentRow >= 0) {
            QListWidgetItem item = list.currentItem();
            if (item != null) {
                if (list == datacenterList) {
                    Datacenter copy = (Datacenter) item.data(Qt.ItemDataRole.UserRole);
                    Datacenter duplicate = new Datacenter(copy.getName() + "Duplicated" + datacenters.size(), copy);
                    datacenters.add(duplicate);
                    updateListWidget(datacenterList, datacenters);
                } else if (list == cloudletList) {
                    Cloudlet copy = (Cloudlet) item.data(Qt.ItemDataRole.UserRole);
                    Cloudlet duplicate = new Cloudlet(cloudlets.size(), copy.getCloudletLength(),
                            copy.getNumberOfPes(), copy.getCloudletFileSize(), copy.getCloudletOutputSize(),
                            copy.getUtilizationModelCpu(), copy.getUtilizationModelRam(), copy.getUtilizationModelBw());
                    cloudlets.add(duplicate);
                    updateListWidget(cloudletList, cloudlets);
                } else if (list == vmList) {
                    Vm copy = (Vm) item.data(Qt.ItemDataRole.UserRole);
                    Vm duplicate = new Vm(vms.size(), copy.getUserId(), copy.getMips(), copy.getNumberOfPes(),
                            copy.getRam(), copy.getBw(), copy.getSize(), copy.getVmm(), copy.getCloudletScheduler());
                    vms.add(duplicate);
                    updateListWidget(vmList, vms);
                }
            }
        }
    }

    private void startSim() {
        broker.submitVmList(vms);
        broker.submitCloudletList(cloudlets);

        /*for (int index = 0; index < cloudlets.size(); index++) {
            broker.bindCloudletToVm(cloudlets.get(index).getCloudletId(), vms.get(index % vms.size()).getId());
        }*/

        CloudSim.startSimulation();

        List<Cloudlet> newList = broker.getCloudletReceivedList();
        CloudSim.stopSimulation();

        printCloudletList(newList);
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return broker;
    }

    private static void printCloudletList(List<Cloudlet> newList) {
        Cloudlet cloudlet;
        StringBuilder res = new StringBuilder();

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

        res.append("========== OUTPUT ==========\n" + "Cloudlet ID").append(indent).append("STATUS").append(indent).append("Data center ID").append(indent).append("VM ID").append(indent).append("Time").append(indent).append("Start Time").append(indent).append("Finish Time");
        DecimalFormat dft = new DecimalFormat("###.##");
        for (Cloudlet value : newList) {
            cloudlet = value;
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
                        indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime()) +
                        indent + indent + dft.format(cloudlet.getFinishTime()));
                res.append("SUCCESS").append(indent).append(indent).append(cloudlet.getResourceId()).append(indent).append(indent).append(indent).append(cloudlet.getVmId()).append(indent).append(indent).append(dft.format(cloudlet.getActualCPUTime())).append(indent).append(indent).append(dft.format(cloudlet.getExecStartTime())).append(indent).append(indent).append(dft.format(cloudlet.getFinishTime()));
            }
        }
        results.setPlainText(String.valueOf(res));
    }

    //TODO: move this somewhere else
    public static void main(String[] args) {



    }

}
