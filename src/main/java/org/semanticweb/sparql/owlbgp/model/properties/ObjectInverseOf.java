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
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.AbstractExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
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
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;


public class ObjectInverseOf extends AbstractExtendedOWLObject implements ObjectPropertyExpression {
    private static final long serialVersionUID = 4170522309299326290L;

    protected static InterningManager<ObjectInverseOf> s_interningManager = new InterningManager<ObjectInverseOf>() {
        protected boolean equal(ObjectInverseOf object1, ObjectInverseOf object2) {
            return object1.m_ope == object2.m_ope;
        }

        protected int getHashCode(ObjectInverseOf object) {
            return -object.m_ope.hashCode();
        }
    };

    protected final ObjectPropertyExpression m_ope;

    protected ObjectInverseOf(ObjectPropertyExpression objectPropertyExpression) {
        m_ope = objectPropertyExpression;
    }

    public ObjectPropertyExpression getInvertedObjectProperty() {
        return m_ope;
    }

    public String getIRIString() {
        return null;
    }

    @Override
    public String toString(Prefixes prefixes) {
        return "ObjectInverseOf(" + m_ope.toString(prefixes) + ")";
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        if (mainNode == null) mainNode = AbstractExtendedOWLObject.getNextBlankNode();
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_INVERSE_OF.toString(prefixes));
        buffer.append(" ");
        if (m_ope instanceof Atomic) {
            buffer.append(m_ope.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
        }
        else {
            AnonymousIndividual bnode = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(bnode);
            buffer.append(" . ");
            buffer.append(LB);
            buffer.append(m_ope.toTurtleString(prefixes, bnode));
        }
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static ObjectInverseOf create(ObjectPropertyExpression objectPropertyExpression) {
        if (objectPropertyExpression instanceof Atomic)
            return s_interningManager.intern(new ObjectInverseOf(objectPropertyExpression));
        else
            throw new RuntimeException("Error: In OWL Dl one can only build inverse properties of atomic properties, but " + objectPropertyExpression + " is not aomic. ");
    }

    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(ExtendedOWLObjectVisitor visitor) {
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
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((ObjectProperty) m_ope.getBoundVersion(variablesToBindings));
    }

    public ObjectPropertyExpression getNormalized() {
        if (m_ope instanceof ObjectProperty || m_ope instanceof ObjectPropertyVariable) // r- x-
            return m_ope;
        else
            return ((ObjectInverseOf) m_ope).getInvertedObjectProperty().getNormalized();
    }
}
