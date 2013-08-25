import edu.rit.pj2.Task; 
import edu.rit.pj2.Loop;
import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;

public class LinearTableSmp extends Task
{
	int[][] lat; // the linear approximation table
	int[] map;   // the mapping being analyzed

	public int weight(int x)
	{
		return (new BigInteger("" + x)).bitCount();
	}

	public int ioSum(int a, int b, int x, int y)
	{
		return ((weight(a & x) % 2) ^ (weight(b & y) % 2)) % 2;
	}

	public void main(String[] args) throws Exception
	{
		// Start timing
		long start = System.currentTimeMillis();

		// Check cmd line args
		if (args.length != 1) throw new Exception("usage: java pj2 LinearTableSeq mapFile");

		// Read the map from a file and then close the buffer
		BufferedReader fin = new BufferedReader(new FileReader(args[0]));
		String mapLine = fin.readLine();
		fin.close();

		// Split up the input/output map by comma delimiters
		String[] mapElems = mapLine.split(",");
		map = new int[mapElems.length];
		for (int i = 0; i < map.length; i++)
		{
			map[i] = Integer.parseInt(mapElems[i].trim(), 16);
		}

		// Compute the entries of the linear approximation table
		lat = new int[map.length][map.length];
		parallelFor (0, map.length - 1) .exec (new Loop() 
		{ 
			public void run (int a)
			{ 
				for (int b = 0; b < map.length; b++)
				{
					for (int x = 0; x < map.length; x++)
					{
						for (int y = 0; y < map.length; y++)
						{
							if ((map[x] == y) && (ioSum(a, b, x, y) % 2 == 0))
							{
								lat[a][b]++;
							}
						}
					}
				}	
			} 
		});

		for (int i = 0; i < map.length; i++)
		{
			for (int j = 0; j < map.length; j++)
			{
				System.out.printf("%3d", lat[i][j]);
			}
			System.out.printf("\n");
		}

		// End timing
		long end = System.currentTimeMillis();
		System.out.printf("%dms\n", (end - start));
	}
}