package ui;

import cube.RubixCube;

import javax.swing.*;
import java.awt.*;

import static java.awt.Color.*;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;

/**
 * Created by chris_000 on 1/17/2016.
 */

public class CubeDisplayPanel extends JPanel {

    private int cubeSize;
    private RubixCube cube;

    public CubeDisplayPanel(int cubeSize, RubixCube cube) {
        this.cubeSize = cubeSize;
        this.cube = cube;
    }

    @Override
    public void paintComponent(Graphics g){
        int width = this.getWidth();
        int height = this.getHeight();

        int offsX = width/3, offsY = height/4;
        int dx = width/(cube.getSize()*3), dy = height/(cube.getSize()*4);
        int xOffs = 0, yOffs = offsY;

        {
            byte[][] left = new byte[cubeSize][cubeSize];
            byte[][] face = cube.getFace(RubixCube.Faces.LEFT);
            for (int i = 0; i < face.length; i++)
                System.arraycopy(face[i], 0, left[i], 0, cubeSize);
//            System.arraycopy(cube.getFace(RubixCube.Faces.LEFT), 0, left, 0, cubeSize);
            RubixCube.rotateCW(left);
            drawFace(g, xOffs, yOffs, dx, dy, left);
        }

        {
            xOffs = offsX;
            yOffs = 0;
            byte[][] back = new byte[cubeSize][cubeSize];
            byte[][] face = cube.getFace(RubixCube.Faces.BACK);
            for (int i = 0; i < face.length; i++)
                System.arraycopy(face[i], 0, back[i], 0, cubeSize);
            //System.arraycopy(cube.getFace(RubixCube.Faces.BACK), 0, back, 0, cubeSize);
            drawFace(g, xOffs, yOffs, dx, dy, back);
        }

        {
            xOffs = offsX;
            yOffs = offsY;
            byte[][] top = new byte[cubeSize][cubeSize];
            byte[][] face = cube.getFace(RubixCube.Faces.TOP);
            for (int i = 0; i < face.length; i++)
                System.arraycopy(face[i], 0, top[i], 0, cubeSize);
            drawFace(g, xOffs, yOffs, dx, dy, top);
        }

        {
            xOffs = 2 * offsX;
            yOffs = offsY;
            byte[][] right = new byte[cubeSize][cubeSize];
            byte[][] face = cube.getFace(RubixCube.Faces.RIGHT);
            for (int i = 0; i < face.length; i++)
                System.arraycopy(face[i], 0, right[i], 0, cubeSize);
//            System.arraycopy(cube.getFace(RubixCube.Faces.RIGHT), 0, right, 0, cubeSize);
            RubixCube.rotateCCW(right);
            drawFace(g, xOffs, yOffs, dx, dy, right);
        }

        {
            xOffs = offsX;
            yOffs = offsY * 2;
            byte[][] front = new byte[cubeSize][cubeSize];
            byte[][] face = cube.getFace(RubixCube.Faces.FRONT);
            for (int i = 0; i < face.length; i++)
                System.arraycopy(face[i], 0, front[i], 0, cubeSize);
//            System.arraycopy(cube.getFace(RubixCube.Faces.FRONT), 0, front, 0, cubeSize);
            drawFace(g, xOffs, yOffs, dx, dy, front);
        }

        {
            xOffs = offsX;
            yOffs = offsY * 3;
            byte[][] bottom = new byte[cubeSize][cubeSize];
            byte[][] face = cube.getFace(RubixCube.Faces.BOTTOM);
            for (int i = 0; i < face.length; i++)
                System.arraycopy(face[i], 0, bottom[i], 0, cubeSize);
//            System.arraycopy(cube.getFace(RubixCube.Faces.BOTTOM), 0, bottom, 0, cubeSize);
            drawFace(g, xOffs, yOffs, dx, dy, bottom);
        }
    }

    private Color[] colors = new Color[]{ORANGE, GREEN, BLUE, RED, WHITE, YELLOW};

    public void drawFace(Graphics g, int xOffs, int yOffs, int dx, int dy, byte[][] face){
        for (int i = 0; i < face.length; i++) {
            byte[] col = RubixCube.getCol(face,i);

            for (int j = 0; j < col.length; j++) {
                g.setColor(Color.black);
                g.drawRect(xOffs+(i*dx),yOffs+(j*dy),dx,dy);
                g.setColor(colors[col[j]]);
                g.fillRect(xOffs+(i*dx)+1,yOffs+(j*dy)+1,dx-2,dy-2);
            }
        }
    }


    public int getCubeSize() {
        return cubeSize;
    }

    public void setCubeSize(int cubeSize) {
        this.cubeSize = cubeSize;
    }

    public RubixCube getCube() {
        return new RubixCube(cube);
    }

    public void setCube(RubixCube cube) {
        this.cube = new RubixCube(cube);
    }
}
