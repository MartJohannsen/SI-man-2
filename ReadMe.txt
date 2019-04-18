How to run this project:

1. At first, start up activemq.
2. Then, in this project, you will have to run the requester class.
3. One will notice at this point, that a queue named 'request' has been made.
4. In this project, run the receiver class.
5. Notice that in activemq that a queue named 'reply' has been, which contains the output of the request.

Do note, if the requester makes an invalid request, the receiver will push an error message into the 'invalid' queue.

