package il.ac.idc.milab.soundscape;

public class Option {
	
	private String m_Name;
	private String m_Category;
	private int m_Value;
	
	
	public String getName() 
	{
		return this.m_Name;
	}
	
	public void setName(String m_Name) 
	{
		this.m_Name = m_Name;
	}
	
	public String getCategory() 
	{
		return this.m_Category;
	}
	
	public void setCategory(String m_Category) 
	{
		this.m_Category = m_Category;
	}
	
	public int getValue() 
	{
		return this.m_Value;
	}
	
	public void setValue(int m_Value) 
	{
		this.m_Value = m_Value;
	}
}
