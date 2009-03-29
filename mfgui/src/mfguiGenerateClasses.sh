#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: mfguiGenerateClasses.sh,v 1.18 2009-03-21 08:47:38 mella Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.17  2009/03/20 09:43:46  mella
# update for castor 1.3
#
# Revision 1.16  2009/03/20 06:30:29  mella
# Change due to castor 1.3
#
# Revision 1.15  2009/03/12 09:29:05  mella
# add all xsl of fr.jmmc.mf dir
#
# Revision 1.14  2009/02/24 13:00:11  mella
# typo
#
# Revision 1.13  2009/02/24 12:57:54  mella
# Change label for shared Params
#
# Revision 1.12  2009/01/14 09:36:26  mella
# change xsd path
#
# Revision 1.11  2009/01/13 15:47:16  mella
# move xsd file under java source area
#
# Revision 1.10  2009/01/07 16:13:13  mella
# change Model string representation
#
# Revision 1.9  2008/11/20 12:48:01  mella
# use mkfGenerateJavaClassPath
#
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
    elif [ "$className" == "ParameterLink" ]
    then
        toString="\"ParameterLink[\"+((Parameter)getParameterRef()).getName()+\"]\";"
    elif [ "$className" == "Target" ]
    then
        toString="\"Target[\"+getIdent()+\"]\";"
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