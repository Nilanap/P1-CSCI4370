package uga.cs4370.mydbimpl;

import java.util.ArrayList;
import java.util.List;

import uga.cs4370.mydb.Cell;
import uga.cs4370.mydb.Predicate;
import uga.cs4370.mydb.RA;
import uga.cs4370.mydb.Relation;
import uga.cs4370.mydb.RelationBuilder;
import uga.cs4370.mydb.Type;


// Main Branch, all contribs by group up to date

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
        // Schemas have to match
        if (!rel1.getAttrs().equals(rel2.getAttrs())) {
            throw new IllegalArgumentException("Relations must have the same attributes for union.");
        }
        if (!rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations must have the same types for union.");
        }

        // Create a new relation with the same schema as rel1 and rel2.
        Relation result = new RelationBuilder()
                .attributeNames(rel1.getAttrs())
                .attributeTypes(rel1.getTypes())
                .build();

        // Include all rows from rel1
        for (int i = 0; i < rel1.getSize(); i++) {
            result.insert(rel1.getRow(i));
        }

        // Include rows from rel2, avoiding duplicates
        for (int i = 0; i < rel2.getSize(); i++) {
            List<Cell> row = rel2.getRow(i);

            boolean duplicate = false;
            for (int j = 0; j < result.getSize(); j++) {
                if (result.getRow(j).equals(row)) { // changeed ur code here to use j vs i bc logic error - nilan
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                result.insert(row);
            }
        }

        return result;
    }




    //Bryce method
    @Override
    public Relation diff(Relation rel1, Relation rel2) {
        // Check if rel1 and rel2 have the same schema
        if (!rel1.getAttrs().equals(rel2.getAttrs())) {
            throw new IllegalArgumentException("Relations must have the same attributes for set difference.");
        }
        if (!rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relations must have the same types for set difference.");
        }

        // Create a new relation with the same schema as rel1 and rel2
        Relation result = new RelationBuilder()
                .attributeNames(rel1.getAttrs())
                .attributeTypes(rel1.getTypes())
                .build();

        // Iterate through each row in rel1
        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row = rel1.getRow(i);

            // Check if the row is not in rel2
            boolean found = false;
            for (int j = 0; j < rel2.getSize(); j++) {
                if (row.equals(rel2.getRow(j))) {
                    found = true;
                    break;
                }
            }

            // If the row is not in rel2, add it to the result
            if (!found) {
                result.insert(row);
            }
        }

        return result;
    }


    //Bryce method
    @Override
    public Relation rename(Relation rel, List<String> origAttr, List<String> renamedAttr) {
        // ensure they r same size
        if (origAttr.size() != renamedAttr.size()) {
            throw new IllegalArgumentException("Original and renamed attribute lists must have the same size.");
        }

        // validate they all exist in orig attr
        for (String attr : origAttr) {
            if (!rel.hasAttr(attr)) {
                throw new IllegalArgumentException("Attribute " + attr + " does not exist in the relation.");
            }
        }

        // build new list w renamed attrs
        List<String> newAttrs = new ArrayList<>(rel.getAttrs());
        for (int i = 0; i < origAttr.size(); i++) {
            int index = newAttrs.indexOf(origAttr.get(i)); // locte index of org attr
            newAttrs.set(index, renamedAttr.get(i)); // replace w renamed one
        }

        // create new relation
        Relation result = new RelationBuilder()
            .attributeNames(newAttrs)
            .attributeTypes(rel.getTypes())
            .build();

       
        for (int i = 0; i < rel.getSize(); i++) {
            result.insert(rel.getRow(i));
        }

        return result;
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
        List<String> rel1Attrs = rel1.getAttrs();
        // The indicies of the duplicated attrs in the upcoming join
        List<Integer> rel1DuplicatedAttrIndicies = new ArrayList<>();
        List<Integer> rel2DuplicatedAttrIndicies = new ArrayList<>();

        List<String> rel2AttrsToRenameOldName = new ArrayList<>();
        List<String> rel2AttrsToRenameNewName = new ArrayList<>();

        // Find all attributes in rel2 that exist in rel1,
        // then mark them so we can both compare them in the join and delete them later
        for (int i = 0; i < rel1Attrs.size(); i++) {
            String rel1Attr = rel1Attrs.get(i);
            if (rel2.hasAttr(rel1Attr)) {
                rel1DuplicatedAttrIndicies.add(i);
                rel2DuplicatedAttrIndicies.add(rel1Attrs.size() + rel2.getAttrIndex(rel1Attr));
                rel2AttrsToRenameOldName.add(rel1Attr);
                rel2AttrsToRenameNewName.add("rel2." + rel1Attr);
            }
        }

        // Rename the duplicate columns
        Relation renamedRel2 = rename(rel2, rel2AttrsToRenameOldName, rel2AttrsToRenameNewName);

        // The predicate that handles the natural join comparisons.
        Predicate naturalJoinPredicate = (row) -> {
            for (int i = 0; i < rel1DuplicatedAttrIndicies.size(); i++) {
                int rel1DuplicatedAttrIndex = rel1DuplicatedAttrIndicies.get(i);
                int rel2DuplicatedAttrIndex = rel2DuplicatedAttrIndicies.get(i);
                if (!row.get(rel1DuplicatedAttrIndex).equals(row.get(rel2DuplicatedAttrIndex))) {
                    return false;
                }
            }
            return true;
        };

        // Perform a theta join with the natural join predicate...
        Relation thetaJoin = join(rel1, renamedRel2, naturalJoinPredicate);
        // ... and then remove the duplicate columns.
        Relation result = project(thetaJoin, thetaJoin.getAttrs()
                .stream()
                .filter(attr -> !attr.startsWith("rel2."))
                .toList()
        );
        return result;
    }

    

    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        // IllegalArgumentException thrown by cartesianProduct allowed
        Relation cartesianProduct = cartesianProduct(rel1, rel2);
        return select(cartesianProduct, p);
    }
    
}