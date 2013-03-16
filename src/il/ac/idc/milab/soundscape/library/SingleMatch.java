package il.ac.idc.milab.soundscape.library;

/**
 * Represents a game against a single user - Keeps track of who's turn it is and
 * score so far - Announces winner when condition is reached
 * 
 * @author Gadi, Tal
 */
public class SingleMatch {

	public enum eTurnState {
		WAITING, YOUR_GUESS, YOUR_RECORDING
	};

	private eTurnState m_TurnState;
	private User m_Opponent;
	private int m_Score;

	public SingleMatch(User i_Opponent, eTurnState i_TurnState) {
		this.m_TurnState = i_TurnState;
		this.m_Opponent = i_Opponent;
		this.m_Score = 0;
	}

	public void Play() {
		// TODO: Synch to server here? or use setTurnState?

		switch (this.m_TurnState) {
		case WAITING:
			handleWaitingState();
		case YOUR_GUESS:
			handleGuess();
		case YOUR_RECORDING:
			handleRecording();
		}
	}

	/**
	 * Manages and starts recording phase/activity of the game
	 */
	private void handleRecording() {
		// TODO Start sound recording activity and wait for it to return

		// TODO: synch to server
	}

	/**
	 * Manages and starts the guessing phase/activity of the game
	 */
	private void handleGuess() {
		// TODO Auto-generated method stub
	}

	private void handleWaitingState() {
		// TODO What to do here?????
	}

	public eTurnState turnState() {
		return this.m_TurnState;
	}

	public String getOpponent() {
		return this.m_Opponent.getName();
	}

	public int getScore() {
		return this.m_Score;
	}

	protected void setTurnState(eTurnState i_TurnState) {
		this.m_TurnState = i_TurnState;
	}
}