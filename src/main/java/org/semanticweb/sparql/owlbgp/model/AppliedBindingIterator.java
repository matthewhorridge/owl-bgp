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

package org.semanticweb.sparql.owlbgp.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class AppliedBindingIterator implements Iterator<ExtendedOWLObject>, Iterable<ExtendedOWLObject> {
    protected final ExtendedOWLObject m_extendedOwlObject;
    protected final BindingIterator m_bindingIterator;

    public AppliedBindingIterator(ExtendedOWLObject extendedOWLObject, Map<Variable, Set<? extends Atomic>> variablesToBindings) {
        m_extendedOwlObject = extendedOWLObject;
        m_bindingIterator = new BindingIterator(variablesToBindings);
    }

    public boolean hasNext() {
        return m_bindingIterator.hasNext();
    }

    public ExtendedOWLObject next() {
        return m_extendedOwlObject.getBoundVersion(m_bindingIterator.next());
    }

    public void remove() {
        throw new UnsupportedOperationException("The binding iterator does not support removal. ");
    }

    public Iterator<ExtendedOWLObject> iterator() {
        return this;
    }
}