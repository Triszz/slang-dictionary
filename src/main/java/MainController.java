import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import java.util.*;

public class MainController {

    @FXML private TextField searchSlangField;
    @FXML private TextField searchDefField;
    @FXML private TextArea resultsArea;

    @FXML private TextField addSlangField;
    @FXML private TextField addDefField;

    @FXML private TextField editSlangField;
    @FXML private TextArea editResultArea;

    @FXML private TextArea randomArea;
    @FXML private ListView<String> historyListView;
    @FXML private Label statusLabel;

    private SlangDictionary dictionary;

    @FXML
    public void initialize() {
        dictionary = new SlangDictionary();
        loadDictionary();
        updateStatus("Dictionary loaded successfully!");
    }

    public void cleanup() {
        if (dictionary != null) {
            dictionary.saveDictionary();
            dictionary.saveHistory();
            System.out.println("Dictionary and history saved!");
        }
    }
    /**
     * Load dictionary
     */
    private void loadDictionary() {
        boolean hasDictCache = dictionary.loadDictionary();
        boolean hasOriginalCache = dictionary.loadOriginalDictionary();

        if(!hasDictCache) {
            dictionary.loadDictionaryFromFile("slang.txt");
            dictionary.saveDictionary();
        }

        if(!hasOriginalCache) {
            dictionary.loadOriginalFromFile("slang.txt");
            dictionary.saveOriginalDictionary();
        }

        dictionary.loadHistory();
    }

    /**
     * Search by slang word
     */
    @FXML
    private void handleSearchBySlang() {
        String word = searchSlangField.getText().trim();
        if(word.isEmpty()) {
            showAlert("Input Error", "Please enter a slang word!");
            return;
        }

        List<String> definitions = dictionary.searchBySlangWord(word);
        if(!definitions.isEmpty()) {
            resultsArea.setText("Found: " + word + "\n\n" +
                    "Definitions:\n" +
                    String.join("\n", definitions));
            updateStatus("Found " + definitions.size() + " definition(s)");
        } else {
            resultsArea.setText("Slang word '" + word + "' not found!");
            updateStatus("Not found");
        }
    }

    /**
     * Search by definition
     */
    @FXML
    private void handleSearchByDefinition() {
        String keyword = searchDefField.getText().trim();
        if(keyword.isEmpty()) {
            showAlert("Input Error", "Please enter a keyword!");
            return;
        }

        Map<String, List<String>> results = dictionary.searchByDefinition(keyword);
        if(!results.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Found ").append(results.size()).append(" slang word(s):\n\n");

            int count = 1;
            for(Map.Entry<String, List<String>> entry : results.entrySet()) {
                sb.append(count++).append(". ").append(entry.getKey()).append("\n");
                sb.append("Definition(s):\n");
                for(String def : entry.getValue()) {
                    sb.append("   ").append(def).append("\n");
                }
                sb.append("\n");
            }

            resultsArea.setText(sb.toString());
            updateStatus("Found " + results.size() + " result(s)");
        } else {
            resultsArea.setText("No slang words found with definition containing '" + keyword + "'!");
            updateStatus("Not found");
        }
    }

    /**
     * Show alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Update status
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
}
