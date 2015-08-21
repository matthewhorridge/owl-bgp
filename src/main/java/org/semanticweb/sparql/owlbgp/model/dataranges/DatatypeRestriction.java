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

public class DatatypeRestriction extends AbstractExtendedOWLObject implements DataRange {
    private static final long serialVersionUID = 4586938662095776040L;

    protected static InterningManager<DatatypeRestriction> s_interningManager = new InterningManager<DatatypeRestriction>() {
        protected boolean equal(DatatypeRestriction object1, DatatypeRestriction object2) {
            if (object1.m_datatype != object2.m_datatype) return false;
            if (object1.m_facetRestrictions.size() != object2.m_facetRestrictions.size()) return false;
            for (FacetRestriction facetRestriction : object1.m_facetRestrictions) {
                if (!contains(facetRestriction, object2.m_facetRestrictions))
                    return false;
            }
            return true;
        }

        protected boolean contains(FacetRestriction facetRestriction, Set<FacetRestriction> facetRestrictions) {
            for (FacetRestriction restriction : facetRestrictions)
                if (restriction == facetRestriction)
                    return true;
            return false;
        }

        protected int getHashCode(DatatypeRestriction object) {
            int hashCode = 7 * object.m_datatype.hashCode();
            for (FacetRestriction restriction : object.m_facetRestrictions)
                hashCode += restriction.hashCode();
            return hashCode;
        }
    };

    protected final Datatype m_datatype;
    protected final Set<FacetRestriction> m_facetRestrictions;

    protected DatatypeRestriction(Datatype datatype, Set<FacetRestriction> facetRestrictions) {
        m_datatype = datatype;
        m_facetRestrictions = Collections.unmodifiableSet(facetRestrictions);
    }

    public Datatype getDatatype() {
        return m_datatype;
    }

    public Set<FacetRestriction> getFacetRestrictions() {
        return m_facetRestrictions;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("DatatypeRestriction(");
        buffer.append(m_datatype.toString(prefixes));
        buffer.append(" ");
        boolean notFirst = false;
        for (FacetRestriction facetRestriction : m_facetRestrictions) {
            if (notFirst) buffer.append(" ");
            else notFirst = true;
            buffer.append(facetRestriction.toString(prefixes));
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
        buffer.append(Vocabulary.OWL_ON_DATA_TYPE.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_datatype.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(Vocabulary.OWL_WITH_RESTRICTIONS.toString(prefixes));
        Identifier[] listNodes = new Identifier[m_facetRestrictions.size()];
        FacetRestriction[] facetRestrictions = m_facetRestrictions.toArray(new FacetRestriction[0]);
        for (int i = 0; i < facetRestrictions.length; i++) {
            listNodes[i] = AbstractExtendedOWLObject.getNextBlankNode();
        }
        printSequence(buffer, prefixes, null, listNodes);
        for (int i = 0; i < facetRestrictions.length; i++) {
            buffer.append(facetRestrictions[i].toTurtleString(prefixes, listNodes[i]));
        }
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static DatatypeRestriction create(Datatype datatype, FacetRestriction... facetRestrictions) {
        return create(datatype, new HashSet<FacetRestriction>(Arrays.asList(facetRestrictions)));
    }

    public static DatatypeRestriction create(Datatype datatype, Set<FacetRestriction> facetRestrictions) {
        return s_interningManager.intern(new DatatypeRestriction(datatype, facetRestrictions));
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
        variables.addAll(m_datatype.getVariablesInSignature(varType));
        return variables;
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return create((Datatype) m_datatype.getBoundVersion(variablesToBindings), m_facetRestrictions);
    }
}
