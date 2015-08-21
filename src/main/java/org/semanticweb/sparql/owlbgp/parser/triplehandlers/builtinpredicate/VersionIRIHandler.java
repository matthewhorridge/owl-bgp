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
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.triplehandlers.TripleHandler;

public class VersionIRIHandler extends TripleHandler {

    public VersionIRIHandler(TripleConsumer consumer) {
        super(consumer);
    }

    @Override
    public void handleStreaming(Identifier subject, Identifier predicate, Identifier object) {
        super.handleStreaming(subject, predicate, object, true);
        if (consumer.isAnonymous(object)) {
            throw new RuntimeException("The object of a version IRI triple is anonymous/a blank node, which is not allowed: " + object);
        }
        else if (consumer.isAnonymous(subject)) {
            throw new RuntimeException("The subject of a version IRI triple is anonymous/a blank node, which is not allowed: " + subject);
        }
        else {
            Identifier currentOntologyIRI = consumer.getOntologyIRI();
            if (currentOntologyIRI == null)
                consumer.setOntologyIRI(subject);
            else if (currentOntologyIRI != subject)
                throw new RuntimeException("The subject (" + subject + ") of a version IRI triple uses an IRI that is different from the onology IRI (" + currentOntologyIRI + "), which is not allowed. ");
            consumer.addVersionIRI(subject, object);
        }
    }
}
