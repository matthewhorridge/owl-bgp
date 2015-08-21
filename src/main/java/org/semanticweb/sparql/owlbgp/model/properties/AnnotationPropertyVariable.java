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


package org.semanticweb.sparql.owlbgp.model.properties;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitor;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;

public class AnnotationPropertyVariable extends Variable implements AnnotationPropertyExpression, Atomic {
    private static final long serialVersionUID = -971279445618480330L;

    protected static InterningManager<AnnotationPropertyVariable> s_interningManager = new InterningManager<AnnotationPropertyVariable>() {
        protected boolean equal(AnnotationPropertyVariable object1, AnnotationPropertyVariable object2) {
            return object1.m_variable == object2.m_variable;
        }

        protected int getHashCode(AnnotationPropertyVariable object) {
            int hashCode = 71;
            hashCode += object.m_variable.hashCode();
            return hashCode;
        }
    };

    protected AnnotationPropertyVariable(String variable) {
        super(variable);
    }

    public ExtendedOWLObject getBoundVersion(Atomic binding) {
        if (binding == null) return this;
        else if (binding instanceof AnnotationProperty) return binding;
        else
            throw new IllegalArgumentException("Error: Only annotation properties can be assigned to annotation property variables, but annotation property variable " + m_variable + " was assigned the non-annotation property " + binding);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static AnnotationPropertyVariable create(String variable) {
        return s_interningManager.intern(new AnnotationPropertyVariable(variable));
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
        Set<Variable> variables = new HashSet<Variable>();
        if (varType == null || varType == VarType.ANNOTATION_PROPERTY) variables.add(this);
        return variables;
    }
}
