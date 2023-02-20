package duke;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for internal components of the chatbot.
 */
public class Duke {
    /** Deals with loading tasks from the file and saving tasks in the file */
    private final Storage storage;

    /** Contains the task list */
    private TaskList tasks;

    /** Deals with interactions with the user */
    private final Ui ui;

    /** Stores the path of the folder which contains the task file storage */
    private final Path foldPath;

    /** Stores the path of the task file storage */
    private final Path filePath;

    /**
     * Constructs Duke to make use of the UI,
     * Storage and TaskList in the program.
     */
    public Duke() {
        String fileSep = System.getProperty("file.separator");
        String userDir = System.getProperty("user.dir");
        foldPath = Paths.get( userDir + fileSep + "data");
        filePath = Paths.get(foldPath + fileSep + "duke.txt");

        ui = new Ui();
        storage = new Storage(filePath.toString());
        try {
            tasks = new TaskList(storage.load());
        } catch (DukeException e) {
            createFile();
            tasks = new TaskList();
        }
    }

    /**
     * Creates the folder and file to save and
     * load tasks if they do not already exist.
     */
    public void createFile() {
        try {
            if (!Files.isDirectory(foldPath)) {
                Files.createDirectory(foldPath);
                Files.createFile(filePath);
            } else if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            } else {
                ui.showLoadingError();
            }
        } catch (IOException e) {
            ui.showFileError();
        }
    }

    /**
     * Generates a response to user input to display.
     *
     * @param input Input command from the user.
     * @throws DukeException If the tasks cannot be saved to the file.
     */
    public String getResponse(String input) throws DukeException {
        String response = ui.getMessage().toString();
        if (response.isEmpty()) {
            Parser p = new Parser(input, ui, tasks, storage);
            p.parse();
            response = ui.getMessage().toString();
        }
        ui.clearMessage();
        assert ui.getMessage().toString().isEmpty() : "User input should have been cleared";
        return response;
    }
}
