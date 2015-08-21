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

import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointDataProperties;
import org.semanticweb.sparql.owlbgp.model.axioms.DisjointObjectProperties;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPPropertyDisjointWithHandler extends TripleHandler {

    public TPPropertyDisjointWithHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        ObjectPropertyExpression property1 = consumer.getOPE(subject);
        DataPropertyExpression dataProperty1 = null;
        if (property1 == null)
            dataProperty1 = consumer.getDPE(subject);
        if (property1 == null && dataProperty1 == null)
            throw new RuntimeException("Could not find a property expression for the subject in the triple " + subject + " " + predicate + " " + object + ". ");

        ObjectPropertyExpression property2 = consumer.getOPE(object);
        DataPropertyExpression dataProperty2 = null;
        if (property2 == null)
            dataProperty2 = consumer.getDPE(object);
        if (property1 == null && dataProperty1 == null)
            throw new RuntimeException("Could not find a property expression for the object in the triple " + subject + " " + predicate + " " + object + ". ");

        if (property1 != null && property2 != null)
            consumer.addAxiom(DisjointObjectProperties.create(property1, property2, annotations));
        else if (dataProperty1 != null && dataProperty2 != null)
            consumer.addAxiom(DisjointDataProperties.create(dataProperty1, dataProperty2, annotations));
        else if (property1 != null)
            throw new RuntimeException("Error: The subject of the triple " + subject + " " + predicate + " " + object + "  was tranlated into an object property, but the object into a data property, which is not allowed in OWL DL. ");
        else
            throw new RuntimeException("Error: The object of the triple " + subject + " " + predicate + " " + object + "  was tranlated into an object property, but the subject into a data property, which is not allowed in OWL DL. ");
    }
}
