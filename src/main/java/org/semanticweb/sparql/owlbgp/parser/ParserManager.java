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

package org.semanticweb.sparql.owlbgp.parser;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.sparql.owlbgp.model.Ontology;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;

public class ParserManager {

    public static Ontology parseBGP(String bgp) throws ParseException {
        return parseBGP(bgp, null, null, null, null, null, null);
    }

    public static Ontology parseBGP(String bgp, Ontology queriedOntology) throws ParseException {
        return parseBGP(bgp, queriedOntology.getClassesInSignature(), queriedOntology.getObjectPropertiesInSignature(),
                queriedOntology.getDataPropertiesInSignature(), queriedOntology.getAnnotationPropertiesInSignature(),
                queriedOntology.getDatatypesInSignature(), queriedOntology.getIndividualsInSignature());
    }

    public static Ontology parseBGP(String bgp, OWLOntology queriedOntology) throws ParseException {
        Set<Clazz> classes = new HashSet<Clazz>();
        Set<ObjectProperty> ops = new HashSet<ObjectProperty>();
        Set<DataProperty> dps = new HashSet<DataProperty>();
        Set<AnnotationProperty> aps = new HashSet<AnnotationProperty>();
        Set<Datatype> datatypes = new HashSet<Datatype>();
        Set<NamedIndividual> individuals = new HashSet<NamedIndividual>();
        if (queriedOntology != null) {
            for (OWLClass cls : queriedOntology.getClassesInSignature(true))
                classes.add(Clazz.create(cls.getIRI().toString()));
            for (OWLObjectProperty op : queriedOntology.getObjectPropertiesInSignature(true))
                ops.add(ObjectProperty.create(op.getIRI().toString()));
            for (OWLDataProperty dp : queriedOntology.getDataPropertiesInSignature(true))
                dps.add(DataProperty.create(dp.getIRI().toString()));
            for (OWLAnnotationProperty ap : queriedOntology.getAnnotationPropertiesInSignature())
                aps.add(AnnotationProperty.create(ap.getIRI().toString()));
            for (OWLDatatype dt : queriedOntology.getDatatypesInSignature(true))
                datatypes.add(Datatype.create(dt.getIRI().toString()));
            for (OWLNamedIndividual ind : queriedOntology.getIndividualsInSignature(true))
                individuals.add(NamedIndividual.create(ind.getIRI().toString()));
        }
        return parseBGP(bgp, classes, ops, dps, aps, datatypes, individuals);
    }

    public static Ontology parseBGP(String bgp, Set<Clazz> classes, Set<ObjectProperty> ops, Set<DataProperty> dps,
                                    Set<AnnotationProperty> aps, Set<Datatype> datatypes, Set<NamedIndividual> individuals) throws ParseException {
        OWLBGPParser parser = new OWLBGPParser(new StringReader(bgp));
        if (classes != null)
            parser.setClassesInOntologySignature(classes);
        if (ops != null)
            parser.setObjectPropertiesInOntologySignature(ops);
        if (dps != null)
            parser.setDataPropertiesInOntologySignature(dps);
        if (aps != null)
            parser.setAnnotationPropertiesInOntologySignature(aps);
        if (datatypes != null)
            parser.setCustomDatatypesInOntologySignature(datatypes);
        if (individuals != null)
            parser.setIndividualsInOntologySignature(individuals);
        parser.parse();
        return parser.getParsedOntology();
    }
}
