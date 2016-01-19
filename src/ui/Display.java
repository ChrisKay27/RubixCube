package ui;

import cube.RubixCube;
import experiment.Experiment;
import experiment.ExperimentParameters;
import experiment.Tuple;
import searches.*;
import searches.Searchable.EdgeChildPair;

import javax.swing.*;
import java.awt.*;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by chris_000 on 1/5/2016.
 */
public class Display {

    private int cubeSize = 3;
    private int moves = 4;
    private int giveUpAfterStates = 1000000;

    private final JButton getChild;
    private final CubeDisplayPanel cubePanel;
    private final JPanel layout;
    private final Runnable updateDiagnosticsPanel;
    private final JList<Tuple> resultsList;

    private SearchDiagnostic searchDiagnostic;


    private int sleepTime = 0;
    private Experiment experiment;

    public Display() {

        JFrame window = new JFrame("Rubix");

        layout = new JPanel(new BorderLayout());
        cubePanel = new CubeDisplayPanel(cubeSize, new RubixCube(cubeSize));
        cubePanel.setMinimumSize(new Dimension(900,900));
        cubePanel.setSize(900,900);
        layout.add(cubePanel,BorderLayout.CENTER);


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
        diagnosticPanel.add(exploredStatesLabel);
        diagnosticPanel.add(exploredStatesValueLabel);
        diagnosticPanel.add(knownUnexploredStatesLabel);
        diagnosticPanel.add(knownUnexploredStatesValueLabel);
        layout.add(diagnosticPanel,BorderLayout.NORTH);


        JPanel resultView = new JPanel();
        resultView.setMinimumSize(new Dimension(900,900));
        resultView.setSize(900,900);
        resultsList = new JList<>();
        resultsList.setModel(new DefaultListModel<>());
        resultView.add(resultsList);
        resultsList.addListSelectionListener(e1 -> {
            Tuple t = resultsList.getSelectedValue();
            cubePanel.setCube((RubixCube) t.state);
            layout.repaint();
        });
        //((DefaultListModel<String>)resultsList.getModel()).addElement(cube.toString());
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

        getChild = new JButton("getChild");
        getChild.addActionListener(e -> {
            java.util.List<EdgeChildPair> children = cubePanel.getCube().getChildren();
            EdgeChildPair edgeChildPair = children.get((int) (Math.random()*children.size()));
            cubePanel.setCube((RubixCube)edgeChildPair.child);
            layout.repaint();
        });
        buttons.add(getChild);


        JButton stop = new JButton("Stop");
        JButton solve = new JButton("Solve!");

        stop.addActionListener(e -> {
            if(experiment != null)
                experiment.stop();
            stop.setEnabled(false);
            solve.setEnabled(true);
        });
        stop.setEnabled(false);


//        solve.addActionListener(e -> {
//
//            solve.setEnabled(false);
//            stop.setEnabled(true);
//        });
//        buttons.add(solve);
        buttons.add(stop);

        JButton runExpButton = new JButton("Run Experiment!");
        runExpButton.addActionListener(e -> {

            runExperiment(new ExperimentParameters(Search.Searches.ASTAR,cubeSize, 1000000, 5 ));

            runExpButton.setEnabled(false);
            stop.setEnabled(true);
        });
        buttons.add(runExpButton);

        JTextField sleepTimeField = new JTextField(sleepTime+"");
        sleepTimeField.setColumns(30);
        Runnable updateSleepBetweenStatesTime = () -> {
            try {
                sleepTime = Integer.parseInt(sleepTimeField.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
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



        JButton doMove = new JButton("Do Move");
        doMove.setEnabled(false);
        doMove.addActionListener(e -> {
            //EdgeChildPair move = ((DefaultListModel<EdgeChildPair>)resultsList.getModel()).get(0);
        });

        layout.add(buttons,BorderLayout.SOUTH);


        window.setContentPane(layout);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(1500,1000);
        window.setVisible(true);
    }



    private void runExperiment(ExperimentParameters expParams){

        Thread t = new Thread(){
            @Override
            public void run() {

                Consumer<Searchable> c = nCube -> {
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

                expParams.setPathTracer(c);

                Experiment exp = new Experiment(expParams);
                searchDiagnostic = exp.getSearchDiagnostic();
                experiment = exp;


                System.out.println("Beginning Experiment!");
                List<EdgeChildPair> result = exp.runExperiment();

                //If we have pressed the stop button then the search will return an empty list.
                if(result.isEmpty())
                    return;



                List<Tuple> resultTuples = new ArrayList<>();
                RubixCube nextState = (RubixCube) result.get(0).child;
                result.remove(0);

                for (EdgeChildPair ecp : result){
                    Tuple t = new Tuple(nextState,ecp.edge);
                    System.out.println(t);
                    resultTuples.add(t);
                    nextState = (RubixCube) ecp.child;
                }

                Tuple t = new Tuple(nextState,null);
                resultTuples.add(t);


                writeResultsToFile(cubeSize,resultTuples);
                updateResultsPanel(resultTuples);


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                SwingUtilities.invokeLater(layout::repaint);
            }
        };
        t.setDaemon(true);
        t.start();
    }

    private void updateResultsPanel(List<Tuple> resultTuples) {
        SwingUtilities.invokeLater(()->{
            System.out.println("Adding " + resultTuples.size() + " to the results panel.");
            resultTuples.forEach(t -> ((DefaultListModel<Tuple>)resultsList.getModel()).addElement(t));
            layout.repaint();
        });
    }


    public static void main(String[] args) {
		new Display();
	}



    private static void writeResultsToFile(int cubeSize, List<Tuple> results ){
        String filename = cubeSize+"x"+cubeSize+"x"+cubeSize+"x"+"-exp";

        File f = null;
        int count=1;
        while( f == null ){
            f = new File(filename+ count++ +".txt");
            if( !f.exists() )
                try {
                    boolean created = f.createNewFile();
                    if( !created )
                        f = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else
                f = null;
        }


        try(PrintWriter pw = new PrintWriter(f)) {

            for (Tuple t : results)
                pw.write(t + "\n");


//            RubixCube nextState = (RubixCube) results.get(0).child;
//            results.remove(0);
//            for (EdgeChildPair ecp : results){
//                String move = "(" + nextState + "," + ecp.edge + ")\n";
//				nextState = (RubixCube) ecp.child;
//				pw.write(move);
//            }
//			String move = "(" + nextState + ",null)\n";
//			pw.write(move);
            pw.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }






    /*

                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        if( result.isEmpty() ) {
                            solve.setEnabled(true);
                            executor.shutdown();
                            return;
                        }
                        cube = (RubixCube) result.remove(0).child;
                        System.out.println(cube.hashCode());
                        layout.repaint();
                        executor.schedule(this, 1, TimeUnit.SECONDS);
                    }
                };

                //executor.schedule(task, 1, TimeUnit.SECONDS);
     */


}
