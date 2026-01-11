package bg.reshavalnik.app.exceptions.message;

public class ErrorMessage {

    public static final String WRONG_EMAIL_OR_PASSWORD = "Wrong email or password!";
    public static final String USERNAME_IS_ALREADY_TAKEN = "Email is already taken!";
    public static final String CANNOT_ENTER_AS_ADMIN = "Cannot enter as: ";
    public static final String CANNOT_CHANGE_PASSWORD_FOR_ADMIN = "Cannot change password for: ";
    public static final String WRONG_OLD_PASSWORD = "Wrong old password!!!! : ";
    public static final String USER_NOT_FOUND = "User not found!";

    //    Tasks
    public static final String TASK_ALREADY_EXISTS = "Task with this name already exists!";
    public static final String TASK_NOT_FOUND = "Task not found!";
    public static final String FILE_NOT_FOUND = "File not found: ";
    public static final String ERROR_WRITING_TEMPORARY_FILE = "Error writing temporary file: ";
    public static final String ERROR_READING_PROCESS_OUTPUT = "Error reading process output: ";
    public static final String PROCESS_WAS_INTERRUPTED = "Process was interrupted: ";
    public static final String THERE_IS_NO_SUCH_TASK_FOR_THIS_USER =
            "There is no such task for this user.";
    public static final String THERE_IS_NOT_EXIST_PENDING_EXAM = "There is not exist pending exam";

    //    ResultExam
    public static final String RESULT_EXAM_DOES_NOT_EXIST = "The exam result does not exist.";

    // Section
    public static final String SECTION_ALREADY_EXISTS = "Section already exists";
    public static final String SECTION_NOT_FOUND = "Section not found";
    public static final String SECTION_MISSED = "The section is missing";

    // TaskSection
    public static final String TASK_SECTION_NOT_FOUND = "TaskSection not found";
}
