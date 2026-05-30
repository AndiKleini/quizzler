# Bug Backlog

Contains the open bugs of the application.

## BUG_1 
In case of long loading the quiz-session displays not found as long as the API doesn't return. This can be reproduced by turning off the database of the API which results in a timeout. -> Fixed

## BUG_2
The single pick options numbering is wrong. At the second questions the numbers are steadily increasing and not stating with 1 for the first option.
