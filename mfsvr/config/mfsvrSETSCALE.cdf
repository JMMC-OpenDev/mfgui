<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrSETSCALE.cdf,v 1.2 2006-07-04 08:27:09 lsauge Exp $"

 History
 ~~~~~~~
 $Log: not supported by cvs2svn $
 Revision 1.1  2006/06/27 09:05:42  lsauge
 Implementation of callback


paramType is the parameter type, and can be : boolean integer double or string
-->
<cmd
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:noNamespaceSchemaLocation="cmdDefinitionFile.xsd">
    <mnemonic>SETSCALE</mnemonic>
    <desc>SetScale description</desc>
    <params>
        <param optional="true">
            <name>reset</name>
            <type>boolean</type>
            <desc>set all scaling facor to 1</desc>
        </param>
        <param optional="true">
            <name>auto</name>
            <type>boolean</type>
            <desc>automatic scaling</desc>
        </param>
        <param optional="method">
            <name>method</name>
            <type>integer</type>
            <minValue><integer>0</integer></minValue>
            <maxValue><integer>1</integer></maxValue>
            <defaultValue><integer>0</integer></defaultValue>
            <desc>automatic scaling method</desc>
        </param>
    </params>        
</cmd>
