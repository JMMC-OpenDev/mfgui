<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrSETPARAMETER.cdf,v 1.1 2006-06-27 09:05:42 lsauge Exp $"

 History
 ~~~~~~~
 $Log: not supported by cvs2svn $

paramType is the parameter type, and can be : boolean integer double or string
-->
<cmd
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:noNamespaceSchemaLocation="cmdDefinitionFile.xsd">
    <mnemonic>SETPARAMETER</mnemonic>
    <desc>SetParameter description</desc>
    <params optional="true">
        <param optional="false">
            <name>index</name>
            <type>integer</type>
            <minValue><integer>0</integer></minValue>
            <desc>Index value</desc>
        </param>
        <param optional="true">
            <name>value</name>
            <type>double</type>
            <desc>parameter value</desc>
        </param>
        <param optional="true">
            <name>min</name>
            <type>double</type>
            <desc>min parameter value</desc>
        </param>
        <param optional="true">
            <name>max</name>
            <type>double</type>
            <desc>max parameter value</desc>
        </param>
        <param optional="true">
            <name>fixed</name>
            <type>boolean</type>
            <desc>is the parameter value fixed during the fit</desc>
        </param>
        <param optional="true">
            <name>unit</name>
            <type>string</type>
            <desc>unit</desc>
        </param>
        <param optional="true">
            <name>scale</name>
            <type>double</type>
            <desc>scale value</desc>
        </param>
    </params>        
</cmd>
