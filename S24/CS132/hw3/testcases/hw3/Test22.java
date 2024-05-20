class TestDoubleInheritance {
  public static void main(String[] args) {
    GrandStudent student;
    int result;

    student = new GrandStudent();

    System.out.println(student.setId(1234)); // Set student ID
    System.out.println(student.setSpecialId(5678)); // Set a special ID, demonstrating shadowing

    System.out.println(student.initGrades()); // Initialize grades array
    result =
        student.computeGrades(
            student.getAttendance(),
            student.getPerformance()); // Compute grades based on student data
    System.out.println(student.setGrade(result)); // Store the grade in the array

    System.out.println(student.getId()); // Print the inherited ID
    System.out.println(student.getSpecialId()); // Print the special ID, tests variable shadowing
    System.out.println(student.getGrade(0)); // Print the computed grade from the array
  }
}

class Person {
  int id;

  public int setId(int newId) {
    id = newId;
    return id;
  }

  public int getId() {
    return id;
  }
}

class Student extends Person {
  int[] grades;

  public int initGrades() {
    grades = new int[10]; // Initialize grades array with capacity for 10 grades
    return 1; // Success initialization
  }

  public int setGrade(int grade) {
    int i;
    i = 0;
    while (i < (grades.length)) {
      grades[i] = grade;
      i = i + 1;
    }
    return 0; // No space to set the grade
  }

  public int getGrade(int index) {
    return grades[index];
  }

  public int getAttendance() {
    return 85; // Dummy attendance for testing
  }

  public int getPerformance() {
    return 75; // Dummy performance for testing
  }
}

class GrandStudent extends Student {
  int specialId; // Shadowing some ID, not necessarily from Person but introducing the concept

  public int setSpecialId(int newId) {
    specialId = newId;
    return specialId;
  }

  public int getSpecialId() {
    return specialId;
  }

  public int computeGrades(int attendance, int performance) {
    int grade;
    if (80 < attendance) {
      if (70 < performance) {
        grade = 5; // High grade
      } else {
        grade = 3; // Average grade
      }
    } else {
      grade = 1; // Low grade
    }
    return grade;
  }
}
