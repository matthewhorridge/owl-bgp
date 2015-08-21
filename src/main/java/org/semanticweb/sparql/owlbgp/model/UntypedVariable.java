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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;

public class UntypedVariable extends Variable implements Atomic {
    private static final long serialVersionUID = -4792039522218409937L;

    protected static InterningManager<UntypedVariable> s_interningManager = new InterningManager<UntypedVariable>() {
        protected boolean equal(UntypedVariable object1, UntypedVariable object2) {
            return object1.m_variable == object2.m_variable;
        }

        protected int getHashCode(UntypedVariable object) {
            int hashCode = 13;
            hashCode += object.m_variable.hashCode();
            return hashCode;
        }
    };

    protected UntypedVariable(String variable) {
        super(variable);
    }

    public ExtendedOWLObject getBoundVersion(Atomic binding) {
        if (binding == null) return this;
        else
            throw new IllegalArgumentException("Error: Untyped variables cannot be bound, but variable " + m_variable + " was assigned the binding " + binding);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static UntypedVariable create(String variable) {
        return s_interningManager.intern(new UntypedVariable(variable));
    }

    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(ExtendedOWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    protected OWLObject convertToOWLAPIObject(ToOWLAPIConverter converter) {
        return converter.visit(this);
    }

    public Set<Variable> getVariablesInSignature(VarType varType) {
        return new HashSet<Variable>();
    }

    public Set<Variable> getUnboundVariablesInSignature(VarType varType) {
        return new HashSet<Variable>();
    }
}
