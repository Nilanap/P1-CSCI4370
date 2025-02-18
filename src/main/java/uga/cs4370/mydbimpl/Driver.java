package uga.cs4370.mydbimpl;

import java.nio.file.Path;
import java.util.List;

import uga.cs4370.mydb.Predicate;
import uga.cs4370.mydb.RA;
import uga.cs4370.mydb.Relation;
import uga.cs4370.mydb.RelationBuilder;
import uga.cs4370.mydb.Type;

;

public class Driver {

    private static final String DATA_HEAD = "(path)";
    private static final String DATA_INSTRUCTOR = Path.of(DATA_HEAD, "instructor_export.csv").toString();
    private static final String DATA_DEPARTMENT = Path.of(DATA_HEAD, "department_export.csv").toString();
    private static final String DATA_STUDENT = Path.of(DATA_HEAD, "student_export.csv").toString();
    private static final String DATA_ADVISOR = Path.of(DATA_HEAD, "advisor_export.csv").toString();
    private static final String DATA_SECTION = Path.of(DATA_HEAD, "section_export.csv").toString();
    private static final String DATA_TEACHES = Path.of(DATA_HEAD, "teaches_export.csv").toString();

    public static void main(String[] args) {
        RA ra = new RAImpl();

        Relation instructor = new RelationBuilder()
                .attributeNames(List.of("Instructor_ID", "Name", "Department", "Salary"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        instructor.loadData(DATA_INSTRUCTOR);
        Relation department = new RelationBuilder()
                .attributeNames(List.of("Department", "Building Name", "Budget"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        department.loadData(DATA_DEPARTMENT);
        Relation student = new RelationBuilder()
                .attributeNames(List.of("Student_ID", " Last Name", "Major", "Credit Hours"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        student.loadData(DATA_STUDENT);
        Relation advisor = new RelationBuilder()
                .attributeNames(List.of("Student_ID", "Instructor_ID"))
                .attributeTypes(List.of(Type.STRING, Type.STRING))
                .build();
        advisor.loadData(DATA_ADVISOR);

        System.out.println("Query 1 (Bryce Wellman): Students who have completed more than 90 total credits in departments with a budget > $400,000.");
        System.out.println("Query 2 (Nilan Patel): instructors who make over 70k in departments with a budget > $100000");
        System.out.println("Query 3 (Michael Scott): Find the name of each instructor who teaches a section that is located in the building of the department the instructor belongs to, as well as which section that is (course and section IDs, year, and seemster) and what building it is in.");
        System.out.println("Query 4 (Matthew Griffith): Find the names of Students who have taken over 125 credit hours and the IDs of their assigned advisors.");
        System.out.println("Query 5: (Not used) Instructors who's salary totals to at least 1/8th of their departments yearly budget");

        System.out.println("");

        Relation studentFix = ra.rename(student, List.of(" Last Name", "Credit Hours"), List.of("Last_Name", "Credit_Hours"));

        Predicate creditHoursAbove128 = row -> {
            double creditHours = row.get(3).getAsDouble();
            return creditHours > 128;
        };
        Relation highCreditStudents = ra.select(studentFix, creditHoursAbove128);

        Predicate budgetAbove900k = row -> {
            double budget = row.get(2).getAsDouble();
            return budget > 900000;
        };
        Relation wellFundedDepartments = ra.select(department, budgetAbove900k);

        Relation joinedResu = ra.join(highCreditStudents, wellFundedDepartments);

        List<String> projectedAtt = List.of("Last_Name", "Student_ID", "Credit_Hours", "Major", "Department", "Budget");
        Relation query2Result = ra.project(joinedResu, projectedAtt);

        System.out.println("Bryce Wellman Query: Students who have completed more than 128 total credits in departments with a budget > $900,000");
        query2Result.print();

        Predicate salaryGreaterThan70k = row -> {
            double salary = row.get(3).getAsDouble();
            return salary > 70000;
        };
        Relation highSalaryInstructors = ra.select(instructor, salaryGreaterThan70k);
        Predicate budgetGreaterThan100k = row -> {
            double budget = row.get(2).getAsDouble();
            return budget > 100000;
        };
        Relation highBudgetDepartments = ra.select(department, budgetGreaterThan100k);
        Relation joinResult = ra.join(highSalaryInstructors, highBudgetDepartments);

        List<String> projectedAttrs = List.of("Name", "Department", "Salary", "Building Name", "Budget");
        Relation query1Result = ra.project(joinResult, projectedAttrs);

        System.out.println("Nilan Query Result: Instructors who make over 70k in departments with a budget > $100,000");
        query1Result.print();

        queryMichael(ra);

        System.out.println("Matthew Query Result: Find the names of Students who have taken over 125 credit hours and the IDs of their assigned advisors.");

        Relation studentFixed = ra.rename(student, List.of(" Last Name", "Credit Hours"), List.of("Last_Name", "Credit_Hours"));

        Predicate creditHoursAbove125 = row -> {
            double creditHours = row.get(3).getAsDouble();
            return creditHours > 125;
        };
        Relation eligibleStudents = ra.select(studentFixed, creditHoursAbove125);

        Relation advisors = ra.join(eligibleStudents, advisor);

        Relation finalResult = ra.project(advisors, List.of("Last_Name", "Instructor_ID"));
        finalResult.print();

    }

    // Test SQL query: SELECT name, course_id, sec_id, year, building FROM (((SELECT ID, name, building FROM ((SELECT ID, name, dept_name FROM instructor) iT NATURAL JOIN (SELECT dept_name, building FROM department) dT)) _ NATURAL JOIN teaches)) NATURAL JOIN section ORDER BY ID, course_id;
    private static final String QUERY_MICHAEL = "Michael Query: Find the name of each instructor who teaches a section that is located in the building of the department the instructor belongs to, as well as which section that is (course and section IDs, year, and seemster) and what building it is in.";

    private static void queryMichael(RA ra) {
        System.out.println(QUERY_MICHAEL);

        Relation instructor = new RelationBuilder()
                .attributeNames(List.of("ID", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        Relation department = new RelationBuilder()
                .attributeNames(List.of("dept_name", "building", "budget"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        Relation teaches = new RelationBuilder()
                .attributeNames(List.of("ID", "course_id", "sec_id", "semester", "year"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        Relation section = new RelationBuilder()
                .attributeNames(List.of("course_id", "sec_id", "semester", "year", "building", "room_number", "time_slot_id"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.INTEGER, Type.STRING, Type.STRING, Type.STRING))
                .build();

        instructor.loadData(DATA_INSTRUCTOR);
        department.loadData(DATA_DEPARTMENT);
        section.loadData(DATA_SECTION);
        teaches.loadData(DATA_TEACHES);

        // Just the attributes we need from instructor
        // [ID, name, dept_name]
        Relation instructorTrim = ra.project(instructor, List.of("ID", "name", "dept_name"));

        // Just the attributes we need from department
        // [dept_name, building]
        Relation departmentTrim = ra.project(department, List.of("dept_name", "building"));

        // Each instructor's ID and name linked to the department they belong to and the building that department uses
        // [ID, name, dept_name, building]
        Relation instrWithDept = ra.join(instructorTrim, departmentTrim);

        // We no longer need dept_name, we only care about the building and the instructor.
        // We now have every instructor's ID and name linked to the building of the department they belong to.
        // [ID, name, building]
        Relation instrWithDeptTrim = ra.project(instrWithDept, List.of("ID", "name", "building"));

        // We now have each instructor's ID, name, department building linked with every section they have ever taught.
        // [ID, name, building, course_id, sec_id, semester, year]
        Relation instrWithTeaches = ra.join(instrWithDeptTrim, teaches);

        // Link each instructor/building/section key combo to the other info about each section
        // [ID, name, building, course_id, sec_id, semester, year, room_number, time_slot_id]
        Relation instrWithSectionSameBuilding = ra.join(instrWithTeaches, section);

        // Get the attributes we care about
        Relation result = ra.project(instrWithSectionSameBuilding,
                List.of("name", "course_id", "sec_id", "year", "building"));

        result.print();

    }

    /* 
        // Test Union
        Relation instructorModified = new RelationBuilder()
       .attributeNames(List.of("ID", "name", "dept_name", "cred_salary")) // Renaming salary as "cred_salary"
       .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.DOUBLE))
       .build();

        Relation studentModified = new RelationBuilder()
            .attributeNames(List.of("ID", "name", "dept_name", "cred_salary")) // Renaming tot_cred as "cred_salary"
            .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.DOUBLE))
            .build();

        instructorModified.loadData("/Users/nilanpatel/Desktop/Junior Year /Junior Year S2/DataBase Mgmtn CSCI 4370/instructor_export.csv");
        studentModified.loadData("/Users/nilanpatel/Desktop/Junior Year /Junior Year S2/DataBase Mgmtn CSCI 4370/instructor_export.csv");

        Relation unionResult = ra.union(instructorModified, studentModified);

        System.out.println("Union of Instructor and Student:");
        unionResult.print();

        // Should throw exception
        Relation instructor = new RelationBuilder()
            .attributeNames(List.of("ID", "name", "dept_name", "salary")) // Original instructor schema
            .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.DOUBLE))
            .build();

        Relation student = new RelationBuilder()
            .attributeNames(List.of("ID", "name", "dept_name", "tot_cred")) // Original student schema
            .attributeTypes(List.of(Type.STRING, Type.STRING, Type.STRING, Type.DOUBLE))
            .build();

        instructor.loadData("/Users/nilanpatel/Desktop/Junior Year /Junior Year S2/DataBase Mgmtn CSCI 4370/instructor_export.csv");
        student.loadData("/Users/nilanpatel/Desktop/Junior Year /Junior Year S2/DataBase Mgmtn CSCI 4370/student_export.csv");


        try {
        Relation unionResult1 = ra.union(instructor, student);
        System.out.println("Test Failed: Union should not be possible!");
        } catch (IllegalArgumentException e) {
        System.out.println("Test Passed: Caught expected IllegalArgumentException -> " + e.getMessage());
        }

        // rename testing
        List<String> origAttr = List.of("ID", "name", "dept_name", "salary");
        List<String> renamedAttr = List.of("Hello", "Bruh", "Hi", "Renamed");

        Relation renamedRelation = ra.rename(instructor, origAttr, renamedAttr);

        System.out.println("Renamed Relation:");
        renamedRelation.print();

     */
 /* 
        // this all tests project and select operations
        // instructors above 70k (test select)
        Predicate salaryGreaterThan70k = row -> {
            double salary = row.get(3).getAsDouble(); 
            return salary > 70000;
        };

        // instructors below 40k (test select)
        Predicate salaryLow = row -> {
            double salary = row.get(3).getAsDouble(); 
            return salary < 40000;
        };

        Predicate nameMird = row -> {
            String name = row.get(1).getAsString(); 
            return name.equals("Mird");
        };
       

        // select operation.
        Relation highSalaryInstructors = ra.select(instructor, salaryGreaterThan70k);
        Relation broke = ra.select(instructor, salaryLow);
        Relation mird = ra.select(instructor, nameMird);

        System.out.println("\nInstructors with Salary > 70000:");
        highSalaryInstructors.print();
        System.out.println("\nInstructors who r broke:");
        broke.print();
        System.out.println("\nInstructors who name Mird:");
        mird.print();

        System.out.println("Original Instructor Relation:");
        instructor.print();

        List<String> projectedAttrs = List.of("Name", "Salary");

        // Perform the project operation.
        Relation projectedRelation = ra.project(instructor, projectedAttrs);
        // projected relation.
        System.out.println("\nProjected Relation (Name and Salary):");
        projectedRelation.print();

    
        // this  test cart product
        // lol dont try w student x instructor it is ALOT of combos and takes 3 mins 

        try {
            Relation cartesianProductResult = ra.cartesianProduct(instructor, department);

            // Print the result of the Cartesian Product.
            System.out.println("\nCartesian Product of Instructor and Student:");
            cartesianProductResult.print();
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
     */
}
