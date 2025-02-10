package uga.cs4370.mydbimpl;

import java.util.List;

import uga.cs4370.mydb.Predicate;
import uga.cs4370.mydb.RA;
import uga.cs4370.mydb.Relation;

// Testing out Nilan Branch

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
