import java.util.*;

public class TrieTree {
    private TrieNode root;
    public TrieTree() {
        root = new TrieNode();
    }

    /**
     * Insert a word into the Trie
     */
    public void insert(String word) {
        if (word == null || word.isEmpty()) return;

        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (!node.hasChild(c)) {
                node.setChild(c, new TrieNode());
            }
            node = node.getChild(c);
        }
        node.setEndOfWord(true);
        node.addWord(word);
    }

    /**
     * Search for exact word
     */
    public boolean search(String word) {
        TrieNode node = searchNode(word);
        return node != null && node.isEndOfWord();
    }

    /**
     * Find all words with given prefix
     */
    public List<String> searchByPrefix(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = searchNode(prefix);
        if (node == null) return results;
        // DFS to find all words
        dfs(node, results);
        return results;
    }

    /**
     * Find node for a prefix
     */
    private TrieNode searchNode(String prefix) {
        if (prefix == null || prefix.isEmpty()) return root;

        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (!node.hasChild(c)) {
                return null;
            }
            node = node.getChild(c);
        }
        return node;
    }

    /**
     * DFS to collect all words from a node
     */
    private void dfs(TrieNode node, List<String> results) {
        if (node.isEndOfWord()) {
            results.addAll(node.getWords());
        }

        for (int i = 0; i < 256; i++) {
            if (node.hasChild((char) i)) {
                dfs(node.getChild((char) i), results);
            }
        }
    }
}
