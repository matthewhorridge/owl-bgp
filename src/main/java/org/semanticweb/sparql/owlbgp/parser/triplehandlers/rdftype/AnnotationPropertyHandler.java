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

package org.semanticweb.sparql.owlbgp.parser.triplehandlers.rdftype;

import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.axioms.Declaration;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyVariable;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class AnnotationPropertyHandler extends TripleHandler {

    public AnnotationPropertyHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleStreaming(Identifier subject, Identifier predicate, Identifier object) {
        super.handleStreaming(subject, predicate, object, false);
        if (consumer.isVariable(subject))
            consumer.mapAnnotationPropertyIdentifierToAnnotationProperty(subject, AnnotationPropertyVariable.create(subject.toString()));
        else if (consumer.isAnonymous(subject))
            throw new IllegalArgumentException("The subject of an annotation property declaration triple cannot be a blank node, but here we have the triple: " + subject + " " + predicate + " " + object);
        else
            consumer.mapAnnotationPropertyIdentifierToAnnotationProperty(subject, AnnotationProperty.create(subject.toString()));
    }

    @Override
    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
        if (consumer.isVariable(subject))
            consumer.addAxiom(Declaration.create((AnnotationPropertyVariable) consumer.getAPE(subject), annotations));
        else
            consumer.addAxiom(Declaration.create((AnnotationProperty) consumer.getAPE(subject), annotations));
    }
}
