package oof.command;

import java.util.ArrayList;

import oof.SelectedInstance;
import oof.Ui;
import oof.exception.CommandException.CommandException;
import oof.exception.CommandException.InvalidArgumentException;
import oof.exception.CommandException.MissingArgumentException;
import oof.exception.CommandException.ModuleNotSelectedException;
import oof.model.module.Module;
import oof.model.module.SemesterList;
import oof.model.task.Assignment;
import oof.model.task.TaskList;
import oof.storage.StorageManager;

public class AddAssignmentCommand extends AddDeadlineCommand {

    public static final String COMMAND_WORD = "assignment";

    /**
     * Constructor for AddAssignmentCommand.
     *
     * @param arguments Command inputted by user for processing.
     */
    public AddAssignmentCommand(ArrayList<String> arguments) {
        super(arguments);
    }

    /**
     * Adds a deadline task to taskList.
     *
     * @param semesterList   Instance of SemesterList that stores Semester objects.
     * @param taskList       Instance of TaskList that stores Task objects.
     * @param ui             Instance of Ui that is responsible for visual feedback.
     * @param storageManager Instance of Storage that enables the reading and writing of Task
     *                       objects to hard disk.
     * @throws CommandException if module is not selected or if user input contains missing or invalid arguments.
     */
    @Override
    public void execute(SemesterList semesterList, TaskList taskList, Ui ui, StorageManager storageManager)
            throws CommandException {
        SelectedInstance selectedInstance = SelectedInstance.getInstance();
        Module module = selectedInstance.getModule();
        if (module == null) {
            throw new ModuleNotSelectedException("OOPS!! Please select a Module.");
        }
        String moduleCode = module.getModuleCode();
        String description = arguments.get(INDEX_DESCRIPTION);
        if (arguments.get(INDEX_DESCRIPTION).equals("")) {
            throw new MissingArgumentException("OOPS!!! The assignment needs a name.");
        }
        if (arguments.size() < ARRAY_SIZE_DATE || arguments.get(INDEX_DATE).equals("")) {
            throw new MissingArgumentException("OOPS!!! The assignment needs a due date.");
        }
        String date = parseDateTime(arguments.get(INDEX_DATE));
        if (!isDateValid(date)) {
            throw new InvalidArgumentException("OOPS!!! The due date is invalid.");
        } else {
            Assignment assignment = new Assignment(moduleCode, description, date);
            if (exceedsMaxLength(assignment.getDescription())) {
                throw new InvalidArgumentException("OOPS!!! Task exceeds maximum description length!");
            }
            taskList.addTask(assignment);
            module.addAssignment(assignment);
            ui.addTaskMessage(assignment, taskList.getSize());
            storageManager.writeTaskList(taskList);
        }
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
