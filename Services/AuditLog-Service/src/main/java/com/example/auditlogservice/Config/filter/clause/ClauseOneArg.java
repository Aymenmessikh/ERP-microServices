package com.example.auditlogservice.Config.filter.clause;

import com.example.auditlogservice.Config.filter.creator.AttribueCreator;
import com.example.auditlogservice.Config.filter.creator.JoinCreator;
import com.example.auditlogservice.Config.filter.enums.Operation;
import com.example.auditlogservice.Config.filter.factory.ValueFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
@Setter
@Getter
public class ClauseOneArg extends Clause {

    private String arg;

    public ClauseOneArg(String filed, Operation operation, String arg) {
        super(filed, operation);
        this.arg = arg;
    }

    public static Predicate toPredicate(Root root , CriteriaBuilder criteriaBuilder , Clause clause) throws ParseException, ClassNotFoundException {
        ClauseOneArg clauseOneArg = (ClauseOneArg) clause;
        Join joinMap  = JoinCreator.createJoin(root , clause.getFiled());
        String attribute = AttribueCreator.createAttribute(clause.getFiled());
        try {
            switch (clauseOneArg.getOperation()){
                case NotEquals:
                    if(joinMap == null){
                        return criteriaBuilder.notEqual(criteriaBuilder.upper(root.get(attribute))  , ValueFactory.toValue(root, attribute ,clauseOneArg.getArg()).toString().toUpperCase());
                    }else {
                        return criteriaBuilder.notEqual(criteriaBuilder.upper(joinMap.get(attribute)) , ValueFactory.toValue(joinMap, attribute ,clauseOneArg.getArg()).toString().toUpperCase());
                    }
                case Endswith:
                    if(joinMap == null){
                        return criteriaBuilder.like(criteriaBuilder.upper(root.get(attribute))   , "%"+ValueFactory.toValue(root, attribute ,clauseOneArg.getArg()).toString().toUpperCase());
                    }else {
                        return criteriaBuilder.like(criteriaBuilder.upper(joinMap.get(attribute))  , "%"+ValueFactory.toValue(joinMap, attribute ,clauseOneArg.getArg()).toString().toUpperCase());
                    }
                case Startswith:
                    if(joinMap == null){
                        return criteriaBuilder.like(criteriaBuilder.upper(root.get(attribute))   , ValueFactory.toValue(root, attribute ,clauseOneArg.getArg()).toString().toUpperCase()+"%");
                    }else {
                        return criteriaBuilder.like(criteriaBuilder.upper(joinMap.get(attribute))  , clauseOneArg.getArg().toString().toUpperCase()+"%");
                    }
                case Contains:
                    if(joinMap == null){
                        return criteriaBuilder.like(criteriaBuilder.upper(root.get(attribute))  , "%"+ValueFactory.toValue(root, attribute ,clauseOneArg.getArg()).toString().toUpperCase()+"%");
                    }else {
                        return criteriaBuilder.like(criteriaBuilder.upper(joinMap.get(attribute))  , "%"+ValueFactory.toValue(joinMap, attribute ,clauseOneArg.getArg()).toString().toUpperCase()+"%");
                    }
                case Notcontains:
                    if(joinMap == null){
                        return criteriaBuilder.notLike(criteriaBuilder.upper(root.get(attribute)) , "%"+ValueFactory.toValue(root, attribute ,clauseOneArg.getArg()).toString().toUpperCase()+"%");
                    }else {
                        return criteriaBuilder.notLike(criteriaBuilder.upper(joinMap.get(attribute))   , "%"+ValueFactory.toValue(joinMap, attribute ,clauseOneArg.getArg()).toString().toUpperCase()+"%");
                    }
                case Less:
                    if(joinMap == null){
                        return criteriaBuilder.lessThan(root.get(attribute)  ,  (Comparable) ValueFactory.toValue(root, attribute ,clauseOneArg.getArg()));
                    }else {
                        return criteriaBuilder.lessThan(joinMap.get(attribute)  , (Comparable) ValueFactory.toValue(joinMap, attribute ,clauseOneArg.getArg()));
                    }

                case LessOrEquals:
                    if(joinMap == null){
                        return criteriaBuilder.lessThanOrEqualTo(root.get(attribute)  ,  (Comparable) ValueFactory.toValue(root, attribute ,clauseOneArg.getArg()));
                    }else {
                        return criteriaBuilder.lessThanOrEqualTo(joinMap.get(attribute)  , (Comparable) ValueFactory.toValue(joinMap, attribute ,clauseOneArg.getArg()));
                    }
                case Greater:
                    if(joinMap == null){
                        return criteriaBuilder.greaterThan(root.get(attribute)  , (Comparable) ValueFactory.toValue(root, attribute ,clauseOneArg.getArg()));
                    }else {
                        return criteriaBuilder.greaterThan(joinMap.get(attribute)  , (Comparable) ValueFactory.toValue(joinMap, attribute ,clauseOneArg.getArg()));
                    }
                case GreaterOrEquals:
                    if(joinMap == null){
                        return criteriaBuilder.greaterThanOrEqualTo(root.get(attribute)  , (Comparable) ValueFactory.toValue(root, attribute ,clauseOneArg.getArg()));
                    }else {
                        return criteriaBuilder.greaterThanOrEqualTo(joinMap.get(attribute)  , (Comparable) ValueFactory.toValue(joinMap, attribute ,clauseOneArg.getArg()));
                    }
                default:
                    if(joinMap == null){
                        return criteriaBuilder.equal(root.get(attribute)  , ValueFactory.toValue(root, attribute ,clauseOneArg.getArg()));
                    }else {
                        return criteriaBuilder.equal(joinMap.get(attribute)  , ValueFactory.toValue(joinMap, attribute ,clauseOneArg.getArg()));
                    }
            }
        }catch (IllegalArgumentException | ClassNotFoundException illegalArgumentException){
            throw  illegalArgumentException;
        }
    }
}
