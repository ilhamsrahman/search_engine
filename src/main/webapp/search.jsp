<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>

<%@ page import="main.Main" %>
<%@ page import="main.WebPage.WebPage" %>
<%@ page import="main.Crawler.Crawler" %>
<%@ page import="main.Searching.Searching" %>
<%@ page import="main.TestClass.TestClass" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Vector" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <title>Search Engine</title>
    <link rel="stylesheet" type="text/css" href="index.css" />
    <link
      rel="stylesheet"
      href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"
      integrity="sha384-pzjw8WWO2ZK4Kq2rW5+7r8n1FyB4c4OwWe4qCf4ZcbC4f30pzz5+1Rs0a8b3Jw5b"
      crossorigin="anonymous"
    />


  </head>
  <body>
    <header>
      <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <a class="navbar-brand" href="#">
          <img src="logo.png" alt="Logo" class="img-fluid" style="max-width: 200px;" />
        </a>
        <button
          class="navbar-toggler"
          type="button"
          data-toggle="collapse"
          data-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav">
            <li class="nav-item active">
              <a class="nav-link" href="/tomcatConfiguration/search.jsp">Home <span class="sr-only">(current)</span></a>
            </li>
          </ul>
        </div>
      </nav>
    </header>
    <form method="post" action="search.jsp">
        <label for="txtname">Please enter a list of words:</label>
        <input type="text" id="txtname" name="txtname" required value="<%= (request.getParameter("txtname") != null) ? request.getParameter("txtname") : "" %>">
        <input type="submit" value="Submit">
    </form>

    <%
    if(request.getParameter("txtname") != "" && request.getParameter("txtname") != null)
    {
        //Print the result of the user searching
        String txtname = request.getParameter("txtname");
        String[] words = txtname.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        //testclass
        out.println("<div class='container'><div class='row' style='margin-top: 20px'>Searching for: &nbsp");
        for(int i = 0; i < words.length; i++){
            out.println("<a href='search.jsp?txtname=" + words[i] + "'>" + words[i] + "</a>&nbsp");
        }
        out.println("</div>");
        TestClass tc = new TestClass();
        Map<String,Double> result = tc.se_search(words);



        Map<String, WebPage> myObject=new HashMap<>();
         try {
                    //Retriving the webpage url
                    FileInputStream fileIn = new FileInputStream("crawledPage.ser");
                    ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                    myObject = (HashMap<String, WebPage>) objectIn.readObject();
                    objectIn.close();
                    fileIn.close(); }
         catch (Exception e) {
                e.printStackTrace();
        }
        Vector<String> s = Searching.retainSearchURL(words);
        Map<String, Double> returned = new HashMap<>();
        returned = Searching.rank(s, words, myObject);

        int i = 0;
        for (Map.Entry<String, Double> content : returned.entrySet()) {
            if (i >= 50)break;
            i++;
            WebPage wp=myObject.get(content.getKey());

            //Title
            out.println("<div class='content'><div class='row'><div class='col-12'><h2><a>" + i + "</a><a class = '' href='"+wp.getUrl() +"' target='_blank' style='color: rgb(37, 60, 136);'>");
            out.println(wp.getTitle());
            out.println("</a></h2></div></div>");

            //Initial information list
            out.println("<div class='row'><p class = 'small-text'>");

            //score
            out.println("<strong>Score:</strong>" + content.getValue() + "<br>");

            //URL
            out.println("<strong>URL:</strong><a href='" + wp.getUrl() + "'>" + wp.getUrl() +"</a><br>");

            //Last modified
            out.println("<strong>Last Updated:</strong>");
            if(wp.getDate() >0){
                out.println(new Date(wp.getDate()));
            }
            else{
                out.println("No last updated date");
            }
            out.println("<br>");

            //Size
            out.println("<strong>Size:</strong>" + wp.getSize() + "Byte <br>");

            //Most Frequent Word
            out.println("<strong>Most Frequent Word:</strong>");
            int numDisplay= 0;

            String similarpage = "";

            for(Map.Entry<String,Integer> p:wp.getWordFreq().entrySet()){
               if(numDisplay>=5)
                       break;
               numDisplay++;
               out.print(p.getKey()+" : "+p.getValue()+" ; ");
               similarpage = similarpage + p.getKey() + " ";
           }
           out.println("<a href='search.jsp?txtname=" + similarpage + "'>Get similar page</a>");

           out.println("<br>");

           //Parent Link
           out.println("<strong>Parent Link</strong>");
           numDisplay= Math.min(wp.getParent().size(), 5);
           for(int j=0;j<numDisplay;j++){
                   out.println("<br><a href='" +wp.getParent().toArray()[j] + "'>" + wp.getParent().toArray()[j] + "</a>");
           }
           out.println("<br>");


           //Child link
           out.println("<strong>Child Link</strong>");
          numDisplay= Math.min(wp.getChild().size(), 5);
          for(int j=0;j<numDisplay;j++){
                  out.println("<br><a href='" + wp.getChild().get(j) + "'>" + wp.getChild().get(j) + "</a>");
          }
          out.println("<br>");
            out.println("</div>");
    }



        out.println("</div>");
    }
    %>
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3"></script>
  </body>
</html>

