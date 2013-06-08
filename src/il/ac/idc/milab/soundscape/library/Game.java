package il.ac.idc.milab.soundscape.library;

import java.util.HashMap;

import org.json.JSONObject;

/**
 * This class represents a game between two players
 * @author Tal Kammer & Gadi Ickowicz
 *
 */
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

	/**
	 * A Getter for the user playing
	 * @return the user that started the game
	 */
	public static String getUser() {
		return m_User;
	}
	
	/**
	 * A Getter for the opponent
	 * @return the opponent of the user
	 */
	public static String getOpponent() {
		return m_Opponent;
	}
	
	/**
	 * A Getter for the person that it's his turn
	 * @return the person who's turn it is
	 */
	public static String getWhosTurn() {
		return m_WhosTurn;
	}
	
	/**
	 * A Getter for the number of turns that passed from the start of the game
	 * @return a string representing the number of the turn
	 */
	public static String getTurnCount() {
		return m_TurnCount;
	}

	/**
	 * A Getter for the state of the game (0 for guessing / 1 for recording)
	 * @return a string representing a number that states if we guess or record
	 */
	public static String getState() {
		return m_State;
	}
	
	/**
	 * A Getter for the encoded sound file that we got from the server
	 * @return a string representing the sound we are guessing
	 */
	public static String getSound() {
		return m_Sound;
	}
	
	/**
	 * A Getter for the game ID
	 * @return a string representing the game ID
	 */
	public static String getId() {
		return m_Id;
	}
	
	/**
	 * A Getter for the name of the sound we are currently guessing/recording
	 * @return a string representing the name of the sound we are recording
	 */
	public static String getWord() {
		return m_CurrentWord;
	}
	
	/**
	 * A Setter for the name of the sound we will guess/record
	 */
	public static void setWord(String i_Word) {
		m_CurrentWord = i_Word;
	}
	
	/**
	 * A Getter for the difficulty level of the sound
	 * @return a string representing the difficulty level of the sound
	 */
	public static String getDifficualty() {
		return m_CurrentWordDifficualty;
	}
	
	/**
	 * A Setter for the difficulty level of the sound
	 * @param i_CurrentWordDifficualty the difficulty as we got from the server
	 */
	public static void setDifficualty(String i_CurrentWordDifficualty) {
		m_CurrentWordDifficualty = i_CurrentWordDifficualty;
	}
	
	/**
	 * A method that inits all fields of the game according to the given game 
	 * details that we got from the server
	 * @param i_GameDetails a JSONObject representing the game details as we got
	 * from the server
	 */
	public static void init(JSONObject i_GameDetails) {
		HashMap<String, String> game = JSONHelper.getMapFromJson(i_GameDetails);
		m_Id = game.get(ServerRequests.RESPONSE_FIELD_GAME_ID);
		m_User = game.get(ServerRequests.RESPONSE_FIELD_GAME_USER);
		m_Opponent = game.get(ServerRequests.RESPONSE_FIELD_GAME_OPPONENT);
		m_WhosTurn = game.get(ServerRequests.RESPONSE_FIELD_GAME_WHOSTURN);
		m_TurnCount = game.get(ServerRequests.RESPONSE_FIELD_GAME_TURNCOUNT);
		m_State = game.get(ServerRequests.RESPONSE_FIELD_GAME_STATE);
		m_CurrentWord = game.get(ServerRequests.RESPONSE_FIELD_GAME_WORD);
		m_CurrentWordDifficualty = game.get(
				ServerRequests.RESPONSE_FIELD_GAME_DIFFICUALTY);
	}
}
