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


package org.semanticweb.sparql.owlbgp.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.sparql.owlbgp.model.Variable.VarType;
import org.semanticweb.sparql.owlbgp.model.axioms.Axiom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.dataranges.Datatype;
import org.semanticweb.sparql.owlbgp.model.dataranges.DatatypeVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.IndividualVariable;
import org.semanticweb.sparql.owlbgp.model.individuals.NamedIndividual;
import org.semanticweb.sparql.owlbgp.model.literals.LiteralVariable;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationProperty;
import org.semanticweb.sparql.owlbgp.model.properties.AnnotationPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.DataProperty;
import org.semanticweb.sparql.owlbgp.model.properties.DataPropertyVariable;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;
import org.semanticweb.sparql.owlbgp.parser.Vocabulary;

public class Ontology extends AbstractExtendedOWLObject {
    private static final long serialVersionUID = 470208281332338403L;

    protected static InterningManager<Ontology> s_interningManager = new InterningManager<Ontology>() {
        protected boolean equal(Ontology object1, Ontology object2) {
            if (object1.m_IRI != object2.m_IRI
                    || object1.m_versionIRI != object2.m_versionIRI
                    || object1.m_directlyImported.size() != object2.m_directlyImported.size()
                    || object1.m_axioms.size() != object2.m_axioms.size()
                    || object1.m_annotations.size() != object2.m_annotations.size()
                    || object1.m_classesInSignature.size() != object2.m_classesInSignature.size()
                    || object1.m_datatypesInSignature.size() != object2.m_datatypesInSignature.size()
                    || object1.m_objectPropertiesInSignature.size() != object2.m_objectPropertiesInSignature.size()
                    || object1.m_dataPropertiesInSignature.size() != object2.m_dataPropertiesInSignature.size()
                    || object1.m_annotationPropertiesInSignature.size() != object2.m_annotationPropertiesInSignature.size()
                    || object1.m_individualsInSignature.size() != object2.m_individualsInSignature.size()
                    || object1.m_classVariablesInSignature.size() != object2.m_classVariablesInSignature.size()
                    || object1.m_datatypeVariablesInSignature.size() != object2.m_datatypeVariablesInSignature.size()
                    || object1.m_objectPropertyVariablesInSignature.size() != object2.m_objectPropertyVariablesInSignature.size()
                    || object1.m_dataPropertyVariablesInSignature.size() != object2.m_dataPropertyVariablesInSignature.size()
                    || object1.m_annotationPropertyVariablesInSignature.size() != object2.m_annotationPropertyVariablesInSignature.size()
                    || object1.m_individualVariablesInSignature.size() != object2.m_individualVariablesInSignature.size()
                    || object1.m_literalVariablesInSignature.size() != object2.m_literalVariablesInSignature.size()
                    || object1.m_untypedVariablesInSignature.size() != object2.m_untypedVariablesInSignature.size()
                    )
                return false;
            for (Import imported : object1.m_directlyImported) {
                if (!contains(imported, object2.m_directlyImported))
                    return false;
            }
            for (Axiom ax : object1.m_axioms) {
                if (!contains(ax, object2.m_axioms))
                    return false;
            }
            for (Annotation anno : object1.m_annotations) {
                if (!contains(anno, object2.m_annotations))
                    return false;
            }
            for (Clazz cls : object1.m_classesInSignature) {
                if (!contains(cls, object2.m_classesInSignature))
                    return false;
            }
            for (Datatype dt : object1.m_datatypesInSignature) {
                if (!contains(dt, object2.m_datatypesInSignature))
                    return false;
            }
            for (ObjectProperty op : object1.m_objectPropertiesInSignature) {
                if (!contains(op, object2.m_objectPropertiesInSignature))
                    return false;
            }
            for (DataProperty dp : object1.m_dataPropertiesInSignature) {
                if (!contains(dp, object2.m_dataPropertiesInSignature))
                    return false;
            }
            for (AnnotationProperty ap : object1.m_annotationPropertiesInSignature) {
                if (!contains(ap, object2.m_annotationPropertiesInSignature))
                    return false;
            }
            for (NamedIndividual ind : object1.m_individualsInSignature) {
                if (!contains(ind, object2.m_individualsInSignature))
                    return false;
            }
            for (ClassVariable clsv : object1.m_classVariablesInSignature) {
                if (!contains(clsv, object2.m_classVariablesInSignature))
                    return false;
            }
            for (DatatypeVariable dtv : object1.m_datatypeVariablesInSignature) {
                if (!contains(dtv, object2.m_datatypeVariablesInSignature))
                    return false;
            }
            for (ObjectPropertyVariable opv : object1.m_objectPropertyVariablesInSignature) {
                if (!contains(opv, object2.m_objectPropertyVariablesInSignature))
                    return false;
            }
            for (DataPropertyVariable dpv : object1.m_dataPropertyVariablesInSignature) {
                if (!contains(dpv, object2.m_dataPropertyVariablesInSignature))
                    return false;
            }
            for (AnnotationPropertyVariable apv : object1.m_annotationPropertyVariablesInSignature) {
                if (!contains(apv, object2.m_annotationPropertyVariablesInSignature))
                    return false;
            }
            for (IndividualVariable indv : object1.m_individualVariablesInSignature) {
                if (!contains(indv, object2.m_individualVariablesInSignature))
                    return false;
            }
            for (LiteralVariable litv : object1.m_literalVariablesInSignature) {
                if (!contains(litv, object2.m_literalVariablesInSignature))
                    return false;
            }
            for (UntypedVariable v : object1.m_untypedVariablesInSignature) {
                if (!contains(v, object2.m_untypedVariablesInSignature))
                    return false;
            }
            return true;
        }

        protected boolean contains(Import imported, Set<Import> imports) {
            for (Import imp : imports)
                if (imported == imp) return true;
            return false;
        }

        protected boolean contains(Axiom ax, Set<Axiom> axioms) {
            for (Axiom axiom : axioms)
                if (ax == axiom) return true;
            return false;
        }

        protected boolean contains(Annotation annotation, Set<Annotation> annotations) {
            for (Annotation anno : annotations)
                if (anno == annotation) return true;
            return false;
        }

        protected boolean contains(Clazz cls, Set<Clazz> classes) {
            for (Clazz clazz : classes)
                if (clazz == cls) return true;
            return false;
        }

        protected boolean contains(Datatype dt, Set<Datatype> datatypes) {
            for (Datatype dt2 : datatypes)
                if (dt == dt2) return true;
            return false;
        }

        protected boolean contains(ObjectProperty o1, Set<ObjectProperty> objects) {
            for (ObjectProperty o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(DataProperty o1, Set<DataProperty> objects) {
            for (DataProperty o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(AnnotationProperty o1, Set<AnnotationProperty> objects) {
            for (AnnotationProperty o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(NamedIndividual o1, Set<NamedIndividual> objects) {
            for (NamedIndividual o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(ClassVariable o1, Set<ClassVariable> objects) {
            for (ClassVariable o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(DatatypeVariable o1, Set<DatatypeVariable> objects) {
            for (DatatypeVariable o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(ObjectPropertyVariable o1, Set<ObjectPropertyVariable> objects) {
            for (ObjectPropertyVariable o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(DataPropertyVariable o1, Set<DataPropertyVariable> objects) {
            for (DataPropertyVariable o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(AnnotationPropertyVariable o1, Set<AnnotationPropertyVariable> objects) {
            for (AnnotationPropertyVariable o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(IndividualVariable o1, Set<IndividualVariable> objects) {
            for (IndividualVariable o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(LiteralVariable o1, Set<LiteralVariable> objects) {
            for (LiteralVariable o : objects)
                if (o == o1) return true;
            return false;
        }

        protected boolean contains(UntypedVariable o1, Set<UntypedVariable> objects) {
            for (UntypedVariable o : objects)
                if (o == o1) return true;
            return false;
        }

        protected int getHashCode(Ontology object) {
            int hashCode = 1313;
            if (object.m_IRI != null) hashCode += object.m_IRI.hashCode();
            if (object.m_versionIRI != null) hashCode += object.m_versionIRI.hashCode();
            for (Import imported : object.m_directlyImported)
                hashCode += imported.hashCode();
            for (Axiom ax : object.m_axioms)
                hashCode += ax.hashCode();
            for (Annotation anno : object.m_annotations)
                hashCode += anno.hashCode();
            for (Clazz obj : object.m_classesInSignature)
                hashCode += obj.hashCode();
            for (Datatype obj : object.m_datatypesInSignature)
                hashCode += obj.hashCode();
            for (ObjectProperty obj : object.m_objectPropertiesInSignature)
                hashCode += obj.hashCode();
            for (DataProperty obj : object.m_dataPropertiesInSignature)
                hashCode += obj.hashCode();
            for (AnnotationProperty obj : object.m_annotationPropertiesInSignature)
                hashCode += obj.hashCode();
            for (NamedIndividual obj : object.m_individualsInSignature)
                hashCode += obj.hashCode();
            for (ClassVariable obj : object.m_classVariablesInSignature)
                hashCode += obj.hashCode();
            for (DatatypeVariable obj : object.m_datatypeVariablesInSignature)
                hashCode += obj.hashCode();
            for (ObjectPropertyVariable obj : object.m_objectPropertyVariablesInSignature)
                hashCode += obj.hashCode();
            for (DataPropertyVariable obj : object.m_dataPropertyVariablesInSignature)
                hashCode += obj.hashCode();
            for (AnnotationPropertyVariable obj : object.m_annotationPropertyVariablesInSignature)
                hashCode += obj.hashCode();
            for (IndividualVariable obj : object.m_individualVariablesInSignature)
                hashCode += obj.hashCode();
            for (LiteralVariable obj : object.m_literalVariablesInSignature)
                hashCode += obj.hashCode();
            for (UntypedVariable obj : object.m_untypedVariablesInSignature)
                hashCode += obj.hashCode();
            return hashCode;
        }
    };

    protected final Identifier m_IRI;
    protected final Identifier m_versionIRI;
    protected final Set<Import> m_directlyImported;
    protected final Set<Annotation> m_annotations;
    protected final Set<Axiom> m_axioms;
    protected final Set<Clazz> m_classesInSignature;
    protected final Set<Datatype> m_datatypesInSignature;
    protected final Set<ObjectProperty> m_objectPropertiesInSignature;
    protected final Set<DataProperty> m_dataPropertiesInSignature;
    protected final Set<AnnotationProperty> m_annotationPropertiesInSignature;
    protected final Set<NamedIndividual> m_individualsInSignature;
    protected final Set<ClassVariable> m_classVariablesInSignature;
    protected final Set<DatatypeVariable> m_datatypeVariablesInSignature;
    protected final Set<ObjectPropertyVariable> m_objectPropertyVariablesInSignature;
    protected final Set<DataPropertyVariable> m_dataPropertyVariablesInSignature;
    protected final Set<AnnotationPropertyVariable> m_annotationPropertyVariablesInSignature;
    protected final Set<IndividualVariable> m_individualVariablesInSignature;
    protected final Set<LiteralVariable> m_literalVariablesInSignature;
    protected final Set<UntypedVariable> m_untypedVariablesInSignature;

    protected Ontology(Identifier ontologyIRI, Identifier versionIRI, Set<Import> directlyImports, Set<Axiom> axioms, Set<Annotation> ontologyAnnotations) {
        m_IRI = ontologyIRI;
        m_versionIRI = versionIRI;
        m_directlyImported = Collections.unmodifiableSet(directlyImports == null ? new HashSet<Import>() : directlyImports);
        m_axioms = Collections.unmodifiableSet(axioms == null ? new HashSet<Axiom>() : axioms);
        m_annotations = Collections.unmodifiableSet(ontologyAnnotations == null ? new HashSet<Annotation>() : ontologyAnnotations);
        SignatureExtractor signatureExtractor = new SignatureExtractor();
        if (ontologyIRI != null)
            ontologyIRI.accept(signatureExtractor);
        if (versionIRI != null)
            versionIRI.accept(signatureExtractor);
        for (Import directImport : m_directlyImported)
            directImport.accept(signatureExtractor);
        for (Axiom ax : m_axioms)
            ax.accept(signatureExtractor);
        for (Annotation annotation : m_annotations)
            annotation.accept(signatureExtractor);
        m_classesInSignature = Collections.unmodifiableSet(signatureExtractor.getClassesInSignature());
        m_datatypesInSignature = Collections.unmodifiableSet(signatureExtractor.getDatatypesInSignature());
        m_objectPropertiesInSignature = Collections.unmodifiableSet(signatureExtractor.getObjectPropertiesInSignature());
        m_dataPropertiesInSignature = Collections.unmodifiableSet(signatureExtractor.getDataPropertiesInSignature());
        m_annotationPropertiesInSignature = Collections.unmodifiableSet(signatureExtractor.getAnnotationPropertiesInSignature());
        m_individualsInSignature = Collections.unmodifiableSet(signatureExtractor.getIndividualsInSignature());
        m_classVariablesInSignature = Collections.unmodifiableSet(signatureExtractor.getClassVariablesInSignature());
        m_datatypeVariablesInSignature = Collections.unmodifiableSet(signatureExtractor.getDatatypeVariablesInSignature());
        m_objectPropertyVariablesInSignature = Collections.unmodifiableSet(signatureExtractor.getObjectPropertyVariablesInSignature());
        m_dataPropertyVariablesInSignature = Collections.unmodifiableSet(signatureExtractor.getDataPropertyVariablesInSignature());
        m_annotationPropertyVariablesInSignature = Collections.unmodifiableSet(signatureExtractor.getAnnotationPropertyVariablesInSignature());
        m_individualVariablesInSignature = Collections.unmodifiableSet(signatureExtractor.getIndividualVariablesInSignature());
        m_literalVariablesInSignature = Collections.unmodifiableSet(signatureExtractor.getLiteralVariablesInSignature());
        m_untypedVariablesInSignature = Collections.unmodifiableSet(signatureExtractor.getUntypedVariablesInSignature());
    }

    protected Ontology(Identifier ontologyIRI, Identifier versionIRI, Set<Import> directlyImports, Set<Axiom> axioms, Set<Annotation> ontologyAnnotations,
                       Set<Clazz> classesInSignature, Set<Datatype> datatypesInSignature, Set<ObjectProperty> objectPropertiesInSignature,
                       Set<DataProperty> dataPropertiesInSignature, Set<AnnotationProperty> annotationPropertiesInSignature, Set<NamedIndividual> individualsInSignature,
                       Set<ClassVariable> classVariablesInSignature, Set<DatatypeVariable> datatypeVariablesInSignature,
                       Set<ObjectPropertyVariable> objectPropertyVariablesInSignature, Set<DataPropertyVariable> dataPropertyVariablesInSignature,
                       Set<AnnotationPropertyVariable> annotationPropertyVariablesInSignature, Set<IndividualVariable> individualVariablesInSignature,
                       Set<LiteralVariable> literalVariablesInSignature, Set<UntypedVariable> untypedVariablesInSignature) {
        m_IRI = ontologyIRI;
        m_versionIRI = versionIRI;
        m_directlyImported = Collections.unmodifiableSet(directlyImports == null ? new HashSet<Import>() : directlyImports);
        m_axioms = Collections.unmodifiableSet(axioms == null ? new HashSet<Axiom>() : axioms);
        m_annotations = Collections.unmodifiableSet(ontologyAnnotations == null ? new HashSet<Annotation>() : ontologyAnnotations);
        m_classesInSignature = Collections.unmodifiableSet(classesInSignature);
        m_datatypesInSignature = Collections.unmodifiableSet(datatypesInSignature);
        m_objectPropertiesInSignature = Collections.unmodifiableSet(objectPropertiesInSignature);
        m_dataPropertiesInSignature = Collections.unmodifiableSet(dataPropertiesInSignature);
        m_annotationPropertiesInSignature = Collections.unmodifiableSet(annotationPropertiesInSignature);
        m_individualsInSignature = Collections.unmodifiableSet(individualsInSignature);
        m_classVariablesInSignature = Collections.unmodifiableSet(classVariablesInSignature);
        m_datatypeVariablesInSignature = Collections.unmodifiableSet(datatypeVariablesInSignature);
        m_objectPropertyVariablesInSignature = Collections.unmodifiableSet(objectPropertyVariablesInSignature);
        m_dataPropertyVariablesInSignature = Collections.unmodifiableSet(dataPropertyVariablesInSignature);
        m_annotationPropertyVariablesInSignature = Collections.unmodifiableSet(annotationPropertyVariablesInSignature);
        m_individualVariablesInSignature = Collections.unmodifiableSet(individualVariablesInSignature);
        m_literalVariablesInSignature = Collections.unmodifiableSet(literalVariablesInSignature);
        m_untypedVariablesInSignature = Collections.unmodifiableSet(untypedVariablesInSignature);
    }

    public Set<Clazz> getClassesInSignature() {
        return m_classesInSignature;
    }

    public Set<Datatype> getDatatypesInSignature() {
        return m_datatypesInSignature;
    }

    public Set<ObjectProperty> getObjectPropertiesInSignature() {
        return m_objectPropertiesInSignature;
    }

    public Set<DataProperty> getDataPropertiesInSignature() {
        return m_dataPropertiesInSignature;
    }

    public Set<AnnotationProperty> getAnnotationPropertiesInSignature() {
        return m_annotationPropertiesInSignature;
    }

    public Set<NamedIndividual> getIndividualsInSignature() {
        return m_individualsInSignature;
    }

    public Set<ClassVariable> getClassVariablesInSignature() {
        return m_classVariablesInSignature;
    }

    public Set<DatatypeVariable> getDatatypeVariablesInSignature() {
        return m_datatypeVariablesInSignature;
    }

    public Set<ObjectPropertyVariable> getObjectPropertyVariablesInSignature() {
        return m_objectPropertyVariablesInSignature;
    }

    public Set<DataPropertyVariable> getDataPropertyVariablesInSignature() {
        return m_dataPropertyVariablesInSignature;
    }

    public Set<AnnotationPropertyVariable> getAnnotationPropertyVariablesInSignature() {
        return m_annotationPropertyVariablesInSignature;
    }

    public Set<IndividualVariable> getIndividualVariablesInSignature() {
        return m_individualVariablesInSignature;
    }

    public Set<LiteralVariable> getLiteralVariablesInSignature() {
        return m_literalVariablesInSignature;
    }

    public Set<UntypedVariable> getUntypedVariablesInSignature() {
        return m_untypedVariablesInSignature;
    }

    public boolean containsAxiom(Axiom axiom) {
        return m_axioms.contains(axiom);
    }

    public boolean containsImport(Import imported) {
        return m_directlyImported.contains(imported);
    }

    public boolean hasVersionIRI(Identifier versionIRI) {
        return m_versionIRI.equals(versionIRI);
    }

    public boolean containsAnnotation(Annotation annotation) {
        return m_annotations.contains(annotation);
    }

    public Identifier getOntologyIRI() {
        return m_IRI;
    }

    public Identifier getVersionIRI() {
        return m_versionIRI;
    }

    public Set<Import> getDirectImports() {
        return m_directlyImported;
    }

    public Set<Annotation> getAnnotations() {
        return m_annotations;
    }

    public Set<Axiom> getAxioms() {
        return m_axioms;
    }

    @Override
    public String toString(Prefixes prefixes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Ontology(");
        if (m_IRI != null) buffer.append(m_IRI + LB);
        if (m_versionIRI != null) buffer.append(m_versionIRI + LB);
        buffer.append(LB);
        for (Import imported : m_directlyImported) {
            buffer.append(imported.toString(prefixes));
            buffer.append(LB);
        }
        for (Annotation annotation : m_annotations) {
            buffer.append(annotation.toString(prefixes));
            buffer.append(LB);
        }
        for (Axiom ax : m_axioms) {
            buffer.append(ax.toString(prefixes));
            buffer.append(LB);
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public String toTurtleString(Prefixes prefixes, Identifier mainNode) {
        StringBuffer buffer = new StringBuffer();
        Identifier ontologyIdentifier;
        if (m_IRI != null) ontologyIdentifier = m_IRI;
        else ontologyIdentifier = AbstractExtendedOWLObject.getNextBlankNode();
        buffer.append(ontologyIdentifier.toString(prefixes));
        buffer.append(" rdf:type owl:Ontology . ");
        buffer.append(LB);
        if (m_versionIRI != null) {
            buffer.append(ontologyIdentifier.toString(prefixes));
            buffer.append(" ");
            buffer.append(Vocabulary.OWL_VERSION_IRI);
            buffer.append(" ");
            buffer.append(m_versionIRI.toString(prefixes));
            buffer.append(" . ");
            buffer.append(LB);
        }
        for (Import imported : m_directlyImported) {
            buffer.append(imported.toTurtleString(prefixes, ontologyIdentifier));
            buffer.append(LB);
        }
        for (Annotation annotation : m_annotations) {
            buffer.append(annotation.toTurtleString(prefixes, ontologyIdentifier));
            buffer.append(LB);
        }
        for (Axiom ax : m_axioms) {
            buffer.append(ax.toString(prefixes));
            buffer.append(LB);
        }
        return buffer.toString();
    }

    protected Object readResolve() {
        return s_interningManager.intern(this);
    }

    public static Ontology create(Identifier ontologyIRI, Identifier versionIRI, Set<Import> directlyImports, Set<Axiom> axioms, Set<Annotation> ontologyAnnotations) {
        return s_interningManager.intern(new Ontology(ontologyIRI, versionIRI, directlyImports, axioms, ontologyAnnotations));
    }

    public Set<Variable> getVariablesInSignature(VarType varType) {
        Set<Variable> variables = new HashSet<Variable>();
        if (m_IRI != null)
            variables.addAll(m_IRI.getVariablesInSignature(varType));
        if (m_versionIRI != null)
            variables.addAll(m_versionIRI.getVariablesInSignature(varType));
        for (Import imported : m_directlyImported)
            variables.addAll(imported.getVariablesInSignature(varType));
        for (Axiom ax : m_axioms)
            variables.addAll(ax.getVariablesInSignature(varType));
        for (Annotation anno : m_annotations)
            variables.addAll(anno.getVariablesInSignature(varType));
        return variables;
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

    public ExtendedOWLObject getBoundVersion(Map<Variable, ? extends Atomic> variablesToBindings) {
        Identifier boundIRI = (m_IRI != null ? (Identifier) m_IRI.getBoundVersion(variablesToBindings) : null);
        Identifier boundVersionIRI = (m_versionIRI != null ? (Identifier) m_versionIRI.getBoundVersion(variablesToBindings) : null);
        Set<Import> boundImports = new HashSet<Import>();
        for (Import imported : m_directlyImported)
            boundImports.add((Import) imported.getBoundVersion(variablesToBindings));
        Set<Axiom> boundAxioms = new HashSet<Axiom>();
        for (Axiom ax : m_axioms)
            boundAxioms.add((Axiom) ax.getBoundVersion(variablesToBindings));
        Set<Annotation> boundAnnotations = new HashSet<Annotation>();
        for (Annotation anno : m_annotations)
            boundAnnotations.add((Annotation) anno.getBoundVersion(variablesToBindings));
        return create(boundIRI, boundVersionIRI, boundImports, boundAxioms, boundAnnotations);
    }
}