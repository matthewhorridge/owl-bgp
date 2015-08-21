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
import java.util.Collections;
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
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;


public class SameIndividual extends AbstractAxiom implements Assertion {
    private static final long serialVersionUID = 5809151112245431871L;

    protected static InterningManager<SameIndividual> s_interningManager = new InterningManager<SameIndividual>() {
        protected boolean equal(SameIndividual object1, SameIndividual object2) {
            if (object1.m_individuals.size() != object2.m_individuals.size()
                    || object1.m_annotations.size() != object2.m_annotations.size())
                return false;
            for (Individual individual : object1.m_individuals) {
                if (!contains(individual, object2.m_individuals))
                    return false;
            }
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

        protected boolean contains(Individual individual, Set<Individual> individuals) {
            for (Individual ind : individuals)
                if (individual == ind)
                    return true;
            return false;
        }

        protected int getHashCode(SameIndividual object) {
            int hashCode = 13;
            for (Individual individual : object.m_individuals)
                hashCode += individual.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final Set<Individual> m_individuals;

    protected SameIndividual(Set<Individual> individuals, Set<Annotation> annotations) {
        super(annotations);
        m_individuals = Collections.unmodifiableSet(individuals);
    }

    public Set<Individual> getIndividuals() {
        return m_individuals;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SameIndividual(");
        writeAnnoations(buffer, prefixes);
        boolean notFirst = false;
        for (Individual individual : m_individuals) {
            if (notFirst)
                buffer.append(' ');
            else
                notFirst = true;
            buffer.append(individual.toString(prefixes));
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        Individual[] individuals = m_individuals.toArray(new Individual[0]);
        for (int i = 0; i < individuals.length - 1; i++)
            buffer.append(writeSingleMainTripleAxiom(prefixes, individuals[i], Vocabulary.OWL_SAME_AS, individuals[i + 1], m_annotations));
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static SameIndividual create(Individual... individuals) {
        return create(new HashSet<Individual>(Arrays.asList(individuals)));
    }

    public static SameIndividual create(Set<Individual> individuals) {
        return create(individuals, new HashSet<Annotation>());
    }

    public static SameIndividual create(Set<Individual> individuals, Annotation... annotations) {
        return create(individuals, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static SameIndividual create(Set<Individual> individuals, Set<Annotation> annotations) {
        return s_interningManager.intern(new SameIndividual(individuals, annotations));
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
        for (Individual individual : m_individuals)
            variables.addAll(individual.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<Individual> individuals = new HashSet<Individual>();
        for (Individual individual : m_individuals) {
            individuals.add((Individual) individual.getBoundVersion(variablesToBindings));
        }
        return create(individuals, getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_individuals);
    }
}
