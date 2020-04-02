package contacts;

public class Main {
    public static void main(String[] args) {
        StandardInputHandler in = new StandardInputHandler();
        PhoneBook contacts;
        String fileName;
        if (args.length != 0) {
            try {
                fileName = args[0];
                contacts = SaveLoad.load(fileName);
                contacts.setIn(in);
                contacts.fileName = fileName;
            } catch (Exception e) {
                System.out.println("Error: Can't load Phone book from file. New Phone book created.");
                fileName = args[0];
                contacts = new PhoneBook(in, fileName);
            }
        } else {
            contacts = new PhoneBook(in, "default.txt");
            fileName = "default.txt";
        }
        GeneralMenu mainMenu = new GeneralMenu("[menu] Enter action");
        mainMenu.add("add", new AddCommand(contacts))
                .add("list", new ListCommand(contacts))
                .add("search", new SearchCommand(contacts))
                .add("count", new CountCommand(contacts))
                .add("exit", new ExitCommand(contacts, contacts.fileName));
        while(contacts.isUsed) {
            System.out.print(mainMenu);
            mainMenu.executeCommand(in.getNextLine());
        }
    }
}
