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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


public class BindingIterator implements Iterator<Map<Variable, ? extends Atomic>>, Iterable<Map<Variable, ? extends Atomic>> {
    protected final Variable[] m_variables;
    protected int[] m_currentBindingIndexes;
    protected final Atomic[][] m_variablesToBindings;

    public BindingIterator(Map<Variable, Set<? extends Atomic>> variablesToBindings) {
        m_variables = variablesToBindings.keySet().toArray(new Variable[0]);
        m_variablesToBindings = new Atomic[m_variables.length][];
        for (int index = 0; index < m_variables.length; index++) {
            m_variablesToBindings[index] = variablesToBindings.get(m_variables[index]).toArray(new Atomic[0]);
        }
    }

    public boolean hasNext() {
        if (m_currentBindingIndexes == null) return true;
        for (int index = 0; index < m_variables.length; index++) {
            if (m_currentBindingIndexes[index] < m_variablesToBindings[index].length - 1) return true;
        }
        return false;
    }

    public Map<Variable, Atomic> next() {
        if (!hasNext()) throw new NoSuchElementException();
        Map<Variable, Atomic> currentBinding = new HashMap<Variable, Atomic>();
        if (m_currentBindingIndexes == null) {
            // first entry, initialize
            m_currentBindingIndexes = new int[m_variables.length];
        }
        else {
            boolean flip = false;
            for (int index = m_variables.length - 1; index >= 0; index--) {
                if (index == m_variables.length - 1) {
                    // last bit, always flip
                    if (m_currentBindingIndexes[index] < m_variablesToBindings[index].length - 1)
                        m_currentBindingIndexes[index] = m_currentBindingIndexes[index] + 1;
                    else {
                        m_currentBindingIndexes[index] = 0;
                        flip = true;
                    }
                }
                else if (flip) {
                    if (m_currentBindingIndexes[index] < m_variablesToBindings[index].length - 1) {
                        m_currentBindingIndexes[index] = m_currentBindingIndexes[index] + 1;
                        flip = false;
                    }
                    else
                        m_currentBindingIndexes[index] = 0;
                }
            }
        }
        for (int i = 0; i < m_variables.length; i++) {
            currentBinding.put(m_variables[i], m_variablesToBindings[i][m_currentBindingIndexes[i]]);
        }
        return currentBinding;
    }

    public void remove() {
        throw new UnsupportedOperationException("The binding iterator does not support removal. ");
    }

    public Iterator<Map<Variable, ? extends Atomic>> iterator() {
        return this;
    }
}