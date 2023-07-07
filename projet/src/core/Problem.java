package core;

public abstract class Problem {
	
	public abstract int[][] encode();
	public abstract int[] solve(int[][] _encodedInput);
	public abstract Solution decode(int[] _encodedSolution);
	
	public Solution process() {
		
		int[][] encodedInput = encode();
		
		int[] encodedSolution = solve(encodedInput);
		
		return decode(encodedSolution);
	}
}
