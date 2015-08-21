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
import org.semanticweb.sparql.owlbgp.model.IRI;
import org.semanticweb.sparql.owlbgp.model.Identifier;
import org.semanticweb.sparql.owlbgp.model.InterningManager;
import org.semanticweb.sparql.owlbgp.model.Prefixes;
import org.semanticweb.sparql.owlbgp.model.ToOWLAPIConverter;
import org.semanticweb.sparql.owlbgp.model.Variable;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;

public class Clazz extends AbstractExtendedOWLObject implements ClassExpression, Atomic {
    private static final long serialVersionUID = -8150996806174432055L;

    protected static InterningManager<Clazz> s_interningManager = new InterningManager<Clazz>() {
        protected boolean equal(Clazz object1, Clazz object2) {
            return object1.m_iri == object2.m_iri;
        }

        protected int getHashCode(Clazz object) {
            return object.m_iri.hashCode();
        }
    };

    public static final Clazz THING = create(IRI.create("http://www.w3.org/2002/07/owl#Thing"));
    public static final Clazz NOTHING = create(IRI.create("http://www.w3.org/2002/07/owl#Nothing"));

    protected final IRI m_iri;

    protected Clazz(IRI iri) {
        m_iri = iri;
    }

    public String getIRIString() {
        return m_iri.getIRIString();
    }

    public IRI getIRI() {
        return m_iri;
    }

    @Override
    public String toString(Prefixes prefixes) {
        return m_iri.toString(prefixes);
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        return m_iri.toString(prefixes);
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static Clazz create(String iriString) {
        if (iriString.charAt(0) == '<') iriString = iriString.substring(1, iriString.length() - 1);
        return create(IRI.create(iriString));
    }

    public static Clazz create(IRI iri) {
        return s_interningManager.intern(new Clazz(iri));
    }

    public Identifier getIdentifier() {
        return m_iri;
    }

    public String getIdentifierString() {
        return m_iri.getIRIString();
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
        return new HashSet<Variable>();
    }

    @Override
    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        return this;
    }
}
