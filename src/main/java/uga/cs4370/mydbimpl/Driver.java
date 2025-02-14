package uga.cs4370.mydbimpl;

import java.util.List;

import uga.cs4370.mydb.RA;
import uga.cs4370.mydb.Relation;
import uga.cs4370.mydb.RelationBuilder;
import uga.cs4370.mydb.Type;

public class Driver {

    public static void main(String[] args) {
        RA ra = new RAImpl();


        
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

        /*  
        Relation instructor = new RelationBuilder()
                .attributeNames(List.of("Instructor_ID", "Name", "Department", "Salary"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        instructor.loadData("/Users/nilanpatel/Desktop/Junior Year /Junior Year S2/DataBase Mgmtn CSCI 4370/instructor_export.csv");
        System.out.println("Instructor Relation:");
        instructor.print();
        Relation department = new RelationBuilder()
                .attributeNames(List.of("Department", "Building Name", "Budget"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        department.loadData("/Users/nilanpatel/Desktop/Junior Year /Junior Year S2/DataBase Mgmtn CSCI 4370/department_export.csv");
        System.out.println("Department Relation:");
        department.print();
        Relation student = new RelationBuilder()
                .attributeNames(List.of("Student_ID", " Last Name", "Major", "Credit Hours"))
                .attributeTypes(List.of(Type.DOUBLE, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        student.loadData("/Users/nilanpatel/Desktop/Junior Year /Junior Year S2/DataBase Mgmtn CSCI 4370/student_export.csv");

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

    // Test SQL query: SELECT name, course_id, sec_id, year, building FROM (((SELECT ID, name, building FROM ((SELECT ID, name, dept_name FROM instructor) iT NATURAL JOIN (SELECT dept_name, building FROM department) dT)) _ NATURAL JOIN teaches)) NATURAL JOIN section ORDER BY ID, course_id;
    private static final String QUERY_MICHAEL = "Find the name of each instructor who teaches a section that is located in the building of the department the instructor belongs to, as well as which section that is (course and section IDs, year, and seemster) and what building it is in.";
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
        
            
        instructor.loadData("(path removed)");
        department.loadData("(path removed)");
        teaches.loadData("(path removed)");
        section.loadData("(path removed)");

        
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

}

