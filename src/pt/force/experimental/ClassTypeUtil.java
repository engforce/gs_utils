package pt.force.experimental;

/**
 * An enum class that aims to map it's members to the corresponding java data type class, 
 * as well as to providing a way to easily parse values in a dynamic way.
 * 
 * @author Force
 */
public enum ClassTypeUtil
{
	/**java.lang.Character*/
	Character	(java.lang.Character.class.getCanonicalName()),
	/**java.lang.String*/
	String		(java.lang.String.class.getCanonicalName()),
	/**java.lang.Boolean*/
	Boolean		(java.lang.Boolean.class.getCanonicalName()),
	/**java.lang.Byte*/
	Byte		(java.lang.Byte.class.getCanonicalName()),
	/**java.lang.Short*/
	Short		(java.lang.Short.class.getCanonicalName()),
	/**java.lang.Integer*/
	Integer		(java.lang.Integer.class.getCanonicalName()),
	/**java.lang.Long*/
	Long		(java.lang.Long.class.getCanonicalName()),
	/**java.lang.Float*/
	Float		(java.lang.Float.class.getCanonicalName()),
	/**java.lang.Double*/
	Double		(java.lang.Double.class.getCanonicalName()),
	/**java.lang.Number*/
	Number		(java.lang.Number.class.getCanonicalName()),
	/**DEFAULT reference (null)*/
	DEFAULT		(null);
	
	/**
	 * A String that holds the value of an enum's object 
	 */
	private String enumValue;
	
	
	/**
	 * Private constructor to allow the enum to have a custom String value
	 * @param enumValue a string that represents an enum
	 */
	private ClassTypeUtil(String enumValue)
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
	 * @param name a String the hopefully represents a ClassTypeUtil enum member.
	 * @return the corresponding enum member or the DEFAULT one if not found.
	 */
	public static ClassTypeUtil valueOfEnum(String name)
	{
		/*
		 * Search the ClassTypeUtil for the supplied name
		 * !!!!NEEDS TO BE OPTIMIZED!!!! 
		 */
		for(ClassTypeUtil identifier : ClassTypeUtil.values())
		{
			if(identifier.toString().equals(name))
			{
				return identifier;
			}
		}
		return DEFAULT;
	}
	
	
	/**
	 * A method that provides a way to parse String into a value of the corresponding 
	 * java data type class that the classType represents. 
	 * It returns null if the classType represents Number, DEFAULT enum member or if out of range.
	 * 
	 * @param value the value to parse.
	 * @param classType a ClassTypeUtil enum object.
	 * @return an object of the classType representative type containing the parsed value. 
	 */
	public static Object parseValue(String value, ClassTypeUtil classType)
	{
		switch(classType)
		{
			case Character:
				return java.lang.Character.valueOf(value.toCharArray()[0]);
			case String:
				return value;
			case Boolean:
				return java.lang.Boolean.parseBoolean(value);
			case Byte:
				return java.lang.Byte.parseByte(value);
			case Short:
				return java.lang.Short.parseShort(value);
			case Integer:
				return java.lang.Integer.parseInt(value);
			case Long:
				return java.lang.Long.parseLong(value);
			case Float:
				return java.lang.Float.parseFloat(value);
			case Double:
				return java.lang.Double.parseDouble(value);
			case Number:
				return null;
			case DEFAULT:
				return null;
		}
		return null;
	}
	
	
	/**
	 * Given an ClassTypeUtil enum object return it's corresponding java data type class,
	 * it may return null if classType is the DEFAULT enum member or if it's value doesn't
	 * doesn't correspond to the existing ones.
	 * 
	 * @param classType a ClassTypeUtil enum object.
	 * @return the enum object java data type class.
	 */
	public static Class<?> getClass(ClassTypeUtil classType)
	{		
		switch(classType)
		{
			case Character:
				return java.lang.Character.class;
			case String:
				return java.lang.String.class;
			case Boolean:
				return java.lang.Boolean.class;
			case Byte:
				return java.lang.Byte.class;
			case Short:
				return java.lang.Short.class;
			case Integer:
				return java.lang.Integer.class;
			case Long:
				return java.lang.Long.class;
			case Float:
				return java.lang.Float.class;
			case Double:
				return java.lang.Double.class;
			case Number:
				return java.lang.Number.class;
			case DEFAULT:
				return null;
			default:
				break;
		}
		return null;
	}
}