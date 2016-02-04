package pt.force.experimental;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

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
		File f = new File(filename);
		FileWriter fw;
		
		if(true /*f.canWrite() &&	f.isFile()*/)
		{
			try
			{
				fw = new FileWriter(f);
				
				Collection<Node> nodeCol = g.getNodeSet();
				Collection<Edge> edgeCol = g.getEdgeSet();
				
				System.out.println("printing nodes");
				
				JsonObject jsonGraph = new JsonObject();
				
				for(String attributeName : g.getAttributeKeySet())
				{
					String attribute = String.valueOf(g.getAttribute(attributeName));
					jsonGraph.addProperty(attributeName, attribute);
				}
				
				JsonArray jsonNodes = new JsonArray();
				JsonArray jsonEdges = new JsonArray();
				
//				HashMap<String, HashMap<String, String>> nodeDic = new HashMap<>();
				
				for(Node n: nodeCol)
				{
					JsonObject jsonNode = new JsonObject();
					
					String nodeID = n.getId();
					jsonNode.addProperty("ID", nodeID);
					
//					HashMap<String, String> nodeAttributes = new HashMap<>();
					
					for(String attributeName : n.getAttributeKeySet())
					{
						String attribute = String.valueOf(n.getAttribute(attributeName));
//						nodeAttributes.put(attributeName, attribute);
						
						jsonNode.addProperty(attributeName, attribute);
					}
					
//					nodeDic.put(nodeID, nodeAttributes);
					
					jsonNodes.add(jsonNode);
				}
				
//				writeNodes(fw, nodeDic);
				
				jsonGraph.add("nodes", jsonNodes);
				
				
//				HashMap<String, HashMap<String, String>> edgeDic = new HashMap<>();
				
				for(Edge e: edgeCol)
				{
					JsonObject jsonEdge = new JsonObject();
					
					String edgeID = e.getId();
					jsonEdge.addProperty("ID", edgeID);
					jsonEdge.addProperty("node1ID", e.getNode0().getId());
					jsonEdge.addProperty("node2ID", e.getNode1().getId());
					
//					HashMap<String, String> edgeAttributes = new HashMap<>();
					
					for(String attributeName : e.getAttributeKeySet())
					{
						String attribute = String.valueOf(e.getAttribute(attributeName));
//						edgeAttributes.put(attributeName, attribute);
						
						jsonEdge.addProperty(attributeName, attribute);
					}
					
//					edgeDic.put(edgeID, edgeAttributes);
					
					jsonEdges.add(jsonEdge);
				}
				
//				writeEdges(fw, edgeDic);
				
				jsonGraph.add("edges", jsonEdges);
				
				
				FileWriter aaa = new FileWriter("hueheu.json");
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				aaa.write(gson.toJson(jsonGraph));
				aaa.close();
				
				System.out.println("finished");
				
				fw.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}		
	}
	
	private static void writeEdges(FileWriter fw, HashMap<String, HashMap<String, String>> edgeDic)
	{
		try
		{
			fw.write("edges\n");
			for(Entry<String, HashMap<String, String>> eSet : edgeDic.entrySet())
			{
				String entryKey = eSet.getKey();
				fw.write("	edge:"+entryKey);
				fw.write("\n		attributes:\n");
				
				for(Entry<String, String> e : eSet.getValue().entrySet())
				{
					String aKey = e.getKey();
					String aVal = e.getValue();
					
					fw.write("			[key:"+aKey);
					fw.write(", ");
					fw.write("val:"+aVal+"]\n");
				}
			}
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}		
	}

	private static void writeNodes(FileWriter fw, HashMap<String, HashMap<String, String>> nodeDic)
	{
		try
		{	
			fw.write("nodes\n");
			for(Entry<String, HashMap<String, String>> eSet : nodeDic.entrySet())
			{
				String entryKey = eSet.getKey();
				fw.write("	node:"+entryKey);
				fw.write("\n		attributes:\n");
				
				for(Entry<String, String> e : eSet.getValue().entrySet())
				{
					String aKey = e.getKey();
					String aVal = e.getValue();
					
					fw.write("			[key:"+aKey);
					fw.write(", ");
					fw.write("val:"+aVal+"]\n");
				}
			}
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
	}

	public static Graph graphFromFile()
	{
		Graph g = null;
		
		return g;
	}
}
