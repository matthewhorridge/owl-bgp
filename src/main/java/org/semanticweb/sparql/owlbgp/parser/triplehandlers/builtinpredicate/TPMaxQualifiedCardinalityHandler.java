/* Copyright 2010-2012 by the developers of the OWL-BGP project. 

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

package org.semanticweb.sparql.owlbgp.parser.triplehandlers.builtinpredicate;

import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class TPMaxQualifiedCardinalityHandler extends AbstractCardinalityHandler {

    public TPMaxQualifiedCardinalityHandler(TripleConsumer consumer) {
        super(consumer, Vocabulary.OWL_MAX_CARDINALITY);
    }

    public void handleTriple(Identifier subject, Identifier predicate, Identifier object) {
        int cardinality = getCardinality(object);
        PropertyExpression pe = getPropertyExpression(subject);
        if (pe instanceof ObjectPropertyExpression) {
            ObjectPropertyExpression ope = (ObjectPropertyExpression) pe;
            ClassExpression ce = getClassExpression(subject);
            consumer.mapClassIdentifierToClassExpression(subject, ObjectMaxCardinality.create(cardinality, ope, ce));
        }
        else if (pe instanceof DataPropertyExpression) {
            DataPropertyExpression dpe = (DataPropertyExpression) pe;
            DataRange dr = getDataRange(subject);
            consumer.mapClassIdentifierToClassExpression(subject, DataMaxCardinality.create(cardinality, dpe, dr));
        }
        else {
            // TODO: error handling
            throw new RuntimeException("error");
        }
    }
}
