package pt.force.experimental;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/**
 * 
 * @author Force
 *
 */
public class GraphParser
{
	/**
	 * 
	 * @param filename
	 * @param g
	 */
	public static void graphToFile(String filename, Graph g)
	{
		File f = new File(filename.concat(".json"));
		FileWriter fw;
		
		if(f.canWrite() &&	f.isFile())
		{
			try
			{
				fw = new FileWriter(f);
				
				Collection<Node> nodeCol = g.getNodeSet();
				Collection<Edge> edgeCol = g.getEdgeSet();
				
				
				JsonObject jsonGraph = new JsonObject();
				JsonObject jGraphAttributes = new JsonObject();
				for(String attributeName : g.getAttributeKeySet())
				{
					String attribute = g.getAttribute(attributeName).toString();
					
					jGraphAttributes.addProperty(attributeName, attribute);
				}
				jsonGraph.add(FileGraphIdentifiers.GraphAttributes.toString(), jGraphAttributes);
				jsonGraph.addProperty(FileGraphIdentifiers.NodeCount.toString(), g.getNodeCount());
				jsonGraph.addProperty(FileGraphIdentifiers.EdgeCount.toString(), g.getEdgeCount());
				
				JsonArray jsonNodes = new JsonArray();
				for(Node n: nodeCol)
				{
					JsonObject jsonNode = new JsonObject();
					
					String nodeID = n.getId();
					jsonNode.addProperty(FileGraphIdentifiers.NodeID.toString(), nodeID);
					
					for(String attributeName : n.getAttributeKeySet())
					{
						String attribute = n.getAttribute(attributeName).toString();
						
						String type = n.getAttribute(attributeName).getClass().getCanonicalName();
						
						JsonObject attriTuple = new JsonObject();
						attriTuple.addProperty(FileGraphIdentifiers.AttributeValue.toString(), attribute);
						attriTuple.addProperty(FileGraphIdentifiers.AttributeType.toString(), type);
						
						jsonNode.add(attributeName, attriTuple);
					}
					jsonNodes.add(jsonNode);
				}
				jsonGraph.add(FileGraphIdentifiers.Nodes.toString(), jsonNodes);
				
				
				JsonArray jsonEdges = new JsonArray();
				for(Edge e: edgeCol)
				{
					JsonObject jsonEdge = new JsonObject();
					
					String edgeID = e.getId();
					jsonEdge.addProperty(FileGraphIdentifiers.EdgeID.toString(), edgeID);
					jsonEdge.addProperty(FileGraphIdentifiers.EdgeNode1ID.toString(), e.getNode0().getId());
					jsonEdge.addProperty(FileGraphIdentifiers.EdgeNode2ID.toString(), e.getNode1().getId());
					
					
					for(String attributeName : e.getAttributeKeySet())
					{
						String attribute = e.getAttribute(attributeName).toString();
						
						String type = e.getAttribute(attributeName).getClass().getCanonicalName();
						JsonObject attriTuple = new JsonObject();
						attriTuple.addProperty(FileGraphIdentifiers.AttributeValue.toString(), attribute);
						attriTuple.addProperty(FileGraphIdentifiers.AttributeType.toString(), type);
						
						jsonEdge.add(attributeName, attriTuple);
					}
					
					jsonEdges.add(jsonEdge);
				}
				jsonGraph.add(FileGraphIdentifiers.Edges.toString(), jsonEdges);
	
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

	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static Graph graphFromFile(String filename)
	{
		Graph g = null;
		
		File f = new File(filename.concat(".json"));
		
		if(!f.isFile())
		{
			return null;
		}
		else if(!f.canRead())
		{
			return null;
		}
		else
		{
			try
			{
				FileReader fr = new FileReader(f);
				Gson gson = new GsonBuilder().create();
				
				BufferedReader bf = new BufferedReader(fr);
				String line = null;
			    StringBuilder stringBuilder = new StringBuilder();
				
			    while((line = bf.readLine()) != null)
			    {
		            stringBuilder.append(line.trim());
		        }
			    
			    String theJson = stringBuilder.toString();
				JsonObject data = gson.fromJson(theJson, JsonObject.class);
				

				bf.close();
				fr.close();
				
				
				g = createGraph(data);
				insertNodes(data, g);
				insertEdges(data, g);			
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return g;
	}

	
	/**
	 * 
	 * @param data
	 * @return
	 */
	private static Graph createGraph(JsonObject data)
	{
		Graph graph = new DefaultGraph("default_name", false, false);
		
		HashMap<String, String> graphAttributes = extractAttributes(data);
		
		for(Entry<String, String> attribute : graphAttributes.entrySet())
		{
			String key = attribute.getKey().replace("\"", "");
			String value = attribute.getValue().toString().replace("\"", "");
			
			graph.addAttribute(key, value);
		}
		
		return graph;
	}

	
	/**
	 * 
	 * @param data
	 * @param g
	 */
	private static void insertEdges(JsonObject data, Graph g)
	{	
		JsonArray jsonEdges = data.getAsJsonArray(FileGraphIdentifiers.Edges.toString());
		
		for(JsonElement jsonEdge : jsonEdges)
		{
			String id = null;
			Node from = null;
			Node to = null;
			HashMap<String, Object> attributes = new HashMap<>();
			
			for(Entry<String, JsonElement> e : jsonEdge.getAsJsonObject().entrySet())
			{
				String key = e.getKey().replace("\"", "");
				
				Object value = null;
				
				if(!e.getValue().isJsonObject())
				{
					value = e.getValue().toString().replace("\"", "");
				}
				else
				{
					JsonObject sub = e.getValue().getAsJsonObject();
					String subValue = sub.get(FileGraphIdentifiers.AttributeValue.toString()).getAsString();
					String type = sub.get(FileGraphIdentifiers.AttributeType.toString()).getAsString();
					
					value = ClassTypeUtil.parseValue(subValue, ClassTypeUtil.valueOfEnum(type));
				}
				
				switch(FileGraphIdentifiers.valueOfEnum(key))
				{
					case EdgeID:
						id = (String)value;
						break;
					case EdgeNode1ID:
						from = g.getNode((String)value);
						break;
					case EdgeNode2ID:
						to = g.getNode((String)value);
						break;

					default:
						attributes.put(key, value);
						break;
				}
			}
			
			if(id != null && from != null && to != null)
			{
				Edge e = g.addEdge(id, from, to);
				for(Entry<String, Object> attribute : attributes.entrySet())
				{
					e.setAttribute(attribute.getKey(), attribute.getValue());
				}
			}
		}
	}

	/**
	 * 
	 * @param data
	 * @param graph
	 */
	private static void insertNodes(JsonObject data, Graph graph)
	{		
		JsonArray jsonNodes = data.getAsJsonArray(FileGraphIdentifiers.Nodes.toString());
		
		for(JsonElement jsonNode : jsonNodes)
		{		
			String id = null;
			HashMap<String, Object> attributes = new HashMap<>();
			
			for(Entry<String, JsonElement> e : jsonNode.getAsJsonObject().entrySet())
			{
				String key = e.getKey().replace("\"", "");
				
				Object value = null;
				
				if(!e.getValue().isJsonObject())
				{
					value = e.getValue().toString().replace("\"", "");
				}
				else
				{
					JsonObject sub = e.getValue().getAsJsonObject();
					String subValue = sub.get(FileGraphIdentifiers.AttributeValue.toString()).getAsString();
					String type = sub.get(FileGraphIdentifiers.AttributeType.toString()).getAsString();
					
					value = ClassTypeUtil.parseValue(subValue, ClassTypeUtil.valueOfEnum(type));
				}
				
				switch(FileGraphIdentifiers.valueOfEnum(key))
				{
					case NodeID:
						id = (String) value;
						break;
					default:
						attributes.put(key, value);
						break;
				}
			}
			
			if(id != null)
			{
				Node n = graph.addNode(id);
				for(Entry<String, Object> attribute : attributes.entrySet())
				{
					n.setAttribute(attribute.getKey(), attribute.getValue());
				}
			}
		}
	}


	/**
	 * 
	 * @param data
	 * @return
	 */
	private static HashMap<String, String> extractAttributes(JsonObject data)
	{
		HashMap<String, String> graphAttributes = new HashMap<>();
		
		JsonObject jsonGraphAttributes = data.getAsJsonObject(FileGraphIdentifiers.GraphAttributes.toString());
		
		for(Entry<String, JsonElement> e : jsonGraphAttributes.entrySet())
		{
			String key = e.getKey().replace("\"", "");
			String value = e.getValue().toString().replace("\"", "");
			
			graphAttributes.put(key, value);
		}
		
		return graphAttributes;
	}
}
