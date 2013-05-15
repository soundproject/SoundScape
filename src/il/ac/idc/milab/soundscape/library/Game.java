package il.ac.idc.milab.soundscape.library;

import java.util.HashMap;

import org.json.JSONObject;

public class Game {
	
	HashMap<String, String> m_GameDetails;
	private static String m_User;
	private static String m_Opponent;
	private static String m_WhosTurn;
	private static String m_TurnCount;
	private static String m_State;
	private static String m_Sound;
	private static String m_Id;
	private static String m_CurrentWord;
	private static String m_CurrentWordDifficualty;

	public Game() {}

	public static String getUser() {
		return m_User;
	}
	
	public static String getOpponent() {
		return m_Opponent;
	}
	
	public static String getWhosTurn() {
		return m_WhosTurn;
	}
	
	public static String getTurnCount() {
		return m_TurnCount;
	}

	public static String getState() {
		return m_State;
	}
	
	public static String getSound() {
		return m_Sound;
	}
	
	public static String getId() {
		return m_Id;
	}
	
	public static String getWord() {
		return m_CurrentWord;
	}
	
	public static void setWord(String i_Word) {
		m_CurrentWord = i_Word;
	}
	
	public static String getDifficualty() {
		return m_CurrentWordDifficualty;
	}
	
	public static void setDifficualty(String i_CurrentWordDifficualty) {
		m_CurrentWordDifficualty = i_CurrentWordDifficualty;
	}
	
	public static void init(JSONObject i_GameDetails) {
		HashMap<String, String> game = JSONHelper.getMapFromJson(i_GameDetails);
		m_Id = game.get(ServerRequests.RESPONSE_FIELD_GAME_ID);
		m_User = game.get(ServerRequests.RESPONSE_FIELD_GAME_USER);
		m_Opponent = game.get(ServerRequests.RESPONSE_FIELD_GAME_OPPONENT);
		m_WhosTurn = game.get(ServerRequests.RESPONSE_FIELD_GAME_WHOSTURN);
		m_TurnCount = game.get(ServerRequests.RESPONSE_FIELD_GAME_TURNCOUNT);
		m_State = game.get(ServerRequests.RESPONSE_FIELD_GAME_STATE);
		m_CurrentWord = game.get(ServerRequests.RESPONSE_FIELD_GAME_WORD);
		m_CurrentWordDifficualty = game.get(ServerRequests.RESPONSE_FIELD_GAME_DIFFICUALTY);
	}
}
