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

package org.semanticweb.sparql.owlbgp.parser.translators;

import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction;
import org.semanticweb.sparql.owlbgp.model.dataranges.FacetRestriction.OWL2_FACET;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;

public class FacetRestrictionListItemTranslator implements ListItemTranslator<FacetRestriction> {

    protected final TripleConsumer consumer;

    public FacetRestrictionListItemTranslator(TripleConsumer consumer) {
        this.consumer = consumer;
    }

    public FacetRestriction translate(Identifier firstObject) {
        for (OWL2_FACET facet : OWL2_FACET.values()) {
            Literal lit = consumer.getLiteralObject(firstObject, facet.getIRI());
            if (lit != null) {
                if (lit instanceof TypedLiteral)
                    return FacetRestriction.create(facet, (TypedLiteral) lit);
                else
                    // TODO: error handling
                    System.err.println("error");
            }
        }
        return null;
    }
}
