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


package org.semanticweb.sparql.owlbgp.model.classexpressions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.AbstractExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.ClassAndPropertyExpressionVisitor;
import org.semanticweb.sparql.owlbgp.model.ClassAndPropertyExpressionVisitorEx;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitor;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;


public class DataHasValue extends AbstractExtendedOWLObject implements ClassExpression {
    private static final long serialVersionUID = 584771936735129139L;

    protected static InterningManager<DataHasValue> s_interningManager = new InterningManager<DataHasValue>() {
        protected boolean equal(DataHasValue object1, DataHasValue object2) {
            return object1.m_dpe == object2.m_dpe && object1.m_literal == object2.m_literal;
        }

        protected int getHashCode(DataHasValue object) {
            return 11 * object.m_dpe.hashCode() + 17 * object.m_literal.hashCode();
        }
    };

    protected final DataPropertyExpression m_dpe;
    protected final Literal m_literal;

    protected DataHasValue(DataPropertyExpression dpe, Literal literal) {
        m_dpe = dpe;
        m_literal = literal;
    }

    public DataPropertyExpression getDataPropertyExpression() {
        return m_dpe;
    }

    public Literal getLiteral() {
        return m_literal;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DataHasValue(");
        buffer.append(m_dpe.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_literal.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        if (mainNode == null) mainNode = AbstractExtendedOWLObject.getNextBlankNode();
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(Vocabulary.RDF_TYPE.toString(prefixes));
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_RESTRICTION.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_ON_PROPERTY.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_dpe.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_HAS_VALUE.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_literal.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static DataHasValue create(DataPropertyExpression dpe, Literal literal) {
        return s_interningManager.intern(new DataHasValue(dpe, literal));
    }

    @Override
    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(ExtendedOWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    public void accept(ClassAndPropertyExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public <O> O accept(ClassAndPropertyExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    protected OWLObject convertToOWLAPIObject(ToOWLAPIConverter converter) {
        return converter.visit(this);
    }

    @Override
    public Set<Variable> getVariablesInSignature(VarType varType) {
        Set<Variable> variables = new HashSet<Variable>();
        variables.addAll(m_dpe.getVariablesInSignature(varType));
        variables.addAll(m_literal.getVariablesInSignature(varType));
        return variables;
    }

    @Override
    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((DataPropertyExpression) m_dpe.getBoundVersion(variablesToBindings), (Literal) m_literal.getBoundVersion(variablesToBindings));
    }
}
