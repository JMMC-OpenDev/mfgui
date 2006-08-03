<?xml version="1.0" encoding="UTF-8"?>
<!--uu
********************************************************************************
 JMMC project

 "@(#) $Id: mfsvrSETMODEL.cdf,v 1.2 2006-06-27 09:05:19 lsauge Exp $"

 History 
 ~~~~~~~
 $Log: not supported by cvs2svn $
 Revision 1.1  2006/03/31 08:19:50  gzins
 *** empty log message ***

-->
<cmd
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xsi:noNamespaceSchemaLocation="cmdDefinitionFile.xsd">
    <mnemonic>SETMODEL</mnemonic>
    <desc>Define the model to be fitted.</desc>
    <params>
        <param>
            <name>type</name>
            <type>integer</type>
            <minValue><integer>0</integer></minValue>
            <maxValue><integer>3</integer></maxValue>
            <desc>model type</desc>
        </param>
    </params>        
</cmd>
