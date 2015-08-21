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
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;

public class PropertyExpressionListItemTranslator implements ListItemTranslator<PropertyExpression> {
    protected final TripleConsumer consumer;

    public PropertyExpressionListItemTranslator(TripleConsumer consumer) {
        this.consumer = consumer;
    }

    public PropertyExpression translate(Identifier iri) {
        PropertyExpression pe = consumer.getOPE(iri);
        if (pe == null)
            pe = consumer.getDPE(iri);
        return pe;
    }
}
