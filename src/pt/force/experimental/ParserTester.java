package pt.force.experimental;

import java.io.IOException;

import javafx.geometry.Point3D;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleNode;

/**
 * 
 * @author Force
 *
 */
@SuppressWarnings("restriction")
public class ParserTester
{
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		Graph g = new DefaultGraph("a random sq graph", false, false);
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
		
		g = buildSquareNodes(g, 2, 2, 2);
		g = connectIntoCube(g);
		
		g.display(true);
		
		save("uber", g);
		
		Graph hhhh = load("uber");
		
		hhhh.display(true);
		
		save("rekt", hhhh);
	}

	
	/**
	 * 
	 * @param filename
	 * @param g
	 */
	private static void save(String filename, Graph g)
	{
		try
		{
			GraphParser.graphToFile(filename, g);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	private static Graph load(String filename)
	{
		Graph graph = null;
		try
		{
			graph = GraphParser.graphFromFile(filename);
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return graph;
	}

	
	/**
	 * 
	 * @param g
	 * @param maxXNodes
	 * @param maxYNodes
	 * @param maxZNodes
	 * @return
	 */
	private static Graph buildSquareNodes(Graph g, int maxXNodes, int maxYNodes, int maxZNodes)
	{
		int nodeCount = 0;
		
		for(int x = 0; x < maxXNodes; x++)
		{
			for(int y = 0; y < maxYNodes; y++)
			{
				for(int z = 0; z < maxZNodes; z++)
				{
					SingleNode n = g.addNode(String.valueOf(nodeCount++));
					n.setAttribute("x", x);
					n.setAttribute("y", y);
					n.setAttribute("z", z);
					
					//heat based node coloring
					double xColor = Math.abs(x - (maxXNodes/2.0)) / (double) (maxXNodes/2.0);
					double yColor = Math.abs(y - (maxYNodes/2.0)) / (double) (maxYNodes/2.0);
					double zColor = Math.abs(z - (maxZNodes/2.0)) / (double) (maxZNodes/2.0);
					
					double color = trunc((xColor + yColor + zColor)/3.0);
					
					n.addAttribute("ui.color", color);
				}
			}
		}
		return g;
	}
	
	
	/**
	 * 
	 * @param g
	 * @return
	 */
	private static Graph connectIntoCube(Graph g)
	{
		int nodeCount = g.getNodeCount();
		
		for(int i = 0; i < nodeCount; i++)
		{
			Node n = g.getNode(i);
			
			for(int j = i; j < nodeCount; j++)
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
					
					if(pointDistance == 1)
					{
						if(xNN - xN == 1)
						{
							//x edge
							Edge e = g.addEdge("x:"+n.getId()+"|"+nn.getId(), n, nn);
							e.addAttribute("weight", 1);
							e.addAttribute("ui.color", 0.0);
						}
						
						if(yNN - yN == 1)
						{
							//y edge
							Edge e = g.addEdge("y:"+n.getId()+"|"+nn.getId(), n, nn);
							e.addAttribute("weight", 1);
							e.addAttribute("ui.color", 0.5);
						}
						
						if(zNN - zN == 1)
						{
							//z edge
							Edge e = g.addEdge("z:"+n.getId()+"|"+nn.getId(), n, nn);
							e.addAttribute("weight", 1);
							e.addAttribute("ui.color", 1);
						}
					}
						//Connect
					if(pointDistance == squareBaseDist)
					{
						Edge e = g.addEdge("square:"+n.getId()+"|"+nn.getId(), n, nn);
						e.addAttribute("weight", squareBaseDist);
						e.addAttribute("ui.color", 0.15);
					}
				}
			}
		}
		
		return g;
	}
	
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private static double trunc(double value)
	{
		return (double) Math.round(value * 100)/100;
	}

}
