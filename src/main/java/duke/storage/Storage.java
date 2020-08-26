/**
 * Encapsulates a storage object that provides
 * functionality with regard to reading and writing
 * from a file
 */
package duke.storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.ArrayList;

import duke.task.Todo;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.TaskList;

public class Storage {

    // DateTime format constant
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy h:mma");

    // instance variables
    String directory; // directory of data
    File store; // the database

    // constructor
    public Storage(String directory, String path) {
        this.directory = directory;
        this.store = new File(path);
    }

    // methods for reading and writing

    /**
     * Loads tasks data from store, parses them and saves them
     * @return list of tasks read from store
     */
    public List<Task> load() {
        List<Task> res = new ArrayList<>();
        try {
            // initialize
            Files.createDirectories(Paths.get(directory));
            store.createNewFile();
            BufferedReader reader = new BufferedReader(new FileReader(store));

            // start reading and parsing
            while (reader.ready()) {
                String current = reader.readLine().trim();
                String[] tokens = current.split("\\s\\|\\s", 3);
                String type = tokens[0];
                boolean isDone = Integer.parseInt(tokens[1]) == 1;
                String remaining = tokens[2];

                String desc;

                switch(type) {
                case "T":
                    desc = remaining;
                    res.add(new Todo(desc, isDone));
                    break;
                case "E":
                    String[] eventDetails = remaining.split("\\s\\|\\s");
                    desc = eventDetails[0];
                    LocalDateTime date = LocalDateTime.parse(eventDetails[1], DATE_TIME_FORMAT);
                    res.add(new Event(desc, date, isDone));
                    break;
                case "D":
                    String[] deadlineDetails = remaining.split("\\s\\|\\s");
                    desc = deadlineDetails[0];
                    LocalDateTime deadline = LocalDateTime.parse(deadlineDetails[1], DATE_TIME_FORMAT);
                    res.add(new Deadline(desc, deadline, isDone));
                    break;
                default:
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Something went wrong in the loading");
        } finally {
            return res;
        }
    }

    /**
     * Writes tasks data to store after each function call
     * @param tasks the list of tasks to write represented as a TaskList object
     */
    public void write(TaskList tasks) {
        try {
            Files.createDirectories(Paths.get(directory));
            store.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(store));

            for (Task t : tasks.getList()) {
                writer.write(t.databaseRep() + System.lineSeparator());
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Something went wrong in the writing");
        }
    }
}
