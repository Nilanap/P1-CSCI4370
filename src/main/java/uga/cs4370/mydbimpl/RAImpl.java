package uga.cs4370.mydbimpl;

import java.util.ArrayList;
import java.util.List;

import uga.cs4370.mydb.Predicate;
import uga.cs4370.mydb.RA;
import uga.cs4370.mydb.Relation;

public class RAImpl implements RA {

    @Override
    public Relation select(Relation rel, Predicate p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Relation project(Relation rel, List<String> attrs) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
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
