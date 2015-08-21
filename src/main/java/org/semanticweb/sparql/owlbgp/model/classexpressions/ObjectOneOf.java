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
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class ObjectOneOf extends AbstractExtendedOWLObject implements ClassExpression {
    private static final long serialVersionUID = -3492317792968371893L;

    protected static InterningManager<ObjectOneOf> s_interningManager = new InterningManager<ObjectOneOf>() {
        protected boolean equal(ObjectOneOf objectOneOf1, ObjectOneOf objectOneOf2) {
            if (objectOneOf1.m_enumeration.size() != objectOneOf2.m_enumeration.size())
                return false;
            for (Individual individual : objectOneOf1.m_enumeration) {
                if (!contains(individual, objectOneOf2.m_enumeration))
                    return false;
            }
            return true;
        }

        protected boolean contains(Individual individual, Set<Individual> individuals) {
            for (Individual oneOf : individuals)
                if (individual == oneOf)
                    return true;
            return false;
        }

        protected int getHashCode(ObjectOneOf oneOf) {
            int hashCode = 0;
            for (Individual individual : oneOf.m_enumeration)
                hashCode += individual.hashCode();
            return hashCode;
        }
    };

    protected final Set<Individual> m_enumeration;

    protected ObjectOneOf(Set<Individual> enumeration) {
        m_enumeration = Collections.unmodifiableSet(enumeration);
    }

    public Set<Individual> getIndividuals() {
        return m_enumeration;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ObjectOneOf(");
        boolean notFirst = false;
        for (Individual individual : m_enumeration) {
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
        buffer.append(Vocabulary.OWL_ONE_OF.toString(prefixes));
        Identifier[] listNodes = new Identifier[m_enumeration.size()];
        Individual[] individuals = m_enumeration.toArray(new Individual[0]);
        for (int i = 0; i < individuals.length; i++)
            listNodes[i] = individuals[i].getIdentifier();
        printSequence(buffer, prefixes, null, listNodes);
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static ObjectOneOf create(Individual... individuals) {
        return create(new HashSet<Individual>(Arrays.asList(individuals)));
    }

    public static ObjectOneOf create(Set<Individual> oneOfs) {
        return s_interningManager.intern(new ObjectOneOf(oneOfs));
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
        for (Individual individual : m_enumeration) {
            variables.addAll(individual.getVariablesInSignature(varType));
        }
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<Individual> individuals = new HashSet<Individual>();
        for (Individual individual : m_enumeration) {
            individuals.add((Individual) individual.getBoundVersion(variablesToBindings));
        }
        return create(individuals);
    }
}