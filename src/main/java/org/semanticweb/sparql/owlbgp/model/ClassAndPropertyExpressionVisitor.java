package org.semanticweb.sparql.owlbgp.model;

import org.semanticweb.sparql.owlbgp.model.classexpressions.ClassVariable;
import org.semanticweb.sparql.owlbgp.model.classexpressions.Clazz;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.DataSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectAllValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectComplementOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectExactCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasSelf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectHasValue;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectIntersectionOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMaxCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectMinCardinality;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectOneOf;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectSomeValuesFrom;
import org.semanticweb.sparql.owlbgp.model.classexpressions.ObjectUnionOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectInverseOf;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectProperty;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyChain;
import org.semanticweb.sparql.owlbgp.model.properties.ObjectPropertyVariable;

public interface ClassAndPropertyExpressionVisitor {

    void visit(Clazz clazz);

    void visit(ClassVariable classVariable);

    void visit(ObjectProperty objectProperty);

    void visit(ObjectInverseOf objectproperty);

    void visit(ObjectPropertyChain objectProperty);

    void visit(ObjectPropertyVariable objectPropertyVariable);

    void visit(ObjectIntersectionOf objectIntersectionOf);

    void visit(ObjectUnionOf objectUnionOf);

    void visit(ObjectComplementOf objectComplementOf);

    void visit(ObjectSomeValuesFrom objectSomeValuesFrom);

    void visit(ObjectAllValuesFrom objectAllValuesFrom);

    void visit(ObjectHasValue objectHasValue);

    void visit(ObjectMinCardinality objectMinCardinality);

    void visit(ObjectExactCardinality objectExactCardinality);

    void visit(ObjectMaxCardinality objectMaxCardinality);

    void visit(ObjectHasSelf objectHasSelf);

    void visit(ObjectOneOf objectOneOf);

    void visit(DataSomeValuesFrom dataSomeValuesFrom);

    void visit(DataAllValuesFrom dataAllValuesFrom);

    void visit(DataHasValue dataHasValue);

    void visit(DataMinCardinality dataMinCardinality);

    void visit(DataExactCardinality dataExactCardinality);

    void visit(DataMaxCardinality dataMaxCardinality);

}
