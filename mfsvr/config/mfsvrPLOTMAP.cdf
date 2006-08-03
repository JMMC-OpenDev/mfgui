<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrPLOTMAP.cdf,v 1.2 2006-07-10 12:20:06 lsauge Exp $"

 History
 ~~~~~~~
 $Log: not supported by cvs2svn $
 Revision 1.1  2006/06/28 12:36:35  lsauge
 First implementation


paramType is the parameter type, and can be : boolean integer double or string
-->
<cmd
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:noNamespaceSchemaLocation="cmdDefinitionFile.xsd">
    <mnemonic>PLOTMAP</mnemonic>
    <desc>PlotMap description</desc>
    <params optional="true">
        <param>
            <name>umin</name>
            <type>double</type>
            <defaultValue><double>-50</double></defaultValue>
            <desc>u lower bound</desc>
        </param>
        <param>
            <name>umax</name>
            <type>double</type>
            <defaultValue><double>50</double></defaultValue>
            <desc>u upper bound</desc>
        </param>
        <param>
            <name>vmin</name>
            <type>double</type>
            <defaultValue><double>-50</double></defaultValue>
            <desc>v lower bound</desc>
        </param>
        <param>
            <name>vmax</name>
            <type>double</type>
            <defaultValue><double>50</double></defaultValue>
            <desc>v upper bound</desc>
        </param>
        <param>
            <name>lambda</name>
            <type>double</type>
            <defaultValue><double>1e-6</double></defaultValue>
            <desc>wavelength</desc>
        </param>
        <param>
            <name>surface</name>
            <type>boolean</type>
            <defaultValue><boolean>false</boolean></defaultValue>
            <desc>plot survace</desc>
        </param>
        <param>
            <name>output</name>
            <type>string</type>
            <defaultValue><string>none</string></defaultValue>
            <desc>Set ouput eps file</desc>
        </param>

    </params>
</cmd>
