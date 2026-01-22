<%@ page import="main.TestClass.TestClass" %>
<%@ page import="main.Main" %>
<%@ page import="java.util.Map" %>


<html>
<body>
<%
    String[] words = new String[10];
    words[0] = "hello";
    words[1] = "test";
    TestClass tc = new TestClass();
    Main.main(words);
    Map<String,Double> result = tc.se_search(words);
%>
<h2>Crawled!</h2>
</body>
</html>
