package queens;

import java.io.FileWriter;
import java.io.IOException;

import core.Solution;

public class QueensSolution extends Solution {
	
	private int[][] board;
	
	public QueensSolution(int[][] _board) {
		this.board = _board;
	}
	
	public void print() {
		if (board == null) {
			System.out.println("No solution found");
			return;
		}
		
		for (int[] row : board) {
			for (int square : row) {
				if (square == 1)
					System.out.print(RED + square + " " + RESET);
				else
					System.out.print(RESET + square + " ");
			}
			System.out.println("");
		}
			
	}
	
	public void save() {
		String desktopPath = System.getProperty("user.home") + "\\Desktop\\";
        String filePath = desktopPath + "queens_solution.txt";


        try (FileWriter writer = new FileWriter(filePath)) {
            for (int[] row : board) {
                for (int col : row) {
                    writer.write(col + " ");
                }
                writer.write(System.lineSeparator());
            }
            System.out.println("Queens solution saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
