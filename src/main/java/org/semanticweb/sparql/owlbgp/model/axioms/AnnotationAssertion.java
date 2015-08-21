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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.AnnotationSubject;
import org.semanticweb.sparql.owlbgp.model.AnnotationValue;
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
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyExpression;

public class AnnotationAssertion extends AbstractAxiom {
    private static final long serialVersionUID = -2138189482006639179L;

    protected static InterningManager<AnnotationAssertion> s_interningManager = new InterningManager<AnnotationAssertion>() {
        protected boolean equal(AnnotationAssertion object1, AnnotationAssertion object2) {
            if (object1.m_annotationProperty != object2.m_annotationProperty
                    || object1.m_annotationSubject != object2.m_annotationSubject
                    || object1.m_annotationValue != object2.m_annotationValue
                    || object1.m_annotations.size() != object2.m_annotations.size())
                return false;
            for (Annotation anno : object1.m_annotations) {
                if (!contains(anno, object2.m_annotations))
                    return false;
            }
            return true;
        }

        protected boolean contains(Annotation annotation, Set<Annotation> annotations) {
            for (Annotation anno : annotations)
                if (anno == annotation)
                    return true;
            return false;
        }

        protected int getHashCode(AnnotationAssertion object) {
            int hashCode = 43 * object.m_annotationProperty.hashCode() + 7 * object.m_annotationSubject.hashCode() + 11 * object.m_annotationValue.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final AnnotationPropertyExpression m_annotationProperty;
    protected final AnnotationSubject m_annotationSubject;
    protected final AnnotationValue m_annotationValue;

    protected AnnotationAssertion(AnnotationPropertyExpression annotationProperty, AnnotationSubject annotationSubject, AnnotationValue annotationValue, Set<Annotation> annotations) {
        super(annotations);
        m_annotationProperty = annotationProperty;
        m_annotationSubject = annotationSubject;
        m_annotationValue = annotationValue;
    }

    public AnnotationPropertyExpression getAnnotationProperty() {
        return m_annotationProperty;
    }

    public AnnotationSubject getAnnotationSubject() {
        return m_annotationSubject;
    }

    public AnnotationValue getAnnotationValue() {
        return m_annotationValue;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("AnnotationAssertion(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_annotationProperty.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_annotationSubject.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_annotationValue.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        return writeSingleMainTripleAxiom(prefixes, (Atomic) m_annotationSubject, (Atomic) m_annotationProperty, (Atomic) m_annotationValue, m_annotations);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static AnnotationAssertion create(AnnotationPropertyExpression annotationProperty, AnnotationSubject annotationSubject, AnnotationValue annotationValue) {
        return create(annotationProperty, annotationSubject, annotationValue, new HashSet<Annotation>());
    }

    public static AnnotationAssertion create(AnnotationPropertyExpression annotationProperty, AnnotationSubject annotationSubject, AnnotationValue annotationValue, Set<Annotation> annotations) {
        return s_interningManager.intern(new AnnotationAssertion(annotationProperty, annotationSubject, annotationValue, annotations));
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
        variables.addAll(m_annotationProperty.getVariablesInSignature(varType));
        variables.addAll(m_annotationSubject.getVariablesInSignature(varType));
        variables.addAll(m_annotationValue.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((AnnotationProperty) m_annotationProperty.getBoundVersion(variablesToBindings), (AnnotationSubject) m_annotationSubject.getBoundVersion(variablesToBindings), (AnnotationValue) m_annotationValue.getBoundVersion(variablesToBindings), getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_annotationProperty, m_annotationSubject, m_annotationValue);
    }
}
