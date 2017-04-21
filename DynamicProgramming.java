import java.util.ArrayList;

/**
 * 
 * @author Chris Bui & Michael Bonpua
 *
 */
public class DynamicProgramming {

	public DynamicProgramming(){
		
	}
	
	public static ArrayList<Integer> minCostVC(int[][] M){
		ArrayList<Integer> v = new ArrayList<Integer>();
		int n = M.length;
		int m = M[0].length;
		int bottomMinColumn = Integer.MAX_VALUE;
		int bottomCost = Integer.MAX_VALUE;
		int[][] costMatrix = new int[n][m];
		int[] y = new int[n];
		
		for (int i = 0; i < m; i++) {
			costMatrix[0][i] = M[0][i];
		}
		
		/* Create cost matrix by adding the current cost = current cost + min(adjacent above cost) */
		for (int i = 1; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (j == 0 && m > 1) {
					costMatrix[i][j] = Math.min(costMatrix[i - 1][j], costMatrix[i - 1][j + 1]) + M[i][j];
				} else if (j == m - 1 && m > 1) {
					costMatrix[i][j] = Math.min(costMatrix[i - 1][j], costMatrix[i - 1][j - 1]) + M[i][j];
				} else if (j - 1 >= 0 && j + 1 < m && m > 1) {
					int min = Math.min(costMatrix[i - 1][j - 1], costMatrix[i - 1][j + 1]);
					costMatrix[i][j] = Math.min(min, costMatrix[i - 1][j]) + M[i][j];
				} else {
					costMatrix[i][j] = costMatrix[i - 1][j] + M[i][j];
				}
				
				if (i == n - 1) {
					if (costMatrix[i][j] < bottomCost) {
						bottomCost = costMatrix[i][j];
						bottomMinColumn = j;
					}
				}
			}
		}
		
		y[n - 1] = bottomMinColumn;
		
		/* Here we start bottom up, choosing the min cost of each above adjacent cell */
		for (int i = n - 1; i > 0; i--) {
			if (bottomMinColumn < 0) break;
			
			if (bottomMinColumn == 0 && m > 1) {
				if (costMatrix[i - 1][bottomMinColumn + 1] < costMatrix[i - 1][bottomMinColumn])
					bottomMinColumn++;
			} else if (bottomMinColumn == m - 1 && m > 1) {
				if (costMatrix[i - 1][bottomMinColumn - 1] < costMatrix[i - 1][bottomMinColumn])
					bottomMinColumn = bottomMinColumn - 1;
			} else if (bottomMinColumn - 1 >= 0 && bottomMinColumn + 1 < m && m > 1) {
				int location = bottomMinColumn;
				if (costMatrix[i - 1][bottomMinColumn - 1] < costMatrix[i - 1][bottomMinColumn]) {
					location = bottomMinColumn - 1;
					if (costMatrix[i - 1][bottomMinColumn + 1] < costMatrix[i - 1][bottomMinColumn - 1]) {
						location = bottomMinColumn + 1;
					}
				} 
				else if(costMatrix[i -1][bottomMinColumn + 1] < costMatrix[i - 1][bottomMinColumn]) {
					location = bottomMinColumn + 1;
				}
				bottomMinColumn = location;
			}
			y[i - 1] = bottomMinColumn;
		}
		
		/* Add x coord and y coord to array list */
		for (int i = 0; i < n; i++) {
			v.add(i);
			v.add(y[i]);

			//System.out.println("[" + i + " " + y[i] +"]");
		}
		
		return v;
	}
	
	/**
	 * Given two characters a and b, we define a function penalty(a, b) as follows: 
	 * if a = b, penalty(a, b) = 0. If a or b = $, then penalty(a, b) = 4; otherwise penalty(a, b) = 2
	 * 
	 * @param x is a string of length n
	 * @param y is a string of length m such that n >= m.
	 * @return string z obtained by inserting $ at n - m indices in y
	 * such that AlignCost(x,z) <= AlignCost(x,y)
	 */
	public static String stringAlignment(String x, String y){
		// bottom up
		String defaultZ = y;
		String finalZ = "";
		int n = x.length();
		int m = y.length();
		int defaultCost = 0;
		int finalCost = Integer.MAX_VALUE;
		int charsToAdd = x.length() - y.length();
		
		if(x.length() == 0 || charsToAdd <= 0) return defaultZ;
		
		while(defaultZ.length() < x.length()){ //fills the new string as a base case
			defaultZ += '$';
		}
		
		defaultCost = alignCost(x, defaultZ);
		
		int[][] costMatrix = new int[n + 1][m + 1];
		
		for (int i = 0; i < n + 1; i++) {
			costMatrix[i][0] = i;
		}
		
		for (int i = 0; i < m + 1; i++) {
			costMatrix[0][i] = i;
		}
		
		/* CostMatrix to determine what spots give the least amount of penalty for adding '$' */
		for (int i = 1; i < n + 1; i++) {
			for (int j = 1; j < m + 1; j++) {
				if (x.charAt(i - 1) == y.charAt(j - 1)) {
					costMatrix[i][j] = costMatrix[i - 1][j - 1];
				} else {
					int tmpMin = Math.min(costMatrix[i - 1][j - 1], costMatrix[i - 1][j]);
					costMatrix[i][j] = Math.min(tmpMin, costMatrix[i][j - 1]) + 1;
				}
			}
		}
		
		/* Build the string in reverse order by taking the min path back to match string x */
		int i = n, j = m, added = 0;
		while ((i > 0 || j > 0)) {
			int currentCost = costMatrix[i][j];
			if (i > 0 && added < charsToAdd && currentCost == costMatrix[i - 1][j] + 1) { // if cost is equal to left + 1 then append $
				i--;
				added++;
				finalZ = finalZ +  '$';
			} else if (j > 0 && currentCost == costMatrix[i][j - 1] + 1) { // if cost is equal to top + 2 then append $
				j--;
				finalZ = finalZ +  y.charAt(j);
			} else if (i > 0 && j > 0 && currentCost == costMatrix[i - 1][j - 1] + 1) { // if cost is equal to diagonal + 1 then append $
				i--;
				j--;
				finalZ = finalZ +  y.charAt(j);
			} else if (i > 0 && j > 0 && currentCost == costMatrix[i - 1][j - 1]) { /* Same value as diagonal means to put the same character in the same position */
				i--;
				j--;
				finalZ = finalZ +  y.charAt(j);
			} 
		}
		finalZ = new StringBuilder(finalZ).reverse().toString();
		//System.out.println(x);
		finalCost = alignCost(x, finalZ);
		return (finalCost < defaultCost) ? finalZ : defaultZ;
	}
	
	/**
	 * @param x final string
	 * @param y string with $ added 
	 * return total cost of string y after penalties added
	 */
	private static int alignCost(String x, String y){
		
		int cost = 0;
		int m = y.length();
		for (int i = 0; i < x.length(); i++) {
			if (i >= m) cost += 4;
			else cost += penalty(x.charAt(i), y.charAt(i));
		}
		
		return cost;
	}
	
	private static int penalty(char a, char b) {
		if (a == b) {
			return 0;
		} else if (a == '$' || b == '$') {
			return  4;
		}
		return 2;
	}
}
