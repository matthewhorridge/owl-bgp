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

public class DataPropertyVariable extends Variable implements DataPropertyExpression {
    private static final long serialVersionUID = 3644196667162641608L;

    protected static InterningManager<DataPropertyVariable> s_interningManager = new InterningManager<DataPropertyVariable>() {
        protected boolean equal(DataPropertyVariable object1, DataPropertyVariable object2) {
            return object1.m_variable == object2.m_variable;
        }

        protected int getHashCode(DataPropertyVariable object) {
            int hashCode = 43;
            hashCode += object.m_variable.hashCode();
            return hashCode;
        }
    };

    protected DataPropertyVariable(String variable) {
        super(variable);
    }

    public ExtendedOWLObject getBoundVersion(Atomic binding) {
        if (binding instanceof DataProperty) return binding;
        else if (binding == null) return this;
        else
            throw new IllegalArgumentException("Error: Only data properties can be assigned to data property variables, but data proiperty variable " + m_variable + " was assigned the non-data property " + binding);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static DataPropertyVariable create(String variable) {
        return s_interningManager.intern(new DataPropertyVariable(variable));
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
        if (varType == null || varType == VarType.DATA_PROPERTY) variables.add(this);
        return variables;
    }
}
