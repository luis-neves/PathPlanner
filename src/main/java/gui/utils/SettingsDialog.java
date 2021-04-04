package gui.utils;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {

    public int CHECK_ERP_PERIOD; //MINUTOS
    public float corridorwidth;
    public String CLIENT_ID;

    public JTextField nome_input;
    public JSpinner width;
    public JSpinner check_erp_period;

    public SettingsDialog(int CHECK_ERP_PERIOD, float corridorwidth, String CLIENT_ID) {
        this.CHECK_ERP_PERIOD = CHECK_ERP_PERIOD;
        this.corridorwidth = corridorwidth;
        this.CLIENT_ID=CLIENT_ID;
        this.setModal(true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel name_label = new JLabel("ESB identifier for PathPlanner:");
        add(name_label, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel period_label = new JLabel("Task polling period (min):");
        add(period_label, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel corridor_label = new JLabel("Minimum width for operator (m):");
        add(corridor_label, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        nome_input = new JTextField(CLIENT_ID);
        add(nome_input, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        SpinnerModel model= new SpinnerNumberModel(CHECK_ERP_PERIOD,1,30,1);
        check_erp_period = new JSpinner(model);
        add(check_erp_period, gbc);


        gbc.gridx = 1;
        gbc.gridy = 2;
        SpinnerModel model2= new SpinnerNumberModel(corridorwidth,0.5,3,0.1);
        width = new JSpinner(model2);
        add(width, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JButton ok_button=new JButton("OK");
        add(ok_button, gbc);
        ok_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateValues();
                dispose();
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 3;
        JButton cancel_button=new JButton("Cancel");
        add(cancel_button, gbc);
        cancel_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        pack();
        //setSize(950, 500);
        this.setVisible(true);
    }

    public void updateValues(){
        CLIENT_ID=nome_input.getText();
        corridorwidth=(float)width.getValue();
        CHECK_ERP_PERIOD=((Number)check_erp_period.getValue()).intValue();
    }

}
