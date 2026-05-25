# As a trainer I want to provide a set of one question to quizzers

, so that I can verify their knowledge.

# description

The quizzers get an url that offers the first question in the session. For sake of simplicity we will only deal with sessions including exactly one question.

# solution
- A session has to be created in the database (via insert ... no frontend solution in place yet).
- The session gets assigned randomly one question from the database.
- The session has an unguessable Id that is put into a link and shared with the quizzers
- After the single question is answered the session is closed for the user on the server. Then the link is not working anymore.

