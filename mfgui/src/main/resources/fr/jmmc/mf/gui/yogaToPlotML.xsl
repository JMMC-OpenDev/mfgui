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


Mars 2021 :  assume matrix mode provides tr series of next tds: 
        ruv, visamp, visamperr, visamp_model, visamp_wght, visphi, visphierr, visphi_model, visphi_wght
        ruv, vis2data, vis2data_model
        ruv, t3amp, t3amperr, t3amp_model, t3amp_wght, t3phi, t3phierr, t3phi_model, t3phi_wght                    
        (see Matrix templates between)
        
        TODO : matrix variables must be adapted to avoid // and match with future yorick output.
            look at //world vs /result/world/ (yorick output is )
            
            

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
    <!-- plotName parameter should be one of the following types (separated by space, comma, or whatever)
     - plotBaselines plotUVCoverage visamp visphi vis2 t3amp t3phi
     note: vis2 is sometimes called vis2data into the world structure
     -->
    <xsl:param name="plotName">plotBaselines</xsl:param>
    <xsl:param name="residuals"></xsl:param>

    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="contains($plotName,'vis2') or contains($plotName,'visamp') or contains($plotName,'visphi') or contains($plotName,'t3phi') or contains($plotName,'t3amp')">
                <xsl:choose>
                    <xsl:when test="$residuals">
                        <xsl:call-template name="plotRadialResiduals">
                            <xsl:with-param name="plotName" select="$plotName"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="plotRadial">
                            <xsl:with-param name="plotName" select="$plotName"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$plotName='plotBaselines'">
                <xsl:call-template name="plotBaselines"/>
            </xsl:when>
            <xsl:when test="$plotName='plotUVCoverage'">
                <xsl:call-template name="plotUVCoverage"/>
            </xsl:when>
            <xsl:otherwise>
                <plot>
                    <title>
                        <xsl:value-of select="$plotName"/> plot not yet supported
                    </title>
                </plot>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="plotRadial">
        <xsl:param name="plotName"/>
        <xsl:param name="yfactor">
            <xsl:choose>
                <xsl:when test="contains($plotName,'phi')">57.29578</xsl:when>
                <xsl:otherwise>1</xsl:otherwise>
            </xsl:choose>
        </xsl:param>

        <plot>
            <title>
                <xsl:value-of select="concat('Plot ',$plotName,' versus spatial frequency')"/>
            </title>
            <xLabel>spatial frequency (1/rad) <xsl:if test="contains($plotName,'3')">(max of the 3 frequencies)</xsl:if></xLabel>
            <yLabel>
                <xsl:value-of select="$plotName"/>
                <xsl:if test="contains($plotName,'phi')"> (deg)</xsl:if>
            </yLabel>

            <xsl:variable name="dataName">
                <xsl:choose>
                    <xsl:when test="$plotName='vis2'">vis2data</xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$plotName"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:variable name="errName" select="concat($plotName,'err')"/>
            <xsl:variable name="modelName" select="concat($dataName,'_model')"/>      
                  
            <xsl:for-each select="//world/_modeler/dataset/*/*[starts-with(name(),'DB')]">                                                
                <xsl:variable name="yElem" select="./*[name() = $dataName]"/>
                <xsl:if test="$yElem">
                    <xsl:variable name="errElem" select="./*[name() = $errName]"/>
                    <xsl:variable name="mElem" select="./*[name() = $modelName]"/>

                    <!-- plot data -->
                    <xsl:call-template name="ptPlotDataSet">
                        <xsl:with-param name="xElements" select="./ruv/table/tr/td"/>
                        <xsl:with-param name="yElements" select="$yElem/table/tr/td"/>
                        <xsl:with-param name="errorBarElements" select="$errElem/table/tr/td"/>                      
                        <!--<xsl:with-param name="flagElements" select="./flags/table/tr/td"/>-->
                        <xsl:with-param name="name" select="concat($plotName,' (data)')"/>
                        <xsl:with-param name="marks" select="'points'"/>
                        <!-- Convert internal values from radians to degrees -->
                        <xsl:with-param name="yfactor" select="$yfactor"/>
                    </xsl:call-template>
                    <!-- plot model -->
                    <xsl:call-template name="ptPlotDataSet">
                        <xsl:with-param name="xElements" select="./ruv/table/tr/td"/>
                        <xsl:with-param name="yElements" select="$mElem/table/tr/td"/>
                        <!--<xsl:with-param name="flagElements" select="./flags/table/tr/td"/>-->
                        <xsl:with-param name="name" select="concat($plotName,' (model)')"/>
                        <xsl:with-param name="marks" select="'dots'"/>
                        <!-- Convert internal values from radians to degrees -->
                        <xsl:with-param name="yfactor" select="$yfactor"/>
                    </xsl:call-template>
                </xsl:if>                 
            </xsl:for-each>
            
            <!-- handle squeezed_world if any -->
            <xsl:for-each select="//squeezed_world/*/*[starts-with(name(),'DB')]">                                                                                                                        
                <xsl:call-template name="ptPlotDB">
                    <xsl:with-param name="db" select="."/>                        
                    <xsl:with-param name="name" select="$plotName"/>                        
                    <xsl:with-param name="dataName" select="$dataName"/>
                    <xsl:with-param name="errName" select="$errName"/>
                    <xsl:with-param name="modelName" select="$modelName"/>
                    <xsl:with-param name="yfactor" select="$yfactor"/>          
                </xsl:call-template>                                                        
            </xsl:for-each>
            
        </plot>
    </xsl:template>

    <xsl:template name="plotRadialResiduals">
        <xsl:param name="plotName"/>

        <plot>
            <title>
                <xsl:value-of select="concat('Plot ',$plotName,' residuals versus spatial frequency')"/>
            </title>
            <xLabel>spatial frequency (1/rad) <xsl:if test="contains($plotName,'3')">(max of the 3 frequencies)</xsl:if></xLabel>
            <yLabel>
                <xsl:value-of select="$plotName"/> residuals = (d-m)/sigma</yLabel>

            <xsl:variable name="dataName">
                <xsl:choose>
                    <xsl:when test="$plotName='vis2'">vis2data</xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$plotName"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:variable name="modelName" select="concat($dataName,'_model')"/>
            <xsl:variable name="weightName" select="concat($plotName,'_wght')"/>

            <xsl:for-each select="//world/_modeler/dataset/*/*[starts-with(name(),'DB')]">
                <xsl:variable name="dataElem" select="./*[name() = $dataName]"/>
                <xsl:if test="$dataElem">
                    <xsl:variable name="mElem" select="./*[name() = $modelName]"/>
                    <xsl:variable name="wElem" select="./*[name() = $weightName]"/>
                    <!-- plot (m-d)/w -->
                    <xsl:call-template name="ptPlotResidualsDataSet">
                        <xsl:with-param name="xElements" select="./ruv/table/tr/td"/>
                        <xsl:with-param name="dataElements" select="$dataElem/table/tr/td"/>
                        <xsl:with-param name="modelElements" select="$mElem/table/tr/td"/>
                        <xsl:with-param name="weightElements" select="$wElem/table/tr/td"/>
                        <!--<xsl:with-param name="flagElements" select="./flags/table/tr/td"/>-->
                        <xsl:with-param name="name" select="$plotName"/>
                        <xsl:with-param name="marks" select="'dots'"/>
                    </xsl:call-template>
                </xsl:if>                                
            </xsl:for-each>
            
            <!-- handle squeezed_world if any -->
            <xsl:for-each select="//squeezed_world/*/*[starts-with(name(),'DB')]">                                                                                                                        
                <xsl:call-template name="ptPlotResidualsDB">
                    <xsl:with-param name="db" select="."/>                        
                    <xsl:with-param name="name" select="$plotName"/>                          
                </xsl:call-template>                                                        
            </xsl:for-each>
            
        </plot>
    </xsl:template>

    <xsl:template name="ptPlotDB">
        <xsl:param name="db"/> <!-- DBn -->
        <xsl:param name="name"/>        
        <xsl:param name="dataName"/>        
        <xsl:param name="errName"/>                
        <xsl:param name="modelName"/> 
                       
        <xsl:param name="yfactor" select="1"/>                
        
        <xsl:if test="$db/labels//td[.=$dataName]">

            <xsl:variable name="yfactorNum" select="number($yfactor)"/>

            <xsl:variable name="xpos">
                <xsl:for-each select="$db/labels//td"><xsl:if test=". = 'ruv'"><xsl:value-of select="position()"/></xsl:if></xsl:for-each>
            </xsl:variable>        
            <xsl:variable name="ypos">
                <xsl:for-each select="$db/labels//td"><xsl:if test=". = $dataName"><xsl:value-of select="position()"/></xsl:if></xsl:for-each>
            </xsl:variable>        
            <xsl:variable name="errpos">
                <xsl:for-each select="$db/labels//td"><xsl:if test=". = $errName"><xsl:value-of select="position()"/></xsl:if></xsl:for-each>
            </xsl:variable>
            <xsl:variable name="modelpos">
                <xsl:for-each select="$db/labels//td"><xsl:if test=". = $modelName"><xsl:value-of select="position()"/></xsl:if></xsl:for-each>
            </xsl:variable>                                             

            <!-- data -->
            <xsl:element name="dataset">
                <xsl:attribute name="name"><xsl:value-of select="concat($name,' (data)')"/></xsl:attribute>               
                <xsl:attribute name="connected">no</xsl:attribute>
                <xsl:attribute name="marks">points</xsl:attribute>            
           
                <xsl:for-each select="$db/data//tr">            
                    <xsl:variable name="y" select="td[position()=$ypos]"/>
                    <xsl:variable name="err" select="td[position()=$errpos]"/>                            
                    <xsl:element name="p">
                        <xsl:attribute name="x">
                            <xsl:value-of select="td[position()=$xpos]"/>
                        </xsl:attribute>
                        <xsl:attribute name="y">
                            <xsl:value-of select="$y * $yfactorNum"/>
                        </xsl:attribute>
                        <xsl:attribute name="lowErrorBar">
                            <xsl:value-of select="( $y - $err ) * $yfactorNum"/>
                        </xsl:attribute>
                        <xsl:attribute name="highErrorBar">
                            <xsl:value-of select="( $y + $err ) * $yfactorNum"/>
                        </xsl:attribute>
                    </xsl:element>                    
                </xsl:for-each>            
            </xsl:element>

            <!-- model -->
            <xsl:element name="dataset">
                <xsl:attribute name="name">
                    <xsl:value-of select="concat($name,' (model)')"/> 
                </xsl:attribute>
                <xsl:attribute name="connected">no</xsl:attribute>
                <xsl:attribute name="marks">dots</xsl:attribute>            
                <xsl:for-each select="$db/data//tr">                             
                    <xsl:element name="p">
                        <xsl:attribute name="x">
                            <xsl:value-of select="td[position()=$xpos]"/>
                        </xsl:attribute>
                        <xsl:attribute name="y">
                            <xsl:value-of select="td[position()=$modelpos] * $yfactorNum"/>
                        </xsl:attribute>
                    </xsl:element>                    
                </xsl:for-each>            
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template name="ptPlotResidualsDB">
        <xsl:param name="db"/> <!-- DBn -->
        <xsl:param name="name"/>                
        <xsl:variable name="residualsName" select="concat($name,'_residuals')"/>        
                
        <xsl:if test="$db/labels//td[.=$residualsName]">
                                
            <xsl:variable name="xpos">
                    <xsl:for-each select="$db/labels//td"><xsl:if test=". = 'ruv'"><xsl:value-of select="position()"/></xsl:if></xsl:for-each>
            </xsl:variable>        
            <xsl:variable name="ypos">
                <xsl:for-each select="$db/labels//td"><xsl:if test=". = $residualsName"><xsl:value-of select="position()"/></xsl:if></xsl:for-each>
            </xsl:variable> 

            <xsl:element name="dataset">
                <xsl:attribute name="name">
                    <xsl:value-of select="$name"/>
                </xsl:attribute>
                <xsl:attribute name="connected">no</xsl:attribute>
                <xsl:attribute name="marks">points</xsl:attribute>            
                <xsl:for-each select="$db/data//tr">                                       
                    <xsl:element name="p">
                        <xsl:attribute name="x">
                            <xsl:value-of select="td[position()=$xpos]"/>
                        </xsl:attribute>
                        <xsl:attribute name="y">
                            <xsl:value-of select="td[position()=$ypos]"/>
                        </xsl:attribute>
                    </xsl:element>                    
                </xsl:for-each>            
            </xsl:element>                        

        </xsl:if>
    </xsl:template>

    <xsl:template name="ptPlotDataSet">
        <xsl:param name="xElements"/>
        <xsl:param name="yElements"/>
        <xsl:param name="flagElements"/>
        <xsl:param name="errorBarElements" select='no'/>
        <xsl:param name="name" select="'missing name'"/>
        <xsl:param name="marks" select="'points'"/>
        <xsl:param name="yfactor" select="1"/>

        <xsl:variable name="yfactorNum" select="number($yfactor)"/>

        <xsl:element name="dataset">
            <xsl:attribute name="name">
                <xsl:value-of select="$name"/>
            </xsl:attribute>
            <xsl:attribute name="connected">no</xsl:attribute>
            <xsl:attribute name="marks">
                <xsl:value-of select="$marks"/>
            </xsl:attribute>

            <xsl:choose>
                <xsl:when test="$errorBarElements != 'no'">
                    <xsl:for-each select="$xElements">
                        <xsl:variable name="i" select="position()"/>

                        <!--<xsl:if test="$flagElements[$i] = '0' or not($flagElements[$i] = '1')">-->
                            <xsl:variable name="y" select="number($yElements[$i])"/>
                            <xsl:variable name="err" select="number($errorBarElements[$i])"/>
                            <xsl:element name="p">
                                <xsl:attribute name="x">
                                    <xsl:value-of select="."/>
                                </xsl:attribute>
                                <xsl:attribute name="y">
                                    <xsl:value-of select="$y * $yfactorNum"/>
                                </xsl:attribute>
                                <xsl:attribute name="lowErrorBar">
                                    <xsl:value-of select="( $y - $err ) * $yfactorNum"/>
                                </xsl:attribute>
                                <xsl:attribute name="highErrorBar">
                                    <xsl:value-of select="( $y + $err ) * $yfactorNum"/>
                                </xsl:attribute>
                            </xsl:element>
                        <!--</xsl:if>-->
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="$xElements">
                        <xsl:variable name="i" select="position()"/>

<!--                        <xsl:if test="$flagElements[$i] = '0' or not($flagElements[$i] = '1')">-->
                            <xsl:variable name="y" select="number($yElements[$i])"/>
                            <xsl:element name="p">
                                <xsl:attribute name="x">
                                    <xsl:value-of select="."/>
                                </xsl:attribute>
                                <xsl:attribute name="y">
                                    <xsl:value-of select="$y * $yfactorNum"/>
                                </xsl:attribute>
                            </xsl:element>
                        <!--</xsl:if>-->
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="ptPlotResidualsDataSet">
        <xsl:param name="xElements"/>
        <xsl:param name="dataElements"/>
        <xsl:param name="modelElements"/>
        <xsl:param name="weightElements"/>
        <!--<xsl:param name="flagElements"/>-->
        <xsl:param name="name" select="'missing name'"/>
        <xsl:param name="marks" select="'points'"/>

        <xsl:element name="dataset">
            <xsl:attribute name="name">
                <xsl:value-of select="$name"/>
            </xsl:attribute>
            <xsl:attribute name="connected">no</xsl:attribute>
            <xsl:attribute name="marks">
                <xsl:value-of select="$marks"/>
            </xsl:attribute>

            <xsl:for-each select="$xElements">
                <xsl:variable name="i" select="position()"/>

                <!--<xsl:if test="$flagElements[$i] = '0' or not($flagElements[$i] = '1')">-->
                    <xsl:variable name="d" select="number($dataElements[$i])"/>
                    <xsl:variable name="m" select="number($modelElements[$i])"/>
                    <xsl:variable name="w" select="number($weightElements[$i])"/>
                    <xsl:element name="p">
                        <xsl:attribute name="x">
                            <xsl:value-of select="."/>
                        </xsl:attribute>
                        <xsl:attribute name="y">
                            <xsl:value-of select="( $d - $m ) * $w"/>
                        </xsl:attribute>
                    </xsl:element>
                <!--</xsl:if>-->
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template name="plotBaselines">
        <plot>
            <title>Baselines plot</title>
            <xLabel>Ucoord (meters)</xLabel>
            <yLabel>Vcoord (meters)</yLabel>
            <xsl:for-each select="//world/_modeler/dataset/*/*[starts-with(name(),'CR')] | //squeezed_world//*[starts-with(name(),'CR')]">
                <!-- read ucoord and vcoord array and plot u,v and -u -v -->
                <dataset connected="no" marks="various">
                    <xsl:variable name="ucoord" select="./ucoord/table/tr/td"/>
                    <xsl:variable name="vcoord" select="./vcoord/table/tr/td"/>

                    <xsl:for-each select="$ucoord">
                        <xsl:variable name="i" select="position()"/>
                        <xsl:variable name="u" select="number(.)"/>
                        <xsl:variable name="v" select="number($vcoord[$i])"/>
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="$u"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="$v"/>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="-$u"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="-$v"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </dataset>
            </xsl:for-each>
            <xsl:for-each select="//squeezed_world//*[starts-with(name(),'CR')]">                
                <!-- read ucoord and vcoord array and plot u,v and -u -v -->
                <dataset connected="no" marks="various">
                    <xsl:for-each select="uvcoord//tr">
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="td[1]"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="td[2]"/>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="-number(td[1])"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="-number(td[2])"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </dataset>
            </xsl:for-each>
        </plot>
    </xsl:template>

    <xsl:template name="plotUVCoverage">
        <plot>
            <title>UV coverage plot</title>
            <xLabel>Ufreq (1/rad)</xLabel>
            <yLabel>Vfreq (1/rad)</yLabel>

            <xsl:for-each select="/result/world/_modeler/dataset/*/*[starts-with(name(),'CR')] | //squeezed_world//*[starts-with(name(),'CR')]">
                <!-- read ucoord and vcoord array and plot u,v and -u -v -->
                <dataset connected="no" marks="various">
                    <xsl:variable name="ufreq" select="./ufreq/table/tr/td"/>
                    <xsl:variable name="vfreq" select="./vfreq/table/tr/td"/>

                    <xsl:for-each select="$ufreq">
                        <xsl:variable name="i" select="position()"/>
                        <xsl:variable name="u" select="number(.)"/>
                        <!--  take care, vfreq and ufreq are not array(1,n) but one matrix (n,1) -->
                        <xsl:variable name="v" select="number($vfreq[$i])"/>
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="$u"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="$v"/>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="-$u"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="-$v"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </dataset>
            </xsl:for-each>
            <xsl:for-each select="//squeezed_world//*[starts-with(name(),'CR')]">                
                <!-- read ucoord and vcoord array and plot u,v and -u -v -->
                <dataset connected="no" marks="various">
                    <xsl:for-each select="uvfreq//tr">
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="td[1]"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="td[2]"/>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="-number(td[1])"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="-number(td[2])"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </dataset>
            </xsl:for-each>
        </plot>
    </xsl:template>
</xsl:stylesheet>
