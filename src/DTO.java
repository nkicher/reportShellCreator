import java.util.List;

public class DTO 
{
	private String mnuemonic;
	private String tab;
	private String bkNm;
	private String query;
	private List<Paramter> parameters;
	private List<Column> columns;
	
	
	public DTO() {}
	
	
	public DTO(String mnuemonic, String tab, String bkNm, String query, List<Paramter> parameters,
			List<Column> columns) 
	{
		this.mnuemonic = mnuemonic;
		this.tab = tab;
		this.bkNm = bkNm;
		this.query = query;
		this.parameters = parameters;
		this.columns = columns;
	}


	public String getMnuemonic() {
		return mnuemonic;
	}


	public void setMnuemonic(String mnuemonic) {
		this.mnuemonic = mnuemonic;
	}


	public String getTab() {
		return tab;
	}


	public void setTab(String tab) {
		this.tab = tab;
	}


	public String getBkNm() {
		return bkNm;
	}


	public void setBkNm(String bkNm) {
		this.bkNm = bkNm;
	}


	public String getQuery() {
		return query;
	}


	public void setQuery(String query) {
		this.query = query;
	}


	public List<Paramter> getParameters() {
		return parameters;
	}


	public void setParameters(List<Paramter> parameters) {
		this.parameters = parameters;
	}


	public List<Column> getColumns() {
		return columns;
	}


	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

}
