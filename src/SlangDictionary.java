import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SlangDictionary {
    private HashMap<String, List<String>> dictionary;
    private HashMap<String, List<String>> originalDictionary;
    private ArrayList<String> searchHistory;
    private String data_file = "dictionary.dat";
    private String history_file = "history.dat";

    public SlangDictionary() {
        this.dictionary = new HashMap<>();
        this.originalDictionary = new HashMap<>();
        this.searchHistory = new ArrayList<>();
    }

    // Load slang words from file
    public void loadFromFile(String filePath) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            while((line = reader.readLine()) != null) {
                if(line.trim().isEmpty()) continue;

                if(isFirstLine && line.contains("Slag")) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;

                String[] parts = line.split("`", 2);
                if(parts.length == 2) {
                    String word = parts[0].trim();
                    String definitions = parts[1].trim();
                    List<String> defList = Arrays.stream(definitions.split("\\|")).map(String::trim).collect(Collectors.toList());
                    dictionary.put(word, defList);
                }
            }
            originalDictionary.putAll(dictionary);
            System.out.println("Loaded " + dictionary.size() + " slang words successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Search by slang word
    public List<String> searchBySlangWord(String word) {
        addToHistory("Slang: " + word);
        return dictionary.getOrDefault(word, null);
    }

    // Add search history
    public void addToHistory(String word) {
        searchHistory.add(word);
    }

    // Get search history
    public List<String> getSearchHistory() {
        return new ArrayList<>(searchHistory);
    }

    // Clear search history
    public void clearHistory() {
        searchHistory.clear();
    }

    // Search by definition
    public List<String> searchByDefinition(String keyword) {
        addToHistory("Definition: " + keyword);
        return dictionary.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(def -> def.toLowerCase().contains(keyword.toLowerCase())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
