package test.app;

import java.util.ArrayList;
import java.util.List;

public class App {

  public static void main(String[] args) {
    List<String> myList = new ArrayList<>();
    myList.add("Hello");
    myList.add("World");

    // Scenario 1: Accessing list without checking if it's empty
    System.out.println(myList.get(0));

    // Scenario 2: Accessing list after checking it's not empty
    if (!myList.isEmpty()) {
      System.out.println(myList.get(1));
    }

    // Scenario 3: Potentially problematic loop (if list were empty)
    for (String item : myList) {
      System.out.println(item);
    }

    // Scenario 4: Safely iterating over the list
    if (!myList.isEmpty()) {
      for (String item : myList) {
        System.out.println(item);
      }
    }

    // Scenario 5: Clearing the list and then trying to access it
    myList.clear();
    if (!myList.isEmpty()) {
      System.out.println(myList.get(0));
    }
  }
}
