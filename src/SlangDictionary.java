import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SlangDictionary {
    private HashMap<String, List<String>> dictionary;
    private final HashMap<String, List<String>> originalDictionary;
    private ArrayList<String> searchHistory;
    private static final String data_file = "dictionary.dat";
    private static final String history_file = "history.dat";

    public SlangDictionary() {
        this.dictionary = new HashMap<>();
        this.originalDictionary = new HashMap<>();
        this.searchHistory = new ArrayList<>();
    }

    /**
     * Load slang words from file
     */
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

    /**
     * Search by slang word
     */
    public List<String> searchBySlangWord(String word) {
        addToHistory("Slang word: " + word);
        return dictionary.getOrDefault(word, null);
    }

    /**
     * Add search history
     */
    public void addToHistory(String word) {
        searchHistory.add(word);
    }

    /**
     * Get search history
     */
    public List<String> getSearchHistory() {
        return new ArrayList<>(searchHistory);
    }

    /**
     * Clear search history
     */
    public void clearHistory() {
        searchHistory.clear();
    }

    /**
     * Search by definition
     */
    public List<String> searchByDefinition(String keyword) {
        addToHistory("Definition: " + keyword);
        return dictionary.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(def -> def.toLowerCase().contains(keyword.toLowerCase())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Add new slang word
     */
    public boolean addSlangWord(String word, String definition) {
        if(dictionary.containsKey(word))
            return false;
        List<String> defList = new ArrayList<>();
        defList.add(definition);
        dictionary.put(word, defList);
        return true;
    }

    /* TODO: Edit slang word */

    /**
     * Delete slang word
     */
    public boolean deleteSlangWord(String word) {
        return dictionary.remove(word) != null;
    }

    /**
     * Get random slang word
     */
    public String getRandomSlangWord() {
        if(dictionary.isEmpty())
            return null;

        List<String> words = new ArrayList<>(dictionary.keySet());
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    /**
     * Reset to original dictionary
     */
    public void resetDictionary() {
        dictionary.clear();
        dictionary.putAll(originalDictionary);
        System.out.println("Dictionary reset to original!");
    }

    /**
     * Save dictionary to file
     */
    public void saveDictionary() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(data_file))) {
            oos.writeObject(dictionary);
            System.out.println("Dictionary save to file!");
        } catch (IOException e) {
            System.out.println("Error saving dictionary: " + e.getMessage());
        }
    }

    /**
     * Load dictionary from file
     */
    public boolean  loadDictionary() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(data_file))) {
            dictionary = (HashMap<String, List<String>>) ois.readObject();
            System.out.println("Dictionary loaded from cached file!");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("No cached dictionary found. Loading from original file...");
            return false;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading dictionary: " + e.getMessage());
            return false;
        }
    }

    /**
     * Save history to file
     */
    public void saveHistory() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(history_file))) {
            oos.writeObject(searchHistory);
            System.out.println("History save to file!");
        } catch (IOException e) {
            System.out.println("Error saving history: " + e.getMessage());
        }
    }

    /**
     * Load history from file
     */
    public boolean loadHistory() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(history_file))) {
            searchHistory = (ArrayList<String>) ois.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading dictionary: " + e.getMessage());
            return false;
        }
    }

    // Getters
    public int getTotalWords() {
        return dictionary.size();
    }

    public HashMap<String, List<String>> getDictionary() {
        return dictionary;
    }
}

