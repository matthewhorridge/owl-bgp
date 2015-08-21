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


public class ObjectHasSelf extends AbstractExtendedOWLObject implements ClassExpression {
    private static final long serialVersionUID = -5958845591224826209L;

    protected static InterningManager<ObjectHasSelf> s_interningManager = new InterningManager<ObjectHasSelf>() {
        protected boolean equal(ObjectHasSelf object1, ObjectHasSelf object2) {
            return object1.m_ope == object2.m_ope;
        }

        protected int getHashCode(ObjectHasSelf object) {
            return 17 * object.m_ope.hashCode();
        }
    };

    protected final ObjectPropertyExpression m_ope;

    protected ObjectHasSelf(ObjectPropertyExpression ope) {
        m_ope = ope;
    }

    public ObjectPropertyExpression getObjectPropertyExpression() {
        return m_ope;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ObjectHasSelf(");
        buffer.append(m_ope.toString(prefixes));
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
        buffer.append(Vocabulary.OWL_HAS_SELF.toString(prefixes));
        buffer.append(" \"true\"^^");
        buffer.append(Datatype.XSD_BOOLEAN.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static ObjectHasSelf create(ObjectPropertyExpression ope) {
        return s_interningManager.intern(new ObjectHasSelf(ope));
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
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((ObjectPropertyExpression) m_ope.getBoundVersion(variablesToBindings));
    }
}
