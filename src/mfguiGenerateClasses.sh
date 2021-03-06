#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
#*******************************************************************************

SCRIPTNAME=$(basename $0)
# if this file is sourced it does return the caller program name
SCRIPTBASE=$(dirname $0)/..
#make SCRIPTBASE fully qualified
SCRIPTBASE=$( cd $SCRIPTBASE && pwd )

cd $SCRIPTBASE

# generate model java source from xml schema
if ! mvn -Dcastor.schemaDirectory=$SCRIPTBASE/src/main/resources/fr/jmmc/mf -Dcastor.createJdoDescriptors=true  -Dcastor.packaging=fr.jmmc.mf.models  -Dcastor.dest=$SCRIPTBASE/src/main/java/ -Dcastor.types=j2 -X castor:generate
then 
    exit
fi


for f in $SCRIPTBASE/src/main/java/fr/jmmc/mf/models/*.java
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
        toString="getType();"
    else
        toString="\"$className\";"
    fi

    if grep "String toString" $f &> /dev/null
    then
        echo "toString already appended to $f"
    else
        tmp=$(mktemp tmpXXXXX)
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
        echo "'$className' class uses toString: $toString" 
    fi
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

cd -
