/**
 * 
 */
package pt.force.experimental;

import javafx.geometry.Point3D;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleNode;

/**
 * @author Force
 *
 */
@SuppressWarnings("restriction")
public class Tester
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		boolean showNodeAnimation = false;
		boolean showManhattanEdgesAnimation = false;
		boolean showSquareEdgesAnimation = false;
		boolean showCubeEdgesAnimation = false;
		
		boolean drawSquareEdges = false;
		boolean drawCubeEdges = false;
		
		long nodeAnimationSleepTime = 0;
		long manhattanAnimationSleepTime = 0;
		long cubeAnimationSleepTime = 0;
		long squareAnimationSleepTime = 0;
		
		int numberOfNodes = 0;
		int maxXNodes, maxYNodes, maxZNodes;
		
		Graph g = new DefaultGraph("g", false, false);
		g.addAttribute("ui.stylesheet", "node"
									+ "{"
									+ "fill-mode: dyn-plain;"
									+ "fill-color: black, brown, orange, red, purple, blue, green;"
									+ "} "
									+ "edge"
									+ "{"
									+ "fill-mode: dyn-plain;"
									+ "fill-color: black, red, blue;"
									+ "}");
		g.display(true);
		
		maxXNodes = 10;
		maxYNodes = 10;
		maxZNodes = 10;
		
		double p = Math.pow(100.0, 2);
		double pp = Math.pow(50.0, 2);
		nodeAnimationSleepTime = 		(long)( (1/ (double) ((maxXNodes * maxYNodes * maxZNodes) * (	1/p		) ) ) );
		manhattanAnimationSleepTime = 	(long)( (1/ (double) ((maxXNodes * maxYNodes * maxZNodes) * (	1/pp	) ) ) );
		
		cubeAnimationSleepTime = squareAnimationSleepTime = manhattanAnimationSleepTime;
		
		for(int x = 0; x < maxXNodes; x++)
		{
			for(int y = 0; y < maxYNodes; y++)
			{
				for(int z = 0; z < maxZNodes; z++)
				{
					SingleNode n = g.addNode(String.valueOf(numberOfNodes));
					n.setAttribute("x", x);
					n.setAttribute("y", y);
					n.setAttribute("z", z);
					
					//coloring based on node insertion order
//					double color = numberOfNodes/(double)(maxXNodes*maxYNodes*maxZNodes);
					
					//coloring based on node position
					double xColor = Math.abs(x - (maxXNodes/2.0)) / (double)(maxXNodes/2.0);
					double yColor = Math.abs(y - (maxYNodes/2.0)) / (double)(maxYNodes/2.0);
					double zColor = Math.abs(z - (maxZNodes/2.0)) / (double)(maxZNodes/2.0);
//					double xColor = x - (maxXNodes/2.0) / (double)(maxXNodes/2.0);
//					double yColor = y - (maxYNodes/2.0) / (double)(maxYNodes/2.0);
//					double zColor = z - (maxZNodes/2.0) / (double)(maxZNodes/2.0);
					double color = trunc((xColor + yColor + zColor)/3);
					
					n.addAttribute("ui.color", color);
					
					numberOfNodes++;
					
					if(showNodeAnimation)
					{
						try
						{
							Thread.sleep(nodeAnimationSleepTime);
						}
						catch(InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		for(int i = 0; i < numberOfNodes; i++)
		{
			Node n = g.getNode(i);
			
			for(int j = i; j < numberOfNodes; j++)
			{
				Node nn = g.getNode(j);
				
				if(!n.getId().equals(nn.getId()) && Integer.parseInt(n.getId()) < Integer.parseInt(nn.getId()))
				{
					int xN = n.getAttribute("x");
					int yN = n.getAttribute("y");
					int zN = n.getAttribute("z");					
					Point3D currentPoint = new Point3D(xN, yN, zN);
					
					int xNN = nn.getAttribute("x");
					int yNN = nn.getAttribute("y");
					int zNN = nn.getAttribute("z");
					Point3D closePoint = new Point3D(xNN, yNN, zNN);
					
					double pointDistance = trunc(currentPoint.distance(closePoint));
					double squareBaseDist = trunc(Math.sqrt(Math.pow(1.0, 2.0) + Math.pow(1.0, 2.0)));
					double cubeDist = trunc(Math.sqrt(Math.pow(squareBaseDist, 2.0) + Math.pow(1.0, 2.0)));
					
					
					if(pointDistance == 1)
					{
						if(xNN - xN == 1)
						{
							//x edge
							Edge e = g.addEdge("x:"+n.getId()+"|"+nn.getId(), n, nn);
							e.addAttribute("weight", 1);
							e.addAttribute("ui.color", 0.0);
							
							if(showManhattanEdgesAnimation)
							{
								try
								{
									Thread.sleep(manhattanAnimationSleepTime);
								}
								catch(InterruptedException error)
								{
									error.printStackTrace();
								}
							}
						}
						
						if(yNN - yN == 1)
						{
							//y edge
							Edge e = g.addEdge("y:"+n.getId()+"|"+nn.getId(), n, nn);
							e.addAttribute("weight", 1);
							e.addAttribute("ui.color", 0.5);
							
							if(showManhattanEdgesAnimation)
							{
								try
								{
									Thread.sleep(manhattanAnimationSleepTime);
								}
								catch(InterruptedException error)
								{
									error.printStackTrace();
								}
							}
						}
						
						if(zNN - zN == 1)
						{
							//z edge
							Edge e = g.addEdge("z:"+n.getId()+"|"+nn.getId(), n, nn);
							e.addAttribute("weight", 1);
							e.addAttribute("ui.color", 1);
							
							if(showManhattanEdgesAnimation)
							{
								try
								{
									Thread.sleep(manhattanAnimationSleepTime);
								}
								catch(InterruptedException error)
								{
									error.printStackTrace();
								}
							}						
						}
					}
					
					if(pointDistance == squareBaseDist && drawSquareEdges)
					{
						Edge e = g.addEdge("square:"+n.getId()+"|"+nn.getId(), n, nn);
						e.addAttribute("weight", squareBaseDist);
						e.addAttribute("ui.color", 0.15);
						
						if(showSquareEdgesAnimation)
						{
							try
							{
								Thread.sleep(cubeAnimationSleepTime);
							}
							catch(InterruptedException error)
							{
								error.printStackTrace();
							}
						}
					}
					
					if(pointDistance == cubeDist && drawCubeEdges)
					{
						Edge e = g.addEdge("cube:"+n.getId()+"|"+nn.getId(), n, nn);
						e.addAttribute("weight", cubeDist);
						e.addAttribute("ui.color", 0.65);
						
						if(showCubeEdgesAnimation)
						{
							try
							{
								Thread.sleep(squareAnimationSleepTime);
							}
							catch(InterruptedException error)
							{
								error.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		GraphParser.graphToFile("testfile", g);
	}
	
	
	
	private static double trunc(double value)
	{
		return (double) Math.round(value * 100) / 100;
	}
}
