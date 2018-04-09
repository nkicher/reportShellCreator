
public class Column 
{
	private String fieldName;
	private String displayName;
	private String dataType;
	
	
	public Column() {}
			
			
	public Column(String fieldName, String displayName, String dataType) 
	{
		this.fieldName = fieldName;
		this.displayName = displayName;
		this.dataType = dataType;
	}


	public String getFieldName() {
		return fieldName;
	}


	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public String getDataType() {
		return dataType;
	}


	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	
}
