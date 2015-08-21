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
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class EquivalentDataProperties extends AbstractAxiom {
    private static final long serialVersionUID = 3226003365814187905L;

    protected static InterningManager<EquivalentDataProperties> s_interningManager = new InterningManager<EquivalentDataProperties>() {
        protected boolean equal(EquivalentDataProperties object1, EquivalentDataProperties object2) {
            if (object1.m_dataPropertyExpressions.size() != object2.m_dataPropertyExpressions.size()
                    || object1.m_annotations.size() != object2.m_annotations.size())
                return false;
            for (DataPropertyExpression ope : object1.m_dataPropertyExpressions) {
                if (!contains(ope, object2.m_dataPropertyExpressions))
                    return false;
            }
            for (Annotation anno : object1.m_annotations) {
                if (!contains(anno, object2.m_annotations))
                    return false;
            }
            return true;
        }

        protected boolean contains(DataPropertyExpression ope, Set<DataPropertyExpression> opes) {
            for (DataPropertyExpression equiv : opes)
                if (equiv == ope)
                    return true;
            return false;
        }

        protected boolean contains(Annotation annotation, Set<Annotation> annotations) {
            for (Annotation anno : annotations)
                if (anno == annotation)
                    return true;
            return false;
        }

        protected int getHashCode(EquivalentDataProperties object) {
            int hashCode = 0;
            for (DataPropertyExpression equiv : object.m_dataPropertyExpressions)
                hashCode += equiv.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            return hashCode;
        }
    };

    protected final Set<DataPropertyExpression> m_dataPropertyExpressions;

    protected EquivalentDataProperties(Set<DataPropertyExpression> dataPropertyExpressions, Set<Annotation> annotations) {
        super(annotations);
        m_dataPropertyExpressions = Collections.unmodifiableSet(dataPropertyExpressions);
    }

    public Set<DataPropertyExpression> getDataPropertyExpressions() {
        return m_dataPropertyExpressions;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("EquivalentDataProperties(");
        writeAnnoations(buffer, prefixes);
        boolean notFirst = false;
        for (DataPropertyExpression equiv : m_dataPropertyExpressions) {
            if (notFirst)
                buffer.append(' ');
            else
                notFirst = true;
            buffer.append(equiv.toString(prefixes));
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        DataPropertyExpression[] dataPropertyExpressions = m_dataPropertyExpressions.toArray(new DataPropertyExpression[0]);
        Identifier[] dataPropertyIDs = new Identifier[dataPropertyExpressions.length];
        for (int i = 0; i < dataPropertyExpressions.length; i++) {
            dataPropertyIDs[i] = (Atomic) dataPropertyExpressions[i];
        }
        for (int i = 0; i < dataPropertyIDs.length - 1; i++)
            buffer.append(writeSingleMainTripleAxiom(prefixes, dataPropertyIDs[i], Vocabulary.OWL_EQUIVALENT_PROPERTY, dataPropertyIDs[i + 1], m_annotations));
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static EquivalentDataProperties create(Set<DataPropertyExpression> dataPropertyExpressions) {
        return EquivalentDataProperties.create(dataPropertyExpressions, new HashSet<Annotation>());
    }

    public static EquivalentDataProperties create(DataPropertyExpression dataPropertyExpression1, DataPropertyExpression dataPropertyExpression2, Annotation... annotations) {
        return create(dataPropertyExpression1, dataPropertyExpression2, new HashSet<Annotation>(Arrays.asList(annotations)));
    }

    public static EquivalentDataProperties create(DataPropertyExpression dataPropertyExpression1, DataPropertyExpression dataPropertyExpression2, Set<Annotation> annotations) {
        Set<DataPropertyExpression> equivs = new HashSet<DataPropertyExpression>();
        equivs.add(dataPropertyExpression1);
        equivs.add(dataPropertyExpression2);
        return create(equivs, annotations);
    }

    public static EquivalentDataProperties create(Set<DataPropertyExpression> dataPropertyExpressions, Set<Annotation> annotations) {
        return s_interningManager.intern(new EquivalentDataProperties(dataPropertyExpressions, annotations));
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
        for (DataPropertyExpression ope : m_dataPropertyExpressions)
            variables.addAll(ope.getVariablesInSignature(varType));
        getAnnotationVariables(varType, variables);
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<DataPropertyExpression> dataPropertyExpression = new HashSet<DataPropertyExpression>();
        for (DataPropertyExpression dpe : m_dataPropertyExpressions) {
            dataPropertyExpression.add((DataPropertyExpression) dpe.getBoundVersion(variablesToBindings));
        }
        return create(dataPropertyExpression, getBoundAnnotations(variablesToBindings));
    }

    public Axiom getAxiomWithoutAnnotations() {
        return create(m_dataPropertyExpressions);
    }
}
