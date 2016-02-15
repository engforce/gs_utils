/**
 * 
 */
package pt.force.experimental;

/**
 * @author Force
 *
 */
public enum FileGraphIdentifiers
{
	NodeID("nodeID"),
	EdgeNode1ID("node1ID"),
	EdgeNode2ID("node2ID"),
	EdgeID("edgeID"),
	GraphAttributes("graphAttributes"),
	NodeCount("nodeCount"),
	EdgeCount("edgeCount"),
	Nodes("nodes"),
	Edges("edges"),
	AttributeType("type"),
	AttributeValue("value"),
	DEFAULT("");
	
	private String enumValue;
		
	private FileGraphIdentifiers(String enumValue)
	{
		this.enumValue = enumValue;
	}

	@Override
	public String toString()
	{
		return this.enumValue;
	}
	
	public static FileGraphIdentifiers valueOfEnum(String name)
	{
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
