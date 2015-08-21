/* Copyright 2010-2012 by the developers of the OWL-BGP project. 

   This file is part of OWL-BGP.

   OWL-BGP is free software: you can redistribute it and/or modify
   it under the terms of the GNU Lesser General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OWL-BGP is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License
   along with OWL-BGP. If not, see <http://www.gnu.org/licenses/>.
 */

package org.semanticweb.sparql.owlbgp.model.axioms;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.AbstractExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.AxiomVisitor;
import org.semanticweb.sparql.owlbgp.model.AxiomVisitorEx;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitor;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyExpression;
import org.semanticweb.sparql.owlbgp.model.properties.PropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class HasKey extends AbstractAxiom {
    private static final long serialVersionUID = -8985552871990493146L;

    protected static InterningManager<HasKey> s_interningManager = new InterningManager<HasKey>() {
        protected boolean equal(HasKey object1, HasKey object2) {
            if (object1.m_classExpression != object2.m_classExpression
                    || object1.m_propertyExpressions.size() != object2.m_propertyExpressions.size()
                    || object1.m_annotations.size() != object2.m_annotations.size())
                return false;
            for (PropertyExpression pe : object1.m_propertyExpressions) {
                if (!contains(pe, object2.m_propertyExpressions))
                    return false;
            }
            for (Annotation anno : object1.m_annotations) {
                if (!contains(anno, object2.m_annotations))
                    return false;
            }
            return true;
        }

        protected boolean contains(PropertyExpression pe, Set<PropertyExpression> pes) {
            for (PropertyExpression prop : pes)
                if (pe == prop)
                    return true;
            return false;
        }

        protected boolean contains(Annotation annotation, Set<Annotation> annotations) {
            for (Annotation anno : annotations)
                if (anno == annotation)
                    return true;
            return false;
        }

        protected int getHashCode(HasKey object) {
            int hashCode = 31;
            hashCode += object.m_classExpression.hashCode();
            for (PropertyExpression pe : object.m_propertyExpressions)
                hashCode += pe.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final ClassExpression m_classExpression;
    protected final Set<PropertyExpression> m_propertyExpressions;

    protected HasKey(ClassExpression classExpression, Set<PropertyExpression> propertyExpressions, Set<Annotation> annotations) {
        super(annotations);
        m_classExpression = classExpression;
        m_propertyExpressions = Collections.unmodifiableSet(propertyExpressions);
    }

    public ClassExpression getClassExpression() {
        return m_classExpression;
    }

    public Set<PropertyExpression> getPropertyExpressions() {
        return m_propertyExpressions;
    }

    public Set<ObjectPropertyExpression> getObjectPropertyExpressions() {
        Set<ObjectPropertyExpression> result = new HashSet<ObjectPropertyExpression>();
        for (PropertyExpression pe : m_propertyExpressions)
            if (pe instanceof ObjectPropertyExpression)
                result.add((ObjectPropertyExpression) pe);
        return result;
    }

    public Set<DataPropertyExpression> getDataPropertyExpressions() {
        Set<DataPropertyExpression> result = new HashSet<DataPropertyExpression>();
        for (PropertyExpression pe : m_propertyExpressions)
            if (pe instanceof DataPropertyExpression)
                result.add((DataPropertyExpression) pe);
        return result;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("HasKey(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_classExpression.toString(prefixes));
        buffer.append(" (");
        boolean notFirst = false;
        for (ObjectPropertyExpression pe : getObjectPropertyExpressions()) {
            if (notFirst)
                buffer.append(" ");
            else
                notFirst = true;
            buffer.append(pe.toString(prefixes));
        }
        buffer.append(") (");
        notFirst = false;
        for (DataPropertyExpression dpe : getDataPropertyExpressions()) {
            if (notFirst)
                buffer.append(" ");
            else
                notFirst = true;
            buffer.append(dpe.toString(prefixes));
        }
        buffer.append(")");
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        Identifier subject;
        if (m_classExpression instanceof Atomic)
            subject = (Atomic) m_classExpression;
        else {
            subject = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(m_classExpression.toTurtleString(prefixes, subject));
        }
        Identifier listMainNode = AbstractExtendedOWLObject.getNextBlankNode();
        buffer.append(writeSingleMainTripleAxiom(prefixes, subject, Vocabulary.OWL_HAS_KEY, listMainNode, m_annotations));
        Identifier[] listNodes = new Identifier[m_propertyExpressions.size()];
        PropertyExpression[] propertyExpressions = m_propertyExpressions.toArray(new PropertyExpression[0]);
        for (int i = 0; i < propertyExpressions.length; i++) {
            if (propertyExpressions[i] instanceof Atomic)
                listNodes[i] = ((Atomic) propertyExpressions[i]).getIdentifier();
            else
                listNodes[i] = AbstractExtendedOWLObject.getNextBlankNode();
        }
        printSequence(buffer, prefixes, listMainNode, listNodes);
        for (int i = 0; i < propertyExpressions.length; i++) {
            if (!(propertyExpressions[i] instanceof Atomic)) {
                buffer.append(propertyExpressions[i].toTurtleString(prefixes, listNodes[i]));
            }
        }
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static HasKey create(ClassExpression classExpression, PropertyExpression... propertyExpressions) {
        return create(classExpression, new HashSet<PropertyExpression>(Arrays.asList(propertyExpressions)));
    }

    public static HasKey create(ClassExpression classExpression, Set<PropertyExpression> propertyExpressions) {
        return create(classExpression, propertyExpressions, new HashSet<Annotation>());
    }

    public static HasKey create(ClassExpression classExpression, Set<PropertyExpression> propertyExpressions, Set<Annotation> annotations) {
        return s_interningManager.intern(new HasKey(classExpression, propertyExpressions, annotations));
    }

    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(ExtendedOWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    public <O> O accept(AxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(AxiomVisitor visitor) {
        visitor.visit(this);
    }

    protected OWLObject convertToOWLAPIObject(ToOWLAPIConverter converter) {
        return converter.visit(this);
    }

    public Set<Variable> getVariablesInSignature(VarType varType) {
        Set<Variable> variables = new HashSet<Variable>();
        variables.addAll(m_classExpression.getVariablesInSignature(varType));
        for (PropertyExpression pe : m_propertyExpressions)
            variables.addAll(pe.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<PropertyExpression> propertyExpressions = new HashSet<PropertyExpression>();
        for (PropertyExpression pe : m_propertyExpressions) {
            propertyExpressions.add((PropertyExpression) pe.getBoundVersion(variablesToBindings));
        }
        return create((ClassExpression) m_classExpression.getBoundVersion(variablesToBindings), propertyExpressions, getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_classExpression, m_propertyExpressions, new HashSet<Annotation>());
    }
}
