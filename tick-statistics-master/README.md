# tick-statistics
Solactive technical task

1)How to run the project (preferably with maven Spring Boot plugin or alike, or as anexecutable jar)
Clone codebase from Git Repository using below command
Git clone https://github.com/nareshthota247/tick-statistics.git.

Run the below command in command prompt from the root project folder 
mvnw clean package >log-file.log

Please notice the generated jar file in the location target folder

Run the below command from the same location i.e root project folder
java -jar target/solactive.jar
OR
You can also deploy in AWS server using Jenkinsfile & Dockerfile files

Now you can test using swagger or Postman

I provided detailed setup documents with screenshoot in the mail.

2)Which assumptions you made while developing
Deleted all the ticks before Sliding time interval(60 seconds) in every request for statistics to improve performance

3)And, whether you liked the challenge or not
Yes I liked the challege.

4)What would you improve if you had more time
We can write write a scheduler running backend and creating a state so that multiple threads(requests) comming can access them and generate results so the performace would be good.
To avaid deletion of ticts we can save them in DB assigning a flag to old ticks and optimise database using index to query fast for good performance.
I would have written more test cases to test in multithreading approch and cover more test senarios.



