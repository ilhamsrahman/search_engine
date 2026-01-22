COMP4321 Group 31

Project Run order:
1. Please install intelliJ IDEA Community on your computer
2. Open /tomcatConfiguration directory
3. Click 3 dots "More action" on the upper right page, and click edit configuration.
4. Add smart-tomcat
5. Set the server to Apache Tomcat/9.0.89 Server
6. Set Catalina Base to ./tomcatConfiguration/.base
7. Set deployment directory to ./tomcatConfiguration/src/main/webapp
8. Click apply
9. Click run
10. Open http://localhost:8080/tomcatConfiguration/
11. Wait until the web finish loading, it takes time since it is crawling.
12. Open http://localhost:8080/tomcatConfiguration/search.jsp
13. You can start to type the query input and click search button.

If you just want to know how the crawling process is, you can run src/main/java/main/Main.java
You can also see src/main/java/TestClass/TestClass.java to see our big picture of the search engine.
