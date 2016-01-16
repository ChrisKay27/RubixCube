package cube;

import com.google.common.primitives.Bytes;
import com.sun.deploy.util.ArrayUtil;
import cube.Face.Color;
import searches.Searchable;


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

			byte[] topcol = top.getCol(col);
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
					rotateEW(i,!cw);
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


			byte[] topRow = top.getRow(col);
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


			byte[] frontrow = front.getRow(row);
			front.setRow(row, right.getRow(row));
			right.setRow(row, reverse(back.getRow(size-row-1)));
			back.setRow(size-row-1, reverse(left.getRow(row)));
			left.setRow(row, frontrow);
		}
		return nCube;
	}

	/**
	 * @return A new byte[] object
     */
	private static byte[] reverse(byte[] colors) {
		List<Byte> bytes = Bytes.asList(colors);
		Collections.reverse(bytes);
		colors = Bytes.toArray(bytes);
		return colors;
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




	public static class RubixCubeTester{

		public static boolean test() {
			boolean passed;
			passed = testEquals();
			return passed;
		}


		public static boolean testEquals() {

			for (int size = 2; size < 10; size++) {
				RubixCube rc = new RubixCube(size);
				RubixCube rc2 = new RubixCube(size);

				boolean passed;

				for (int randomMoves = 0; randomMoves < 1000; randomMoves++) {
					boolean cw = Math.random() < 0.5;
					int colOrRow = (int)(Math.random()*size);
					int choice = (int)(Math.random()*3);
					switch(choice){
						case 0:
							passed = rc.rotateNS(colOrRow,cw).equals(rc2.rotateNS(colOrRow,cw));
							if( !passed )
								return false;
							break;
						case 1:
							passed = rc.rotateEW(colOrRow,cw).equals(rc2.rotateEW(colOrRow,cw));
							if( !passed )
								return false;
							break;
						case 2:
							passed = rc.rotateRow(colOrRow,cw).equals(rc2.rotateRow(colOrRow,cw));
							if( !passed )
								return false;
							break;
					}
				}
			}

			return true;
		}




	}

}
