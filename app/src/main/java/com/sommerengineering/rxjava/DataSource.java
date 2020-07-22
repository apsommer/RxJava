package com.sommerengineering.rxjava;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a list of tasks.
 */
public class DataSource {

    public static List<Task> createTaskList() {

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("Eat lunch", true, 3));
        tasks.add(new Task("Wash dishes", true, 2));
        tasks.add(new Task("Learn RxJava", false, 1));
        tasks.add(new Task("Go to sleep", false, 4));
        tasks.add(new Task("Go to sleep", false, 4)); // add a duplicate to test distinct()

        return tasks;
    }
}
