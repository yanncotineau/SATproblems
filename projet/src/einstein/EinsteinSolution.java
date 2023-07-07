package einstein;

import core.Solution;

public class EinsteinSolution extends Solution {

	private String[][] resultGrid;
	
	public EinsteinSolution(String[][] _resultGrid) {
		this.resultGrid = _resultGrid;
	}
	
	public void print() {
		for (String[] row : resultGrid) {
			for (String s : row) {
				System.out.print(s + "\t\t");
			}
			System.out.println("");
		}
	}
}
