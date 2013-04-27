package il.ac.idc.milab.soundscape.library;
import java.util.ArrayList;
import java.util.Random;
import android.util.Log;

public class RandomLetterGenerator {

	private static final String TAG = "RandomLetterGenerator";
	private String m_word;
	private Random m_Random;
	private ArrayList<Character> m_CharacterArray;
	
	public RandomLetterGenerator(String i_word) 
	{
		this.m_word = i_word.toUpperCase();
		this.m_Random = new Random();
	}
	
	public Character[] GetRandomLetters(int numberOfLetters)
	{
		this.m_CharacterArray = new ArrayList<Character>();
		
		// make sure each letter in the word is in the result:
		for (Character character : m_word.toCharArray()) {
			int index = Math.max(m_CharacterArray.size(), 1);
			m_CharacterArray.add(this.m_Random.nextInt(index), character);
		}
		
		// add other random letters
		int charsLeftToAdd = numberOfLetters - m_CharacterArray.size();
		while(charsLeftToAdd > 0)
		{
			Character character = generateRandomChar();
			m_CharacterArray.add(this.m_Random.nextInt(m_CharacterArray.size()), character);
			charsLeftToAdd--;
		}
		
		
		m_CharacterArray.trimToSize();
		return m_CharacterArray.toArray(new Character[1]);
	}
	
	public int[] RemoveRandomChars()
	{
//		int numberOfCharactersToRemove = this.m_CharacterArray.size() / 2;
		int numberOfCharactersToRemove = 4;
		int[] result = new int[numberOfCharactersToRemove];
		
		while (numberOfCharactersToRemove > 0)
		{
			int index = this.m_Random.nextInt(this.m_CharacterArray.size());			
			if (Character.isLowerCase(this.m_CharacterArray.get(index)))
			{
				Log.d(TAG, "Removing Character " + this.m_CharacterArray.get(index));
				this.m_CharacterArray.set(index, ' ');
				result[result.length - numberOfCharactersToRemove] = index;
				numberOfCharactersToRemove--;
			}
		}
		
//		return this.m_CharacterArray.toArray(new Character[1]);
		return result;
	}
	
	private Character generateRandomChar() {
		int random = this.m_Random.nextInt((int) 'z' - 'a');
		random += 'a';
		Log.d(TAG, "Random number is " + random);
		Character result  = Character.valueOf((char) random);
		Log.d(TAG, "Random char is: " + result.toString());
		return result;
	}

	public boolean checkGuess(String i_guess) {
		Log.i(TAG, "Checking guess: " + i_guess + " against word " + this.m_word);
		return this.m_word.equals(i_guess);
	}
}
