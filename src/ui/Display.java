package ui;

import cube.RubixCube;
import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import experiment.ExperimentResultsWriter;
import experiment.RubixCubeExperiment;
import experiment.ExperimentParameters;
import experiment.Tuple;
import searches.*;
import searches.Searchable.EdgeChildPair;

import javax.swing.*;
import java.awt.*;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * Created by Chris on 1/5/2016.
 */
public class Display {
    private static final boolean autoRestartProgramAfterCompletion = false;
    private static final boolean autoStartExperiment = false;

    private boolean showCubeStatesDuringSearch = false;

    private int cubeSize = 3;
    private int MOVES = 10;
    private int GIVEUPAFTERSTATES = 3000000;

    private final CubeDisplayPanel cubePanel;
    private final JPanel layout;
    private final Runnable updateDiagnosticsPanel;
    private final JList<Tuple> resultsList;
    private final JButton solveButton;
    private final JButton stopButton;

    private SearchDiagnostic searchDiagnostic;

    private int sleepTime = 0;
    private RubixCubeExperiment experiment;


    public Display() {

        JFrame window = new JFrame("Rubix");
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Neural Net",new NeuralNetPanel());

        //Setup menu bar
        JMenuBar menuBar = new JMenuBar();

        JMenu experimentMenu = new JMenu("Experiments");
        JMenuItem experimentMenuItem = new JMenuItem("RubixCubeExperiment",'e');
        experimentMenuItem.addActionListener(e -> new ExperimentPopupWindow(Display.this));
        experimentMenu.add(experimentMenuItem);


        JMenuItem NNexperimentMenu = new JMenuItem("NN Experiment",'n');
        NNexperimentMenu.addActionListener(e -> new NNExperimentPopupWindow(Display.this));
        experimentMenu.add(NNexperimentMenu);
        menuBar.add(experimentMenu);

        window.setJMenuBar(menuBar);


        layout = new JPanel(new BorderLayout());
        cubePanel = new CubeDisplayPanel(new RubixCube(cubeSize));
        cubePanel.setMinimumSize(new Dimension(900,900));
        cubePanel.setSize(900,900);
        layout.add(cubePanel,BorderLayout.CENTER);

        //Top panel which shows the size of the open and closed lists
        JPanel diagnosticPanel = new JPanel();
        diagnosticPanel.setMinimumSize(new Dimension(200,900));
        diagnosticPanel.setSize(900,900);
        JLabel exploredStatesLabel = new JLabel("Explored States: ");
        JLabel exploredStatesValueLabel = new JLabel("0");
        JLabel knownUnexploredStatesLabel = new JLabel("Known Unexplored States: ");
        JLabel knownUnexploredStatesValueLabel = new JLabel("0");
        updateDiagnosticsPanel = () ->{
            SearchDiagnostic sd = searchDiagnostic;
            if( sd != null ){
                exploredStatesValueLabel.setText("" + sd.getStatesExplored());
                knownUnexploredStatesValueLabel.setText("" + sd.getKnownUnexploredStates());
                diagnosticPanel.repaint();
            }
        };


        JCheckBox showCubeStatesDuringSearchCheckbox = new JCheckBox("Show cube states during search.");
        showCubeStatesDuringSearchCheckbox.setSelected(showCubeStatesDuringSearch);
        showCubeStatesDuringSearchCheckbox.addActionListener(e -> {
            showCubeStatesDuringSearch = showCubeStatesDuringSearchCheckbox.isSelected();
        });
        diagnosticPanel.add(showCubeStatesDuringSearchCheckbox);


        diagnosticPanel.add(exploredStatesLabel);
        diagnosticPanel.add(exploredStatesValueLabel);
        diagnosticPanel.add(knownUnexploredStatesLabel);
        diagnosticPanel.add(knownUnexploredStatesValueLabel);
        layout.add(diagnosticPanel,BorderLayout.NORTH);


        JButton doMove = new JButton("Do Move");


        JPanel resultView = new JPanel(new BorderLayout());
        resultView.setMinimumSize(new Dimension(900,900));
        resultView.setSize(900,900);
        resultView.setBorder(BorderFactory.createTitledBorder("Results View"));
        resultView.add(new JLabel("       Try clicking on the tuples once the simulation is done         "),BorderLayout.NORTH);


        resultsList = new JList<>();
        resultsList.setModel(new DefaultListModel<>());
        resultView.add(resultsList);
        resultsList.addListSelectionListener(e1 -> {
            Tuple t = resultsList.getSelectedValue();
            if( t != null ) {
                cubePanel.setCube((RubixCube) t.state);
                layout.repaint();
                doMove.setEnabled(true);
            }
        });
        layout.add(resultView,BorderLayout.EAST);


        JPanel buttons = new JPanel(new FlowLayout());

        JLabel colLabel = new JLabel("Col:");
        JComboBox<Integer> col = new JComboBox<>();
        col.addItem(-1);
        for (int i = 0; i < cubeSize; i++)
            col.addItem(i);

        buttons.add(colLabel);
        buttons.add(col);


        JLabel rowLabel = new JLabel("Row:");
        JComboBox<Integer> row = new JComboBox<>();
        row.addItem(-1);
        for (int i = 0; i < cubeSize; i++)
            row.addItem(i);
        buttons.add(rowLabel);
        buttons.add(row);

        JComboBox<String> dir = new JComboBox<>();
        dir.addItem("CW");
        dir.addItem("CCW");
        buttons.add(dir);


        JButton rotateNS = new JButton("RotateNS");
        rotateNS.addActionListener(e -> {
            int column = (Integer)col.getSelectedItem();
            if( column != -1){
                cubePanel.setCube(cubePanel.getCube().rotateNS(column,"CW".equals(dir.getSelectedItem())));
                layout.repaint();
            }
        });
        buttons.add(rotateNS);

        JButton rotateEW = new JButton("RotateEW");
        rotateEW.addActionListener(e -> {
            int column = (Integer)col.getSelectedItem();
            if( column != -1){
                boolean cw = "CW".equals(dir.getSelectedItem());
                cubePanel.setCube(cubePanel.getCube().rotateEW(column,cw));
                layout.repaint();
            }
        });
        buttons.add(rotateEW);

        JButton rotateRow = new JButton("RotateRow");
        rotateRow.addActionListener(e -> {
            int Row = (Integer)row.getSelectedItem();
            if( Row != -1){
                cubePanel.setCube(cubePanel.getCube().rotateRow(Row,"CW".equals(dir.getSelectedItem())));
                layout.repaint();
            }
        });
        buttons.add(rotateRow);

        JButton getChild = new JButton("getChild");
        getChild.addActionListener(e -> {
            java.util.List<EdgeChildPair> children = cubePanel.getCube().getChildren();
            EdgeChildPair edgeChildPair = children.get((int) (Math.random()*children.size()));
            cubePanel.setCube((RubixCube)edgeChildPair.child);
            layout.repaint();
        });
        buttons.add(getChild);


        stopButton = new JButton("Stop");
        solveButton = new JButton("Solve!");

        stopButton.addActionListener(e -> {
            if(experiment != null)
                experiment.stop();
            stopButton.setEnabled(false);
            solveButton.setEnabled(true);
        });
        stopButton.setEnabled(false);


        buttons.add(stopButton);


        solveButton.addActionListener(e -> {
            ExperimentParameters expParams = new ExperimentParameters(Search.Searches.ASTAR,cubeSize, GIVEUPAFTERSTATES, MOVES );
            //expParams.setStartState(new RubixCube(cubePanel.getCube()));

            runExperiment(expParams);

            solveButton.setEnabled(false);
            stopButton.setEnabled(true);
        });
        buttons.add(solveButton);

        JTextField sleepTimeField = new JTextField(sleepTime+"");
        sleepTimeField.setColumns(30);
        Runnable updateSleepBetweenStatesTime = () -> {
            try {
                sleepTime = Integer.parseInt(sleepTimeField.getText());
            } catch (Exception ex) {
                //ex.printStackTrace();
                sleepTimeField.setText("0");
            }
        };
        sleepTimeField.addActionListener(e -> updateSleepBetweenStatesTime.run());
        sleepTimeField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateSleepBetweenStatesTime.run();
            }
        });
        buttons.add(sleepTimeField);

        doMove.setEnabled(false);
        doMove.addActionListener(e -> {
            Tuple moveTuple = ((DefaultListModel<Tuple>)resultsList.getModel()).get(resultsList.getSelectedIndex());
            cubePanel.setCube(new RubixCube((RubixCube) moveTuple.state));
            String move = (String) moveTuple.edge;
            if( move != null ) {
                String[] parts = move.split(":");
                int colOrRow = Integer.parseInt(parts[1]);

                boolean cw = parts[2].equals("cw");
                dir.setSelectedIndex(cw ? 0 : 1);

                switch (parts[0]){
                    case "EW":
                        //System.out.println("Rotating ew col " + colOrRow + (cw?"cw":"ccw"));
                        col.setSelectedIndex(colOrRow+1);
                        rotateEW.doClick();
                        break;
                    case "NS":
                        //System.out.println("Rotating ns col " + colOrRow + (cw?"down":"up"));
                        col.setSelectedIndex(colOrRow+1);
                        rotateNS.doClick();
                        break;
                    case "Row":
                        //System.out.println("Rotating row " + colOrRow + (cw?"cw":"ccw"));
                        row.setSelectedIndex(colOrRow+1);
                        rotateRow.doClick();
                        break;
                }
            }
        });
        buttons.add(doMove);

        layout.add(buttons,BorderLayout.SOUTH);

        tabbedPane.addTab("Searches",layout);
        window.setContentPane(tabbedPane);

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        window.setSize(width-100,height-100);
        window.setVisible(true);

        if( autoStartExperiment ){
            solveButton.doClick();
        }
    }



    private void runExperiment(ExperimentParameters expParams){

        Thread t = new Thread(){
            @Override
            public void run() {

                expParams.setPathTracer(getPathTracer());
                expParams.setTooManyStatesListener(()->{
                    restartProgram();
                    System.exit(0);
                });


                RubixCubeExperiment exp = new RubixCubeExperiment(expParams);

                searchDiagnostic = exp.getSearchDiagnostic();
                experiment = exp;


                System.out.println("Beginning RubixCubeExperiment!");
                List<Tuple> resultTuples = exp.runExperiment();

                if( resultTuples != null ) {

                    ExperimentResultsWriter.writeResultsToFile("results",cubeSize,resultTuples);

                    new ExperimentCompletePopupWindow(resultTuples.size());

                    updateResultsPanel(resultTuples);

                    if (autoRestartProgramAfterCompletion)
                        possiblyStartAnotherRun();
                }
                stopButton.setEnabled(false);
                solveButton.setEnabled(true);
            }
        };
        t.setDaemon(true);
        t.start();
    }

    Consumer<Searchable> getPathTracer(){
        return nCube -> {
            if( !showCubeStatesDuringSearch )
                return;

            cubePanel.setCube((RubixCube) nCube);
            layout.repaint();

            if( sleepTime > 0 )
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }


            SwingUtilities.invokeLater(updateDiagnosticsPanel);
        };
    }

    void possiblyStartAnotherRun() {
        if( !autoRestartProgramAfterCompletion )
            return;

        File f = new File("C:\\Users\\chris_000\\SkyDrive\\Documents\\School\\CPSC 371 AI\\RubixCube\\results");

        int expCount=0;

        for(String fileName : f.list()){
            if(fileName.endsWith(".txt"))
                expCount++;
        }
        //So if there are less than 100000 results then restart the program and close this jvm
        if( expCount < 100000 ){
            restartProgram();
            System.exit(0);
        }
    }


    void updateResultsPanel(List<Tuple> resultTuples) {
        SwingUtilities.invokeLater(()->{
            ((DefaultListModel<Tuple>)resultsList.getModel()).clear();
            //System.out.println("Adding " + resultTuples.size() + " to the results panel.");
            resultTuples.forEach(t -> ((DefaultListModel<Tuple>)resultsList.getModel()).addElement(t));
            layout.repaint();
        });
        SwingUtilities.invokeLater(layout::repaint);
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            try {
                UIManager.removeAuxiliaryLookAndFeel(UIManager.getLookAndFeel());

                UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            new Display();
        });
    }




    public static void restartProgram()  {
        String separator = System.getProperty("file.separator");
        String classpath = System.getProperty("java.class.path");
        String path = System.getProperty("java.home")
                + separator + "bin" + separator + "java";
        ProcessBuilder processBuilder =
                new ProcessBuilder(path, "-cp",
                        classpath,
                        Display.class.getName());
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setSearchDiagnostic(SearchDiagnostic searchDiagnostic) {
        this.searchDiagnostic = searchDiagnostic;
    }

    public void setExperiment(RubixCubeExperiment experiment) {
        this.experiment = experiment;
    }

    public RubixCube getDisplayedCube() {
        return cubePanel.getCube();
    }
}
