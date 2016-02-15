package pt.force.experimental;

/**
 * 
 * @author Force
 *
 */
public enum ClassTypeUtil
{
	Character	(java.lang.Integer.class.getCanonicalName()),
	String		(java.lang.String.class.getCanonicalName()),
	Boolean		(java.lang.Integer.class.getCanonicalName()),
	Byte		(java.lang.Integer.class.getCanonicalName()),
	Short		(java.lang.Integer.class.getCanonicalName()),
	Integer		(java.lang.Integer.class.getCanonicalName()),
	Long		(java.lang.Integer.class.getCanonicalName()),
	Float		(java.lang.Float.class.getCanonicalName()),
	Double		(java.lang.Double.class.getCanonicalName()),
	Number		(java.lang.Integer.class.getCanonicalName()),
	DEFAULT		(null);
	
	/**
	 * 
	 */
	private String enumValue;
	
	/**
	 * 
	 * @param enumValue
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
	 * 
	 * @param name
	 * @return
	 */
	public static ClassTypeUtil valueOfEnum(String name)
	{
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
	 * 
	 * @param subValue
	 * @param type
	 * @return
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
}
