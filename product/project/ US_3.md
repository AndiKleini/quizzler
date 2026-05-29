# As a trainer I want to provide a sequence of questions to quizzers

, so that I can verify their knowledge.

# description

The quizzers get an url that offers the underlying session that contains a sequence of questions.

# solution
- The session gets assigned a set of questions from the database (quiz specification)
- The session has an unguessable Id that is put into a link and shared with the quizzers.
- The quizzers can now start an attempt.
- After stating the attempt the first single question is displayed, can be answered and the result can be displayed.
- By clicking on the next button the next question will be displayed. 
- If the last question was answered instead of the next button a finalize button will be displayed, that routes to the session start.

