<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrPLOTMODEL.cdf,v 1.3 2006-08-02 09:03:05 lsauge Exp $"

 History
 ~~~~~~~
 $Log: not supported by cvs2svn $
 Revision 1.2  2006/07/10 12:19:54  lsauge
 Add T3, split and errorbars flag (bispectrum plotting)
 Add output  flag (eps output)

 Revision 1.1  2006/06/27 09:05:42  lsauge
 Implementation of callback


paramType is the parameter type, and can be : boolean integer double or string
-->
<cmd
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:noNamespaceSchemaLocation="cmdDefinitionFile.xsd">
    <mnemonic>PLOTMODEL</mnemonic>
    <desc>PlotModel description</desc>
    <params>
        <param>
            <name>T3</name>
            <type>boolean</type>
            <defaultValue><boolean>false</boolean></defaultValue>
            <desc>Plot observed/computed bispectrum in polar frame</desc>
        </param>
        <param>
            <name>split</name>
            <type>boolean</type>
            <defaultValue><boolean>false</boolean></defaultValue>
            <desc>Split bispectrum representation in 2 differents frame as
            function as the index. Only relevant when T3 flag is set to true,
            and have no influence otherwise.
            </desc>
        </param>
        <param>
            <name>errorbars</name>
            <type>boolean</type>
            <defaultValue><boolean>true</boolean></defaultValue>
            <desc>Plot errorbars</desc>
        </param>
        <param>
            <name>output</name>
            <type>string</type>
            <defaultValue><string>none</string></defaultValue>
            <desc>Set ouput eps file</desc>
        </param>
    </params> 
</cmd>
