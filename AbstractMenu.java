package contacts;

import java.util.LinkedHashMap;
import java.util.Map;

interface Command {
    void execute();
}

class AddCommand implements Command {
    private PhoneBook phoneBook;
    public AddCommand(PhoneBook phoneBook) {
        this.phoneBook = phoneBook;
    }
    public void execute() {
        this.phoneBook.add();
    }
}

class RemoveCommand implements Command {
    private PhoneBook phoneBook;
    private int index;
    public RemoveCommand(PhoneBook phoneBook, int index) {
        this.phoneBook = phoneBook;
        this.index = index;
    }
    public void execute() {
        this.phoneBook.remove(this.index);
    }
}
class EditCommand implements Command {
    private PhoneBook phoneBook;
    private int index;
    public EditCommand(PhoneBook phoneBook, int index) {
        this.phoneBook = phoneBook;
        this.index = index;
    }
    public void execute() {
        this.phoneBook.edit(this.index);
    }
}

class CountCommand implements Command {
    private PhoneBook phoneBook;
    public CountCommand(PhoneBook phoneBook) {
        this.phoneBook = phoneBook;
    }
    public void execute() {
        this.phoneBook.count();
    }
}

class InfoCommand implements Command {
    private PhoneBook phoneBook;
    private int index;
    public InfoCommand(PhoneBook phoneBook, int index) {
        this.phoneBook = phoneBook;
        this.index = index;
    }
    public void execute() {
        this.phoneBook.info(this.index);
    }
}

class ListCommand implements Command {
    private PhoneBook phoneBook;
    public ListCommand(PhoneBook phoneBook) {
        this.phoneBook = phoneBook;
    }
    public void execute() {
        this.phoneBook.list();
    }
}

class SearchCommand implements Command {
    private PhoneBook phoneBook;
    public SearchCommand(PhoneBook phoneBook) {
        this.phoneBook = phoneBook;
    }
    public void execute() {
        this.phoneBook.search(askForQuery(phoneBook.getIn()));
    }

    public String askForQuery(StandardInputHandler in) {
        System.out.print("Enter search query: ");
        return in.getNextLine();
    }
}

class ExitCommand implements Command {
    private PhoneBook phoneBook;
    private String fileName;
    public ExitCommand(PhoneBook phoneBook, String fileName) {
        this.phoneBook = phoneBook;
        this.fileName = fileName;
    }
    @Override
    public void execute() {
        new SaveCommand(phoneBook, fileName).execute();
        phoneBook.isUsed = false;
    }
}

class SaveCommand implements Command {
    private PhoneBook phoneBook;
    private String fileName;

    public SaveCommand(PhoneBook phoneBook, String fileName) {
        this.phoneBook = phoneBook;
        this.fileName = fileName;
    }

    @Override
    public void execute() {
        try {
            SaveLoad.save(this.phoneBook, fileName);
        } catch (Exception e) {
            System.out.println("Error: Can't save Phone book to file!");
        }
    }
}

class GeneralMenu {

    private String menuTitle;
    private LinkedHashMap<String, Command> menuItems;

    public GeneralMenu(String menuTitle) {
        this.menuTitle = menuTitle;
        this.menuItems = new LinkedHashMap<>();
    }

    public void setCommand (String name, Command command) {
        this.menuItems.put(name, command);
    }

    public void executeCommand(String name) {
        if (this.menuItems.containsKey(name)) {
            this.menuItems.get(name).execute();
        }
        else System.out.println("Unknown command!\n");
    }

    public String toString() {
        StringBuilder out = new StringBuilder(this.menuTitle + " (");
        for (Map.Entry<String, Command> item : this.menuItems.entrySet()) {
            out.append(item.getKey())
                    .append(", ");
        }
        out.deleteCharAt(out.length() - 1).setCharAt(out.lastIndexOf(","),')');
        return out.append(": ").toString();
    }

    public GeneralMenu add(String name, Command command) {
         this.menuItems.putIfAbsent(name, command);
         return this;
    }

    public String getCommandFromUser(String menu, StandardInputHandler in) {
        System.out.print(menu);
        return in.getNextLine();
    }

    public static GeneralMenu constructMenu(String title, String... commands) {
        GeneralMenu menu = new GeneralMenu(title);
        for (String string : commands) {
            menu.add(string, null);
        }
        return menu;
    }

}