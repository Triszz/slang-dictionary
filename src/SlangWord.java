import java.io.*;
import java.util.*;

public class SlangWord implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String word;
    private List<String> definitions;

    // Constructor
    public SlangWord(String word, List<String> definitions) {
        this.word = word;
        this.definitions = definitions;
    }

    // Getters
    public String getWord() {
        return word;
    }
    public List<String> getDefinitions() {
        return definitions;
    }
    // Setters
    public void setWord(String word) {
        this.word = word;
    }
    public void setDefinitions(List<String> definitions) {
        this.definitions = definitions;
    }
    // Add 1 definition
    public void addDefinition(String definition) {
        definitions.add(definition);
    }
    public String getDefinitionAsString() {
        return String.join(" | ", definitions);
    }

    public String toString() {
        return word + " -> " + getDefinitionAsString();
    }
}
