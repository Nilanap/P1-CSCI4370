package uga.cs4370.mydbimpl;

import java.util.ArrayList;
import java.util.List;

import uga.cs4370.mydb.Cell;
import uga.cs4370.mydb.Predicate;
import uga.cs4370.mydb.RA;
import uga.cs4370.mydb.Relation;
import uga.cs4370.mydb.RelationBuilder;
import uga.cs4370.mydb.Type;


// Testing out Nilan Branch

public class RAImpl implements RA {

    @Override
    public Relation select(Relation rel, Predicate p) {
        Relation result = new RelationBuilder()
            .attributeNames(rel.getAttrs())
            .attributeTypes(rel.getTypes())
            .build();

        // iterating each row
        for (int i = 0; i < rel.getSize(); i++) {
            List<Cell> row = rel.getRow(i);
            // make sure thr row satisfies the predicate
            if (p.check(row)) {
                // if it does add to the result relation builder thing
                result.insert(row);
            }
        }

        return result;
    }

    @Override
    public Relation project(Relation rel, List<String> attrs) {
        // make sure all attrs exist in the relation we specify
        for (String attr : attrs) {
            if (!rel.hasAttr(attr)) {
                throw new IllegalArgumentException("Attribute " + attr + " does not exist in the relation.");
            }
        }

        // creates new list for thr attrs
        List<Type> projectedTypes = new ArrayList<>();
        for (String attr : attrs) {
            int index = rel.getAttrIndex(attr); 
            projectedTypes.add(rel.getTypes().get(index)); 
        }

        // create new project relation thing 
        Relation result = new RelationBuilder()
            .attributeNames(attrs)
            .attributeTypes(projectedTypes)
            .build();

        
        for (int i = 0; i < rel.getSize(); i++) {
            List<Cell> row = rel.getRow(i);
            List<Cell> projectedRow = new ArrayList<>();

            // extract the cell columns from thr rows above wiht the stuff we want
            for (String attr : attrs) {
                int index = rel.getAttrIndex(attr); 
                projectedRow.add(row.get(index)); 
            }

            
            result.insert(projectedRow);
        }

        return result;
    }


    

    @Override
    public Relation union(Relation rel1, Relation rel2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Relation diff(Relation rel1, Relation rel2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Relation rename(Relation rel, List<String> origAttr, List<String> renamedAttr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Relation cartesianProduct(Relation rel1, Relation rel2) {
        // throw error if they have common attributes
        for (String attr : rel1.getAttrs()) {
            if (rel2.hasAttr(attr)) {
                throw new IllegalArgumentException("Relations have common attributes: " + attr);
            }
        }

        // i build list of the ones combined
        List<String> newAttrs = new ArrayList<>(rel1.getAttrs());
        newAttrs.addAll(rel2.getAttrs());

        // new list combining table 1 and 2
        List<Type> newTypes = new ArrayList<>(rel1.getTypes());
        newTypes.addAll(rel2.getTypes());

        // builds the relation
        Relation result = new RelationBuilder()
            .attributeNames(newAttrs)
            .attributeTypes(newTypes)
            .build();

        // loop thru to perform cart product 
        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row1 = rel1.getRow(i);
            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> row2 = rel2.getRow(j);
                List<Cell> newRow = new ArrayList<>(row1);
                newRow.addAll(row2);
                result.insert(newRow);
            }
        }

        return result;
    }

    @Override
    public Relation join(Relation rel1, Relation rel2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
