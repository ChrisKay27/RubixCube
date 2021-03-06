package cube;

import com.google.common.primitives.Bytes;

import searches.Searchable;
import util.WTFException;

import java.util.*;

import static cube.RubixCube.Color.*;
import static cube.RubixCube.Faces.*;


/**
 * Created by Chris on 1/5/2016.
 */
public class RubixCube implements Searchable {



	public static class Color{
		public static final byte ORANGE = 0;
		public static final byte GREEN = 1;
		public static final byte BLUE = 2;
		public static final byte RED = 3;
		public static final byte WHITE = 4;
		public static final byte YELLOW = 5;
		public static final byte[] FACE_COLORS = {0,1,2,3,4,5};
	}

	private final int size;

	public enum Faces {FRONT,RIGHT, BACK,LEFT ,TOP,BOTTOM}
    private static Faces[] facesArray = {FRONT,RIGHT, BACK,LEFT ,TOP,BOTTOM};

	private final byte[][][] faces = new byte[6][][];
	//private final Face[] faces = new Face[6];


	public RubixCube(RubixCube rubixCube) {
		this.size = rubixCube.size;

        for (int k = 0; k < 6; k++) {
            faces[k] = new byte[size][size];
            byte[][] face = rubixCube.getFace(facesArray[k]);
            for (int i = 0; i < size; i++) {
                System.arraycopy(face[i], 0, faces[k][i], 0, size);
            }
        }
	}

	public RubixCube(int size) {
		this.size = size;

		for (int i = 0; i < 6; i++) {
			faces[i] = new byte[size][size];
			for (int k = 0; k < size; k++)
				for (int j = 0; j < size; j++)
					faces[i][k][j] = FACE_COLORS[i];
		}
	}


	/**
	 * Used to recreate a cube from a string to verify my test results were actually legit.
	 * @param fromString a string produced from a rubixCube.toString() call.
     */
	public RubixCube(String fromString) {
		fromString = fromString.substring(fromString.indexOf(':')+1, fromString.length()-1);

		String[] parts = fromString.split(":");
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].substring(1,parts[i].lastIndexOf(']'));
		}

		size = (int) Math.sqrt(parts[0].length()/2 + 1);

		for (int faceCount = 0; faceCount < 6; faceCount++) {
			faces[faceCount] = new byte[size][size];

			String[] colorChars = parts[faceCount].split(",");
			int count = 0;
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					faces[faceCount][i][j] = colorCharToByte(colorChars[count].charAt(0));
					count++;
				}
			}
		}
	}



	private static byte colorCharToByte(char c){
		if( c == 'R' )
			return Color.RED;
		else if( c == 'B' )
			return Color.BLUE;
		else if( c == 'Y' )
			return Color.YELLOW;
		else if( c == 'W' )
			return Color.WHITE;
		else if( c == 'G' )
			return Color.GREEN;
		else if( c == 'O' )
			return Color.ORANGE;
		throw new WTFException("Bad char value! : " + c);
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
			//System.out.println("Rotating center ns col so rotating all other cols the other way!");
			for (int i = 0; i < size; i++) {
				if( i != col )
					rotateNS(i,!down);
			}
			return nCube;
		}

		int rotations = 1;
		if( !down )
			rotations = 3;

		byte[][] front = nCube.getFace(FRONT);
		byte[][] back = nCube.getFace(BACK);
		byte[][] top = nCube.getFace(TOP);
		byte[][] bottom = nCube.getFace(BOTTOM);

		while(rotations --> 0) {
			if(col == 0)
				rotateCW(nCube.getFace(Faces.LEFT));
			else if(col == size-1)
				rotateCCW(nCube.getFace(Faces.RIGHT));

			byte[] topcol = getCol(top,col);
			setCol(top,col, getCol(back,col));
			setCol(back,col, getCol(bottom,col));
			setCol(bottom,col, getCol(front,col));
			setCol(front,col, topcol);
		}

		return nCube;
	}

	public RubixCube rotateEW(int col, boolean cw){
		RubixCube nCube = this;// new RubixCube(this);

		if( size%2 == 1 && col == size/2 ){
			//System.out.println("Rotating center ew col so rotating all other cols the other way!");
			for (int i = 0; i < size; i++) {
				if( i != col )
					rotateEW(i,!cw);
			}
			return nCube;
		}

		int rotations = 1;
		if( !cw )
			rotations = 3;

		byte[][] right = nCube.getFace(RIGHT);
		byte[][] left = nCube.getFace(LEFT);
		byte[][] top = nCube.getFace(TOP);
		byte[][] bottom = nCube.getFace(BOTTOM);

		while(rotations --> 0 ) {

			if(col == 0)
				rotateCCW(nCube.getFace(Faces.BACK));
			else if(col == size-1)
				rotateCW(nCube.getFace(Faces.FRONT));


			byte[] topRow = getRow(top,col);
			setRow(top,col, reverse(getCol(left,col)));
			setCol(left,col, getRow(bottom,size-col-1));
			setRow(bottom,size-col-1, reverse(getCol(right,size-col-1)));
			setCol(right,size-col-1, topRow);
		}
		return nCube;
	}


	public RubixCube rotateRow(int row, boolean cw){
		RubixCube nCube = this;//new RubixCube(this);

		if( size%2 == 1 && row == size/2 ){
			//System.out.println("Rotating center row so rotating all other rows the other way!");
			for (int i = 0; i < size; i++) {
				if( i != row )
					rotateRow(i,!cw);
			}
			return nCube;
		}

		int rotations = 1;
		if( !cw )
			rotations = 3;

		byte[][] front = nCube.getFace(FRONT);
		byte[][] right = nCube.getFace(RIGHT);
		byte[][] back = nCube.getFace(BACK);
		byte[][] left = nCube.getFace(LEFT);


		while(rotations --> 0 ) {
			if(row == 0)
				rotateCW(nCube.getFace(Faces.TOP));
			else if(row == size-1)
				rotateCCW(nCube.getFace(Faces.BOTTOM));

			byte[] frontrow = getRow(front,row);
			setRow(front, row, getRow(right,row));
			setRow(right,row, reverse(getRow(back,size-row-1)));
			setRow(back,size-row-1, reverse(getRow(left,row)));
			setRow(left,row, frontrow);
		}
		return nCube;
	}



	public byte[][] getFace(Faces face) {
		return faces[face.ordinal()];
	}


	public int getSize() {
		return size;
	}





	@Override
	public int heuristicDistanceTo(Searchable s) {
		if( !(s instanceof RubixCube) ) return Integer.MAX_VALUE;

		RubixCube cube = (RubixCube) s;

		int distance=0;
		for (int i = 0; i < 6; i++) {

			distance += faceDistanceFrom(faces[i], FACE_COLORS[i],cube.faces[i]);
			//faces[i].heuristicDistanceTo(cube.faces[i]);
		}

		return distance/8;
	}


	@Override
	public boolean equals(Object obj) {
		if( obj == this ) return true;
		if( !(obj instanceof RubixCube) ) return false;

		RubixCube cube = (RubixCube) obj;

		for (int i = 0; i < 6; i++)
			if( !Arrays.deepEquals(faces[i], cube.faces[i]))
				return false;

		return true;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Cube;Front:");

		byte[][] f = faces[Faces.FRONT.ordinal()];
		sb.append(faceToString(f)).append(",Right:");
		f = faces[Faces.RIGHT.ordinal()];
		sb.append(faceToString(f)).append(",Back:");
		f = faces[Faces.BACK.ordinal()];
		sb.append(faceToString(f)).append(",Left:");
		f = faces[Faces.LEFT.ordinal()];
		sb.append(faceToString(f)).append(",Top:");
		f = faces[Faces.TOP.ordinal()];
		sb.append(faceToString(f)).append(",Bottom:");
		f = faces[Faces.BOTTOM.ordinal()];
		sb.append(faceToString(f)).append("]");
		return sb.toString();
	}

//	@Override
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("[Cube;Front:");
//
//		Face f = faces[Faces.FRONT.ordinal()];
//		sb.append(fac).append(",Right:");
//		f = faces[Faces.RIGHT.ordinal()];
//		sb.append(f).append(",Back:");
//		f = faces[Faces.BACK.ordinal()];
//		sb.append(f).append(",Left:");
//		f = faces[Faces.LEFT.ordinal()];
//		sb.append(f).append(",Top:");
//		f = faces[Faces.TOP.ordinal()];
//		sb.append(f).append(",Bottom:");
//		f = faces[Faces.BOTTOM.ordinal()];
//		sb.append(f).append("]");
//		return sb.toString();
//	}

	/**
	 * @return A new byte[] object
	 */
	private static byte[] reverse(byte[] colors) {
		List<Byte> bytes = Bytes.asList(colors);
		Collections.reverse(bytes);
		colors = Bytes.toArray(bytes);
		return colors;
	}


	public static void rotateCW(byte[][] face){
		//System.out.println("Rotate cw");
		byte[][] newFace = new byte[face.length][face.length];

		for (int i = 0; i < face.length; ++i) {
			for (int j = 0; j < face.length; ++j) {
				newFace[i][j] = face[j][face.length - i - 1];
			}
		}


		for(int r = 0; r < newFace.length; r++)
			for(int c = 0; c < newFace.length; c++)
				face[r][c] = newFace[r][c];

	}

	public static void rotateCCW(byte[][] face){
		//System.out.println("Rotate ccw");
		byte[][] newFace = new byte[face.length][face.length];

		for (int i = 0; i < face.length; ++i) {
			for (int j = 0; j < face.length; ++j) {
				newFace[i][j] = face[face.length - j - 1][i];
			}
		}


		for(int r = 0; r < newFace.length; r++)
			for(int c = 0; c < newFace.length; c++)
				face[r][c] = newFace[r][c];

		//rotateCW(face);
		//rotateCW(face);
		//rotateCW(face);
	}



	public static byte[] getCol(byte[][] face, int col) {
		byte[] newcolors = new byte[face.length];
		System.arraycopy(face[col], 0, newcolors, 0, face.length);
		return newcolors;
	}

	/**
	 * @return A new array with the same elements as this faces row indexed by the param 'row'
	 */
	public static byte[] getRow(byte[][] face, int row) {
		byte[] Row = new byte[face.length];
		for (int i = 0; i < face.length; i++) {
			Row[i] = face[i][row];
		}
		return Row;
	}

	public static void setCol(byte[][] face, int col, byte[] Col){
		System.arraycopy(Col, 0, face[col], 0, face.length);
	}


	/** @param Row Copies the elements from Row into this faces row.
	 */
	public static void setRow(byte[][] face, int row, byte[] Row){
		for (int i = 0; i < face.length; i++)
			face[i][row] = Row[i];
	}

	public static boolean isSolid(byte[][] face) {
		byte c = face[0][0];
		for (int i = 0; i < face.length; ++i)
			for (int j = 0; j < face.length; ++j)
				if( face[i][j] != c)
					return false;

		return true;
	}


	public static boolean faceEquals(byte[][] face1, byte[][] face2) {

		for (int i = 0; i < face1.length; ++i)
			for (int j = 0; j < face1.length; ++j)
				if( face1[i][j] != face2[i][j])
					return false;

		return true;
	}



	public static String faceToString(byte[][] face) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < face.length; ++i)
			for (int j = 0; j < face.length; ++j)
				if( face[i][j] == Color.RED )
					sb.append("R,");
				else if( face[i][j] == Color.BLUE )
					sb.append("B,");
				else if( face[i][j] == Color.YELLOW )
					sb.append("Y,");
				else if( face[i][j] == Color.WHITE )
					sb.append("W,");
				else if( face[i][j] == Color.GREEN )
					sb.append("G,");
				else if( face[i][j] == Color.ORANGE )
					sb.append("O,");

		String s = sb.substring(0,sb.length()-1);
		return s + "]";
	}



    /**
     * Used to find a heuristic distance between the two faces given
     */
	public long faceDistanceFrom(byte[][] face1, byte face1Color, byte[][] face2) {
		long distance=0;

		for (int i = 0; i < getSize(); i++) {
			for (int j = 0; j < getSize(); j++) {
				if( face1[i][j] != face2[i][j] ) {
					if( !isAdjacent(face1Color,face1[i][j]) )
						distance++;
					distance++;
				}
			}
		}

		return distance;
	}

    /**
     * Used to see if the color c is on a face adjacent to face with color c1
     */
	private static boolean isAdjacent(byte c1, byte c){
		if( c1 == Color.RED )
			return c != Color.GREEN;
		if( c1 == Color.WHITE )
			return c != Color.YELLOW;
		if( c1 == Color.BLUE )
			return c != Color.ORANGE;
		if( c1 == Color.GREEN )
			return c != Color.RED;
		if( c1 == Color.ORANGE )
			return c != Color.BLUE;
		if( c1 == Color.YELLOW )
			return c != Color.WHITE;
		return false;
	}


	private static byte[][] faceCopy(byte[][] face){
		byte[][] newFace = new byte[face.length][face.length];
		for (int i = 0; i < face.length; i++)
			System.arraycopy(face[i], 0, newFace[i], 0, face.length);
		return newFace;
	}


	public static class RubixCubeTester{

		public static boolean test() {
			boolean passed = true;

			passed &= testEquals();
			passed &= testRotate();
			passed &= testGetChildren();
			passed &= testRotateFace();

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

		public static boolean testRotateFace() {

			int size = 2;
			byte[][] face = new byte[size][size];
			for (int i = 0; i < size; i++)
				for (int j = 0; j < size; j++)
					face[i][j] = (byte) (j + i*size);

			rotateCW(face);
			if( face[0][0] != 1 || face[0][1] != 3 ||face[1][0] != 0 ||face[1][1] != 2 ){
				System.out.println("failed rotate cw!" + faceToString(face));
				return false;
			}

			rotateCCW(face);
			if( face[0][0] != 0 || face[0][1] != 1 ||face[1][0] != 2 ||face[1][1] != 3 ){
				System.out.println("failed rotate ccw!" + faceToString(face));
				return false;
			}

			for( ; size < 11; size++) {
				face = new byte[size][size];

//				for (int i = 0; i < size; i++)
//					for (int j = 0; j < size; j++)
//						face[i][j] = (byte) (j + i * size);

				//Mess the face up randomly
				while (Math.random() < 0.9) {
					face[(int) (Math.random() * face.length)][(int) (Math.random() * face.length)] = (byte) (Math.random() * face.length);
				}

				//Copy it
				byte[][] copy = faceCopy(face);

				//Try rotating it 4 times in the same direction
				rotateCW(face);
				rotateCW(face);
				rotateCW(face);
				rotateCW(face);

				//Should be equal
				if (!faceEquals(face, copy)) {
					System.out.println("failed rotate cw 4x!" + faceToString(face) + " should be " + faceToString(copy));
					return false;
				}


				//Try rotating it 2 times in one direction
				rotateCW(face);
				rotateCW(face);
				//Then 2 times back
				rotateCCW(face);
				rotateCCW(face);

				//Should be equal
				if (!faceEquals(face, copy)) {
					System.out.println("failed rotate cw 2x then 2x back!" + faceToString(face) + " should be " + faceToString(copy));
					return false;
				}


				//Then 2 times back
				rotateCCW(face);
				rotateCCW(face);
				//Try rotating it 2 times in one direction
				rotateCW(face);
				rotateCW(face);

				//Should be equal
				if (!faceEquals(face, copy)) {
					System.out.println("failed rotate ccw 2x then 2x back!" + faceToString(face) + " should be " + faceToString(copy));
					return false;
				}


				//Rotate one face ccw once
				rotateCCW(face);

				//Rotate a copy cw three times
				rotateCW(copy);
				rotateCW(copy);
				rotateCW(copy);

				//Should be equal
				if (!faceEquals(face, copy)) {
					System.out.println("failed rotate cw 1x == ccw 3x check!" + faceToString(face) + " should be " + faceToString(copy));
					return false;
				}

			}

			return true;
		}


        public static boolean testRotate() {

			for (int size = 2; size < 10; size++) {

				RubixCube control = new RubixCube(size);
				RubixCube r1 = new RubixCube(size);
				RubixCube r2 = new RubixCube(size);

				for (int i = 0; i < 10000; i++) {

					doRandomMove(r1,r2);
					if( control.equals(r1) || control.equals(r2) || !r1.equals(r2) )
						return false;

					control = new RubixCube(r1);
					if( !control.equals(r1) || !control.equals(r2) )
						return false;

					boolean dir = Math.random()<0.5;
					int colOrRow = (int)(Math.random()*size);
					double rand = Math.random();
					if(rand < 0.333){
						r1.rotateNS(colOrRow,dir);
						if( r1.equals(r2) )
							return false;
						r2.rotateNS(colOrRow,dir);
						if( !r1.equals(r2) )
							return false;
						r1.rotateNS(colOrRow,!dir);
						if( r1.equals(r2) )
							return false;
						r2.rotateNS(colOrRow,!dir);
						if( !r1.equals(r2) )
							return false;
					}
					else if(rand < 0.666){
						r1.rotateEW(colOrRow,dir);
						if( r1.equals(r2) )
							return false;
						r2.rotateEW(colOrRow,dir);
						if( !r1.equals(r2) )
							return false;
						r1.rotateEW(colOrRow,!dir);
						if( r1.equals(r2) )
							return false;
						r2.rotateEW(colOrRow,!dir);
						if( !r1.equals(r2) )
							return false;
					}
					else{
						r1.rotateRow(colOrRow,dir);
						if( r1.equals(r2) )
							return false;
						r2.rotateRow(colOrRow,dir);
						if( !r1.equals(r2) )
							return false;
						r1.rotateRow(colOrRow,!dir);
						if( r1.equals(r2) )
							return false;
						r2.rotateRow(colOrRow,!dir);
						if( !r1.equals(r2) )
							return false;
					}
					boolean ce1 = control.equals(r1);
					boolean ce2 = control.equals(r2);
					boolean r1e2 = r1.equals(r2);
					if( !control.equals(r1) || !control.equals(r2) || !r1.equals(r2) )
						return false;
				}


				for (int rotCol = 0; rotCol < size; rotCol++) {

					//I do not rotate the center slices ever, Instead we rotate every other slice the opposite direction,
					//Testing that here would cause excessive complexity
					if( size%2 == 1 && rotCol == size/2 )
						continue;

					RubixCube rc = new RubixCube(size);

					//Change the left and right faces to ensure they have or have not changed, based on which column is being rotated
					byte[][] leftFace = rc.faces[LEFT.ordinal()];
					byte[][] rightFace = rc.faces[RIGHT.ordinal()];

					for (int i = 0; i < size; i++)
						for (int j = 0; j < size; j++) {
							leftFace[i][j] = (byte) (j + i * size);
							rightFace[i][j] = (byte) (j + i * size);
						}


					//Testing North-South slice downwards rotation
					rc.rotateNS(rotCol,true);

					//If rotating anything but the right most column, make sure the right face did not change
					if( rotCol != size-1 ){
						for (int i = 0; i < size; i++)
							for (int j = 0; j < size; j++)
								if( rightFace[i][j] != (byte) (j + i * size))
									return false;
					}
					//If rotating anything but the left most column, make sure the left face did not change
					if( rotCol != 0 ){
						for (int i = 0; i < size; i++)
							for (int j = 0; j < size; j++)
								if( leftFace[i][j] != (byte) (j + i * size))
									return false;
					}
					//If rotating the right most column, make sure the right face did change
					if( rotCol == size-1 ){
						for (int i = 0; i < size; i++)
							for (int j = 0; j < size; j++)
								//If the cube size is odd then the middle piece should be the same so skip this check
								if( !(size%2 == 1 && i == size/2 && j == size/2) && rightFace[i][j] == (byte) (j + i * size))
									return false;
					}
					//If rotating the left most column, make sure the left face did change
					if( rotCol == 0 ){
						for (int i = 0; i < size; i++)
							for (int j = 0; j < size; j++)
								//If the cube size is odd then the middle piece should be the same so skip this check
								if( !(size%2 == 1 && i == size/2 && j == size/2) && leftFace[i][j] == (byte) (j + i * size))
									return false;
					}



					{//Check the top face
						byte[][] faceColors = rc.faces[FRONT.ordinal()];

						//Make sure the first column is white (the top color)
						for (int j = 0; j < size; j++)
							if (!(faceColors[rotCol][j] == FACE_COLORS[TOP.ordinal()]))
								return false;

						//Make sure the other columns are orange still (the front color)
						for (int i = 0; i < size; i++)
							for (int j = 0; j < size; j++)
								if (i != rotCol && !(faceColors[i][j] == FACE_COLORS[FRONT.ordinal()]))
									return false;
					}
					{//Check the top face
						byte[][] faceColors = rc.faces[TOP.ordinal()];

						//Make sure the first column is white (the top color)
						for (int j = 0; j < size; j++)
							if (!(faceColors[rotCol][j] == FACE_COLORS[BACK.ordinal()]))
								return false;

						//Make sure the other columns are orange still (the front color)
						for (int i = 0; i < size; i++)
							for (int j = 0; j < size; j++)
								if (i != rotCol && !(faceColors[i][j] == FACE_COLORS[TOP.ordinal()]))
									return false;
					}
					{//Check the back face
						byte[][] faceColors = rc.faces[BACK.ordinal()];

						//Make sure the first column is the bottom color
						for (int j = 0; j < size; j++)
							if (!(faceColors[rotCol][j] == FACE_COLORS[BOTTOM.ordinal()]))
								return false;

						//Make sure the other columns are the back color still
						for (int i = 0; i < size; i++)
							for (int j = 0; j < size; j++)
								if (i != rotCol && !(faceColors[i][j] == FACE_COLORS[BACK.ordinal()]))
									return false;
					}
					{//Check the bottom face
						byte[][] faceColors = rc.faces[BOTTOM.ordinal()];

						//Make sure the first column is the bottom color
						for (int j = 0; j < size; j++)
							if (!(faceColors[rotCol][j] == FACE_COLORS[FRONT.ordinal()]))
								return false;

						//Make sure the other columns are the back color still
						for (int i = 0; i < size; i++)
							for (int j = 0; j < size; j++)
								if (i != rotCol && !(faceColors[i][j] == FACE_COLORS[BOTTOM.ordinal()]))
									return false;
					}
				}
			}

            return true;
        }


		private static void doRandomMove(RubixCube r1, RubixCube r2) {
			boolean dir = Math.random()<0.5;
			int colOrRow = (int)(Math.random()*r1.getSize());
			double rand = Math.random();
			if(rand < 0.333){
				r1.rotateNS(colOrRow,dir);
				r2.rotateNS(colOrRow,dir);
			}else if(rand < 0.666){
				r1.rotateEW(colOrRow,dir);
				r2.rotateEW(colOrRow,dir);
			}else{
				r1.rotateRow(colOrRow,dir);
				r2.rotateRow(colOrRow,dir);
			}
		}


		public static boolean testGetChildren() {

			for (int size = 2; size < 10; size++) {

				RubixCube rc = new RubixCube(size);

				List<EdgeChildPair> children = rc.getChildren();
				if(children.size() != size*3*2 )
					return false;

				for (EdgeChildPair ecp : children){
					if( ecp.child.equals(rc) )
						return false;
					for (EdgeChildPair ecp2 : children)
						if( ecp2 != ecp && ecp2.equals(ecp) )
							return false;
				}
			}

			return true;
		}
	}



	/**
	 * Helper main method... ignore this
     */
	public static void main(String[] args) {

		byte[][] face = new byte[3][3];

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				face[i][j] = (byte) (j + i*3);

		print(face);
		System.out.println("rotate cw");
		rotateCW(face);
		print(face);

		System.out.println("rotate ccw");
		rotateCCW(face);
		print(face);

		System.out.println("rotate ccw 3x");
		rotateCCW(face);rotateCCW(face);rotateCCW(face);
		print(face);
	}



	public static void print(byte[][] face) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++)
				System.out.print(face[i][j] + " ");
			System.out.println();
		}
	}


	public RubixCube getRandomChild() {
		List<EdgeChildPair> children = getChildren();
		return (RubixCube) children.get((int)(Math.random()*children.size())).child;
	}
}
