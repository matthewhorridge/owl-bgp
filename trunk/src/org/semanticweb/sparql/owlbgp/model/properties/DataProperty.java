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


package  org.semanticweb.sparql.owlbgp.model.properties;

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

public class DataProperty extends AbstractExtendedOWLObject implements DataPropertyExpression, Atomic {
    private static final long serialVersionUID = 6601006990377858121L;

    protected static InterningManager<DataProperty> s_interningManager=new InterningManager<DataProperty>() {
        protected boolean equal(DataProperty object1,DataProperty object2) {
            return object1.m_iri==object2.m_iri;
        }
        protected int getHashCode(DataProperty object) {
            return object.m_iri.hashCode();
        }
    };
    
    public static final DataProperty TOP_DATA_PROPERTY=create(IRI.create("http://www.w3.org/2002/07/owl#topDataProperty"));
    public static final DataProperty BOTTOM_DATA_PROPERTY=create(IRI.create("http://www.w3.org/2002/07/owl#bottomDataProperty"));
    
    protected final IRI m_iri;
   
    protected DataProperty(IRI iri) {
        m_iri=iri;
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
    public String toTurtleString(Prefixes prefixes,Identifier mainNode) {
        return toString(prefixes);
    }
    protected Object readResolve() {
        return s_interningManager.intern(this);
    }
    public static DataProperty create(String iriString) {
        if (iriString.charAt(0)=='<') iriString=iriString.substring(1,iriString.length()-1);
        return create(IRI.create(iriString));
    }
    public static DataProperty create(IRI iri) {
        return s_interningManager.intern(new DataProperty(iri));
    }
    @Override
    public <O> O accept(ExtendedOWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }
    public void accept(ExtendedOWLObjectVisitor visitor) {
        visitor.visit(this);
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
    public ExtendedOWLObject getBoundVersion(Map<Variable,? extends Atomic> variablesToBindings) {
        return this;
    }
    public Identifier getIdentifier() {
        return m_iri;
    }
    public String getIdentifierString() {
        return m_iri.getIRIString();
    }
}
