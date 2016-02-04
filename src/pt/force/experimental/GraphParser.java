package pt.force.experimental;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GraphParser
{
	public static void graphToFile(String filename, Graph g)
	{
		File f = new File(filename.concat(".json"));
		FileWriter fw;
		
		if(true /*f.canWrite() &&	f.isFile()*/)
		{
			try
			{
				fw = new FileWriter(f);
				
				Collection<Node> nodeCol = g.getNodeSet();
				Collection<Edge> edgeCol = g.getEdgeSet();
				
				
				JsonObject jsonGraph = new JsonObject();
				for(String attributeName : g.getAttributeKeySet())
				{
					String attribute = String.valueOf(g.getAttribute(attributeName));
					jsonGraph.addProperty(attributeName, attribute);
				}
				jsonGraph.addProperty("nodeCount", g.getNodeCount());
				jsonGraph.addProperty("edgeCount", g.getEdgeCount());
				
				JsonArray jsonNodes = new JsonArray();
				for(Node n: nodeCol)
				{
					JsonObject jsonNode = new JsonObject();
					
					String nodeID = n.getId();
					jsonNode.addProperty("ID", nodeID);
					
					for(String attributeName : n.getAttributeKeySet())
					{
						String attribute = String.valueOf(n.getAttribute(attributeName));
						
						jsonNode.addProperty(attributeName, attribute);
					}
					jsonNodes.add(jsonNode);
				}
				jsonGraph.add("nodes", jsonNodes);
				
				
				JsonArray jsonEdges = new JsonArray();
				for(Edge e: edgeCol)
				{
					JsonObject jsonEdge = new JsonObject();
					
					String edgeID = e.getId();
					jsonEdge.addProperty("ID", edgeID);
					jsonEdge.addProperty("node1ID", e.getNode0().getId());
					jsonEdge.addProperty("node2ID", e.getNode1().getId());
					
					for(String attributeName : e.getAttributeKeySet())
					{
						String attribute = String.valueOf(e.getAttribute(attributeName));
						
						jsonEdge.addProperty(attributeName, attribute);
					}
					
					jsonEdges.add(jsonEdge);
				}
				jsonGraph.add("edges", jsonEdges);
				
				
	
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				fw.write(gson.toJson(jsonGraph));
	
				fw.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}		
	}
	
	public static Graph graphFromFile()
	{
		Graph g = null;
		
		return g;
	}
}
