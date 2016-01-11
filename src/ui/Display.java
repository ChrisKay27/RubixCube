package ui;

import cube.Face;
import cube.RubixCube;
import cube.RubixCube.Faces;
import searches.BreadthFirstSearch;
import searches.Searchable;
import searches.Searchable.EdgeChildPair;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by chris_000 on 1/5/2016.
 */
public class Display {

	private static RubixCube cube;
	private static int cubeSize = 3;

	public static void main(String[] args) {
		cube = new RubixCube(cubeSize);


		JFrame window = new JFrame("Rubix");

		JPanel layout = new JPanel(new BorderLayout());
		JPanel content = new JPanel(){
			@Override
			public void paintComponent(Graphics g){
				int width = getWidth();
				int height = getHeight();

				int offsX = width/3, offsY = height/4;
				int dx = width/(cube.getSize()*3), dy = height/(cube.getSize()*4);

				Face left = new Face(cube.getFace(Faces.LEFT));
				left.rotateCCW();
				int xOffs = 0, yOffs = offsY;
				drawFace(g,xOffs,yOffs, dx, dy,left);

				xOffs = offsX; yOffs = 0;
				Face back = new Face(cube.getFace(Faces.BACK));
				//back.rotateCW();back.rotateCW();
				drawFace(g,xOffs,yOffs, dx, dy,back);

				xOffs = offsX; yOffs = offsY;
				Face top = cube.getFace(Faces.TOP);
				drawFace(g,xOffs,yOffs, dx, dy,top);

				xOffs = 2*offsX; yOffs = offsY;
				Face right = new Face(cube.getFace(Faces.RIGHT));
				right.rotateCW();
				drawFace(g,xOffs,yOffs, dx, dy,right);

				xOffs = offsX; yOffs = offsY*2;
				Face front = cube.getFace(Faces.FRONT);
				drawFace(g,xOffs,yOffs, dx, dy,front);

				xOffs = offsX; yOffs = offsY*3;
				Face bottom = cube.getFace(Faces.BOTTOM);
				drawFace(g,xOffs,yOffs, dx, dy,bottom);
			}

			public void drawFace(Graphics g, int xOffs, int yOffs, int dx, int dy, Face face){
				for (int i = 0; i < face.getSize(); i++) {
					Color[] col = face.getCol(i);
					for (int j = 0; j < col.length; j++) {
						g.setColor(Color.black);
						g.drawRect(xOffs+(i*dx),yOffs+(j*dy),dx,dy);
						g.setColor(col[j]);
						g.fillRect(xOffs+(i*dx)+1,yOffs+(j*dy)+1,dx-2,dy-2);
					}
				}
			}
		};
		content.setMinimumSize(new Dimension(900,900));
		content.setSize(900,900);
		layout.add(content,BorderLayout.CENTER);

		JPanel resultView = new JPanel();
		resultView.setMinimumSize(new Dimension(900,900));
		resultView.setSize(900,900);
		JList<String> resultsList = new JList<>();
		resultsList.setModel(new DefaultListModel<>());
		resultView.add(resultsList);
		//((DefaultListModel<String>)resultsList.getModel()).addElement(cube.toString());
		layout.add(resultView,BorderLayout.EAST);


		JPanel buttons = new JPanel(new FlowLayout());


		JLabel colLabel = new JLabel("Col:");
		JComboBox<Integer> col = new JComboBox<>();
		col.addItem(-1);
		for (int i = 0; i < cube.getSize(); i++)
			col.addItem(i);

		buttons.add(colLabel);
		buttons.add(col);


		JLabel rowLabel = new JLabel("Row:");
		JComboBox<Integer> row = new JComboBox<>();
		row.addItem(-1);
		for (int i = 0; i < cube.getSize(); i++)
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
				cube = cube.rotateNS(column,"CW".equals(dir.getSelectedItem()));
				content.repaint();
				System.out.println(cube.hashCode());
			}
		});
		buttons.add(rotateNS);

		JButton rotateEW = new JButton("RotateEW");
		rotateEW.addActionListener(e -> {
			int column = (Integer)col.getSelectedItem();
			if( column != -1){
                cube = cube.rotateEW(column,"CW".equals(dir.getSelectedItem()));
				content.repaint();
				System.out.println(cube.hashCode());
			}
		});
		buttons.add(rotateEW);

		JButton rotateRow = new JButton("RotateRow");
		rotateRow.addActionListener(e -> {
			int Row = (Integer)row.getSelectedItem();
			if( Row != -1){
                cube = cube.rotateRow(Row,"CW".equals(dir.getSelectedItem()));
				content.repaint();
				System.out.println(cube.hashCode());
			}
		});
		buttons.add(rotateRow);

		JButton getChild = new JButton("getChild");
		getChild.addActionListener(e -> {
			java.util.List<EdgeChildPair> children = cube.getChildren();
			EdgeChildPair edgeChildPair = children.get((int) (Math.random()*children.size()));
			cube = (RubixCube)edgeChildPair.child;
			System.out.println(edgeChildPair.edge);
			System.out.println(cube.hashCode());
			content.repaint();
		});
		buttons.add(getChild);



		JButton solve = new JButton("Solve!");
		solve.addActionListener(e -> {
			BreadthFirstSearch bfs = new BreadthFirstSearch();

			RubixCube goal = new RubixCube(cubeSize);
			solve.setEnabled(false);

			Thread t = new Thread(){
				@Override
				public void run() {
					ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

					System.out.println("Searching for goal!");
					List<EdgeChildPair> result = bfs.findGoal(cube, goal);
                    result.remove(0);

                    writeResultsToFile(cubeSize,result);

					RubixCube nextState = (RubixCube) result.get(0).child;
					for (EdgeChildPair ecp : result){
						String move = "(" + nextState + "," + ecp.edge + "),";
						((DefaultListModel<String>) resultsList.getModel()).addElement(move);
						System.out.println(move);
					}

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
							content.repaint();
							executor.schedule(this, 1, TimeUnit.SECONDS);
						}
                    };

					//executor.schedule(task, 1, TimeUnit.SECONDS);

				}
			};
			t.setDaemon(true);
			t.start();

		});
		buttons.add(solve);

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



    private static void writeResultsToFile(int cubeSize, List<EdgeChildPair> results ){
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

            RubixCube nextState = (RubixCube) results.get(0).child;
            for (EdgeChildPair ecp : results){
                String move = "(" + nextState + "," + ecp.edge + ")\n";
                pw.write(move);
            }
            pw.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
