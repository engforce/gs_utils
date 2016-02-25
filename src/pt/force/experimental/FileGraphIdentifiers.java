package pt.force.experimental;

/**
 * An enum class that hopes to provide a standard of specification when saving/reading graphs from/into json files.
 *  
 * @author Force
 */
public enum FileGraphIdentifiers
{
	/**Graph ID reference - graphID*/
	GraphID			("graphID"),
	/**Node ID reference - nodeID*/
	NodeID			("nodeID"),
	/**First node off an edge reference - node1ID*/
	EdgeNode1ID		("node1ID"),
	/**Second node off an edge reference - node2ID*/
	EdgeNode2ID		("node2ID"),
	/**Edge ID reference - edgeID*/
	EdgeID			("edgeID"),
	/**Graph attributes reference - graphAttributes*/
	GraphAttributes	("graphAttributes"),
	/**Number of nodes in a graph reference - nodeCount*/
	NodeCount		("nodeCount"),
	/**Number of edges in a graph reference - edgeCount*/
	EdgeCount		("edgeCount"),
	/**Json object name for where to put the nodes reference - nodes*/
	Nodes			("nodes"),
	/**Json object name for where to put the edges reference - edges*/
	Edges			("edges"),
	/**Attribute type field reference - type*/
	AttributeType	("type"),
	/**Attribute value field reference - value*/
	AttributeValue	("value"),
	/**DEFAULT reference - (null)*/
	DEFAULT			(null);
	
	/**
	 * A String that holds the value of an enum's object 
	 */
	private String enumValue;
	
	/**
	 * Private constructor to allow the enum to have a custom String value
	 * @param enumValue a string that represents an enum
	 */
	private FileGraphIdentifiers(String enumValue)
	{
		this.enumValue = enumValue;
	}

	
	@Override
	public String toString()
	{
		return this.enumValue;
	}
	
	
	/**
	 * A custom valueOf method, this one uses the custom string names used on the enum.
	 * If not found it returns the DEFAULT enum member.
	 * 
	 * @param name a String the hopefully represents a FileGraphIdentifiers enum member.
	 * @return the corresponding enum member or the DEFAULT one if not found.
	 */
	public static FileGraphIdentifiers valueOfEnum(String name)
	{
		/*
		 * Search the ClassTypeUtil for the supplied name
		 * !!!!NEEDS TO BE OPTIMIZED!!!! 
		 */
		for(FileGraphIdentifiers identifier : FileGraphIdentifiers.values())
		{
			if(identifier.toString().equals(name))
			{
				return identifier;
			}
		}
		
		return DEFAULT;
	}
}
