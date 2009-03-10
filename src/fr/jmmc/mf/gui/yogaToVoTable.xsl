<?xml version="1.0" standalone="yes" ?>
<!--
Document   : yogaToVoTable.xsl
Author     : mella
Description:        
try to export the maximum of informations into VOTable format
Output must be follow plotml format:
http://t.bd
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt="http://exslt.org/common"
                xmlns:math="http://exslt.org/math"
                xmlns:date="http://exslt.org/dates-and-times"
                xmlns:func="http://exslt.org/functions"
                xmlns:set="http://exslt.org/sets"
                xmlns:str="http://exslt.org/strings"
                xmlns:dyn="http://exslt.org/dynamic"
                xmlns:saxon="http://icl.com/saxon"
                xmlns:xt="http://www.jclark.com/xt"
                xmlns:libxslt="http://xmlsoft.org/XSLT/namespace"
                xmlns:test="http://xmlsoft.org/XSLT/"
                extension-element-prefixes="exslt math date func set str dyn saxon xt libxslt test"
                exclude-result-prefixes="math str" >
    
    <xsl:output method="xml" standalone="yes" indent="yes"/>


    <!-- copy of definition collected from yoga/OIlib/yorick/oidata.i -->
    <xsl:variable name="OI_CLASS_NONE" select="'0'"/>
    <xsl:variable name="OI_CLASS_TARGET" select="'1'"/>
    <xsl:variable name="OI_CLASS_INSTRUMENT" select="'2'"/>
    <xsl:variable name="OI_CLASS_ARRAY" select="'3'"/>
    <xsl:variable name="OI_CLASS_VIS" select="'4'"/>
    <xsl:variable name="OI_CLASS_VIS2" select="'5'"/>
    <xsl:variable name="OI_CLASS_T3" select="'6'"/>
    <xsl:variable name="OI_CLASS_SED" select="'7'"/>



    <xsl:template match="/">
        <VOTABLE version="1.1"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://www.ivoa.net/xml/VOTable/v1.1 http://www.ivoa.net/xml/VOTable/v1.1"
                 xmlns="http://www.ivoa.net/xml/VOTable/v1.1">
            <INFO>This votable has been generated from a xml LITpro result file.</INFO>
            <RESOURCE>
                <xsl:for-each select="//_modeler/dataset//*[starts-with(name(),'DB')]">
                    <xsl:variable name="db"><xsl:copy-of select="./*"/></xsl:variable>
                    <xsl:variable name="arrname"><xsl:value-of select="arrname"/></xsl:variable>                    
                    <xsl:variable name="insname"><xsl:value-of select="insname"/></xsl:variable>
                    <xsl:variable name="classname"><xsl:call-template name="getClassName"/></xsl:variable>
                    <xsl:for-each select="visdata|vis2data|t3amp">
                        <xsl:variable name="setname"><xsl:value-of select="name()"/></xsl:variable>                     
                        <xsl:variable name="dataset">                                             
                            <xsl:choose>
                                <xsl:when test="name()='visdata'">
                                    <xsl:copy-of select="."/>                                                                    
                                    <xsl:copy-of select="../viserr"/>                                
                                    <xsl:copy-of select="../visdata_model"/>                                
                                </xsl:when>
                                <xsl:when test="name()='vis2data'">
                                    <xsl:copy-of select="."/>                                
                                    <xsl:copy-of select="../vis2err"/>                                
                                    <xsl:copy-of select="../vis2data_model"/>                                
                                </xsl:when>
                                <xsl:when test="name()='t3amp'">
                                    <xsl:copy-of select="."/>                                
                                    <xsl:copy-of select="../t3amperr"/>                                                                    
                                    <xsl:copy-of select="../t3phi"/>
                                    <xsl:copy-of select="../t3phierr"/>
                                    <xsl:copy-of select="../t3amp_model"/>                                
                                    <xsl:copy-of select="../t3phi_model"/>                                
                                </xsl:when>                                
                            </xsl:choose>                        
                        </xsl:variable>                    
                        <TABLE>
                            <DESCRIPTION>This table contains <xsl:choose>
                                    <xsl:when test="contains(name(),'_model')">modelized data</xsl:when>
                                    <xsl:otherwise>observed data</xsl:otherwise> 
                            </xsl:choose> of <xsl:value-of select="$classname"/>.</DESCRIPTION>
                            <PARAM arraysize="*" datatype="char" name="EXTNAME" value="OI_VIS2">
                                <DESCRIPTION>Name of this binary table extension</DESCRIPTION>
                            </PARAM>
                            <PARAM datatype="int" name="EXTVER" value="1">
                                <DESCRIPTION>Extension version</DESCRIPTION>
                            </PARAM>
                            <PARAM datatype="int" name="OI_REVN" value="1">
                                <DESCRIPTION>Revision number of the table definition</DESCRIPTION>
                            </PARAM>
                            <PARAM arraysize="*" datatype="char" name="DATE-OBS" value="2003-12-27">
                                <DESCRIPTION>UTC start date of observations</DESCRIPTION>
                            </PARAM>
                            <PARAM arraysize="*" datatype="char" name="ARRNAME" value="{$arrname}">
                                <DESCRIPTION>(optional) Identifies corresponding OI_ARRAY</DESCRIPTION>
                            </PARAM>
                            <PARAM arraysize="*" datatype="char" name="INSNAME" value="{$insname}">
                                <DESCRIPTION>Identifies corresponding OI_WAVELENGTH table</DESCRIPTION>
                            </PARAM>
                            <xsl:for-each select="exslt:node-set($dataset)/*|../ucoord|../vcoord|../u1coord|../v1coord |../u2coord |../v2coord |../flags">
                                <xsl:choose>
                                    <xsl:when test="contains(name(),'coord')">
                                        <FIELD datatype="double" name="{name()}" unit="m" arraysize="{count(.//tr/td)}" />
                                    </xsl:when>
                                    <xsl:when test="name()='flags'">
                                        <FIELD datatype="boolean" name="FLAG" arraysize="{count(.//tr/td)}"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <FIELD datatype="double" name="{name()}" arraysize="{count(.//tr/td)}"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:for-each>
                            <DATA>
                                <TABLEDATA>
                                    <xsl:comment><xsl:copy-of select="../flags"/></xsl:comment>
                                    <xsl:for-each select="../flags//tr">
                                        <xsl:variable name="i" select="position()"/>
                                        <TR>
                                            <xsl:for-each select="td">
                                                <xsl:variable name="j" select="position()"/>                                                
                                                        <TD>
                                                            <xsl:value-of select="exslt:node-set($dataset)//tr[position()=$i]/td[position()=$j]"/>
                                                        </TD>                                                                         
                                                <xsl:for-each select="exslt:node-set($db)/ucoord//tr[position()=$i]/td[position()=$j] |
                                                          exslt:node-set($db)/vcoord//tr[position()=$i]/td[position()=$j] |
                                                          exslt:node-set($db)/u1coord//tr[position()=$i]/td[position()=$j] |
                                                          exslt:node-set($db)/v1coord//tr[position()=$i]/td[position()=$j] |
                                                          exslt:node-set($db)/u2coord//tr[position()=$i]/td[position()=$j] |
                                                          exslt:node-set($db)/v2coord//tr[position()=$i]/td[position()=$j]">
                                                    <TD>
                                                        <xsl:value-of select="."/>
                                                    </TD>
                                                </xsl:for-each>
                                            </xsl:for-each>
                                        </TR>
                                    </xsl:for-each>
                                </TABLEDATA>
                            </DATA>
                        </TABLE>
                    </xsl:for-each>
                </xsl:for-each>
            </RESOURCE>
        </VOTABLE>
    </xsl:template>
    
    <!-- Returns class name associated to .//class element -->
    <xsl:template name="getClassName">
        <xsl:param name="class" select=".//class"/>
        <xsl:choose>
            <xsl:when test="$class=$OI_CLASS_NONE">OI_CLASS</xsl:when>
            <xsl:when test="$class=$OI_CLASS_TARGET">OI_CLASS_TARGET</xsl:when>
            <xsl:when test="$class=$OI_CLASS_INSTRUMENT">OI_CLASS_INSTRUMENT</xsl:when>
            <xsl:when test="$class=$OI_CLASS_ARRAY">OI_CLASS_ARRAY</xsl:when>
            <xsl:when test="$class=$OI_CLASS_VIS">OI_CLASS_VIS</xsl:when>
            <xsl:when test="$class=$OI_CLASS_VIS2">OI_CLASS_VIS2</xsl:when>
            <xsl:when test="$class=$OI_CLASS_T3">OI_CLASS_T3</xsl:when>
            <xsl:when test="$class=$OI_CLASS_SED">OI_CLASS_SED</xsl:when>           
            <xsl:otherwise>Unsupported class <xsl:value-of select="$class"/></xsl:otherwise>
        </xsl:choose>                            
    </xsl:template>
    
</xsl:stylesheet>
