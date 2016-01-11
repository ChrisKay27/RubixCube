package cube;

import searches.Searchable;


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import static cube.RubixCube.Faces.*;


/**
 * Created by chris_000 on 1/5/2016.
 */
public class RubixCube implements Searchable {

	private final int size;

	public enum Faces {FRONT,RIGHT, BACK,LEFT ,TOP,BOTTOM}

	private final Face[] faces = new Face[6];

	public RubixCube(RubixCube rubixCube) {
		this.size = rubixCube.size;
		int i = 0;

		faces[i++] = new Face(rubixCube.getFace(FRONT));
		faces[i++] = new Face(rubixCube.getFace(RIGHT));
		faces[i++] = new Face(rubixCube.getFace(BACK));
		faces[i++] = new Face(rubixCube.getFace(LEFT));
		faces[i++] = new Face(rubixCube.getFace(TOP));
		faces[i] =   new Face(rubixCube.getFace(BOTTOM));
	}


	public RubixCube(int size) {
		this.size = size;
		int i = 0;

		faces[i++] = new Face(size, Color.ORANGE);
		faces[i++] = new Face(size,Color.GREEN);
		faces[i++] = new Face(size,Color.BLUE);
		faces[i++] = new Face(size,Color.RED);
		faces[i++] = new Face(size,Color.WHITE);
		faces[i] = new Face(size,Color.YELLOW);

	}

	@Override
	public List<EdgeChildPair> getChildren() {
		List<EdgeChildPair> childrenStates = new ArrayList<>(size*size*size*2);
		for (int i = 0; i < size; i++) {
			childrenStates.add(new EdgeChildPair("NS:"+i+":cw",  new RubixCube(this).rotateNS(i,true)));
			childrenStates.add(new EdgeChildPair("NS:"+i+":ccw" ,new RubixCube(this).rotateNS(i,false)));
			childrenStates.add(new EdgeChildPair("EW:"+i+":cw",  new RubixCube(this).rotateEW(i,true)));
			childrenStates.add(new EdgeChildPair("EW:"+i+":ccw", new RubixCube(this).rotateEW(i,false)));
			childrenStates.add(new EdgeChildPair("Row:"+i+":cw", new RubixCube(this).rotateRow(i,true)));
			childrenStates.add(new EdgeChildPair("Row:"+i+":ccw",new RubixCube(this).rotateRow(i,false)));
		}
		return childrenStates;
	}

	public RubixCube rotateNS(int col, boolean down){
		RubixCube nCube = this;//new RubixCube(this);

		if( size%2 == 1 && col == size/2 ){
			for (int i = 0; i < size; i++) {
				if( i != col )
					rotateNS(i,!down);
			}
			return nCube;
		}

		int rotations = 1;
		if( !down )
			rotations = 3;

		Face front = nCube.getFace(FRONT);
		Face back = nCube.getFace(BACK);
		Face top = nCube.getFace(TOP);
		Face bottom = nCube.getFace(BOTTOM);

		while(rotations --> 0 ) {
			if(col == 0)
				if(down)
					nCube.getFace(Faces.LEFT).rotateCW();
				else
					nCube.getFace(Faces.LEFT).rotateCCW();
			else if(col == size-1)
				if(down)
					nCube.getFace(Faces.RIGHT).rotateCCW();
				else
					nCube.getFace(Faces.RIGHT).rotateCW();

			Color[] topcol = top.getCol(col);
			top.setCol(col, back.getCol(col));
			back.setCol(col, bottom.getCol(col));
			bottom.setCol(col, front.getCol(col));
			front.setCol(col, topcol);
		}

		return nCube;
	}

	public RubixCube rotateEW(int col, boolean cw){
		RubixCube nCube = this;// new RubixCube(this);

		if( size%2 == 1 && col == size/2 ){
			for (int i = 0; i < size; i++) {
				if( i != col )
					rotateNS(i,!cw);
			}
			return nCube;
		}

		int rotations = 1;
		if( !cw )
			rotations = 3;

		Face right = nCube.getFace(RIGHT);
		Face left = nCube.getFace(LEFT);
		Face top = nCube.getFace(TOP);
		Face bottom = nCube.getFace(BOTTOM);

		while(rotations --> 0 ) {
			if(col == 0)
				if(cw)
					nCube.getFace(Faces.BACK).rotateCCW();
				else
					nCube.getFace(Faces.BACK).rotateCW();
			else if(col == size-1)
				if(cw)
					nCube.getFace(Faces.FRONT).rotateCW();
				else
					nCube.getFace(Faces.FRONT).rotateCCW();


			Color[] topRow = top.getRow(col);
			top.setRow(col, reverse(left.getCol(col)));
			left.setCol(col, bottom.getRow(size-col-1));
			bottom.setRow(size-col-1, reverse(right.getCol(size-col-1)));
			right.setCol(size-col-1, topRow);
		}
		return nCube;
	}

	public RubixCube rotateRow(int row, boolean cw){
		RubixCube nCube = this;//new RubixCube(this);

		if( size%2 == 1 && row == size/2 ){
			for (int i = 0; i < size; i++) {
				if( i != row )
					rotateRow(i,!cw);
			}
			return nCube;
		}

		int rotations = 1;
		if( !cw )
			rotations = 3;

		Face front = nCube.getFace(FRONT);
		Face right = nCube.getFace(RIGHT);
		Face back = nCube.getFace(BACK);
		Face left = nCube.getFace(LEFT);


		while(rotations --> 0 ) {
			if(row == 0)
				if(cw)
					nCube.getFace(Faces.TOP).rotateCW();
				else
					nCube.getFace(Faces.TOP).rotateCCW();
			else if(row == size-1)
				if(cw)
					nCube.getFace(Faces.BOTTOM).rotateCCW();
				else
					nCube.getFace(Faces.BOTTOM).rotateCW();


			Color[] frontrow = front.getRow(row);
			front.setRow(row, right.getRow(row));
			right.setRow(row, reverse(back.getRow(size-row-1)));
			back.setRow(size-row-1, reverse(left.getRow(row)));
			left.setRow(row, frontrow);
		}
		return nCube;
	}

//	public void rotateFace(Faces face, boolean cw){
//		int rotations = 1;
//		if( !cw )
//			rotations = 3;
//
//		Face front = getFace(FRONT);
//		Face right = getFace(RIGHT);
//		Face back = getFace(BACK);
//		Face left = getFace(LEFT);
//		Face top = getFace(TOP);
//		Face bottom = getFace(BOTTOM);
//
//		while(rotations --> 0 ) {
//			getFace(face).rotateCW();
//			switch (face) {
//				case FRONT: {
//					Color[] toprow = top.getRow(2);
//					top.setRow(2, reverse(left.getCol(2)));
//
//					left.setCol(2, bottom.getRow(0));
//					bottom.setRow(0, reverse(right.getCol(0)));
//					right.setCol(0, toprow);
//					break;
//				}
//				case RIGHT: {
//					Color[] topcol = top.getCol(2);
//					top.setCol(2, front.getCol(2));
//					front.setCol(2, bottom.getCol(2));
//					bottom.setCol(2, reverse(back.getCol(2)));
//					back.setCol(2, topcol);
//					break;
//				}
//				case BACK: {
//					Color[] toprow = top.getRow(0);
//					top.setRow(0, right.getCol(2));
//					right.setCol(2, reverse(bottom.getRow(2)));
//					bottom.setRow(2, left.getCol(0));
//					left.setCol(0, reverse(toprow));
//					break;
//				}
//				case LEFT: {
//					Color[] topcol = top.getCol(0);
//					top.setCol(0, back.getCol(0));
//					back.setCol(0, bottom.getCol(0));
//					bottom.setCol(0, front.getCol(0));
//					front.setCol(0, topcol);
//					break;
//				}
//				case TOP:{
//					Color[] frontrow = front.getRow(0);
//					front.setRow(0, right.getRow(0));
//					right.setRow(0, reverse(back.getRow(2)));
//					back.setRow(2, reverse(left.getRow(0)));
//					left.setRow(0, frontrow);
//					break;
//				}
//				case BOTTOM: {
//					Color[] frontrow = front.getRow(2);
//					front.setRow(2, left.getRow(2));
//					left.setRow(2, reverse(back.getRow(0)));
//					back.setRow(0, reverse(right.getRow(2)));
//					right.setRow(2, frontrow);
//					break;
//				}
//			}
//		}
//
//
//	}

	private Color[] reverse(Color[] colors) {
		List<Color> colorList = Arrays.asList(colors);
		Collections.reverse(colorList);
		return colorList.toArray(colors);
	}


	public Face getFace(Faces face) {
		return faces[face.ordinal()];
	}

	public Face[] getFaces() {
		return faces;
	}


	public int getSize() {
		return size;
	}


	@Override
	public boolean isGoal() {
		for (int i = 0; i < 6; i++)
			if( !faces[i].isSolid())
				return false;

		return true;
	}


	@Override
	public long distanceFrom(Searchable s) {
		if( !(s instanceof RubixCube) ) return Long.MAX_VALUE;

		RubixCube cube = (RubixCube) s;

		long distance=0;
		for (int i = 0; i < 6; i++)
			distance += faces[i].distanceFrom(cube.faces[i]);
		return distance;
	}


	@Override
	public boolean equals(Object obj) {
		if( obj == this ) return true;
		if( !(obj instanceof RubixCube) ) return false;

		RubixCube cube = (RubixCube) obj;

		for (int i = 0; i < 6; i++)
			if( !faces[i].equals(cube.faces[i]))
				return false;

		return true;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Cube;Front:");

		Face f = faces[Faces.FRONT.ordinal()];
		sb.append(f).append(",Right:");
		f = faces[Faces.RIGHT.ordinal()];
		sb.append(f).append(",Back:");
		f = faces[Faces.BACK.ordinal()];
		sb.append(f).append(",Left:");
		f = faces[Faces.LEFT.ordinal()];
		sb.append(f).append(",Top:");
		f = faces[Faces.TOP.ordinal()];
		sb.append(f).append(",Bottom:");
		f = faces[Faces.BOTTOM.ordinal()];
		sb.append(f).append("]");
		return sb.toString();
	}
}
