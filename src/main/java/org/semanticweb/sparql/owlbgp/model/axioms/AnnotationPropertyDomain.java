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


package org.semanticweb.sparql.owlbgp.model.axioms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.Annotation;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.AxiomVisitor;
import org.semanticweb.sparql.owlbgp.model.AxiomVisitorEx;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitor;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class AnnotationPropertyDomain extends AbstractAxiom implements AnnotationPropertyAxiom {
    private static final long serialVersionUID = 3117954908697132827L;

    protected static InterningManager<AnnotationPropertyDomain> s_interningManager = new InterningManager<AnnotationPropertyDomain>() {
        protected boolean equal(AnnotationPropertyDomain object1, AnnotationPropertyDomain object2) {
            if (object1.m_annotationPropertyExpression != object2.m_annotationPropertyExpression
                    || object1.m_domain != object2.m_domain
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

        protected int getHashCode(AnnotationPropertyDomain object) {
            int hashCode = 43 + 11 * object.m_annotationPropertyExpression.hashCode() + 17 * object.m_domain.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final AnnotationPropertyExpression m_annotationPropertyExpression;
    protected final Identifier m_domain;

    protected AnnotationPropertyDomain(AnnotationPropertyExpression ope, Identifier classExpression, Set<Annotation> annotations) {
        super(annotations);
        m_annotationPropertyExpression = ope;
        m_domain = classExpression;
    }

    public AnnotationPropertyExpression getAnnotationPropertyExpression() {
        return m_annotationPropertyExpression;
    }

    public Identifier getDomain() {
        return m_domain;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("AnnotationPropertyDomain(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_annotationPropertyExpression.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_domain.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        return writeSingleMainTripleAxiom(prefixes, (Atomic) m_annotationPropertyExpression, Vocabulary.RDFS_DOMAIN, m_domain, m_annotations);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static AnnotationPropertyDomain create(AnnotationPropertyExpression annotationPropertyExpression, Identifier domain, Annotation... annotations) {
        return create(annotationPropertyExpression, domain, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static AnnotationPropertyDomain create(AnnotationPropertyExpression annotationPropertyExpression, Identifier domain, Set<Annotation> annotations) {
        if (domain instanceof AnonymousIndividual)
            throw new IllegalArgumentException("Error: The domain of an annotation property domain axiom cannot be anonymous, but here we have: " + domain);
        return s_interningManager.intern(new AnnotationPropertyDomain(annotationPropertyExpression, domain, annotations));
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
        variables.addAll(m_annotationPropertyExpression.getVariablesInSignature(varType));
        variables.addAll(m_domain.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((AnnotationProperty) m_annotationPropertyExpression.getBoundVersion(variablesToBindings), (IRI) m_domain.getBoundVersion(variablesToBindings), getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_annotationPropertyExpression, m_domain);
    }
}
