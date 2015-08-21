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
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassExpression;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class ClassAssertion extends AbstractAxiom implements Assertion {
    private static final long serialVersionUID = -4207422378867470495L;

    protected static InterningManager<ClassAssertion> s_interningManager = new InterningManager<ClassAssertion>() {
        protected boolean equal(ClassAssertion object1, ClassAssertion object2) {
            if (object1.m_ce != object2.m_ce
                    || object1.m_individual != object2.m_individual
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

        protected int getHashCode(ClassAssertion object) {
            int hashCode = 43 * object.m_ce.hashCode() + 7 * object.m_individual.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final ClassExpression m_ce;
    protected final Individual m_individual;

    protected ClassAssertion(ClassExpression ope, Individual individual, Set<Annotation> annotations) {
        super(annotations);
        m_ce = ope;
        m_individual = individual;
    }

    public ClassExpression getClassExpression() {
        return m_ce;
    }

    public Individual getIndividual() {
        return m_individual;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ClassAssertion(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_ce.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_individual.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        Identifier object;
        if (!(m_ce instanceof Atomic)) {
            object = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(m_ce.toTurtleString(prefixes, object));
        }
        else
            object = (Atomic) m_ce;
        buffer.append(writeSingleMainTripleAxiom(prefixes, (Atomic) m_individual, Vocabulary.RDF_TYPE, object, m_annotations));
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static ClassAssertion create(ClassExpression ce, Individual individual) {
        return create(ce, individual, new HashSet<Annotation>());
    }

    public static ClassAssertion create(ClassExpression ce, Individual individual, Annotation... annotations) {
        return create(ce, individual, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static ClassAssertion create(ClassExpression ce, Individual individual, Set<Annotation> annotations) {
        return s_interningManager.intern(new ClassAssertion(ce, individual, annotations));
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
        variables.addAll(m_ce.getVariablesInSignature(varType));
        variables.addAll(m_individual.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((ClassExpression) m_ce.getBoundVersion(variablesToBindings), (Individual) m_individual.getBoundVersion(variablesToBindings), getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_ce, m_individual);
    }
}
