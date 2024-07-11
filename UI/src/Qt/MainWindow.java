package Qt;

import io.qt.gui.QIcon;
import io.qt.widgets.*;

public class MainWindow extends QMainWindow {

    public MainWindow() {

        setWindowTitle("CloudSim");
        setWindowIcon(new QIcon("./logoCloud.png"));
        setMinimumSize(1000, 800);

        QTabWidget tabs = new QTabWidget(this);
        CreateSimulationWindow createSimWindow = new CreateSimulationWindow(this);
        tabs.addTab(createSimWindow, "Create Simulation");

        setCentralWidget(tabs);
    }

    public static void main(String[] args) {
        QApplication.initialize(args);

        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
        mainWindow.show();
        QApplication.exec();
    }
}
