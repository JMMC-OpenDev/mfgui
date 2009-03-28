<?xml version="1.0" standalone="yes" ?>

<!--
Document   : yogaToPlotML.xsl
Created on : 14 mars 2007, 11:09
Author     : mella
Description:        
Template code try to make same actions of following yoga functions
lp_plot_baselines, lp_plot_uvcoverage, lp_plot_radial, 
lp_plot_image, lp_plot_uvmap

Output must be follow plotml format:
http://ptolemy.berkeley.edu/java/ptplot5.6/ptolemy/plot/doc/plot.pdf


NOTES pour futur documenter les GROS PROBLEMES CONSTATE entre xsltproc et xalan
le param d'un template qui disparait a completer...
exslt:node-set($var)//eleName ne marche pas, il faut utiliser exslt:node-set($var)/eleName

Ensuite avec xalan et le proxy la reference au dtd peut poser probleme avec une trasfo xsl

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
            
    <!-- <xsl:output method="xml" standalone="yes" doctype-public= "-//UC Berkeley//DTD PlotML 1//EN"
                doctype-system="http://ptolemy.eecs.berkeley.edu/archive/plotml.dtd"/>
                -->
    <xsl:output method="xml" standalone="yes" />
    <xsl:param name="plotName">plotBaselines</xsl:param>
    
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
        <xsl:choose>
            <xsl:when test="$plotName='plotBaselines'">
                <xsl:call-template name="plotBaselines"/>
            </xsl:when>
            <xsl:when test="$plotName='plotUVCoverage'">
                <xsl:call-template name="plotUVCoverage"/>
            </xsl:when>
            <xsl:when test="$plotName='plotRadial'">
                <xsl:call-template name="plotRadial"/>
            </xsl:when>
            <xsl:when test="$plotName='plotRadialT3'">
                <xsl:call-template name="plotRadialT3"/>
            </xsl:when>
            <xsl:when test="$plotName='plotRadialVIS'">
                <xsl:call-template name="plotRadialVIS"/>
            </xsl:when>
            
            <xsl:when test="$plotName='plotRadialVOTABLE'">
                <xsl:call-template name="plotRadialVOTABLE"/>
            </xsl:when>
            <xsl:otherwise>
                <plot>
                    <title><xsl:value-of select="$plotName"/> plot not yet supported</title>
                </plot>        
            </xsl:otherwise>
        </xsl:choose>                    
    </xsl:template>
    
    <xsl:template name="plotBaselines">
        <plot>
            <title>Baselines plot</title>
            <xLabel>Ucoord (meters)</xLabel>
            <yLabel>Vcoord (meters)</yLabel>
            
            <!-- we should follow crlst list according yorick code -> 
            //_modeler/dataset//crlst            
            -->
            <xsl:for-each select="//_modeler/dataset//CR1">
                <!-- read ucoord and vcoord array and plot u,v and -u -v -->
                <dataset connected="no" marks="various">
                    <xsl:variable name="ucoord" select="ucoord"/>
                    <xsl:variable name="vcoord" select="vcoord"/>
                    <xsl:for-each select="ucoord//td">
                        <xsl:variable name="i" select="position()"/>
                        <xsl:variable name="u" select="."/>
                        <xsl:variable name="v" select="$vcoord//td[position()=$i]"/>                            
                        <xsl:element name="p">
                            <xsl:attribute name="x"><xsl:value-of select="$u"/></xsl:attribute>
                            <xsl:attribute name="y"><xsl:value-of select="$v"/></xsl:attribute>
                        </xsl:element>
                        <xsl:element name="p">                            
                            <xsl:attribute name="x"><xsl:value-of select="-$u"/></xsl:attribute>
                            <xsl:attribute name="y"><xsl:value-of select="-$v"/></xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </dataset>
            </xsl:for-each>                
        </plot>
    </xsl:template>
    
    <xsl:template name="plotUVCoverage">
        <plot>
            <title>UV coverage plot</title>
            <xLabel>Ucoord (1/rad)</xLabel>
            <yLabel>Vcoord (1/rad)</yLabel>         
            <!-- we should follow crlst list according yorick code -> 
            //_modeler/dataset//crlst            
            -->
            <xsl:for-each select="//_modeler/dataset//CR1">
                <!-- read ucoord and vcoord array and plot u,v and -u -v -->
                <dataset connected="no" marks="various">                
                    <xsl:variable name="pair"><xsl:copy-of select="vfreq//td"/></xsl:variable>
                    <xsl:for-each select="ufreq//td">
                        <xsl:variable name="i" select="position()"/>
                        <xsl:variable name="u" select="."/>
                        <!--  take care, vfreqand ufreq are not array(1,n) but one matrix (n,1) -->                                                  
                        <xsl:variable name="v" select="exslt:node-set($pair)/td[position()=$i]"/>
                        <xsl:element name="p">
                            <xsl:attribute name="x"><xsl:value-of select="$u"/></xsl:attribute>
                            <xsl:attribute name="y"><xsl:value-of select="$v"/></xsl:attribute>
                        </xsl:element>
                        <xsl:element name="p">                            
                            <xsl:attribute name="x"><xsl:value-of select="-$u"/></xsl:attribute>
                            <xsl:attribute name="y"><xsl:value-of select="-$v"/></xsl:attribute>
                        </xsl:element>                    
                    </xsl:for-each>
                </dataset>
            </xsl:for-each>                                        
        </plot>
    </xsl:template>

    <xsl:template name="plotRadial">    
        <plot>
            <title>Plot versus radial distance</title>
            <xLabel>spatial frequency (1/rad)</xLabel>
            <yLabel>(VIS2 or T3)</yLabel>                        
            <xsl:for-each select="//_modeler/dataset//*[starts-with(name(),'DB')]">
                <xsl:variable name="db"><xsl:copy-of select="./*"/></xsl:variable>
                <xsl:variable name="classname"><xsl:call-template name="getClassName"/></xsl:variable>
                <xsl:variable name="ruv"><xsl:copy-of select=".//ruv//td"/></xsl:variable>
                <xsl:variable name="errorTds"><xsl:copy-of select="exslt:node-set($db)/viserr//td|exslt:node-set($db)/vis2err//td|exslt:node-set($db)/t3amperr//td|exslt:node-set($db)/t3phierr//td"/></xsl:variable>
                <xsl:for-each select="visamp|visamp_model|vis2data|vis2data_model|t3amp|t3phi|t3amp_model|t3phi_model">
                    <xsl:variable name="source"><xsl:choose>
                            <xsl:when test="contains(name(),'_model')">model</xsl:when>
                            <xsl:otherwise>data</xsl:otherwise> 
                    </xsl:choose></xsl:variable>
                    <!-- to debug only because dtd is strict with white spaces <xsl:value-of select="'&#10;&#10;&#10;'"/>
                    -->
                    <xsl:element name="dataset">
                        <xsl:attribute name="name"> 
                            <xsl:value-of select="concat($source,' ',$classname)"/>
                        </xsl:attribute>                    
                        <xsl:attribute name="connected">no</xsl:attribute>
                        <xsl:attribute name="marks">
                            <xsl:choose>
                                <xsl:when test="$source='model'">points</xsl:when>
                                <xsl:otherwise>dots</xsl:otherwise> 
                            </xsl:choose>
                        </xsl:attribute>          
                        <xsl:for-each select=".//td">                                    
                            <xsl:variable name="i" select="position()"/>
                            <xsl:variable name="x" select="exslt:node-set($ruv)/td[position()=$i]"/>
                            <xsl:variable name="y" select="."/>
                            <xsl:element name="p">
                                <xsl:attribute name="x"><xsl:value-of select="$x"/></xsl:attribute>
                                <xsl:attribute name="y"><xsl:value-of select="$y"/></xsl:attribute>
                                <xsl:choose>
                                <xsl:when test="$source='data'">
                                <xsl:for-each select="exslt:node-set($errorTds)/td[position()=$i]">
                                    <xsl:attribute name="lowErrorBar"><xsl:value-of select="$y - . div 2"/></xsl:attribute>
                                    <xsl:attribute name="highErrorBar"><xsl:value-of select="$y + . div 2"/></xsl:attribute>
                                </xsl:for-each>    
                                </xsl:when>
                                </xsl:choose>                                
                            </xsl:element>                    
                        </xsl:for-each>                            
                    </xsl:element>                        
                </xsl:for-each>                
            </xsl:for-each>                                       
        </plot>
    </xsl:template>

    <xsl:template name="plotRadialT3">    
        <plot>
            <title>Plot T3 versus radial distance</title>
            <xLabel>spatial frequency (1/rad)</xLabel>
            <yLabel>T3</yLabel>                        
            <xsl:for-each select="//_modeler/dataset//*[starts-with(name(),'DB')]">                            
                <xsl:variable name="db"><xsl:copy-of select="./*"/></xsl:variable>                 
                <xsl:variable name="classname"><xsl:call-template name="getClassName"/></xsl:variable>
                <xsl:variable name="ruv"><xsl:copy-of select=".//ruv//td"/></xsl:variable>
                <xsl:for-each select="t3a
                mp|t3phi|t3amp_model|t3phi_model">
                    <xsl:variable name="errorTds"><xsl:copy-of select="./following-sibling::viserr//td|./following-sibling::vis2err//td|./following-sibling::t3amperr//td|./following-sibling::t3phierr//td"/></xsl:variable>
                    <xsl:variable name="source"><xsl:choose>
                            <xsl:when test="contains(name(),'_model')">model</xsl:when>
                            <xsl:otherwise>data</xsl:otherwise>
                    </xsl:choose></xsl:variable>
                    <!-- to debug only because dtd is strict with white spaces <xsl:value-of select="'&#10;&#10;&#10;'"/>
                    -->
                    <xsl:element name="dataset">
                        <xsl:attribute name="name">
                            <xsl:value-of select="name()"/>
                        </xsl:attribute>
                        <xsl:attribute name="connected">no</xsl:attribute>
                        <xsl:attribute name="marks">
                            <xsl:choose>
                                <xsl:when test="$source='model'">dots</xsl:when>
                                <xsl:otherwise>points</xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:for-each select=".//td">
                            <xsl:variable name="i" select="position()"/>
                            <xsl:variable name="x" select="exslt:node-set($ruv)/td[position()=$i]"/>
                            <xsl:variable name="y" select="."/>
                            <xsl:element name="p">
                                <xsl:attribute name="x"><xsl:value-of select="$x"/></xsl:attribute>
                                <xsl:attribute name="y"><xsl:value-of select="$y"/></xsl:attribute>
                                <xsl:choose>
                                <xsl:when test="$source='data'">
                                <xsl:for-each select="exslt:node-set($errorTds)/td[position()=$i]">
                                    <xsl:attribute name="lowErrorBar"><xsl:value-of select="$y - . div 2"/></xsl:attribute>
                                    <xsl:attribute name="highErrorBar"><xsl:value-of select="$y + . div 2"/></xsl:attribute>
                                    <xsl:comment>Error on returned data: <xsl:value-of select="."/></xsl:comment>
                                </xsl:for-each>
                                </xsl:when>
                                </xsl:choose>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:element>
                </xsl:for-each>
            </xsl:for-each>                                       
        </plot>
    </xsl:template>
    
    <xsl:template name="plotRadialVIS">    
        <plot>
            <title>Plot VIS or VIS2 versus radial distance</title>
            <xLabel>spatial frequency (1/rad)</xLabel>
            <yLabel>(VIS or VIS2)</yLabel>                        
            <xsl:for-each select="//_modeler/dataset//*[starts-with(name(),'DB')]">                            
                <xsl:variable name="db"><xsl:copy-of select="./*"/></xsl:variable>                 
                <xsl:variable name="classname"><xsl:call-template name="getClassName"/></xsl:variable>
                <xsl:variable name="ruv"><xsl:copy-of select=".//ruv//td"/></xsl:variable>
                <xsl:variable name="errorTds"><xsl:copy-of select="exslt:node-set($db)/viserr//td|exslt:node-set($db)/vis2err//td|exslt:node-set($db)/t3amperr//td|exslt:node-set($db)/t3phierr//td"/></xsl:variable>
                <xsl:for-each select="visamp|visamp_model|vis2data|vis2data_model">
                    <xsl:variable name="source"><xsl:choose>
                            <xsl:when test="contains(name(),'_model')">model</xsl:when>
                            <xsl:otherwise>data</xsl:otherwise> 
                    </xsl:choose></xsl:variable>
                    <xsl:element name="dataset">
                        <xsl:attribute name="name">
                            <xsl:value-of select="concat($source,' ',$classname)"/>
                        </xsl:attribute>
                        <xsl:attribute name="connected">no</xsl:attribute>
                        <xsl:attribute name="marks">
                            <xsl:choose>
                                <xsl:when test="$source='model'">points</xsl:when>
                                <xsl:otherwise>dots</xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:for-each select=".//td">
                            <xsl:variable name="i" select="position()"/>
                            <xsl:variable name="x" select="exslt:node-set($ruv)/td[position()=$i]"/>
                            <xsl:variable name="y" select="."/>
                            <xsl:element name="p">
                                <xsl:attribute name="x"><xsl:value-of select="$x"/></xsl:attribute>
                                <xsl:attribute name="y"><xsl:value-of select="$y"/></xsl:attribute>
                                <xsl:choose>
                                <xsl:when test="$source='data'">
                                <xsl:for-each select="exslt:node-set($errorTds)/td[position()=$i]">
                                    <xsl:attribute name="lowErrorBar"><xsl:value-of select="$y - . div 2"/></xsl:attribute>
                                    <xsl:attribute name="highErrorBar"><xsl:value-of select="$y + . div 2"/></xsl:attribute>
                                </xsl:for-each>
                                </xsl:when>
                                </xsl:choose>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:element>       
                </xsl:for-each>
            </xsl:for-each>                                       
        </plot>
    </xsl:template>
    
    <xsl:template name="old_plotRadial">    
        <plot>
            <title>Plot versus radial distance</title>
            <xLabel>spatial frequency (1/rad)</xLabel>
            <yLabel>Squared visibility (VIS2)</yLabel>            
            <xsl:for-each select="//_modeler/*[starts-with(name(),'DB')]">
                <xsl:element name="dataset">
                    <xsl:attribute name="name">Observed <xsl:call-template name="getClassName"/></xsl:attribute>
                    <xsl:attribute name="connected">no</xsl:attribute>
                    <xsl:attribute name="marks">various</xsl:attribute>
                    <xsl:variable name="abscissa">
                        <xsl:if test="not(.//t3amp)">
                            <xsl:variable name="ucoords"> <xsl:copy-of select=".//ucoord//td"/> </xsl:variable>
                            <xsl:variable name="vcoords"> <xsl:copy-of select=".//vcoord//td"/> </xsl:variable>
                            <xsl:for-each select="exslt:node-set($ucoords)/*">
                                <xsl:variable name="i" select="position()"/>
                                <xsl:variable name="ucoord" select="exslt:node-set($ucoords)/*[position()=$i]"/>
                                <xsl:variable name="vcoord" select="exslt:node-set($vcoords)/*[position()=$i]"/>
                                <v><xsl:value-of select="math:sqrt($ucoord*$ucoord+$vcoord*$vcoord)"/></v>
                            </xsl:for-each>
                        </xsl:if>
                        <xsl:if test=".//t3amp">
                            <xsl:variable name="ucoords1"> <xsl:copy-of select=".//u1coord//td"/> </xsl:variable>
                            <xsl:variable name="ucoords2"> <xsl:copy-of select=".//u2coord//td"/> </xsl:variable>
                            <xsl:variable name="ucoords3"> <xsl:copy-of select=".//u3coord//td"/> </xsl:variable>
                            <xsl:variable name="vcoords1"> <xsl:copy-of select=".//v1coord//td"/> </xsl:variable>
                            <xsl:variable name="vcoords2"> <xsl:copy-of select=".//v2coord//td"/> </xsl:variable>
                            <xsl:variable name="vcoords3"> <xsl:copy-of select=".//v3coord//td"/> </xsl:variable>
                            <!-- next line combine t3 and other class handling of uv coords -->
                            <xsl:for-each select="exslt:node-set($ucoords1)/*">
                                <xsl:variable name="i" select="position()"/>
                                <xsl:variable name="ucoord1" select="exslt:node-set($ucoords1)/*[position()=$i]"/>
                                <xsl:variable name="vcoord1" select="exslt:node-set($vcoords1)/*[position()=$i]"/>
                                <xsl:variable name="ucoord2" select="exslt:node-set($ucoords2)/*[position()=$i]"/>
                                <xsl:variable name="vcoord2" select="exslt:node-set($vcoords2)/*[position()=$i]"/>
                                <xsl:variable name="ucoord3" select="exslt:node-set($ucoords3)/*[position()=$i]"/>
                                <xsl:variable name="vcoord3" select="exslt:node-set($vcoords3)/*[position()=$i]"/>
                                <xsl:variable name="dists">
                                    <e>
                                        <xsl:value-of select="math:sqrt($ucoord1*$ucoord1+$vcoord1*$vcoord1)"/>
                                    </e>
                                    <e>
                                        <xsl:value-of select="math:sqrt($ucoord2*$ucoord2+$vcoord2*$vcoord2)"/>
                                    </e>
                                    <e>
                                        <xsl:value-of select="math:sqrt($ucoord3*$ucoord3+$vcoord3*$vcoord3)"/>
                                    </e>
                                </xsl:variable>
                                <v><xsl:value-of select="math:max(exslt:node-set($dists)/e)"/></v>
                                <xsl:comment><xsl:value-of select="''"/>t3 abs</xsl:comment>
                            </xsl:for-each>
                        </xsl:if>
                    </xsl:variable>
                    
                    <xsl:comment><xsl:value-of select="count(.//vis2data//td | .//t3amp//td)"/> data points</xsl:comment>
                    <xsl:for-each select=".//vis2data//td | .//t3amp//td">
                        <xsl:variable name="i" select="position()"/>
                        <xsl:variable name="x" select="exslt:node-set($abscissa)/*[position()=$i]"/>
                        <xsl:variable name="y" select="."/>               
                        <xsl:element name="p">
                            <xsl:attribute name="x"><xsl:value-of select="$x"/></xsl:attribute>
                            <xsl:attribute name="y"><xsl:value-of select="$y"/></xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
                
            </xsl:for-each>
            
            
            <!-- Plot model related --> 
            <!-- we should follow crlst list according yorick code -> 
            //_modeler/dataset//crlst            
            -->
            <xsl:for-each select="//_modeler/dataset//*[starts-with(name(),'DB')]">
                <xsl:element name="dataset">
                    <xsl:attribute name="name"><xsl:call-template name="getClassName"/></xsl:attribute>
                    <xsl:attribute name="connected">no</xsl:attribute>
                    <xsl:attribute name="marks">various</xsl:attribute>
                    <xsl:variable name="abscissa">
                        <xsl:if test="not(.//t3amp)">
                            <xsl:variable name="ucoords"> <xsl:copy-of select=".//ucoord//td"/> </xsl:variable>
                            <xsl:variable name="vcoords"> <xsl:copy-of select=".//vcoord//td"/> </xsl:variable>
                            <xsl:for-each select="exslt:node-set($ucoords)/*">
                                <xsl:variable name="i" select="position()"/>
                                <xsl:variable name="ucoord" select="exslt:node-set($ucoords)/*[position()=$i]"/>
                                <xsl:variable name="vcoord" select="exslt:node-set($vcoords)/*[position()=$i]"/>
                                <v><xsl:value-of select="math:sqrt($ucoord*$ucoord+$vcoord*$vcoord)"/></v>
                            </xsl:for-each>
                        </xsl:if>
                        <xsl:if test=".//t3amp">
                            <xsl:variable name="ucoords1"> <xsl:copy-of select=".//u1coord//td"/> </xsl:variable>
                            <xsl:variable name="ucoords2"> <xsl:copy-of select=".//u2coord//td"/> </xsl:variable>
                            <xsl:variable name="ucoords3"> <xsl:copy-of select=".//u3coord//td"/> </xsl:variable>
                            <xsl:variable name="vcoords1"> <xsl:copy-of select=".//v1coord//td"/> </xsl:variable>
                            <xsl:variable name="vcoords2"> <xsl:copy-of select=".//v2coord//td"/> </xsl:variable>
                            <xsl:variable name="vcoords3"> <xsl:copy-of select=".//v3coord//td"/> </xsl:variable>
                            <!-- next line combine t3 and other class handling of uv coords -->
                            <xsl:for-each select="exslt:node-set($ucoords1)/*">
                                <xsl:variable name="i" select="position()"/>
                                <xsl:variable name="ucoord1" select="exslt:node-set($ucoords1)/*[position()=$i]"/>
                                <xsl:variable name="vcoord1" select="exslt:node-set($vcoords1)/*[position()=$i]"/>
                                <xsl:variable name="ucoord2" select="exslt:node-set($ucoords2)/*[position()=$i]"/>
                                <xsl:variable name="vcoord2" select="exslt:node-set($vcoords2)/*[position()=$i]"/>
                                <xsl:variable name="ucoord3" select="exslt:node-set($ucoords3)/*[position()=$i]"/>
                                <xsl:variable name="vcoord3" select="exslt:node-set($vcoords3)/*[position()=$i]"/>
                                <xsl:variable name="dists">
                                    <e>
                                        <xsl:value-of select="math:sqrt($ucoord1*$ucoord1+$vcoord1*$vcoord1)"/>
                                    </e>
                                    <e>
                                        <xsl:value-of select="math:sqrt($ucoord2*$ucoord2+$vcoord2*$vcoord2)"/>
                                    </e>
                                    <e>
                                        <xsl:value-of select="math:sqrt($ucoord3*$ucoord3+$vcoord3*$vcoord3)"/>
                                    </e>
                                </xsl:variable>
                                <v><xsl:value-of select="math:max(exslt:node-set($dists)/e)"/></v>
                                <xsl:comment><xsl:value-of select="''"/>t3 abs</xsl:comment>
                            </xsl:for-each>
                        </xsl:if>
                    </xsl:variable>
                    
                    <xsl:comment><xsl:value-of select="count(.//vis2data//td | .//t3amp//td)"/> data points</xsl:comment>
                    <xsl:for-each select=".//vis2data//td | .//t3amp//td">
                        <xsl:variable name="i" select="position()"/>
                        <xsl:variable name="x" select="exslt:node-set($abscissa)/*[position()=$i]"/>
                        <xsl:variable name="y" select="."/>               
                        <xsl:element name="p">
                            <xsl:attribute name="x"><xsl:value-of select="$x"/></xsl:attribute>
                            <xsl:attribute name="y"><xsl:value-of select="$y"/></xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </xsl:for-each>
        </plot>
    </xsl:template>
    
    
    <xsl:template name="plotRadialVOTABLE">    
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
                                        <FIELD datatype="double" name="{name()}" unit="m"/>
                                    </xsl:when>
                                    <xsl:when test="name()='flags'">
                                        <FIELD datatype="boolean" name="FLAG"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <FIELD datatype="double" name="{name()}"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:for-each>
                            <DATA>
                                <TABLEDATA>
                                    <xsl:for-each select="../flags//tr">
                                        <xsl:variable name="i" select="position()"/>
                                        <TR>
                                            <xsl:for-each select="exslt:node-set($dataset)/*">                                             
                                                <xsl:for-each select=".//tr[position()=$i]/td">
                                                    <TD><xsl:value-of select="."/></TD>
                                                </xsl:for-each>
                                            </xsl:for-each>
                                            <xsl:for-each select="exslt:node-set($db)/ucoord//td[position()=$i] |
                                                          exslt:node-set($db)/vcoord//td[position()=$i] |
                                                          exslt:node-set($db)/u1coord//td[position()=$i] |
                                                          exslt:node-set($db)/v1coord//td[position()=$i] |
                                                          exslt:node-set($db)/u2coord//td[position()=$i] |
                                                          exslt:node-set($db)/v2coord//td[position()=$i] |
                                            exslt:node-set($db)/flags//tr[position()=$i]/td">
                                                <TD><xsl:value-of select="."/></TD>
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
    
    <xsl:template name="plotImage">    
        <plot>
            <title>Plot an image of the model</title>
            <xLabel>spatial frequency (1/rad)</xLabel>
            <yLabel>Squared visibility (VIS2)</yLabel>            
            <!-- we should follow crlst list according yorick code -> 
            //_modeler/dataset//crlst            
            -->
            <xsl:for-each select="//_modeler/dataset//*[starts-with(name(),'DB')]">
                <xsl:element name="dataset">
                    <xsl:attribute name="name"><xsl:call-template name="getClassName"/></xsl:attribute>
                </xsl:element>
            </xsl:for-each>
        </plot>
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
