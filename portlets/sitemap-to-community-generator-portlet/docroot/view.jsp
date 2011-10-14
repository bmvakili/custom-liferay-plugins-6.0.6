<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ page isELIgnored ="false" %> 

<portlet:defineObjects />
<portlet:renderURL var="generateURL">
</portlet:renderURL>
<h1>Sitemap to Community Generator</h1>
<section>
Enter the sitemap text into text area below, then click create.<br/>
The format for the sitemap is emacs outline, which is described at following URI:
<a href="http://www.emacswiki.org/emacs/OutlineMode">Emacs Outline</a><br/>
For the impatient, it is as follows:<br/>
*Site Page 0 Level 0<br/>
**Site Page 0 Level 1<br/>
***Site Page 0 Level 2<br/>
*Site Page 1 Level 0<br/>
**Site Page 1 Level 1<br/>
***Site Page 1 Level 2<br/>
***Site Page 2 Level 2<br/>
**Site Page 2 Level 1<br/>
***Site Page 1 Level 2<br/>
***Site Page 2 Level 2<br/>
</section>
<aside>
<aui:form action="${generateURL}" method="post">
<textarea name="<portlet:namespace />sitemap" rows="20" cols="80" autofocus="autofocus">
*Level 1
**Level 2
***Level 3
</textarea>
<aui:button type="submit" value="Submit"></aui:button>
</aui:form>
</aside>
