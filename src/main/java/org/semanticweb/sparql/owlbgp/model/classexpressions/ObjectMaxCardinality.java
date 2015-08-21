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
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;


public class ObjectMaxCardinality extends AbstractExtendedOWLObject implements ClassExpression {
    private static final long serialVersionUID = -5215709385051212705L;

    protected static InterningManager<ObjectMaxCardinality> s_interningManager = new InterningManager<ObjectMaxCardinality>() {
        protected boolean equal(ObjectMaxCardinality object1, ObjectMaxCardinality object2) {
            return object1.m_cardinality == object2.m_cardinality && object1.m_ope == object2.m_ope && object1.m_classExpression == object2.m_classExpression;
        }

        protected int getHashCode(ObjectMaxCardinality object) {
            return 4777 + object.m_cardinality * 11 + object.m_ope.hashCode() * 23 + (object.m_classExpression != null ? object.m_classExpression.hashCode() * 57 : 1713);
        }
    };

    protected final int m_cardinality;
    protected final ObjectPropertyExpression m_ope;
    protected final ClassExpression m_classExpression;

    protected ObjectMaxCardinality(int cardinality, ObjectPropertyExpression ope, ClassExpression classExpression) {
        m_cardinality = cardinality;
        m_ope = ope;
        m_classExpression = classExpression;
    }

    public int getCardinality() {
        return m_cardinality;
    }

    public ObjectPropertyExpression getObjectPropertyExpression() {
        return m_ope;
    }

    public ClassExpression getClassExpression() {
        return m_classExpression;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ObjectMaxCardinality(");
        buffer.append(m_cardinality);
        buffer.append(" ");
        buffer.append(m_ope.toString(prefixes));
        if (m_classExpression != null) {
            buffer.append(" ");
            buffer.append(m_classExpression.toString(prefixes));
        }
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
        if (m_classExpression == null)
            buffer.append(Vocabulary.OWL_MAX_CARDINALITY.toString(prefixes));
        else
            buffer.append(Vocabulary.OWL_MAX_QUALIFIED_CARDINALITY.toString(prefixes));
        buffer.append(" ");
        buffer.append("\"");
        buffer.append(m_cardinality);
        buffer.append("\"^^");
        buffer.append(Datatype.XSD_NON_NEGATIVE_INTEGER.toString(prefixes));
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
        if (m_classExpression != null) {
            buffer.append(mainNode);
            buffer.append(" ");
            buffer.append(Vocabulary.OWL_ON_CLASS.toString(prefixes));
            buffer.append(" ");
            if (m_classExpression instanceof Atomic) {
                buffer.append(m_classExpression.toString(prefixes));
                buffer.append(" . ");
                buffer.append(LB);
            }
            else {
                AnonymousIndividual drbnode = AbstractExtendedOWLObject.getNextBlankNode();
                buffer.append(drbnode);
                buffer.append(" . ");
                buffer.append(LB);
                buffer.append(m_classExpression.toTurtleString(prefixes, drbnode));
            }
        }
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static ObjectMaxCardinality create(int cardinality, ObjectPropertyExpression ope) {
        return create(cardinality, ope, null);
    }

    public static ObjectMaxCardinality create(int cardinality, ObjectPropertyExpression ope, ClassExpression classExpression) {
        return s_interningManager.intern(new ObjectMaxCardinality(cardinality, ope, classExpression));
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
        if (m_classExpression != null) variables.addAll(m_classExpression.getVariablesInSignature(varType));
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        if (m_classExpression == null)
            return create(m_cardinality, (ObjectPropertyExpression) m_ope.getBoundVersion(variablesToBindings));
        else
            return create(m_cardinality, (ObjectPropertyExpression) m_ope.getBoundVersion(variablesToBindings), (ClassExpression) m_classExpression.getBoundVersion(variablesToBindings));
    }
}
