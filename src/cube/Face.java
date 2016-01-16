package cube;


/**
 * Created by chris_000 on 1/5/2016.
 */
public class Face {

	public static class Color{
		public static final byte RED = 0;
		public static final byte BLUE = 1;
		public static final byte GREEN = 2;
		public static final byte WHITE = 3;
		public static final byte YELLOW = 4;
		public static final byte ORANGE = 5;
	}

	//public enum Color {RED,BLUE,GREEN,WHITE,YELLOW,ORANGE}
	private byte[][] colors;
	private final byte color;

	public Face(int size, byte color){
		this.color = color;
		colors = new byte[size][size];
		for (int i = 0; i < colors.length; i++)
			for (int j = 0; j < colors[0].length; j++)
				colors[i][j] = color;

	}

	public Face(Face face) {
		this.color = face.color;
		colors = new byte[face.getSize()][face.getSize()];
		for (int i = 0; i < colors.length; i++)
			for (int j = 0; j < colors[0].length; j++)
				colors[i][j] = face.colors[i][j];
	}


	public byte[] getCol(int col) {
		byte[] newcolors = new byte[this.colors.length];
		System.arraycopy(colors[col], 0, newcolors, 0, colors.length);
		return newcolors;
	}

	/**
	 * @return A new array with the same elements as this faces row indexed by the param 'row'
     */
	public byte[] getRow(int row) {
		byte[] Row = new byte[colors.length];
		for (int i = 0; i < colors.length; i++) {
			Row[i] = colors[i][row];
		}
		return Row;
	}

	public void setCol(int col, byte[] Col){
		System.arraycopy(Col, 0, colors[col], 0, colors.length);
	}

	/**
	 * @param Row Copies the elements from Row into this faces row.
     */
	public void setRow(int row, byte[] Row){

		//System.out.println("Set row " +row+" to:"+ Arrays.asList(Row));

		for (int i = 0; i < colors.length; i++)
			colors[i][row] = Row[i];
	}


	public void rotateCW(){
		byte[][] newFace = new byte[colors.length][colors.length];

		for (int i = 0; i < colors.length; ++i) {
			for (int j = 0; j < colors.length; ++j) {
				newFace[i][j] = colors[colors.length - j - 1][i];
			}
		}
//		for(int r = 0; r <colors[0].length; r++){
//			for(int c=colors.length-1; c>=0; c--){
//				newFace[r][c] = colors[c][r];
//			}
//		}
		colors = newFace;
	}

	public void rotateCCW(){
		rotateCW();rotateCW();rotateCW();
//		Color[][] newFace = new Color[colors.length][colors.length];
//
//		for(int r = colors[0].length-1; r >= 0; r--){
//			for(int c=0; c < colors.length; c++){
//				newFace[r][c] = colors[c][r];
//			}
//		}
	}

	public int getSize() {
		return colors.length;
	}


//	@Override
//	public int hashCode() {
//		int hashcode = 0b10101010101010101011010101010101;
//
//		for (int i = 0; i < colors.length; ++i)
//			for (int j = 0; j < colors.length; ++j) {
//				if( i%2 == 1 )
//					hashcode |= colors[i][j].hashCode() << i*j;
//				else
//					hashcode &= colors[i][j].hashCode() << i*j;
//			}
//		//System.out.println("face:"+hashcode);
//		return hashcode;
//	}

	@Override
	public boolean equals(Object obj) {
		if( obj == this ) return true;
		if( !(obj instanceof Face)) return false;

		Face f = (Face) obj;

		//if( f.hashCode() != hashCode() ) return false;

		for (int i = 0; i < colors.length; ++i)
			for (int j = 0; j < colors.length; ++j)
				if( colors[i][j] != f.colors[i][j])
					return false;

		return true;
	}

	public boolean isSolid() {
		byte c = colors[0][0];
		for (int i = 0; i < colors.length; ++i)
			for (int j = 0; j < colors.length; ++j)
				if( colors[i][j] != c)
					return false;

		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < colors.length; ++i)
			for (int j = 0; j < colors.length; ++j)
				if( colors[i][j] == Color.RED )
					sb.append("R,");
				else if( colors[i][j] == Color.BLUE )
					sb.append("B,");
				else if( colors[i][j] == Color.YELLOW )
					sb.append("Y,");
				else if( colors[i][j] == Color.WHITE )
					sb.append("W,");
				else if( colors[i][j] == Color.GREEN )
					sb.append("G,");
				else if( colors[i][j] == Color.ORANGE )
					sb.append("O,");

		String s = sb.substring(0,sb.length()-1);
		return s + "]";
	}


	public long distanceFrom(Face face) {
		long distance=0;

		for (int i = 0; i < getSize(); i++) {
			for (int j = 0; j < getSize(); j++) {
				if( colors[i][j] != face.colors[i][j] ) {
					if( !isAdjacent(colors[i][j]) )
						distance++;
					distance++;
				}
			}
		}

		return distance;
	}


	private boolean isAdjacent(byte c){
		if( color == Color.RED )
			return c != Color.GREEN;
		if( color == Color.WHITE )
			return c != Color.YELLOW;
		if( color == Color.BLUE )
			return c != Color.ORANGE;
		if( color == Color.GREEN )
			return c != Color.RED;
		if( color == Color.ORANGE )
			return c != Color.BLUE;
		if( color == Color.YELLOW )
			return c != Color.WHITE;
		return false;
	}
}

