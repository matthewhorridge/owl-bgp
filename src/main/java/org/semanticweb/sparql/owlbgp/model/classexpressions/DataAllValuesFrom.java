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
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.dataranges.DataRange;
import org.semanticweb.sparql.owlbgp.model.individuals.AnonymousIndividual;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyExpression;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class DataAllValuesFrom extends AbstractExtendedOWLObject implements ClassExpression {
    private static final long serialVersionUID = -7542119517439032267L;

    protected static InterningManager<DataAllValuesFrom> s_interningManager = new InterningManager<DataAllValuesFrom>() {
        protected boolean equal(DataAllValuesFrom object1, DataAllValuesFrom object2) {
            return object1.m_dpe == object2.m_dpe && object1.m_dataRange == object2.m_dataRange;
        }

        protected int getHashCode(DataAllValuesFrom object) {
            return 11 * object.m_dpe.hashCode() + 23 * object.m_dataRange.hashCode();
        }
    };

    protected final DataPropertyExpression m_dpe;
    protected final DataRange m_dataRange;

    protected DataAllValuesFrom(DataPropertyExpression ope, DataRange dataRange) {
        m_dpe = ope;
        m_dataRange = dataRange;
    }

    public DataPropertyExpression getDataPropertyExpression() {
        return m_dpe;
    }

    public DataRange getDataRange() {
        return m_dataRange;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DataAllValuesFrom(");
        buffer.append(m_dpe.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_dataRange.toString(prefixes));
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
        buffer.append(Vocabulary.OWL_RESTRICTION.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_ON_PROPERTY.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_dpe.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_ALL_VALUES_FROM.toString(prefixes));
        buffer.append(" ");
        if (m_dataRange instanceof Atomic) {
            buffer.append(m_dataRange.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
        }
        else {
            AnonymousIndividual drbnode = AbstractExtendedOWLObject.getNextBlankNode();
            buffer.append(drbnode);
            buffer.append(" . ");
            buffer.append(LB);
            buffer.append(m_dataRange.toTurtleString(prefixes, drbnode));
        }
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static DataAllValuesFrom create(DataPropertyExpression dpe, DataRange dataRange) {
        return s_interningManager.intern(new DataAllValuesFrom(dpe, dataRange));
    }

    @Override
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

    @Override
    protected OWLObject convertToOWLAPIObject(ToOWLAPIConverter converter) {
        return converter.visit(this);
    }

    @Override
    public Set<Variable> getVariablesInSignature(VarType varType) {
        Set<Variable> variables = new HashSet<Variable>();
        variables.addAll(m_dpe.getVariablesInSignature(varType));
        variables.addAll(m_dataRange.getVariablesInSignature(varType));
        return variables;
    }

    @Override
    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((DataPropertyExpression) m_dpe.getBoundVersion(variablesToBindings), (DataRange) m_dataRange.getBoundVersion(variablesToBindings));
    }
}
