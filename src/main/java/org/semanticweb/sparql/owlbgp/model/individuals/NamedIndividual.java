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


package org.semanticweb.sparql.owlbgp.model.individuals;

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
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;


public class NamedIndividual extends AbstractExtendedOWLObject implements Individual {
    private static final long serialVersionUID = -8797258383209941720L;

    protected static InterningManager<NamedIndividual> s_interningManager = new InterningManager<NamedIndividual>() {
        protected boolean equal(NamedIndividual object1, NamedIndividual object2) {
            return object1.m_iri == object2.m_iri;
        }

        protected int getHashCode(NamedIndividual object) {
            return object.m_iri.hashCode();
        }
    };

    protected final IRI m_iri;

    protected NamedIndividual(IRI iri) {
        m_iri = iri;
    }

    public IRI getIRI() {
        return m_iri;
    }

    public String getIRIString() {
        return m_iri.toString();
    }

    @Override
    public String toString(Prefixes prefixes) {
        return m_iri.toString(prefixes);
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        return toString(prefixes);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static NamedIndividual create(String iriString) {
        if (iriString.charAt(0) == '<') iriString = iriString.substring(1, iriString.length() - 1);
        return create(IRI.create(iriString));
    }

    public static NamedIndividual create(IRI iri) {
        return s_interningManager.intern(new NamedIndividual(iri));
    }

    public Identifier getIdentifier() {
        return m_iri;
    }

    public String getIdentifierString() {
        return m_iri.getIRIString();
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
