/* Copyright 2011 by the Oxford University Computing Laboratory

   This file is part of OWL-BGP.

   OWL-BGP is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OWL-BGP is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with OWL-BGP. If not, see <http://www.gnu.org/licenses/>.
 */

package  org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate;

import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectExactCardinality;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class TPExactCardinalityHandler extends AbstractCardinalityHandler {

    public TPExactCardinalityHandler(TripleConsumer consumer) {
        super(consumer, Vocabulary.OWL_CARDINALITY);
    }

    public void handleTriple(Identifier subject, Identifier predicate, Identifier object) {
        int cardinality=getCardinality(object);
        PropertyExpression pe=getPropertyExpression(subject);
        if (pe instanceof ObjectPropertyExpression) {
            ObjectPropertyExpression ope=(ObjectPropertyExpression)pe;
            consumer.mapClassIdentifierToClassExpression(subject, ObjectExactCardinality.create(cardinality,ope));
        } else if (pe instanceof DataPropertyExpression) {
            DataPropertyExpression dpe=(DataPropertyExpression)pe;
            consumer.mapClassIdentifierToClassExpression(subject, DataExactCardinality.create(cardinality,dpe));
        } else {
            // TODO: error handling
            throw new RuntimeException("error");
        }
    }
}
