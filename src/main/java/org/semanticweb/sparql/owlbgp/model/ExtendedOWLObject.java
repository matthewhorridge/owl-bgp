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


package org.semanticweb.sparql.owlbgp.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;

public interface ExtendedOWLObject extends Serializable {
    String toString(Prefixes prefixes);

    String toTurtleString();

    String toTurtleString(Identifier mainNode);

    String toTurtleString(Prefixes prefixes, Identifier mainNode);

    ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings);

    OWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings, OWLDataFactory dataFactory);

    Iterable<ExtendedOWLObject> getAppliedBindingsIterator(Map<Variable, Set<? extends Atomic>> variablesToBindings);

    Iterable<OWLObject> getAppliedBindingsOWLAPIIterator(Map<Variable, Set<? extends Atomic>> variablesToBindings, OWLDataFactory dataFactory);

    Set<Variable> getVariablesInSignature();

    Set<Variable> getVariablesInSignature(VarType varType);

    <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor);

    void accept(ExtendedOWLObjectVisitor visitor);

    OWLObject asOWLAPIObject(OWLDataFactory dataFactory);

    OWLObject asOWLAPIObject(ToOWLAPIConverter converter);

    boolean isVariable();
}
