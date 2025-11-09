import java.util.*;

public class SlangDictionaryApp {
    private SlangDictionary dictionary;
    private Scanner scanner;

    public SlangDictionaryApp() {
        dictionary = new SlangDictionary();
        scanner = new Scanner(System.in);
    }

    /**
     * Initialize app
     */
    public void initialize() {
        System.out.println("=================================== SLANG DICTIONARY APP ===================================");
        boolean hasDictionaryCache = dictionary.loadDictionary();
        boolean hasOriginalCache = dictionary.loadOriginalDictionary();
        if(!hasDictionaryCache) {
            System.out.println("Loading dictionary from text file...");
            dictionary.loadDictionaryFromFile("slang.txt");
            dictionary.saveDictionary();
        }

        if(!hasOriginalCache) {
            System.out.println("Loading original dictionary from text file...");
            dictionary.loadOriginalFromFile("slang.txt");
            dictionary.saveOriginalDictionary();
        }

        if(hasDictionaryCache && hasOriginalCache) {
            System.out.println("Dictionary loaded from cache successfully!");
        }
        if(!dictionary.loadHistory()) {
            System.out.println("No search history yet!");
        }

    }

    /**
     * Show menu
     */
    public void showMenu() {
        System.out.println("=================================== MENU ===================================");
        System.out.println("1. Search by a slang word");
        System.out.println("2. Search by a definition");
        System.out.println("3. View a search history");
        System.out.println("4. Add a new slang word");
        System.out.println("5. Edit a slang word");
        System.out.println("6. Delete a slang word");
        System.out.println("7. Reset a dictionary");
        System.out.println("8. Random a slang word");
        System.out.println("9. Quiz: Guess a definition");
        System.out.println("10. Quiz: Guess a slang word");
        System.out.println("0. Exit");
        System.out.print("Chose an option: ");
    }

    /**
     * Run app
     */
    public void run() {
        while (true) {
            showMenu();
            try {
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        searchBySlangWord();
                        break;
                    case "2":
                        searchByDefinition();
                        break;
                    case "3":
                        viewSearchHistory();
                        break;
                    case "4":
                        addSlangWord();
                        break;
                    case "5":
                        editSlangWord();
                        break;
                    case "6":
                        deleteSlangWord();
                        break;
                    case "7":
                        resetDictionary();
                        break;
                    case "8":
                        showRandomSlangWord();
                        break;
                    case "9":
                        quizGuessDefinition();
                        break;
                    case "10":
                        quizGuessSlangWord();
                        break;
                    case "0":
                        System.out.println("\nSaving data...");
                        dictionary.saveDictionary();
                        dictionary.saveHistory();
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option! Please try again.");
                }
            } catch (Exception e) {
                System.err.println("✗ An error occurred: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    /**
     * main function
     */
    public static void main(String[] args) {
        SlangDictionaryApp app = new SlangDictionaryApp();
        app.initialize();
        app.run();
    }

    /**
     * Search by slang word
     */
    private void searchBySlangWord() {
        System.out.print("\nEnter a slang word: ");
        String word = scanner.nextLine().trim();

        List<String> definitions = dictionary.searchBySlangWord(word);
        if(!definitions.isEmpty()) {
            System.out.println("Found: " + word);
            System.out.println("Definition(s) of '" + word + "': " + String.join(" | ", definitions));
        } else {
            System.out.println("Slang word " + word + " not found!");
        }
    }

    /**
     * Search by definition
     */
    private void searchByDefinition() {
        System.out.print("\nEnter a definition: ");
        String keyword = scanner.nextLine().trim();
        Map<String, List<String>> slangs = dictionary.searchByDefinition(keyword);
        if (!slangs.isEmpty()) {
            System.out.println("Found " + slangs.size() + " slang word(s) with definition containing '" + keyword + "':");
            int count = 1;
            for(Map.Entry<String, List<String>> entry : slangs.entrySet()) {
                System.out.println("\n" + count + ". " + entry.getKey());
                System.out.println("   Definition(s): " + String.join(" | ", entry.getValue()));
                count++;
            }
        } else {
            System.out.println("\nNo slang words found with definition containing '" + keyword + "'!");
        }
    }

    /**
     * View search history
     */
    private void viewSearchHistory() {
        System.out.println("\n=================================== Search History ===================================");
        List<String> searchHistory = dictionary.getSearchHistory();
        if(searchHistory.isEmpty()) {
            System.out.println("No search history!");
        }
        for(int i = 0; i < searchHistory.size() ; i++) {
            System.out.println((i + 1) + ". " + searchHistory.get(i));
        }
    }

    /**
     * Add new slang word
     */
    private void addSlangWord() {
        System.out.print("\nEnter slang word: ");
        String slangWord = scanner.nextLine().trim();
        System.out.println("Enter definition: ");
        String definition = scanner.nextLine().trim();

        if(dictionary.getDictionary().containsKey(slangWord)) {
            System.out.println("Slang word '" + slangWord + "' already exists!");
            System.out.print("Do you want to (1) Overwrite or (2) Duplicate? (1/2): ");
            String choice = scanner.nextLine().trim();
            if(choice.equals("1")) {
                List<String> newDefList = new ArrayList<>();
                newDefList.add(definition);
                dictionary.getDictionary().put(slangWord, newDefList);
                System.out.println("Slang word overwritten!");
            } else if(choice.equals("2")) {
                dictionary.addDefinitionToWord(slangWord, definition);
                System.out.println("New definition added to '" + slangWord + "'!");
            }
        } else {
            if(dictionary.addSlangWord(slangWord, definition)) {
                System.out.println("Slang word added successfully!");
            } else {
                System.out.println("Fail to add slang word!");
            }
        }
    }

    private void handleEditSlangWord(String slangWord) {
        System.out.print("Enter new slang word: ");
        String newSlangWord = scanner.nextLine().trim();
        if(!dictionary.editSlangWord(slangWord, newSlangWord)) {
            System.out.println("Fail to update slang word!");
            return;
        }
        System.out.println("Slang word updated successfully!");
    }

    private void handleEditDefinition(String slangWord, int defSize) {
        System.out.print("Enter definition index to edit (1-" + defSize + "): ");
        try {
            int index = scanner.nextInt() - 1;
            scanner.nextLine();
            if (index < 0 || index >= defSize) {
                System.out.println("Invalid index!");
                return;
            }
            System.out.print("Enter new definition: ");
            String newDefinition = scanner.nextLine().trim();
            if (!dictionary.editDefinition(slangWord, index, newDefinition)) {
                System.out.println("Failed to update definition!");
                return;
            }
            System.out.println("Definition updated successfully!");
        } catch(InputMismatchException  e) {
            System.out.println("Invalid input! Please enter a number.");
            scanner.nextLine();
        }
    }

    /**
     * Edit slang word
     */
    private void editSlangWord() {
        System.out.print("\nEnter slang word to edit: ");
        String slangWord = scanner.nextLine().trim();
        List<String> definitions = dictionary.getDictionary().get(slangWord);
        if (definitions == null) {
            System.out.println("Slang word not found!");
            return;
        }
        System.out.println("Definition(s) of '" + slangWord + "':");
        for(int i = 0; i < definitions.size(); i++) {
            System.out.println((i + 1) + ". " + definitions.get(i));
        }
        System.out.print("Do you want to edit slang word (1) or definition (2) or both (3)? (1/2/3): ");
        String choice = scanner.nextLine().trim();
        if(choice.equals("1")) {
            handleEditSlangWord(slangWord);
        } else if(choice.equals("2")) {
            handleEditDefinition(slangWord, definitions.size());
        } else if(choice.equals("3")) {
            System.out.print("Enter new slang word: ");
            String newSlangWord = scanner.nextLine().trim();
            if(!dictionary.editSlangWord(slangWord, newSlangWord)) {
                System.out.println("Fail to update slang word!");
                return;
            }
            System.out.println("Slang word updated successfully!");
            handleEditDefinition(newSlangWord, definitions.size());
        } else {
            System.out.println("Invalid choice!");
            return;
        }
    }

    /**
     * Delete slang word
     */
    private void deleteSlangWord() {
        System.out.print("\nEnter slang word to delete: ");
        String slangWord = scanner.nextLine().trim();

        if(!dictionary.getDictionary().containsKey(slangWord)) {
            System.out.println("Slang word not found!");
            return;
        }
        System.out.print("Are you sure you want to delete '" + slangWord + "'? (yes/no): " );
        String choice = scanner.nextLine().trim().toLowerCase();
        if(choice.equals("yes")) {
            if(!dictionary.deleteSlangWord(slangWord)) {
                System.out.println("Fail to delete slang word '" + slangWord + "'!");
                return;
            }
            System.out.println("Slang word deleted successfully!");
        } else {
            System.out.println("Deletion canceled!");
        }
    }

    /**
     * Show random slang word
     */
    private void showRandomSlangWord() {
        String slangWord = dictionary.getRandomSlangWord();
        if(slangWord == null) {
            System.out.println("Dictionary is empty!");
            return;
        }
        List<String> definitions = dictionary.getDictionary().get(slangWord);
        System.out.println("\nTODAY'S SLANG WORD: " + slangWord);
        for(int i = 0; i < definitions.size(); i++) {
            System.out.println((i + 1) + ". " + definitions.get(i));
        }
    }

    /**
     * Quiz: Guess definition
     */
    private void quizGuessDefinition() {
        String randomSlangWord = dictionary.getRandomSlangWord();
        if(randomSlangWord == null) {
            System.out.println("Dictionary is empty!");
            return;
        }
        List<String> definitions = dictionary.getDictionary().get(randomSlangWord);
        String correctDefinition = definitions.get(0);
        List<String> options = new ArrayList<>();
        options.add(correctDefinition);

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

        System.out.println("\nQUIZ: GUESS A DEFINITION OF A SLANG WORD");
        System.out.println("What does '" + randomSlangWord + "' means?");
        for(int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        System.out.print("Your answer (1-4): ");
        try {
            int answer = scanner.nextInt();
            scanner.nextLine();

            if(answer >= 1 && answer <= 4) {
                if(options.get(answer - 1).equals(correctDefinition)) {
                    System.out.println("Correct! The answer is: " + correctDefinition);
                } else {
                    System.out.println("Wrong! The correct answer is: " + correctDefinition);
                }
            } else {
                System.out.println("Invalid option!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Please enter a number!");
            scanner.nextLine();
        }
    }

    /**
     * Quiz: Guess slang word
     */
    private void quizGuessSlangWord() {
        String randomSlangWord = dictionary.getRandomSlangWord();
        if(randomSlangWord == null) {
            System.out.println("Dictionary is empty!");
            return;
        }
        String correctSlangWord = randomSlangWord;
        List<String> definitions = dictionary.getDictionary().get(randomSlangWord);
        String definition = definitions.get(0);

        List<String> options = new ArrayList<>();
        options.add(correctSlangWord);

        Random random = new Random();
        List<String> allWords = new ArrayList<>(dictionary.getDictionary().keySet());
        while(options.size() < 4) {
            String randomWord = allWords.get(random.nextInt(allWords.size()));
            if(!options.contains(randomWord)) {
                options.add(randomWord);
            }
        }

        Collections.shuffle(options);
        System.out.println("\nQUIZ: GUESS A SLANG WORD FROM A DEFINITION");
        System.out.println("What does '" + definition + "' means?");
        for(int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        System.out.print("Your answer (1-4): ");
        try {
            int answer = scanner.nextInt();
            scanner.nextLine();

            if(answer >= 1 && answer <= 4) {
                if(options.get(answer - 1).equals(correctSlangWord)) {
                    System.out.println("Correct! The answer is: " + correctSlangWord);
                } else {
                    System.out.println("Wrong! The correct answer is: " + correctSlangWord);
                }
            } else {
                System.out.println("Invalid option!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Please enter a number!");
            scanner.nextLine();
        }
    }

    /**
     * Reset dictionary
     */
    private void resetDictionary() {
        System.out.print("\nAre you sure you want to reset to origin dictionary? (yes/no): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if(choice.equals("yes")) {
            dictionary.resetDictionary();
        }
    }
}
