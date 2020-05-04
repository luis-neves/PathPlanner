package gui;

import net.sf.jni4net.Bridge;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

    public Main() {

    	//SERVICE BUS
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String userInput;
		Bridge.setVerbose(true);
		try {
			Bridge.init();
			File proxyAssembyFile = new File("./ClassLib.j4n.dll");
			Bridge.LoadAndRegisterAssemblyFrom(proxyAssembyFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//END SERVICE BUS


		MainFrame frame = new MainFrame();

        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exception) {
                    exception.printStackTrace();
                }
                new Main();
            }
        });
    }
}
