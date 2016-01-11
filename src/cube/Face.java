package cube;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by chris_000 on 1/5/2016.
 */
public class Face {


	//public enum Color {RED,BLUE,GREEN,WHITE,YELLOW,ORANGE}
	private Color[][] colors;


	public Face(int size, Color color){
		colors = new Color[size][size];
		for (int i = 0; i < colors.length; i++)
			for (int j = 0; j < colors[0].length; j++)
				colors[i][j] = color;
	}

	public Face(Face face) {
		colors = new Color[face.getSize()][face.getSize()];
		for (int i = 0; i < colors.length; i++)
			for (int j = 0; j < colors[0].length; j++)
				colors[i][j] = face.colors[i][j];
	}


	public Color[] getCol(int col) {
		Color[] newcolors = new Color[this.colors.length];
		System.arraycopy(colors[col], 0, newcolors, 0, colors.length);
		return newcolors;
	}
	public Color[] getRow(int row) {
		Color[] Row = new Color[colors.length];
		for (int i = 0; i < colors.length; i++) {
			Row[i] = colors[i][row];
		}
		return Row;
	}

	public void setCol(int col, Color[] Col){
		System.arraycopy(Col, 0, colors[col], 0, colors.length);
	}

	public void setRow(int row, Color[] Row){

		//System.out.println("Set row " +row+" to:"+ Arrays.asList(Row));

		for (int i = 0; i < colors.length; i++)
			colors[i][row] = Row[i];
	}

	public void rotateCW(){
		Color[][] newFace = new Color[colors.length][colors.length];

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


	@Override
	public int hashCode() {
		int hashcode = 0b10101010101010101011010101010101;

		for (int i = 0; i < colors.length; ++i)
			for (int j = 0; j < colors.length; ++j) {
				if( i%2 == 1 )
					hashcode |= colors[i][j].hashCode() << i*j;
				else
					hashcode &= colors[i][j].hashCode() << i*j;
			}
		//System.out.println("face:"+hashcode);
		return hashcode;
	}

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
		Color c = colors[0][0];
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
				if( colors[i][j] != face.colors[i][j] )
					distance++;
			}
		}

		return distance;
	}
}

