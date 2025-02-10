/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.cs4370.mydbimpl;

import java.util.List;

import uga.cs4370.mydb.RA;
import uga.cs4370.mydb.Relation;
import uga.cs4370.mydb.RelationBuilder;
import uga.cs4370.mydb.Type;

public class Driver {
    
    public static void main(String[] args) {
        // Following is an example of how to use the relation class.
        // This creates a table with three columns with below mentioned
        // column names and data types.
        // After creating the table, data is loaded from a CSV file.
        // Path should be replaced with a correct file path for a compatible
        // CSV file.
        RA ra = null;
        Relation instructor = null;
        Relation department = null;


        Relation rel1 = new RelationBuilder()
                .attributeNames(List.of("Instructor_ID", "Name", "Department", "Salary"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        rel1.loadData("/Users/nilanpatel/Desktop/Junior Year /Junior Year S2/DataBase Mgmtn CSCI 4370/instructor_export.csv");
        System.out.println("811871158");
        rel1.print();

        Relation rel2 = new RelationBuilder()
                .attributeNames(List.of("Department", "Name", "Salary"))
                .attributeTypes(List.of(Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        rel2.loadData("/Users/nilanpatel/Desktop/Junior Year /Junior Year S2/DataBase Mgmtn CSCI 4370/department_export.csv");
        System.out.println("811871158");
        rel2.print();
    }

}
