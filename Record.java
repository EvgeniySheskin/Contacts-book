package contacts;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class Record implements Serializable {
    private String name;
    private static final long serialVersionUID = 2L;
    private String phoneNumber;
    private LocalDateTime lastEdit = LocalDateTime.now();
    public final LocalDateTime creationTime = LocalDateTime.now();

    Record(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = checkValidityOfPhoneNumber(phoneNumber) ? phoneNumber : "";
    }

    abstract public String[] getAllFieldsToChange();
    abstract public void setField(String fieldName, String newValue);

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = checkValidityOfPhoneNumber(phoneNumber) ? phoneNumber : "";
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setLastEdit() {
        this.lastEdit = LocalDateTime.now();
    }

    public LocalDateTime getLastEdit() {
        return lastEdit;
    }

    public boolean hasNumber() {
        return (!this.phoneNumber.isEmpty());
    }

    protected static boolean checkValidityOfPhoneNumber(String phoneNumber) {
        String splittedGroupsRegex = "\\+?((\\(?[a-zA-Z\\d]+\\)?)?([\\s-][a-zA-Z\\d]{2,})?|([a-zA-Z\\d]+[\\s-])?(\\(?[a-zA-Z\\d]{2,}\\)?)?)(([\\s-][a-zA-Z\\d]{2,})+)?";
        String oneGroupRegex = "\\+?(\\(?[a-zA-Z\\d]+\\)?[a-zA-Z\\d]+|[a-zA-Z\\d]+\\(?[a-zA-Z\\d]+\\)?)([a-zA-Z\\d]+)+";
        Pattern pattern = Pattern.compile(splittedGroupsRegex + "|" + oneGroupRegex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    abstract public String visualizeRecord();
    abstract public String getNameForList();
    abstract public String getRecordAsString();

}

class Person extends Record {

    private String surname;
    private char gender;
    private String birthDate;

    public Person(String name, String phoneNumber, String surname, char gender, String birthDate) {
        super(name, phoneNumber);
        this.gender = checkGender(gender) ? gender : '\u0000';
        this.birthDate = checkBirthDate(birthDate) ? birthDate : "";
        this.surname = surname;
    }

    protected static boolean checkBirthDate(String birthDate) {
        try {
            LocalDate.parse(birthDate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    protected static boolean checkGender(char gender) {
        return gender == 'M' || gender == 'F';
    }

    @Override
    public String[] getAllFieldsToChange() {
        return new String[] {"surname","birth","gender"};
    }

    @Override
    public void setField(String fieldName, String newValue) {
        switch(fieldName) {
            case "name":
                this.setName(newValue);
                break;
            case "surname":
                this.surname = newValue;
                break;
            case "birth":
                this.birthDate = checkBirthDate(newValue) ? newValue : "";
                break;
            case "gender":
                this.gender = checkGender(newValue.charAt(0)) ? newValue.charAt(0) : '\u0000';
                break;
            case "number":
                this.setPhoneNumber(newValue);
                break;
        }
    }

    @Override
    public String getRecordAsString() {
        StringBuilder out = new StringBuilder(this.getName() + this.surname);
        if (checkGender(this.gender)) out.append(this.gender);
        if (checkBirthDate(this.birthDate)) out.append(this.birthDate);
        out.append(this.getPhoneNumber());
        return out.toString().replaceAll(" ","").toLowerCase();
    }

    @Override
    public String visualizeRecord() {
        StringBuilder out = new StringBuilder();
        out.append("Name: ")
                .append(super.getName())
                .append("\nSurname: ")
                .append(this.surname)
                .append("\n");
        if (!this.birthDate.isEmpty()) out.append("Birth date: ")
                                                    .append(LocalDate.parse(this.birthDate)).append("\n");
        else out.append("Birth date: [no data]\n");
        if (this.gender != '\u0000') out.append("Gender: ")
                                        .append(this.gender)
                                        .append("\n");
        else out.append("Gender: [no data]\n");
        if (super.hasNumber()) out.append("Number: ")
                                    .append(super.getPhoneNumber())
                                    .append("\n");
        else out.append("Number: [no data]\n");
        out.append("Time created: ")
                .append(super.creationTime.withNano(0).withSecond(0))
                .append("\nTime last edit: ")
                .append(super.getLastEdit().withNano(0).withSecond(0));
        return out.toString();
    }

    @Override
    public String getNameForList() {
        return this.getName() + " " + this.surname;
    }
}

class Organization extends Record {

    private String address;

    public Organization(String name, String address, String phoneNumber) {
        super(name, phoneNumber);
        this.address = address;
    }

    @Override
    public String[] getAllFieldsToChange() {
        return new String[] {"address"};
    }

    @Override
    public void setField(String fieldName, String newValue) {
        switch (fieldName) {
            case "name":
                this.setName(newValue);
                break;
            case "address":
                this.address = newValue;
                break;
            case "number":
                this.setPhoneNumber(newValue);
                break;
        }
    }

    @Override
    public String visualizeRecord() {
        StringBuilder out = new StringBuilder();
        out.append("Organization name: ")
                .append(super.getName())
                .append("\nAddress: ")
                .append(this.address)
                .append("\n");
        if (super.hasNumber()) out.append("Number: ")
                                .append(super.getPhoneNumber())
                                .append("\n");
        else out.append("[no data]\n");
        out.append("Time created: ")
                .append(super.creationTime.withNano(0).withSecond(0))
                .append("\nTime last edit: ")
                .append(super.getLastEdit().withNano(0).withSecond(0));
        return out.toString();
    }

    @Override
    public String getNameForList() {
        return this.getName();
    }

    @Override
    public String getRecordAsString() {
        return (this.getName() + this.address
                + this.getPhoneNumber()).replaceAll(" ","").toLowerCase();
    }
}
