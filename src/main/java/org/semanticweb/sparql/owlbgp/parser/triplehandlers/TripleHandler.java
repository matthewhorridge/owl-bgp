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

package org.semanticweb.sparql.owlbgp.parser.triplehandlers;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;

public abstract class TripleHandler {

    protected final TripleConsumer consumer;

    public TripleHandler(TripleConsumer consumer) {
        this.consumer = consumer;
    }

    public void handleStreaming(Identifier subject, Identifier predicate, Identifier object, boolean consume) {
        if (!consume) consumer.addTriple(subject, predicate, object);
    }

    public void handleStreaming(Identifier subject, Identifier predicate, Identifier object) {
    }

    public void handleTriple(Identifier subject, Identifier predicate, Identifier object) {
        handleTriple(subject, predicate, object, new HashSet<Annotation>());
    }

    public void handleTriple(Identifier subject, Identifier predicate, Identifier object, Set<Annotation> annotations) {
    }
}
