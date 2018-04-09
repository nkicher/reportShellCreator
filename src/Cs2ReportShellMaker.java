import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 SELECT "POWRBCAP"."A_CPR_GRP_PMT_MTH"."AR_GRP_CD",   
         "POWRBCAP"."A_CPR_GRP_PMT_MTH"."PMT_MTHD_CD",   
         "POWRBCAP"."A_CPR_GRP_PMT_MTH"."MUNT_NO",   
         "POWRBCAP"."A_CPR_GRP_PMT_MTH"."GL_NO"  
    FROM "POWRBCAP"."A_CPR_GRP_PMT_MTH"  
    WHERE "POWRBCAP"."A_CPR_GRP_PMT_MTH"."AR_GRP_CD" <> ''ADMIN''
    AND AR_GRP_CD = ?
    AND PMT_MTHD_CD = ?
    AND MUNT_NO = ?
    AND GL_NO = ?
	
	
<parameters>
    <parameter name="AR_GRP_CD" type="string"/>
    <parameter name="PMT_MTHD_CD" type="string"/>
    <parameter name="MUNT_NO" type="string"/>
    <parameter name="GL_NO" type="string"/>
</parameters>
 */


public class Cs2ReportShellMaker
{
	private static final String INPUT 	 = "input/input.xlsx";
	private static final String SHELL 	 = "input/templates/shell";
	private static final String PARAMTER = "input/templates/parameter";
	private static final String COLUMN 	 = "input/templates/column";
	
	private static String input, shell, parameter, column;
	
	
	public static void main(String[] args) 
	{
		//input		= read(INPUT);
		shell 		= read(SHELL);
		parameter 	= read(PARAMTER);
		column 		= read(COLUMN);

		//pl(input);
		
		/*
		|bk_nm|
		|parameters|
			|parameterName|
			|parameterType|
		|query|
		|columns|
			|columnFieldName|
			|columnDisplayName|
			|columnDataType|
		*/
			
		List<DTO> dtos = readExcel();

		createReportShells(dtos);
		
		// p(dtos);
		// pl("DONE");
	}
	

	/******************************************
	 * createReportShells
	 ******************************************/
	private static void createReportShells(List<DTO> dtos) 
	{
		for(DTO dto : dtos)
		{
			String thisShell = shell;
			
			thisShell = thisShell.replaceAll("\\|bk_nm\\|", dto.getBkNm());
			thisShell = thisShell.replaceAll("\\|query\\|", dto.getQuery());
			
			String columns = createColumns(dto.getColumns());
			String parameters = createParameters(dto.getParameters());
			
			thisShell = thisShell.replaceAll("\\|columns\\|", columns);
			thisShell = thisShell.replaceAll("\\|parameters\\|", parameters);

			pl(thisShell);
			
			pl("\n\n\n");
		}
	}

	
	/******************************************
	 * createColumns
	 ******************************************/
	private static String createColumns(List<Column> columns) 
	{
		StringBuilder sb = new StringBuilder();
		
		for(Column col : columns)
		{
			String thisColumn = column;
			
			thisColumn = thisColumn.replaceAll("\\|columnFieldName\\|", col.getFieldName());
			thisColumn = thisColumn.replaceAll("\\|columnDisplayName\\|", col.getDisplayName());
			thisColumn = thisColumn.replaceAll("\\|columnDataType\\|", col.getDataType());
			
			sb.append(thisColumn);
		}
		
		return sb.toString();
	}


	/******************************************
	 * createParameters
	 ******************************************/
	private static String createParameters(List<Paramter> parameters) 
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<parameters>\n");
	
		for(Paramter p : parameters)
		{
			String thisParameter = parameter;
			
			thisParameter = thisParameter.replaceAll("\\|parameterName\\|", p.getName());
			thisParameter = thisParameter.replaceAll("\\|parameterType\\|", p.getType());
			
			sb.append(thisParameter);
		}
		
		sb.append("</parameters>\n");
		

		return sb.toString();
	}


	/******************************************
	 * p
	 ******************************************/
	private static void p(List<DTO> dtos) 
	{
		pl(""+dtos.size());
		
		for(DTO dto : dtos)
		{
			pl("columns:" );
			
			for(Column p : dto.getColumns() )
			{
				pl(
						""+p.getFieldName() 
				+ "|" + p.getDisplayName()
				+ "|" + p.getDataType()
				);
			}
		}
	}




	/******************************************
	 * readExcel

		mnu|tab|bk_nm||parameterName|columnFieldName|columnDisplayName|columnDataType
		cpr|Reference Payment Method|d_cpr_rfrnc_grp_pymnt||test1|PAYMENT_METHOD_CODE|Payment Method Code|string
		null|null|null||tes2|PAYMENT_METHOD_DESC|Payment Method Description|string
		null|null|null||null|ACCOUNT_TYPE|Account Type|string
		cpr|Reference Group Payment Method|d_cpr_rfrnc_pymnt||null|AR_GRP_CD|AR Group Code|null
		null|null|null||null|PMT_MTHD_CD|Payment Method Code|string
		null|null|null||null|GL_NO|GL No|int
		null|null|null||null|MUNT_NO|Cost Center No|int

	 ******************************************/
	private static List<DTO> readExcel() 
	{
		String mnuemonic 		 = null;
     	String tab 				 = null;
     	String bkNm 			 = null;
     	String query 			 = null;
     	String parameterName	 = null;
     	String columnFieldName	 = null;
     	String columnDisplayName = null;
     	String columnDataType 	 = null;
     	
     	Paramter parameter		= new Paramter();
     	Column column			= new Column();
     	
     	List<Paramter> parameters 	= new ArrayList<Paramter>();
     	List<Column> columns		= new ArrayList<Column>();
     	
     	DTO dto 					= new DTO();
     	List<DTO> dtos = new ArrayList<DTO>();
     	
     	boolean firstRow 	= true;
     	boolean main 		= true;
     	
     	
		try 
		{
            FileInputStream excelFile = new FileInputStream(new File(INPUT));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            outer:while (iterator.hasNext()) // row
            {
            	Row currentRow = iterator.next();
            	 
            	if(firstRow)
            	{
            		firstRow = false;
            		continue;
            	}

            	int col = 0;
            	
            	for (int c = 0; c < 8; c++) // column
                {
                	Cell currentCell = currentRow.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                	String value = null;
                	
                	if(currentCell != null)
                		value = currentCell.getStringCellValue();
                	
                	col++;
                	
					if(col == 1) 
					{ 
						mnuemonic = value; 
					    if(mnuemonic == null)
					    {
					    	//main = true;
					    }
					    else if(mnuemonic.equals("EOF"))
					    {
					    	dto.setColumns(columns);
					    	dto.setParameters(parameters);
					    	
					    	dtos.add(dto);
					    	
					    	break outer; 
					    }
					    else // New Record
					    {
					    	if(columns.size() > 0) 
					    	{
					    		dto.setColumns(columns);
						    	dto.setParameters(parameters);
						    	
						    	dtos.add(dto);
						    	
						    	parameters = new ArrayList<Paramter>();
						    	columns = new ArrayList<Column>();
						    	dto = new DTO();
						    	dto.setMnuemonic(value);
					    	}
					    	else // first dto
					    	{
					    		parameters = new ArrayList<Paramter>();
						    	columns = new ArrayList<Column>();
						    	dto = new DTO();
						    	dto.setMnuemonic(value);
					    	}

					    	//main = false;
					    }
                	}
                	else if(col == 2) 
                	{ 
                		if(value != null)
                			dto.setTab(value);
                	}
                	else if(col == 3) 
                	{ 
                		if(value != null)
                			dto.setBkNm(value);
                	}
                	else if(col == 4) 
                	{ 
                		if(value != null)
                			dto.setQuery(value);
                	}
					// 
                	else if(col == 5) 
                	{ 
                		parameterName 		= value; 
                		parameter.setName(parameterName);
                	}
                	else if(col == 6) 
                	{ 
                		columnFieldName 	= value; 
                		column.setFieldName(columnFieldName);
                	}
                	else if(col == 7) 
                	{ 
                		columnDisplayName 	= value; 
                		column.setDisplayName(columnDisplayName);
                	}
                	else if(col == 8)
                	{ 
                		columnDataType 		= value; 
                		
                		
                		parameter.setType(columnDataType);
                		column.setDataType(columnDataType);
                		
                		if(parameter.getName() != null)
                			parameters.add(parameter);
                		
                		columns.add(column);
                		
                		parameter = new Paramter();
                		column = new Column();
                	}
                }
                
            	/*
                pl(
                			 mnuemonic 			
                		+"|"+tab 				
                		+"|"+bkNm 				
                		+"|"+"" 				
                		+"|"+parameterName 		
                		+"|"+columnFieldName 	
                		+"|"+columnDisplayName 	
                		+"|"+columnDataType 		
                );
                */
            }
        } 
		catch (IOException e) 
		{
            e.printStackTrace();
        }
		
		return dtos;
	}


	/******************************************
	 * read
	 ******************************************/
	private static String read(String file) 
	{
		StringBuilder sb = new StringBuilder();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) 
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
				sb.append(System.lineSeparator());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}


	/******************************************
	 * pl
	 ******************************************/
	private static void pl(String s) 
	{
		System.out.println(s);
	}
}
