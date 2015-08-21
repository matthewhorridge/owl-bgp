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


package org.semanticweb.sparql.owlbgp.model.dataranges;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.AbstractExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitor;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class DataUnionOf extends AbstractExtendedOWLObject implements DataRange {
    private static final long serialVersionUID = 2833631154707106474L;

    protected static InterningManager<DataUnionOf> s_interningManager = new InterningManager<DataUnionOf>() {
        protected boolean equal(DataUnionOf intersection1, DataUnionOf intersection2) {
            if (intersection1.m_dataRanges.size() != intersection2.m_dataRanges.size())
                return false;
            for (DataRange conjunct : intersection1.m_dataRanges) {
                if (!contains(conjunct, intersection2.m_dataRanges))
                    return false;
            }
            return true;
        }

        protected boolean contains(DataRange dataRange, Set<DataRange> dataRanges) {
            for (DataRange conjunct : dataRanges)
                if (conjunct == dataRange)
                    return true;
            return false;
        }

        protected int getHashCode(DataUnionOf intersection) {
            int hashCode = 0;
            for (DataRange conjunct : intersection.m_dataRanges)
                hashCode += conjunct.hashCode();
            return hashCode;
        }
    };

    protected final Set<DataRange> m_dataRanges;

    protected DataUnionOf(Set<DataRange> dataRanges) {
        m_dataRanges = Collections.unmodifiableSet(dataRanges);
    }

    public Set<DataRange> getDataRanges() {
        return m_dataRanges;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DataUnionOf(");
        boolean notFirst = false;
        for (DataRange conjunct : m_dataRanges) {
            if (notFirst)
                buffer.append(' ');
            else
                notFirst = true;
            buffer.append(conjunct.toString(prefixes));
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
        buffer.append(Vocabulary.RDFS_DATATYPE.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_UNION_OF.toString(prefixes));
        Identifier[] listNodes = new Identifier[m_dataRanges.size()];
        DataRange[] dataRanges = m_dataRanges.toArray(new DataRange[0]);
        for (int i = 0; i < dataRanges.length; i++) {
            if (dataRanges[i] instanceof Atomic)
                listNodes[i] = ((Atomic) dataRanges[i]).getIdentifier();
            else
                listNodes[i] = AbstractExtendedOWLObject.getNextBlankNode();
        }
        printSequence(buffer, prefixes, null, listNodes);
        for (int i = 0; i < dataRanges.length; i++) {
            if (!(dataRanges[i] instanceof Atomic)) {
                buffer.append(dataRanges[i].toTurtleString(prefixes, listNodes[i]));
            }
        }
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static DataUnionOf create(Set<DataRange> dataRanges) {
        return s_interningManager.intern(new DataUnionOf(dataRanges));
    }

    public static DataUnionOf create(DataRange... dataRanges) {
        return s_interningManager.intern(new DataUnionOf(new HashSet<DataRange>(Arrays.asList(dataRanges))));
    }

    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(ExtendedOWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    protected OWLObject convertToOWLAPIObject(ToOWLAPIConverter converter) {
        return converter.visit(this);
    }

    public Set<Variable> getVariablesInSignature(VarType varType) {
        Set<Variable> variables = new HashSet<Variable>();
        for (DataRange dataRange : m_dataRanges) {
            variables.addAll(dataRange.getVariablesInSignature(varType));
        }
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<DataRange> dataRanges = new HashSet<DataRange>();
        for (DataRange dr : m_dataRanges) {
            dataRanges.add((DataRange) dr.getBoundVersion(variablesToBindings));
        }
        return create(dataRanges);
    }
}