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
 * A graph parsing library that provides a standard way of saving/loading graphs created with the GraphStream library
 * @author Force
 *
 */
public class GraphParser
{
	/**
	 * Take the given a GraphStream graph and writes it into a .json file specified with <b>filename</b>
	 * @param filename the path to the file (includes the file name)
	 * @param graph the graph object
	 * @throws IOException throws an exception if the file cannot be written or if its not a file 
	 */
	public static void graphToFile(String filename, Graph graph) throws IOException
	{
		File file = new File(filename.concat(".json"));
		FileWriter fileWriter;
		
		if(file.canWrite() && file.isFile())
		{
			try
			{
				//Start the file writer
				fileWriter = new FileWriter(file);
				
				//Get the nodes, edges collections
				Collection<Node> nodeCollection = graph.getNodeSet();
				Collection<Edge> edgeCollection = graph.getEdgeSet();
				
				//JsonObject that represents the graph
				JsonObject jGraph = new JsonObject();
				
				//Store the graphs ID
				jGraph.addProperty(FileGraphIdentifiers.GraphID.toString(), graph.getId().toString());
				
				//JsonObject that represent the graphs attributes
				JsonObject jGraphAttributes = new JsonObject();
				
				//Fecth all the graphs attributes
				for(String attributeName : graph.getAttributeKeySet())
				{
					String attribute = graph.getAttribute(attributeName).toString();
					
					jGraphAttributes.addProperty(attributeName, attribute);
				}
				
				//Add the graphs attributes
				jGraph.add(FileGraphIdentifiers.GraphAttributes.toString(), jGraphAttributes);
				
				//Store the node count
				jGraph.addProperty(FileGraphIdentifiers.NodeCount.toString(), graph.getNodeCount());
				//Store the edge count
				jGraph.addProperty(FileGraphIdentifiers.EdgeCount.toString(), graph.getEdgeCount());
				
				
				//JsonArray that represents a list with all nodes
				JsonArray jNodes = new JsonArray();
				
				//Run through the node collection
				for(Node node: nodeCollection)
				{
					//Create a new json node
					JsonObject jNode = new JsonObject();
					
					//Store the nodeID
					jNode.addProperty(FileGraphIdentifiers.NodeID.toString(), node.getId());
					
					//Fetch its attributes
					for(String attributeName : node.getAttributeKeySet())
					{
						//Get the attribute
						String attribute = node.getAttribute(attributeName).toString();
						
						//Get the attribute's class type
						String type = node.getAttribute(attributeName).getClass().getCanonicalName();
						
						//Create a tuple to store the attribute and its own type
						JsonObject attriTuple = new JsonObject();
						attriTuple.addProperty(FileGraphIdentifiers.AttributeValue.toString(), attribute);
						attriTuple.addProperty(FileGraphIdentifiers.AttributeType.toString(), type);
						
						//Store this attribute tuple into the json node
						jNode.add(attributeName, attriTuple);
					}
					//Store the json node into the array of nodes
					jNodes.add(jNode);
				}
				//Store the array of nodes into the json graph
				jGraph.add(FileGraphIdentifiers.Nodes.toString(), jNodes);
				
				
				//JsonArray that represents a list with all edges
				JsonArray jEdges = new JsonArray();

				//Run through the edge collection
				for(Edge edge: edgeCollection)
				{
					//Create a new json edge
					JsonObject jEdge = new JsonObject();
					
					//Store the edge ID
					jEdge.addProperty(FileGraphIdentifiers.EdgeID.toString(), edge.getId());
					//Store both linked nodes IDs
					jEdge.addProperty(FileGraphIdentifiers.EdgeNode1ID.toString(), edge.getNode0().getId());
					jEdge.addProperty(FileGraphIdentifiers.EdgeNode2ID.toString(), edge.getNode1().getId());
					
					//Go through the edge attributes
					for(String attributeName : edge.getAttributeKeySet())
					{
						//Get the attribute
						String attribute = edge.getAttribute(attributeName).toString();
						
						//Get the attribute's class type
						String type = edge.getAttribute(attributeName).getClass().getCanonicalName();
						
						//Create a tuple to store the attribute and its own type
						JsonObject attriTuple = new JsonObject();
						attriTuple.addProperty(FileGraphIdentifiers.AttributeValue.toString(), attribute);
						attriTuple.addProperty(FileGraphIdentifiers.AttributeType.toString(), type);
						
						//Store this attribute tuple into the json node
						jEdge.add(attributeName, attriTuple);
					}
					//Store the json edge into the array of nodes
					jEdges.add(jEdge);
				}
				//Store the array of edges into the json graph
				jGraph.add(FileGraphIdentifiers.Edges.toString(), jEdges);
	
				//Build a pretty Gson for printing
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				//Write the whole json graph into disk
				fileWriter.write(gson.toJson(jGraph));
				//Close the file writer
				fileWriter.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			//If the file is not an actual file, throw an IOException
			if(!file.isFile())
				throw new IOException("File is not actually a file");
			//If the file cannot be written, throw an IOException
			if(!file.canWrite())
				throw new IOException("File could not be written");
		}
	}

	
	/**
	 * Given a filename path to a json stored graph this method reads the file's content and creates </br>
	 * a graph based on that data
	 * @param filename the path to the file (includes the file name)
	 * @return the created graph
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
				
				
				g = createGraph(data, getGraphID(data));
				insertNodes(data, g);
				insertEdges(data, g);			
			}
			catch(IOException | NullPointerException e)
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
	private static String getGraphID(JsonObject data) throws NullPointerException
	{
		JsonElement graphIDElement = data.get(FileGraphIdentifiers.GraphID.toString());
		
		if(graphIDElement == null)
			throw new NullPointerException("The data doesn't provide a graph ID");
		
		return data.get(FileGraphIdentifiers.GraphID.toString()).getAsString(); 
	}


	/**
	 * 
	 * @param data
	 * @return
	 */
	private static Graph createGraph(JsonObject data, String graphID)
	{
		Graph graph = new DefaultGraph(graphID, false, false);
		
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
