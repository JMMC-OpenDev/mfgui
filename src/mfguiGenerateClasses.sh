#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: mfguiGenerateClasses.sh,v 1.9 2008-11-20 12:48:01 mella Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.8  2008/02/20 18:30:23  mella
# Jalopization on 1.0.7beta
#
# Revision 1.7  2007/02/14 17:05:39  mella
# make it run on macos
#
# Revision 1.6  2007/02/14 09:29:52  mella
# Moved jmmc.* into fr.jmmc.*
#
# Revision 1.5  2007/02/12 14:14:15  mella
# Add message for every class
#
# Revision 1.4  2006/11/22 14:51:13  mella
# Add toString() to File and Target
#
# Revision 1.3  2006/09/28 09:41:36  mella
# add engine param list generation
#
# Revision 1.2  2006/09/26 12:44:59  mella
# Search model.xsd using miscLocateFile
#
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

#MODEL_SCHEMA=$(miscLocateFile mfmdl.xsd)
MODEL_SCHEMA=../config/table.xsd
# generate model java source from xml schema
echo "Generating classes for $MODEL_SCHEMA"
java -classpath $(mkfMakeJavaClasspath) org.exolab.castor.builder.SourceGenerator -i ${MODEL_SCHEMA} -f -package fr.jmmc.mcs.castor  $*


#MODEL_SCHEMA=$(miscLocateFile mfmdl.xsd)
MODEL_SCHEMA=../config/mfmdl.xsd
# generate model java source from xml schema
echo "Generating classes for $MODEL_SCHEMA"
java -classpath $(mkfMakeJavaClasspath) org.exolab.castor.builder.SourceGenerator -i ${MODEL_SCHEMA} -f -package fr.jmmc.mf.models  $*

for f in fr/jmmc/mf/models/*.java
do
    tmp=${f%%.java}
    className=${tmp##*/}
    if [ "$className" == "File" ]
    then
        toString="\"File[\"+getName()+\"]\";"
    elif [ "$className" == "FileLink" ]
    then
        toString="\"FileLink[\"+((File)getFileRef()).getName()+\"]\";"
    elif [ "$className" == "Parameter" ]
    then
        toString="\"Parameter[\"+getName()+\"]\";"
    elif [ "$className" == "ParameterLink" ]
    then
        toString="\"ParameterLink[\"+((Parameter)getParameterRef()).getName()+\"]\";"
    elif [ "$className" == "Target" ]
    then
        toString="\"Target[\"+getIdent()+\"]\";"
    elif [ "$className" == "Model" ]
    then
        toString="\"Model[\"+getType()+\":\"+getName()+\"]\";"
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
