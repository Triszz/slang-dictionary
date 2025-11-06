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
        if(!dictionary.loadDictionary()) {
            dictionary.loadFromFile("slang.txt");
            dictionary.saveDictionary();
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
        System.out.println("Chose an option: ");
    }

    /**
     * Run app
     */
    public void run() {
        while (true) {
            showMenu();
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
                    // Edit slang word
                    break;
                case "6":
                    // Delete slang word
                    break;
                case "7":
                    // Reset a dictionary
                    break;
                case "8":
                    // Random a slang word
                    break;
                case "9":
                    // Quiz: Guess a definition
                    break;
                case "10":
                    // Quiz: Guess a slang word
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
    public void searchBySlangWord() {
        System.out.print("Enter a slang word: ");
        String word = scanner.nextLine().trim();

        List<String> definitions = dictionary.searchBySlangWord(word);
        if(!definitions.isEmpty()) {
            System.out.println("Found: " + word);
            for(int i = 0; i < definitions.size(); i++){
                System.out.println((i + 1) + ". " + definitions.get(i));
            }
        } else {
            System.out.println("Slang word " + word + " not found!");
        }
    }

    /**
     * Search by definition
     */
    public void searchByDefinition() {
        System.out.print("Enter a definition: ");
        String keyword = scanner.nextLine().trim();

        List<String> slangs = dictionary.searchByDefinition(keyword);
        if (!slangs.isEmpty()) {
            System.out.println("Found: " + keyword);
            for (int i = 0; i < slangs.size(); i++) {
                System.out.println((i + 1) + ". " + slangs.get(i));
            }
        } else {
            System.out.println("Definition " + keyword + " does not match any slang word!");
        }
    }

    /**
     * View search history
     */
    public void viewSearchHistory() {
        System.out.println("=================================== Search History ===================================");
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
    public void addSlangWord() {
        System.out.print("Enter slang word: ");
        String slangWord = scanner.nextLine().trim();
        System.out.println("Enter definition: ");
        String definition = scanner.nextLine().trim();

        if(dictionary.getDictionary().containsKey(slangWord)) {
            System.out.println("Slang word " + slangWord + " already exists!");
            System.out.print("Do you want to (1) Overwrite or (2) Duplicate? (1/2): ");
            String choice = scanner.nextLine().trim();
            if(choice.equals(("1"))) {
                List<String> newDefList = new ArrayList<>();
                newDefList.add(definition);
                dictionary.getDictionary().put(slangWord, newDefList);
                System.out.println("Slang word overwritten!");
            } else if(choice.equals("2")) {
                dictionary.addDefinitionToWord(slangWord, definition);
                System.out.println("New definition added to " + slangWord + "!");
            }
        } else {
            if(dictionary.addSlangWord(slangWord, definition)) {
                System.out.println("Slang word added successfully!");
            } else {
                System.out.println("Fail to add slang word!");
            }
        }
    }

    /* TODO: Edit slang word */


}
