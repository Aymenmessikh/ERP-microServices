package com.example.auditlogservice.Config.filter.creator;

public class AttribueCreator {
    static public String createAttribute(String field){
        String[] args =  field.split("\\.");
        String attribute = args[args.length-1];
        return attribute;
    }
}
