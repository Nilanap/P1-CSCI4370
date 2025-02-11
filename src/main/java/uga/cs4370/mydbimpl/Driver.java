package uga.cs4370.mydbimpl;

import java.util.List;

import uga.cs4370.mydb.Predicate;
import uga.cs4370.mydb.RA;
import uga.cs4370.mydb.Relation;
import uga.cs4370.mydb.RelationBuilder;
import uga.cs4370.mydb.Type;

public class Driver {

    public static void main(String[] args) {
        RA ra = new RAImpl();

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


    }
}
