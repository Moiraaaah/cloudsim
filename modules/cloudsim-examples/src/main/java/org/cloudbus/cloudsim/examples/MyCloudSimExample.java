package org.cloudbus.cloudsim.examples;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class MyCloudSimExample {
    private static List<Cloudlet> cloudlets;
    private static List<Vm> vms;

    public static void main(String[] args) {
        Log.printLine("Starting MyCloudSimExample...");
        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            createDatacenter("DC1");

            DatacenterBroker broker = createBroker();
            int brokerId = broker.getId();

            vms = new ArrayList<>();

            //VMs description
            int vmId = 0;
            int mips = 250;
            long size = 10000; //image size (MB)
            int ram = 2048; //vm memory (MB)
            long bw = 1000;
            int pesNumber = 1; //number of CPUs
            String vmm = "Xen";

            Vm vm1 = new Vm(vmId, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vms.add(vm1);
            vmId++;

            Vm vm2 = new Vm(vmId, brokerId, mips * 2, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vms.add(vm2);

            broker.submitVmList(vms);

            cloudlets = new ArrayList<>();

            //cloudlet properties
            int id = 0;
            long length = 40000;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            Cloudlet cloudlet1 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet1.setUserId(brokerId);

            id++;
            Cloudlet cloudlet2 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet2.setUserId(brokerId);

            cloudlets.add(cloudlet1);
            cloudlets.add(cloudlet2);

            broker.submitCloudletList(cloudlets);

            broker.bindCloudletToVm(cloudlet1.getCloudletId(), vm1.getId());
            broker.bindCloudletToVm(cloudlet2.getCloudletId(), vm2.getId());

            CloudSim.startSimulation();

            List<Cloudlet> newList = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();

            printCloudletList(newList);
            Log.printLine("MyCloudSimExample finished :)");

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("MyCloudSimExample finished :(");
        }

    }

    private static void printCloudletList(List<Cloudlet> newList) {
        int size = newList.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
                "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (Cloudlet value : newList) {
            cloudlet = value;
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
                        indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime()) +
                        indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }
    }

    private static DatacenterBroker createBroker() {
        DatacenterBroker broker = null;
        try {
            broker = new DatacenterBroker("Broker");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hosts = new ArrayList<>();

        //First machine
        List<Pe> pes = new ArrayList<>();
        int mips = 1000;
        pes.add(new Pe(0, new PeProvisionerSimple(mips)));

        int hostId = 0;
        int ram = 2048;
        long storage = 1000000;
        int bw = 10000;

        hosts.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, pes, new VmSchedulerTimeShared(pes)));

        //Second machine
        List<Pe> pes2 = new ArrayList<>();
        pes2.add(new Pe(0, new PeProvisionerSimple(mips)));

        hostId++;

        hosts.add(new Host(hostId, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, pes2, new VmSchedulerTimeShared(pes2)));

        String architecture = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;
        LinkedList<Storage> storageList = new LinkedList<>();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(architecture, os, vmm, hosts, time_zone, cost, costPerMem, costPerStorage, costPerBw);
        Datacenter DC = null;
        try {
            DC = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hosts),storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return DC;
    }
}
