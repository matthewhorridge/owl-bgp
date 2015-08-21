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
import org.semanticweb.sparql.owlbgp.model.individuals.Individual;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class NegativeDataPropertyAssertion extends AbstractAxiom implements Assertion {
    private static final long serialVersionUID = -8885275559316000307L;

    protected static InterningManager<NegativeDataPropertyAssertion> s_interningManager = new InterningManager<NegativeDataPropertyAssertion>() {
        protected boolean equal(NegativeDataPropertyAssertion object1, NegativeDataPropertyAssertion object2) {
            if (object1.m_dpe != object2.m_dpe
                    || object1.m_individual != object2.m_individual
                    || object1.m_literal != object2.m_literal
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

        protected int getHashCode(NegativeDataPropertyAssertion object) {
            int hashCode = -13 * object.m_dpe.hashCode() + 17 * object.m_individual.hashCode() + 23 * object.m_literal.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final DataPropertyExpression m_dpe;
    protected final Individual m_individual;
    protected final Literal m_literal;

    protected NegativeDataPropertyAssertion(DataPropertyExpression dpe, Individual individual, Literal literal, Set<Annotation> annotations) {
        super(annotations);
        m_dpe = dpe;
        m_individual = individual;
        m_literal = literal;
    }

    public DataPropertyExpression getDataPropertyExpression() {
        return m_dpe;
    }

    public Individual getIndividual() {
        return m_individual;
    }

    public Literal getLiteral() {
        return m_literal;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("NegativeDataPropertyAssertion(");
        writeAnnoations(buffer, prefixes);
        buffer.append(m_dpe.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_individual.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_literal.toString(prefixes));
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        Identifier bnode = AbstractExtendedOWLObject.getNextBlankNode();
        buffer.append(bnode);
        buffer.append(" ");
        buffer.append(Vocabulary.RDF_TYPE);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_NEGATIVE_PROPERTY_ASSERTION);
        buffer.append(" . ");
        buffer.append(LB);
        buffer.append(bnode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_SOURCE_INDIVIDUAL);
        buffer.append(" ");
        buffer.append(m_individual.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        buffer.append(bnode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_ASSERTION_PROPERTY);
        buffer.append(" ");
        buffer.append(((Atomic) m_dpe).toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        buffer.append(bnode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_TARGET_VALUE);
        buffer.append(" ");
        buffer.append(m_literal.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        for (Annotation anno : m_annotations)
            buffer.append(anno.toTurtleString(prefixes, bnode));
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static NegativeDataPropertyAssertion create(DataPropertyExpression dpe, Individual individual, Literal literal, Annotation... annotations) {
        return create(dpe, individual, literal, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static NegativeDataPropertyAssertion create(DataPropertyExpression dpe, Individual individual, Literal literal, Set<Annotation> annotations) {
        return s_interningManager.intern(new NegativeDataPropertyAssertion(dpe, individual, literal, annotations));
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
        variables.addAll(m_dpe.getVariablesInSignature(varType));
        variables.addAll(m_individual.getVariablesInSignature(varType));
        variables.addAll(m_literal.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((DataPropertyExpression) m_dpe.getBoundVersion(variablesToBindings), (Individual) m_individual.getBoundVersion(variablesToBindings), (Literal) m_literal.getBoundVersion(variablesToBindings), getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_dpe, m_individual, m_literal);
    }
}
