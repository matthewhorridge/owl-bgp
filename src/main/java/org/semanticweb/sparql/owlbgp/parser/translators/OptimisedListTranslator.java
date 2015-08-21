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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.parser.TripleConsumer;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class OptimisedListTranslator<O extends ExtendedOWLObject> {

    protected final TripleConsumer consumer;
    protected final ListItemTranslator<O> translator;

    public OptimisedListTranslator(TripleConsumer consumer, ListItemTranslator<O> translator) {
        this.consumer = consumer;
        this.translator = translator;
    }

    protected void translateList(Identifier mainNode, List<O> list) {
        Identifier first = consumer.getFirst(mainNode);
        if (first != null) {
            O translated = translator.translate(first);
            if (translated != null)
                list.add(translated);
        }
        if (list.isEmpty()) return;
        Identifier rest = consumer.getRest(mainNode);
        if (rest != null && rest != Vocabulary.RDF_NIL) translateList(rest, list);
    }

    public List<O> translateToList(Identifier mainNode) {
        List<O> list = new ArrayList<O>();
        translateList(mainNode, list);
        return list;
    }

    public Set<O> translateToSet(Identifier mainNode) {
        return new HashSet<O>(translateToList(mainNode));
    }
}