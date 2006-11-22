#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: mfguiGenerateClasses.sh,v 1.4 2006-11-22 14:51:13 mella Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
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
CLASSPATH=`mkfMakeJavaClasspath`

MODEL_SCHEMA=$(miscLocateFile mfmdl.xsd)
# generate model java source from xml schema
echo "Generating classes for $MODEL_SCHEMA"
java -classpath $CLASSPATH org.exolab.castor.builder.SourceGenerator -i ${MODEL_SCHEMA} -f -package jmmc.mf.models  $*


for f in jmmc/mf/models/*.java
do
    tmp=${f%%.java}
    className=${tmp##*/}

    if [ "$className" == "File" ]
    then
        toString="\"File[\"+getName()+\"]\";"
    elif [ "$className" == "Target" ]
    then
        toString="\"Target[\"+getIdent()+\"]\";"
    else
        toString="\"$className\";"
    fi
    
    tmp=$(mktemp tmpXX)
    nbLines=( $( wc -l $f ))
    let b1=$nbLines-1
    head -$b1 $f > $tmp
    echo "    public String toString(){ return $toString } " >> $tmp
    echo "}" >> $tmp
    cp -af $tmp $f
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
