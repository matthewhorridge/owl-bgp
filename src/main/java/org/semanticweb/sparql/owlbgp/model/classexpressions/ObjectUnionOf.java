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

import java.util.Arrays;
import java.util.Collections;
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
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class ObjectUnionOf extends AbstractExtendedOWLObject implements ClassExpression {
    private static final long serialVersionUID = 6211740113622574460L;

    protected static InterningManager<ObjectUnionOf> s_interningManager = new InterningManager<ObjectUnionOf>() {
        protected boolean equal(ObjectUnionOf intersection1, ObjectUnionOf intersection2) {
            if (intersection1.m_classExpressions.size() != intersection2.m_classExpressions.size())
                return false;
            for (ClassExpression conjunct : intersection1.m_classExpressions) {
                if (!contains(conjunct, intersection2.m_classExpressions))
                    return false;
            }
            return true;
        }

        protected boolean contains(ClassExpression classExpression, Set<ClassExpression> classExpressions) {
            for (ClassExpression conjunct : classExpressions)
                if (conjunct == classExpression)
                    return true;
            return false;
        }

        protected int getHashCode(ObjectUnionOf intersection) {
            int hashCode = 0;
            for (ClassExpression conjunct : intersection.m_classExpressions)
                hashCode += conjunct.hashCode();
            return hashCode;
        }
    };

    protected final Set<ClassExpression> m_classExpressions;

    protected ObjectUnionOf(Set<ClassExpression> classExpressions) {
        m_classExpressions = Collections.unmodifiableSet(classExpressions);
    }

    public Set<ClassExpression> getClassExpressions() {
        return m_classExpressions;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ObjectUnionOf(");
        boolean notFirst = false;
        for (ClassExpression conjunct : m_classExpressions) {
            if (notFirst)
                buffer.append(' ');
            else
                notFirst = true;
            buffer.append(conjunct.toString(prefixes));
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
        buffer.append(Vocabulary.OWL_CLASS.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_UNION_OF.toString(prefixes));
        Identifier[] listNodes = new Identifier[m_classExpressions.size()];
        ClassExpression[] classExpressions = m_classExpressions.toArray(new ClassExpression[0]);
        for (int i = 0; i < classExpressions.length; i++) {
            if (classExpressions[i] instanceof Atomic)
                listNodes[i] = ((Atomic) classExpressions[i]).getIdentifier();
            else
                listNodes[i] = AbstractExtendedOWLObject.getNextBlankNode();
        }
        printSequence(buffer, prefixes, null, listNodes);
        for (int i = 0; i < classExpressions.length; i++) {
            if (!(classExpressions[i] instanceof Atomic)) {
                buffer.append(classExpressions[i].toTurtleString(prefixes, listNodes[i]));
            }
        }
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static ObjectUnionOf create(ClassExpression... classExpressions) {
        return create(new HashSet<ClassExpression>(Arrays.asList(classExpressions)));
    }

    public static ObjectUnionOf create(Set<ClassExpression> classExpressions) {
        return s_interningManager.intern(new ObjectUnionOf(classExpressions));
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
        for (ClassExpression classExpression : m_classExpressions) {
            variables.addAll(classExpression.getVariablesInSignature(varType));
        }
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<ClassExpression> classExpressions = new HashSet<ClassExpression>();
        for (ClassExpression ce : m_classExpressions) {
            classExpressions.add((ClassExpression) ce.getBoundVersion(variablesToBindings));
        }
        return create(classExpressions);
    }
}