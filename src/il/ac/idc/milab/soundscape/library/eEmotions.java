package il.ac.idc.milab.soundscape.library;

public enum eEmotions {
	VERY_UNPLEASANT(0, "Very Unpleasant"),
	UNPLEASANT(1, "Unpleasant"),
	NEUTRAL(2, "Neutral"),
	PLEASANT(3, "Pleasant"),
	VERY_PLEASANT(4, "Very Pleasant");

	private int m_val;
	private String m_label;
	
	private eEmotions(int val, String label) {
		// TODO Auto-generated constructor stub
		m_val = val;
		m_label = label;
	}
	
	public int getValue()
	{
		return this.m_val;
	}
	
	public String getLabel()
	{
		return this.m_label;
	}
	
	public static eEmotions parseEmotionFromInt(int value)
	{
		eEmotions result = null;
		for (eEmotions emotion : eEmotions.values())
		{
			if (emotion.m_val == value)
			{
				result = emotion;
				break;
			}
		}
		return result;
	}
}
