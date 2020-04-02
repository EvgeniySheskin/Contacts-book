package contacts;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PhoneBook implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Record> records;
    transient private StandardInputHandler in;
    public boolean isUsed;
    public String fileName;

    public PhoneBook(StandardInputHandler in, String fileName) {
        this.records = new ArrayList<>();
        this.in = in;
        isUsed = true;
        this.fileName = fileName;
    }

    public void addRecord(Record record) {
        this.records.add(record);
    }

    public void removeRecord(int selectedRecord) {
        this.records.remove(selectedRecord - 1);
    }

    public int countRecords() {
        if (this.records == null) return 0;
        return this.records.size();
    }
    public StandardInputHandler getIn() {
        return this.in;
    }

    public void setIn(StandardInputHandler in) {
        this.in = in;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < this.records.size(); i++) {
            out.append(i + 1)
                    .append(". ")
                    .append(this.records.get(i).getNameForList())
                    .append("\n");
        }
        return out.toString();
    }
    public Record getRecordByIndex(int index) {
        return this.records.get(index);
    }

    public int getIndexByRecord(Record rec) {
        for (int i = 0; i < this.records.size(); i++) {
            if (this.records.get(i).getName().equals(rec.getName())) return i;
        }
        return -1;
    }

    public void add() {
        Record record = null;
        String name, surname, address, phoneNumber, birthDate;
        char gender;
        System.out.print("Enter the type (person, organization): ");
        switch(in.getNextLine()) {
            case "person":
                System.out.print("Enter the name: ");
                name = in.getNextLine();
                System.out.print("Enter the surname: ");
                surname = in.getNextLine();
                System.out.print("Enter the birth date: ");
                birthDate = in.getNextLine();
                if (!Person.checkBirthDate(birthDate)) {
                    System.out.println("Bad birth date!");
                }
                System.out.print("Enter the gender (M, F): ");
                gender = in.getNextChar();
                if (!Person.checkGender(gender)) {
                    System.out.println("Bad gender!");
                }
                System.out.print("Enter the number: ");
                phoneNumber = in.getNextLine();
                if (!Record.checkValidityOfPhoneNumber(phoneNumber)) {
                    phoneNumber = "";
                }
                record = new Person(name, phoneNumber, surname, gender, birthDate);
                break;
            case "organization":
                System.out.print("Enter the organization name: ");
                name = in.getNextLine();
                System.out.print("Enter the address: ");
                address = in.getNextLine();
                System.out.print("Enter the number: ");
                phoneNumber = in.getNextLine();
                if (!Record.checkValidityOfPhoneNumber(phoneNumber)) {
                    phoneNumber = "";
                }
                record = new Organization(name, address, phoneNumber);
                break;
            default:
                System.out.println("Unknown command");
        }
        if (record != null) {
            addRecord(record);
            System.out.println("The record added.\n");
            new SaveCommand(this, this.fileName).execute();
        } else System.out.println("Unknown error occurred. The record has not been added.\n");
    }

    public void remove(int index) {
        removeRecord(index);
        System.out.println("The record removed!");
        new SaveCommand(this, this.fileName).execute();
    }

    public void edit(int index) {

        Record editedRecord = getRecordByIndex(index - 1);
        StringBuilder query = new StringBuilder("name, ");
        for (String item : editedRecord.getAllFieldsToChange()) {
            query.append(item).append(", ");
        }
        query.append("number");
        System.out.print("Select a field (" + query.toString() + "): ");
        String field = in.getNextLine();
        System.out.printf("Enter %s: ", field);
        editedRecord.setField(field, in.getNextLine());
        System.out.println("Saved");
        editedRecord.setLastEdit();
        new SaveCommand(this, this.fileName).execute();
    }

    public void count() {
        System.out.printf("The Phone book has %d records.\n\n", countRecords());
    }

    public void list() {
        System.out.println(this);
        GeneralMenu listMenu = GeneralMenu.constructMenu("[list] Enter action",
                                            "[number]", "back");
        String input = listMenu.getCommandFromUser(listMenu.toString(), this.in);
        if (input.matches("\\d+")) {
            listMenu.setCommand("[number]", new InfoCommand(this,Integer.parseInt(input)));
            listMenu.executeCommand("[number]");
        }
        else if (input.matches("back")) {
            System.out.println();
        }
    }

    public void info(int index) {
        while (true) {
            System.out.println(getRecordByIndex(index - 1).visualizeRecord());
            System.out.println();
            GeneralMenu recordMenu = new GeneralMenu("[record] Enter action");
            recordMenu.add("edit", new EditCommand(this, index))
                    .add("delete", new RemoveCommand(this, index))
                    .add("menu", new SaveCommand(this, this.fileName));
            String command = recordMenu.getCommandFromUser(recordMenu.toString(), this.in);
            recordMenu.executeCommand(command);
            if (command.equals("menu")) {
                System.out.println();
                break;
            }
        }
    }

    public void search(String query) {
        List<Record> searchResults = new ArrayList<>();
        for (Record rec : this.records) {
            String temp = rec.getRecordAsString();
            if (temp.matches("[a-z\\+0-9._\\-'\\(\\)]*" + query + "['a-z\\+\\(\\)0-9\\-._]*")) {
                searchResults.add(rec);
            }
        }
        PhoneBook filtered = new PhoneBook(this.in, this.fileName);
        filtered.records = searchResults;
        if (filtered.records.size() == 0) System.out.println("No matching records found.\n");
        else if (filtered.records.size() == 1) System.out.printf("Found %d result:\n", filtered.countRecords());
        else System.out.printf("Found %d results:\n", filtered.countRecords());
        System.out.println(filtered);
        GeneralMenu searchMenu = GeneralMenu.constructMenu("[search] Enter action",
                                                "[number]", "back", "again");
        String command = searchMenu.getCommandFromUser(searchMenu.toString(), this.in);
        if (command.matches("\\d+")) {
            int index = this.getIndexByRecord(filtered.records.get(Integer.parseInt(command) - 1));
            searchMenu.setCommand("[number]", new InfoCommand(this, ++index));
            searchMenu.executeCommand("[number]");
        }
        else {
            switch(command) {
                case "back":
                    System.out.println();
                    break;
                case "again":
                    searchMenu.setCommand(command, new SearchCommand(filtered));
                    searchMenu.executeCommand(command);
                    break;
                default:
                    System.out.println("Unknown command!\n");
            }
        }
    }
}

class SaveLoad {

        public static void save(PhoneBook phoneBook, String fileName) throws IOException {
        if (fileName.isEmpty()) {
            System.out.println("Save file is not specified.");
            return;
        }
        FileOutputStream fos = new FileOutputStream(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(phoneBook);
        oos.close();
    }
    public static PhoneBook load(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ObjectInputStream ois = new ObjectInputStream(bis);
        PhoneBook phoneBook = (PhoneBook) ois.readObject();
        ois.close();
        return phoneBook;
    }
}