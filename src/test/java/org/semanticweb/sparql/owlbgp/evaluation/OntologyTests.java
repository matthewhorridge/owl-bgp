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

package org.semanticweb.sparql.owlbgp.evaluation;

import java.util.HashMap;

import junit.framework.TestCase;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.sparql.arq.OWLOntologyDataSet;
import org.semanticweb.sparql.arq.OWLOntologyGraph;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;

public class OntologyTests extends TestCase {
    public static final String LB = System.getProperty("line.separator") ;
    
    protected final OWLOntologyManager manager;
    protected final OWLDataFactory factory;
    protected OWLOntology ontology;
    protected OWLOntologyDataSet dataset;
    
    public OntologyTests() {
        this(null);
    }
    public OntologyTests(String name) {
        super(name);
        manager = OWLManager.createOWLOntologyManager();
        factory=manager.getOWLDataFactory();
    }

    public void testOWLThingNothing() throws Exception {
        ontology=manager.createOntology();
        OWLClass top=factory.getOWLThing();
        OWLClass bot=factory.getOWLNothing();
        OWLClass c=factory.getOWLClass(IRI.create("http://ex.org/c"));
        OWLObjectProperty r=factory.getOWLObjectProperty(IRI.create("http://ex.org/r"));
        OWLAxiom ax=factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(c, top), factory.getOWLObjectSomeValuesFrom(r, bot));
        manager.addAxiom(ontology, ax);
        dataset=new OWLOntologyDataSet(ontology, new HashMap<String, OWLOntology>());
        OWLOntologyGraph graph=dataset.getDefaultGraph();
        assertTrue(graph.getClassesInSignature().contains(Clazz.THING));
        assertTrue(graph.getClassesInSignature().contains(Clazz.NOTHING));
    }
    public void testOWLTopBotObjectProperty() throws Exception {
        ontology=manager.createOntology();
        OWLObjectProperty top=factory.getOWLTopObjectProperty();
        OWLObjectProperty bot=factory.getOWLBottomObjectProperty();
        OWLClass c=factory.getOWLClass(IRI.create("http://ex.org/c"));
        OWLAxiom ax=factory.getOWLSubClassOfAxiom(factory.getOWLObjectSomeValuesFrom(top, c), factory.getOWLObjectSomeValuesFrom(bot, c));
        manager.addAxiom(ontology, ax);
        dataset=new OWLOntologyDataSet(ontology, new HashMap<String, OWLOntology>());
        OWLOntologyGraph graph=dataset.getDefaultGraph();
        assertTrue(graph.getObjectPropertiesInSignature().contains(ObjectProperty.TOP_OBJECT_PROPERTY));
        assertTrue(graph.getObjectPropertiesInSignature().contains(ObjectProperty.BOTTOM_OBJECT_PROPERTY));
    }
    public void testOWLTopBotDataProperty() throws Exception {
        ontology=manager.createOntology();
        OWLDataProperty top=factory.getOWLTopDataProperty();
        OWLDataProperty bot=factory.getOWLBottomDataProperty();
        OWLAxiom ax=factory.getOWLSubDataPropertyOfAxiom(bot, top);
        manager.addAxiom(ontology, ax);
        dataset=new OWLOntologyDataSet(ontology, new HashMap<String, OWLOntology>());
        OWLOntologyGraph graph=dataset.getDefaultGraph();
        assertTrue(graph.getDataPropertiesInSignature().contains(DataProperty.TOP_DATA_PROPERTY));
        assertTrue(graph.getDataPropertiesInSignature().contains(DataProperty.BOTTOM_DATA_PROPERTY));
    }
}