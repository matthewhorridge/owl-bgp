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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.AbstractExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitor;
import org.semanticweb.sparql.owlbgp.model.ExtendedOWLObjectVisitorEx;
import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.literals.Literal;
import org.semanticweb.sparql.owlbgp.model.literals.TypedLiteral;


public class FacetRestriction extends AbstractExtendedOWLObject {
    private static final long serialVersionUID = -3713040265806923820L;

    public enum OWL2_FACET {
        MIN_INCLUSIVE(IRI.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "minInclusive")),
        MAX_INCLUSIVE(IRI.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "maxInclusive")),
        MIN_EXCLUSIVE(IRI.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "minExclusive")),
        MAX_EXCLUSIVE(IRI.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "maxExclusive")),
        LENGTH(IRI.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "length")),
        MIN_LENGTH(IRI.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "minLength")),
        MAX_LENGTH(IRI.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "maxLength")),
        PATTERN(IRI.create(Prefixes.s_semanticWebPrefixes.get("xsd") + "pattern")),
        LANG_RANGE(IRI.create(Prefixes.s_semanticWebPrefixes.get("rdf") + "langRange"));

        protected final IRI m_facetIRI;

        OWL2_FACET(IRI facetIRI) {
            this.m_facetIRI = facetIRI;
        }

        public IRI getIRI() {
            return m_facetIRI;
        }

        public String toString() {
            return toString(Prefixes.STANDARD_PREFIXES);
        }

        public String toString(Prefixes prefixes) {
            return m_facetIRI.toString(prefixes);
        }
    }

    protected static InterningManager<FacetRestriction> s_interningManager = new InterningManager<FacetRestriction>() {
        protected boolean equal(FacetRestriction object1, FacetRestriction object2) {
            return object1.m_facet == object2.m_facet && object1.m_literal == object2.m_literal;
        }

        protected int getHashCode(FacetRestriction object) {
            return object.m_facet.hashCode() + object.m_literal.hashCode();
        }
    };

    protected final OWL2_FACET m_facet;
    protected final TypedLiteral m_literal;

    protected FacetRestriction(OWL2_FACET facet, TypedLiteral literal) {
        m_facet = facet;
        m_literal = literal;
    }

    public OWL2_FACET getFacet() {
        return m_facet;
    }

    public Literal getLiteral() {
        return m_literal;
    }

    @Override
    public String toString(Prefixes prefixes) {
        return m_facet.toString(prefixes) + " " + m_literal.toString(prefixes);
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(mainNode);
        buffer.append(" ");
        buffer.append(m_facet.toString(prefixes));
        buffer.append(" ");
        buffer.append(m_literal.toString(prefixes));
        buffer.append(" . ");
        buffer.append(LB);
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static FacetRestriction create(OWL2_FACET facet, TypedLiteral literal) {
        return s_interningManager.intern(new FacetRestriction(facet, literal));
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
        return new HashSet<Variable>();
    }

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return this;
    }
}
