package org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate;

import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPDataIntersectionOfHandler extends TripleHandler {

    public TPDataIntersectionOfHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object) {
        Set<DataRange> dataRangeSet=consumer.translateToDataRangeSet(object);
        if (dataRangeSet!=null&&dataRangeSet.size()>0) {
            if (dataRangeSet.size()>1)
                consumer.mapDataRangeIdentifierToDataRange(subject, DataIntersectionOf.create(dataRangeSet));
            else 
                consumer.mapDataRangeIdentifierToDataRange(subject, dataRangeSet.iterator().next());
        } else {
            // TODO: error handling
            throw new RuntimeException("error");
        }
    }
}