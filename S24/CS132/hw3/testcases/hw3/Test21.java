class TestInheritance {
  public static void main(String[] args) {
    Teacher teacher;
    Student student;
    int result;

    teacher = new Teacher();
    student = new Student();

    System.out.println(student.setId(1234)); // Set student ID
    System.out.println(
        teacher.setId(5678)); // Set teacher ID, different from student's due to shadowing

    System.out.println(student.initGrades()); // Initialize grades array
    result =
        teacher.computeGrades(
            student.getAttendance(),
            student.getPerformance()); // Compute grades based on student data
    System.out.println(student.setGrade(result)); // Store the grade in the array

    System.out.println(teacher.getId()); // Print teacher's ID
    System.out.println(student.getId()); // Print student's ID, tests variable shadowing
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

class Teacher extends Person {
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

class Student extends Person {
  int[] grades;
  int id; // Shadowing parent's ID

  public int initGrades() {
    grades = new int[10]; // Initialize grades array with capacity for 10 grades
    return 0; // Dummy return for MiniJava compliance
  }

  public int setGrade(int grade) {
    int i;
    boolean done;

    i = 0;
    done = false; // Control variable to exit the loop once the grade is set

    while ((i < (grades.length)) && !done) {
      if ((grades[i]) < 1) {
        grades[i] = grade;
        done = true; // Grade set, prepare to exit loop
      } else {}
      i = i + 1;
    }

    return 0;
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

  // Overriding the setId method to test shadowing
  public int setId(int newId) {
    id = newId;
    return id;
  }

  // Getter for shadowed id
  public int getId() {
    return id;
  }
}
