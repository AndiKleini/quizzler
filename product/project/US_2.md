# As a trainer I want to provide a set of one question to quizzers

, so that I can verify their knowledge.

# description

The quizzers get an url that offers the first question in the session. For sake of simplicity we will only deal with sessions including exactly one question.

# solution
- A session has to be created in the database (via insert ... no frontend solution in place yet).
- The session gets assigned one question from the database (quiz specification)
- The session has an unguessable Id that is put into a link and shared with the quizzers.
- The quizzers can now start an attempt.
- After stating the attempt the single question is displayed, can be answered and the result can be displayed.
- By clicking on the next button another attempt can be started.

