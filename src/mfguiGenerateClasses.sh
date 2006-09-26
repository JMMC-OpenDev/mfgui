#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: mfguiGenerateClasses.sh,v 1.2 2006-09-26 12:44:59 mella Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.1  2006/09/26 12:13:07  mella
# First revision
#
#*******************************************************************************

#/**
# @file
# brief Generates classes from xsd using castor.
# 
# */


# signal trap (if any)



#___oOo___
#!/bin/bash


# Generate the class path for Java
CLASSPATH=`mkfMakeJavaClasspath`

MODEL_SCHEMA=$(miscLocateFile mfmdl.xsd)
# generate model java source from xml schema
echo "Generating classes for $MODEL_SCHEMA"
java -classpath $CLASSPATH org.exolab.castor.builder.SourceGenerator -i ${MODEL_SCHEMA} -f -package jmmc.mf.models  $*

exit 0

cat >> jmmc/mf/models/Model.java << EOM
 /**
     * Returns one string descrition.
     */
    public String toString(){
        return getName()+"["+getType()+"]";
    }

    /**
     * Returns one copy of the model (clone).
     */
    public Model getClone(){
         
         Model m = null;
         try{
           m = (Model) this.clone();
           Parameter[] params=getParameter();
           m.setParameter(params);                      
        }catch(Exception e){
            e.printStackTrace();
        }
        return m;
    }

, Cloneable

EOM
