<!--Example:Start-->
<!--$Id$-->
<!--
<% response.setHeader("Pragma","no-cache"); %>
<% response.setHeader("Cache-Control", "no-cache"); %>
<% response.setDateHeader("Last-Modified",System.currentTimeMillis());%>
<% response.setHeader("Expires","0");%>



<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="rdc" uri="http://jakarta.apache.org/taglibs/rdc-1.0" %>
-->
<vxml version="2.0" xml:lang="en-US" xmlns="http://www.w3.org/2001/vxml">


<jsp:useBean id="rdcStack" class="java.util.Stack" scope="request"/>
<jsp:useBean id="dialogMap"  class="java.util.LinkedHashMap" scope="session"/>


<rdc:push stack="${rdcStack}" element="${dialogMap}"/>

 
  <form>

    	<rdc:select1 id="test" submit="${submit}" config="config/equipment-type/equipment-cfg.xml" 
	             optionList="config/equipment-type/equipment-opt.xml" confirm="true"
	             minConfidence="40.0F" numNBest="6" echo="true" />

  </form>
</vxml>
<rdc:pop var="discard" stack="${rdcStack}"/>
<!--Example:End-->
