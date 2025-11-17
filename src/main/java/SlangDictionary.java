import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SlangDictionary {
    private HashMap<String, List<String>> dictionary;
    private final HashMap<String, List<String>> originalDictionary;
    private ArrayList<String> searchHistory;
    private static final String DATA_FILE = "dictionary.dat";
    private static final String ORIGINAL_DICTIONARY = "original_dictionary.dat";
    private static final String HISTORY_FILE = "history.dat";

    public SlangDictionary() {
        this.dictionary = new HashMap<>();
        this.originalDictionary = new HashMap<>();
        this.searchHistory = new ArrayList<>();

    }

    /**
     * Load slang words from file
     */
    public void loadDictionaryFromFile(String filePath) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int duplicateCount = 0;

            dictionary.clear();

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
                    List<String> defList = Arrays.stream(definitions.split("\\|"))
                            .map(String::trim)
                            .collect(Collectors.toList());

                    // Xử lý duplicate key
                    if(dictionary.containsKey(word)) {
                        duplicateCount++;
                        List<String> existingDefs = dictionary.get(word);

                        // Merge definitions, tránh duplicate
                        for(String def : defList) {
                            if(!existingDefs.contains(def)) {
                                existingDefs.add(def);
                            }
                        }

                        System.out.println("Duplicate #" + duplicateCount + ": '" + word +
                                "' - Merged " + defList.size() + " definition(s)");
                    } else {
                        dictionary.put(word, defList);
                    }
                }
            }

            System.out.println("✓ Loaded " + dictionary.size() + " slang words from text file!");
            if(duplicateCount > 0) {
                System.out.println("Found and merged " + duplicateCount + " duplicate slang words");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Load original dictionary
     */
    public void loadOriginalFromFile(String filePath) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int duplicateCount = 0;

            originalDictionary.clear();

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
                    List<String> defList = Arrays.stream(definitions.split("\\|"))
                            .map(String::trim)
                            .collect(Collectors.toList());

                    if(originalDictionary.containsKey(word)) {
                        duplicateCount++;
                        List<String> existingDefs = originalDictionary.get(word);

                        for(String def : defList) {
                            if(!existingDefs.contains(def)) {
                                existingDefs.add(def);
                            }
                        }
                    } else {
                        List<String> valueCopy = new ArrayList<>(defList);
                        originalDictionary.put(word, valueCopy);
                    }
                }
            }
            System.out.println("✓ Original dictionary loaded from text file!");
            if(duplicateCount > 0) {
                System.out.println("Found and merged " + duplicateCount + " duplicate slang words in original");
            }
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
        return dictionary.getOrDefault(word, new ArrayList<>());
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
    public Map<String, List<String>> searchByDefinition(String keyword) {
        addToHistory("Definition: " + keyword);
        Map<String, List<String>> slangs = new LinkedHashMap<>();
        for(Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
            for(String def : entry.getValue()) {
                if(def.toLowerCase().contains(keyword.toLowerCase())) {
                    slangs.put(entry.getKey(), entry.getValue());
                    break;
                }
            }
        }
        return slangs;
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

    /**
     * Add definition to word
     */
    public boolean addDefinitionToWord(String word, String definition) {
        if(!dictionary.containsKey(word)) {
            return false;
        }
        dictionary.get(word).add(definition);
        return true;
    }

    /**
     * Edit definition
     */
    public boolean editDefinition(String word, int definitionIndex, String newDefinition) {
        List<String> definitions = dictionary.get(word);
        if(definitions == null) {
            return false;
        }
        if(definitionIndex < 0 || definitionIndex >= definitions.size()) {
            return false;
        }
        definitions.set(definitionIndex, newDefinition);
        return true;
    }
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
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(dictionary);
            System.out.println("Dictionary save to file!");
        } catch (IOException e) {
            System.out.println("Error saving dictionary: " + e.getMessage());
        }
    }

    /**
     * Save original dictionary to file
     */
    public void saveOriginalDictionary() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORIGINAL_DICTIONARY))) {
            oos.writeObject(originalDictionary);
            System.out.println("Original dictionary saved to file!");
        } catch (IOException e) {
            System.out.println("Error saving original dictionary: " + e.getMessage());
        }
    }

    /**
     * Load dictionary from file
     */
    public boolean loadDictionary() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
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
     * Load original dictionary from file
     */
    public boolean loadOriginalDictionary() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ORIGINAL_DICTIONARY))) {
            HashMap<String, List<String>> loaded = (HashMap<String, List<String>>) ois.readObject();
            originalDictionary.clear();
            originalDictionary.putAll(loaded);
            System.out.println("Original dictionary loaded from cache!");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("No cached original dictionary found.");
            return false;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading original dictionary: " + e.getMessage());
            return false;
        }
    }

    /**
     * Save history to file
     */
    public void saveHistory() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {
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
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HISTORY_FILE))) {
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

