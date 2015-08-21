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


package org.semanticweb.sparql.owlbgp.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyExpression;

public class Annotation extends AbstractExtendedOWLObject {
    private static final long serialVersionUID = -4586686325214553112L;

    protected static InterningManager<Annotation> s_interningManager = new InterningManager<Annotation>() {
        protected boolean equal(Annotation object1, Annotation object2) {
            if (object1.m_annotationProperty != object2.m_annotationProperty
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

        protected int getHashCode(Annotation object) {
            return 7 * object.m_annotationProperty.hashCode() + 13 * object.m_annotationValue.hashCode();
        }
    };

    protected final AnnotationPropertyExpression m_annotationProperty;
    protected final AnnotationValue m_annotationValue;
    protected final Set<Annotation> m_annotations;

    protected Annotation(AnnotationPropertyExpression annotationProperty, AnnotationValue annotationValue, Set<Annotation> annotations) {
        m_annotationProperty = annotationProperty;
        m_annotationValue = annotationValue;
        m_annotations = Collections.unmodifiableSet(new HashSet<Annotation>(annotations)); //immutable
    }

    public AnnotationPropertyExpression getAnnotationProperty() {
        return m_annotationProperty;
    }

    public AnnotationValue getAnnotationValue() {
        return m_annotationValue;
    }

    public Set<Annotation> getAnnotations() {
        return m_annotations;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer sb = new StringBuffer();
        sb.append("Annotation(");
        boolean notFirst = false;
        for (Annotation anno : m_annotations) {
            if (notFirst) sb.append(" ");
            else notFirst = true;
            sb.append(anno.toString(prefixes));
        }
        if (notFirst) sb.append(" ");
        sb.append(m_annotationProperty.toString(prefixes));
        sb.append(" ");
        sb.append(m_annotationValue.toString(prefixes));
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        if (mainNode == null) mainNode = AbstractExtendedOWLObject.getNextBlankNode();
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(m_annotationProperty.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_annotationValue.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        if (!m_annotations.isEmpty()) {
            AnonymousIndividual bnode = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(bnode);
            buffer.append(" rdf:type owl:Annotation .");
            buffer.append(LB);
            buffer.append(bnode);
            buffer.append(" owl:annotatedSource ");
            buffer.append(mainNode.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
            buffer.append(bnode);
            buffer.append(" owl:annotatedProperty ");
            buffer.append(m_annotationProperty.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
            buffer.append(bnode);
            buffer.append(" owl:annotatedTarget ");
            buffer.append(m_annotationValue.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
            for (Annotation anno : m_annotations)
                buffer.append(anno.toTurtleString(prefixes, bnode));
        }
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static Annotation create(AnnotationPropertyExpression annotationProperty, AnnotationValue annotationValue) {
        return create(annotationProperty, annotationValue, new HashSet<Annotation>());
    }

    public static Annotation create(AnnotationPropertyExpression annotationProperty, AnnotationValue annotationValue, Annotation... annotations) {
        return create(annotationProperty, annotationValue, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static Annotation create(AnnotationPropertyExpression annotationProperty, AnnotationValue annotationValue, Set<Annotation> annotations) {
        return s_interningManager.intern(new Annotation(annotationProperty, annotationValue, annotations));
    }

    @Override
    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(ExtendedOWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected OWLObject convertToOWLAPIObject(ToOWLAPIConverter converter) {
        return converter.visit(this);
    }

    @Override
    public Set<Variable> getVariablesInSignature(VarType varType) {
        Set<Variable> variables = new HashSet<Variable>();
        variables.addAll(m_annotationProperty.getVariablesInSignature(varType));
        variables.addAll(m_annotationValue.getVariablesInSignature(varType));
        for (Annotation annotation : m_annotations)
            variables.addAll(annotation.getVariablesInSignature(varType));
        return variables;
    }

    @Override
    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<Annotation> annotations = new HashSet<Annotation>();
        for (Annotation annotation : m_annotations)
            annotations.add((Annotation) annotation.getBoundVersion(variablesToBindings));
        return Annotation.create((AnnotationProperty) m_annotationProperty.getBoundVersion(variablesToBindings), (AnnotationValue) m_annotationValue.getBoundVersion(variablesToBindings), annotations);
    }
}
