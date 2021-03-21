package gui.utils;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {

    public int CHECK_ERP_PERIOD; //MINUTOS
    public double SENSIBILITY;
    public String CLIENT_ID;

    public JTextField nome_input;
    public JSlider sensibility;
    public JSpinner check_erp_period;

    public SettingsDialog(int CHECK_ERP_PERIOD, double SENSIBILITY, String CLIENT_ID) {
        this.CHECK_ERP_PERIOD = CHECK_ERP_PERIOD;
        this.SENSIBILITY = SENSIBILITY;
        this.CLIENT_ID=CLIENT_ID;
        this.setModal(true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel name_label = new JLabel("Nome da aplicacao no ESB:");
        add(name_label, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel period_label = new JLabel("Período de verificação de tarefas (min):");
        add(period_label, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel sensibility_label = new JLabel("Sensibilidade do editor de grafos (cm):");
        add(sensibility_label, gbc);

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
        sensibility = new JSlider(0,100,new Double(SENSIBILITY*100).intValue());
        add(sensibility, gbc);

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
        SENSIBILITY=(float)sensibility.getValue()/100.0;
        CHECK_ERP_PERIOD=((Number)check_erp_period.getValue()).intValue();
    }

}
