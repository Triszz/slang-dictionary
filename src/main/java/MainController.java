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
            StringBuilder sb = new StringBuilder();
            sb.append("Found: ").append(word).append("\n\n");
            sb.append("Definition(s):\n");
            for(int i = 0; i < definitions.size(); i++) {
                sb.append("   ").append(definitions.get(i)).append("\n");
            }
            resultsArea.setText(sb.toString());
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
     * Add slang word
     */
    @FXML
    private void handleAddSlang() {
        String slang = addSlangField.getText().trim();
        String def = addDefField.getText().trim();

        if(slang.isEmpty() || def.isEmpty()) {
            showAlert("Input Error", "Please enter both slang word and definition!");
            return;
        }
        if(dictionary.getDictionary().containsKey(slang)) {
            handleDuplicateSlang(slang, def);
        } else {
            if(dictionary.addSlangWord(slang, def)) {
                showAlert("Success", "Slang word added successfully!\n\n" + "Slang word: " +
                        slang + "\nDefinition: " + def);
                addSlangField.clear();
                addDefField.clear();
                updateStatus("Added: " + slang);
            } else {
                showAlert("Error", "Failed to add slang word!");
            }
        }
    }

    /**
     * Handle duplicate slang word
     */
    private void handleDuplicateSlang(String slang, String newDef) {
        List<String> existingDefs = dictionary.getDictionary().get(slang);

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Duplicate Slang Word");
        alert.setHeaderText("Slang word '" + slang + "' already exists!");
        alert.setContentText("Current definition(s):\n" +
                String.join("\n", existingDefs) +
                "\n\nNew definition:\n" + newDef +
                "\n\nWhat would you like to do?");

        ButtonType btnOverride = new ButtonType("Override");
        ButtonType btnAddDef = new ButtonType("Add Definition");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnOverride, btnAddDef, btnCancel);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnOverride) {
                dictionary.deleteSlangWord(slang);
                dictionary.addSlangWord(slang, newDef);

                showAlert("Success", "Slang word overridden!\n\n" + "Slang word: " +
                        slang + "\nDefinition: " + newDef);

                addSlangField.clear();
                addDefField.clear();
                updateStatus("Overridden: " + slang);

            } else if (response == btnAddDef) {
                if(dictionary.addDefinitionToWord(slang, newDef)) {
                    StringBuilder message = new StringBuilder();
                    message.append("Definition added!\n\n");
                    message.append("'").append(slang).append("' now has ")
                            .append(dictionary.getDictionary().get(slang).size())
                            .append(" definitions:\n");

                    List<String> allDefs = dictionary.getDictionary().get(slang);
                    for(int i = 0; i < allDefs.size(); i++) {
                        message.append("   ").append(allDefs.get(i)).append("\n");
                    }
                    showAlert("Success", message.toString());
                    addSlangField.clear();
                    addDefField.clear();
                    updateStatus("Added definition to: " + slang);
                } else {
                    showAlert("Error", "Failed to add definition!");
                }

            } else {
                updateStatus("Add cancelled");
            }
        });
    }

    /**
     * Show random slang word
     */
    @FXML
    private void handleRandom() {
        String slang = dictionary.getRandomSlangWord();
        if(slang != null) {
            List<String> defs = dictionary.getDictionary().get(slang);

            StringBuilder sb = new StringBuilder();
            sb.append("Today's Slang Word: ").append(slang).append("\n\n");
            sb.append("Definition(s):\n");
            for(String def : defs) {
                sb.append("   ").append(def).append("\n");
            }
            randomArea.setText(sb.toString());
            updateStatus("Random word shown");
        }
    }

    /**
     * Refresh history
     */
    @FXML
    private void handleRefreshHistory() {
        List<String> history = dictionary.getSearchHistory();
        historyListView.getItems().setAll(history);
        updateStatus("History refreshed");
    }

    /**
     * Clear history
     */
    @FXML
    private void handleClearHistory() {
        dictionary.clearHistory();
        historyListView.getItems().clear();
        updateStatus("History cleared");
    }

    /**
     * Load slang word needs to edit/delete
     */
    @FXML
    private void handleLoadForEdit() {
        String slang = editSlangField.getText().trim();
        if(slang.isEmpty()) {
            showAlert("Input Error", "Please enter a slang word!");
            return;
        }

        List<String> defs = dictionary.getDictionary().get(slang);
        if(defs != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Slang Word: ").append(slang).append("\n\nDefinitions:\n");
            for(int i = 0; i < defs.size(); i++) {
                sb.append("   ").append(defs.get(i)).append("\n");
            }
            editResultArea.setText(sb.toString());
            updateStatus("Loaded: " + slang);
        } else {
            editResultArea.setText("Slang word '" + slang + "' not found!");
            updateStatus("Not found");
        }
    }

    /**
     * Delete slang word
     */
    @FXML
    private void handleDelete() {
        String slang = editSlangField.getText().trim();
        if(slang.isEmpty()) {
            showAlert("Input Error", "Please enter a slang word!");
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete slang word: " + slang);
        confirm.setContentText("Are you sure?");

        if(confirm.showAndWait().get() == ButtonType.OK) {
            if(dictionary.deleteSlangWord(slang)) {
                showAlert("Success", "Deleted successfully!");
                editResultArea.clear();
                editSlangField.clear();
                updateStatus("Deleted: " + slang);
            } else {
                showAlert("Error", "Failed to delete!");
            }
        }
    }

    /**
     * Edit slang word
     */
    @FXML
    private void handleEdit() {
        String slang = editSlangField.getText().trim();
        if(slang.isEmpty()) {
            showAlert("Input Error", "Please enter a slang word!");
            return;
        }

        List<String> defs = dictionary.getDictionary().get(slang);
        if(defs == null || defs.isEmpty()) {
            showAlert("Error", "Slang word not found!");
            return;
        }

        if(defs.size() == 1) {
            editDefinition(slang, 0, defs.get(0));
            return;
        }

        List<String> choices = new ArrayList<>();
        for(int i = 0; i < defs.size(); i++) {
            choices.add(defs.get(i));
        }

        ChoiceDialog<String> choiceDialog = new ChoiceDialog<>(choices.get(0), choices);
        choiceDialog.setTitle("Edit Definition");
        choiceDialog.setHeaderText("Slang word: " + slang);
        choiceDialog.setContentText("Choose definition to edit:");

        choiceDialog.showAndWait().ifPresent(selected -> {
            int index = choices.indexOf(selected);
            if(index >= 0) {
                String oldDef = defs.get(index);
                editDefinition(slang, index, oldDef);
            }
        });
    }

    /**
     * Helper method for editing 1 definition
     */
    private void editDefinition(String slang, int index, String oldDef) {
        TextInputDialog inputDialog = new TextInputDialog(oldDef);
        inputDialog.setTitle("Edit Definition");
        inputDialog.setHeaderText("Editing definition '" + oldDef + "' of: " + slang);
        inputDialog.setContentText("New definition:");

        inputDialog.showAndWait().ifPresent(newDef -> {
            if(newDef.trim().isEmpty()) {
                showAlert("Error", "Definition cannot be empty!");
                return;
            }

            if(dictionary.editDefinition(slang, index, newDef.trim())) {
                showAlert("Success", "Definition updated successfully!");

                List<String> updatedDefs = dictionary.getDictionary().get(slang);
                if(updatedDefs != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Slang: ").append(slang).append("\n\nDefinitions:\n");
                    for(int i = 0; i < updatedDefs.size(); i++) {
                        sb.append("   ").append(updatedDefs.get(i)).append("\n");
                    }
                    editResultArea.setText(sb.toString());
                }

                updateStatus("Updated definition for: " + slang);
            } else {
                showAlert("Error", "Failed to update definition!");
            }
        });
    }

    /**
     * Quiz: Guess definition
     */
    @FXML
    private void handleQuizDefinition() {
        String randomSlang = dictionary.getRandomSlangWord();
        if(randomSlang == null) {
            showAlert("Error", "Dictionary is empty!");
            return;
        }

        List<String> defs = dictionary.getDictionary().get(randomSlang);
        String correctDef = defs.get(0);

        List<String> options = new ArrayList<>();
        options.add(correctDef);

        Random random = new Random();
        List<String> allWords = new ArrayList<>(dictionary.getDictionary().keySet());
        while(options.size() < 4) {
            String randomWord = allWords.get(random.nextInt(allWords.size()));
            String randomDef = dictionary.getDictionary().get(randomWord).get(0);
            if(!options.contains(randomDef)) {
                options.add(randomDef);
            }
        }

        Collections.shuffle(options);

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Quiz: Guess Definition");
        dialog.setHeaderText("What does '" + randomSlang + "' mean?");
        dialog.setContentText("Choose:");

        dialog.showAndWait().ifPresent(answer -> {
            if(answer.equals(correctDef)) {
                showAlert("Correct!", "Well done!\nThe definition of '" + randomSlang + "': " + correctDef);
            } else {
                showAlert("Wrong!", "Try again!\nThe definition of '" + randomSlang + "': " + correctDef);
            }
        });
    }

    /**
     * Quiz: Guess slang word
     */
    @FXML
    private void handleQuizSlang() {
        String randomSlang = dictionary.getRandomSlangWord();
        if(randomSlang == null) {
            showAlert("Error", "Dictionary is empty!");
            return;
        }

        List<String> defs = dictionary.getDictionary().get(randomSlang);
        String definition = defs.get(0);

        List<String> options = new ArrayList<>();
        options.add(randomSlang);

        Random random = new Random();
        List<String> allWords = new ArrayList<>(dictionary.getDictionary().keySet());
        while(options.size() < 4) {
            String randomWord = allWords.get(random.nextInt(allWords.size()));
            if(!options.contains(randomWord)) {
                options.add(randomWord);
            }
        }

        Collections.shuffle(options);

        ChoiceDialog<String> dialog = new ChoiceDialog<>(options.get(0), options);
        dialog.setTitle("Quiz: Guess Slang Word");
        dialog.setHeaderText("Which slang word means:\n'" + definition + "'?");
        dialog.setContentText("Choose:");

        dialog.showAndWait().ifPresent(answer -> {
            if(answer.equals(randomSlang)) {
                showAlert("Correct!", "Well done!\nThe slang word of '" + definition + "': " + randomSlang);
            } else {
                showAlert("Wrong!", "Try again!\nThe slang word of '" + definition + "': " + randomSlang);
            }
        });
    }

    /**
     * Reset dictionary
     */
    @FXML
    private void handleResetDictionary() {
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Reset");
        confirm.setHeaderText("Reset Dictionary to Original");
        confirm.setContentText("This will delete all your changes and restore the original dictionary.\nAre you sure?");

        if(confirm.showAndWait().get() == ButtonType.OK) {
            dictionary.resetDictionary();
            showAlert("Success", "Dictionary has been reset to original!");

            searchSlangField.clear();
            searchDefField.clear();
            resultsArea.clear();
            addSlangField.clear();
            addDefField.clear();
            editSlangField.clear();
            editResultArea.clear();
            randomArea.clear();

            updateStatus("Dictionary reset to original - " + dictionary.getTotalWords() + " words");
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
