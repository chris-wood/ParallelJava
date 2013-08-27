import java.math.BigInteger;

public class OSGArrowDecider 
{
	// Encapsulate vertex pairs for edges
	class Edge 
	{
		public int x; // edge tail
		public int y; // edge head
		public Edge(int xx, int yy) { x = xx; y = yy; }
	}

	public boolean isInduced(int order, int edgeCount, int[][] F, int[][] colorState, Edge[] edgeMap, int color)
	{
		for (int edgeIndex = 0; edgeIndex < edgeCount; edgeIndex++) // search over each  edge
		{
			int x = edgeMap[edgeIndex].x;
			int y = edgeMap[edgeIndex].y;
			if (colorState[x][y] == color)
			{
				for (int v = 0; v < order && (x != v) && (y != v); v++) // check each of the remaining vertices
				{
					if (F[x][v] == 1 && F[y][v] == 1 && colorState[x][v] == color && colorState[y][v] == color) // both edges exist
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean arrowsTriangles(int[][] F, int lb, int ub)
	{
		int colors = 2;
		int order = F.length; // F is assumed to be symmetric
		int[][] colorState = new int[order][order];

		// Copy over the required portions and set all edge colors to 0
		int edgeCount = 0;
		for (int i = 0; i < order; i++) 
		{
			for (int j = 0; j < order && i >= j; j++) // only look at the upper diagonal
			{
				if (F[i][j] == 1) 
				{
					edgeCount++;
					colorState[i][j] = 0;
					colorState[j][i] = 0;
				}
				else 
				{
					colorState[i][j] = -1; // this edge doesn't exist, so its color is -1
					colorState[j][i] = -1; // Ditto
				}
			}
		}

		// Allocate and initialize the edge map
		int edgeIndex = 0;
		Edge[] edgeMap = new Edge[edgeCount];
		for (int i = 0; i < order; i++) 
		{
			for (int j = 0; j < order; j++) // only look at the upper diagonal
			{
				if (F[i][j] == 1 && j >= i)
				{
					Edge e = new Edge(i, j);
					edgeMap[edgeIndex++] = e;
				}
			}
		}

		// Walk from lb -> ub - 1
		BigInteger upper = new BigInteger("" + ub);
		BigInteger lower = new BigInteger("" + lb);
		boolean result = true;
		while (lower.compareTo(upper) < 0 && result == true) 
		{
			// Set the edges in the color state
			for (int c = 0; c < colors && result; c++) // we only have 2 colors (try to generalize this for fun!)
			{
				for (int index = 0; index < edgeCount && result; index++)
				{
					if (lower.testBit(index)) // bit is 1 (this edge is included)
					{
						int x = edgeMap[index].x;
						int y = edgeMap[index].y;
						colorState[x][y] = c;
						colorState[y][x] = c;
					}
				}

				// Walk over the colors given the color state
				boolean induced = false;

				// Check red and blue, and if neither returns true, exit early and output no
				if (isInduced(order, edgeCount, F, colorState, edgeMap, 0) == false &&
					isInduced(order, edgeCount, F, colorState, edgeMap, 1) == false) return false;
			}

			// Next edge configuration
			lower = lower.add(BigInteger.ONE);
		}

		// Output yes
		return true; 
	}

	public static void main(String[] args)
	{
		if (args.length != 3) 
		{
			System.err.println("usage: java OSGArrowDecider n lb ub");
			System.exit(-1);
		}

		// Build the complete graph adjacency matrix from n
		int n = Integer.parseInt(args[0]);
		int[][] F = new int[n][n];
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				if (i != j) F[i][j] = 1;
			}
		}

		// Grab config bounds
		int lb = Integer.parseInt(args[1]);
		int ub = Integer.parseInt(args[2]);

		OSGArrowDecider decider = new OSGArrowDecider();
		System.out.println(decider.arrowsTriangles(F, lb, ub));
	}
}