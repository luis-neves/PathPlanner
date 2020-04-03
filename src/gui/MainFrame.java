package gui;

import experiments.Experiment;
import experiments.ExperimentEvent;
import ga.GAEvent;
import ga.GAListener;
import ga.GASingleton;
import ga.GeneticAlgorithm;
import ga.geneticOperators.*;
import ga.selectionMethods.*;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

import picking.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MainFrame extends JFrame implements GAListener {

    private static final long serialVersionUID = 1L;
    private Picking picking;
    private GeneticAlgorithm<PickingIndividual, Picking> ga;
    private PickingExperimentsFactory experimentsFactory;
    SimulationPanel simulationPanel = new SimulationPanel();
    PanelTextArea bestIndividualPanel;
    private PanelParameters panelParameters = new PanelParameters();
    private JButton buttonStop = new JButton("Stop");
    private JButton buttonRunFromMemory = new JButton("Run Genetic Algorithm");
    public JButton buttonVisualize = new JButton("Play");
    private JButton buttonSlowVisualize = new JButton(">");
    private JButton buttonFastVisualize = new JButton(">>");

    private JButton buttonExperiments = new JButton("Experiments");
    private JButton buttonRunExperiments = new JButton("Run experiments");
    private JTextField textFieldExperimentsStatus = new JTextField("", 10);
    private XYSeries seriesBestIndividual;
    private XYSeries seriesAverage;
    private SwingWorker<Void, Void> worker;
    private boolean stop = false;

    public MainFrame() {
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void jbInit() throws Exception {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Pathfinding using genetic algorithms");
        GASingleton.getInstance().setMainFrame(this);
        //North Left Panel
        JPanel panelNorthLeft = new JPanel(new BorderLayout());
        panelNorthLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        panelNorthLeft.add(panelParameters, java.awt.BorderLayout.WEST);
        JPanel panelButtons = new JPanel();
        panelButtons.add(buttonStop);
        panelButtons.add(buttonRunFromMemory);
        panelButtons.add(buttonSlowVisualize);
        panelButtons.add(buttonVisualize);
        panelButtons.add(buttonFastVisualize);
        buttonStop.setEnabled(false);


        buttonStop.addActionListener(new ButtonStop_actionAdapter(this));
        buttonVisualize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //VISUALIZEE
                    if (GASingleton.getInstance().isNodeProblem() && !stop) {
                        Runnable r = new ShowPathRunnable(stop);
                        new Thread(r).start();
                        stop = true;
                        buttonVisualize.setText("||");
                        GASingleton.getInstance().getSimulationPanel().setStop(false);

                    } else if (GASingleton.getInstance().isNodeProblem() && stop) {
                        GASingleton.getInstance().getSimulationPanel().setStop(true);
                        stop = false;
                        buttonVisualize.setText("Play");

                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Wrong parameters!", "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonFastVisualize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //VISUALIZEE
                if (GASingleton.getInstance().isNodeProblem()) {
                    GASingleton.getInstance().getSimulationPanel().descrementTime(5);
                }
            }
        });
        buttonSlowVisualize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //VISUALIZEE
                if (GASingleton.getInstance().isNodeProblem()) {
                    GASingleton.getInstance().getSimulationPanel().incrementTime(5);
                }
            }
        });

        buttonRunFromMemory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    picking = Picking.buildKnapsackFromMemory();
                    //System.out.println(picking.toString());
                    generateGA();

                    ga.addGAListener(MainFrame.this);

                    runGA();

                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Wrong parameters!", "Error!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panelNorthLeft.add(panelButtons, BorderLayout.SOUTH);

        //North Right Panel - Chart creation
        seriesBestIndividual = new XYSeries("Best");
        seriesAverage = new XYSeries("Average");

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesBestIndividual);
        dataset.addSeries(seriesAverage);
        JFreeChart chart = ChartFactory.createXYLineChart("Evolution", // Title
                "generation", // x-axis Label
                "fitness", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

        //North Panel
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(panelNorthLeft, java.awt.BorderLayout.WEST);
        northPanel.add(chartPanel, java.awt.BorderLayout.CENTER);
        panelNorthLeft.add(simulationPanel, BorderLayout.CENTER);
        //Center panel       
        bestIndividualPanel = new PanelTextArea("Best solution: ", 20, 40);
        GASingleton.getInstance().setBestIndividualPanel(bestIndividualPanel);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(bestIndividualPanel, java.awt.BorderLayout.CENTER);

        //South Panel
        JPanel southPanel = new JPanel();
        southPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));

        southPanel.add(buttonExperiments);
        buttonExperiments.addActionListener(new ButtonExperiments_actionAdapter(this));
        southPanel.add(buttonRunExperiments);
        buttonRunExperiments.setEnabled(false);
        buttonRunExperiments.addActionListener(new ButtonRunExperiments_actionAdapter(this));
        southPanel.add(new JLabel("Status: "));
        southPanel.add(textFieldExperimentsStatus);
        textFieldExperimentsStatus.setEditable(false);

        //Global structure
        JPanel globalPanel = new JPanel(new BorderLayout());
        globalPanel.add(northPanel, java.awt.BorderLayout.NORTH);
        globalPanel.add(centerPanel, java.awt.BorderLayout.CENTER);
        globalPanel.add(southPanel, java.awt.BorderLayout.SOUTH);
        this.getContentPane().add(globalPanel);

        pack();
    }

    public void finishedShow() {
        this.stop = false;

    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public class ShowPathRunnable implements Runnable {

        public ShowPathRunnable(boolean stop) {
            // store parameter for later user

        }

        public void run() {
            GASingleton.getInstance().getSimulationPanel().runPath(GASingleton.getInstance().getBestInRun());
        }
    }

    public void buttonDataSet_actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(new java.io.File("."));
        int returnVal = fc.showOpenDialog(this);

        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File dataSet = fc.getSelectedFile();
                picking = Picking.buildKnapsack(dataSet);
            }
        } catch (IOException e1) {
            e1.printStackTrace(System.err);
        } catch (java.util.NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void jButtonRun_actionPerformed(ActionEvent e) {
        try {
            if (picking == null) {
                JOptionPane.showMessageDialog(this, "You must first choose a problem", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            generateGA();

            ga.addGAListener(this);

            runGA();


        } catch (NumberFormatException e1) {
            JOptionPane.showMessageDialog(this, "Wrong parameters!", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runGA() {
        manageButtons(false, false, true, false, false);

        worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                try {

                    ga.run(picking);

                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
                return null;
            }

            @Override
            public void done() {
                manageButtons(true, true, false, true, experimentsFactory != null);
            }
        };

        worker.execute();
    }

    private void generateGA() {
        bestIndividualPanel.textArea.setText("");
        seriesBestIndividual.clear();
        seriesAverage.clear();

        picking.setProb1s(Double.parseDouble(panelParameters.jTextFieldProb1s.getText()));

        ga = new GeneticAlgorithm<PickingIndividual, Picking>(
                Integer.parseInt(panelParameters.jTextFieldN.getText()),
                Integer.parseInt(panelParameters.jTextFieldGenerations.getText()),
                panelParameters.getSelectionMethod(),
                panelParameters.getMutationMethod(),
                panelParameters.getRecombinationMethod(),
                new Random(Integer.parseInt(panelParameters.jTextFieldSeed.getText())));

        System.out.println(ga);
    }

    @Override
    public void generationEnded(GAEvent e) {
        GeneticAlgorithm<PickingIndividual, Picking> source = e.getSource();
        bestIndividualPanel.textArea.setText(source.getBestInRun().toString());
        seriesBestIndividual.add(source.getGeneration(), source.getBestInRun().getFitness());
        seriesAverage.add(source.getGeneration(), source.getAverageFitness());
        if (worker.isCancelled()) {
            e.setStopped(true);
        }
    }

    @Override
    public void runEnded(GAEvent e) {
    }

    public void jButtonStop_actionPerformed(ActionEvent e) {
        worker.cancel(true);
    }

    public void buttonExperiments_actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser(new java.io.File("."));
        int returnVal = fc.showOpenDialog(this);

        try {
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                experimentsFactory = new PickingExperimentsFactory(fc.getSelectedFile());
                manageButtons(true, picking != null, false, true, true);
            }
        } catch (IOException e1) {
            e1.printStackTrace(System.err);
        } catch (java.util.NoSuchElementException e2) {
            JOptionPane.showMessageDialog(this, "File format not valid", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void buttonRunExperiments_actionPerformed(ActionEvent e) {

        manageButtons(false, false, false, false, false);
        textFieldExperimentsStatus.setText("Running");

        worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                try {
                    int iteration = 0;
                    while (experimentsFactory.hasMoreExperiments()) {
                        try {
                            Experiment experiment = experimentsFactory.nextExperiment();
                            iteration++;
                            System.out.println(iteration);
                            experiment.run();
                        } catch (IOException e1) {
                            e1.printStackTrace(System.err);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                }
                return null;
            }

            @Override
            public void done() {
                manageButtons(true, picking != null, false, true, false);
                textFieldExperimentsStatus.setText("Finished");
            }
        };
        worker.execute();
    }

    @Override
    public void experimentEnded(ExperimentEvent e) {
    }

    private void manageButtons(
            boolean dataSet,
            boolean run,
            boolean stopRun,
            boolean experiments,
            boolean runExperiments) {

        buttonStop.setEnabled(stopRun);
        buttonExperiments.setEnabled(experiments);
        buttonRunExperiments.setEnabled(runExperiments);
    }


}

class ButtonDataSet_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonDataSet_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.buttonDataSet_actionPerformed(e);
    }
}

class ButtonRun_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonRun_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonRun_actionPerformed(e);
    }
}

class ButtonStop_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonStop_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.jButtonStop_actionPerformed(e);
    }
}

class ButtonExperiments_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonExperiments_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.buttonExperiments_actionPerformed(e);
    }
}

class ButtonRunExperiments_actionAdapter implements ActionListener {

    final private MainFrame adaptee;

    ButtonRunExperiments_actionAdapter(MainFrame adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.buttonRunExperiments_actionPerformed(e);
    }
}

class PanelAtributesValue extends JPanel {

    protected String title;
    protected List<JLabel> labels = new ArrayList<>();
    protected List<JComponent> valueComponents = new ArrayList<>();

    public PanelAtributesValue() {
    }

    protected void configure() {

        //for(JComponent textField : textFields)
        //textField.setHorizontalAlignment(SwingConstants.RIGHT);

        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);

        //addLabelTextRows

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHEAST;
        Iterator<JLabel> itLabels = labels.iterator();
        Iterator<JComponent> itTextFields = valueComponents.iterator();

        while (itLabels.hasNext()) {
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;                       //reset to default
            add(itLabels.next(), c);

            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            add(itTextFields.next(), c);
        }
    }
}

class PanelParameters extends PanelAtributesValue {

    public static final int TEXT_FIELD_LENGHT = 7;
    public static final String SEED = "1";
    public static final String POPULATION_SIZE = "100";
    public static final String GENERATIONS = "100";
    public static final String TOURNAMENT_SIZE = "2";
    public static final String PROB_RECOMBINATION = "0.8";
    public static final String PROB_MUTATION = "0.01";
    public static final String PROB_1S = "0.05";
    JTextField jTextFieldSeed = new JTextField(SEED, TEXT_FIELD_LENGHT);
    JTextField jTextFieldN = new JTextField(POPULATION_SIZE, TEXT_FIELD_LENGHT);
    JTextField jTextFieldGenerations = new JTextField(GENERATIONS, TEXT_FIELD_LENGHT);
    String[] selectionMethods = {"Tournament", "Roulette wheel", "Rank"};
    JComboBox jComboBoxSelectionMethods = new JComboBox(selectionMethods);
    JTextField jTextFieldTournamentSize = new JTextField(TOURNAMENT_SIZE, TEXT_FIELD_LENGHT);
    String[] recombinationMethods = {"One cut", "Partialy Matched Crossover", "Cycle Crossover", "Order One Crossover"};
    JComboBox jComboBoxRecombinationMethods = new JComboBox(recombinationMethods);
    JTextField jTextFieldProbRecombination = new JTextField(PROB_RECOMBINATION, TEXT_FIELD_LENGHT);
    String[] mutationMethods = {"1st To Last", "Inversion(Reverse Sequence)", "Swap(Exchange)"};
    JComboBox jComboBoxMutationMethods = new JComboBox(mutationMethods);

    JTextField jTextFieldProbMutation = new JTextField(PROB_MUTATION, TEXT_FIELD_LENGHT);
    JTextField jTextFieldProb1s = new JTextField(PROB_1S, TEXT_FIELD_LENGHT);
    String[] fitnessTypes = {"Bigger Path Priority", "Smaller Path Priority", "Closest to Exit Priority"};
    JComboBox jComboBoxFitnessTypes = new JComboBox(fitnessTypes);
    JCheckBox checkBoxWeights = new JCheckBox("");

    public PanelParameters() {
        title = "Genetic algorithm parameters";

        labels.add(new JLabel("Seed: "));
        valueComponents.add(jTextFieldSeed);
        jTextFieldSeed.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("Population size: "));
        valueComponents.add(jTextFieldN);
        jTextFieldN.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("# of generations: "));
        valueComponents.add(jTextFieldGenerations);
        jTextFieldGenerations.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("Selection method: "));
        valueComponents.add(jComboBoxSelectionMethods);
        jComboBoxSelectionMethods.addActionListener(new JComboBoxSelectionMethods_ActionAdapter(this));

        labels.add(new JLabel("Tournament size: "));
        valueComponents.add(jTextFieldTournamentSize);
        jTextFieldTournamentSize.addKeyListener(new IntegerTextField_KeyAdapter(null));

        labels.add(new JLabel("Recombination method: "));
        valueComponents.add(jComboBoxRecombinationMethods);

        labels.add(new JLabel("Recombination prob.: "));
        jComboBoxRecombinationMethods.setSelectedIndex(1);
        valueComponents.add(jTextFieldProbRecombination);

        labels.add(new JLabel("Mutation method: "));
        jComboBoxMutationMethods.setSelectedIndex(1);
        valueComponents.add(jComboBoxMutationMethods);


        labels.add(new JLabel("Mutation prob.: "));
        valueComponents.add(jTextFieldProbMutation);

//        labels.add(new JLabel("Initial proportion of 1s: "));
//        valueComponents.add(jTextFieldProb1s);

        labels.add(new JLabel("Priority: "));
        valueComponents.add(jComboBoxFitnessTypes);
        jComboBoxFitnessTypes.addActionListener(new JComboBoxFitnessFunction_ActionAdapter(this));
        labels.add(new JLabel("Simulate Weights: "));
        valueComponents.add(checkBoxWeights);
        checkBoxWeights.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JCheckBox cbLog = (JCheckBox) actionEvent.getSource();
                if (cbLog.isSelected()) {
                    System.out.println("Simulating weights");
                    GASingleton.getInstance().setSimulatingWeights(true);
                } else {
                    System.out.println("Not Simulating weights");
                    GASingleton.getInstance().setSimulatingWeights(false);

                }
            }
        });
        configure();
    }

    public void actionPerformedSelectionMethods(ActionEvent e) {
    }

    public SelectionMethod<PickingIndividual, Picking> getSelectionMethod() {
        switch (jComboBoxSelectionMethods.getSelectedIndex()) {
            case 0:
                return new Tournament<>(
                        Integer.parseInt(jTextFieldN.getText()),
                        Integer.parseInt(jTextFieldTournamentSize.getText()));
            case 1:
                return new RouletteWheel<>(
                        Integer.parseInt(jTextFieldN.getText()));
            case 2:
                return new Ranking<>(Integer.parseInt(jTextFieldN.getText()));
        }
        return null;
    }

    public Recombination<PickingIndividual> getRecombinationMethod() {

        double recombinationProb = Double.parseDouble(jTextFieldProbRecombination.getText());

        switch (jComboBoxRecombinationMethods.getSelectedIndex()) {
            case 0:
                return new RecombinationOneCut<>(recombinationProb);
            case 1:
                return new RecombinationPMX<>(recombinationProb);
            case 2:
                return new RecombinationCX<>(recombinationProb);
            case 3:
                return new RecombinationOX<>(recombinationProb);
        }
        return null;
    }


    public void actionPerformedFitnessType(ActionEvent e) {
        GASingleton.getInstance().setFitnessType(jComboBoxFitnessTypes.getSelectedIndex());
    }

    public Mutation<PickingIndividual> getMutationMethod() {
        double mutationProb = Double.parseDouble(jTextFieldProbMutation.getText());
        switch (jComboBoxMutationMethods.getSelectedIndex()) {
            case 0:
                return new MutationFirstLast<>(mutationProb);
            case 1:
                return new MutationInversion<>(mutationProb);
            case 2:
                return new MutationSwap<>(mutationProb);
        }
        return null;
    }

    class JComboBoxSelectionMethods_ActionAdapter implements ActionListener {

        final private PanelParameters adaptee;

        JComboBoxSelectionMethods_ActionAdapter(PanelParameters adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            adaptee.actionPerformedSelectionMethods(e);
        }
    }

    class JComboBoxFitnessFunction_ActionAdapter implements ActionListener {

        final private PanelParameters adaptee;

        JComboBoxFitnessFunction_ActionAdapter(PanelParameters adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            adaptee.actionPerformedFitnessType(e);
        }
    }

    class IntegerTextField_KeyAdapter implements KeyListener {

        final private MainFrame adaptee;

        IntegerTextField_KeyAdapter(MainFrame adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                e.consume();
            }
        }
    }
}
