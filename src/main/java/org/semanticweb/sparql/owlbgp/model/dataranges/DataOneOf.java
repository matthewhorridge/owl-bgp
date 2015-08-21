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
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class DataOneOf extends AbstractExtendedOWLObject implements DataRange {
    private static final long serialVersionUID = 3341650510200806191L;

    protected static InterningManager<DataOneOf> s_interningManager = new InterningManager<DataOneOf>() {
        protected boolean equal(DataOneOf dataOneOf1, DataOneOf dataOneOf2) {
            if (dataOneOf1.m_enumeration.size() != dataOneOf2.m_enumeration.size())
                return false;
            for (Literal literal : dataOneOf1.m_enumeration) {
                if (!contains(literal, dataOneOf2.m_enumeration))
                    return false;
            }
            return true;
        }

        protected boolean contains(Literal literal, Set<Literal> literals) {
            for (Literal oneOf : literals)
                if (literal == oneOf)
                    return true;
            return false;
        }

        protected int getHashCode(DataOneOf oneOf) {
            int hashCode = 0;
            for (Literal literal : oneOf.m_enumeration)
                hashCode += literal.hashCode();
            return hashCode;
        }
    };

    protected final Set<Literal> m_enumeration;

    protected DataOneOf(Set<Literal> enumeration) {
        m_enumeration = Collections.unmodifiableSet(enumeration);
    }

    public Set<Literal> getLiterals() {
        return m_enumeration;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DataOneOf(");
        boolean notFirst = false;
        for (Literal literal : m_enumeration) {
            if (notFirst)
                buffer.append(' ');
            else
                notFirst = true;
            buffer.append(literal.toString(prefixes));
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
        buffer.append(Vocabulary.OWL_ONE_OF.toString(prefixes));
        Identifier[] listNodes = new Identifier[m_enumeration.size()];
        Literal[] literals = m_enumeration.toArray(new Literal[0]);
        for (int i = 0; i < literals.length; i++)
            listNodes[i] = literals[i].getIdentifier();
        printSequence(buffer, prefixes, null, listNodes);
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static DataOneOf create(Literal... literals) {
        return create(new HashSet<Literal>(Arrays.asList(literals)));
    }

    public static DataOneOf create(Set<Literal> oneOfs) {
        return s_interningManager.intern(new DataOneOf(oneOfs));
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
        for (Literal literal : m_enumeration) {
            variables.addAll(literal.getVariablesInSignature(varType));
        }
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Set<Literal> literals = new HashSet<Literal>();
        for (Literal literal : m_enumeration)
            literals.add((Literal) literal.getBoundVersion(variablesToBindings));
        return create(literals);
    }
}