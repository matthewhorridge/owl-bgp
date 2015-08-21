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


package org.semanticweb.sparql.owlbgp.model.properties;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.AbstractExtendedOWLObject;
import org.semanticweb.sparql.owlbgp.model.Atomic;
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


public class ObjectProperty extends AbstractExtendedOWLObject implements ObjectPropertyExpression, Atomic {
    private static final long serialVersionUID = 6601006990377858121L;

    protected static InterningManager<ObjectProperty> s_interningManager = new InterningManager<ObjectProperty>() {
        protected boolean equal(ObjectProperty object1, ObjectProperty object2) {
            return object1.m_iri == object2.m_iri;
        }

        protected int getHashCode(ObjectProperty object) {
            return object.m_iri.hashCode();
        }
    };

    public static final ObjectProperty TOP_OBJECT_PROPERTY = create(IRI.create("http://www.w3.org/2002/07/owl#topObjectProperty"));
    public static final ObjectProperty BOTTOM_OBJECT_PROPERTY = create(IRI.create("http://www.w3.org/2002/07/owl#bottomObjectProperty"));

    protected final IRI m_iri;

    protected ObjectProperty(IRI iri) {
        m_iri = iri;
    }

    public IRI getIRI() {
        return m_iri;
    }

    public String getIRIString() {
        return m_iri.getIRIString();
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

    public static ObjectProperty create(String iriString) {
        if (iriString.charAt(0) == '<') iriString = iriString.substring(1, iriString.length() - 1);
        return create(IRI.create(iriString));
    }

    public static ObjectProperty create(IRI iri) {
        return s_interningManager.intern(new ObjectProperty(iri));
    }

    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    public void accept(ExtendedOWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    public <O> O accept(ClassAndPropertyExpressionVisitorEx<O> visitor) {
        return visitor.visit(this);
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

    public Identifier getIdentifier() {
        return m_iri;
    }

    public String getIdentifierString() {
        return m_iri.getIRIString();
    }

    public ObjectPropertyExpression getNormalized() {
        return this;
    }
}
