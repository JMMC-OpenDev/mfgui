#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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

#MODEL_SCHEMA=$(miscLocateFile mfmdl.xsd)
MODEL_SCHEMA=fr/jmmc/mf/mfmdl.xsd
# generate model java source from xml schema
echo "Generating classes for $MODEL_SCHEMA"
java -classpath $(mkfMakeJavaClasspath) org.exolab.castor.builder.SourceGenerator -types j2 -i ${MODEL_SCHEMA} -f -package fr.jmmc.mf.models  -verbose $*

for f in fr/jmmc/mf/models/*.java
do
    tmp=${f%%.java}
    className=${tmp##*/}
    if [ "$className" == "File" ]
    then
        toString="\"File[\"+getName()+\"]\";"
    elif [ "$className" == "FileLink" ]
    then
        toString="\"File[\"+((File)getFileRef()).getName()+\"]\";"
    elif [ "$className" == "Parameters" ]
    then
        toString="\"Shared parameters[\"+getParameterCount()+\"]\";"
    elif [ "$className" == "Parameter" ]
    then
        toString="\"Parameter[\"+getName()+\"]\";"
    elif [ "$className" == "Residual" ]
    then
        toString="getName();"
    elif [ "$className" == "ParameterLink" ]
    then
        toString="\"ParameterLink[\"+((Parameter)getParameterRef()).getName()+\"]\";"
    elif [ "$className" == "Target" ]
    then
        toString="\"Target[\"+getIdent()+\"]\";"
    elif [ "$className" == "Responses" ]
    then
        toString="\"Plots\";"
    elif [ "$className" == "Response" ]
    then
        toString="getName();"
    elif [ "$className" == "Model" ]
    then
        toString="\"Model[\"+getType()+\":\"+getName()+\"]\";"
        toString="getName();"
    else
        toString="\"$className\";"
    fi
    echo "'$className' class uses toString: $toString" 
    
    tmp=$(mktemp tmpXX)
    nbLines=( $( wc -l $f ))
    let b1=$nbLines-1
    head -$b1 $f > $tmp
    echo "    public String toString(){ return $toString } " >> $tmp
    echo "}" >> $tmp
    if ! cp -af $tmp $f
    then
        cp -pf $tmp $f
    fi
    rm $tmp
done

cat << EOM
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
