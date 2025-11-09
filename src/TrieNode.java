import java.util.*;

public class TrieNode {
    private static final int ALPHABET_SIZE = 256;
    private TrieNode[] children;
    private boolean isEndOfWord;
    private List<String> words;

    public TrieNode() {
        children = new TrieNode[ALPHABET_SIZE];
        isEndOfWord = false;
        words = new ArrayList<>();
    }

    public TrieNode getChild(char c) {
        return children[c];
    }

    public void setChild(char c, TrieNode node) {
        children[c] = node;
    }

    public boolean hasChild(char c) {
        return children[c] != null;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public void addWord(String word) {
        words.add(word);
    }

    public List<String> getWords() {
        return words;
    }
}
