# TIW-RIA-Exam
The project techs required are: JavaScript+HTML+CSS for front-end and Java Servlet+MySQL backend.  
The website is a public image gallery organized in albums. When pressing on an album the first 5 picture miniatures appear, and when hovering on one of them a modal displays the corresponding picture in full-size with its properties. Also sign-up and sign-in have to be supported, and on each picture a user can publish a comment. Other specifications are:
* On signup checks on validity of email and repeated passwords have to be done also on client side
* After the login, the whole application must be on a single page
* Each user interaction must be performed without refreshing the whole page but with asynchronous calls to the server
* Each user has personalized album page ordering. By default it is by date of creation, and then a user can move each album and save the new order. When the user signs in again they should see their last saved order of albums
* The "next 5 pictures" and "previous 5 pictures" must function without any server request
* Hovering on miniatures must display a modal with original size picture, all the other informations, and comments with a submit comment box
* The application checks also client side that the client doesn't send blank comments
* Errors server-side must appear in the client though an alert box

For more information on database design, application design, and events check out `exam-tiw-presentation.pdf`


# Screenshots of the application
## Login and Signup
![alt text](https://github.com/StefanDjokovic/TIW-2020-RIA/blob/master/Screenshots/s0.PNG)
## After login
![alt text](https://github.com/StefanDjokovic/TIW-2020-RIA/blob/master/Screenshots/s1.PNG)
## After opening the album "memes"
![alt text](https://github.com/StefanDjokovic/TIW-2020-RIA/blob/master/Screenshots/s2.PNG)
## After hovering on the first picture
![alt text](https://github.com/StefanDjokovic/TIW-2020-RIA/blob/master/Screenshots/s3.PNG)
## After selecting the "next" option and re-ordeing the albums
![alt text](https://github.com/StefanDjokovic/TIW-2020-RIA/blob/master/Screenshots/s4.PNG)
