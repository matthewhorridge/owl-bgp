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
import org.semanticweb.sparql.owlbgp.model.axioms.SubAnnotationPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubDataPropertyOf;
import org.semanticweb.sparql.owlbgp.model.axioms.SubObjectPropertyOf;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class TPSubPropertyOfHandler extends TripleHandler {

    public TPSubPropertyOfHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        ObjectPropertyExpression subObjProperty = consumer.getOPE(subject);
        DataPropertyExpression subDataProperty = null;
        AnnotationPropertyExpression subAnnotationProperty = null;
        if (subObjProperty == null)
            subDataProperty = consumer.getDPE(subject);
        if (subObjProperty == null && subDataProperty == null)
            subAnnotationProperty = consumer.getAPE(subject);
        if (subObjProperty == null && subDataProperty == null && subAnnotationProperty == null)
            throw new RuntimeException("Could not find a property expression for the subject in the triple " + subject + " " + predicate + " " + object + ". ");

        ObjectPropertyExpression superObjProperty = consumer.getOPE(object);
        DataPropertyExpression superDataProperty = null;
        AnnotationPropertyExpression superAnnotationProperty = null;
        if (superObjProperty == null)
            superDataProperty = consumer.getDPE(object);
        if (superObjProperty == null && superDataProperty == null)
            superAnnotationProperty = consumer.getAPE(object);
        if (superObjProperty == null && superDataProperty == null && superAnnotationProperty == null)
            throw new RuntimeException("Could not find a property expression for the object in the triple " + subject + " " + predicate + " " + object + ". ");

        if (subObjProperty != null && superObjProperty != null)
            consumer.addAxiom(SubObjectPropertyOf.create(subObjProperty, superObjProperty, annotations));
        else if (subDataProperty != null && superDataProperty != null)
            consumer.addAxiom(SubDataPropertyOf.create(subDataProperty, superDataProperty, annotations));
        else if (subAnnotationProperty != null && superAnnotationProperty != null)
            consumer.addAxiom(SubAnnotationPropertyOf.create(subAnnotationProperty, superAnnotationProperty, annotations));
        else
            throw new RuntimeException("Error: The subject and object of the triple " + subject + " " + predicate + " " + object + "  were tranlated into different kinds of properties (e.g., one object and one data property). ");
    }
}
