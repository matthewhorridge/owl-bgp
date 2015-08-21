/* Copyright 2010-2012 by the developers of the OWL-BGP project. 

   This file is part of the OWL-BGP project.

   OWL-BGP is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OWL-BGP is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with OWL-BGP.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.semanticweb.sparql.bgpevaluation.queryobjects;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.Variable;

public interface QueryObject<T> {
    public T getAxiomTemplate();

    public boolean isComplex();

    public List<Atomic[]> computeBindings(List<Atomic[]> candidateBindings, Map<Variable, Integer> bindingPositions);

    <O> O accept(DynamicQueryObjectVisitorEx<O> visitor);

    <O> O accept(StaticQueryObjectVisitorEx<O> visitor, Set<Variable> bound);
}
