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
		
		if(!file.isFile())
		{
			//If the file is not an actual file, throw an IOException
			throw new IOException("File is not actually a file");
		}
		else if(!file.canWrite())
		{
			//If the file cannot be written, throw an IOException
			throw new IOException("File could not be written");
		}
		else
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
	}

	
	/**
	 * Given a filename path to a json stored graph this method reads the file's content and creates 
	 * a graph based on that data
	 * @param filename the path to the file (includes the file name)
	 * @return the created graph
	 * @throws IOException throws an exception if the file cannot be read or if its not a file  
	 */
	public static Graph graphFromFile(String filename) throws IOException
	{
		Graph graph = null;
		
		File file = new File(filename.concat(".json"));
		
		if(!file.isFile())
		{
			//If the file is not an actual file, throw an IOException
			throw new IOException("File is not actually a file");
		}
		else if(!file.canRead())
		{
			//If the file cannot be written, throw an IOException
			throw new IOException("File could not be read");
		}
		else
		{
			try
			{	
				//Read the json file and dump its content into a string
			    String jsonFileString = fileContentToString(file);
			    
			    //Initialize a gson object for data convertion
			    Gson gson = new GsonBuilder().create();
			    //Take the json file string and convert it into a Json Object
				JsonObject data = gson.fromJson(jsonFileString, JsonObject.class);
				
				//Create the graph with its attributes
				graph = createGraph(data, getGraphID(data));
				//Insert the nodes
				insertNodes(data, graph);
				//Insert the edges
				insertEdges(data, graph);			
			}
			catch(IOException | NullPointerException e)
			{
				e.printStackTrace();
			}
		}
		
		if(graph == null)
		{
			//Throw a NullPointerException if the graph still is null
			throw new NullPointerException("The graph could not be created");
		}
		return graph;
	}

	
	/**
	 * Takes a file and reads its content into a String
	 * @param file a java File Object
	 * @return String with the content
	 * @throws IOException 
	 */
	protected static String fileContentToString(File file) throws IOException
	{
		//Create a new FileReader
		FileReader fileReader = new FileReader(file);
		
		//Create a BufferedReader
		BufferedReader readBuffer = new BufferedReader(fileReader);
		//Auxiliary String that holds, at a given time, a line from the file
		String line = null;
		//Create a StringBuilder to store the contents
	    StringBuilder stringBuilder = new StringBuilder();
		
	    //Go through each line in the file
	    while((line = readBuffer.readLine()) != null)
	    {
	    	//Append each line
            stringBuilder.append(line.trim());
        }
	    
	    //Close the reader
	    fileReader.close();
	    
	    return stringBuilder.toString();
	}
	
	
	/**
	 * Attempts to retrieve the GraphID from the given data, if not successful throws an exception
	 * @param data a JsonObject containing a graph
	 * @return the graphID
	 * @throws NullPointerException
	 */
	protected static String getGraphID(JsonObject data) throws NullPointerException
	{
		//Get the graphID in the form of a JsonElement (may be null if not found)
		JsonElement graphIDElement = data.get(FileGraphIdentifiers.GraphID.toString());
		
		//If null throw an exception
		if(graphIDElement == null)
			throw new NullPointerException("The data doesn't provide a graph ID");
		
		return data.get(FileGraphIdentifiers.GraphID.toString()).getAsString(); 
	}


	/**
	 * Given a JsonObject containing the graph data and it's ID, it builds the baseline of a GraphStream Graph,
	 * a Graph with it's own attributes
	 * @param data
	 * @return
	 */
	protected static Graph createGraph(JsonObject data, String graphID)
	{
		//Create a graph using the given graph ID
		Graph graph = new DefaultGraph(graphID, false, false);
		
		//Fetch the graphs attributes using the data provided
		HashMap<String, String> graphAttributes = extractAttributes(data);
		
		//Go through the map containing the attributes and add then into the graph
		for(Entry<String, String> attribute : graphAttributes.entrySet())
		{
			//The .replace("\"", "") thingy is to make sure we don't read " symbols into our key/value Strings
			//It would prove a problem when saving a graph created using this method, causing it
			//to have keys/values with, at least, double quotes
			String key = attribute.getKey().replace("\"", "");
			String value = attribute.getValue().toString().replace("\"", "");
			
			graph.addAttribute(key, value);
		}
		return graph;
	}

	
	/**
	 * From the supplied data, retrieve the edges on it and add them into the graph
	 * @param data JsonObject containing the data
	 * @param graph an existing graph
	 */
	protected static void insertEdges(JsonObject data, Graph graph)
	{	
		//Find the edges within the given data
		JsonArray jEdges = data.getAsJsonArray(FileGraphIdentifiers.Edges.toString());
		
		//Run through every edge on the jEdges JsonArray
		for(JsonElement jEdge : jEdges)
		{
			String edgeID = null;
			Node fromNode = null;
			Node toNode = null;
			HashMap<String, Object> attributes = new HashMap<>();
			
			//Given a JsonElement that represents an edge
			for(Entry<String, JsonElement> e : jEdge.getAsJsonObject().entrySet())
			{
				//Get it's key
				String key = e.getKey().replace("\"", "");
				
				//Assume a generic data type for the value
				Object value = null;
				
				//Find out if the value is an JsonObject or not
				if(!e.getValue().isJsonObject())
				{
					//If it isn't, then it must be an actual value
					//Convert it into a string removing any quotes
					value = e.getValue().toString().replace("\"", "");
				}
				else
				{
					//If not, then we are before a JsonObject that contains a tuple of data
					//The tuple contains the value and it's Java Data Type
					
					//Take the value and convert it into a JsonObject
					JsonObject sub = e.getValue().getAsJsonObject();
					
					//Take the value as a String
					String subValue = sub.get(FileGraphIdentifiers.AttributeValue.toString()).getAsString();
					
					//Get the value's type
					String type = sub.get(FileGraphIdentifiers.AttributeType.toString()).getAsString();
					
					//Using the information we took, use the ClassTypeUtil generic parseValue method
					//to parse any string value into the specified Java Data Type
					value = ClassTypeUtil.parseValue(subValue, ClassTypeUtil.valueOfEnum(type));
				}
				
				//Fill the edge related data (ID, connecting nodes and attributes)
				switch(FileGraphIdentifiers.valueOfEnum(key))
				{
					case EdgeID:
						edgeID = (String)value;
						break;
					case EdgeNode1ID:
						fromNode = graph.getNode((String)value);
						break;
					case EdgeNode2ID:
						toNode = graph.getNode((String)value);
						break;
					//The generic attribute are not defined on the FileGraphIdentifiers enum
					//so they fall into the switch's default case
					default:
						attributes.put(key, value);
						break;
				}
			}
			
			//Check if the edge data has been successfully extracted
			if(edgeID != null && fromNode != null && toNode != null)
			{
				//Add the edge into the graph
				Edge e = graph.addEdge(edgeID, fromNode, toNode);
				//Add the attributes into the edge
				for(Entry<String, Object> attribute : attributes.entrySet())
				{
					e.setAttribute(attribute.getKey(), attribute.getValue());
				}
			}
		}
	}

	
	/**
	 * From the supplied data, retrieve the nodes on it and add them into the graph
	 * @param data JsonObject containing the data
	 * @param graph an existing graph
	 */
	protected static void insertNodes(JsonObject data, Graph graph)
	{		
		//Find the nodes within the given data
		JsonArray jsonNodes = data.getAsJsonArray(FileGraphIdentifiers.Nodes.toString());
		
		//Run through every node on the jNodes JsonArray
		for(JsonElement jsonNode : jsonNodes)
		{		
			String id = null;
			HashMap<String, Object> attributes = new HashMap<>();
			
			//Given a JsonElement that represents an node
			for(Entry<String, JsonElement> e : jsonNode.getAsJsonObject().entrySet())
			{
				//Get it's key
				String key = e.getKey().replace("\"", "");
				
				//Assume a generic data type for the value
				Object value = null;
				
				//Find out if the value is an JsonObject or not
				if(!e.getValue().isJsonObject())
				{
					//If it isn't, then it must be an actual value
					//Convert it into a string removing any quotes
					value = e.getValue().toString().replace("\"", "");
				}
				else
				{
					//If not, then we are before a JsonObject that contains a tuple of data
					//The tuple contains the value and it's Java Data Type
					
					//Take the value and convert it into a JsonObject
					JsonObject sub = e.getValue().getAsJsonObject();
					
					//Take the value as a String
					String subValue = sub.get(FileGraphIdentifiers.AttributeValue.toString()).getAsString();
					
					//Get the value's type
					String type = sub.get(FileGraphIdentifiers.AttributeType.toString()).getAsString();
					
					//Using the information we took, use the ClassTypeUtil generic parseValue method
					//to parse any string value into the specified Java Data Type
					value = ClassTypeUtil.parseValue(subValue, ClassTypeUtil.valueOfEnum(type));
				}
				
				//Fill the node related data (ID and attributes)
				switch(FileGraphIdentifiers.valueOfEnum(key))
				{
					case NodeID:
						id = (String) value;
						break;
					//The generic attribute are not defined on the FileGraphIdentifiers enum
					//so they fall into the switch's default case
					default:
						attributes.put(key, value);
						break;
				}
			}
			
			//Check if the node data has been successfully extracted
			if(id != null)
			{
				//Add the node into the graph
				Node n = graph.addNode(id);
				//Add the attributes into the node
				for(Entry<String, Object> attribute : attributes.entrySet())
				{
					n.setAttribute(attribute.getKey(), attribute.getValue());
				}
			}
		}
	}


	/**
	 * Given a JsonObject that contains data about a graph, this method returns the contained graph's attributes 
	 * in the form of an hash map
	 * @param data JsonObject containing the data
	 * @return a map of the graph's attributes
	 */
	protected static HashMap<String, String> extractAttributes(JsonObject data)
	{
		HashMap<String, String> graphAttributes = new HashMap<>();
		
		//Attempt to retrieve the graph's attributes into a JsonObject
		JsonObject jsonGraphAttributes = data.getAsJsonObject(FileGraphIdentifiers.GraphAttributes.toString());
		
		//Run through an entry set that possibly contains the attributes 
		for(Entry<String, JsonElement> e : jsonGraphAttributes.entrySet())
		{
			String key = e.getKey().replace("\"", "");
			String value = e.getValue().toString().replace("\"", "");
			
			graphAttributes.put(key, value);
		}
		
		return graphAttributes;
	}
}
