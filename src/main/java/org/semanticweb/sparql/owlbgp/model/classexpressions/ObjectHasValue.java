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
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;


public class ObjectHasValue extends AbstractExtendedOWLObject implements ClassExpression {
    private static final long serialVersionUID = -4900504105887302464L;

    protected static InterningManager<ObjectHasValue> s_interningManager = new InterningManager<ObjectHasValue>() {
        protected boolean equal(ObjectHasValue object1, ObjectHasValue object2) {
            return object1.m_ope == object2.m_ope && object1.m_individual == object2.m_individual;
        }

        protected int getHashCode(ObjectHasValue object) {
            return 7 * object.m_ope.hashCode() + 23 * object.m_individual.hashCode();
        }
    };

    protected final ObjectPropertyExpression m_ope;
    protected final Individual m_individual;

    protected ObjectHasValue(ObjectPropertyExpression ope, Individual individual) {
        m_ope = ope;
        m_individual = individual;
    }

    public ObjectPropertyExpression getObjectPropertyExpression() {
        return m_ope;
    }

    public Individual getIndividual() {
        return m_individual;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ObjectHasValue(");
        buffer.append(m_ope.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_individual.toString(prefixes));
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
        if (m_ope instanceof Atomic) {
            buffer.append(m_ope.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
        }
        else {
            AnonymousIndividual opebnode = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(opebnode);
            buffer.append(" . ");
            buffer.append(LB);
            buffer.append(m_ope.toTurtleString(prefixes, opebnode));
        }
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_HAS_VALUE.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_individual.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static ObjectHasValue create(ObjectPropertyExpression ope, Individual individual) {
        return s_interningManager.intern(new ObjectHasValue(ope, individual));
    }

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

    protected OWLObject convertToOWLAPIObject(ToOWLAPIConverter converter) {
        return converter.visit(this);
    }

    public Set<Variable> getVariablesInSignature(VarType varType) {
        Set<Variable> variables = new HashSet<Variable>();
        variables.addAll(m_ope.getVariablesInSignature(varType));
        variables.addAll(m_individual.getVariablesInSignature(varType));
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((ObjectPropertyExpression) m_ope.getBoundVersion(variablesToBindings), (Individual) m_individual.getBoundVersion(variablesToBindings));
    }
}
