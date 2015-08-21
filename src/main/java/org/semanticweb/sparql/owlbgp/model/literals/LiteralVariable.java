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


package org.semanticweb.sparql.owlbgp.model.literals;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitor;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.UntypedVariable;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;


public class LiteralVariable extends Variable implements Literal {
    private static final long serialVersionUID = -2509521674677767182L;

    protected static InterningManager<LiteralVariable> s_interningManager = new InterningManager<LiteralVariable>() {
        protected boolean equal(LiteralVariable object1, LiteralVariable object2) {
            return object1.m_variable == object2.m_variable;
        }

        protected int getHashCode(LiteralVariable object) {
            int hashCode = 23;
            hashCode += object.m_variable.hashCode();
            return hashCode;
        }
    };

    protected LiteralVariable(String variable) {
        super(variable);
    }

    public ExtendedOWLObject getBoundVersion(Atomic binding) {
        if (binding instanceof Literal) return binding;
        else if (binding == null) return this;
        else
            throw new IllegalArgumentException("Error: Only literals can be assigned to literal variables, but literal variable " + m_variable + " was assigned the non-literal " + binding);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static LiteralVariable create(UntypedVariable variable) {
        return create(variable.getVariable());
    }

    public static LiteralVariable create(String variable) {
        return s_interningManager.intern(new LiteralVariable(variable));
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
        if (varType == null || varType == VarType.INDIVIDUAL) variables.add(this);
        return variables;
    }

    public Datatype getDatatype() {
        return null;
    }

    public String getLangTag() {
        return null;
    }

    public String getLexicalForm() {
        return null;
    }
}
